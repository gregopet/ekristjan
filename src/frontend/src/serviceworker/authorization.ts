import {storeTokens} from "@/serviceworker/credentialStore";
import {messageClients} from "@/serviceworker/messaging";


/** Event fired when good credentials have been entered into the system */
export const EVENT_LOGIN_FAILED = "LOGIN_BAD"

/** Event fired when a login has gone bad (perhaps user has logged out?) */
export const EVENT_LOGIN_SUCCESS = "LOGIN_GOOD"

/** Path prefixes for API paths that need authentication  */
const authorizedPathPrefixes = ['/departures']

/** Exceptions for API paths that need authentication */
const noAuthRequired = ['/departures/push/key']

/**
 * Current access & refresh tokens. All the users' tabs currently share the same session. They are also stored in
 * indexedDB in order to survive service worker upgrades.
 */
let mostRecentTokens: undefined | dto.LoginDTO

/** Do we currently have valid tokens? */
export function loggedIn(): Boolean {
    return mostRecentTokens !== undefined;
}

/** Updates the authorization tokens, after a login for example */
export function updateTokens(tokens: dto.LoginDTO | undefined) {
    mostRecentTokens = tokens;
    storeTokens(tokens);
}

export async function handleFetch(ev: FetchEvent) {
    const url = new URL(ev.request.url)
    // TODO: add hostname verification for extra security

    // In case of successful login, store tokens in service worker
    if (ev.request.url.endsWith("/login")) {
        const authReq = new Request(ev.request)
        ev.respondWith(fetch(authReq).then( resp => {
            if (resp.ok) {
                return resp.json().then((login: dto.LoginDTO) => {
                    updateTokens(login)
                    return messageClients(EVENT_LOGIN_SUCCESS).then ( () => {
                        return new Response(null, {})
                    })
                })
            } else {
                return resp;
            }
        }))
    } else if (
        authorizedPathPrefixes.filter(prefix => url.pathname.startsWith(prefix)).length &&
        !noAuthRequired.filter(path => url.pathname === path).length
    ) {
        if (loggedIn()) {
            ev.respondWith(authorizedFetch(ev.request));
        }
    }
}

/**
 * Perform a fetch with access & refresh token. Try to refresh access token if it had expired.
 * @param req The original request that needs to be authorized.
 */
export async function authorizedFetch(req: Request): Promise<Response> {
    const { accessToken, refreshToken } = mostRecentTokens!;
    const reqWithAuth = new Request(req, { headers: {
            Authorization: `Bearer ${accessToken}`
        } })
    const accessTokenFetch = await fetch(reqWithAuth);
    if (accessTokenFetch.status != 401) {
        // no need for refresh token apparently, return whatever was returned
        return accessTokenFetch;
    } else {
        const refreshTokenFetch = await fetch("/security/refresh-token", { method: 'POST', headers: {
            Authorization: `Bearer ${refreshToken}`
        }})
        if (refreshTokenFetch.ok) {
            // great, token refreshed, try the original request again and let the chips fall where they may :P
            return refreshTokenFetch.json().then((refreshedTokens: dto.LoginDTO) => {
                mostRecentTokens = refreshedTokens;
                storeTokens(refreshedTokens);
                const repeatOriginal = new Request(req, {
                    headers: {
                        Authorization: `Bearer ${refreshedTokens.accessToken}`
                    }
                });
                return fetch(repeatOriginal);
            })
        } else if (refreshTokenFetch.status === 401) {
            // something wrong, could not refresh token!
            // delete tokens (..though a grace period could be given?) and return original response
            mostRecentTokens = undefined;
            storeTokens(undefined);
            return messageClients(EVENT_LOGIN_FAILED).then ( () => {
                return accessTokenFetch;
            })
        } else {
            // something else was wrong, huh.. well, just return original response?
            return accessTokenFetch;
        }
    }
}
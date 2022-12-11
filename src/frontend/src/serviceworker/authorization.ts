import {restoreTokens, storeTokens} from "@/serviceworker/credentialStore";
import {messageClients} from "@/serviceworker/messaging";
import {sendLog} from "@/diagnostics";
import type {AccessToken} from "@/AccessToken";
import jwtDecode from "jwt-decode";


/** Event fired when good credentials have been entered into the system */
export const EVENT_LOGIN_FAILED = "LOGIN_BAD"

/** Event fired when a login has gone bad (perhaps user has logged out?) */
export const EVENT_LOGIN_SUCCESS = "LOGIN_GOOD"

/** Path prefixes for API paths that need authentication  */
const authorizedPathPrefixes = ['/departures', '/backoffice', '/activities']

/** Exceptions for API paths that need authentication */
const noAuthRequired = ['/departures/push/key']

/**
 * Current access & refresh tokens. All the users' tabs currently share the same session. They are also stored in
 * indexedDB in order to survive service worker upgrades.
 *
 * If the property is undefined, we need to check its value in the database.
 */
let mostRecentTokens: undefined | null | dto.LoginDTO = undefined;


/** Returns the currently valid token or null if user is not logged in. */
export async function loggedIn(): Promise<AccessToken | null> {
    const tokensOrNull = (token: null | dto.LoginDTO) => {
        if (token == null) {
            return null;
        } else {
            return jwtDecode(token.accessToken) as AccessToken
        }
    }

    if (mostRecentTokens !== undefined) {
        return tokensOrNull(mostRecentTokens);
    } else {
        // WARN: there is no protection from multiple parts of code calling this at once during the period between
        // the function invocation & promise being resolved. This is _probably_ not a problem as indexedDb query should
        // be quick (there's no other data in there) and there shouldn't be any harm if it does happen.
        return restoreTokens().then( tokens => {
            sendLog("login", "trace", "Restoring security tokens: " + JSON.stringify(tokens))
            mostRecentTokens = tokens;
            return tokensOrNull(mostRecentTokens);
        })
    }
}

/** Updates the authorization tokens, after a login for example */
export function updateTokens(tokens: dto.LoginDTO | null) {
    mostRecentTokens = tokens;
    storeTokens(tokens);
}

export async function handleFetch(ev: FetchEvent) {
    const url = new URL(ev.request.url)
    // TODO: add hostname verification for extra security

    // In case of successful login or password reset, store tokens in service worker
    if (ev.request.url.endsWith("/login")) { interceptLogin(ev) }
    else if (ev.request.url.endsWith("/submit-password-reset")) { interceptPasswordReset(ev) }
    else if (
        authorizedPathPrefixes.filter(prefix => url.pathname.startsWith(prefix)).length &&
        !noAuthRequired.filter(path => url.pathname === path).length
    ) {
        if (await loggedIn()) {
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
            sendLog("login", "info", "Refresh token failed: " + refreshToken)
            mostRecentTokens = null;
            storeTokens(null);
            return messageClients(EVENT_LOGIN_FAILED).then ( () => {
                return accessTokenFetch;
            })
        } else {
            // something else was wrong, huh.. well, just return original response?
            sendLog("login", "warn", `Authorized request failed with code ${refreshTokenFetch.status}`)
            return accessTokenFetch;
        }
    }
}

/** Intercept a login call & update security tokens if successful */
function interceptLogin(ev: FetchEvent) {
    const authReq = new Request(ev.request)
    ev.respondWith(fetch(authReq).then( resp => {
        if (resp.ok) {
            return resp.json().then((login: dto.LoginDTO) => {
                return installAccessTokens(login).then ( () => {
                    return new Response(null, {})
                })
            })
        } else {
            sendLog("login", "debug", "Login NOT successful, got status code " + resp.status)
            return resp;
        }
    }))
}

/** Intercept a successful password reset, store the tokens & log in with them */
function interceptPasswordReset(ev: FetchEvent) {
    ev.respondWith(fetch(ev.request).then(resp => {
        if (resp.ok) {
            return resp.json().then((login: dto.LoginDTO) => {
                return installAccessTokens(login).then(() => {
                    return new Response(null, {})
                })
            })
        } else {
            return resp;
        }
    }))
}

/** Store new login token in IndexedDB and let app know we're logged in now */
function installAccessTokens(tokens: dto.LoginDTO): Promise<void> {
    updateTokens(tokens);
    return messageClients(jwtDecode(tokens.accessToken) as AccessToken);
}
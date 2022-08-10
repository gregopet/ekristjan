import {createFetch, useStorage} from '@vueuse/core'
import { forceLoginScreen } from './router';

interface SecurityStatus {
    accessToken: null | string;
    refreshToken: null | string;
}

const securityState = useStorage('security', {
    accessToken: null,
    refreshToken: null,
} as SecurityStatus)

/** Checks whether a person is logged in (although that login could be stale!) */
export function isLoggedIn() {
    return !!securityState.value.accessToken
}

/** Logs user in & returns null OR fails login & returns error description */
export async function login(email: string, password: string): Promise<string | null> {
    const request = await fetch("/security/login", {
        method: "POST",
        body: JSON.stringify({ email, password })
    });
    if (request.ok) {
        const reply = await request.json() as dto.LoginSuccess;
        securityState.value.accessToken = reply.accessToken
        securityState.value.refreshToken = reply.refreshToken
        return null
    } else if (request.status == 401) {
        return "Napačno geslo";
    } else if (request.status == 404) {
        return "Neznan uporabnik";
    } else {
        return "Prišlo je do napake";
    }
}

/** A Vue hook for fetches */
export const useSecureFetch = createFetch({
    options: {
        fetch: wrappedFetch
    }
})

/** Append access token to fetch before firing it */
function fetchWithAccessToken(input: RequestInfo, init?: RequestInit): Promise<Response> {
    if (!init) init = {}
    if (!init.headers) init.headers = {}
    const headers = init.headers as Record<string, string>
    headers.Authorization = `Bearer ${securityState.value.accessToken}`
    return fetch(input, init);
}

/**
 * Try a fetch with access token; if it fails with 401, try to use refresh token to get a new access token and if that
 * succeeds, repeat the initial request. If the request does not succeed, navigate the user to login.
 */
async function wrappedFetch(input: RequestInfo, init?: RequestInit): Promise<Response> {
    const initialRequest = await fetchWithAccessToken(input, init)
    if (initialRequest.status === 401) {
        console.debug("Attempting to refresh access token")
        const refreshTokenReq = await fetch("/security/refresh-token", {
            method: 'POST',
            headers: { authorization: `Bearer ${securityState.value.refreshToken}` }
        })
        if (refreshTokenReq.ok) {
            const newTokens = await refreshTokenReq.json() as dto.LoginSuccess;
            securityState.value.accessToken = newTokens.accessToken
            securityState.value.refreshToken = newTokens.refreshToken
            return fetchWithAccessToken(input, init);
        } else {
            if (refreshTokenReq.status === 401) {
                console.log("Refresh token didn't work")
                forceLoginScreen();
                return Response.error();
            } else {
                console.log("General error getting refresh token - returning original request, errors & all")
                return initialRequest;
            }
        }
    } else {
        return initialRequest;
    }
}



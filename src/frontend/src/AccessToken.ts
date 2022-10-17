/**
 * Contains a summary of the data included in the access token.
 *
 * The access token is decoded from its base64 form and may contain other fields as well but these are the ones
 * relevant to the frontend application.
 */
export interface AccessToken {
    /** A list of special permissions the user may have */
    permissions: Array<"backOfficePermission">;

    /** The "issued at" claim (in "seconds since Epoch" format) */
    iat: number;
}

/** Does this person have permission to access the backoffice? */
export function hasBackofficePermission(token: AccessToken | null): boolean {
    return (token && token.permissions && token.permissions.indexOf("backOfficePermission") >= 0) || false;
}
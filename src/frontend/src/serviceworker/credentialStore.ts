import {openDB, type IDBPDatabase} from 'idb';

/*
 * Store access & refresh tokens in the local IndexedDB so they survive a service worker update.
 */

const STORE_NAME = "ekristjan";
const TOKEN_KEY = "token" ;

/** Stores tokens inside IndexedDB */
export async function storeTokens(tokens: dto.LoginDTO | null) {
    const db = await getDatabase();
    db.put(STORE_NAME, tokens, TOKEN_KEY)
}

/** Retrieves any stored token from IndexedDB */
export async function restoreTokens(): Promise<dto.LoginDTO | null> {
    const db = await getDatabase();
    return db.get(STORE_NAME, TOKEN_KEY) as Promise<dto.LoginDTO | null>;
}

function getDatabase(): Promise<IDBPDatabase> {
    return openDB("ekristjan", 1, {
        upgrade(database: IDBPDatabase) {
            if (!database.objectStoreNames.contains(STORE_NAME)) {
                database.createObjectStore(STORE_NAME);
            }
        }
    })
}
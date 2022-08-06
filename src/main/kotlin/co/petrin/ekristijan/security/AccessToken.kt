package co.petrin.ekristijan.security

import co.petrin.ekristijan.db.tables.records.TeacherRecord
import io.vertx.core.Future
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.ext.auth.jwtOptionsOf

/*
    This code generates authentication tokens. The JWTAuth already implements an AuthenticationProvider that checks them
    and we can use it within Vertx.

    The app currently defines some permissions and gives them to all users, but this could of course change.
*/

val ACCESS_TOKEN_SCOPE = "access-token"

/** Generates a new access token that can be used with the application. Currently gives out all roles! */
fun generateAccessToken(teacher: TeacherRecord, expiresInMinutes: Int, jwt: JWTAuth): String {
    val props = jwtOptionsOf(
        subject = teacher.email,
        expiresInMinutes = expiresInMinutes,
    )
    return jwt.generateToken(jsonObjectOf(
        "teacherId" to teacher.teacherId,
        "schoolId" to teacher.schoolId,
        "name" to teacher.name,
        "scope" to ACCESS_TOKEN_SCOPE,
    ), props)
}
package co.petrin.ekristijan.security

import co.petrin.ekristijan.db.tables.records.TeacherRecord
import io.vertx.core.Future
import io.vertx.ext.auth.User
import io.vertx.ext.auth.authorization.Authorization
import io.vertx.ext.auth.authorization.PermissionBasedAuthorization
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.ext.auth.jwtOptionsOf

/*
    This code generates authentication tokens. The JWTAuth already implements an AuthenticationProvider that checks them
    and we can use it within Vertx.

    The app currently defines some permissions and gives them to all users, but this could of course change.
*/

val ACCESS_TOKEN_SCOPE = "access-token"


/** Does a user have permission to access the backoffice? */
val OFFICE_PERMISSION = PermissionBasedAuthorization.create("backOfficePermission")

/** Generates a new access token that can be used with the application. Currently gives out all roles! */
fun generateAccessToken(teacher: TeacherRecord, expiresInMinutes: Int, jwt: JWTAuth): String {
    val permissions = mutableListOf<String>()
    if (teacher.backofficeAccess) permissions.add(OFFICE_PERMISSION.permission)
    val props = jwtOptionsOf(
        subject = teacher.email,
        expiresInMinutes = expiresInMinutes,
        permissions = if (permissions.isEmpty()) null else permissions,
    )
    return jwt.generateToken(jsonObjectOf(
        "teacherId" to teacher.teacherId,
        "schoolId" to teacher.schoolId,
        "name" to teacher.name,
        "scope" to ACCESS_TOKEN_SCOPE,
    ), props)
}

/** A helper function to get the teacher ID in handlers where the user must be logged in */
val RoutingContext.teacherId: Int get() = user().principal().getInteger("teacherId")

/** A helper function to get the school ID in handlers where the user must be logged in */
val RoutingContext.schoolId: Int get() = user().principal().getInteger("schoolId")
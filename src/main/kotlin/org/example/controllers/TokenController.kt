package org.example.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import io.javalin.http.Context
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.RouteRole
import javalinjwt.JWTGenerator
import javalinjwt.JWTProvider
import org.example.Roles
import org.example.model.User

val HEADER = "Authorization"

class UserGenerator : JWTGenerator<User> {
    override fun generate(user: User, alg: Algorithm?): String {
        val token: JWTCreator.Builder = JWT.create()
            .withClaim("id", user.id)
        return token.sign(alg)
    }
}

class TokenController(var users: MutableList<User>) {

    private val algorithm = Algorithm.HMAC256("very_secret")
    private val verifier = JWT.require(algorithm).build()
    private val generator = UserGenerator()
    private val provider = JWTProvider(algorithm, generator, verifier)


    fun addToken(ctx: Context, user: User) {
        val token = provider.generateToken(user)
        ctx.header(HEADER, token)
    }

    private fun tokenToUser(header: String): User {
        val validateToken = provider.validateToken(header)
        if (validateToken.isPresent) {
            val userId = validateToken.get().getClaim("id").asString()
            try {
                return users.find { it.id == userId } ?: throw UnauthorizedResponse("Token not valid")
            } catch (error: java.lang.Exception) {
                throw UnauthorizedResponse(error.message!!)
            }
        }
        throw UnauthorizedResponse("Token not valid")
    }

    fun validate(ctx: Context) {
        val header = ctx.header(HEADER)
        when {
            ctx.routeRoles().contains(Roles.ANYONE) -> return
            header == null -> {
                throw UnauthorizedResponse("Invalid token")
            }
            else -> {
                val user = tokenToUser(header)
                ctx.attribute("user", user)
                return
            }
        }
    }
}
package org.example.controllers

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import org.example.utils.CreateUserBody
import org.example.utils.EditUserBody
import org.example.utils.UserDTO
import org.example.model.User
import org.example.utils.LoginBody
import kotlin.reflect.KFunction2

class UserController(var users: MutableList<User>, val addToken: KFunction2<Context, User, Unit>) {
    fun getAllUsers(ctx: Context) {
        ctx.json(users.map { UserDTO(it) })
    }

    fun login(ctx: Context) {
        val body =  ctx.bodyValidator(LoginBody::class.java)
            .check({ it.username.isNotEmpty() }, "username was empty")
            .check({ it.password.isNotEmpty() }, "password was empty")
            .getOrThrow {
                BadRequestResponse("mandaste cualquier cosa")
            }
        val user = users.find { it.username == body.username && it.password == body.password } ?: throw NotFoundResponse("No se encontro o algun dato mal")
        addToken(ctx, user)
        ctx.json(UserDTO(user))

    }
    fun createUser(ctx: Context) {
        val body =  ctx.bodyValidator(CreateUserBody::class.java)
            .check({ it.username.isNotEmpty() }, "username was empty")
            .check({ it.password.isNotEmpty() }, "password was empty")
            .getOrThrow {
                BadRequestResponse("mandaste cualquier cosa")
            }
        val newUser = User("u_${users.size + 1}", body.username, body.password, mutableListOf())
        users.add(newUser)
        ctx.json(UserDTO(newUser))
    }

    fun getUser(ctx: Context) {
        val id = ctx.pathParam("id")
        val user = users.find { it.id == id } ?: throw NotFoundResponse("el id no se encontro")
        ctx.json(UserDTO(user))
    }

    fun updateUser(ctx: Context) {
        val id = ctx.pathParam("id")
        val user = users.find { it.id == id } ?: throw NotFoundResponse("el id no se encontro")
        val body =  ctx.bodyValidator(EditUserBody::class.java)
            .check({ it.password.isNotEmpty() }, "password was empty")
            .check({ it.password != user.password }, "password no puede ser el mismo que la anterior")
            .getOrThrow {
                BadRequestResponse("mandaste cualquier cosa")
            }
        user.password = body.password
        ctx.json(UserDTO(user))
    }

    fun deleteUser(ctx: Context) {
        val id = ctx.pathParam("id")
        users = users.filter { it.id != id }.toMutableList()
        ctx.result()
    }
}
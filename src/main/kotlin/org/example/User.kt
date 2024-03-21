package org.example

import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import io.javalin.validation.ValidationError

class User (val id: String, val username: String, var password: String, val followers: MutableList<User>)

class UserController {

    var users = mutableListOf(
        User("u_1", "jota", "jota", mutableListOf()),
        User("u_2", "fede", "fede", mutableListOf()),
        User("u_3", "pepe", "pepe", mutableListOf()),
        User("u_4", "flor", "flor", mutableListOf())
    )

    init {
        users[0].followers.add(users[1])
        users[1].followers.add(users[0])
    }

    fun getAllUsers(ctx: Context) {
        ctx.json(users.map { UserDTO(it) })
    }

    fun createUser(ctx: Context) {
        val body =  ctx.bodyValidator(CreateUserBody::class.java)
            .check({ it.username.isNotEmpty() }, ValidationError("username was empty", args = mapOf()))
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
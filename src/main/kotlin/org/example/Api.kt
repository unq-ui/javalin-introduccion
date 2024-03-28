package org.example

import io.javalin.Javalin

import io.javalin.apibuilder.ApiBuilder.*
import org.example.controllers.TokenController
import org.example.controllers.UserController
import org.example.model.User


import io.javalin.security.RouteRole

enum class Roles: RouteRole {
    ANYONE,
    USER
}
class Api {

    var users = mutableListOf(
        User("u_1", "jota", "jota", mutableListOf()),
        User("u_2", "fede", "fede", mutableListOf()),
        User("u_3", "pepe", "pepe", mutableListOf()),
        User("u_4", "flor", "flor", mutableListOf())
    )

    private val app: Javalin

    private val tokenController = TokenController(users)
    private val userController = UserController(users, tokenController::addToken)
    init {
        app = Javalin.create { config ->
            config.http.defaultContentType = "application/json"
            config.router.apiBuilder {
                path("/login") {
                    post(userController::login, Roles.ANYONE)
                }
                path("/users") {
                    get(userController::getAllUsers, Roles.ANYONE)
                    post(userController::createUser, Roles.USER)
                    path("/{id}") {
                        get(userController::getUser, Roles.ANYONE)
                        put(userController::updateUser, Roles.USER)
                        delete(userController::deleteUser, Roles.USER)
                    }
                }
            }
        }
    }
    fun start(port: Int = 7070) {
        app.beforeMatched(tokenController::validate)
        app.start(port)
    }
}
fun main() {
    Api().start()
}
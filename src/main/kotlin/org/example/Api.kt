package org.example

import io.javalin.Javalin

import io.javalin.apibuilder.ApiBuilder.*
import org.example.controllers.UserController

class Api {
    private val app: Javalin
    private val userController = UserController()
    init {
        app = Javalin.create { config ->
            config.http.defaultContentType = "application/json"
            config.router.apiBuilder {
                path("/users") {
                    get(userController::getAllUsers)
                    post(userController::createUser)
                    path("/{id}") {
                        get(userController::getUser)
                        put(userController::updateUser)
                        delete(userController::deleteUser)
                    }
                }
            }
        }
    }
    fun start(port: Int = 7070) {
        app.start(port)
    }
}
fun main() {
    Api().start()
}
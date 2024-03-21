package org.example

import bootstrap.Bootstrap
import io.javalin.Javalin

import io.javalin.apibuilder.ApiBuilder.*

fun main() {
    val userController = UserController()

    val service = Bootstrap().getSystem()


    val app = Javalin.create { config ->
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

    app.start(7070)
}
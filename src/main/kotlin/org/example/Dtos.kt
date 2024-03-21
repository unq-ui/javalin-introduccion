package org.example

class UserDTO (user: User) {
    val id = user.id
    val username = user.username
    val followers = user.followers.map { UserDTO2(it) }
}

class UserDTO2 (user: User) {
    val id = user.id
    val username = user.username
    val followers = user.followers.map { SimpleUserDTO(it) }
}

class SimpleUserDTO (user: User) {
    val id = user.id
    val username = user.username
}
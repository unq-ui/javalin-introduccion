package org.example.model

class User (val id: String, val username: String, var password: String, val followers: MutableList<User>)

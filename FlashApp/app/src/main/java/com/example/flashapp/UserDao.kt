package com.example.flashapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insert(userentity: UserEntity)

    @Query("SELECT * FROM UserEntity WHERE username = :username AND password = :password")
    fun findUser(username: String, password: String): UserEntity?

    @Query("SELECT * FROM UserEntity WHERE username = :username")
    fun findUserByName(username: String): UserEntity?
}
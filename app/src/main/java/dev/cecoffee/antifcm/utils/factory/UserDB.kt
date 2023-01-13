package dev.cecoffee.antifcm.utils.factory

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.schema.time

object UserDB :Table<Nothing>(tableName = "users"){
    val uid = int("uid").primaryKey()
    val accessKey = text("access_key")
    val expireTime = time("expires")

}
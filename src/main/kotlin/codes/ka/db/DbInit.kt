package codes.ka.db

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDbConnection() {
    // see application.conf file in resources
    val host = System.getenv("DB_HOST")
    val port = System.getenv("DB_PORT")
    val user = System.getenv("DB_USER")
    val password = System.getenv("DB_PASSWORD")
    val database = System.getenv("DB_NAME")

    Database.connect(
        "jdbc:postgresql://$host:$port/$database", driver = "org.postgresql.Driver",
        user = user, password = password
    )

    transaction {
        SchemaUtils.createMissingTablesAndColumns(AppInstallation, RefreshToken)
    }
}

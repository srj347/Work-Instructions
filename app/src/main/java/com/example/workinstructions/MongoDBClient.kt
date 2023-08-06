package com.example.workinstructions

import com.mongodb.ConnectionString
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.MongoException
import com.mongodb.client.MongoDatabase
import org.bson.BsonInt64
import org.bson.Document

object MongoDBClient {
//    val CONNECTION_STRING = "mongodb+srv://srj347:Suraj2580@cluster0.rb2zsxz.mongodb.net"
    val CONNECTION_STRING = "mongodb://admin:Innovation54321@0.tcp.in.ngrok.io:14008/"

//    fun connect(): MongoDatabase {
//        val connectionString = ConnectionString(CONNECTION_STRING)
////        val serverApi = ServerApi.builder()
////            .version(ServerApiVersion.V1)
////            .build()
//
//        val mongoClientSettings = MongoClientSettings.builder()
//            .applyConnectionString(connectionString)
//            .serverApi(serverApi)
//            .build()
//        return MongoClient.create(mongoClientSettings).getDatabase("Innovapptive")
//    }

    suspend fun setupConnection(
        databaseName: String = "Innovapptive",
        connectionEnvVariable: String = "MONGODB_URI"
    ): MongoDatabase? {
        val connectString = if (System.getenv(connectionEnvVariable) != null) {
            System.getenv(connectionEnvVariable)
        } else {
            CONNECTION_STRING
        }

        val mongoClientUri: MongoClientURI = MongoClientURI(CONNECTION_STRING)
        val client = MongoClient(mongoClientUri)
        val database = client.getDatabase(databaseName)

        return try {
            // Send a ping to confirm a successful connection
            val command = Document("ping", BsonInt64(1))
            database.runCommand(command)
            println("Pinged your deployment. You successfully connected to MongoDB!")
            database
        } catch (me: MongoException) {
            System.err.println(me)
            null
        }
    }

}
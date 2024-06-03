/**
 * Project: Space Game
 * Purpose Details: Creating a MySQL and MongoDB CRUD using the space game.
 * Course: IST 242 Section 611 Inter App Dev
 * Author: Christopher Carlos
 * Date Developed: 06/02/24
 * Last Date Changed: 06/02/24
 * Revision: 1
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;

public class Main {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/SpaceGame";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "IST888IST888";

    public static void main(String[] args) {
        // MySQL CRUD operations
        SpaceGame_MySQL_CRUD();

        // MongoDB CRUD operations
        SpaceGame_Mongo_CRUD();
    }

    private static void SpaceGame_MySQL_CRUD() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Create
            insertShipHealth(connection);

            // Read
            List<Ship> ships = getAllShips(connection);
            for (Ship ship : ships) {
                System.out.println(ship);
            }

            // Update
            updateShipHealth(connection);

            // Read again
            ships = getAllShips(connection);
            for (Ship ship : ships) {
                System.out.println(ship);
            }

            // Delete
            deleteShipHealth(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void SpaceGame_Mongo_CRUD() {
        // Create a MongoClient using the factory method
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Access the database and collection
            MongoDatabase database = mongoClient.getDatabase("SpaceGame");
            MongoCollection<Document> collection = database.getCollection("ships");

            // Create
            insertShipHealth(collection);

            // Read
            FindIterable<Document> ships = getAllShips(collection);
            for (Document ship : ships) {
                System.out.println(ship.toJson());
            }

            // Update
            updateShipHealth(collection);

            // Read again
            ships = getAllShips(collection);
            for (Document ship : ships) {
                System.out.println(ship.toJson());
            }

            // Delete
            deleteShipHealth(collection);
        }
    }

    private static void insertShipHealth(Connection connection) throws SQLException {
        String sql = "INSERT INTO ships (name, health) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "Starship");
            preparedStatement.setInt(2, 100);
            preparedStatement.executeUpdate();
            System.out.println("Insert success: " + "Starship" + " with health " + 100);
        }
    }

    private static List<Ship> getAllShips(Connection connection) throws SQLException {
        List<Ship> ships = new ArrayList<>();
        String sql = "SELECT name, health FROM ships";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int health = resultSet.getInt("health");
                ships.add(new Ship(name, health));
            }
        }
        return ships;
    }

    private static void updateShipHealth(Connection connection) throws SQLException {
        String sql = "UPDATE ships SET health = ? WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, 150);
            preparedStatement.setString(2, "Starship");
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Update success: " + "Starship" + " with health " + 150);
            } else {
                System.out.println("Update failed: Ship " + "Starship" + " not found.");
            }
        }
    }

    private static void deleteShipHealth(Connection connection) throws SQLException {
        String sql = "DELETE FROM ships WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "Starship");
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Delete success: " + "Starship");
            } else {
                System.out.println("Delete failed: Ship " + "Starship" + " not found.");
            }
        }
    }

    private static void insertShipHealth(MongoCollection<Document> collection) {
        Document newShip = new Document("name", "Starship")
                .append("health", 100);
        collection.insertOne(newShip);
        System.out.println("Insert success: " + "Starship" + " with health " + 100);
    }

    private static FindIterable<Document> getAllShips(MongoCollection<Document> collection) {
        return collection.find();
    }

    private static void updateShipHealth(MongoCollection<Document> collection) {
        Document updatedShip = new Document("$set", new Document("health", 150));
        collection.updateOne(new Document("name", "Starship"), updatedShip);
        System.out.println("Update success: " + "Starship" + " with health " + 150);
    }

    private static void deleteShipHealth(MongoCollection<Document> collection) {
        collection.deleteOne(new Document("name", "Starship"));
        System.out.println("Delete success: " + "Starship");
    }
}

class Ship {
    private final String name;
    private final int health;

    public Ship(String name, int health) {
        this.name = name;
        this.health = health;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "name='" + name + '\'' +
                ", health=" + health +
                '}';
    }
}

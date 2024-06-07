import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;

class Main {
    public static void main(String[] args) {
        // Create a MongoClient using the factory method
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            // Access the database and collection
            MongoDatabase database = mongoClient.getDatabase("SpaceGame");
            MongoCollection<Document> collection = database.getCollection("ships");

            // Example: Insert a ship document
            Document newShip = new Document("name", "Starship")
                    .append("health", 100);
            collection.insertOne(newShip);

            // Read all ships
            FindIterable<Document> ships = collection.find();
            for (Document ship : ships) {
                System.out.println(ship.toJson());
            }

            // Update the health of the ship
            Document updatedShip = new Document("$set", new Document("health", 150));
            collection.updateOne(new Document("name", "Starship"), updatedShip);

            // Read all ships again
            ships = collection.find();
            for (Document ship : ships) {
                System.out.println(ship.toJson());
            }

            // Delete the ship
            collection.deleteOne(new Document("name", "Starship"));

            // Read all ships again to confirm deletion
            ships = collection.find();
            for (Document ship : ships) {
                System.out.println(ship.toJson());
            }

        }
    }
}

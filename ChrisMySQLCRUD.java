import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Main {
    private static final String JDBC_URL = "jdbc:mysql://127.0.0.1:3306/SpaceGame";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "IST888IST888";

    public static void main(String[] args) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Create
            insertShip(connection, "Starship", 100);

            // Read
            List<Ship> ships = getAllShips(connection);
            for (Ship ship : ships) {
                System.out.println(ship);
            }

            // Update
            updateShip(connection, "Starship", 150);

            // Read again
            ships = getAllShips(connection);
            for (Ship ship : ships) {
                System.out.println(ship);
            }

            // Delete
            deleteShip(connection, "Starship");

            // Read again
            ships = getAllShips(connection);
            for (Ship ship : ships) {
                System.out.println(ship);
            }

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

    private static void insertShip(Connection connection, String name, int health) throws SQLException {
        // Check if the ship already exists
        if (shipExists(connection, name)) {
            System.out.println("Ship already exists: " + name);
            return;
        }

        String sql = "INSERT INTO ships (name, health) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, health);
            preparedStatement.executeUpdate();
            System.out.println("Insert success: " + name + " with health " + health);
        }
    }

    private static boolean shipExists(Connection connection, String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ships WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
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

    private static void updateShip(Connection connection, String name, int health) throws SQLException {
        String sql = "UPDATE ships SET health = ? WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, health);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            System.out.println("Update success: " + name + " with health " + health);
        }
    }

    private static void deleteShip(Connection connection, String name) throws SQLException {
        String sql = "DELETE FROM ships WHERE name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            System.out.println("Delete success: " + name);
        }
    }
}

class Ship {
    private String name;
    private int health;

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

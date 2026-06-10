package cn.edu.whut.sept.zuul;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单的数据库工具类，用于保存/载入存档。
 * 使用 MySQL (root/123456) 在本机创建名为 `zuul` 的数据库和 `saves` 表。
 */
public class DBUtil {
    private static final String URL_ROOT = "JDBC:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String URL_DB = "JDBC:mysql://localhost:3306/zuul?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ensureDatabaseAndTable();
        } catch (Exception e) {
            System.out.println("数据库初始化失败: " + e.getMessage());
        }
    }

    private static void ensureDatabaseAndTable() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL_ROOT, USER, PASSWORD);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE DATABASE IF NOT EXISTS zuul CHARACTER SET utf8mb4");
        }
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS saves ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100),"
                    + "saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "current_room VARCHAR(255),"
                    + "player_max_weight DOUBLE,"
                    + "player_current_weight DOUBLE,"
                    + "inventory TEXT,"
                    + "map_state TEXT"
                    + ")");
        }
    }

    public static void saveGame(String name, String currentRoom, double maxWeight, double currentWeight, String inventory, String mapState) throws SQLException {
        String sql = "INSERT INTO saves (name, current_room, player_max_weight, player_current_weight, inventory, map_state) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, currentRoom);
            ps.setDouble(3, maxWeight);
            ps.setDouble(4, currentWeight);
            ps.setString(5, inventory);
            ps.setString(6, mapState);
            ps.executeUpdate();
        }
    }

    public static Map<String, String> loadLatest() throws SQLException {
        String sql = "SELECT * FROM saves ORDER BY saved_at DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                Map<String, String> m = new HashMap<>();
                m.put("id", String.valueOf(rs.getInt("id")));
                m.put("name", rs.getString("name"));
                m.put("current_room", rs.getString("current_room"));
                m.put("player_max_weight", String.valueOf(rs.getDouble("player_max_weight")));
                m.put("player_current_weight", String.valueOf(rs.getDouble("player_current_weight")));
                m.put("inventory", rs.getString("inventory"));
                m.put("map_state", rs.getString("map_state"));
                m.put("saved_at", rs.getString("saved_at"));
                return m;
            } else {
                return null;
            }
        }
    }

    public static Map<String, String> loadById(int id) throws SQLException {
        String sql = "SELECT * FROM saves WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> m = new HashMap<>();
                    m.put("id", String.valueOf(rs.getInt("id")));
                    m.put("name", rs.getString("name"));
                    m.put("current_room", rs.getString("current_room"));
                    m.put("player_max_weight", String.valueOf(rs.getDouble("player_max_weight")));
                    m.put("player_current_weight", String.valueOf(rs.getDouble("player_current_weight")));
                    m.put("inventory", rs.getString("inventory"));
                    m.put("map_state", rs.getString("map_state"));
                    m.put("saved_at", rs.getString("saved_at"));
                    return m;
                } else {
                    return null;
                }
            }
        }
    }

    public static java.util.List<java.util.Map<String, String>> listSaves() throws SQLException {
        String sql = "SELECT id, name, saved_at, current_room FROM saves ORDER BY saved_at DESC";
        java.util.List<java.util.Map<String, String>> list = new java.util.ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                java.util.Map<String, String> m = new java.util.HashMap<>();
                m.put("id", String.valueOf(rs.getInt("id")));
                m.put("name", rs.getString("name"));
                m.put("saved_at", rs.getString("saved_at"));
                m.put("current_room", rs.getString("current_room"));
                list.add(m);
            }
        }
        return list;
    }

    public static boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM saves WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        }
    }

    /**
     * 使用指定 id 覆盖或插入存档。
     * 如果该 id 已存在则更新，否则插入带有该 id 的新记录（并更新 saved_at）。
     */
    public static void saveGameWithId(int id, String name, String currentRoom, double maxWeight, double currentWeight, String inventory, String mapState) throws SQLException {
        String sql = "INSERT INTO saves (id, name, current_room, player_max_weight, player_current_weight, inventory, map_state) VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE name=VALUES(name), saved_at=CURRENT_TIMESTAMP, current_room=VALUES(current_room), player_max_weight=VALUES(player_max_weight), player_current_weight=VALUES(player_current_weight), inventory=VALUES(inventory), map_state=VALUES(map_state)";
        try (Connection conn = DriverManager.getConnection(URL_DB, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, currentRoom);
            ps.setDouble(4, maxWeight);
            ps.setDouble(5, currentWeight);
            ps.setString(6, inventory);
            ps.setString(7, mapState);
            ps.executeUpdate();
        }
    }
}


package com.saim.pneumoniacare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.dao.DuplicateKeyException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@Component
public class DatabaseInitializer {
    private final JdbcTemplate db;

    @Autowired
    public DatabaseInitializer(JdbcTemplate db) {
        this.db = db;
    }

    public void initializeDatabase() {
        String createUserTable = "CREATE TABLE IF NOT EXISTS User (" +
                "userID INT AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "age INT NOT NULL," +
                "nationality VARCHAR(20) NOT NULL," +
                "email VARCHAR(100) NOT NULL," +
                "password VARCHAR(100) NOT NULL," +
                "PRIMARY KEY (userID));";

        String createReportTable = "CREATE TABLE IF NOT EXISTS Report (" +
                "reportID INT AUTO_INCREMENT," +
                "userID INT," +
                "report TEXT," +
                "time VARCHAR(20)," +
                "PRIMARY KEY (reportID, userID)," +
                "FOREIGN KEY (userID) REFERENCES User(userID));";

        try {
            // Create the users table
            db.execute(createUserTable);
            db.execute(createReportTable);
        } catch (DataAccessException e) {
            // Handle any exceptions that occur during table creation
            e.printStackTrace();
        }
    }

    public String registerUser(String name, int age, String nationality, String email, String password) {
        String insertQuery = "INSERT INTO user (name, age, nationality, email, password) VALUES (?, ?, ?, ?, ?)";

        try {
            db.update(insertQuery, name, age, nationality, email, password);
            return "{\"status\":\"ok\",\"message\":\"User registered successfully.\", \"username\":\"" + name + "\", \"age\":" + age + ", \"email\":\"" + email + "\"}";
        } catch (DuplicateKeyException e) {
            return "{\"status\":\"error\",\"message\":\"User already exists.\"}";
        } catch (DataAccessException e) {
            return "{\"status\":\"error\",\"message\":\"An error occurred while registering the user.\"}";
        }
    }

    public String loginUser(String name, String password) {
        String query = "SELECT * FROM user WHERE name = ? AND password = ?";
        try {
            // Query the database
            return db.queryForObject(query, new Object[]{name, password}, new RowMapper<String>() {
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    // If a matching user is found, return user details as JSON string
                    int userId = rs.getInt("userId");
                    String retrievedEmail = rs.getString("email");
                    String retrievedName = rs.getString("name");
                    return String.format("{\"status\":\"ok\",\"userId\":%d,\"email\":\"%s\",\"name\":\"%s\"}", userId, retrievedEmail, retrievedName);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            // If no matching user is found, return an error message
            return "{\"status\":\"error\",\"message\":\"Incorrect email or password\"}";
        } catch (DataAccessException e) {
            // For other database access errors, return an error message
            return "{\"status\":\"error\",\"message\":\"An error occurred while logging in\"}";
        }
    }

    public void saveReport(int userId, String report, String time) {
        String insertReportSQL = "INSERT INTO report (userID, report, time) VALUES ( ?, ?, ?)";
        try {
            db.update(insertReportSQL, userId, report, time);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    public List<Report> getReports(int userId) {
        String query = "SELECT reportID, userID, report FROM report WHERE userID = ?";
        try {
            return db.query(query, new Object[]{userId}, (rs, rowNum) -> {
                int reportId = rs.getInt("reportID");
                String report = rs.getString("report");
                return new Report(reportId, userId, report);
            });
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getUserID(String email) {
        String query = "SELECT userID FROM user WHERE email = ?";
        try {
            // Query the database
            return db.queryForObject(query, new Object[]{email}, new RowMapper<Integer>() {
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    // If a matching user is found, return user details as JSON string
                    int userId = rs.getInt("userId");
                    return userId;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            // If no matching user is found, return an error message
            return -1;
        } catch (DataAccessException e) {
            // For other database access errors, return an error message
            return -2;
        } catch (NullPointerException e) {
            return -3;
        }
    }

    public String getSQLPosts() {
        String sql = "SELECT report, time, name FROM report r JOIN user s ON r.userID = s.userID";
        try {
            List<Map<String, Object>> reports = db.queryForList(sql);
            StringBuilder messageBuilder = new StringBuilder("{\"status\":\"ok\",\"message\":[");
            for (int i = 0; i < reports.size(); i++) {
                Map<String, Object> report = reports.get(i);
                String userId = (String) report.get("name");
                String reportText = (String) report.get("report");
                String time = (String) report.get("time");
                reportText = reportText.replaceAll("\"", "\\\\\"");
                String reportJson = String.format(
                        "{\"userid\":\"%s\",\"report\":\"%s\",\"time\":\"%s\"}",
                        userId,
                        reportText,
                        time
                );
                messageBuilder.append(reportJson);
                if (i < reports.size() - 1) {
                    messageBuilder.append(",");
                }
            }
            messageBuilder.append("]}");
            return messageBuilder.toString().replace("\n", ""); // Remove new lines
        } catch (DataAccessException e) {
            e.printStackTrace();
            return "{\"status\":\"error\",\"message\":\"An error occurred while retrieving reports\"}";
        }
    }


/*
    public String getReport() {
        String query = "SELECT * FROM report";
        try {
            // Query the database
            return db.queryForObject(query, new Object[]{}, new RowMapper<Integer>() {
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    // If a matching user is found, return user details as JSON string
                    String report = rs.getString("report");
                    String time = rs.getString("time");
                    return report, time;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            // If no matching user is found, return an error message
            return -1;
        } catch (DataAccessException e) {
            // For other database access errors, return an error message
            return -2;
        } catch (NullPointerException e) {
            return -3;
        }
    }

 */
}
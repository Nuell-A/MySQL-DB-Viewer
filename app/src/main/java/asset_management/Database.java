package asset_management;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.sql.ResultSet;
import java.util.StringJoiner;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.lang.StringBuilder;


public class Database {
    Connection conn;
    Statement stmt = null;
    PreparedStatement preparedStatement = null;
    ResultSet result = null;
    
    @SuppressWarnings("deprecation")
    public Database() {
        // Load driver.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Successfully loaded driver...");
        } catch (Exception error) {
            System.out.println(error);
        }
        // Establish connection to server. 
        try {
            // Load properties file.
            Properties properties = new Properties();
            FileInputStream file = new FileInputStream("config.properties");
            properties.load(file);

            // Get MySQL config.
            String url = properties.getProperty("MYSQL_URL");
            String username = properties.getProperty("MYSQL_USERNAME");
            String password = properties.getProperty("MYSQL_PASSWORD");

            // Establish conn
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to database...");

            // Create statement for queries.
            stmt = conn.createStatement();
        } catch (SQLException error) {
            System.out.println("Error connecting to database...");
            System.out.println("SQLexception: " + error.getMessage());
            System.out.println("SQLState: " + error.getSQLState());
            System.out.println("VendorError: " + error.getErrorCode());System.out.println(error);
        } catch (IOException error) {
            System.out.println("There was an error with the properties file");
            System.out.println(error);
        }
    }

    public ResultSet queryAll(String tableName) {
        /*
         * Queries database table and returns a ResultSet
         */
        try {
            // Queries database. 
            System.out.println("Querying database...");
            if (stmt == null) {
                stmt = conn.createStatement();
            }
            result = stmt.executeQuery("SELECT * FROM " + tableName);
            System.out.println("Success: :" + result.toString());
            return result;
            
        } catch (SQLException error) {
            System.out.println("Error quering testing...");
            System.out.println("SQLexception: " + error.getMessage());
            System.out.println("SQLState: " + error.getSQLState());
            System.out.println("VendorError: " + error.getErrorCode());System.out.println(error);
            return null;
        }
    }

    String ConvertResultSet(ResultSet resultSet) {
        StringBuilder result = new StringBuilder();
        // Gets meta data

        // Amount of columns
        return "hello";
    }

    void closeSQLObjects() {
        try {
            // Closes connections in REVERSE ORDER.
            if (result != null) {
                System.out.println("Releasing result object...");
                result.close();
                result = null;
            }

            if (stmt != null) {
                System.out.println("Releasing statement object...");
                stmt.close();
                stmt = null;
            }

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            conn.close();
        } catch (SQLException error) {
            System.out.println("Error quering testing...");
            System.out.println("SQLexception: " + error.getMessage());
            System.out.println("SQLState: " + error.getSQLState());
            System.out.println("VendorError: " + error.getErrorCode());System.out.println(error);
        }
    }

    void createTable(String tableName, ArrayList<HashMap<String, Object>> fields) {
        /* CREATE TABLE tableName(
        tableNameID INT PRIMARY KEY AUTO_INCREMENT,
        col1 VARCHAR(100) NOT NULL);
        */
        /*
         * ArryaList of column attributes
         * [
         * {
         * fieldName: name,
         * dataType: int,
         * nullable: true,
         * },
         * ]
         */
        try {
            // Initiates if not already active.
            if (stmt == null) {
                stmt = conn.createStatement();
            }

            // Will have complete script at the end for table statement.
            String createScript = "create table " + tableName + " ("
                                + tableName + "ID int primary key auto_increment";
            // Creates the body of the script
            for (HashMap<String, Object> hashmp : fields) {
                // Field attributes.
                String fieldName = hashmp.get("fieldName").toString();
                System.out.println(fieldName);
                String dataType;
                boolean nullable;

                // Checks data type of field.
                if (hashmp.get("dataType").toString() == "integer") {
                    dataType = "INT";
                } else {
                    dataType = "VARCHAR(180)";
                }
                
                // Checks if field can be nullable
                if (hashmp.get("nullable").toString() == "false") {
                    nullable = false;
                } else {
                    nullable = true;
                }

                // Creates line for current field in the table.
                if (nullable) {
                    createScript += ", " + fieldName + " " + dataType;
                } else {
                    createScript += ", " + fieldName + " " + dataType + " NOT NULL";
                }
            }
            // Finishes off script and executes.
            createScript += ");";
            System.out.println("Executing statment...");
            System.out.println(createScript);
            stmt.executeUpdate(createScript);
            System.out.println("Table successfully created.");

        } catch (SQLException error) {
            System.out.println("There was an error creating the table...");
            System.out.println("SQLexception: " + error.getMessage());
            System.out.println("SQLState: " + error.getSQLState());
            System.out.println("VendorError: " + error.getErrorCode());System.out.println(error);
        }
    }

    void insertInto(String tableName, HashMap<String, Object> values) {
        // INSERT INTO tableName(col1, col2, col3) values(val1, val2, val3);
        /* HashMap should hold correct column names along with value.
         * {
         * columnName1=val1,
         * calumnName2=val2,
         * }
         */

        // Attributes
        StringJoiner columns = new StringJoiner(",", "(", ")");
        StringJoiner vals = new StringJoiner(",", "(", ")");

        try {
            // Get column names and insert placeholders.
            for (Map.Entry<String, Object> kp : values.entrySet()) {
                columns.add(kp.getKey());
                vals.add("?"); // ? is for prepared statement
            }

            String insertScript = "INSERT INTO " + tableName + columns.toString() + " VALUES" + vals.toString();
            System.out.println("PLACEHOLDERS: " + insertScript);
            
            // PreparedStatement used to insert values properly for example 'string value' neesd the single ' around them.
            PreparedStatement preparedStatement = conn.prepareStatement(insertScript);
            // Iterate over the vals list
            int index = 1; // Index to keep track of ?'s 
            for (Object value : values.values()) {
                preparedStatement.setObject(index, value);
                index++;
            }
            preparedStatement.executeUpdate();
            System.out.println("Successfully inserted " + preparedStatement.toString());

        } catch (SQLException error) {
            System.out.println("There was an error inserting data...");
            System.out.println("SQLexception: " + error.getMessage());
            System.out.println("SQLState: " + error.getSQLState());
            System.out.println("VendorError: " + error.getErrorCode());System.out.println(error);
        }
    }
}

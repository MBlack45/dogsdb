package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ScriptRunner {
    public static void runScript(Connection connection, String resourcePath) throws SQLException {
        try (InputStream inputStream = ScriptRunner.class.getClassLoader().getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            Statement statement = connection.createStatement();
            String line;
            StringBuilder script = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                script.append(line);
                // Check if it's the end of a statement
                if (line.trim().endsWith(";")) {
                    // Execute the statement
                    statement.execute(script.toString());
                    script.setLength(0); // Clear the StringBuilder for the next statement
                }
            }

            statement.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package com.spring.heroku.odata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;

import static spark.Spark.*;


/**
 * Created by cshah on 4/21/2017.
 */
public class SparkMain {

    public static void main(String args[]) throws Exception {
        System.out.println(" Hello World Maven Spark Java Heroku !");
        System.out.println(" Env " + System.getenv("PORT") );
        port( Integer.parseInt( System.getenv("PORT") ) );

        get("/hello", (req, res) -> {
            String output = " why did you print hello ? message from .env file " + System.getenv("HELLO_MESG") + " db info " + System.getenv("JDBC_DATABASE_URL") ;
            output += "<br><br> java version : " + System.getProperty("java.version");
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                output += "<br> env : " + envName + " : " + env.get(envName);
            }
            return output;
        });

        get("/db", (req, res) -> {
            Connection connection = null;
            try {
                String output = "db creating : ";
                connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
                Statement stmt = connection.createStatement();
                //stmt.executeUpdate("CREATE SEQUENCE odata_test_seq START 101 ");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ODATA_TEST (ID integer DEFAULT nextval('odata_test_seq'), VALUE VARCHAR(100), CREATEDTIME timestamp)");
                stmt.executeUpdate("INSERT INTO ODATA_TEST(VALUE, CREATEDTIME) VALUES ('VALUE ', now()) ");
                output += " db created . ";
                return output;
            } catch(Exception e) {
                e.printStackTrace();
                return e.getLocalizedMessage();
            } finally {
                try { connection.close(); } catch(Exception e) {}
            }
        });
    }
}

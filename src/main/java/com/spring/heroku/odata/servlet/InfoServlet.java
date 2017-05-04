package com.spring.heroku.odata.servlet;

/**
 * Created by cshah on 4/25/2017.
 */

import com.spring.heroku.odata.DemoEdmProvider;
import com.spring.heroku.odata.DemoEntityCollectionProcessor;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet( name = "InfoServlet", urlPatterns = {"/info"}
)
public class InfoServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
        try {
            String output = " db info1 " + System.getenv("JDBC_DATABASE_URL") ;
            output += "\n java version : " + System.getProperty("java.version");
            output +=  "\n\n\n";
            Map<String, String> env = System.getenv();
            for (String envName : env.keySet()) {
                output += "\n env : " + envName + " : " + env.get(envName);
            }
            ServletOutputStream out = resp.getOutputStream();
            out.write(output.getBytes());
            out.flush();
            out.close();
        } catch (RuntimeException e) {
            System.err.println("Server Error occurred in InfoServlet");
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

}

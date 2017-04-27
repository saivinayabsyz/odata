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
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

@WebServlet( name = "ODataServlet", loadOnStartup=1, urlPatterns = {"/odata.svc/*"})
public class ODataServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException {
        try {
            req = new HttpServletRequestWrapper(req) {
                private Set<String> headerNameSet;
                @Override
                public Enumeration<String> getHeaderNames() {
                    if (headerNameSet == null) {
                        // first time this method is called, cache the wrapped request's header names:
                        headerNameSet = new HashSet<>();
                        Enumeration<String> wrappedHeaderNames = super.getHeaderNames();
                        while (wrappedHeaderNames.hasMoreElements()) {
                            String headerName = wrappedHeaderNames.nextElement();
                            if (!"accept".equalsIgnoreCase(headerName)) {
                                headerNameSet.add(headerName);
                            }
                        }
                    }
                    return Collections.enumeration(headerNameSet);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if ("accept".equalsIgnoreCase(name)) {
                        return Collections.<String>emptyEnumeration();
                    }
                    return super.getHeaders(name);
                }

                @Override
                public String getHeader(String name) {
                    if ("accept".equalsIgnoreCase(name)) {
                        return null;
                    }
                    return super.getHeader(name);
                }
            };
            OData odata = OData.newInstance();
            for( Object headerName : Collections.list( req.getHeaderNames() ) ) {
                System.out.println(" Header " + headerName + " -> " + req.getHeader(headerName.toString()) );
            }
            System.out.println(" ODataServlet.service 1 ");
            ServiceMetadata edm = odata.createServiceMetadata(new DemoEdmProvider(), new ArrayList<EdmxReference>());
            System.out.println(" ODataServlet.service 2 ");
            ODataHttpHandler handler = odata.createHandler(edm);
            System.out.println(" ODataServlet.service 3 ");
            EntityCollectionProcessor processor = new DemoEntityCollectionProcessor();
            System.out.println(" ODataServlet.service 4 ");
            handler.register(processor);
            System.out.println(" ODataServlet.service 5 ");
            handler.process(req, resp);
            System.out.println(" ODataServlet.service 6 ");
        } catch (RuntimeException e) {
            System.err.println("Server Error occurred in ODataServlet");
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}

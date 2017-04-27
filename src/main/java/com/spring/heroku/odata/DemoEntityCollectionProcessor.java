package com.spring.heroku.odata;

import org.apache.olingo.commons.api.data.*;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.List;

/**
 * Created by rr245546 on 4/25/2017.
 */
public class DemoEntityCollectionProcessor implements org.apache.olingo.server.api.processor.EntityCollectionProcessor {

    private OData odata;
    private ServiceMetadata serviceMetadata;

    @Override
    public void readEntityCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        try {
            System.out.println("DemoEntityCollectionProcessor.readEntityCollection : start ");
            System.out.println("DemoEntityCollectionProcessor.readEntityCollection : oDataRequest " + oDataRequest );
            System.out.println("DemoEntityCollectionProcessor.readEntityCollection : uriInfo " + uriInfo );
            System.out.println("DemoEntityCollectionProcessor.readEntityCollection : contentType " + contentType );
            System.out.println("DemoEntityCollectionProcessor.readEntityCollection : odata " + odata );
            // 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
            List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
            UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
            EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

            // 2nd: fetch the data from backend for this requested EntitySetName
            // it has to be delivered as EntitySet object
            EntityCollection entitySet = getData(edmEntitySet);

            if( odata == null ) {
                odata = OData.newInstance();
            }

            // 3rd: create a serializer based on the requested format (json)
            System.out.println("DemoEntityCollectionProcessor.readEntityCollection odata " + odata + " contentType " + contentType );
            //ODataSerializer serializer = odata.createSerializer(contentType);
            contentType = ContentType.APPLICATION_JSON;
            ODataSerializer serializer = odata.createSerializer(ContentType.APPLICATION_JSON);

            // 4th: Now serialize the content: transform from the EntitySet object to InputStream
            EdmEntityType edmEntityType = edmEntitySet.getEntityType();
            ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

            final String id = oDataRequest.getRawBaseUri() + "/" + edmEntitySet.getName();
            EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
            SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
            InputStream serializedContent = serializerResult.getContent();

            // Finally: configure the response object: set the body, headers and status code
            oDataResponse.setContent(serializedContent);
            oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
            oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void init(OData oData, ServiceMetadata serviceMetadata) {
        System.out.println("DemoEntityCollectionProcessor.init : start ");
        System.out.println("DemoEntityCollectionProcessor.init : oData " + oData);
        System.out.println("DemoEntityCollectionProcessor.init : serviceMetadata " + serviceMetadata );
        this.odata = odata;
        this.serviceMetadata = serviceMetadata;
    }


    private EntityCollection getData(EdmEntitySet edmEntitySet){
        Connection connection = null;
        EntityCollection entityCollection = new EntityCollection();
        // check for which EdmEntitySet the data is requested
        if("AllEmployees".equals(edmEntitySet.getName())) {
            try {
                List<Entity> entities = entityCollection.getEntities();
               connection = DriverManager.getConnection(System.getenv("JDBC_DATABASE_URL"));
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select * from employee ");
                while( resultSet != null && resultSet.next() ) {
                    final Entity entity = new Entity()
                            .addProperty(new Property(null, "id", ValueType.PRIMITIVE, resultSet.getInt("id")))
                            .addProperty(new Property(null, "fname", ValueType.PRIMITIVE, resultSet.getString("fName")))
                            .addProperty(new Property(null, "lname", ValueType.PRIMITIVE, resultSet.getString("lname")));
                    entity.setId(createId("AllEmployees", 1));
                    entities.add(entity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {  connection.close(); } catch(Exception e) {}
            }
        }

        return entityCollection;
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
        }
    }
}

package com.spring.heroku.odata;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by rr245546 on 4/24/2017.
 * edm : Entity Data Model
 *
 */
public class DemoEdmProvider extends org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider {

    // Service Namespace
    public static final String NAMESPACE = "ODataNS";
    public static List<String> schemaNames = Arrays.asList("HR", "Customer");
    public static List<String> entityTypeNames = Arrays.asList("Employee", "Address");
    public static final String containerName = "GenericContainer";     // seems like olango support only one container
    public static List<String> entitySetNames = Arrays.asList("AllEmployees", "AllAddresses", "GoodEmployees", "BadAddresses");
    public static final FullQualifiedName CONTAINER_NS = new FullQualifiedName(NAMESPACE, containerName);

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
        System.out.println("DemoEdmProvider.getEntityContainerInfo  start : entityContainerName " + entityContainerName );
        // This method is invoked when displaying the Service Document at e.g. http://localhost:8080/DemoService/DemoService.svc
        if (entityContainerName == null || entityContainerName.equals(CONTAINER_NS)) {
            CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
            entityContainerInfo.setContainerName(CONTAINER_NS);
            System.out.println("DemoEdmProvider.getEntityContainerInfo  end : entityContainerInfo " + entityContainerInfo );
           return entityContainerInfo;
        }
        return null;
    }

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        System.out.println("DemoEdmProvider.getSchemas ");
        List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
        {
            CsdlSchema schema = new CsdlSchema();
            schema.setNamespace(NAMESPACE);

            List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
            schema.setEntityTypes(entityTypes);
            for (String entityTypeName : entityTypeNames) {
                entityTypes.add(getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName)));
            }

            schema.setEntityContainer(getEntityContainer());
            schemas.add(schema);
        }

        return schemas;
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        System.out.println("DemoEdmProvider.getEntityType  entityTypeName " + entityTypeName );
        // this method is called for one of the EntityTypes that are configured in the Schema

        if(entityTypeName.getName().equals("Employee")){
            CsdlProperty id = new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty fname = new CsdlProperty().setName("fname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty  lname = new CsdlProperty().setName("lname").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("id");
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(entityTypeName.getName());
            entityType.setProperties(Arrays.asList(id, fname , lname));
            entityType.setKey(Collections.singletonList(propertyRef));
            return entityType;
        }

        if(entityTypeName.getName().equals("Address")){
            CsdlProperty id = new CsdlProperty().setName("id").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
            CsdlProperty addrline = new CsdlProperty().setName("addrline").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty  zipcode = new CsdlProperty().setName("zipcode").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlProperty  country = new CsdlProperty().setName("country").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
            CsdlPropertyRef propertyRef = new CsdlPropertyRef();
            propertyRef.setName("id");
            CsdlEntityType entityType = new CsdlEntityType();
            entityType.setName(entityTypeName.getName());
            entityType.setProperties(Arrays.asList(id, addrline , zipcode, country));
            entityType.setKey(Collections.singletonList(propertyRef));
            return entityType;
        }

        return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        System.out.println("DemoEdmProvider.getEntityContainer ");
        List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
        for(String entitySet : entitySetNames ) {
            entitySets.add(getEntitySet(CONTAINER_NS, entitySet));
        }
        CsdlEntityContainer entityContainer = new CsdlEntityContainer();
        entityContainer.setName(containerName);
        entityContainer.setEntitySets(entitySets);
        return entityContainer;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        System.out.println("DemoEdmProvider.getEntitySet  entityContainer " + entityContainer + " entitySetName "  + entitySetName );
        if(entityContainer.equals(CONTAINER_NS)){
            for(String definedEntitySetName : entitySetNames ) {
                if( definedEntitySetName.equals(entitySetName) ) {
                    if( definedEntitySetName == "AllEmployees") {
                        CsdlEntitySet entitySet = new CsdlEntitySet();
                        entitySet.setName(definedEntitySetName);
                        entitySet.setType( new FullQualifiedName(NAMESPACE, "Employee") );
                        return entitySet;
                    }
                    if( definedEntitySetName == "AllAddresses") {
                        CsdlEntitySet entitySet = new CsdlEntitySet();
                        entitySet.setName(definedEntitySetName);
                        entitySet.setType( new FullQualifiedName(NAMESPACE, "Address") );
                        return entitySet;
                    }
                    if( definedEntitySetName == "GoodEmployees") {
                        CsdlEntitySet entitySet = new CsdlEntitySet();
                        entitySet.setName(definedEntitySetName);
                        entitySet.setType( new FullQualifiedName(NAMESPACE, "Employee") );
                        return entitySet;
                    }
                    if( definedEntitySetName == "BadAddresses") {
                        CsdlEntitySet entitySet = new CsdlEntitySet();
                        entitySet.setName(definedEntitySetName);
                        entitySet.setType( new FullQualifiedName(NAMESPACE, "Address") );
                        return entitySet;
                    }
                }
            }
        }
        return null;
    }

}

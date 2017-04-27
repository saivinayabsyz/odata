InitialSetupReadme.txt file has initial setup

==============================================
How to run:
==============================================
1) mvn clean install
   builds jar file

2)
    // spark : not using spark anymore
   // heroku local web -p 8991  (this set PORT env variable)

   tomcat:
   target\bin\webapp.bat

==============================================
How it works: local
==============================================

1) .env files stores all variable which are avialble via System.getenv
** ONLY APPLIES TO LOCAL : heroku local web -p 9000 **

2) system.propties determines which version of JRE to use
*** ONLY APPLIES TO SERVER ***

3) pom.xml has build is plugin is used by heroku
   -> build will generate the odata.jar file
      -> this jar file has main class which is com.spring.heroku.odata.SparkMain
         -> procFile which is read by Heroku has entry to execute odata.jar file

         so heroku local or server
         reads Procfile -> reads Jar -> Jar has Main file which runs spark jetty

==============================================
How it works: server
==============================================

Just Commit. It picks up from Procfile


==============================================
Salesforce Configuration
==============================================

Create external data source with : http://odata-cshah.herokuapp.com/odata.svc
if you want to view all supported operations : http://odata-cshah.herokuapp.com/odata.svc/$metadata

Click on sync, and it will automatically create all the external objects with their fields.


==============================================
How it works: ODATA 4.0
==============================================
-root  (e.g. http://www.odata.org/getting-started/basic-tutorial/#modifyData)
http://services.odata.org/V4/Northwind/Northwind.svc/

-lists all services supported and their metadata, pick one and use next
http://services.odata.org/V4/Northwind/Northwind.svc/$metadata

    Note: same line, attribute, new line, element
    Note: We can have all entity container in one schema and entity type in other, and a lot more other combination.

    DataServices
        Schema (*) : Namespace
            EntityType(*) : Name
                Property (Name, Type)
                Key (PropertyRef)
            EntityContainer (*) : Name
                EntitySet(*) : Name, EntityType = "Schema.EntityType"


    In code we implement : DemoEdmProvider

    getEntityContainerInfo() : when we click on http://localhost:8080/odata.svc/   (root document)
    getSchemas() The Schema is the root element to carry the elements.
        getEntityType() Here we declare the EntityType “Product” and a few of its properties
        getEntityContainer() Here we provide a Container element that is necessary to host the EntitySet.
            getEntitySet() Here we state that the list of products can be called via the EntitySet “Products”


    http://localhost:8080/odata.svc/$metadata calls : schemas, entitytype for each, then entity container for each and entity set for each.


-lists results of Category_Sales_for_1997 (which is entity set name)
http://services.odata.org/V4/Northwind/Northwind.svc/Category_Sales_for_1997

- id would be @odata.id from the above request. Unfortunately it doesn't have odata id
http://services.odata.org/V4/Northwind/Northwind.svc/Category_Sales_for_1997(id)

- this just returns that field, could be complex which can walk through /
http://services.odata.org/V4/Northwind/Northwind.svc/Category_Sales_for_1997(id)/field1

- filter
http://services.odata.org/V4/Northwind/Northwind.svc/Category_Sales_for_1997?$filter=contains(CategoryName,'ts')
http://services.odata.org/V4/Northwind/Northwind.svc/Category_Sales_for_1997?$filter=CategoryName eq 'ts'
http://services.odata.org/V4/Northwind/Northwind.svc/Category_Sales_for_1997?$filter=CategoryName eq 'Condiments'

- $orderby=EndsAt desc

- ?$top=2

- $skip=18

- $count

- $expand=Friends (expands that relation)

- $select=Name, IcaoCode  (limited set of properties)

- $search=Boise  (depends on implementation)

-

branch1
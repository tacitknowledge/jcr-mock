JCR Mock
=====================

Intent
------

The intent of this framework is to eliminate boilerplate code for setting up JCR node structures in unit tests by
writing them in an easy to read format such as JSON.  For example:

1.  Say you're working on an application which uses a JCR repository for storing product content.

2.  One feature of this application may be related to building an API to search for products.

4.  In order to write unit tests for the JCR you'll need to setup some test data.  You can do this in 2 ways:
    - Use a [TransientRepository](http://jackrabbit.apache.org/api/2.2/org/apache/jackrabbit/core/TransientRepository.html)
      and populate it with some data.
    - Or you can use a mocking framework (such as mockito) to mock out the repository and the node structure.

5.  For this example we'll use mockito.

6.  Let's say that the node structure looks like the following:
```
    /products
      /awesome_shoe 
        - name 
        - description
        - price
      /awesome_shirt
        - name
        - description
        - price
      /awesome_jeans
        - name
        - description
        - price
```

7.  In order to mock a structure with 1 products with 1 digital asset in the repository we'd do something like this:

```java
    Node productBaseNode = mock(Node.class);
    Node awesomeShoeNode = mock(Node.class);
    Node awesomeShirtNode = mock(Node.class);
    Node awesomeJeansNode = mock(Node.class);

    NodeIterator nodeIterator = mock(NodeIterator.class);
    when(nodeIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
    when(nodeIterator.nextNode())
            .thenReturn(awesomeShoeNode);
            .thenReturn(awesomeShirtNode);
            .thenReturn(awesomeJeansNode);
    when(productBaseNode.getNodes()).thenReturn(nodeIterator);

    when(productBaseNode.getNode("awesome_shoe")).thenReturn(awesomeShoeNode);
    when(productBaseNode.getNode("awesome_shirt")).thenReturn(awesomeShirtNode);
    when(productBaseNode.getNode("awesome_jeans")).thenReturn(awesomeJeansNode);

    when(awesomeShoeNode.getProperty("name")).thenReturn("Awesome Shoe");
    when(awesomeShoeNode.getProperty("description")).thenReturn("Description for an awesome shoe");
    when(awesomeShoeNode.getProperty("price")).thenReturn("50.00");

    when(awesomeShirtNode.getProperty("name")).thenReturn("Awesome Shirt");
    when(awesomeShirtNode.getProperty("description")).thenReturn("Description for an awesome shirt");
    when(awesomeShirtNode.getProperty("price")).thenReturn("75.00");

    when(awesomeJeansNode.getProperty("name")).thenReturn("Awesome Jeans");
    when(awesomeJeansNode.getProperty("description")).thenReturn("Description for an awesome jeans");
    when(awesomeJeansNode.getProperty("price")).thenReturn("175.00");

    PropertyIterator propertyIterator = mock(PropertyIterator.class);
    when(propertyIterator.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
    when(propertyIterator.next()).thenReturn(viewProp);
    when(digitalAsset.getProperties()).thenReturn(propertyIterator);
```

8.  This is a lot of code just for setting up a relatively small tree.  Imagine if we'd needed to setup a much larger structure. It's just too much boilerplate code.  With this framework you can achieve the same thing by doing:
```
    String jsonNodeStructure = " { " +
        "products: {" +
          "awesome_shoe: {" +
             "name: 'Awesome Shoe', " +
             "description: 'Description for an awesome shoe', " +
             "price: '50.00' " +
          "},"
          "awesome_shirt: {" +
             "name: 'Awesome Shirt', " +
             "description: 'Description for an awesome shirt', " +
             "price: '75.00' " +
          "},"
          "awesome_jeans: {" +
             "name: 'Awesome Jeans', " +
             "description: 'Description for awesome jeans', " +
             "price: '175.00' " +
          "}"
        "}" +
    "}";
    Node productBaseNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeStructure);
```

9.  Read the [Usage](#usage) section for details on how to use the framework.

<a name="usage" />
Usage
------
*  You can include the framework as a dependency with _test_ scope in your pom.xml file:

```xml
    <dependency>
        <groupId>com.tacitknowledge</groupId>
        <artifactId>jcr-test</artifactId>
        <version>0.1-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
```

*  Then you can use the mock service on your tests:

```java    
    String myNodeStructure = "{myAsset : " +
                                    "{ trustEntity : " +
                                        "{" +
                                            "nodeType: 'nt:unstructured'," +
                                            " view:'left'," +
                                            " binary : " +
                                               "{ " +
                                                  "nodeType : 'nt:file'" +
                                               "}," +
                                            " anotherNode: " +
                                               "{ " +
                                                  "attri: 'valueyes'" +
                                                "} " +
                                        "}" +
                                     "} " +
                                "} ";
    Node myAssetNode = JcrMockingUtils.createNodesFromJsonString(myNodeStructure);
```

*  You can also load from a json file:

```java
    InputStream assetsJsonFile = getClass().getResourceAsStream("/assets.json");
    Node assetsNode = JcrMockingUtils.createNodesFromJsonFile(assetsJsonFile);
```

Supported Functionality
-----------------------

### JSON format Basics

*  JSON Objects correspond to JCR Nodes, where the name of the node is the name of the object.
*  JSON Properties correspond to JCR Properties.

### Node Types and Property Types

An important feature for this framework is the ability to create node structures based on node types, this allows to create
make our JSON much more concise.

*  JCR Node Types are supported by use of a the 'nodeType' special property keyword.  For example:
```
    myNode: {
        nodeType : 'nt:file'
    }
```

*  JSON Properties can contain the JCR Property Type information included in the value as well, for example:
```
    myImportantProperty : 'type:Date'
```

*  If the type information is included in the property, you can set the JCR Property value by using the 'value' special
   keyword:
```
    myImportantProperty : 'type:String, value:The value of the property'
```

*  If no type information is included in the property value, it will default as a String property.
*  If no property value information can be determined, the following happens:
    *  If it's a String, the default is an empty string "".
    *  If it's a number, date or binary, an exception is thrown.

### Special Property Types

*  Properties of type Binary are also supported, in which case it's value will be a path to a resource file:
```
    myFile : "type:Binary, value:/path/to/file.jpg"
```
*  If the path to the binary doesn't exist an exception will be thrown.
*  For Date properties the supported format is "MM/DD/YYYY":
```
    dateOfBirth : "type:Date, value:09/24/1982"
```

### Mocked Methods

*  _Node.getNode()_ with relative paths.  For example, this will work:

```java
    String jsonNodeStructure = "{" +
        "a: {" +
          "b: {" +
             "c: {" +
                "d : {" +
                    "my_property : 'top'" +
                "}" +
             "}" +
          "}" +
        "}" +
    "}";
    Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeStructure);
    Node c = rootNode.getNode("a/b/c");
    Node d = c.getNode("d");
```

*  _Node.getParent()_ will work all the way to the top:

```java
    String jsonNodeStructure = "{" +
        "a: {" +
          "b: {" +
             "c: {}" +
          "}" +
        "}" +
    "}";

    Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeStructure);
    Node c = rootNode.getNode("a/b/c");
    Node a = c.getParent().getParent();
```


TODOs
-----

*  Currently, only mocking of nodes is supported (via _MockNodeFactory_).  However, it's possible to extend the framework
   to support writing of nodes to a real repository (for example, a _TransientRepository_) by extending from
   _AbstractNodeFactory_ and implementing the required methods.

*  The framework requires of a _NodeTypeManager_ in order to determine a node structure based on a give node type
   (e.g. nt:file).  For convenience, we're instantiating a TransientRepository and obtaining the node type manager via the
   _session.getWorkspace().getNodeTypeManager()_ method.  However, the ideal would be that the framework does not depend
   on a _TransientRepository_.

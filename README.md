JCR Mock
=====================

Intent
------

The intent of this framework is to eliminate boilerplate code for setting up JCR node structures in unit tests by
writing JCR structures in an easy to read format such as JSON.  Take the following example.

1.  Say you're working on an application which uses a JCR repository for storing images.

2.  One feature of this application may be related to building an API to retrieve an asset from the repository based on
    a couple of parameters (e.g. a product id and an image attribute). Of course you'd write unit tests for this functionality.
    You might even want to develop you're code writing your tests first.

4.  In order to write unit tests for the JCR you'll need to setup some test data.  You can do this in 2 ways:
    - Use a [TransientRepository](http://jackrabbit.apache.org/api/2.2/org/apache/jackrabbit/core/TransientRepository.html)
      and populate it with some data.
    - Or you can use a mocking framework (such as mockito) to mock out the repository and the node structure.

5.  For this example we'll use mockito.

6.  Let's say that the node structure looks like the following:
        /var/dam/digitalassetassociatedentities
          /PRODID (as an example) --> 1234561 | 'null'
               /digitalassets
                    /DIGITALASSETID  --> 35466456
                         /authoring | repurposed format
                              /image
                                   /trustentity <<attribute, required>>
                                   /dataclassification <<attribute, default='bronze'>>
                                   /other attributes <<attribute>>
                                   /productcopy <<JCR node, optional>>
                                   /allowedentities <<path, optional>>
                                       /mmx (as an example) <<JCR node>>
                                       /gtm (as an example) <<JCR node>>

7.  In order to mock a structure with 1 products with 1 digital asset in the repository we'd do something like this:
        Node productBaseNode = mock(Node.class);
        Node product1 = mock(Node.class);

        NodeIterator nodeIterator = mock(NodeIterator.class);
        when(nodeIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(nodeIterator.nextNode())
                .thenReturn(product1););
        when(productBaseNode.getNodes()).thenReturn(nodeIterator);

        String productId1 = "myProduct";
        when(productBaseNode.getNode(productId1)).thenReturn(product1);

        Node digitalAssetsBaseNode = mock(Node.class);

        Node digitalAsset = mock(Node.class);
        Property viewProp = mock(Property.class);
        when(viewProp.getType()).thenReturn(PropertyType.STRING);
        when(viewProp.getName()).thenReturn(VIEW);
        when(viewProp.getString()).thenReturn("top");

        PropertyIterator propertyIterator = mock(PropertyIterator.class);
        when(propertyIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(propertyIterator.next()).thenReturn(viewProp);
        when(digitalAsset.getProperties()).thenReturn(propertyIterator);

        when(product1.getNode("digitalAssets").thenReturn(digitalAssetsBaseNode);
        NodeIterator digitalAssetNodeIterator = mock(NodeIterator.class);
        when(digitalAssetNodeIterator.hasNext()).thenReturn(true).thenReturn(false);
        when(digitalAssetNodeIterator.nextNode()).thenReturn(digitalAsset);

8.  This is a lot of code just for setting up 1 product.  Imagine if we'd need to setup 3 products each with 2 assets.
    It's just too much boilerplate code.  With this framework you can achieve the same thing by doing:
        String jsonNodeStructure = {
            productBaseNode: {
              myProduct: {
                 digitalAssets:
                    digitalAsset : {
                        VIEW : 'top'
                    }
                 }
              }
            }
        }
        Node productBaseNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeStructure);

    NOTE: For the sake of simplicity, the Json node structure above is lacking the quotes "", thus it will not compile AS-IS.

9.  Read the Usage section for details on how to use the framework.


Usage
------

*  You can include the framework as a dependency with _test_ scope in your pom.xml file:
        <dependency>
            <groupId>com.tacitknowledge</groupId>
            <artifactId>jcr-test</artifactId>
            <version>0.1-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>


*  Then you can use the mock service on your tests:
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

*  You can also load from a json file:
        InputStream assetsJsonFile = getClass().getResourceAsStream("/assets.json");
        Node assetsNode = JcrMockingUtils.createNodesFromJsonFile(assetsJsonFile);


Supported Functionality
-----------------------

### JSON format Basics

*  JSON Objects correspond to JCR Nodes, where the name of the node is the name of the object.
*  JSON Properties correspond to JCR Properties.

### Node Types and Property Types

An important feature for this framework is the ability to create node structures based on node types, this allows to create
make our JSON much more concise.

*  JCR Node Types are supported by use of a the 'nodeType' special property keyword.  For example:
        myNode: {
            nodeType : 'nt:file'
        }
*  JSON Properties can contain the JCR Property Type information included in the value as well, for example:
        myImportantProperty : 'type:Date'
*  If the type information is included in the property, you can set the JCR Property value by using the 'value' special
   keyword:
        myImportantProperty : 'type:String, value:The value of the property'
*  If no type information is included in the property value, it will default as a String property.
*  If no property value information can be determined, the following happens:
    *  If it's a String, the default is an empty string "".
    *  If it's a number, date or binary, an exception is thrown.

### Special Property Types

*  Properties of type Binary are also supported, in which case it's value will be a path to a resource file:
        myFile : "type:Binary, value:/path/to/file.jpg"
*  If the path to the binary doesn't exist an exception will be thrown.
*  For Date properties the supported format is "MM/DD/YYYY":
        dateOfBirth : "type:Date, value:09/24/1982"

### Mocked Methods

*  Node.getNode() with relative paths.  For example, this will work:
        String jsonNodeStructure = {
            a: {
              b: {
                 c: {
                    d : {
                        VIEW : 'top'
                    }
                 }
              }
            }
        }
        Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeStructure);
        Node c = rootNode.getNode("a/b/c");
        Node d = c.getNode("d");

*  Node.getParent() will work all the way to the top:
        String jsonNodeStructure = {
            a: {
              b: {
                 c: {
                    }
                 }
              }
            }
        }
        Node rootNode = JcrMockingUtils.createNodesFromJsonString(jsonNodeStructure);
        Node c = rootNode.getNode("a/b/c");
        Node b = c.getParent();


TODOs
-----

*  Currently, only mocking of nodes is supported (via MockNodeFactory).  However, it's possible to extend the framework
   to support writing of nodes to a real repository (for example, a TransientRepository) by extending from
   AbstractNodeFactory and implementing the required methods.

*  The framework requires of a NodeTypeManager in order to determine a node structure based on a give node type
   (e.g. nt:file).  For convenience, we're instantiating a TransientRepository and obtaining the node type manager via the
   session.getWorkspace().getNodeTypeManager() method.  However, the ideal would be that the framework does not depend
   on a TransientRepository.

*  Instead of throwing exceptions for Number, Date and Binary empty properties we could set sensible defaults.

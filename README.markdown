EaDiagramGenerator - a tool for exporting diagrams from Sparx Enterprise Architect
==================================================================================

This prototype software can be used to extract the UML models from the
[Sparx Enterprise Architect](http://www.sparxsystems.com/products/ea/index.html) UML diagramming tool via it's Java
API (`eaapi.jar`). We developed the software as part of an enterprise architecture documentation project where we 
wanted to combine written documentation in a [Confluence](http://www.atlassian.com/software/confluence) wiki with
architecture models in Enterprise Architect. Via the EaDiagramGenerator software we were able to:

- Extract diagrams for inclusion in Confluence wiki pages
- Give the diagrams fixed, and logical file names based on their name in the UML model
- Embed the UML models in wiki-pages in an effective manner; when model diagrams where updated in
 Enterprise Architect we would use the a command line script to automatically update the diagrams in the wiki pages.

A lightning talk about this work was given at the [JavaZone](www.javazone.no) conference in 2009. The slides from the
talk are found here: [Agile documentation with Confluence and Sparx Enterprise Architect](http://www.slideshare.net/pspilling/agile-documentation-with-confluence-and-ea).
Take a look at the slides for more details about how we used used EaDiagramGenerator to integrate Confluence and
Enterprise Architect.

We are not planning to maintain or do more development on this code, but anyone who finds the code useful is welcome to
do whatever they want with it. 

How to build and run EaDiagramGenerator for generating diagrams
---------------------------------------------------------------

### Prerequisites

* JDK 1.6 & Maven 2 installed
* [Sparx Enterprise Architect](http://www.sparxsystems.com/products/ea/index.html) installed. *Note: Sparx Enterprise
Architect only runs on Windows.*

### Installation

1. Install the Enterprise Architect API jar file, which can be found in the `Java API` directory in the EA
installation. The standard location for this is: `C:\Program Files\Sparx Systems\EA\Java API`. Do the following:

        $ cd <the directory where the **eaapi.jar** is located>
        $ mvn install:install-file -DgroupId=org.sparx -DartifactId=eaapi -Dversion=1.0.0 -Dpackaging=jar -Dfile=eaapi.jar

2. Copy the **SSJavaCOM.dll** (located in the same directory as eaapi.jar) to the **windows\system32** directory

3. cd back to the directory with the EAtools project, and compile the :

        $ mvn clean install

4. Build the diagramgen.jar file:

        $ mvn package -Dmaven.test.skip=true -DmainClass=no.eatools.diagramgen.EaDiagramGenerator -DjarFileName=diagramgen

### Usage

Create a properties file with details about the EA repo and UML model that you want to generate diagrams for. An
example file is provided (`ea.application.properties`). Update the property file with your data, and run the diagram
generator as follows:

        $ java -jar diagramgen.jar <properties file>


### Links to resources

- [Sparx EA SDK Reference](http://www.sparxsystems.com/uml_tool_guide/sdk_for_enterprise_architect/reference.htm)
- [Sparx EA SDK Code Samples](http://www.sparxsystems.com/uml_tool_guide/sdk_for_enterprise_architect/codesamples.htm)


<pre>
--
Ove Scheel and Per Spilling (per@kodemaker.no), nov-des 2008

*******************************************************************************
* DISCLAIMER:
*
* The code is written as part of a Proof of Concept. This implies that the code
* may not be up to production quality standards with regards to:
* - structure
* - naming
* - documentation
* - test coverage
* - robustness
* - etc.
*******************************************************************************
</pre>

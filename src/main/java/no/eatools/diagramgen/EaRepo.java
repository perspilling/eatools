package no.eatools.diagramgen;

import org.apache.log4j.Logger;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.Datatype;
import org.sparx.Diagram;
import org.sparx.DiagramObject;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Project;
import org.sparx.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.eatools.util.EaApplicationProperties;


/**
 * Utilities for use with the EA (Enterprise Architect DLL).
 * todo reconsider transactional model, i.e. how/when to open/close the Repos.
 * <p/>
 * Note that the terminology in these methods refer to the corresponding UML elements.
 * E.g. getClassesInPackage means "find and return all elements of UML-type Class in the UML Package".
 * <p/>
 * The class assume that no two elements of same UML type (e.g. Class, Component) and same name may exist
 * in the same namespace (Package).
 * <p/>
 * Date: 21.okt.2008
 *
 * @author AB22273 et al.
 */
public class EaRepo {
    /* Boolean flags that can be used as input params */
    public static final boolean RECURSIVE = true;
    public static final boolean NON_RECURSIVE = false;

    private static final Logger log = Logger.getLogger(EaRepo.class);

    /* Name of the UML stereotype for an XSDschema package */
    private static final String xsdSchemaStereotype = "XSDschema";

    /* The character encoding to use for XSD generation */
    private static final String xmlEncoding = "UTF-8";
    private File reposFile;
    private Repository repository = null;
    private boolean isOpen = false;


    /**
     * @param repositoryFile local file or database connection string
     */
    public EaRepo(File repositoryFile) {
        reposFile = repositoryFile;
    }

    /**
     * Open the Enterprise Architect model repository.
     */
    public void open() {
        if (isOpen) {
            return;
        }

        log.info("Opening model repository: " + reposFile.getAbsolutePath());
        repository = new Repository();
        repository.OpenFile(reposFile.getAbsolutePath());
        isOpen = true;
    }

    /**
     * Alternative name for better code readability internally in this class
     */
    private void ensureRepoIsOpen() {
        open();
    }

    /**
     * Closes the Enterprise Architect model repository.
     */
    public void close() {
        log.info("Closing repository: " + reposFile.getAbsolutePath());
        repository.CloseFile();
        repository.Exit();
        isOpen = false;
    }

    /**
     * Looks for a subpackage with a given unqualified name whithin a given EA package. The
     * search is case sensitive. The first matching package ir returned performing a breadth-first search.
     * If more than one package with the same unqualified name exists within the repos, the result may
     * be ambiguous.
     *
     * @param theName   The unqualified package name to look for
     * @param rootPkg   The EA model root package to search whithin
     * @param recursive Set to true to do a recursive search in package hierarchy,
     *                  false to do a flat search at current level only
     * @return The Package object in the EA model, or null if package was not found.
     */
    public Package findPackageByName(final String theName, Package rootPkg, boolean recursive) {
        ensureRepoIsOpen();

        if (rootPkg == null) {
            return rootPkg;
        }

        Package nextPkg;

        for (Package pkg : rootPkg.GetPackages()) {
            if (pkg.GetName().equals(theName)) {
                return pkg;
            }

            if (recursive) {
                nextPkg = findPackageByName(theName, pkg, recursive);

                if (nextPkg != null) {
                    // Found it
                    return nextPkg;
                }
            }
        }

        // No match
        return null;
    }

    /**
     * Looks for a subpackage with a given unqualified name whithin a given EA package. The
     * search is case sensitive. The first matching package ir returned performing a breadth-first search.
     * If more than one package with the same unqualified name exists within the repos, the result may
     * be ambiguous. The search is always performed from the root of the repos.
     *
     * @param theName   The unqualified package name to look for
     * @param recursive Set to true to do a recursive search in package hierarchy,
     *                  false to do a flat search at current level only
     * @return The Package object in the EA model, or null if package was not found.
     */
    public Package findPackageByName(String theName, boolean recursive) {
        return findPackageByName(theName, getRootPackage(), recursive);
    }

    public Package findPackageByID(int packageID) {
        if (packageID == 0) {
            // id=0 means this is the root
            return null;
        }
        ensureRepoIsOpen();
        return repository.GetPackageByID(packageID);
    }

    /**
     * Find the top level (aka root) package in a given repository.
     * todo check for NPEs.
     *
     * @return the root package or possibly null if there are no root package in the repository.
     *         This is normally the "Views" package or the "Model" package, but it may have an arbitrary name.
     */
    public Package getRootPackage() {
        ensureRepoIsOpen();
        String rootPkgName = EaApplicationProperties.EA_ROOTPKG.value();
        System.out.println("root package name = " + rootPkgName);
        for (Package aPackage : repository.GetModels()) {
            if (aPackage.GetName().equalsIgnoreCase(rootPkgName)) {
                log.debug("Found top level package: " + aPackage.GetName());
                return aPackage;
            }
        }
        throw new RuntimeException("Root pkg '" + rootPkgName + "' not found");
    }

    public Project getProject() {
        ensureRepoIsOpen();
        return repository.GetProjectInterface();
    }

    /**
     * Generates XSD schema file for the package if its UML stereotye is <<XSDschema>>,
     * otherwise a subdirectory corresponding to the UML package is created in
     * directory and the method is called reciursively for all its subpackages.
     *
     * @param directory The file system directory for generation
     * @param pkg       The EA model package to process
     * @param eaProj    Reference to the EA Model project in the repository
     */
    public void generateXSD(File directory, Package pkg, Project eaProj, String fileSeparator) {
        ensureRepoIsOpen();

        String stereotype = pkg.GetStereotypeEx();
        String pkgString = pkg.GetName();

        if (stereotype.equals(xsdSchemaStereotype)) {
            log.info("Generate XSD for package " + pkgString);
            eaProj.GenerateXSD(pkg.GetPackageGUID(), directory.getAbsolutePath() + fileSeparator + pkgString + ".xsd", xmlEncoding, null);
        } else {
            // Create subdirectory in generation directory
            File f = new File(directory, pkgString);

            if (f.mkdirs()) {
                log.debug("New subdir at: " + f.getAbsolutePath());
            }

            // Loop through all subpackages in EA model pkg
            for (Package aPackage : pkg.GetPackages()) {
                generateXSD(f, aPackage, eaProj, fileSeparator);
            }
        }
    }

    /**
     * Find UML Component elements inside a specified UML Package.
     * Non-recursive, searches the top-level (given) package only.
     *
     * @param pack
     * @return
     */
    public List<Element> findComponentsInPackage(Package pack) {
        return findElementsOfTypeInPackage(pack, EaMetaType.COMPONENT);
    }

    /**
     * Find UML Node elements inside a specific UML Package.
     * Non-recursive, searches the top-level (given) package only.
     *
     * @param pack
     * @return
     */
    public List<Element> findNodesInPackage(Package pack) {
        return findElementsOfTypeInPackage(pack, EaMetaType.NODE);
    }

    /**
     * Find UML Class elements inside a specific UML Package.
     * Non-recursive, searches the top-level (given) package only.
     *
     * @param pack the Package to serach in.
     * @return
     */
    public List<Element> findClassesInPackage(Package pack) {
        return findElementsOfTypeInPackage(pack, EaMetaType.CLASS);
    }

    /**
     * Find UML Object elemenst inside a specific UML Package.
     * Non-recursive, searches the top-level (given) package only.
     *
     * @param pack the Package to serach in.
     * @return
     */
    public List<Element> findObjectsInPackage(Package pack) {
        return findElementsOfTypeInPackage(pack, EaMetaType.OBJECT);
    }

    /**
     * Find UML Object elemenst inside a specific UML Package.
     * Non-recursive, searches the top-level (given) package only.
     *
     * @param pack the Package to serach in.
     * @return
     */
    public List<Element> findComponentInstancesInPackage(Package pack) {
        List<Element> theComponents = findElementsOfTypeInPackage(pack, EaMetaType.COMPONENT);
        List<Element> componentInstances = new ArrayList<Element>();
        for (Element component : theComponents) {
            if (EaMetaType.COMPONENT.toString().equals(component.GetClassifierType())) {
                componentInstances.add(component);
            }
        }
        return componentInstances;
    }

    /**
     * @param pack
     * @param objectName
     * @param classifier
     * @return
     */
    public Element findOrCreateObjectInPackage(Package pack, String objectName, Element classifier) {
        ensureRepoIsOpen();

        // We allow for same name on different elements of different type, therfore
        // must also check type
        for (Element element : findObjectsInPackage(pack)) {
            if (element.GetName().equals(objectName)) {
                if (isOfType(element, classifier)) {
                    return element;
                }
            }
        }
        return addElementInPackage(pack, objectName, EaMetaType.OBJECT, classifier);
    }

    public Element findOrCreateComponentInstanceInPackage(Package pack, String name, Element classifier) {
        Element component = findOrCreateComponentInPackage(pack, name);
        if (classifier != null) {
            component.SetClassifierID(classifier.GetElementID());
            component.Update();
            component.Refresh();
        }
        return component;
    }

    private Element addElementInPackage(Package pack, String name, EaMetaType umlType, Element classifier) {
        ensureRepoIsOpen();

        Element element = pack.GetElements().AddNew(name, umlType.toString());
        pack.GetElements().Refresh();

        if (classifier != null) {
            element.SetClassifierID(classifier.GetElementID());
            element.SetClassifierName(classifier.GetName());
        }

        element.Update();
        pack.Update();
        pack.GetElements().Refresh();
        return element;
    }

    public boolean isOfType(Element theObject, Element classifier) {
        int classifierId = theObject.GetClassifierID();
        if (classifier == null) {
            return (classifierId == 0);
        }

        return (classifier.GetElementID() == classifierId);
    }

    private Element findNamedElementOnList(List<Element> elementList, String elementName) {
        ensureRepoIsOpen();

        for (Element element : elementList) {
            if (element.GetName().equals(elementName)) {
                return element;
            }
        }

        return null;
    }

    /**
     * Assemble a List of all Model elements of a certain EaMetaType in the given package.
     * Sub-packages are not examined (non-recursive).
     *
     * @param pkg  the Package to look in.
     * @param type the type of Element to look for.
     * @return a List of found Elements, possibly empty, but never null.
     */
    public List<Element> findElementsOfTypeInPackage(Package pkg, EaMetaType type) {
        ensureRepoIsOpen();

        if (pkg == null) {
            return Collections.EMPTY_LIST;
        }

        type = (type == null) ? EaMetaType.NULL : type;

        List<Element> result = new ArrayList<Element>();

        for (Element e : pkg.GetElements()) {
            if (type.toString().equals(e.GetMetaType())) {
                result.add(e);
            }
        }

        return result;
    }

    /**
     * Find an element of a specific EaMetaType with a given name.
     *
     * @param pack
     * @param type
     * @param name
     * @return null if no match is found.
     */
    public Element findElementOfType(Package pack, EaMetaType type, String name) {
        ensureRepoIsOpen();
        name = name.trim();

        List<Element> existingElements = findElementsOfTypeInPackage(pack, type);

        for (Element element : existingElements) {
            if (element.GetName().equals(name)) {
                return element;
            }
        }

        return null;
    }

    /**
     * todo do we need this, then code it right...
     *
     * @param object
     * @param name
     * @param value
     */
    public void setAttributeValue(Element object, String name, String value) {
        ensureRepoIsOpen();
        // @VAR;Variable=name;Value=mittNavnPaaObjekt;Op==;@ENDVAR;@VAR;Variable=attribEn;Value=enverdi;Op==;@ENDVAR;
        object.SetRunState("@VAR;Variable=name;Value=dittnavn;Op==;@ENDVAR;");
        object.Update();
    }

    /**
     * @param namespaceURI
     * @return
     */
    public Package findOrCreatePackageFromNamespace(String namespaceURI) {
        ensureRepoIsOpen();
        log.debug("Looking for package with namespace:" + namespaceURI);

        // todo implement
        return findPackageByName("Klasser", true);
    }

    /**
     * @param definedPackage
     * @param className
     * @return
     */
    public Element findOrCreateClassInPackage(Package definedPackage, String className) {
        ensureRepoIsOpen();

        Element theClass = findNamedElementOnList(findClassesInPackage(definedPackage), className);

        if (theClass != null) {
            return theClass;
        }

        theClass = definedPackage.GetElements().AddNew(className, EaMetaType.CLASS.toString());
        theClass.Update();
        definedPackage.Update();

        return theClass;
    }

    /**
     * @param definedPackage
     * @param componentName
     * @return
     */
    public Element findOrCreateComponentInPackage(Package definedPackage, String componentName) {
        ensureRepoIsOpen();

        Element theComponent = findNamedElementOnList(findComponentsInPackage(definedPackage), componentName);

        if (theComponent != null) {
            return theComponent;
        }

        return addElementInPackage(definedPackage, componentName, EaMetaType.COMPONENT, null);
    }

    public boolean deleteObjectInPackage(Package pkg, String objectName, Element classifier) {
        if (pkg == null) {
            return false;
        }
        short index = 0;
        short indexToDelete = -1;
        for (Element element : pkg.GetElements()) {
            if (element.GetName().equals(objectName)) {
                if ((classifier == null) || (classifier.GetElementID() == element.GetClassifierID())) {
                    indexToDelete = index;
                }
            }
            ++index;
        }
        if (indexToDelete != -1) {
            pkg.GetElements().Delete(indexToDelete);
            pkg.Update();
            pkg.GetElements().Refresh();
            return true;
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    /**
     * todo move to EaDiagram class...
     *
     * @param pkg the Package to create the Diagram in.
     * @param name name of the Diagram, if null, the Diagram will have the same name as the Package.
     * @param type the type of UML Diagram to look for or create.
     * @return the Diagram created or found.
     */
    public Diagram findOrCreateDiagramInPackage(Package pkg, String name, EaDiagramType type) {
        if (name == null) {
            name = pkg.GetName();
        }
        for (Diagram d : pkg.GetDiagrams()) {
            if (d.GetName().equals(name) && (type.toString().equals(d.GetType()))) {
                return d;
            }
        }
        Diagram newDiagram = pkg.GetDiagrams().AddNew(name, type.toString());
        pkg.GetDiagrams().Refresh();
        newDiagram.Update();
        pkg.Update();

        return newDiagram;
    }

    public Package findOrCreatePackage(Package parent, String name, boolean recursive) {
        ensureRepoIsOpen();
        Package pkg = findPackageByName(name, parent, recursive);
        if (pkg != null) {
            return pkg;
        }
        pkg = parent.GetPackages().AddNew(name, EaMetaType.PACKAGE.toString());
        pkg.Update();
        parent.Update();
        parent.GetPackages().Refresh();
        parent.GetPackages().Refresh();

        return pkg;
    }

    public Package findOrCreatePackage(Package parent, String name) {
        return findOrCreatePackage(parent, name, EaRepo.NON_RECURSIVE);
    }

    public Connector findOrCreateAssociation(Element from, Element to, String name) {
        // todo check for existence
//        from.GetConnectors().AddNew(name, )
//        for (Connector c : from.GetConnectors()) {
//            if (c.GetSupplierEnd())
//        }
        return null;
    }

    /**
     * @param from the source/originator of the link, aka the "Supplier" in EA terms.
     * @param to   the target/destination of the link, aka the "Client" in EA terms.
     * @param name name of the link. Used to look up already existing links.
     * @return
     */
    public Connector findOrCreateLink(Element from, Element to, String name) {
        // check for existence
        for (Connector c : to.GetConnectors()) {
            if (c.GetName().equals(name)) {
                if ((c.GetSupplierID() == to.GetElementID()) && (c.GetClientID() == from.GetElementID())) {
                    return c;
                }
            }
        }

        Connector c = to.GetConnectors().AddNew(name, EaMetaType.ASSOCIATION.toString());
        c.SetSupplierID(to.GetElementID());
        if (!c.Update()) {
            log.error("Unable to update connector to: " + to.GetName());
            return null;
        }
        to.GetConnectors().Refresh();

        c.SetClientID(from.GetElementID());
        if (!c.Update()) {
            log.error("Unable to update connector from: " + from.GetName());
            return null;
        }
        from.GetConnectors().Refresh();

        c.SetDirection(EaLinkDirection.SOURCE_DESTINATION.toString());
        c.Update();

        from.Update();
        to.Update();
        return c;
    }

    /**
     * Just an early testmethod to display the internal EA datatypes
     *
     * @return
     */
    public String getEaDataTypes() {
        ensureRepoIsOpen();

//        Collection<Author> authors = repository.GetAuthors();
//        for (Author a : authors) {
//            System.out.println(a.GetName());
//        }

        StringBuilder sb = new StringBuilder();
        Collection<Datatype> dataTypes = repository.GetDatatypes();
        for (Datatype dt : dataTypes) {
            sb.append(dt.GetName()).append(", ");
        }
        return sb.toString();
    }

    public DiagramObject findOrCreateDiagramObject(Package pkg, Diagram diagram, Element reposElement) {
        for (DiagramObject dObject : diagram.GetDiagramObjects()) {
            if (dObject.GetElementID() == reposElement.GetElementID()) {
                return dObject;
            }
        }
        DiagramObject diagramObject = diagram.GetDiagramObjects().AddNew("","");
        diagramObject.SetInstanceID(reposElement.GetElementID());
        diagramObject.SetElementID(reposElement.GetElementID());
        diagramObject.Update();
        diagram.Update();
        pkg.GetDiagrams().Refresh();
        pkg.Update();
        return diagramObject;
    }
}

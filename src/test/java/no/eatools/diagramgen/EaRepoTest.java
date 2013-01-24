package no.eatools.diagramgen;
/**
 *
 * @author AB22273
 * @since 23.okt.2008 10:55:06
 * @date 23.okt.2008
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sparx.Attribute;
import org.sparx.Collection;
import org.sparx.Diagram;
import org.sparx.Element;
import org.sparx.ObjectType;
import org.sparx.Package;
import org.sparx.TaggedValue;

import java.util.Date;
import java.util.List;

public class EaRepoTest extends AbtractEaTestCase {
    private static final Log log = LogFactory.getLog(EaRepoTest.class);

    public void testOpenRepository() throws Exception {
        eaRepo.open();
        eaRepo.close();
    }

    public void testGetRootPackage() throws Exception {
        Package rootPkg = eaRepo.getRootPackage();
        assertNotNull(rootPkg);
        assertEquals("Model", rootPkg.GetName());
        log.info("Root Package Name: " + rootPkg.GetName());
    }

    public void testFindPackageByName() throws Exception {
        String packName = "Komponenter";
        final Package rootPkg = eaRepo.getRootPackage();

        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        assertNotNull(thePkg);
        assertEquals(packName, thePkg.GetName());

        thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.NON_RECURSIVE);
        assertNull(thePkg);

        packName = "Objekter";
        thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        assertNotNull(thePkg);
        assertEquals(packName, thePkg.GetName());

        thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.NON_RECURSIVE);
        assertNull(thePkg);

        // The root package itself is not considered as a match
        thePkg = eaRepo.findPackageByName(rootPkg.GetName(), rootPkg, EaRepo.NON_RECURSIVE);
        assertNull(thePkg);

        thePkg = eaRepo.findPackageByName(rootPkg.GetName(), rootPkg, EaRepo.RECURSIVE);
        assertNull(thePkg);
    }

    public void testFindKomponenterInPackage() throws Exception {
        String packName = "Komponenter";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        List<Element> components = eaRepo.findComponentsInPackage(thePkg);
        for (Element e : components) {
            assertEquals(EaMetaType.COMPONENT.toString(), e.GetMetaType());
            log.info("Component name: " + e.GetName());
            Collection<TaggedValue> tvs = e.GetTaggedValues();
            for (TaggedValue tv : tvs) {
                log.info(tv.GetName() + ":" + tv.GetValue());
            }
        }
    }

    public void testSetTaggedValues() throws Exception {
        String packName = "Komponenter";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        List<Element> components = eaRepo.findComponentsInPackage(thePkg);
        for (Element e : components) {
            System.out.println("Element name: " + e.GetName());
            assertEquals(EaMetaType.COMPONENT.toString(), e.GetMetaType());
            e.SetAuthor("Ove");
            // Tag i denoted "Keyword" in the EA-model
            e.SetTag("aTag=smthg");
//            e.GetEmbeddedElements();
            Collection<TaggedValue> tvs = e.GetTaggedValues();
            TaggedValue tv = tvs.GetByName("Ove");
            if (tv != null) {
                tv.SetValue("kul@" + new Date().toString());
            } else {
                tv = e.GetTaggedValues().AddNew("Ove", "kulere");
            }
            tv.Update();    // This is required.
            e.Update();
        }
    }

    public void testFindClassesInPackage() throws Exception {
        String packName = "System";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        List<Element> classes = eaRepo.findClassesInPackage(thePkg);
        for (Element e : classes) {
            log.info("Class name: " + e.GetName());
            assertEquals(EaMetaType.CLASS.toString(), e.GetMetaType());
        }
    }

    public void testAddElementToPackage() throws Exception {
        String packName = "Klasser";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        Collection<Element> theElements = thePkg.GetElements();
        Element newClass = theElements.AddNew("EnNyKlasse", EaMetaType.CLASS.toString());
        assertEquals(ObjectType.otElement, newClass.GetObjectType());
        assertEquals(newClass.GetClassfierID(), newClass.GetClassifierID()); // subtle...
        log.info(newClass.GetMetaType());

        Attribute newAtt = newClass.GetAttributes().AddNew("name", "string");
        assertEquals("name", newAtt.GetName());
        log.info(newAtt.GetObjectType().toString());
        newAtt.Update();
        newClass.Update();

        theElements.Refresh();
        thePkg.Update();


        Element newObject = theElements.AddNew("EtObject", EaMetaType.OBJECT.toString());

        log.info(newObject.GetMetaType());
        newObject.GetAttributes().AddNew("name", "String");
        newObject.Update();
        newObject.SetRunState("name=petter");

        theElements.Refresh();
        thePkg.Update();
    }

    public void testAddObjectOfClass() throws Exception {
        final String packName = "Klasser";
        Package thePkg = eaRepo.findPackageByName(packName, EaRepo.RECURSIVE);

        Collection<Element> theElements = thePkg.GetElements();
        assertNotNull(theElements);
        Element myClass = null;
        for (Element element : theElements) {
            if (element.GetName().equals("MinKlasse")) {
                myClass = element;
            }
        }
        assertNotNull(myClass);

        assertEquals(ObjectType.otElement, myClass.GetObjectType());
        log.info(myClass.GetClassifierID() + ":" + myClass.GetClassfierID());
        log.info(myClass.GetMetaType());

        log.info(myClass.GetElementID());

        Element newObject = theElements.AddNew("EtObject", EaMetaType.OBJECT.toString());

        log.info(newObject.GetMetaType());
        newObject.SetClassifierID(myClass.GetElementID());
        newObject.Update();

//        newObject.GetAttributes().AddNew("name", "String");
        newObject.SetRunState("navn=petter");
        newObject.Update();
        log.info(newObject.GetRunState());
        newObject.Update();

        theElements.Refresh();
        thePkg.Update();
    }

    public void testGetObjectRunState() {
        String packName = "Klasser";
        Package thePkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);

        Collection<Element> theElements = thePkg.GetElements();
        assertNotNull(theElements);
        Element myClass = null;
        for (Element element : theElements) {
            if (element.GetName().equals("MyClass")) {
                myClass = element;
            }
        }
    }

    public void testFindOrCreateObjectInPackage() throws Exception {
        String packName = "Klasser";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        Element myObject = eaRepo.findOrCreateObjectInPackage(pkg, "mittObjekt", null);
        assertNotNull(myObject);
        assertEquals("mittObjekt", myObject.GetName());
        assertEquals("", myObject.GetClassifierName());

        pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        Element mQueueClass = eaRepo.findOrCreateClassInPackage(pkg, "MQQueue");
        assertNotNull(mQueueClass);
        myObject = eaRepo.findOrCreateObjectInPackage(pkg, "enKo", mQueueClass);
        assertNotNull(myObject);
        assertEquals(mQueueClass.GetElementID(), myObject.GetClassifierID());

        // This fails, even though I set the ClassifierName explicitly when creating objects,
        // it is blank when retreieved later.
        // assertEquals(mQueueClass.GetName(), myObject.GetClassifierName());

        // Do not create another but retrieve the object just created
        Element sameObject = eaRepo.findOrCreateObjectInPackage(pkg, "enKo", mQueueClass);
        assertEquals(myObject.GetElementID(), sameObject.GetElementID());
        log.info(myObject.GetRunState());
    }


    public void testSetAttributeValue() throws Exception {
        String packName = "Klasser";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        boolean wasDeleted = eaRepo.deleteObjectInPackage(pkg, "mittObjekt", null);
        Element myObject = eaRepo.findOrCreateObjectInPackage(pkg, "mittObjekt", null);
        assertEquals("mittObjekt", myObject.GetName());

        String runStateBefore = myObject.GetRunState();
        log.debug("RunState before: " + runStateBefore);
        eaRepo.setAttributeValue(myObject, "navn", "noe helt annet");

        String runStateAfter = myObject.GetRunState();
        log.debug("RunStateAfter: " + runStateAfter);
        assertFalse(runStateBefore.equals(runStateAfter));
        log.info(runStateAfter);
    }

    public void testFindElementOfType() throws Exception {
        String packName = "Klasser";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        String className = "MQQueue";
        Element theClass = eaRepo.findElementOfType(pkg, EaMetaType.CLASS, className);
        assertNotNull(theClass);
        assertEquals(className, theClass.GetName());
    }

    /**
     * Assert that the index concept in EA is continuous and monotonous
     *
     * @throws Exception
     */
    public void testIndexInCollection() throws Exception {
        String packName = "Klasser";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        assertNotNull(pkg);
        short i = 0;
        for (Element element : pkg.GetElements()) {
            assertEquals(element.GetName(), pkg.GetElements().GetAt(i++).GetName());
        }

        String objectName = "uniqueObject";
        Element anObject = eaRepo.findOrCreateObjectInPackage(pkg, objectName, null);
        assertNotNull(anObject);
        short indexOfAnObject = -1;
        i = 0;
        for (Element element : pkg.GetElements()) {
            if (element.GetName().equals(objectName)) {
                indexOfAnObject = i;
            }
            assertEquals(element.GetName(), pkg.GetElements().GetAt(i++).GetName());
        }
        assertTrue(indexOfAnObject != -1);
        pkg.GetElements().Delete(indexOfAnObject);
        pkg.Update();
        pkg.GetElement().Refresh();

        i = 0;
        // pkg object is no longer valid after Update !!
        pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        for (Element element : pkg.GetElements()) {
            assertFalse("Expected " + element.GetName() + " to be deleted.", element.GetName().equals(objectName));
            assertEquals(element.GetName(), pkg.GetElements().GetAt(i++).GetName());
        }
        return;
    }

    public void testDeleteObjectInPackage() throws Exception {
        String packName = "Klasser";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        assertNotNull(pkg);
        String objectName = "uniqueObject";
        Element anObject = eaRepo.findOrCreateObjectInPackage(pkg, objectName, null);
        assertNotNull(anObject);

        assertTrue(eaRepo.deleteObjectInPackage(pkg, objectName, null));

        Element classifier = eaRepo.findOrCreateClassInPackage(pkg, "AClass");
        anObject = eaRepo.findOrCreateObjectInPackage(pkg, objectName, classifier);
        assertNotNull(anObject);
        assertFalse(eaRepo.deleteObjectInPackage(pkg, objectName, anObject));
        assertTrue(eaRepo.deleteObjectInPackage(pkg, objectName, classifier));
    }

    public void testIsOfType() throws Exception {
        String packName = "Klasser";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);

        Element classifier = eaRepo.findOrCreateClassInPackage(pkg, "AClass");
        assertNotNull(classifier);

        String objectName = "uniqueObject1";
        Element anObject = eaRepo.findOrCreateObjectInPackage(pkg, objectName, null);
        assertNotNull(anObject);

        assertTrue(eaRepo.isOfType(anObject, null));
        assertFalse(eaRepo.isOfType(anObject, classifier));

        anObject = eaRepo.findOrCreateObjectInPackage(pkg, objectName, classifier);

        assertFalse(eaRepo.isOfType(anObject, null));
        assertTrue(eaRepo.isOfType(anObject, classifier));

        classifier = eaRepo.findOrCreateClassInPackage(pkg, "MQQueue");
        assertNotNull(classifier);

        assertFalse(eaRepo.isOfType(anObject, null));
        assertFalse(eaRepo.isOfType(anObject, classifier));

    }

    public void testFindOrCreateComponentInPackage() throws Exception {
        String packName = "Komponenter";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        assertNotNull(pkg);
        Element newComponent = eaRepo.findOrCreateComponentInPackage(pkg, "nyKomponent");
        assertNotNull(newComponent);
        assertEquals("nyKomponent", newComponent.GetName());
        Element theComponent = eaRepo.findOrCreateComponentInPackage(pkg, "nyKomponent");
        assertEquals(newComponent.GetElementID(), theComponent.GetElementID());
    }

    public void testFindOrCreatePackage() throws Exception {
        String packName = "Rot";
        Package pkg = eaRepo.findPackageByName(packName,  EaRepo.RECURSIVE);
        assertNotNull(pkg);

        Package newPkg = eaRepo.findOrCreatePackage(pkg, "Komponenter");
        assertNotNull(newPkg);
        assertEquals("Komponenter", newPkg.GetName());
        assertNotNull(newPkg.GetElement());
        assertEquals(pkg.GetPackageID(), newPkg.GetParentID());
    }

    public void testGetEaDataTypes() throws Exception {
        log.info("EA DataTypes: " + eaRepo.getEaDataTypes());
    }

    public void testFindComponentInstancesInPackage() throws Exception {
        String packName = "MQ Managers";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        List<Element> componentInstances = eaRepo.findComponentInstancesInPackage(thePkg);
//        assertEquals(1, componentInstances.size());
        for (Element e : componentInstances) {
            log.info("Component Instance name: " + e.GetName());
            assertEquals(EaMetaType.COMPONENT.toString(), e.GetMetaType());
            assertEquals(EaMetaType.COMPONENT.toString(), e.GetClassifierType());
        }
    }

    public void testFindOrCreateComponentInstanceInPackage() throws Exception {
        String packName = "MQ Managers";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, EaRepo.RECURSIVE);
        Element classifier = eaRepo.findOrCreateComponentInPackage(thePkg, "KøManager");
        Element componentInstance = eaRepo.findOrCreateComponentInstanceInPackage(thePkg, "EnNyManager", classifier);
        assertNotNull(componentInstance);
        assertEquals(EaMetaType.COMPONENT.toString(), componentInstance.GetMetaType());
        // todo this fails the first time, why?
//        assertEquals(EaMetaType.COMPONENT.toString(), componentInstance.GetClassifierType());
        assertEquals("EnNyManager", componentInstance.GetName());
        assertEquals(classifier.GetElementID(), componentInstance.GetClassifierID());
    }

    public void testFindOrCreateDiagramInPackage() throws Exception {
        String packName = "MQ Managers";
        Package rootPkg = eaRepo.getRootPackage();
        Package thePkg = eaRepo.findPackageByName(packName, rootPkg, /* doRecurse */ true);
        Diagram d = eaRepo.findOrCreateDiagramInPackage(thePkg, null, EaDiagramType.COMPOSITE_STRUCTURE);
        assertNotNull(d);
        assertEquals(d.GetName(), thePkg.GetName());

        d = eaRepo.findOrCreateDiagramInPackage(thePkg, "XyZ Diagram", EaDiagramType.INTERACTION_OVERVIEW);
        assertNotNull(d);
        assertEquals(d.GetName(), "XyZ Diagram");
        assertEquals(d.GetType(), EaDiagramType.INTERACTION_OVERVIEW.toString());
    }
}
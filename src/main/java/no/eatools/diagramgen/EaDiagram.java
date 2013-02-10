package no.eatools.diagramgen;

import no.eatools.util.EaApplicationProperties;
import no.eatools.util.IntCounter;
import no.eatools.util.SystemProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.sparx.Diagram;
import org.sparx.Package;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A Wrapper class to facilitate  Diagram generation and manipulation.
 *
 * @author Per Spilling (per.spilling@objectware.no)
 */
public class EaDiagram {
    private static final Logger log = Logger.getLogger(EaDiagram.class);
    private Diagram eaDiagram;
    private ImageFileFormat defaultImageFormat = ImageFileFormat.PNG;
    private EaRepo eaRepo;
    private String logicalPathname;


    /**
     * Generate all diagrams from the model into the directory path.
     * The package structure of the model is retained as directory structure.
     * All existing diagrams are overwritten.
     */
    public static int generateAll(EaRepo eaRepo) {
        IntCounter count = new IntCounter();
        generateAllDiagrams(eaRepo, eaRepo.getRootPackage(), count);
        return count.count;
    }

    /**
     * Recursive method that finds all diagrams in a package and writes them to file.
     *
     * @param repo
     * @param pkg
     * @param diagramCount
     */
    private static void generateAllDiagrams(EaRepo repo, Package pkg, IntCounter diagramCount) {
        List<EaDiagram> diagrams = findDiagramsInPackage(repo, pkg);
        if (diagrams.size() > 0) {
            log.debug("Generating diagrams in package: " + pkg.GetName());
            diagramCount.count = diagramCount.count + diagrams.size();
            for (EaDiagram d : diagrams) {
                log.debug("Generating diagrams: " + d.getFilename());
                d.writeImageToFile();
            }
        }
        for (Package p : pkg.GetPackages()) {
            generateAllDiagrams(repo, p, diagramCount);
        }
    }

    public static EaDiagram findDiagram(EaRepo eaRepo, String diagramName) {
        return findDiagram(eaRepo, eaRepo.getRootPackage(), diagramName, true);
    }

    private static EaDiagram findDiagram(EaRepo eaRepo, Package pkg, String diagramName, boolean recursive) {
        if (pkg == null) {
            return null;
        }
        for (Diagram diagram : pkg.GetDiagrams()) {
            //log.debug("Diagram name = " + diagram.GetName());
            if (diagram.GetName().equals(diagramName)) {
                return new EaDiagram(eaRepo, diagram, getPackagePath(eaRepo, pkg));
            }
        }
        if (recursive) {
            for (Package p : pkg.GetPackages()) {
                EaDiagram d = findDiagram(eaRepo, p, diagramName, recursive);
                if (d != null) {
                    return d;
                }
            }
        }
        return null;
    }

    private static String getPackagePath(EaRepo eaRepo, Package pkg) {
        ArrayList<Package> ancestorPackages = new ArrayList<Package>();
        getAncestorPackages(ancestorPackages, eaRepo, pkg);
        StringBuffer pathName = new StringBuffer();
        Collections.reverse(ancestorPackages);
        for (Package p : ancestorPackages) {
            pathName.append(SystemProperties.FILE_SEPARATOR.value() + p.GetName());
        }
        return pathName.toString();
    }

    private static String makeWebFriendlyFilename(String s) {
        s = StringUtils.replaceChars(s, ' ', '_');
        s = StringUtils.replaceChars(s, '/', '-');
        /* Replace Norwegian characters with alternatives */
        s = StringUtils.replace(s, "Æ", "ae");
        s = StringUtils.replace(s, "Ø", "oe");
        s = StringUtils.replace(s, "Å", "aa");
        s = StringUtils.replace(s, "æ", "ae");
        s = StringUtils.replace(s, "ø", "oe");
        s = StringUtils.replace(s, "å", "aa");
        s = StringUtils.lowerCase(s);
        return s;
    }

    private static void getAncestorPackages(ArrayList<Package> ancestorPackages, EaRepo eaRepo, Package pkg) {
        ancestorPackages.add(pkg);
        if (pkg.GetParentID() != 0) {
            getAncestorPackages(ancestorPackages, eaRepo, eaRepo.findPackageByID(pkg.GetParentID()));
        }
    }

    /**
     * Find all UML diagrams inside a specific Package. Non-recursive, searches the top-level (given)
     * package only.
     *
     * @param pkg the Package to serach in.
     * @return
     */
    public static List<EaDiagram> findDiagramsInPackage(EaRepo eaRepo, Package pkg) {
        if (pkg == null) {
            return Collections.EMPTY_LIST;
        }
        List<EaDiagram> result = new ArrayList<EaDiagram>();
        for (Diagram d : pkg.GetDiagrams()) {
            result.add(new EaDiagram(eaRepo, d, getPackagePath(eaRepo, pkg)));
        }
        return result;
    }

    public EaDiagram(EaRepo repository, Diagram diagram, String pathName) {
        eaDiagram = diagram;
        eaRepo = repository;
        logicalPathname = pathName;
    }

    public EaDiagram(EaRepo repository, Diagram diagram, String pathName, ImageFileFormat imageFormat) {
        eaDiagram = diagram;
        eaRepo = repository;
        logicalPathname = pathName;
        defaultImageFormat = imageFormat;
    }

    public boolean writeImageToFile() {
        return writeImageToFile(defaultImageFormat);
    }

    public boolean writeImageToFile(ImageFileFormat imageFileFormat) {
        // make sure the directory exists
        File f = new File(getAbsolutePathName());
        f.mkdirs();
        String diagramFileName = getAbsoluteFilename();
        if (eaRepo.getProject().PutDiagramImageToFile(eaDiagram.GetDiagramGUID(), diagramFileName, imageFileFormat.isRaster())) {
            log.info("Diagram generated at: " + diagramFileName);
            return true;
        } else {
            log.error("Unable to create diagram:" + diagramFileName);
            return false;
        }
    }

    public String getPathname() {
        return logicalPathname;
    }

    public String getAbsolutePathName() {
        return makeWebFriendlyFilename(EaApplicationProperties.EA_DOC_ROOT_DIR.value() + logicalPathname);
    }

    public String getFilename() {
        return makeWebFriendlyFilename(eaDiagram.GetName() + defaultImageFormat.getFileExtension());
    }

    public String getAbsoluteFilename() {
        return getAbsolutePathName() + SystemProperties.FILE_SEPARATOR.value() + getFilename();
    }
}

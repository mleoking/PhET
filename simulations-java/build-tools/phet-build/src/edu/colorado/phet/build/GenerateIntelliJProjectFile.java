package edu.colorado.phet.build;

import org.apache.tools.ant.BuildException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class GenerateIntelliJProjectFile extends PhetAllSimTask {
    private static final String SIMULATIONS_JAVA_FOLDER_NAME = "simulations-java";

    private static final String SOURCE_DEPENDS_MACRO  = "SOURCE.ROOTS";
    private static final String LIBRARY_DEPENDS_MACRO = "LIBRARIES";
    private static final String DATA_DEPENDS_MACRO    = "DATA.ROOTS";

    private static final File INTELLIJ_IDEA_MODULE_FILE_TEMPLATE  = new File("build-tools/phet-build/templates/intellij-idea-template-iml.xml");
    private static final File INTELLIJ_IDEA_MODULE_FILE_OUTPUT    = new File("./intellij-idea-all.iml");

    private static final File INTELLIJ_IDEA_PROJECT_FILE_TEMPLATE = new File("build-tools/phet-build/templates/intellij-idea-template-ipr.xml");
    private static final File INTELLIJ_IDEA_PROJECT_FILE_OUTPUT   = new File("./intellij-idea-all.ipr");

    private static final File INTELLIJ_IDEA_WORKSPACE_FILE_TEMPLATE = new File("build-tools/phet-build/templates/intellij-idea-template-iws.xml");
    private static final File INTELLIJ_IDEA_WORKSPACE_FILE_OUTPUT   = new File("./intellij-idea-all.iws");

    public void execute() throws BuildException {
        StringBuffer sourceDepends  = new StringBuffer();
        StringBuffer libraryDepends = new StringBuffer();
        StringBuffer dataDepends    = new StringBuffer();

        Set sourceSet = new HashSet();
        Set librarySet = new HashSet();
        Set dataSet = new HashSet();

        PhetProject[] phetProjects = PhetProject.getAllProjects(getBaseDir());

        for ( int i = 0; i < phetProjects.length; i++ ) {
            PhetProject phetProject = phetProjects[i];

            sourceSet.addAll(Arrays.asList(phetProject.getAllSourceRoots()));
            librarySet.addAll(Arrays.asList(phetProject.getAllJarFiles()));
            dataSet.addAll(Arrays.asList(phetProject.getAllDataDirectories()));
        }

        try {
            generateSourceDepends((File[])sourceSet.toArray(new File[sourceSet.size()]), sourceDepends);

            generateModuleLibraryDepends((File[])librarySet.toArray(new File[librarySet.size()]), libraryDepends);

            generateModuleLibraryDepends((File[])dataSet.toArray(new File[dataSet.size()]), dataDepends);

            HashMap macroToValue = new HashMap();

            macroToValue.put(SOURCE_DEPENDS_MACRO,  "<content url=\"file://$MODULE_DIR$\">" + sourceDepends.toString() + "</content>");
            macroToValue.put(LIBRARY_DEPENDS_MACRO, libraryDepends.toString());
            macroToValue.put(DATA_DEPENDS_MACRO,    dataDepends.toString());

            FileUtils.filter(INTELLIJ_IDEA_MODULE_FILE_TEMPLATE,    INTELLIJ_IDEA_MODULE_FILE_OUTPUT, macroToValue);

			FileUtils.copyTo(INTELLIJ_IDEA_PROJECT_FILE_TEMPLATE,   INTELLIJ_IDEA_PROJECT_FILE_OUTPUT);

			FileUtils.copyTo(INTELLIJ_IDEA_WORKSPACE_FILE_TEMPLATE, INTELLIJ_IDEA_WORKSPACE_FILE_OUTPUT);
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }

    private void generateModuleLibraryDepends(File[] jarFiles, StringBuffer libraryDepends) throws IOException {
        for ( int j = 0; j < jarFiles.length; j++ ) {
            File jarFile = jarFiles[j];

            libraryDepends.append(createModuleLibraryElement(jarFile));
        }
    }

    private void generateSourceDepends(File[] sourceFiles, StringBuffer sourceDepends) throws IOException {
        for ( int j = 0; j < sourceFiles.length; j++) {
            File sourceFile = sourceFiles[j];

            sourceDepends.append(createContentElement(sourceFile));
        }
    }

    private String createContentElement(File absoluteFile) throws IOException {
        String relativePath = convertAbsoluteToRelative(absoluteFile);

        return //"            <content url=\"file://$MODULE_DIR$\">\n" +
               "                <sourceFolder url=\"file://$MODULE_DIR$" + relativePath + "\" isTestSource=\"false\" />\n";
               //"            </content>\n\n";
    }

    private String convertAbsoluteToRelative(File absoluteFile) throws IOException {
        String absolutePath = absoluteFile.getCanonicalFile().getAbsolutePath().replace('\\', '/');

        return absolutePath.substring(absolutePath.lastIndexOf(SIMULATIONS_JAVA_FOLDER_NAME) + SIMULATIONS_JAVA_FOLDER_NAME.length());
    }

    private String createModuleLibraryElement(File absoluteFile) throws IOException {
        String relativePath = convertAbsoluteToRelative(absoluteFile);

		boolean isJar = absoluteFile.getName().toLowerCase().endsWith("jar");

		String prefix = isJar ? "jar" : "file";
        String suffix = isJar ? "!/"  : "";

        return "        <orderEntry type=\"module-library\">\n" +
               "            <library>\n" +
               "                <CLASSES>\n" +
               "                    <root url=\"" + prefix + "://$MODULE_DIR$" + relativePath + suffix + "\" />\n" +
               "                </CLASSES>\n" +
               "               <JAVADOC />\n" +
               "               <SOURCES />\n" +
               "            </library>\n" +
               "        </orderEntry>\n\n";
    }
}

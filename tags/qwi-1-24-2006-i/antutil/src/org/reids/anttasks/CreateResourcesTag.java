/*Copyright, Sam Reid, 2003.*/
package org.reids.anttasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Echo;
import org.apache.tools.ant.util.FileUtils;

import java.io.File;

/**
 * User: Sam Reid
 * Date: Jun 27, 2003
 * Time: 3:11:24 PM
 * Copyright (c) Jun 27, 2003 by Sam Reid
 */
public class CreateResourcesTag extends Task {
    String mainJar;
    String relativePath = "lib";
    String versionString = "1.4+";
    private File directory;
    static final FileUtils utils = FileUtils.newFileUtils();
    File outputFile = null;//if null, output is only to System.out

    public void setMainJar(String mainJar) {
        this.mainJar = mainJar;
    }

    public void setVersionString(String versionString) {
        this.versionString = versionString;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }


    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    static final String inQuotes(String text) {
        return QUOTE + text + QUOTE;
    }

    // The setter for the "message" attribute
    public void setDirectory(File dir) {
        this.directory = dir;
    }

    static final String QUOTE = "\"";

    /**Gets all items in the classpath and copies them to a common location.*/
    public void execute() throws BuildException {
        super.execute();
        File[] files = directory.listFiles();

        String output = "";
        output += ("<resources>\n");
        output += ("<j2se version=" + inQuotes(versionString) + "/>\n");
        output += ("<jar href=" + inQuotes(mainJar) + "/>\n");

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            output += ("<jar href=" + inQuotes(relativePath + "/" + file.getName()) + "/>\n");
        }
        output += "</resources>";

        Echo echo = new Echo();
        echo.setProject(getProject());
//        echo.setRuntimeConfigurableWrapper(getRuntimeConfigurableWrapper());
        echo.setOwningTarget(getOwningTarget());
        echo.setMessage(output);
        if (outputFile != null) {
            echo.setFile(outputFile);
        }
        echo.execute();

    }

}

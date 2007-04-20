package edu.colorado.phet.build;

import java.io.File;
import java.util.Arrays;

/**
 * A localized flavor for a project
 */
public class PhetProjectFlavor {
    private String description;
    private String[] args;
    private String mainclass;
    private File screenshot;
    private String name;

    public PhetProjectFlavor( String name, String description, String mainclass, String[] args, File screenshot ) {
        this.description = description;
        this.args = args;
        this.mainclass = mainclass;
        this.screenshot = screenshot;
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public File getScreenshot() {
        return screenshot;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }

    public String getMainclass() {
        return mainclass;
    }

    public String toString() {
        return "" + name + ": description=" + description + ", mainclass=" + mainclass + ", args=" + Arrays.asList( args ) + ", screenshot=" + screenshot;
    }
}

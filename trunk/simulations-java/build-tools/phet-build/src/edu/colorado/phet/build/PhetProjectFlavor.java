package edu.colorado.phet.build;

import java.io.File;
import java.util.Arrays;

/**
 * A localized flavor for a project
 */
public class PhetProjectFlavor {
    private String flavorName;
    private String description;
    private String[] args;
    private String mainclass;
    private File screenshot;
    private String title;

    public PhetProjectFlavor( String flavorName, String title, String description, String mainclass, String[] args, File screenshot ) {
        this.flavorName = flavorName;
        this.description = description;
        this.args = args;
        this.mainclass = mainclass;
        this.screenshot = screenshot;
        this.title = title;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getDescription() {
        return description;
    }

    public File getScreenshot() {
        return screenshot;
    }

    public String getTitle() {
        return title;
    }

    public String[] getArgs() {
        return args;
    }

    public String getMainclass() {
        return mainclass;
    }

    public String toString() {
        return "" + title + ": description=" + description + ", mainclass=" + mainclass + ", args=" + Arrays.asList( args ) + ", screenshot=" + screenshot;
    }
}

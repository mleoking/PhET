package edu.colorado.phet.buildtools.util;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

/**
 * Author: Sam Reid
 * Jun 27, 2007, 3:49:10 PM
 */
public class FileExistsCondition implements Condition {
    private String file;

    public String getFile() {
        return file;
    }

    public void setFile( String file ) {
        this.file = file;
    }

    public boolean eval() throws BuildException {
        return file != null && new File( file ).exists();
    }
}

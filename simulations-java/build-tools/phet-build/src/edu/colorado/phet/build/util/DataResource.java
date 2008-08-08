package edu.colorado.phet.build.util;

import java.io.File;

/**
 * Created by: Sam
 * Aug 7, 2008 at 10:30:40 AM
 */
public class DataResource {
    private File file;

    public DataResource( File file ) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String toString() {
        return "file=" + file;
    }
}

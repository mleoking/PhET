package edu.colorado.phet.build.util;

import java.io.File;

/**
 * Created by: Sam
 * Aug 7, 2008 at 10:30:40 AM
 */
public class MediaInfo {
    private File file;

    public MediaInfo( File file ) {
        this.file = file;
    }

    public String toString() {
        return "file=" + file;
    }
}

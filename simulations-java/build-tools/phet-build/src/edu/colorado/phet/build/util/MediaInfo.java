package edu.colorado.phet.build.util;

import java.io.File;
import java.util.Properties;

/**
 * Created by: Sam
 * Aug 7, 2008 at 10:30:40 AM
 */
public class MediaInfo {
    private File file;

    public MediaInfo( File file ) {
        this.file = file;
        File annotationDir=new File( "../util/phet-media-license/annotated-data");
        File annotatedVersion=new File( annotationDir, file.getName());
        Properties properties = new Properties( );
    }

    public String toString() {
        return "file=" + file;
    }
}

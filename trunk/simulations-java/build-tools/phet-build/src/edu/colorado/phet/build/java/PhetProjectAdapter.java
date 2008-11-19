package edu.colorado.phet.build.java;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.build.PhetProject;

public class PhetProjectAdapter extends PhetProject {
    private File baseDir;

    public PhetProjectAdapter( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public PhetProjectAdapter( File parentDir, String name ) throws IOException {
        super( parentDir, name );
    }

    public void setAntBaseDir( File baseDir ) {
        this.baseDir = baseDir;
    }

    public File getAntBaseDir() {
        return baseDir;
    }

    public PhetProject[] getDependencies() {
        PhetProject[] a = super.getDependencies();
        return convertToMyPhetProjecets( a, baseDir );
    }

    public static PhetProjectAdapter[] convertToMyPhetProjecets( PhetProject[] a, File baseDir ) {
        PhetProjectAdapter[] b = new PhetProjectAdapter[a.length];
        for ( int i = 0; i < a.length; i++ ) {
            try {
                b[i] = new PhetProjectAdapter( a[i].getProjectDir() );
                b[i].setAntBaseDir( baseDir );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return b;
    }
}

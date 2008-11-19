package edu.colorado.phet.build.java;

import edu.colorado.phet.build.PhetProject;

public interface VersionIncrement {
    void increment( PhetProject project );

    public static class UpdateDev implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setVersionField( "dev", project.getDevVersion() + 1 );
        }
    }

    public static class UpdateProd implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setVersionField( "dev", 0 );
            project.setVersionField( "minor", project.getMinorVersion() + 1 );
        }
    }
}

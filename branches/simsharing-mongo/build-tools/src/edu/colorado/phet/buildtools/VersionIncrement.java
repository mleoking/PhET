package edu.colorado.phet.buildtools;

public interface VersionIncrement {

    void increment( PhetProject project );

    public static class UpdateDev implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setDevVersion( project.getDevVersion() + 1 );
        }
    }

    public static class UpdateProdMinor implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setDevVersion( 0 );
            project.setMinorVersion( project.getMinorVersion() + 1 );
        }
    }

    public static class UpdateProdMajor implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setDevVersion( 0 );
            project.setMinorVersion( 0 );
            project.setMajorVersion( project.getMajorVersion() + 1 );
        }
    }
}

package edu.colorado.phet.buildtools;

public interface VersionIncrement {

    void increment( PhetProject project );

    public static class UpdateDev implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setDevVersion( project.getDevVersion() + 1 );
        }
    }

    public static class UpdateProd implements VersionIncrement {
        public void increment( PhetProject project ) {
            project.setDevVersion( 0 );
            project.setMinorVersion( project.getMinorVersion() + 1 );
        }
    }
}

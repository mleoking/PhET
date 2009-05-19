package edu.colorado.phet.buildtools.translate;

import java.io.File;

public class ResourceDeployUtils {

    public static File getTestDir( File resourceDir ) {
        return new File( resourceDir, "test" );
    }

    public static File getResourceSubDir( File resourceDir ) {
        return new File( resourceDir, "resource" );
    }

    public static File getBackupDir( File resourceDir ) {
        return new File( resourceDir, "backup" );
    }

    public static File getExtrasDir( File resourceDir ) {
        return new File( resourceDir, "extras" );
    }

    public static File getLiveSimsDir( File resourceDir ) {
        return new File( resourceDir, "../.." );
    }


    public static File getResourceProperties( File resourceDir ) {
        return new File( getResourceSubDir( resourceDir ), "resource.properties" );
    }

    public static boolean ignoreTestFile( File file ) {
        return file.getName().endsWith( ".swf" );
    }

}

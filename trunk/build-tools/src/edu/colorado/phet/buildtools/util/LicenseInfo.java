package edu.colorado.phet.buildtools.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * Created by: Sam
 * Aug 5, 2008 at 10:47:00 AM
 */
public class LicenseInfo {
    private File file;
    private String line;

    public LicenseInfo( File file, String line ) {
        System.out.println( "file = " + file );
        System.out.println( "line = " + line );
        this.file = file;
        this.line = line.trim();
    }

    public String toString() {
        return line;
    }

    public static LicenseInfo[] getAll( File file ) {
        if ( !file.exists() ) {
            return new LicenseInfo[0];
        }
        try {
            ArrayList infos = new ArrayList();
            BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
            String line = bufferedReader.readLine();
            while ( line != null ) {
                if ( line.trim().length() > 0 && !line.startsWith( "#" ) ) {
                    infos.add( parseLine( file, line ) );
                }
                line = bufferedReader.readLine();
            }
            return (LicenseInfo[]) infos.toArray( new LicenseInfo[infos.size()] );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        return new LicenseInfo[0];
    }

    private static LicenseInfo parseLine( File file, String line ) {
        return new LicenseInfo( file, line );
    }

    public String getID() {
        return new ResourceAnnotation( line ).getName();
    }

    public String getLicense() {
        return new ResourceAnnotation( line ).getLicense();
    }

    public File getLicenseFile() {
        String child = new ResourceAnnotation( line ).getLicensefile();
        if ( child == null ) {
            return null;
        }
        return new File( file.getParentFile(), child );
    }
}

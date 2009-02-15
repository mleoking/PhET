package edu.colorado.phet.buildtools.scripts;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.java.JavaSimulationProject;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * This utility is responsible for exporting a sim and all its dependencies to an external directory.
 * TODO: add support for SVN export
 */
public class ExportSim {
    public static void main( String[] args ) throws IOException {
        String simname = "circuit-construction-kit";
        File simulationsJava = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" );
        File dest = new File( "C:/Users/Sam/Desktop/cckout" );
        new ExportSim().exportSim( simname, dest, simulationsJava );
    }

    private void exportSim( String simname, File dest, File simulationsJava ) throws IOException {
        PhetProject p = new JavaSimulationProject( new File( simulationsJava, "simulations" ), simname );
        System.out.println( "p = " + p );
        File[] s = p.getAllJavaSourceRoots();
        for ( int i = 0; i < s.length; i++ ) {
            File file = s[i];
            System.out.println( "file = " + file );
            FileUtils.copyRecursive( file, new File( dest, "" + file.getParentFile().getName() + "-src" ) );
        }
        File[] k = p.getAllJarFiles();
        for ( int i = 0; i < k.length; i++ ) {
            File file = k[i];
            System.out.println( "k=" + file );
            File dest1 = new File( dest, "lib/" + file.getName() );
            dest1.getParentFile().mkdirs();
            FileUtils.copyTo( file, dest1 );
        }
        File[] r = p.getAllDataDirectories();
        for ( int i = 0; i < r.length; i++ ) {
            File file = r[i];
            System.out.println( "file = " + file );
            FileUtils.copyRecursive( file, new File( dest, "" + file.getParentFile().getName() + "-data" ) );
        }
    }
}

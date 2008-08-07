package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.LicenseInfo;
import edu.colorado.phet.build.util.MediaInfo;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DisplayDependencies {
    private File trunk;

    public static void main( String[] args ) throws IOException {
        new DisplayDependencies().start();
    }

    private void start() throws IOException {
        trunk = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2" );

        System.out.println( "PhET Java Software Dependencies\n" +
                            "" + new Date() + "\n" );

        File baseDir = new File( trunk, "simulations-java" );
        String[] simNames = PhetProject.getSimNames( baseDir );
        for ( int i = 0; i < simNames.length; i++ ) {
            String simName = simNames[i];
//            System.out.println( "name=" + simName );
            visitSim( simName );
        }
    }

    private void visitSim( String simName ) throws IOException {
        System.out.println( simName + ":" );
        PhetProject phetProject = new PhetProject( new File( trunk, "simulations-java/simulations/" + simName ) );

        PhetProject[] dep = phetProject.getDependencies();
        System.out.println( "\tProject Dependencies:" );
        for ( int i = 0; i < dep.length; i++ ) {
            PhetProject project = dep[i];
            System.out.println( "\t\t" + i + ". " + project.getName() );
        }
        System.out.println( "\tJAR Dependencies:" );
        File[] j = phetProject.getAllJarFiles();
        for ( int i = 0; i < j.length; i++ ) {
            File file = j[i];
            System.out.println( "\t\t" + i + ". " + file.getName() );
        }
        System.out.println( "\tSource Dependencies:" );
        File[] s = phetProject.getSourceRoots();
        for ( int i = 0; i < s.length; i++ ) {
            File file = s[i];
            System.out.println( "\t\t" + i + ". " + file.getParentFile().getName() + "/" + file.getName() );
        }

        LicenseInfo[] licenseInfo = phetProject.getAllLicensingInfo();
        if ( licenseInfo.length > 0 ) {
            System.out.println( "\tLicensing info:" );
        }
        for ( int i = 0; i < licenseInfo.length; i++ ) {
            LicenseInfo info = licenseInfo[i];
            System.out.println( "\t\t" + i + ". " + info );
        }

        MediaInfo[] mediaInfo = phetProject.getAllMediaInfo();
        System.out.println( "\tMedia Info:" );
        for ( int i = 0; i < mediaInfo.length; i++ ) {
            MediaInfo info = mediaInfo[i];
            System.out.println( "\t\t" + i + ". " + info );
        }

        System.out.println( "" );
    }
}

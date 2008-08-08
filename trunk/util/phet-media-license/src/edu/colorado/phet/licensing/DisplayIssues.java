package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.LicenseInfo;

/**
 * Created by: Sam
 * Aug 4, 2008 at 7:10:23 PM
 */
public class DisplayIssues {
    private File trunk;
    private int totalIssues = 0;

    public static void main( String[] args ) throws IOException {
        new DisplayIssues().start();
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
        System.out.println( "Total issues: " + totalIssues );
    }

    private void visitSim( String simName ) throws IOException {
        System.out.println( simName + ":" );
        PhetProject phetProject = new PhetProject( new File( trunk, "simulations-java/simulations/" + simName ) );

        LicenseInfo[] licenseInfo = phetProject.getAllLicensingInfo();
        int issueCount = licenseInfo.length;
        if ( licenseInfo.length > 0 ) {
            System.out.println( "\tLicensing info:" );
        }
        for ( int i = 0; i < licenseInfo.length; i++ ) {
            LicenseInfo info = licenseInfo[i];
            System.out.println( "\t\t" + i + ". " + info );
        }

        File data = phetProject.getDataDirectory();
        OutputLicenseInfo outputLicenseInfo = new OutputLicenseInfo();
        outputLicenseInfo.visitDirectory( phetProject, data );
        issueCount += outputLicenseInfo.getCount();

        if ( issueCount == 0 ) {
            System.out.println( "No known issues for " + simName );
        }
        else {
            System.out.println( "Found " + issueCount + " known issues for " + simName );
        }
        totalIssues += issueCount;

        System.out.println( "" );
    }
}
package edu.colorado.phet.licensing.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.licensing.AnnotatedFile;
import edu.colorado.phet.licensing.LicenseIssue;
import edu.colorado.phet.licensing.LicenseIssue.DataFileLicenseIssue;
import edu.colorado.phet.licensing.SimInfo;
import edu.colorado.phet.licensing.TrunkDirectory;

public class LicenseReport {

    // intended to be called via main
    private LicenseReport() {
    }

    public static void main( String[] args ) {
        if ( args.length != 1 ) {
            System.out.println( "usage: " + LicenseReport.class.getName() + " absolute_path_to_trunk" );
            System.exit( 1 );
        }
        File trunk = new TrunkDirectory( args[0] );
        new LicenseReport().start( trunk );
    }

    private void start( File trunk ) {
        PhetProject[] phetProject = PhetProject.getAllProjects( trunk );
        for ( int i = 0; i < phetProject.length; i++ ) {
            PhetProject project = phetProject[i];
            System.out.println( "Project: " + project.getName() );
            LicenseIssue[] licenseIssue = getLicenseIssues( trunk, project );
            for ( int j = 0; j < licenseIssue.length; j++ ) {
                LicenseIssue issue = licenseIssue[j];
                System.out.println( "\t" + issue );
            }
        }
    }

    private LicenseIssue[] getLicenseIssues( File trunk, PhetProject project ) {
        //report on:
        // -media (images + audio)
        // -code used in the project
        // -contributed libraries
        // -issues in other phet projects (including common projects)

        //alternatively

        //each source root
        //each data root
        //each jar dependency
        //each project dependency

        //alternatively

        //project itself (including source code and media)
        //project dependencies (including common projects)
        //contrib dependencies

        ArrayList<LicenseIssue> issues = new ArrayList<LicenseIssue>();
        issues.addAll( Arrays.asList( getDataIssues( trunk, project ) ) );
        issues.addAll( Arrays.asList( getSourceIssues() ) );
        issues.addAll( Arrays.asList( getProjectDependencies( project ) ) );
        issues.addAll( Arrays.asList( getLibraryIssues() ) );
        return issues.toArray( new LicenseIssue[issues.size()] );
    }

    private LicenseIssue[] getProjectDependencies( PhetProject project ) {
        PhetProject[] dep = project.getAllDependencies();
        ArrayList<LicenseIssue> issues = new ArrayList<LicenseIssue>();
        for ( int i = 0; i < dep.length; i++ ) {
            PhetProject phetProject = dep[i];
            if ( !phetProject.equals( project ) ) {
                issues.addAll( Arrays.asList( getProjectDependencies( phetProject ) ) );
            }
        }
        return issues.toArray( new LicenseIssue[issues.size()] );
    }

    private LicenseIssue[] getLibraryIssues() {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getSourceIssues() {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getDataIssues( File trunk, PhetProject project ) {
        try {
            SimInfo issues = SimInfo.getSimInfo( trunk, project.getName() ).getIssues();
            AnnotatedFile[] a = issues.getResources();
            LicenseIssue[] out = new LicenseIssue[a.length];
            for ( int i = 0; i < out.length; i++ ) {
                out[i] = new DataFileLicenseIssue( a[i] );
            }
            return out;
        }
        catch ( IOException e ) {
            e.printStackTrace();
            return new LicenseIssue[0];
        }
    }
}

package edu.colorado.phet.licensing.reports;

import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.licensing.Config;

public class LicenseReport {
    public static void main( String[] args ) {
        new LicenseReport().start();
    }

    private void start() {
        PhetProject[] phetProject = PhetProject.getAllProjects( Config.SIMULATIONS_JAVA );
        for ( int i = 0; i < phetProject.length; i++ ) {
            PhetProject project = phetProject[i];
            System.out.println( "Project: " + project.getName() );
            LicenseIssue[] licenseIssue = getLicenseIssues( project );
            for ( int j = 0; j < licenseIssue.length; j++ ) {
                LicenseIssue issue = licenseIssue[j];
                System.out.println( "\t" + issue );
            }
        }
    }

    private LicenseIssue[] getLicenseIssues( PhetProject project ) {
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

        ArrayList issues = new ArrayList();
        issues.addAll( Arrays.asList( getMediaLicenseIssues( project ) ) );
        issues.addAll( Arrays.asList( getSourceCodeIssues( project ) ) );
        issues.addAll( Arrays.asList( getLibraryIssues( project ) ) );
        issues.addAll( Arrays.asList( getProjectDependencies( project ) ) );
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getProjectDependencies( PhetProject project ) {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getLibraryIssues( PhetProject project ) {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getSourceCodeIssues( PhetProject project ) {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getMediaLicenseIssues( PhetProject project ) {
        return new LicenseIssue[0];
    }
}

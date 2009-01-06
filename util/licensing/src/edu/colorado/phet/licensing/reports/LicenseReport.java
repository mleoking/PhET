package edu.colorado.phet.licensing.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.licensing.AnnotatedFile;
import edu.colorado.phet.licensing.Config;
import edu.colorado.phet.licensing.SimInfo;

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

        //alternatively

        //project itself (including source code and media)
        //project dependencies (including common projects)
        //contrib dependencies

        ArrayList issues = new ArrayList();
        issues.addAll( Arrays.asList( getDataIssues( project ) ) );
        issues.addAll( Arrays.asList( getSourceIssues( project ) ) );
        issues.addAll( Arrays.asList( getProjectDependencies( project ) ) );
        issues.addAll( Arrays.asList( getLibraryIssues( project ) ) );
        return (LicenseIssue[]) issues.toArray( new LicenseIssue[issues.size()] );
    }

    private LicenseIssue[] getProjectDependencies( PhetProject project ) {
        PhetProject[] dep = project.getAllDependencies();
        ArrayList issues = new ArrayList();
        for ( int i = 0; i < dep.length; i++ ) {
            PhetProject phetProject = dep[i];
            if ( !phetProject.equals( project ) ) {
                issues.addAll( Arrays.asList( getProjectDependencies( phetProject ) ) );
            }
        }
        return (LicenseIssue[]) issues.toArray( new LicenseIssue[issues.size()] );
    }

    private LicenseIssue[] getLibraryIssues( PhetProject project ) {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getSourceIssues( PhetProject project ) {
        return new LicenseIssue[0];
    }

    private LicenseIssue[] getDataIssues( PhetProject project ) {
        try {
            SimInfo issues = SimInfo.getSimInfo( Config.TRUNK, project.getName() ).getIssues();
            AnnotatedFile[] a = issues.getResources();
            LicenseIssue[] out = new LicenseIssue[a.length];
            for ( int i = 0; i < out.length; i++ ) {
                out[i] = new DataFileLicenseIssue( a[i] );
            }
            return out;
        }
        catch( IOException e ) {
            e.printStackTrace();
            return new LicenseIssue[0];
        }
    }

    private class DataFileLicenseIssue extends LicenseIssue {
        private AnnotatedFile annotatedFile;

        public DataFileLicenseIssue( AnnotatedFile annotatedFile ) {
            super();
            this.annotatedFile = annotatedFile;
        }

        public String toString() {
            return annotatedFile.getResourceAnnotation().getName() + " " + annotatedFile.getResourceAnnotation().toText();
        }
    }
}

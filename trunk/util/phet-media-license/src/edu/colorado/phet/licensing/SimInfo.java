package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.util.LicenseInfo;

/**
 * Created by: Sam
 * Aug 8, 2008 at 1:53:25 PM
 */
public class SimInfo {
    private PhetProject project;
    private PhetProject[] dependencies;
    private File[] jarFiles;
    private File[] sourceRoots;
    private LicenseInfo[] licenseInfo;
    private AnnotatedFile[] resources;

    public SimInfo( PhetProject project, PhetProject[] dependencies, File[] jarFiles, File[] sourceRoots, LicenseInfo[] licenseInfo, AnnotatedFile[] resources ) {
        this.project = project;
        this.dependencies = dependencies;
        this.jarFiles = jarFiles;
        this.sourceRoots = sourceRoots;
        this.licenseInfo = licenseInfo;
        this.resources = resources;
    }

    public String getProjectName() {
        return project.getName();
    }

    public AnnotatedFile[] getResources() {
        return resources;
    }

    public int getIssueCount() {
        return dependencies.length + jarFiles.length + sourceRoots.length + licenseInfo.length + resources.length;
    }

    public boolean isEmpty() {
        return getIssueCount() == 0;
    }

    public SimInfo getIssues() {
        //todo: generalize
        return new SimInfo( project, new PhetProject[0], new File[0], new File[0], licenseInfo, getIssues( resources ) );
    }

    public String toString() {
        String s = "Project Dependencies for " + project.getName() + ":\n";
        for ( int i = 0; i < dependencies.length; i++ ) {
            s += "\t" + i + ". " + dependencies[i].getName() + "\n";
        }
        if ( jarFiles.length > 0 ) {
            s += "JAR Dependencies:" + "\n";
        }
        for ( int i = 0; i < jarFiles.length; i++ ) {
            s += "\t" + i + ". " + jarFiles[i].getName() + "\n";
        }

        if ( sourceRoots.length > 0 ) {
            s += "Source Dependencies:" + "\n";
        }
        for ( int i = 0; i < sourceRoots.length; i++ ) {
            s += "\t" + i + ". " + sourceRoots[i].getParentFile().getName() + "/" + sourceRoots[i].getName() + "\n";
        }

        if ( licenseInfo.length > 0 ) {
            s += "Licensing info:" + "\n";
        }
        for ( int i = 0; i < licenseInfo.length; i++ ) {
            s += "\t" + i + ". " + licenseInfo[i] + "\n";
        }

        if ( resources.length > 0 ) {
            s += "Resources:\n";
        }
        for ( int i = 0; i < resources.length; i++ ) {
            s += "\t" + i + ". " + resources[i].getResourceAnnotation().toText() + "\n";
        }
        if ( licenseInfo.length == 0 && resources.length == 0 ) {
            s += ( "No issues found for " + project.getName() );
        }
        return s;
    }

    public static SimInfo getSimInfo( File trunk, String simName ) throws IOException {
        PhetProject phetProject = new PhetProject( new File( trunk, "simulations-java/simulations/" + simName ) );

        return new SimInfo( phetProject, phetProject.getDependencies(), phetProject.getAllJarFiles(), phetProject.getSourceRoots(),
                            phetProject.getAllLicensingInfo(),
                            new DataProcessor().visitDirectory( phetProject, phetProject.getDataDirectory() ) );
    }


    public static boolean hideEntry( ResourceAnnotation entry ) {
        if ( entry.getName().startsWith( "bear" ) ) {
            System.out.println( "bear" );
        }
        return ( entry.getAuthor() != null && entry.getAuthor().equalsIgnoreCase( "phet" ) )
               ||
//               ( entry.getSource() != null && entry.getSource().toLowerCase().startsWith( "microsoft" ) )
//               ||

( entry.getAuthor() != null && entry.getAuthor().equalsIgnoreCase( "cmalley" ) )
||

( entry.getLicense() != null && entry.getLicense().equalsIgnoreCase( "PUBLIC DOMAIN" ) )
||
( entry.getSource() != null && entry.getSource().equalsIgnoreCase( "java" ) )
||
( entry.getSource() != null && entry.getSource().equalsIgnoreCase( "phet" ) )
||
entry.getName().endsWith( ".xml" )
||
entry.getName().endsWith( ".esp" )
||
entry.getName().endsWith( ".html" )
||
entry.getName().endsWith( ".properties" )
||
( entry.getLicense() != null && entry.getLicense().equalsIgnoreCase( "http://creativecommons.org" ) )
||
( entry.getLicense() != null && entry.getLicense().equalsIgnoreCase( "Creative Commons, royalty free, public domain" ) );

    }

    public static AnnotatedFile[] getIssues( AnnotatedFile[] resources ) {
        ArrayList list = new ArrayList();
        for ( int i = 0; i < resources.length; i++ ) {
            AnnotatedFile resource = resources[i];
            if ( !hideEntry( resource.getResourceAnnotation() ) ) {
                list.add( resource );
            }
        }
        return (AnnotatedFile[]) list.toArray( new AnnotatedFile[list.size()] );
    }

    public String getHTMLBody() {
        if ( isEmpty() ) {
            return "<a name=\"" + project.getName() + "\">" + project.getName() + " has no known issues.</a>";
        }
        else {
            String s = "<a name=\"" + project.getName() + "\">" + project.getName() + " issue" + ( getIssueCount() == 1 ? "" : "s" ) + ":</a>";
            s += "<br>";
            for ( int i = 0; i < dependencies.length; i++ ) {
                s += "\t" + i + ". " + dependencies[i].getName() + "<br>";
            }
            if ( jarFiles.length > 0 ) {
                s += "JAR Dependencies:" + "<br>";
            }
            for ( int i = 0; i < jarFiles.length; i++ ) {
                s += "\t" + i + ". " + jarFiles[i].getName() + "<br>";
            }

            if ( sourceRoots.length > 0 ) {
                s += "Source Dependencies:" + "<br>";
            }
            for ( int i = 0; i < sourceRoots.length; i++ ) {
                s += "\t" + i + ". " + sourceRoots[i].getParentFile().getName() + "/" + sourceRoots[i].getName() + "<br>";
            }

            if ( licenseInfo.length > 0 ) {
                s += "Licensing info:" + "<br>";
            }
            for ( int i = 0; i < licenseInfo.length; i++ ) {
                s += "\t" + i + ". " + licenseInfo[i] + "<br>";
            }

            if ( resources.length > 0 ) {
                s += "Resources:<br>";
            }
            for ( int i = 0; i < resources.length; i++ ) {
                s += "\t" + i + ". " + "<a href=\"" + getHTMLFileLocation( resources[i] ) + "\">" + resources[i].getResourceAnnotation().getName() + "</a>: " + resources[i].getResourceAnnotation().toText() + "<br>";
                s += "<br>" +
                     "<img src=\"" + getHTMLFileLocation( resources[i] ) + "\">" +
                     "<br><br><hr>";

            }
            if ( licenseInfo.length == 0 && resources.length == 0 ) {
                s += ( "No issues found for " + project.getName() );
            }
            return s;
        }
    }

    public String getHTMLFileLocation( AnnotatedFile file ) {

        File dataDir = project.getDataDirectory().getAbsoluteFile();
        File f = file.getFile().getAbsoluteFile();
        String relativePath = "";
        while ( !f.equals( dataDir ) ) {
            f = f.getParentFile();
            if ( !f.equals( dataDir ) ) {
                relativePath = f.getName() + "/" + relativePath;
            }
        }
        return "annotated-data/" + project.getName() + "/" + relativePath + file.getFile().getName();
    }

    public String getHTMLHeader() {
        return "<a href=\"#" + project.getName() + "\">" + project.getName() + " (" + getIssueCount() + " issue" + ( getIssueCount() == 1 ? "" : "s" ) + ")</a>";
    }
}

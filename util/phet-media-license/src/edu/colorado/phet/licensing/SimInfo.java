package edu.colorado.phet.licensing;

import java.io.File;
import java.io.IOException;

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

    public String toString() {
        String s = ( "Project Dependencies for " + project.getName() + ":\n" );
        for ( int i = 0; i < dependencies.length; i++ ) {
            s+=( "\t" + i + ". " + dependencies[i].getName() + "\n" );
        }
        s += ( "JAR Dependencies:" + "\n" );
        for ( int i = 0; i < jarFiles.length; i++ ) {
            File file = jarFiles[i];
            s += ( "\t" + i + ". " + file.getName() + "\n" );
        }
        s += ( "Source Dependencies:" + "\n" );
        for ( int i = 0; i < sourceRoots.length; i++ ) {
            File file = sourceRoots[i];
            s += ( "\t" + i + ". " + file.getParentFile().getName() + "/" + file.getName() + "\n" );
        }

        if ( licenseInfo.length > 0 ) {
            s += ( "Licensing info:" + "\n" );
        }
        for ( int i = 0; i < licenseInfo.length; i++ ) {
            LicenseInfo info = licenseInfo[i];
            s += ( "\t" + i + ". " + info + "\n" );
        }

        if ( resources.length > 0 ) {
            s += "Resources:\n";
        }
        for ( int i = 0; i < resources.length; i++ ) {
            AnnotatedFile resource = resources[i];
            s += "\t" + i + ". " + resource.getResourceAnnotation().toText() + "\n";
        }
        return s;
    }

    public static SimInfo getSimInfo( File trunk, String simName ) throws IOException {
        PhetProject phetProject = new PhetProject( new File( trunk, "simulations-java/simulations/" + simName ) );

        return new SimInfo( phetProject, phetProject.getDependencies(), phetProject.getAllJarFiles(), phetProject.getSourceRoots(),
                            phetProject.getAllLicensingInfo(),
                            new OutputLicenseInfo().visitDirectory( phetProject, phetProject.getDataDirectory() ) );
    }
}

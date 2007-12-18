package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Locale;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import edu.colorado.phet.build.PhetProject;

/**
 * Created by: Sam
 * Dec 18, 2007 at 12:29:24 AM
 */
public class SynchronizeJNLP {
    private File basedir;

    public SynchronizeJNLP( File basedir ) {
        this.basedir = basedir;
    }

    public static void main( String[] args ) throws IOException {
        CheckTranslations.clearTempDir();
        PhetProject[] p = args.length == 1 ? PhetProject.getAllProjects( new File( args[0] ) ) :
                          new PhetProject[]{new PhetProject( new File( args[0], "simulations/" + args[2] ) )};
        for ( int i = 0; i < p.length; i++ ) {
            new SynchronizeJNLP( new File( args[0] ) ).synchronizeAllJNLP( p );
        }
    }

    private void synchronizeAllJNLP( PhetProject[] p ) throws IOException {
        for ( int i = 0; i < p.length; i++ ) {
            PhetProject phetProject = p[i];
            synchronizeAllJNLP( phetProject );
        }
    }

    private void synchronizeAllJNLP( PhetProject phetProject ) throws IOException {
        for ( int i = 0; i < phetProject.getFlavorNames().length; i++ ) {
            synchronizeAllJNLP( phetProject, phetProject.getFlavorNames()[i] );
        }
    }

    private void synchronizeAllJNLP( PhetProject phetProject, String flavor ) throws IOException {
        try {
            File downloaded = downloadJAR( phetProject, flavor );

            //for all languages declared locally and remotely, make sure we also have remote JNLP files
            Locale[] local = phetProject.getLocales();
            Locale[] remote = CheckTranslations.listTranslationsInJar( phetProject, downloaded );

            for ( int i = 0; i < local.length; i++ ) {
                if ( !local[i].getLanguage().equals( "en" ) && ( Arrays.asList( remote ).contains( local[i] ) && !remoteJNLPExists( phetProject, flavor, local[i] ) ) ) {
                    createJNLPFile( phetProject, flavor, local[i] );
                }

            }
        }
        catch( FileNotFoundException f ) {
            System.out.println( "ignoring " + phetProject );
            return;//ignore
        }
    }

    private boolean remoteJNLPExists( PhetProject phetProject, String flavor, Locale locale ) {
        try {
            URL url = new URL( "http://phet.colorado.edu/sims/" + phetProject.getName() + "/" + flavor + "_" + locale.getLanguage() + ".jnlp" );
            URLConnection uc = url.openConnection();
            int length = uc.getContentLength();
            System.out.println( "length = " + length );
            InputStream in = uc.getInputStream();
            int b = in.read();
            System.out.println( "b = " + b );
        }
        catch( FileNotFoundException f ) {
            return false;
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return true;
    }

    private void createJNLPFile( PhetProject phetProject, String flavor, Locale locale ) {
        System.setProperty( "JAVA_HOME", "C:\\j2sdk1.4.2_15" );
        System.out.println( "Creating JNLP files for: " + phetProject + ", locale=" + locale );
        File buildFile = new File( basedir, "build.xml" );
        Project p = new Project();
        p.setUserProperty( "ant.file", buildFile.getAbsolutePath() );
        p.setUserProperty( "sim.name", phetProject.getName() );
        p.setUserProperty( "sim.locale", locale.getLanguage() );
        p.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        p.addReference( "ant.projectHelper", helper );
        helper.parse( p, buildFile );
        p.executeTarget( "add-translation-jnlp" );
    }

    private File downloadJAR( PhetProject phetProject, String flavor ) throws IOException {
        String deployUrl = phetProject.getDeployedFlavorJarURL( flavor );

//        File jarFile = File.createTempFile( flavor, ".jar" );
        File jarFile = new File( CheckTranslations.TRANSLATIONS_TEMP_DIR, flavor + ".jar" );

        FileDownload.download( deployUrl, jarFile );
        return jarFile;
    }
}

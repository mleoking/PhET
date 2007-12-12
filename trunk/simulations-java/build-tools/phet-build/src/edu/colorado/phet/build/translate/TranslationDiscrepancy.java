package edu.colorado.phet.build.translate;

import java.io.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;

import com.jcraft.jsch.JSchException;

public class TranslationDiscrepancy {
    private final Set extraLocal;
    private final Set extraRemote;
    private final PhetProject phetProject;
    private final String flavor;

    TranslationDiscrepancy( Set extraLocal, Set extraRemote, PhetProject phetProject, String flavor ) {
        this.extraLocal = extraLocal;
        this.extraRemote = extraRemote;
        this.phetProject = phetProject;
        this.flavor = flavor;
    }

    public Set getExtraLocal() {
        return extraLocal;
    }

    public Set getExtraRemote() {
        return extraRemote;
    }

    public PhetProject getPhetProject() {
        return phetProject;
    }

    public String getFlavor() {
        return flavor;
    }

    public String toString() {
        return "need to be removed from remote jar: " + extraRemote + ", " + "need to be added to remote jar: " + extraLocal + " ";
    }

    public void resolve() {
        try {
            File resolveJAR = new File( FileUtils.getTmpDir(), flavor + "_resolved" + System.currentTimeMillis() + ".jar" );
            resolve( resolveJAR );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void resolve( File resolveJAR ) throws IOException {
        if ( !extraLocal.isEmpty() || !extraRemote.isEmpty() ) {
            File jarFile = downloadJAR();
            System.out.println( "Downloaded Jar file: " + jarFile.getAbsolutePath() );
            synchronizeStrings( jarFile, resolveJAR );

//        try {
//            uploadJAR( resolveJAR );
//        }
//        catch( JSchException e ) {
//            e.printStackTrace();
//        }
        }
        else {
            System.out.println( "no resolution needed for: " + phetProject.getName() + ": " + flavor );
        }
    }

    private void uploadJAR( File resolveJAR ) throws JSchException, IOException {
        ScpTo.uploadFile( resolveJAR, "reids", "tigercat.colorado.edu", "/home/tigercat/phet/reids/" + resolveJAR.getName() );
    }

    private void synchronizeStrings( File jarFile, File resolveJAR ) throws IOException {
        File tempUnzipDir = new File( FileUtils.getTmpDir(), flavor + "-dir" );
        System.out.println( "tempUnzipDir.getAbsolutePath() = " + tempUnzipDir.getAbsolutePath() );
        tempUnzipDir.mkdirs();

        final Pattern excludePattern = Pattern.compile( quote( phetProject.getName() ) + "[\\\\/]localization[\\\\/]" + quote( phetProject.getName() ) + ".*\\.properties" );
        FileUtils.unzip( jarFile, tempUnzipDir, new FileFilter() {
            public boolean accept( File file ) {
                return !excludePattern.matcher( file.getAbsolutePath() ).find();
            }
        } );

        File localizationDir = new File( tempUnzipDir, phetProject.getName() + File.separator + "localization" );

        localizationDir.mkdir();

        Locale[] locales = phetProject.getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            File source = phetProject.getTranslationFile( locales[i] );
            validateKeySet( locales[i], jarFile, source );
            FileUtils.copyAndClose( new FileInputStream( source ), new FileOutputStream( new File( localizationDir, source.getName() ) ), false );
        }

        FileUtils.jar( tempUnzipDir, resolveJAR );
    }

    private void validateKeySet( Locale locale, File jarFile, File source ) throws IOException {
        File tempDir = new File( FileUtils.getTmpDir(), jarFile.getName() + "_keytest" );
        tempDir.mkdirs();
        FileUtils.unzip( jarFile, tempDir );
        String localeName = locale.getLanguage().equals( "en" ) ? "" : "_" + locale.getLanguage();
        File keysToBeReplaced = new File( tempDir, phetProject.getName() + File.separator + "localization" + File.separator + "/" + phetProject.getName() + "-strings" + localeName + ".properties" );
        HashSet missingKeys = validateKeySet( keysToBeReplaced, source );
        if ( missingKeys.isEmpty() ) {

        }
        else {
            System.out.println( "Project: " + phetProject.getName() + " flavor=" + flavor + ", locale=" + locale + ": New key set is missing some pre-existing keys= " + missingKeys );
        }
    }

    private HashSet validateKeySet( File oldPropertiesFile, File newPropertiesFile ) throws IOException {
        Properties oldProperties = new Properties();
        oldProperties.load( new FileInputStream( oldPropertiesFile ) );

        Properties newProperties = new Properties();
        newProperties.load( new FileInputStream( newPropertiesFile ) );

//        final boolean b = newProperties.keySet().containsAll( oldProperties.keySet() );
//        if (!b){
        HashSet set = new HashSet( oldProperties.keySet() );
        set.removeAll( newProperties.keySet() );

        return set;
//            System.out.println( "missing = " + set );
//        }
//        return b;
    }

    private File downloadJAR() throws IOException {
        String deployUrl = phetProject.getDeployedFlavorJarURL( flavor );

        File jarFile = File.createTempFile( flavor, ".jar" );

        FileDownload.download( deployUrl, jarFile );
        return jarFile;
    }

    //http://www.exampledepot.com/egs/java.util.regex/Escape.html
    public static String quote( String name ) {
        if ( name.toLowerCase().indexOf( "\\e" ) > 0 ) {
            throw new RuntimeException( "Quote method will fail" );
        }
        return "\\Q" + name + "\\E";
    }

}

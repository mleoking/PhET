package edu.colorado.phet.build.translate;

import java.io.*;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;

/**
 * Under development and untested.
 *
 * Utility to determine the discrepancy between a set of deployed localizations (files and keys) and local (repository) localizations.
 */
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

    public void resolve( String username, boolean addNewOnly ) {
        try {
            File resolveJAR = new File( CheckTranslations.TRANSLATIONS_TEMP_DIR, flavor + "_resolved" + System.currentTimeMillis() + ".jar" );
            resolve( resolveJAR, username, addNewOnly );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void resolve( File resolveJAR, String username, boolean addNewOnly ) throws IOException {
        if ( !extraLocal.isEmpty() || !extraRemote.isEmpty() ) {
            File jarFile = downloadJAR();
            boolean changed = synchronizeStrings( jarFile, resolveJAR, addNewOnly );
        }
        else {
            System.out.println( "no resolution needed for: " + phetProject.getName() + ": " + flavor );
        }
    }

    private boolean synchronizeStrings( File jarFile, File resolveJAR, final boolean addNewOnly ) throws IOException {
        boolean changed = false;
        File tempUnzipDir = new File( CheckTranslations.TRANSLATIONS_TEMP_DIR, flavor + "-dir" );
//        System.out.println( "tempUnzipDir.getAbsolutePath() = " + tempUnzipDir.getAbsolutePath() );
        tempUnzipDir.mkdirs();

        final Pattern excludePattern = Pattern.compile( quote( phetProject.getName() ) + "[\\\\/]localization[\\\\/]" + quote( phetProject.getName() ) + ".*\\.properties" );
        FileUtils.unzip( jarFile, tempUnzipDir, new FileFilter() {
            public boolean accept( File file ) {
                return addNewOnly || !excludePattern.matcher( file.getAbsolutePath() ).find();
            }
        } );

        File localizationDir = new File( tempUnzipDir, phetProject.getName() + File.separator + "localization" );

        localizationDir.mkdir();

        Locale[] locales = phetProject.getLocales();
        for ( int i = 0; i < locales.length; i++ ) {
            File source = phetProject.getTranslationFile( locales[i] );
            validateKeySet( locales[i], jarFile, source );
        }

        FileUtils.jar( tempUnzipDir, resolveJAR );
        return changed || !addNewOnly;
    }

    private void validateKeySet( Locale locale, File jarFile, File source ) throws IOException {
        File tempDir = new File( CheckTranslations.TRANSLATIONS_TEMP_DIR, jarFile.getName() + "_keytest" );
        tempDir.mkdirs();
        FileUtils.unzip( jarFile, tempDir );
        String localeName = locale.getLanguage().equals( "en" ) ? "" : "_" + locale.getLanguage();
        File keysToBeReplaced = new File( tempDir, phetProject.getName() + File.separator + "localization" + File.separator + "/" + phetProject.getName() + "-strings" + localeName + ".properties" );
        HashSet missingKeys = getMissingKeySet( keysToBeReplaced, source );
        if ( missingKeys.isEmpty() ) {

        }
        else {
            System.out.println( "Project: " + phetProject.getName() + " flavor=" + flavor + ", locale=" + locale + ": New key set is missing some pre-existing keys= " + missingKeys );
        }
    }

    private HashSet getMissingKeySet( File oldPropertiesFile, File newPropertiesFile ) throws IOException {
        if ( !oldPropertiesFile.exists() || !newPropertiesFile.exists() ) {
//            System.out.println( "Cannot compare properties files, one does not exist: old=" + oldPropertiesFile.getAbsolutePath() + ", new=" + newPropertiesFile.getAbsolutePath() );
            return new HashSet();
        }
        Properties oldProperties = new Properties();
        oldProperties.load( new FileInputStream( oldPropertiesFile ) );

        Properties newProperties = new Properties();
        newProperties.load( new FileInputStream( newPropertiesFile ) );

        HashSet set = new HashSet( oldProperties.keySet() );
        set.removeAll( newProperties.keySet() );

        return set;
    }

    private File downloadJAR() throws IOException {
        String deployUrl = phetProject.getDeployedFlavorJarURL( flavor );

//        File jarFile = File.createTempFile( flavor, ".jar" );
        File jarFile = new File( CheckTranslations.TRANSLATIONS_TEMP_DIR, flavor + ".jar" );

        FileUtils.download( deployUrl, jarFile );
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

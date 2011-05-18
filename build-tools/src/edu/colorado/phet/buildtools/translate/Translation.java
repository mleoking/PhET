package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.flex.FlexSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.flashlauncher.util.XMLUtils;

/**
 * Handles information pertaining to a particular translation
 */
public class Translation {

    // the file where the translation is stored
    private File file;
    private File trunk;

    // types of translations TODO: consider enumeration instead
    public static final String TRANSLATION_JAVA = "java";
    public static final String TRANSLATION_FLASH = "flash";

    public Translation( File translationFile, File trunk ) {
        this.file = translationFile;
        this.trunk = trunk;
    }

    /**
     * @return does this appear to be an actual translation?
     */
    public boolean isValid() {
        return ( getProjectName() != null && getType() != null );
    }

    public File getFile() {
        return file;
    }

    public String getProjectName() {
        String projectName = null;
        final int index = file.getName().indexOf( "-strings_" );
        if ( index != -1 ) {
            projectName = file.getName().substring( 0, index );
        }
        else {
            System.out.println( "warning: " + getFile().getName() + " does not match the translation file pattern of -strings_" );
        }

        // if strings for these sims somehow appear, they are not to be thought of as valid!!!
        if ( projectName != null && ( projectName.equals( "java-common-strings" ) || projectName.equals( "flash-common-strings" ) ) ) {
            projectName = null;
        }

        return projectName;
    }

    /**
     * @return is this a common-strings translation? (IE not a simulation translation)
     */
    public boolean isCommonTranslation() {
        boolean ret = false;

        String name = file.getName();

        if ( isJavaTranslation() ) {
            ret = name.startsWith( "phetcommon-strings_" );
        }
        else if ( isFlashTranslation() ) {
            ret = name.startsWith( "common-strings_" );
        }

        return ret;
    }

    /**
     * @return is this a sim translation? (IE not a common-string translation)
     */
    public boolean isSimulationTranslation() {
        return isValid() && !isCommonTranslation();
    }

    public Locale getLocale() {
        Locale ret = null;
        String search = "-strings_";
        int index = file.getName().indexOf( search );

        if ( index != -1 ) {
            index += search.length();
            String localeString = file.getName().substring( index, file.getName().indexOf( ".", index ) );
            ret = LocaleUtils.stringToLocale( localeString );
        }

        return ret;
    }

    /**
     * @return is it a translation for Java (sim or common)?
     */
    public boolean isJavaTranslation() {
        String type = getType();
        return type != null && type.equals( TRANSLATION_JAVA );
    }

    /**
     * @return is it a translation for Flash (sim or common)? Will respond to "true" for Flex simulations also
     */
    public boolean isFlashTranslation() {
        String type = getType();
        return type != null && type.equals( TRANSLATION_FLASH );
    }

    /**
     * @return Whether this translation is for a flex simulation. If this is true, isFlashTranslation() should be true
     */
    public boolean isFlexTranslation() {
        return new File( trunk, BuildToolsPaths.FLEX_SIMULATIONS_DIR + "/" + getProjectName() ).exists();
    }

    /**
     * return the type of the translation.
     * generally, use isJavaTranslation() or isFlashTranslation() instead.
     * this may return null!
     *
     * @return
     */
    public String getType() {
        String filename = file.getName();

        if ( filename.endsWith( ".properties" ) ) {
            return TRANSLATION_JAVA;
        }
        else if ( filename.endsWith( ".xml" ) ) {
            return TRANSLATION_FLASH;
        }

        return null;
    }

    /**
     * convenience function for where simulations are stored
     */
    private File getSimulationsDir( File trunk ) {
        if ( isJavaTranslation() ) {
            return new File( trunk, BuildToolsPaths.JAVA_SIMULATIONS_DIR );
        }
        else if ( isFlashTranslation() ) {
            if ( isFlexTranslation() ) {
                return new File( trunk, BuildToolsPaths.FLEX_SIMULATIONS_DIR );
            }
            else {
                return new File( trunk, BuildToolsPaths.FLASH_SIMULATIONS_DIR );
            }
        }

        return null;
    }

    /**
     * @return A new copy of the project corresponding to this translation
     */
    public PhetProject getProject( File trunk ) throws IOException {
        String projectName = getProjectName();

        if ( isJavaTranslation() ) {
            return new JavaSimulationProject( new File( getSimulationsDir( trunk ), projectName ) );
        }
        else if ( isFlashTranslation() ) {
            // check for the existence of the project under the flex sim root. if we find it, assume we are a flex project
            if ( isFlexTranslation() ) {
                return new FlexSimulationProject( new File( getSimulationsDir( trunk ), projectName ) );
            }
            else {
                return new FlashSimulationProject( new File( getSimulationsDir( trunk ), projectName ) );
            }
        }

        return null;
    }

    public String toString() {
        if ( isValid() ) {
            return file.getName() + " (" + getProjectName() + ", " + getType() + ", " + LocaleUtils.localeToString( getLocale() ) + ")";
        }
        else {
            return file.getName() + " (possibly invalid)";
        }
    }

    /**
     * @return A set of translation keys that are in the translation file
     */
    public Set<String> getTranslationKeys() {
        Set<String> ret = new HashSet<String>();

        try {

            if ( getType().equals( TRANSLATION_JAVA ) ) {
                Properties properties = new Properties();
                FileInputStream in = new FileInputStream( getFile() );
                try {
                    properties.load( in );
                    Enumeration<?> propertyNames = properties.propertyNames();
                    while ( propertyNames.hasMoreElements() ) {
                        ret.add( (String) propertyNames.nextElement() );
                    }
                }
                finally {
                    in.close();
                }
            }
            else if ( getType().equals( TRANSLATION_FLASH ) ) {
                Document document = XMLUtils.toDocument( edu.colorado.phet.common.phetcommon.util.FileUtils.loadFileAsString( getFile() ) );
                NodeList strings = document.getElementsByTagName( "string" );

                for ( int i = 0; i < strings.getLength(); i++ ) {
                    ret.add( ( (Element) strings.item( i ) ).getAttribute( "key" ) );
                }
            }
            else {
                throw new RuntimeException( "Unknown type of translation: " + getFile().getAbsolutePath() );
            }
        }
        catch ( IOException e ) {
            throw new RuntimeException( "translation key problem in " + getFile().getAbsolutePath(), e );
        }
        catch ( ParserConfigurationException e ) {
            throw new RuntimeException( "translation key problem in " + getFile().getAbsolutePath(), e );
        }
        catch ( TransformerException e ) {
            throw new RuntimeException( "translation key problem in " + getFile().getAbsolutePath(), e );
        }
        return ret;
    }

}

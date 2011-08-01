// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.preprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

import static edu.colorado.phet.common.phetcommon.util.FileUtils.filter;
import static java.lang.String.format;

/**
 * Automatically generates resource files for strings provided in the English translation file, and for images in the image directory.
 * This uses the template in trunk/build-tools/templates/ResourceTemplate.java and outputs in the java source path of the specified simulation
 * for use at compile time.  Guarantees that no access is attempted for non-existing strings or images, and helps you identify unused images and strings.
 * Another advantage is that it defines and enforces a convention for string key names.
 * See #2967
 *
 * @author Sam Reid
 */
public class ResourceGenerator {

    //Template used to generate the resource .java files, see trunk/build-tools/templates/ResourceTemplate.java
    public String template;

    //Use whitespace instead of tab character to match PhetStyle IntelliJ code format convention
    private static final String TAB = "    ";
    private File trunk;

    public ResourceGenerator( File trunk ) {
        this.trunk = trunk;
        //Load the template file
        try {
            template = FileUtils.loadFileAsString( new File( trunk, "build-tools/templates/ResourceTemplate.java" ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    //Generate resources for the simulation specified with a path relative to trunk
    public void generateResources( final String simPath ) throws IOException {
        generateResources( new File( trunk, simPath ) );
    }

    //Generate resources for the simulation at the specified absolute location
    //Return true if the file contents changed
    public boolean generateResources( final File simDir ) throws IOException {
        final File englishFile = new File( simDir, "data/" + simDir.getName() + "/localization/" + simDir.getName() + "-strings.properties" );

        //Load and sort the english string keys alphabetically
        final ArrayList<String> list = new ArrayList<String>( new Properties() {{load( new FileReader( englishFile ) );}}.stringPropertyNames() ) {{
            Collections.sort( this );
        }};

        //Identify the simulation name and package name based on the sim name. For instance:
        //sim name circuit-construction-kit
        //package name: circuitconstructionkit
        //class name: CircuitConstructionKit
        final ArrayList<String> words = new ArrayList<String>() {{
            StringTokenizer st = new StringTokenizer( simDir.getName(), "-" );
            while ( st.hasMoreElements() ) {
                add( st.nextToken() );
            }
        }};
        final String packagename = new StringBuilder() {{
            for ( String word : words ) {
                append( word );
            }
        }}.toString();

        final String className = new StringBuilder() {{
            for ( String word : words ) {
                append( upperWord( word ) );
            }
        }}.toString();

        //Create the lines that eagerly load strings from the translation file
        final String strings = new StringBuilder() {{
            for ( String propertyName : list ) {
                if ( propertyName.equals( simDir.getName() + ".name" ) ) {
                    //Skip the simulation name, it is loaded through reflection
                }
                else if ( isFlavorName( simDir, propertyName ) ) {
                    //Also skip the flavor names they are loaded with reflection
                }
                else {
                    final String JAVA_FIELD_NAME = splitCamelCase( propertyName ).
                            replace( ' ', '-' ).
                            toUpperCase().
                            replace( '.', '_' ).//Careful, not invertible
                            replace( '-', '_' );
                    append( TAB + TAB + "public static final String " + JAVA_FIELD_NAME + " = RESOURCES.getLocalizedString( \"" + propertyName + "\" );\n" );
                }
            }
        }}.toStringWithoutLastCharacter();

        //Find the image files under the data/simname/images directory
        final File[] imageFiles = new File( simDir, "data/" + simDir.getName() + "/images" ).listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return !name.equals( "license.txt" ) && !name.equals( ".svn" );
            }
        } );

        //Add lines to the java resource file that will eagerly load the images
        final String images = new StringBuilder() {{
            for ( File imageFile : imageFiles ) {
                String JAVA_FIELD_NAME = imageFile.getName().
                        substring( 0, imageFile.getName().lastIndexOf( '.' ) ).
                        replace( '-', '_' ).
                        toUpperCase();
                append( TAB + TAB + "public static final BufferedImage " + JAVA_FIELD_NAME + " = RESOURCES.getImage( \"" + imageFile.getName() + "\" );\n" );
            }
        }}.toStringWithoutLastCharacter();

        //Construct the class name for the generated file
        final String fullClassName = className + "Resources";

        //Filter the template to create the .java source file
        String resourceFileString = filter( new HashMap<String, String>() {{
                                                put( "packagename", packagename );
                                                put( "classname", className );
                                                put( "simname", simDir.getName() );
                                                put( "generator", ResourceGenerator.class.getName() );
                                                put( "strings", strings );
                                                put( "fullclassname", fullClassName );
                                                put( "images", images );
                                            }}, template ).trim();

        //Store the filtered strings in the java source directory for usage at compile time
        final File destination = new File( simDir, "src/edu/colorado/phet/" + packagename + "/" + fullClassName + ".java" );

        final String originalValue = destination.exists() ? FileUtils.loadFileAsString( destination ) : "";
        if ( !originalValue.equals( resourceFileString ) ) {
            FileUtils.writeString( destination, resourceFileString );
            //Log that the file was written, and list the path so the user can easily find it.
            System.out.println( "Automatically generated resource file changed, wrote to: " + destination.getAbsolutePath() );

            //Signify changes
            return true;
        }
        else {
            System.out.println( "No changes to automatically generated resource file, skipping generation" );
            //Signify no changes
            return false;
        }
    }

    //Checks if the key is flavor name key, if so it is suppressed from generation since
    //that string is loaded automatically
    private boolean isFlavorName( final File simDir, String propertyName ) {
        //Make sure the string matches the flavor name format
        if ( propertyName.endsWith( ".name" ) ) {

            //Identify the flavor name (if it is a flavor)
            String possibleFlavorName = propertyName.substring( 0, propertyName.lastIndexOf( '.' ) );
            String simName = simDir.getName();
            final File buildPropertiesFile = new File( simDir, simName + "-build.properties" );

            //Load the build properties file to see if it has a matching flavor name
            Properties p = new Properties() {{
                try {
                    load( new FileInputStream( buildPropertiesFile ) );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }};
            //If there was a matching key with this form, then exclude from the generated strings file
            if ( p.containsKey( "project.flavor." + possibleFlavorName + ".mainclass" ) ) {
                return true;
            }
        }
        return false;
    }

    // Tokenizes a string that is in camel case representation
    // Copied from http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    public static String splitCamelCase( String s ) {
        return s.replaceAll( format( "%s|%s|%s",
                                     "(?<=[A-Z])(?=[A-Z][a-z])",
                                     "(?<=[^A-Z])(?=[A-Z])",
                                     "(?<=[A-Za-z])(?=[^A-Za-z])" ), " " );
    }

    //Convenience subclass for generating strings with double brace initialization
    private static class StringBuilder {
        String s = "";

        public void append( String string ) {
            s = s + string;
        }

        public String toString() {
            return s;
        }

        //Returns the string excluding the last character for strings that were appended with a separator that shouldn't appear at the end
        public String toStringWithoutLastCharacter() {
            if ( s.length() == 0 ) {
                return "";
            }
            else {
                return s.substring( 0, s.length() - 1 );
            }
        }
    }

    //Uppercases the first letter of the specified word, for use in creating the class name for a multi-word sim name.
    private String upperWord( String word ) {
        return ( "" + word.charAt( 0 ) ).toUpperCase() + word.substring( 1 );
    }

    //Run the resource generator on the specified simulation, populate the template and output it in the simulation source directory as a .java file for use at compilation time.
    public static void main( String[] args ) throws IOException {
        if ( args.length == 0 ) {
            System.out.println( "Usage:\n" +
                                "args[0] = trunk\n" +
                                "args[1] = relative path to simulation directory from trunk" );
        }
        new ResourceGenerator( new File( args[0] ) ).generateResources( args[1] );
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildtools.preprocessor;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

import static edu.colorado.phet.common.phetcommon.util.FileUtils.filter;
import static java.lang.String.format;

/**
 * Automatically generates resource files for strings provided in the English translation file, and for images in the image directory.
 *
 * @author Sam Reid
 */
public class ResourceGenerator {
    public static void main( String[] args ) throws IOException {
        if ( args.length == 0 ) {
            System.out.println( "Usage: args[0] = full path to simulation directory" );
        }
        File simDir = new File( args[0] );
        new ResourceGenerator().generateResources( simDir );
    }

    //Use whitespace instead of tab character to match PhetStyle IntelliJ code format convention
    private static final String TAB = "    ";

    private void generateResources( final File simDir ) throws IOException {
        final File englishFile = new File( simDir, "data/" + simDir.getName() + "/localization/" + simDir.getName() + "-strings.properties" );
        final Properties properties = new Properties() {{load( new FileReader( englishFile ) );}};
        final ArrayList<String> list = new ArrayList<String>( properties.stringPropertyNames() );
        Collections.sort( list );

        final ArrayList<String> words = new ArrayList<String>() {{
            StringTokenizer st = new StringTokenizer( simDir.getName(), "-" );
            while ( st.hasMoreElements() ) {
                add( st.nextToken() );
            }
        }};
        final String packagename = new MyStringBuilder() {{
            for ( String word : words ) {
                append( word );
            }
        }}.toString();

        final String className = new MyStringBuilder() {{
            for ( String word : words ) {
                append( upperWord( word ) );
            }
        }}.toString();

        final String strings = new MyStringBuilder() {{
            for ( String propertyName : properties.stringPropertyNames() ) {
                if ( propertyName.equals( simDir.getName() + ".name" ) ) {
                    //Skip the simulation name, it is loaded through reflection
                }
                else {
                    final String JAVA_FIELD_NAME = splitCamelCase( propertyName ).
                            replace( ' ', '-' ).
                            toUpperCase().
                            replace( '.', '_' ).//Careful, not invertible
                            replace( '-', '_' );
                    append( TAB + "public static final String " + JAVA_FIELD_NAME + " = RESOURCES.getLocalizedString( \"" + propertyName + "\" );\n" );
                }
            }
        }}.toString();
        final String fullClassName = className + "Resources";

        final File[] imageFiles = new File( simDir, "data/" + simDir.getName() + "/images" ).listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return !name.equals( "license.txt" ) && !name.equals( ".svn" );
            }
        } );

        final String images = new MyStringBuilder() {{
            for ( File imageFile : imageFiles ) {
                String JAVA_FIELD_NAME = imageFile.getName().
                        substring( 0, imageFile.getName().lastIndexOf( '.' ) ).
                        replace( '-', '_' ).
                        toUpperCase();
                append( TAB + "public static final BufferedImage " + JAVA_FIELD_NAME + " = RESOURCES.getImage( \"" + imageFile.getName() + "\" );\n" );
            }
        }}.toString();

        String filtered = filter( new HashMap<String, String>() {{
                                      put( "packagename", packagename );
                                      put( "classname", className );
                                      put( "simname", simDir.getName() );
                                      put( "generator", getClass().getName() );
                                      put( "strings", strings );
                                      put( "fullclassname", fullClassName );
                                      put( "images", images );
                                  }}, template ).trim();
        System.out.println( "filtered = \n" + filtered );

        //Store the filtered strings in the resources file
        final File destination = new File( simDir, "src/edu/colorado/phet/" + packagename + "/" + fullClassName + ".java" );
        FileUtils.writeString( destination, filtered );

        System.out.println( "Wrote to dest: " + destination.getAbsolutePath() );
    }

    //Copied from http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
    public static String splitCamelCase( String s ) {
        return s.replaceAll( format( "%s|%s|%s",
                                     "(?<=[A-Z])(?=[A-Z][a-z])",
                                     "(?<=[^A-Z])(?=[A-Z])",
                                     "(?<=[A-Za-z])(?=[^A-Za-z])" ), " " );
    }

    class MyStringBuilder {
        String s = "";

        public void append( String string ) {
            s = s + string;
        }

        public String toString() {
            return s;
        }
    }

    private String upperWord( String word ) {
        return ( "" + word.charAt( 0 ) ).toUpperCase() + word.substring( 1 );
    }

    public static final String template = "// Copyright 2002-2011, University of Colorado\n" +
                                          "package edu.colorado.phet.@packagename@;\n" +
                                          "\n" +
                                          "import java.awt.image.BufferedImage;\n" +
                                          "\n" +
                                          "import edu.colorado.phet.common.phetcommon.resources.PhetResources;\n" +
                                          "\n" +
                                          "/**\n" +
                                          " * Resources (images and translated strings) for \"@classname@\" are loaded eagerly to make sure everything exists on sim startup, see #2967.\n" +
                                          " * Automatically generated by @generator@\n" +
                                          " * See #2967\n" +
                                          " */\n" +
                                          "public class @fullclassname@ {\n" +
                                          TAB + "public static final String NAME = \"@simname@\";\n" +
                                          TAB + "public static final PhetResources RESOURCES = new PhetResources( NAME );\n" +
                                          "\n" +
                                          TAB + "//Strings\n" +
                                          "@strings@" +
                                          "\n" +
                                          TAB + "//Images\n" +
                                          "@images@" +
                                          "}";
}

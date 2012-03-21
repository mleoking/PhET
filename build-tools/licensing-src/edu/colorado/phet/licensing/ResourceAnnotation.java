package edu.colorado.phet.licensing;

import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.util.AnnotationParser;

/**
 * Object-oriented representation of one line in a resource annotation file.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ResourceAnnotation implements ResourceAnnotationElement {

    /**
     * A resource annotation consists of key-value pairs.
     * These are the supported keys, not all are required.
     * To add a new key: add a constant, document it, add it to KEYS, and create a get method.
     */
    private static final String SOURCE_KEY = "source"; // where we found the resource (organization, URL, etc.)
    private static final String AUTHOR_KEY = "author"; // person or organization that created the resource
    private static final String LICENSE_KEY = "license"; // the name of license, see PhetRuleSet for a list of recognized licenses
    private static final String NOTES_KEY = "notes"; // any misc notes that you want to include
    private static final String LICENSEFILE_KEY = "licensefile";  // file that contains the actual license text

    private static final String[] KEYS = { SOURCE_KEY, AUTHOR_KEY, LICENSE_KEY, NOTES_KEY, LICENSEFILE_KEY };

    private String name;
    private HashMap<String, String> values; // maps keys to values

    public ResourceAnnotation( String line ) {
        values = new HashMap<String, String>();
        parse( line );
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return getValue( SOURCE_KEY );
    }

    /**
     * @deprecated This is being misused in ResourceAnnotationList as a hack for documenting things that aren't annotated. This is a bad design.
     */
    public void setSource( String value ) {
        setValue( SOURCE_KEY, value );
    }

    public String getAuthor() {
        return getValue( AUTHOR_KEY );
    }

    public String getLicense() {
        return getValue( LICENSE_KEY );
    }

    public String getNotes() {
        return getValue( NOTES_KEY );
    }

    public String getLicensefile() {
        return getValue( LICENSEFILE_KEY );
    }

    private String getValue( String key ) {
        return values.get( key );
    }

    private void setValue( String key, String value ) {
        values.put( key, value );
    }

    public String toText() {
        String s = name;
        for ( String key : values.keySet() ) {
            s += " " + key + "=" + values.get( key );
        }
        return s;
    }

    private void parse( String line ) {
        try {
            AnnotationParser.Annotation a = AnnotationParser.parse( line.trim() );
            name = a.getId();
            for ( String key : KEYS ) {
                String value = a.get( key );
                if ( value != null ) {
                    setValue( key, a.get( key ) );
                }
            }
        }
        catch ( Exception e ) {
            System.err.println( "Error on line=" + line );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}

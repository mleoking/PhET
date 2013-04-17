// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.util;

import java.util.ArrayList;

/**
 * Validates a target (translated) string by comparing it to a source (English) string.
 * <p>
 * The source string is parsed for an required HTML tags.
 * To be valid, the target string must contain all required HTML tags that are 
 * found in the source string.  Not all HTML markup is required, since things like <br>
 * may need to vary in the target string.  And it's OK if the target is HTML when 
 * the source is not, since the translation may need to break things like labels
 * into multiple lines.
 * <p>
 * Null or zero-length target strings are considered valid, since they will default to
 * the English string in a runtime sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HTMLValidator {
    
    private static final String HTML_BEGIN = "<html>";
    private static final String HTML_END = "</html>";
    
    private final String sourceString;
    private final ArrayList<String> tags;
    
    /**
     * Constructor.
     * @param sourceString source string used to validate target strings
     */
    public HTMLValidator( String sourceString ) {
        this.sourceString = sourceString;
        tags = parseTags( sourceString );
    }
    
    private ArrayList<String>parseTags( String s ) {
        ArrayList<String> tags = new ArrayList<String>();
        if ( s.contains( HTML_BEGIN ) ) {
            tags.add( HTML_BEGIN );
        }
        if ( s.contains( HTML_END ) ) {
            tags.add( HTML_END );
        }
        return tags;
    }
    
    /**
     * Gets the source (English) string used to validate strings.
     * @return
     */
    public String getSourceString() {
        return sourceString;
    }

    /**
     * Validates a specified target string, looking for required HTML tags that are in the source.
     * Returns a list of tags that were missing, or null if all required tags were found.
     * @param targetString
     * @return null (valid) or a list of missing tags (invalid)
     */
    public ArrayList<String> validate( String targetString ) {
        ArrayList<String> missingTags = null;
        if ( targetString != null && targetString.length() > 0 ) {
            for ( String tag : tags ) {
                if ( !targetString.contains( tag ) ) {
                    if ( missingTags == null ) {
                        missingTags = new ArrayList<String>();
                    }
                    missingTags.add( tag );
                }
            }
        }
        return missingTags;
    }
    
    /* test */
    public static void main( String[] args ) {
        HTMLValidator v1 = new HTMLValidator( "<html>foo is bar</html>" );
        HTMLValidator v2 = new HTMLValidator( "foo is bar" );
        System.out.println( v1.validate( "<html>aaa bbb</html>" ) ); // null
        System.out.println( v1.validate( "<html>aaa bbb</html>") ); // null
        System.out.println( v1.validate( "<html>aaa bbb") ); // [</html>]
        System.out.println( v1.validate( "aaa bbb" ) ); // [<html>,</html>]
        System.out.println( v2.validate( "aaa bbb") ); // null
        System.out.println( v2.validate( "<html>aaa bbb</html>") ); // null
    }
}

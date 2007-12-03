/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.IOException;
import java.net.MalformedURLException;

import com.google.api.translate.Translate;

/**
 * AutoTranslator is a collection of static methods for performing translation using Google Translate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AutoTranslator {

    // Describes a mapping between two strings.
    private static class StringMapping {
        public final String from;
        public final String to;
        public StringMapping( String from, String to ) {
            this.from = from;
            this.to = to;
        }
    }
    
    // HTML entities that need to be remapped to their ASCII characters
    private static final StringMapping[] ENTITY_MAPPINGS = {
        new StringMapping( "&#34;", "\"" ),
        new StringMapping( "&quot;", "\"" ),
        new StringMapping( "&#39;", "'" ),
        new StringMapping( "&apos;", "'" ),
        new StringMapping( "&#38;", "&" ),
        new StringMapping( "&amp;", "&" ),
        new StringMapping( "&#60;", "<" ),
        new StringMapping( "&lt;", "<" ),
        new StringMapping( "&#62;", ">" ),
        new StringMapping( "&gt;", ">" )
    };
    
    // HTML tags that get broken by Google translate
    private static final StringMapping[] TAG_MAPPINGS = {
        new StringMapping( "<Html>", "<html>" ),
        new StringMapping( "</ html>", "</html>" ),
        new StringMapping( " <br> ", "<br>" )
    };
    
    /* not intended for instantiation */
    private AutoTranslator() {}
    
    /**
     * Translates a string using Google Translate.
     * 
     * @param value
     * @param sourceCountryCode
     * @param targetCountryCode
     * @return String, possibly null
     */
    public static String translate( String value, String sourceCountryCode, String targetCountryCode ) {
        String s = null;
        try {
            s = Translate.translate( value, sourceCountryCode, targetCountryCode );
            s = applyMappings( s, ENTITY_MAPPINGS ); //XXX expensive!
            s = applyMappings( s, TAG_MAPPINGS ); //XXX expensive!
        }
        catch ( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        return s;
    }
    
    /*
     * Applies mappings to a string.
     * 
     * @param s
     * @param mappings
     * @return a new string with the mappings applied
     */
    private static String applyMappings( String s, StringMapping[] mappings ) {
        String sNew = s;
        if ( s != null && s.length() > 0 ) {
            for ( int i = 0; i < mappings.length; i++ ) {
                sNew = sNew.replaceAll( mappings[i].from, mappings[i].to );
            }
        }
        return sNew;
    }
}

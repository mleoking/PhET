/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.api.translate.Translate;


public class AutoTranslator {

    private static class StringMapping {

        private final String _fromString;
        private final String _toString;

        public StringMapping( String fromString, String toString ) {
            _fromString = fromString;
            _toString = toString;
        }

        public String getFromString() {
            return _fromString;
        }

        public String getToString() {
            return _toString;
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
    
    private AutoTranslator() {}
    
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
    
    private static String applyMappings( String s, StringMapping[] mappings ) {
        String sNew = s;
        if ( s != null && s.length() > 0 ) {
            for ( int i = 0; i < mappings.length; i++ ) {
                sNew = sNew.replaceAll( mappings[i].getFromString(), mappings[i].getToString() );
            }
        }
        return sNew;
    }
}

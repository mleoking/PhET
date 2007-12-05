/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;


/**
 * AutoTranslator is a collection of static methods for performing translation using Google Translate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AutoTranslator {
    
    public static class AutoTranslateException extends Exception {
        
        public AutoTranslateException( String message ) {
            super( message );
        }
        
        public AutoTranslateException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    public static interface IAutoTranslateStrategy {
        public String translate( String text, String sourceLanguageCode, String targetLanguageCode ) throws AutoTranslateException;
    }
    
    private IAutoTranslateStrategy _autoTranslateStrategy;
    
    /**
     * Constructor that uses a default auto-translation strategy.
     */
    public AutoTranslator() {
        this( new GoogleTranslateStrategy() );
    }
    
    /**
     * Constructor that uses a specified auto-translation strategy.
     * 
     * @param autoTranslateStrategy
     */
    public AutoTranslator( IAutoTranslateStrategy autoTranslateStrategy ) {
        _autoTranslateStrategy = autoTranslateStrategy;
    }
    
    /**
     * Translates a string using Google Translate.
     * 
     * @param value
     * @param sourceCountryCode
     * @param targetCountryCode
     * @return String, possibly null
     */
    public String translate( String value, String sourceCountryCode, String targetCountryCode ) {
        String s = null;
        try {
            s = _autoTranslateStrategy.translate( value, sourceCountryCode, targetCountryCode );
        }
        catch ( AutoTranslateException e ) {
            //XXX
            e.printStackTrace();
        }
        return s;
    }
    

}

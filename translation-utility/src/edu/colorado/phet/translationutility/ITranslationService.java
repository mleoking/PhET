/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;


/**
 * ITranslationService is the interface implemented by any automatic translation service.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ITranslationService {
    
    public static class TranslationServiceException extends Exception {
        
        public TranslationServiceException( String message ) {
            super( message );
        }
        
        public TranslationServiceException( String message, Throwable cause ) {
            super( message, cause );
        }
    }

    public String translate( String text, String sourceLanguageCode, String targetLanguageCode ) throws TranslationServiceException;

}

/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.autotranslate;


/**
 * ITranslationService is the interface implemented by any automatic translation service.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ITranslationService {
    
    /**
     * TranslationServiceException is thrown if errors are encountered during translation.
     * Each ITranslationService implementation may encounter different types of errors,
     * all of which will be mapped to TranslationServiceException.
     */
    public static class TranslationServiceException extends Exception {
        
        public TranslationServiceException( String message ) {
            super( message );
        }
        
        public TranslationServiceException( String message, Throwable cause ) {
            super( message, cause );
        }
    }

    /**
     * Translates text from a source language to a target language.
     * @param text
     * @param sourceLanguageCode
     * @param targetLanguageCode
     * @return translated text
     * @throws TranslationServiceException
     */
    public String translate( String text, String sourceLanguageCode, String targetLanguageCode ) throws TranslationServiceException;

}

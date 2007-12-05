package edu.colorado.phet.translationutility.test;

import edu.colorado.phet.translationutility.GoogleTranslateService;
import edu.colorado.phet.translationutility.ITranslationService;

/**
 * Created by: Sam
 * Nov 1, 2007 at 7:28:38 PM
 *
 * Demonstration of api from:
 * http://code.google.com/p/google-api-translate-java/
 */
public class TestGoogleTranslate {
    public static void main( String[] args ) {
        ITranslationService translationService = new GoogleTranslateService();
        try {
            String translatedText = translationService.translate( "cookies", "en", "fr" );
            System.out.println( translatedText );
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
}

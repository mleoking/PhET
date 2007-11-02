package edu.colorado.phet.translationutility.test;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

/**
 * Created by: Sam
 * Nov 1, 2007 at 7:28:38 PM
 *
 * Demonstration of api from:
 * http://code.google.com/p/google-api-translate-java/
 */
public class TestTranslate {
    public static void main( String[] args ) {
        try {
            String translatedText = Translate.translate( "Salut le monde", Language.FRENCH, Language.ENGLISH );
            System.out.println( translatedText );
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
}

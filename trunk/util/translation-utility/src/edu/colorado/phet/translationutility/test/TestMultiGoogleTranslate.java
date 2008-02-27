package edu.colorado.phet.translationutility.test;

import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.translationutility.autotranslate.GoogleTranslateService;
import edu.colorado.phet.translationutility.autotranslate.ITranslationService;
import edu.colorado.phet.translationutility.autotranslate.ITranslationService.TranslationServiceException;

/**
 * Feasibility test for translating a group of strings as a batch instead of making multiple translate requests.
 *
 * Created by: Sam
 * Nov 1, 2007 at 10:30:32 PM
 */
public class TestMultiGoogleTranslate {
    public static String[] translate( String[] toTranslate, String srcLang, String dstLang ) throws IOException {
        
        ITranslationService translationService = new GoogleTranslateService();
        
        StringBuffer str = new StringBuffer();
        for ( int i = 0; i < toTranslate.length; i++ ) {
            String s = toTranslate[i];
            str.append( "(<(" + s + ")>) \n " );
        }
        System.out.println( "Requesting translate for: " + str.toString() );
        String translatedText = null;
        try {
            translatedText = translationService.translate( str.toString(), srcLang, dstLang );
        }
        catch ( TranslationServiceException e ) {
            e.printStackTrace();
            System.exit( 1 );
        }

        //Sample output
//        String translatedText = "(&lt;(Hello World)&gt;) <br>  (&lt;(Hello all)&gt;) <br>  (&lt;(The same)&gt;) <br>  (&lt;(Boy)&gt;)";
        System.out.println( "Received token: " + translatedText );

        String prefix = "(&lt;(";
        String suffix = ")&gt;)";
        ArrayList parsed = new ArrayList();
        while ( true ) {
            int start = translatedText.indexOf( prefix );
            int end = translatedText.indexOf( suffix );
            if ( start == -1 || end == -1 ) {
                break;
            }
            String subString = translatedText.substring( start + prefix.length(), end ).trim();
            System.out.println( "subString=" + subString );
            translatedText = translatedText.substring( end + 1 );
            parsed.add( subString );
        }

        if ( toTranslate.length != parsed.size() ) {
            new RuntimeException( "Wrong string count" ).printStackTrace();
        }
        String[] out = new String[toTranslate.length];
        for ( int i = 0; i < out.length; i++ ) {
            out[i] = (String) parsed.get( i );
        }
        return out;
    }

    public static void main( String[] args ) {
        try {
            String[] src = {"Salut le monde", "bonjour tous", " la meme", "garcon"};
            String[] translatedText = translate( src, "fr", "en" );
            for ( int i = 0; i < translatedText.length; i++ ) {
                String s = translatedText[i];
                System.out.println( "src=" + src[i] + " translated to: " + s );
            }
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
}

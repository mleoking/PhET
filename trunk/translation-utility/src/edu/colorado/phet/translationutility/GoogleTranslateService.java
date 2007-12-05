/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * GoogleTranslateService uses Google Translate translate strings.
 * See http://translate.google.com
 * <p>
 * This is based on google-api-translate-java found at:
 * http://code.google.com/p/google-api-translate-java/
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GoogleTranslateService implements ITranslationService {

    private static final String SERVICE_PREFIX = "http://translate.google.com/translate_t?langpair=";
    private static final String TEXT_VAR = "&text=";
    private static final String KEY_USER_AGENT = "User-Agent";
    private static final String VALUE_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 4.01; Windows NT)";
    
    private HashMap _entitiesMap; // HTML entities that need to be remapped to their ASCII characters
    private HashMap _tagsMap; // HTML tags that get broken by Google translate
    
    /**
     * Constructor.
     */
    public GoogleTranslateService() {
        
        _entitiesMap = new HashMap();
        _entitiesMap.put( "&#34;", "\"" );
        _entitiesMap.put( "&quot;", "\"" );
        _entitiesMap.put( "&#39;", "'" );
        _entitiesMap.put( "&apos;", "'" );
        _entitiesMap.put( "&#38;", "&" );
        _entitiesMap.put( "&amp;", "&" );
        _entitiesMap.put( "&#60;", "<" );
        _entitiesMap.put( "&lt;", "<" );
        _entitiesMap.put( "&#62;", ">" );
        _entitiesMap.put( "&gt;", ">" );
        
        _tagsMap = new HashMap();
        _tagsMap.put( "<Html>", "<html>" );
        _tagsMap.put( "</ html>", "</html>" );
        _tagsMap.put( " <br> ", "<br>" );
    }
    
    /**
     * Translates a text string from one language to another.
     * 
     * @param text
     * @param sourceLanguageCode
     * @param targetLanguageCode
     */
    public String translate( String text, String sourceLanguageCode, String targetLanguageCode ) throws TranslationServiceException {

        // Construct the URL for the Google Translate service
        StringBuffer url = new StringBuffer();
        url.append( SERVICE_PREFIX ).append( sourceLanguageCode ).append( '|' ).append( targetLanguageCode );
        String encodedString;
        try {
            encodedString = URLEncoder.encode( text, "UTF-8" );
        }
        catch ( UnsupportedEncodingException e ) {
            throw new TranslationServiceException( "Encoding of URL to UTF-8 failed", e );
        }
        url.append( TEXT_VAR ).append( encodedString );
        String urlString = url.toString();
        
        // Open connection to the service
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL( urlString ).openConnection();
        }
        catch ( MalformedURLException e ) {
            throw new TranslationServiceException( "Failed to open connection because of malformed URL: " + urlString, e );
        }
        catch ( IOException e ) {
            throw new TranslationServiceException( "Failed to open connection because of IOException", e );
        }
        connection.setRequestProperty( KEY_USER_AGENT, VALUE_USER_AGENT );
        
        // Check the response code
        int responseCode;
        try {
            responseCode = connection.getResponseCode();
        }
        catch ( IOException e ) {
            throw new TranslationServiceException( "Failed to read response code from connection", e );
        }
        if ( responseCode != HttpURLConnection.HTTP_OK ) {
            if ( responseCode == HttpURLConnection.HTTP_BAD_REQUEST ) {
                throw new TranslationServiceException( "Google Translate does not support translations for your combination of languages" );
            }
            else {
                String responseMessage = null;
                try {
                    responseMessage = connection.getResponseMessage();
                }
                catch ( IOException e ) {
                    //no need to throw TranslationServiceException here, just continue.
                    e.printStackTrace();
                }
                throw new TranslationServiceException( "Connection to Google Translate failed: " + responseMessage );
            }
        }
        
        // Create a reader for the connection
        InputStream inStream = null;
        try {
            inStream = connection.getInputStream();
        }
        catch ( IOException e ) {
            throw new TranslationServiceException( "Failed to get InputStream from connection", e );
        }
        BufferedReader reader = new BufferedReader( new InputStreamReader( inStream ) );
        
        // Read the result returned by the service
        StringBuffer result = new StringBuffer();
        String line;
        try {
            while ( ( line = reader.readLine() ) != null ) {
                result.append( line );
            }
        }
        catch ( IOException e ) {
            throw new TranslationServiceException( "Failed to read result from connection", e );
        }
        try {
            reader.close();
        }
        catch ( IOException e ) {
            throw new TranslationServiceException( "Failed to close connection", e );
        }
//        System.out.println( "GoogleTranslateStrategy.translate result=" + result );//XXX

        // Parse the result to extract the translated text
        String translatedText = null;
        int startIndex = result.indexOf( "<div id=result_box dir=" );
        if ( startIndex != -1 ) {
            String start = result.substring( startIndex );
            int endIndex = start.indexOf( "</div>" );
            if ( endIndex != -1 ) {
                translatedText = start.substring( 27, endIndex );
            }
        }
        
        // Fix things that were broken by Google
        translatedText = applyMappings( translatedText, _entitiesMap ); //XXX expensive!
        translatedText = applyMappings( translatedText, _tagsMap ); //XXX expensive!
        
        return translatedText;
    }
    
    /*
     * Applies mappings to a string.
     * This is used to fix things that are broken during the translation process.
     * 
     * @param s
     * @param map
     * @return a new string with the mappings applied
     */
    private static String applyMappings( String s, HashMap map ) {
        String sNew = s;
        if ( s != null && s.length() > 0 ) {
            Set keySet = map.keySet();
            Iterator i = keySet.iterator();
            while ( i.hasNext() ) {
                String from = (String) i.next();
                String to = (String) map.get( from );
                sNew = sNew.replaceAll( from, to );
            }
        }
        return sNew;
    }

}

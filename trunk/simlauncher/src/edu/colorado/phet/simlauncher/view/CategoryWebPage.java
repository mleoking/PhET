/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PhetWebPage
 * <p/>
 * A representation of a page from the Phet site. Used to build the Catalog.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CategoryWebPage {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static final String COMMENT_START = "<!--";
    private static final String COMMENT_END = "-->";
    private static final String ANCHOR_TAG_START = "<a";
    private static final String IMG_TAG_START = "<img";
    private static final String HREF_START = "href";
    private static final String SRC_START = "src";
    private static final String ABS_START = "showabstract";
    private static final String TAG_OPEN = "<";
    private static final String TAG_CLOSE = ">";
    private static final String JNLP_TOKEN = ".jnlp";
    private static final String QUOTE_STR = "\"";
    private static final String SINGLE_QUOTE_STR = "\'";
    private static final String EQUALS_STR = "=";
    private static final String JNLP_DIV = "class=\"simulationjnlp\"";
    private static final String NAME_DIV = "class=\"simulationname\"";
    private static final String THUMBNAIL_DIV = "class=\"simulationthumbnail\"";

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Set jnlpsFound = new HashSet();
    private String path;

    public CategoryWebPage( String path ) {
        this.path = path;
    }


    /**
     * Get category specs
     * <p/>
     * Returns a list of CategorySpecs for all simulations in a specified directory
     */
    public List getSimSpecs() {
        List simSpecs = new ArrayList();

        // Download the page
        StringBuffer text = getText( path );

        // Strip all comments
        stripComments( text );

        // Make a copy and turn it to lower case. We'll use this copy to scan for tags, but we'll
        // use the original to get the paths for our resources.
        StringBuffer searchBuffer = new StringBuffer( text.toString().toLowerCase() );

        // Until we've gotten to the end of the file
        boolean simFound = false;
        int searchStart = 0;
        do {
            simFound = false;

            // Scan for JNLP
            int jnlpDivStart = searchBuffer.indexOf( CategoryWebPage.JNLP_DIV, searchStart );
            if( jnlpDivStart > -1 ) {

                // debug
                String s = searchBuffer.substring( jnlpDivStart, searchBuffer.indexOf( CategoryWebPage.TAG_CLOSE, jnlpDivStart ) + 1 );
//                System.out.println( "s = " + s );

                simFound = true;
                searchStart = jnlpDivStart + CategoryWebPage.JNLP_DIV.length();

                // Find the href
                int hrefStart = searchBuffer.indexOf( CategoryWebPage.HREF_START, searchStart );
                if( hrefStart > -1 ) {

                    // If the last token before the closing " is ".jnlp", we have a simulation tag
                    int tagEndPos = searchBuffer.indexOf( CategoryWebPage.TAG_CLOSE, hrefStart );
                    int jnlpPathEnd = searchBuffer.indexOf( CategoryWebPage.JNLP_TOKEN, hrefStart );
                    if( jnlpPathEnd > -1 && jnlpPathEnd < tagEndPos ) {

                        // Get the jnlp path from the original page text. If we haven't seen this one before, proceed
                        // with getting the rest of the requisite attributes.
                        String jnlpPath = getJnlpPath( searchBuffer, hrefStart, text );
                        if( !jnlpsFound.contains( jnlpPath ) ) {
                            jnlpsFound.add( jnlpPath );

                            // Get the name of the simulation
                            String name = getName( searchBuffer, searchStart, text );

                            // Get the thumbnail path from the original page text
                            String thumbnailPath = getThumbnailPath( searchBuffer, jnlpPathEnd, text );

                            // Get the abstract path from the original page text
//                            String abstractPath = getAbstractPath( searchBuffer, hrefStart, tagEndPos, text );

                            // Create a SimSpec and add it to the list
                            simSpecs.add( new CategoryWebPage.SimSpec( name, jnlpPath, thumbnailPath, "" ) );
                        }
                    }
                }
            }
        } while( simFound );

        return simSpecs;
    }

    private String getJnlpPath( StringBuffer searchBuffer, int searchStart, StringBuffer text ) {
        int end = searchBuffer.indexOf( CategoryWebPage.JNLP_TOKEN, searchStart );
        int equalsPos = searchBuffer.indexOf( CategoryWebPage.EQUALS_STR, searchStart );
        String jnlpPath = path + "/../" + text.substring( equalsPos + 2, end + CategoryWebPage.JNLP_TOKEN.length() ).trim();
        return jnlpPath;
    }

    private String getName( StringBuffer searchBuffer, int searchStart, StringBuffer text ) {
        String name = null;
        int divStart = searchBuffer.indexOf( CategoryWebPage.NAME_DIV, searchStart );
        if( divStart > -1 ) {
            int nameStart = searchBuffer.indexOf( CategoryWebPage.TAG_CLOSE, divStart );
            int nameEnd = searchBuffer.indexOf( CategoryWebPage.TAG_OPEN, nameStart + 1 );
            name = text.substring( nameStart + 1, nameEnd );
        }
        return name;
    }

    private String getAbstractPath( StringBuffer searchBuffer, int searchStart, int searchLimit, StringBuffer text ) {
        String abstractPath = null;
        int absStart = searchBuffer.indexOf( CategoryWebPage.ABS_START, searchStart );
        if( absStart > -1 && absStart < searchLimit ) {
            int absPathStart = searchBuffer.indexOf( CategoryWebPage.SINGLE_QUOTE_STR, absStart ) + 1;
            int absPathEnd = searchBuffer.indexOf( CategoryWebPage.SINGLE_QUOTE_STR, absPathStart + 1 );
            abstractPath = path + "/../" + text.substring( absPathStart, absPathEnd );
        }
        return abstractPath;
    }

    private String getThumbnailPath( StringBuffer searchBuffer, int searchStart, StringBuffer text ) {
        String thumbnailPath = null;
        int imgStart = searchBuffer.indexOf( CategoryWebPage.THUMBNAIL_DIV, searchStart );
        if( imgStart > -1 ) {
            int srcStart = searchBuffer.indexOf( CategoryWebPage.SRC_START, imgStart );
            int imgQuotePosA = searchBuffer.indexOf( CategoryWebPage.QUOTE_STR, srcStart );
            int imgQuotePosB = searchBuffer.indexOf( CategoryWebPage.QUOTE_STR, imgQuotePosA + 1 );
            thumbnailPath = path + "/../" + text.substring( imgQuotePosA + 1, imgQuotePosB );
        }
        return thumbnailPath;
    }
//    private String getThumbnailPath( StringBuffer searchBuffer, int searchStart, StringBuffer text ) {
//        String thumbnailPath = null;
//        int imgStart = searchBuffer.indexOf( IMG_TAG_START, searchStart );
//        if( imgStart > -1 ) {
//            int srcStart = searchBuffer.indexOf( SRC_START, imgStart );
//            int imgQuotePosA = searchBuffer.indexOf( QUOTE_STR, srcStart );
//            int imgQuotePosB = searchBuffer.indexOf( QUOTE_STR, imgQuotePosA + 1 );
//            thumbnailPath = path + "/../" + text.substring( imgQuotePosA + 1, imgQuotePosB );
//        }
//        return thumbnailPath;
//    }

    public StringBuffer getText( String urlPath ) {
        StringBuffer textBuffer = new StringBuffer();
        try {
            // Create a URL for the desired page
            URL url = new URL( path );

            // Read all the text returned by the server
            BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
            String str;
            while( ( str = in.readLine() ) != null ) {
                textBuffer.append( str );
                textBuffer.append( "\n" );
            }
            in.close();
        }
        catch( IOException e ) {
            System.out.println( "e = " + e );
        }
        return textBuffer;
    }

    public StringBuffer stripComments( StringBuffer text ) {
        boolean commentFound = false;
        int searchStart = 0;
        do {
            commentFound = false;
            int commentStart = text.indexOf( CategoryWebPage.COMMENT_START, searchStart );
            if( commentStart > -1 ) {
                searchStart = commentStart;
                commentFound = true;
                int commentEnd = text.indexOf( CategoryWebPage.COMMENT_END, commentStart ) + CategoryWebPage.COMMENT_END.length();
                text.replace( commentStart, commentEnd, "" );
            }
        } while( commentFound );
        return text;
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------
    public static class SimSpec {
        private String name;
        private String jnlpPath;
        private String thumbnailPath;
        private String abstractPath;

        public SimSpec( String name, String jnlpPath, String thumbnailPath, String abstractPath ) {
            this.name = name;
            this.jnlpPath = jnlpPath;
            this.thumbnailPath = thumbnailPath;
            this.abstractPath = abstractPath;
        }

        public String getName() {
            return name;
        }

        public String getJnlpPath() {
            return jnlpPath;
        }

        public String getThumbnailPath() {
            return thumbnailPath;
        }

        public String getAbstractPath() {
            return abstractPath;
        }
    }
}

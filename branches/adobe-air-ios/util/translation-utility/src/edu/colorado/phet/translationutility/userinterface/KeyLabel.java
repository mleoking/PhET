// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.translationutility.userinterface;

import javax.swing.JLabel;

/**
 * Label that displays a the lookup key for a string.  
 * If the key exceeds some max length, it will contain an ellipsis in the middle.
 * This keep the user interface usable, and prevents us from dealing with horizontal scrollbars.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class KeyLabel extends JLabel {
    
    private static final int MAX_LENGTH = 40;
    
    public KeyLabel( String key ) {
        super( createLabelText( key ) );
    }

    /*
     * If the key value is exceed some maximum, replace middle chars with an ellipsis.
     */
    private static String createLabelText( String key ) {
        String text = key;
        if ( text.length() > MAX_LENGTH ) {
            String ellipsis = "...";
            String firstPart = key.substring( 0, MAX_LENGTH / 2 );
            String lastPart = key.substring( key.length() - ( MAX_LENGTH / 2 ) + ellipsis.length(), key.length() );
            text = firstPart + ellipsis + lastPart;
            if ( ! ( text.length() <= MAX_LENGTH ) ) {
                System.out.println( "text.length=" + text.length() );
                System.out.println( "firstPart.length=" + firstPart.length() );
                System.out.println( "lastPart.length=" + lastPart.length() );
            }
        }
        assert( text.length() <= MAX_LENGTH );
        return text;
    }
}

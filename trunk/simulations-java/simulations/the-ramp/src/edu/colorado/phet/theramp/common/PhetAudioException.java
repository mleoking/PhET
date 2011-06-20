// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp.common;

/**
 * User: Sam Reid
 * Date: Dec 31, 2005
 * Time: 11:20:21 AM
 */

public class PhetAudioException extends RuntimeException {
    private PhetAudioClip phetAudioClip;

    public PhetAudioException( Exception exception, PhetAudioClip phetAudioClip ) {
        super( PhetAudioClip.class.getName() + " for URL=" + phetAudioClip.getURL(), exception );
        this.phetAudioClip = phetAudioClip;
    }
}
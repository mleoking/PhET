/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.audio;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Sep 21, 2003
 * Time: 12:49:23 PM
 * Copyright (c) Sep 21, 2003 by Sam Reid
 */
public class AudioProxy {
    AudioClip clip;
    URL url;

    public AudioProxy( URL url ) {
        this.url = url;
    }

    public void play() {
        if( clip == null ) {
            clip = Applet.newAudioClip( url );
        }
        if( clip != null ) {
            clip.play();
        }
    }
}

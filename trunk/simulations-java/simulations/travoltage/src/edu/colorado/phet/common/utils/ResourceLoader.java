package edu.colorado.phet.common.utils;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class ResourceLoader {

    public static AudioClip loadAudioClip( String name, Component cmp ) {
        URL fileLoc = findResource( name, cmp );
        if( cmp instanceof Applet ) {
            return ( (Applet)cmp ).getAudioClip( fileLoc );
        }
        else {
            AudioClip clip = Applet.newAudioClip( fileLoc );
            return clip;
        }
    }

    public static URL findResource( String name, Component cmp ) {
        ClassLoader urlLoader = cmp.getClass().getClassLoader();
        URL fileLoc = urlLoader.getResource( name );
        return fileLoc;
    }

    public static BufferedImage loadBufferedImage( String name, Component c, boolean argb ) {
        //URL fileLoc=findResource("travoltage/images/"+name,c);
        URL fileLoc = findResource( name, c );
        try {
            Image img = c.getToolkit().createImage( fileLoc );
            if( argb ) {
                return ImageConverter.toBufferedImageARGB( img, c );
            }
            else {
                return ImageConverter.toBufferedImageRGB( img, c );
            }
        }
        catch( Exception e ) {
            throw new RuntimeException( e.toString() + " -> looked for resource : " + name + ", fileLoc=" + fileLoc );
        }
    }
}

package edu.colorado.phet.common.utils;

import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.net.URLClassLoader;

public class ResourceLoader {
    ClassLoader loader;

    public ResourceLoader( ClassLoader loader ) {
        this.loader = loader;
    }

    public BufferedImage loadBufferedImage( String name ) {
        Component c = new JPanel();
        return loadBufferedImage( name, c, true );
    }

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
        URLClassLoader urlLoader = (URLClassLoader)cmp.getClass().getClassLoader();
        URL fileLoc = urlLoader.findResource( name );
        return fileLoc;
    }

    /*Cut from the Java2D Demo.*/
    public static Image loadResourceImage( String name, Component cmp ) {
        URL fileLoc = findResource( name, cmp );
        //URL fileLoc=findResource("images/"+name,cmp);
        try {
            Image img = cmp.getToolkit().createImage( fileLoc );
            return img;
        }
        catch( Exception e ) {
            throw new RuntimeException( e.toString() + " -> looked for resource : " + name + ", fileLoc=" + fileLoc );
        }
    }

    public static BufferedImage loadBufferedImage( String name, Component c, boolean argb ) {
        //URL fileLoc=findResource("images/"+name,c);
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

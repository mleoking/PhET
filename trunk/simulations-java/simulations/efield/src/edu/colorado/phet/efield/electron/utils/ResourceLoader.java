// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

// Referenced classes of package edu.colorado.phet.efield.electron.utils:
//            ImageConverter

public class ResourceLoader {

    public static URL findResource( String s, Component component ) {
        ClassLoader classloader = component.getClass().getClassLoader();
        URL url = classloader.getResource( s );
        return url;
    }

    public static BufferedImage loadBufferedImage( String s, Component component, boolean flag ) throws InterruptedException {
        URL url = findResource( s, component );
        Image image = component.getToolkit().createImage( url );
        if ( flag ) {
            return ImageConverter.toBufferedImageARGB( image, component );
        }
        return ImageConverter.toBufferedImageRGB( image, component );
//        Exception exception;
//        exception;
//        throw new RuntimeException(exception.toString() + " -> looked for resource : " + s + ", fileLoc=" + url);
    }
}

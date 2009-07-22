package edu.colorado.phet.naturalselection.util;

import java.awt.image.RGBImageFilter;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;
import java.awt.*;
import java.util.HashMap;

import javax.swing.*;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;

public abstract class HighContrastImageFilter extends RGBImageFilter {

    public Image getImage( String name ) {
        if ( cache.containsKey( name ) ) {
            return cache.get( name );
        }
        else {
            Image image = getImage( NaturalSelectionResources.getImage( name ) );
            // trick from PImage.java to make it into a BufferedImage. Otherwise, Piccolo will create many many copies in memory
            image = new ImageIcon( image ).getImage();
            System.out.println( "Adding to high-contrast cache: " + name );
            cache.put( name, image );
            if ( !( image instanceof BufferedImage ) ) {
                System.out.println( "NOT BUFFERED" );
            }
            return image;
        }
    }

    public PImage getPImage( String name ) {
        return new PImage( getImage( name ) );
    }

    public Image getImage( BufferedImage bImage ) {
        ImageProducer producer = new FilteredImageSource( bImage.getSource(), this );
        return Toolkit.getDefaultToolkit().createImage( producer );
    }

    public PImage getPImage( BufferedImage bImage ) {
        return new PImage( getImage( bImage ) );
    }

    private HashMap<String, Image> cache = new HashMap<String, Image>();

    public int compact( int alpha, int red, int green, int blue ) {
        if ( red > 0xff ) {
            red = 0xff;
        }
        if ( green > 0xff ) {
            green = 0xff;
        }
        if ( blue > 0xff ) {
            blue = 0xff;
        }
        return ( alpha << 24 ) + ( red << 16 ) + ( green << 8 ) + blue;
    }

    private static HighContrastImageFilter equator = new Equator();
    private static HighContrastImageFilter whiteBunny = new WhiteBunny();
    private static HighContrastImageFilter brownBunny = new BrownBunny();

    public static HighContrastImageFilter getEquator() {
        return equator;
    }

    public static HighContrastImageFilter getWhiteBunny() {
        return whiteBunny;
    }

    public static HighContrastImageFilter getBrownBunny() {
        return brownBunny;
    }

    private static class Equator extends HighContrastImageFilter {
        public int filterRGB( int x, int y, int rgb ) {
            int alpha = ( rgb & 0xff000000 ) >> 24;
            int red = ( rgb & 0x00ff0000 ) >> 16;
            int green = ( rgb & 0x0000ff00 ) >> 8;
            int blue = ( rgb & 0x000000ff );

            //return compact( alpha, red * 4 / 3, green * 4 / 3, blue * 4 / 3 );
            if ( red >= 119 && red <= 203 && green >= 89 && green <= 152 && blue >= 15 && blue <= 45 ) {
                return compact( alpha, 171, 129, 35 );
            }
            else {
                return rgb;
            }
        }
    }

    private static class WhiteBunny extends HighContrastImageFilter {
        public int filterRGB( int x, int y, int rgb ) {
            int alpha = ( rgb & 0xff000000 ) >> 24;
            int red = ( rgb & 0x00ff0000 ) >> 16;
            int green = ( rgb & 0x0000ff00 ) >> 8;
            int blue = ( rgb & 0x000000ff );

            if ( alpha != 0 && ( red < 0xee || green < 0xee || blue < 0xee ) ) {
                return compact( alpha, 0x00, 0x00, 0x00 );
            }
            else {
                return rgb;
            }
        }
    }

    private static class BrownBunny extends HighContrastImageFilter {
        public int filterRGB( int x, int y, int rgb ) {
            int alpha = ( rgb & 0xff000000 ) >> 24;
            int red = ( rgb & 0x00ff0000 ) >> 16;
            int green = ( rgb & 0x0000ff00 ) >> 8;
            int blue = ( rgb & 0x000000ff );
            if ( alpha != 0 && ( red == 255 && green == 91 && blue == 255 ) ) {
                return rgb;
            }
            else if ( alpha != 0 && ( red != 171 || green != 129 || blue != 35 ) ) {
                return compact( alpha, 0x00, 0x00, 0x00 );
            }
            else {
                return compact( alpha, 203, 152, 45 );
            }
        }
    }
}

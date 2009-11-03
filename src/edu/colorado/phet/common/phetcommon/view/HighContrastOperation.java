package edu.colorado.phet.common.phetcommon.view;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImageOp;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;

import javax.swing.*;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.BufferedLayerUI;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;

public class HighContrastOperation implements BufferedImageOp {
    // TODO: allow tuning of the high contrast filter
    public static JXLayer<JComponent> getLayer( JComponent component ) {
        JXLayer<JComponent> layer = new JXLayer<JComponent>( component );
        BufferedLayerUI<JComponent> bufferedLayerUI = new BufferedLayerUI<JComponent>();
        BufferedImageOpEffect imageOpEffect = new BufferedImageOpEffect( new HighContrastOperation() );
        bufferedLayerUI.setLayerEffects( imageOpEffect );
        layer.setUI( bufferedLayerUI );
        return layer;
    }

    public BufferedImage filter( BufferedImage a, BufferedImage b ) {
        if ( b == null ) {
            b = createCompatibleDestImage( a, a.getColorModel() );
        }
        for ( int x = 0; x < a.getWidth(); x++ ) {
            for ( int y = 0; y < a.getHeight(); y++ ) {
                int rgb = a.getRGB( x, y );
                int alpha = ( rgb & 0xff000000 ) >> 24;
                int red = ( rgb & 0x00ff0000 ) >> 16;
                int green = ( rgb & 0x0000ff00 ) >> 8;
                int blue = ( rgb & 0x000000ff );
                b.setRGB( x, y, compact( alpha, 255 - colorMod( red ), 255 - colorMod( green ), 255 - colorMod( blue ) ) );
            }
        }
        return b;
    }

    private int colorMod( int color ) {
        double scale = 2.0;
        double in = (double) color;
        double max = 255;
        double offset = ( scale - 1 ) * max / 2;
        double out = in * scale - offset;
        int ret = (int) out;
        if ( ret < 0 ) {
            ret = 0;
        }
        if ( ret > 255 ) {
            ret = 255;
        }
        return ret;
    }

    private int compact( int alpha, int red, int green, int blue ) {
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

    public Rectangle2D getBounds2D( BufferedImage bufferedImage ) {
        return bufferedImage.getRaster().getBounds();
    }

    public BufferedImage createCompatibleDestImage( BufferedImage bufferedImage, ColorModel colorModel ) {
        WritableRaster raster;
        raster = colorModel.createCompatibleWritableRaster( bufferedImage.getWidth(), bufferedImage.getHeight() );
        BufferedImage image = new BufferedImage( colorModel, raster, bufferedImage.isAlphaPremultiplied(), null );
        return image;
    }

    public Point2D getPoint2D( Point2D a, Point2D b ) {
        if ( b != null ) {
            b.setLocation( a );
        }
        return a;
    }

    public RenderingHints getRenderingHints() {
        return new RenderingHints( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT );
    }
}

package edu.colorado.phet.atest;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.BufferedLayerUI;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

public class JXLayerTest extends JFrame {

    public JXLayerTest() throws HeadlessException {
        setTitle( "Test" );
        setSize( 700, 300 );

        JPanel content = new JPanel( new GridLayout( 1, 2 ) );

        JXLayer<JComponent> layer = new JXLayer<JComponent>( createPanel( "High-contrast", "With high-contrast filter" ) );
        BufferedLayerUI<JComponent> bufferedLayerUI = new BufferedLayerUI<JComponent>();
        BufferedImageOpEffect imageOpEffect = new BufferedImageOpEffect( new HighContrastOp() );
        bufferedLayerUI.setLayerEffects( imageOpEffect );
        layer.setUI( bufferedLayerUI );


        content.add( createPanel( "Regular", "Without high-contrast filter" ) );
        content.add( layer );

        setContentPane( content );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setVisible( true );
    }

    private static JPanel createPanel( String title, String mess ) {
        JPanel ret = new JPanel();
        ret.setBorder( new TitledBorder( title ) );
        ret.add( new JLabel( mess ) );
        ret.add( new JButton( "This button does nothing" ) );
        JLabel label = new JLabel( "This label has color changes" );
        label.setBackground( Color.RED );
        label.setForeground( Color.GREEN );
        label.setOpaque( true );
        label.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        ret.add( label );
        PCanvas canvas = new PSwingCanvas();

        canvas.setPreferredSize( new Dimension( 100, 100 ) );
        PPath path = PPath.createRectangle( 0, 0, 50, 50 );
        path.setStroke( null );
        path.setPaint( Color.BLACK );
        canvas.getLayer().addChild( path );
        path = PPath.createRectangle( 0, 50, 50, 50 );
        path.setStroke( null );
        path.setPaint( Color.RED );
        canvas.getLayer().addChild( path );
        path = PPath.createRectangle( 50, 0, 50, 50 );
        path.setStroke( null );
        path.setPaint( Color.BLUE );
        canvas.getLayer().addChild( path );
        path = PPath.createRectangle( 50, 50, 50, 50 );
        path.setStroke( null );
        path.setPaint( Color.WHITE );
        canvas.getLayer().addChild( path );
        PText text = new PText( "Piccolo" );
        text.setPaint( Color.WHITE );
        canvas.getLayer().addChild( text );
        JButton pButton = new JButton( "Button" );
        PSwing pswing = new PSwing( pButton );
        pswing.setOffset( 50 - pButton.getWidth() / 2, 75 - pButton.getHeight() / 2 );
        canvas.getLayer().addChild( pswing );
        //pButton.setOpaque( true );
        ret.add( canvas );

        return ret;
    }

    private static class HighContrastOp implements BufferedImageOp {
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
                    b.setRGB( x, y, compact( alpha, 255 - red, 255 - green, 255 - blue ) );
                }
            }
            return b;
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

    public static void main( String[] args ) {
        try {
            System.out.println( "Setting UI to WindowsLookAndFeel" );
            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
        }
        catch( ClassNotFoundException e ) {
            e.printStackTrace();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        new JXLayerTest();
    }
}
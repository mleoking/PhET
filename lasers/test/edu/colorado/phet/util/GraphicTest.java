package edu.colorado.phet.util;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.lasers.controller.LaserConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

/**
 * Class: GraphicTest
 * Package: edu.colorado.phet.util
 * Author: Another Guy
 * Date: Nov 24, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class GraphicTest {
    private static Point2D pr = new Point2D.Double();

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        ApparatusPanel ap = new ApparatusPanel();
        frame.setContentPane( ap );
        ap.setPreferredSize( new Dimension( 600, 600 ) );
        frame.pack();

        TestGraphic pg = new TestGraphic( ap, LaserConfig.PHOTON_IMAGE_FILE, 0 );
        pg.setPosition( 50, 50 );
        ap.addGraphic( pg );

        TestGraphic pg2 = new TestGraphic( ap, LaserConfig.PHOTON_IMAGE_FILE, Math.PI / 2 );
        pg2.setPosition( 100, 100 );
        ap.addGraphic( pg2 );

        TestGraphic pg3 = new TestGraphic( ap, LaserConfig.PHOTON_IMAGE_FILE, Math.PI );
        pg3.setPosition( 150, 150 );
        ap.addGraphic( pg3 );

        TestGraphic pg4 = new TestGraphic( ap, LaserConfig.PHOTON_IMAGE_FILE, Math.PI * 3 / 2 );
        pg4.setPosition( 200, 200 );
        ap.addGraphic( pg3 );

        frame.setVisible( true );

        ap.repaint();
    }

    public static AffineTransform getRotateTx( BufferedImage bImage, double theta ) {
        // Determine the correct point of the image about which to rotate it. If we don't
        // do this correctly, the image will get clipped when it is rotated into certain
        // quadrants
        Point2D pr = new Point2D.Double();
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );
        if( theta >= 0 && theta <= Math.PI / 2 ) {
            pr.setLocation( 0, bImage.getHeight() );
        }
        if( theta > Math.PI / 2 && theta <= Math.PI ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        if( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            pr.setLocation( bImage.getWidth(), 0 );
        }
        if( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        AffineTransform rtx = AffineTransform.getRotateInstance( theta, pr.getX(), pr.getY() );
        return rtx;
    }

    public static BufferedImage getRotatedImage( BufferedImage bImage, double theta ) {
        // Determine the correct point of the image about which to rotate it. If we don't
        // do this correctly, the image will get clipped when it is rotated into certain
        // quadrants
        pr = new Point2D.Double();
        // Normalize theta to be between 0 and PI*2
        theta = ( ( theta % ( Math.PI * 2 ) ) + Math.PI * 2 ) % ( Math.PI * 2 );
        if( theta >= 0 && theta <= Math.PI / 2 ) {
            //            pr.setLocation( bImage.getWidth()/ 2 , bImage.getHeight( )/ 2 );
            pr.setLocation( 0, bImage.getHeight() );
        }
        if( theta > Math.PI / 2 && theta <= Math.PI ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        if( theta > Math.PI && theta <= Math.PI * 3 / 2 ) {
            pr.setLocation( bImage.getWidth(), 0 );
            //            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        if( theta > Math.PI * 3 / 2 && theta <= Math.PI * 2 ) {
            pr.setLocation( bImage.getWidth(), bImage.getHeight() );
        }
        AffineTransform tx = AffineTransform.getTranslateInstance( pr.getX() /* + bImage.getWidth()*/,
                                                                   pr.getY() /*+ bImage.getHeight() / 2*/ );
        tx.rotate( theta );
        tx.translate( -( pr.getX()/*+ bImage.getWidth()*/ ), -( pr.getY()/* + bImage.getHeight() / 2*/ ) );

        AffineTransform rtx = AffineTransform.getRotateInstance( theta, pr.getX(), pr.getY() );
        //        BufferedImageOp op = new AffineTransformOp( tx, AffineTransformOp.TYPE_BILINEAR );
        BufferedImageOp op = new AffineTransformOp( rtx, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage result = op.filter( bImage, null );
        return result;
    }


    static class TestGraphic extends PhetImageGraphic {
        private Point2D pr = new Point2D.Double();
        private AffineTransform rtx;
        Point2D modelLoc = new Point2D.Double();

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            super.paint( g );

            modelLoc.setLocation( getImage().getWidth(),
                                  getImage().getHeight() / 2 );
            modelLoc.setLocation( getPosition().getX() + getImage().getWidth(),
                                  getPosition().getY() + getImage().getHeight() / 2 );
            rtx.transform( modelLoc, modelLoc );

            Rectangle r = this.getBounds();
            g.setColor( Color.green );
            g.draw( r );
            g.setColor( Color.blue );
            Ellipse2D a = new Ellipse2D.Double( modelLoc.getX() - 1, modelLoc.getY() - 1, 5, 5 );
            g.fill( a );

            restoreGraphicsState();
        }

        public TestGraphic( Component component, String imageResourceName, double theta ) {
            super( component, imageResourceName );
            rtx = getRotateTx( getImage(), theta );
            BufferedImageOp op = new AffineTransformOp( rtx, AffineTransformOp.TYPE_BILINEAR );
            BufferedImage result = op.filter( getImage(), null );
            setImage( result );
            //            setTransform( rtx );
        }
    }

}

/**
 * Class: CockpitView
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:52:18 AM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.model.Cockpit;
import edu.colorado.phet.distanceladder.model.PointOfView;
import edu.colorado.phet.distanceladder.controller.CockpitModule;
import edu.colorado.phet.distanceladder.controller.ParallaxButton;
import edu.colorado.phet.distanceladder.controller.PhotometerButton;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

/**
 * This graphic contains the intruments, joystick, and the border around the star view
 */
public class CockpitView extends CompositeInteractiveGraphic implements ImageObserver {

    Point2D.Double photometerButtonLocation = new Point2D.Double( 140, 555 );
    Point2D.Double parallaxButtonLocation = new Point2D.Double( 425, 535 );

    private BufferedImage cockpitImage;
    private BufferedImage joystickBaseImage;
    private BufferedImage joystickControlImage;
    private AffineTransform joystickTx = new AffineTransform();
    private AffineTransform hitTx = new AffineTransform();

    private double joystickLayer = Config.cockpitLayer + 1;
    private Cockpit cockpit;
    private CockpitModule module;
    private AffineTransform atx;

    public CockpitView( Cockpit cockpit, CockpitModule module ) {
        this.cockpit = cockpit;
        this.module = module;
        ImageLoader imgLoader = new ImageLoader();
        try {
            cockpitImage = imgLoader.loadImage( "images/cockpit-view.gif" );
            joystickBaseImage = imgLoader.loadImage( "images/joystick-base.gif" );
            joystickControlImage = imgLoader.loadImage( "images/joystick-control.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        module.getApparatusPanel().setSize( new Dimension( cockpitImage.getWidth(), cockpitImage.getHeight( ) ));


        this.addGraphic( new JoystickGraphic(), joystickLayer );

        // Locates the joystick
        joystickTx.translate( 530, 545 );

        addGraphic( new ParallaxButton( module, parallaxButtonLocation ), joystickLayer );
        addGraphic( new PhotometerButton( module, photometerButtonLocation ), joystickLayer );
        addGraphic( new CockpitGraphic(), Config.cockpitLayer );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }

    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double( 0, 0, cockpitImage.getWidth(), cockpitImage.getHeight() );
    }

    public void paint( Graphics2D g ) {

        Rectangle bounds = module.getApparatusPanel().getBounds();
        double scaleX = bounds.getWidth() / cockpitImage.getWidth();
        atx = AffineTransform.getScaleInstance( scaleX, scaleX );
        AffineTransform orgTx = g.getTransform();
        g.transform( atx );
        super.paint( g );
        g.setTransform( orgTx );
    }


    //
    // Inner classes
    //
    class JoystickGraphic extends DefaultInteractiveGraphic {
        private Point2D.Double testPt = new Point2D.Double();
        private Point2D.Double knobCenter = new Point2D.Double( 40, 10 );
        private Point dragStart;
        private double joystickDx;
        private AffineTransform tx = new AffineTransform( );
        private AffineTransform hitTx = new AffineTransform( );
        private double joystickControlOffsetX = 29;
        private double joystickControlOffsetY = -70;
        private Ellipse2D.Double joystickKnob = new Ellipse2D.Double( );
        private double joystickMovementFactor = 10;

        public JoystickGraphic() {
            super( null, null );
            addCursorHandBehavior();

            Graphic jg = new Graphic() {
                public void paint( Graphics2D g ) {
                    tx.setTransform( joystickTx );
                    double phi = Math.atan( joystickDx / joystickBaseImage.getHeight( ) );
                    g.drawImage( joystickBaseImage, tx, CockpitView.this );
                    tx.translate( joystickControlOffsetX, joystickControlOffsetY );
                    tx.rotate( phi, joystickControlImage.getWidth() / 2, joystickControlImage.getHeight( ) );
                    g.drawImage( joystickControlImage, tx, CockpitView.this );

                    hitTx.setTransform( atx );
                    hitTx.translate( tx.getTranslateX(), tx.getTranslateY() );
                }
            };
            setGraphic( jg );

            Boundary boundary = new Boundary() {
                public boolean contains( int x, int y ) {
                    boolean result = false;
                    testPt.setLocation( x, y );
                    try {
                        hitTx.inverseTransform( testPt, testPt );
                    }
                    catch( NoninvertibleTransformException e ) {
                        e.printStackTrace();
                    }
                    joystickKnob.setFrame( 0, 0, joystickControlImage.getWidth() * hitTx.getScaleX(), joystickControlImage.getWidth() * hitTx.getScaleX() );
                    result = joystickKnob.contains( testPt );
                    return result;
                }
            };
            setBoundary( boundary );
        }

        public void mousePressed( MouseEvent e ) {
            dragStart = e.getPoint();
            dragRef.setLocation( dragStart );
        }

        public void mouseReleased( MouseEvent e ) {
            joystickDx = 0;
            module.getApparatusPanel().repaint( );
        }

        Point2D.Double dragRef = new Point2D.Double();
        public void mouseDragged( MouseEvent e ) {

            // Sometimes dragStart seems to get set to null, especially
            // if the right button happens to be pressed while dragging.
            if( dragStart == null ) {
                mouseReleased( e );
                return;
            }
            joystickDx = e.getPoint().getX() - dragStart.getX();
            double dx = e.getPoint().getX() - dragRef.getX();
            dragRef.setLocation( e.getPoint() );

            PointOfView pov = module.getCockpitPov();
            pov.setLocation( pov.getX() - ( dx / joystickMovementFactor ) * Math.sin( pov.getTheta() ),
                             pov.getY() + ( dx / joystickMovementFactor ) * Math.cos( pov.getTheta() ) );
            module.setPov( pov );
            module.getApparatusPanel().repaint( );
        }
    }


    class CockpitGraphic implements Graphic {
        public void paint( Graphics2D g ) {
            g.drawImage( cockpitImage, 0, 0, CockpitView.this );
        }
    }
}

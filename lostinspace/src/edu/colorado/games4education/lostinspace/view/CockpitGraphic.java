/**
 * Class: CockpitGraphic
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:52:18 AM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.games4education.lostinspace.Config;
import edu.colorado.games4education.lostinspace.controller.CockpitModule;
import edu.colorado.games4education.lostinspace.controller.ParallaxButton;
import edu.colorado.games4education.lostinspace.controller.PhotometerButton;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class CockpitGraphic extends CompositeInteractiveGraphic implements ImageObserver {

    Point2D.Double photometerButtonLocation = new Point2D.Double( 140, 555 );
    Point2D.Double parallaxButtonLocation = new Point2D.Double( 425, 535 );

    private BufferedImage cockpitImage;
    private BufferedImage joystickBaseImage;
    private BufferedImage joystickControlImage;
    private AffineTransform cockpitTx = new AffineTransform();
    private AffineTransform joystickTx = new AffineTransform();

    private double joystickLayer = 10;
    private CockpitModule module;

    public CockpitGraphic( CockpitModule module ) {
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
        this.addGraphic( new JoystickGraphic(), joystickLayer );

        joystickTx.concatenate( cockpitTx );
        joystickTx.translate( 530, 545 );

        addGraphic( new ParallaxButton( module, parallaxButtonLocation ), joystickLayer );
        addGraphic( new PhotometerButton( module, photometerButtonLocation ), joystickLayer );
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        g.fillRect( 0, 0, cockpitImage.getWidth(), cockpitImage.getHeight() );
        g.drawImage( cockpitImage, cockpitTx, this );
        super.paint( g );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }

    //
    // Inner classes
    //
    class JoystickGraphic extends DefaultInteractiveGraphic {
        private Point2D.Double testPt = new Point2D.Double();
        private Point2D.Double knobCenter = new Point2D.Double( 40, 10 );
        private Point dragStart;
        private double joystickDx;

        public JoystickGraphic() {
            super( null, null );
            addCursorHandBehavior();
            Graphic jg = new Graphic() {
                public void paint( Graphics2D g ) {
                    AffineTransform tx = new AffineTransform( joystickTx );
                    double phi = Math.atan( joystickDx / joystickBaseImage.getHeight( ) );
                    g.drawImage( joystickBaseImage, tx, CockpitGraphic.this );
                    tx.translate( 29, -70 );
                    tx.rotate( phi, joystickControlImage.getWidth() / 2, joystickControlImage.getHeight( ) );
                    g.drawImage( joystickControlImage, tx, CockpitGraphic.this );
                }
            };
            setGraphic( jg );

            Boundary boundary = new Boundary() {
                public boolean contains( int x, int y ) {
                    boolean result = false;
                    testPt.setLocation( x, y );
                    try {
                        joystickTx.inverseTransform( testPt, testPt );
                    }
                    catch( NoninvertibleTransformException e ) {
                        e.printStackTrace();
                    }

                    result = testPt.getX() > 25 && testPt.getX() < 25 + joystickControlImage.getWidth()
                             && testPt.getY() > -70 && testPt.getY() < -50;
                    return result;
                }
            };
            setBoundary( boundary );
        }

        public void mousePressed( MouseEvent e ) {
            dragStart = e.getPoint();
        }

        public void mouseReleased( MouseEvent e ) {
            joystickDx = 0;
            module.update();
        }

        public void mouseDragged( MouseEvent e ) {
            double dx = e.getPoint().getX() - dragStart.getX();
            double dy = e.getPoint().getY() - dragStart.getY();
            double gamma = Math.atan( ( 1 * ( dx > 0 ? 1 : -1 ) ) / Config.fixedStarDistance );
            module.changeCockpitPov( 1 * ( dx > 0 ? 1 : -1 ), 0, -gamma );
//            module.changeCockpitPov( 1 * ( dx > 0 ? 1 : -1 ), 1 * ( dy > 0 ? 1 : -1 ), -gamma );

            joystickDx = dx;
        }
    }
}

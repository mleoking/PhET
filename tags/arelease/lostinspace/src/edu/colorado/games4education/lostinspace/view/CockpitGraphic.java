/**
 * Class: CockpitGraphic
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:52:18 AM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class CockpitGraphic extends CompositeInteractiveGraphic implements ImageObserver {

    private BufferedImage cockpitImage;
    private BufferedImage joystickImage;
    private AffineTransform cockpitTx = new AffineTransform( );
    private AffineTransform joystickTx = new AffineTransform( );
    private ApparatusPanel apparatusPanel;

    private double joystickLayer = 10;

    public CockpitGraphic( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
        ImageLoader imgLoader = new ImageLoader();
        try {
            cockpitImage = imgLoader.loadImage( "images/cockpit-view.gif" );
            joystickImage = imgLoader.loadImage( "images/joystick.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        this.addGraphic( new JoystickGraphic(), joystickLayer );

        joystickTx.concatenate( cockpitTx );
        joystickTx.translate( 530, 480 );
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        g.fillRect( 0, 0, cockpitImage.getWidth(), cockpitImage.getHeight() );
        g.drawImage( cockpitImage, cockpitTx, this );
        super.paint( g );
//        joystickTx.translate( 530, 480 );
//        g.drawImage( joystickImage, joystickTx, this );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }

    class JoystickGraphic extends DefaultInteractiveGraphic {
        private Point2D.Double testPt = new Point2D.Double();
        private Point2D.Double knobCenter = new Point2D.Double( 40, 10 );
        private Point dragStart;

        public JoystickGraphic() {
            super( null, null );
            addCursorHandBehavior();
            Graphic jg = new Graphic() {
                public void paint( Graphics2D g ) {
                    g.drawImage( joystickImage, joystickTx, CockpitGraphic.this );
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

                    result = testPt.getX() > 30 && testPt.getX() < 50
                            && testPt.getY() > 0 && testPt.getY() < 20;
                    return result;
                }
            };
            setBoundary( boundary );
        }

        public void mousePressed( MouseEvent e ) {
            dragStart = e.getPoint();
        }

        public void mouseDragged( MouseEvent e ) {
            double dx = e.getPoint().getX() - dragStart.getX();
            double dy = e.getPoint().getY() - dragStart.getY();
        }
    }
}

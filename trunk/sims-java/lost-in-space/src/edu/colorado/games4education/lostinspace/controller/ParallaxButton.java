/**
 * Class: ParallaxButton
 * Class: edu.colorado.games4education.lostinspace.controller
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 9:50:51 PM
 */
package edu.colorado.games4education.lostinspace.controller;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class ParallaxButton extends DefaultInteractiveGraphic {
    private AffineTransform parallaxBtnTx = new AffineTransform();
    private Ellipse2D.Double button;
    private boolean isOn = false;
    private String label = "On";
    private CockpitModule module;

    public ParallaxButton( CockpitModule module, Point2D.Double location ) {
        super( null, null );
        this.module = module;
        parallaxBtnTx.translate( location.getX(), location.getY() );
        button = new Ellipse2D.Double( 0, 0, 30, 20 );
        addCursorHandBehavior();

        Graphic graphic = new Graphic() {
            public void paint( Graphics2D g ) {
                AffineTransform orgTx = g.getTransform();
                g.transform( parallaxBtnTx );
                g.setColor( Color.yellow );
                g.fill( button );
                g.setColor( Color.black );
                g.draw( button );
                FontMetrics fontMetrics = g.getFontMetrics();
                int strWidth = fontMetrics.stringWidth( label );
                g.drawString( label, (int)( button.getWidth() - strWidth ) / 2, 15 );

                g.setTransform( orgTx );
            }
        };
        setGraphic( graphic );

        Boundary bounds = new Boundary() {
            public boolean contains( int x, int y ) {
                Point2D.Double testPt = new Point2D.Double( x, y );
                try {
                    parallaxBtnTx.inverseTransform( testPt, testPt );
                }
                catch( NoninvertibleTransformException e ) {
                    e.printStackTrace();
                }
                return button.contains( testPt );
            }
        };
        setBoundary( bounds );
    }

    public void mouseClicked( MouseEvent e ) {
        System.out.println( "!!!" );
    }

    public void mousePressed( MouseEvent e ) {
        isOn = !isOn;
        module.setParallaxReticleOn( isOn );
        label = isOn ? "Off" : "On";
    }
}

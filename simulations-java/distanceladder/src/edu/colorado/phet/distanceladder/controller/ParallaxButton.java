/**
 * Class: ParallaxButton
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 9:50:51 PM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class ParallaxButton extends DefaultInteractiveGraphic {
    private AffineTransform hitTx = new AffineTransform();
    private AffineTransform buttonTx = new AffineTransform();
    private Ellipse2D.Double button;
    private boolean isOn = false;
    private String label = "On";
    private CockpitModule module;
    Color buttonBorderColorBase = Color.black;
    Color buttonBorderColorRollover = Color.white;
    Color buttonBorderColor = buttonBorderColorBase;
    private Boundary bounds;
    private Point2D trf;

    public ParallaxButton( final CockpitModule module, Point2D.Double location ) {
        super( null, null );
        this.module = module;
        buttonTx.setToTranslation( location.getX(), location.getY() );
        button = new Ellipse2D.Double( 0, 0, 30, 20 );
        addCursorHandBehavior();

        Graphic graphic = new Graphic() {
            public void paint( Graphics2D g ) {
                Object antiAliasingHint = g.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
                GraphicsUtil.setAntiAliasingOn( g );
                AffineTransform orgTx = g.getTransform();
                g.transform( buttonTx );
                g.setColor( Color.red );
                g.fill( button );
                g.setColor( buttonBorderColor );
                g.draw( button );

                Font orgFont = g.getFont();
                Font labelFont = orgFont.deriveFont( Font.BOLD );
                g.setFont( labelFont );
                FontMetrics fontMetrics = g.getFontMetrics();
                int strWidth = fontMetrics.stringWidth( label );
                g.drawString( label, (int)( button.getWidth() - strWidth ) / 2, 15 );

                strWidth = fontMetrics.stringWidth( "Parallax Instrument" );
                g.setColor( Color.black );
                g.drawString( "Parallax Instrument", (int)( button.getWidth() - strWidth ) / 2, -(int)( button.getHeight() ) / 2 );
                g.setFont( orgFont );

                hitTx.setTransform( orgTx );
                hitTx.concatenate( buttonTx );

                g.setTransform( orgTx );
                g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, antiAliasingHint );
            }
        };
        setGraphic( graphic );

        bounds = new Boundary() {
            public boolean contains( int x, int y ) {
                update();
                Point2D.Double testPt = new Point2D.Double( x, y );
                try {
                    trf = hitTx.inverseTransform( testPt, null );
                }
                catch( NoninvertibleTransformException e ) {
                    e.printStackTrace();
                }
                return button.contains( trf );
            }
        };
        setBoundary( bounds );
    }

    private void update() {
        module.getApparatusPanel().repaint();
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
        isOn = !isOn;
        module.setParallaxReticleOn( isOn );
        buttonBorderColor = isOn ? buttonBorderColorRollover : buttonBorderColorBase;
        label = isOn ? "Off" : "On";
    }
}



/**
 * Class: ContainmentGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.nuclearphysics.model.Containment;
import edu.colorado.phet.nuclearphysics.util.DefaultInteractiveGraphic;
import edu.colorado.phet.nuclearphysics.util.Translatable;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class ContainmentGraphic extends CompositePhetGraphic {
    private Containment containment;
    private AffineTransform atx;
    private Rep rep;
    private Stroke outlineStroke = new BasicStroke( 1 );
    private Area mouseableArea;
    private Point lastDragPt;
    private boolean gettingSmaller;

    public ContainmentGraphic( Containment containment, Component component, AffineTransform atx ) {
        super( null );
        this.containment = containment;
        this.atx = atx;
        rep = new Rep( component );
        addGraphic( rep );

        setTransform( atx );
        setCursorHand();
        addTranslationListener( new Translator() );

        addMouseInputListener( new MyMouseInputAdapter( containment, atx ) );
        this.setIgnoreMouse( false );
    }

    protected Rectangle determineBounds() {
        return rep.getBounds();
    }

    public void paint( Graphics2D g2 ) {
        rep.paint( g2 );
    }

    private class Translator implements TranslationListener {

        public void translationOccurred( TranslationEvent translationEvent ) {
            translate( translationEvent.getDx(), translationEvent.getDy() );
        }

        public void translate( double dx, double dy ) {
            dx /= atx.getScaleX();
            dy /= atx.getScaleY();
            double dr = Math.sqrt( dx * dx + dy * dy );
            if( !gettingSmaller ) {
                dr = -dr;
            }
            containment.adjustRadius( dr );
            rep.update();
        }
    }

    private class Rep extends PhetShapeGraphic implements SimpleObserver {
        Ellipse2D outer = new Ellipse2D.Double();
        Ellipse2D inner = new Ellipse2D.Double();
        private Color color = Color.black;
        private Color outlineColor = new Color( 255, 0, 0 );
        private Color backgroundColor;

        Rep( Component component ) {
            super( component, null, null, null );
            containment.addObserver( this );
            backgroundColor = component.getBackground();
            setPaint( color );
//            setStroke( outlineStroke );
//            setBorderColor( outlineColor );
            setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING,
                                                   RenderingHints.VALUE_ANTIALIAS_ON ) );
            update();
        }

        public void update() {
            this.setShape( containment.getArea() );

            double opacity = containment.getOpacity();
            int redLevel = (int)( backgroundColor.getRed() * ( 1 - opacity ) );
            int greenLevel = (int)( backgroundColor.getGreen() * ( 1 - opacity ) );
            int blueLevel = (int)( backgroundColor.getBlue() * ( 1 - opacity ) );
            if( opacity < 1 ) {
                color = new Color( redLevel, greenLevel, blueLevel );
                outlineColor = new Color( 255, greenLevel, blueLevel );
            }
            setBoundsDirty();
            repaint();
        }

    }

    //--------------------------------------------------------------------------------------------------
    // MouseInputAdapter
    //--------------------------------------------------------------------------------------------------

    /**
     * Handles dragsto resize the containment vessel
     */
    private class MyMouseInputAdapter extends MouseInputAdapter {
        private final Containment containment;
        private final AffineTransform atx;

        public MyMouseInputAdapter( Containment containment, AffineTransform atx ) {
            this.containment = containment;
            this.atx = atx;
        }

        // implements java.awt.event.MouseListener
        public void mousePressed( MouseEvent e ) {
            super.mousePressed( e );
            lastDragPt = e.getPoint();
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( MouseEvent e ) {
            Point2D p = new Point2D.Double( containment.getBounds2D().getX() + containment.getBounds2D().getWidth() / 2,
                                            containment.getBounds2D().getY() + containment.getBounds2D().getHeight() / 2 );
            atx.transform( p, p );
            double d1 = p.distance( lastDragPt );
            double d2 = p.distance( e.getPoint() );
            gettingSmaller = ( d2 < d1 );
            lastDragPt = e.getPoint();
        }
    }
}

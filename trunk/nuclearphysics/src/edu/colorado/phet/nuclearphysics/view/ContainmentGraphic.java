/**
 * Class: ContainmentGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Containment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

public class ContainmentGraphic extends DefaultInteractiveGraphic {
    private Containment containment;
    private AffineTransform atx;
    private Rep rep;
    private int strokeWidth = 150;
    private Stroke outlineStroke = new BasicStroke( 1 );
    private Area mouseableArea;
    private int quadrant;

    public ContainmentGraphic( Containment containment, Component component, AffineTransform atx ) {
        super( null );
        this.containment = containment;
        this.atx = atx;
        rep = new Rep( component );
        setBoundedGraphic( rep );
        addCursorHandBehavior();
        addTranslationBehavior( new Translator() );
    }

    private final int QUAD_1 = 1;
    private final int QUAD_2 = 2;
    private final int QUAD_3 = 3;
    private final int QUAD_4 = 4;

    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );
        Point2D p = new Point2D.Double( containment.getBounds2D().getX() + containment.getBounds2D().getWidth() / 2,
                                        containment.getBounds2D().getY() + containment.getBounds2D().getHeight() / 2 );
        atx.transform( p, p );

        // Determine which quadrant the mouse is in. We need to know this so we will know how to resize
        // the graphic. Note that y < 0 in quadrants I and II in Java graphics.
        quadrant = 0;
        if( e.getX() >= p.getX() && e.getY() <= p.getY() ) {
            quadrant = QUAD_1;
        }
        if( e.getX() < p.getX() && e.getY() <= p.getY() ) {
            quadrant = QUAD_2;
        }
        if( e.getX() < p.getX() && e.getY() > p.getY() ) {
            quadrant = QUAD_3;
        }
        if( e.getX() >= p.getX() && e.getY() > p.getY() ) {
            quadrant = QUAD_4;
        }
    }

    //    public void mouseDragged( MouseEvent e ) {
    //        super.mouseDragged( e );
    //        // Determine which side of the box is selected
    //        Point p = e.getPoint();
    //        if( rep.contains( p.x, p.y ) ) {
    //            leftSideDragged = false;
    //            topSideDragged = false;
    //            rightSideDragged = false;
    //            bottomSideDragged = false;
    //            if( p.getX() >= rep.getBounds().getMinX() - strokeWidth / 2
    //                && p.getX() <= rep.getBounds().getMinX() + strokeWidth / 2 ) {
    //                leftSideDragged = true;
    //            }
    //            if( p.getX() >= rep.getBounds().getMaxX() - strokeWidth / 2
    //                && p.getX() <= rep.getBounds().getMaxX() + strokeWidth / 2 ) {
    //                rightSideDragged = true;
    //            }
    //            if( p.getY() >= rep.getBounds().getMinY() - strokeWidth / 2
    //                && p.getY() <= rep.getBounds().getMinY() + strokeWidth / 2 ) {
    //                topSideDragged = true;
    //            }
    //            if( p.getY() >= rep.getBounds().getMaxY() - strokeWidth / 2
    //                && p.getY() <= rep.getBounds().getMaxY() + strokeWidth / 2 ) {
    //                bottomSideDragged = true;
    //            }
    //        }
    //    }

    private class Translator implements Translatable {
        public void translate( double dx, double dy ) {
            dx /= atx.getScaleX();
            dy /= atx.getScaleY();
            double dr = Math.sqrt( dx * dx + dy * dy );
            int sx = 1;
            int sy = 1;
            switch( quadrant ) {
                case QUAD_1:
                    sx *= -1;
                    sy *= 1;
                    break;
                case QUAD_2:
                    sx *= 1;
                    sy *= 1;
                    break;
                case QUAD_3:
                    sx *= 1;
                    sy *= -1;
                    break;
                case QUAD_4:
                    sx *= -1;
                    sy *= -1;
                    break;
            }
            Ellipse2D containmentShape = (Ellipse2D)containment.geShape();
            containmentShape.setFrame( containmentShape.getX() + dx * sx, containmentShape.getY() + dy * sy,
                                       containmentShape.getWidth() - dx * sx * 2, containmentShape.getHeight() - dy * sy * 2 );
            rep.update();
        }
    }

    private class Rep extends PhetShapeGraphic implements SimpleObserver {
        Ellipse2D outer = new Ellipse2D.Double();
        Ellipse2D inner = new Ellipse2D.Double();
        private Stroke stroke = new BasicStroke( strokeWidth );
        private Color color = Color.black;
        private Color outlineColor = new Color( 255, 0, 0 );
        private Color backgroundColor;

        Rep( Component component ) {
            super( component, null, null, null );
            containment.addObserver( this );
            backgroundColor = component.getBackground();

            update();
        }

        public void update() {
            Shape r = containment.geShape();
            outer.setFrame( r.getBounds2D().getMinX() - strokeWidth,
                            r.getBounds2D().getMinY() - strokeWidth,
                            r.getBounds2D().getWidth() + strokeWidth * 2,
                            r.getBounds2D().getHeight() + strokeWidth * 2 );
            inner.setFrame( r.getBounds2D().getMinX(),
                            r.getBounds2D().getMinY(),
                            r.getBounds2D().getWidth(),
                            r.getBounds2D().getHeight() );
            mouseableArea = new Area( outer );
            mouseableArea.subtract( new Area( inner ) );
            this.setShape( atx.createTransformedShape( mouseableArea ) );

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

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( atx );
            g.setColor( color );
            g.setStroke( stroke );
            g.fill( mouseableArea );

            // Draw the neutron source
            RoundRectangle2D gun = new RoundRectangle2D.Double();
            double gunHeight = 40;
            double gunLength = 15;
            gun.setRoundRect( containment.getNeutronLaunchPoint().getX(),
                              containment.getNeutronLaunchPoint().getY() - gunHeight / 2,
                              gunLength, gunHeight, 15, 15 );
            g.setColor( color );
            g.fill( gun );
            g.setStroke( outlineStroke );
            g.setColor( outlineColor );
            g.draw( gun );

            // Outline the vessel in red
            g.setColor( outlineColor );
            g.setStroke( outlineStroke );
            g.draw( mouseableArea );
            restoreGraphicsState();
        }
    }
}

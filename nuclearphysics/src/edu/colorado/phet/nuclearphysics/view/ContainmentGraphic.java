/**
 * Class: ContainmentGraphic
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Oct 6, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.Containment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class ContainmentGraphic extends DefaultInteractiveGraphic {
    private Containment containment;
    private AffineTransform atx;
    private Rep rep;
    private boolean leftSideDragged;
    private boolean rightSideDragged;
    private boolean topSideDragged;
    private boolean bottomSideDragged;
    private int strokeWidth = 100;
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
            int sx = MathUtil.getSign( dx );
            int sy = MathUtil.getSign( dy );
            switch( quadrant ) {
                case QUAD_1:
                    sx *= 1;
                    sy *= -1;
                    break;
                case QUAD_2:
                    sx *= -1;
                    sy *= -1;
                    break;
                case QUAD_3:
                    sx *= -1;
                    sy *= 1;
                    break;
                case QUAD_4:
                    sx *= 1;
                    sy *= 1;
                    break;
            }
            Ellipse2D containmentShape = (Ellipse2D)containment.geShape();
            containmentShape.setFrame( containmentShape.getX() - dr * sx, containmentShape.getY() - dr * sy,
                                       containmentShape.getWidth() + dr * sx * 2, containmentShape.getHeight() + dr * sy * 2 );
            rep.update();
        }
    }

    private class Rep extends PhetShapeGraphic implements SimpleObserver {
        Ellipse2D outer = new Ellipse2D.Double();
        Ellipse2D inner = new Ellipse2D.Double();
        private Stroke stroke = new BasicStroke( strokeWidth );
        private Color color = Color.black;

        Rep( Component component ) {
            super( component, null, null, null );
            containment.addObserver( this );
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

            g.setColor( Color.red );
            g.setStroke( new BasicStroke( 1 ) );
            g.draw( mouseableArea );
            restoreGraphicsState();
        }
    }
}

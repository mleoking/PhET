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
import edu.colorado.phet.nuclearphysics.model.Containment;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class ContainmentGraphic extends DefaultInteractiveGraphic {
    private Containment containment;
    private Rep rep;
    private boolean leftSideDragged;
    private boolean rightSideDragged;
    private boolean topSideDragged;
    private boolean bottomSideDragged;
    private int strokeWidth = 10;

    public ContainmentGraphic( Containment containment, Component component ) {
        super( null );
        this.containment = containment;
        rep = new Rep( component );
        setBoundedGraphic( rep );
        addCursorHandBehavior();
        addTranslationBehavior( new Translator() );
    }

    //    public void mousePressed( MouseEvent e ) {
    public void mouseDragged( MouseEvent e ) {
        super.mouseDragged( e );
        //        super.mousePressed( e );
        Point p = e.getPoint();
        if( rep.contains( p.x, p.y ) ) {
            leftSideDragged = false;
            topSideDragged = false;
            rightSideDragged = false;
            bottomSideDragged = false;
            if( p.getX() >= rep.getBounds().getMinX() - strokeWidth / 2
                && p.getX() <= rep.getBounds().getMinX() + strokeWidth / 2 ) {
                leftSideDragged = true;
            }
            if( p.getX() >= rep.getBounds().getMaxX() - strokeWidth / 2
                && p.getX() <= rep.getBounds().getMaxX() + strokeWidth / 2 ) {
                rightSideDragged = true;
            }
            if( p.getY() >= rep.getBounds().getMinY() - strokeWidth / 2
                && p.getY() <= rep.getBounds().getMinY() + strokeWidth / 2 ) {
                topSideDragged = true;
            }
            if( p.getY() >= rep.getBounds().getMaxY() - strokeWidth / 2
                && p.getY() <= rep.getBounds().getMaxY() + strokeWidth / 2 ) {
                bottomSideDragged = true;
            }
        }
    }

    private class Translator implements Translatable {
        public void translate( double dx, double dy ) {
            Rectangle2D rect = containment.getBounds();
            if( leftSideDragged ) {
                rect.setRect( rect.getMinX() + dx, rect.getMinY(), rect.getWidth() - dx, rect.getHeight() );
            }
            if( rightSideDragged ) {
                rect.setRect( rect.getMinX(), rect.getMinY(), rect.getWidth() + dx, rect.getHeight() );
            }
            if( topSideDragged ) {
                rect.setRect( rect.getMinX(), rect.getMinY() + dy, rect.getWidth(), rect.getHeight() - dy );
            }
            if( bottomSideDragged ) {
                rect.setRect( rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight() + dy );
            }
            rep.update();
        }
    }

    private class Rep extends PhetShapeGraphic implements SimpleObserver {
        private Area mouseableArea;
        Rectangle2D outer = new Rectangle2D.Double();
        Rectangle2D inner = new Rectangle2D.Double();
        private Stroke stroke = new BasicStroke( strokeWidth );
        private Color color = Color.black;

        Rep( Component component ) {
            super( component, null, null, null );
            containment.addObserver( this );
            update();
        }

        public void update() {
            Rectangle2D r = containment.getBounds();

            outer.setRect( r.getMinX() - strokeWidth / 2,
                           r.getMinY() - strokeWidth / 2,
                           r.getWidth() + strokeWidth, r.getHeight() + strokeWidth );
            inner.setRect( r.getMinX() + strokeWidth / 2,
                           r.getMinY() + strokeWidth / 2,
                           r.getWidth() - strokeWidth, r.getHeight() - strokeWidth );
            mouseableArea = new Area( outer );
            mouseableArea.subtract( new Area( inner ) );
            this.setShape( mouseableArea );
            setBoundsDirty();
            repaint();
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( containment.getBounds() );

            g.setColor( Color.red );
            g.setStroke( new BasicStroke( 1 ) );
            g.draw( mouseableArea );
            restoreGraphicsState();
        }
    }
}

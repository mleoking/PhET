/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.movingman.MMTimer;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.DragHandler;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 9:02:00 AM
 * Copyright (c) Jul 1, 2003 by Sam Reid
 */
public class CursorGraphic implements ObservingGraphic, InteractiveGraphic {
    MovingManModule module;
    MMTimer MMTimer;
    private Color color;
//    private BoxToBoxInvertY2 transform;
    ModelViewTransform2D transform;
    int x = 0;
    int width = 8;
    int height;
    int y;
    private DragHandler dragHandler;
//    private BoxToBoxInvertY2 inversion;
    boolean visible = false;
    private Stroke stroke = new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 2, new float[]{6, 4}, 0 );
    private Stroke dummystroke = new BasicStroke( 2.0f );

    public CursorGraphic( MovingManModule module, MMTimer MMTimer, Color color, int y, int height ) {
        this.module = module;
        this.MMTimer = MMTimer;
        this.color = color;
        this.y = y;
        this.height = height;
        MMTimer.addObserver( this );
        update( MMTimer, null );
    }

    public void setHeight( int height ) {
        this.height = height;
    }

    public void setBounds( ModelViewTransform2D transform ) {
        this.transform = transform;
        updateYourself();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void paint( Graphics2D g ) {
        GraphicsState state = new GraphicsState( g );
        Stroke origSTroke = g.getStroke();
        if( !visible || transform == null ) {
            return;
        }
        g.setColor( color );
        g.setStroke( stroke );
        g.drawRect( x, y, width, height );
        g.setStroke( origSTroke );
        state.restoreGraphics();
    }

    public void update( Observable o, Object arg ) {
        if( transform == null ) {
            return;
        }
        double time = MMTimer.getTime();
        double coordinate = transform.modelToViewX( time );
        Rectangle origShape = getShape();
        this.x = (int)coordinate - width / 2;
        Rectangle newShape = getShape();
        repaint( origShape, newShape );
    }

    private void repaint( Rectangle s1, Rectangle s2 ) {
        Rectangle union = s1.union( s2 );
        module.getApparatusPanel().repaint( union );
    }

    private Rectangle getShape() {
        return dummystroke.createStrokedShape( new Rectangle2D.Double( x - 1, y - 1, width + 2, height + 2 ) ).getBounds();
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        if( !visible || transform == null ) {
            return false;
        }
        Rectangle r = new Rectangle( x, y, width, height );
        return r.contains( event.getPoint() );
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent event ) {
        Point start = new Point( x, y );
        this.dragHandler = new DragHandler( event.getPoint(), start );
    }

    public void mouseDragged( MouseEvent event ) {
        Point newPoint = dragHandler.getNewLocation( event.getPoint() );
        double xCoord = newPoint.x;
        Point2D.Double input = new Point2D.Double( xCoord, 0 );
//        double requestedTime = inversion.transform( input ).x;
        double requestedTime = transform.viewToModelX( (int)input.x );
        module.cursorMovedToTime( requestedTime );
    }

    public void mouseMoved( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent event ) {
        dragHandler = null;
    }

    public void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public boolean isVisible() {
        return visible;
    }

    public void updateYourself() {
        update( MMTimer, null );
    }

    public boolean contains( int x, int y ) {
        if( !visible || transform == null ) {
            return false;
        }
        Rectangle r = new Rectangle( this.x, this.y, width, height );
        return r.contains( x, y );
    }
}

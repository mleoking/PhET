/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.graphics;

//import edu.colorado.phet.graphics.ShapeGraphic;
import edu.colorado.phet.idealgas.physics.PressureSensingBox;
//import edu.colorado.phet.physics.PhysicalSystem;
//import edu.colorado.phet.physics.Vector2D;
//import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

public class Box2DGraphic extends ShapeGraphic implements MouseListener, MouseMotionListener {

    private int stroke;
    private Area a1;
    private Area a2;

    public Box2DGraphic() {
        super( s_defaultColor, s_defaultStroke );
        init();
    }

    public Box2DGraphic( Box2D box ) {
        super( s_defaultColor, s_defaultStroke );
        init();
        init( box );
        setPosition( box );
    }

    private void init() {
        this.setRep( new Rectangle2D.Float() );
    }

    private BaseIdealGasApparatusPanel getIdealGasApparatusPanel() {
        return (BaseIdealGasApparatusPanel)getApparatusPanel();
    }

    private double lastPressure = 0;

    /**
     * Notes state change in the box the receiver is observing. Changes
     * the position of the leaning man if the pressure in the box has
     * changed sufficiently
     *
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        this.setPosition( (CollidableBody)o );
        if( o instanceof PressureSensingBox ) {
            PressureSensingBox box = (PressureSensingBox)o;
            double newPressure = box.getPressure();
            if( newPressure > lastPressure * s_leaningManStateChangeScaleFactor ) {
                getIdealGasApparatusPanel().moveLeaner( 1 );
                lastPressure = box.getPressure();
            }
            else if( newPressure < lastPressure / s_leaningManStateChangeScaleFactor ) {
                getIdealGasApparatusPanel().moveLeaner( -1 );
                lastPressure = box.getPressure();
            }
        }
    }

    protected void setPosition( Particle body ) {
        Rectangle2D rep = (Rectangle2D)this.getRep();
        a1 = new Area( rep );
        Box2D box = (Box2D)body;
        float prevLeftWallX = (float)rep.getMinX();
        rep.setFrameFromDiagonal( getApparatusPanel().worldToScreenX( box.getCorner1X() ),
                                  getApparatusPanel().worldToScreenY( box.getCorner1Y() ),
                                  getApparatusPanel().worldToScreenX( box.getCorner2X() ),
                                  getApparatusPanel().worldToScreenY( box.getCorner2Y() ) );
        float currLeftWallX = (float)rep.getMinX();
        int dir = (int)( Math.abs( currLeftWallX - prevLeftWallX ) / ( currLeftWallX - prevLeftWallX ) );
        this.getIdealGasApparatusPanel().movePusher( dir );
        a2 = new Area( rep );

        // todo: The hard-coded constants here cover the size of the pusher and the stroke.
        // It would be really good if they were symbolic.
        getApparatusPanel().repaint( (int)( a1.getBounds().getMinX() - 100 ),
                                     (int)( a1.getBounds().getMinY() - 50 ),
                                     (int)( a1.getBounds().getWidth() + 110 ),
                                     (int)( a1.getBounds().getHeight() + 20 ) );
        getApparatusPanel().repaint( (int)( a2.getBounds().getMinX() - 100 ),
                                     (int)( a2.getBounds().getMinY() - 50 ),
                                     (int)( a2.getBounds().getWidth() + 110 ),
                                     (int)( a2.getBounds().getHeight() + 70 ) );
//        getApparatusPanel().repaint( (int)( a1.getBounds().getMinX() - this.stroke ),
//                                     (int)( a1.getBounds().getMinY() - this.stroke ),
//                                     (int)( a1.getBounds().getWidth() + this.stroke ),
//                                     (int)( a1.getBounds().getHeight() + this.stroke ) );
//        getApparatusPanel().repaint( (int)( a2.getBounds().getMinX() - this.stroke ),
//                                     (int)( a2.getBounds().getMinY() - this.stroke ),
//                                     (int)( a2.getBounds().getWidth() + this.stroke ),
//                                     (int)( a2.getBounds().getHeight() + this.stroke ) );
    }

    /**
     *
     */
    Rectangle2D.Float openingShape = new Rectangle2D.Float();

    public void paint( Graphics2D g ) {
        super.paint( g );

        Color oldColor = g.getColor();
        g.setColor( Color.WHITE );
        Box2D box = (Box2D)this.getBody();
        Vector2D[] opening = box.getOpening();
        openingShape.setRect( opening[0].getX(), opening[0].getY(),
                              opening[1].getX() - opening[0].getX(),
                              opening[1].getY() - opening[0].getY() );
        g.draw( openingShape );
        g.setColor( oldColor );
    }

    //
    // Mouse-related methods
    //
    private boolean dragging = false;
    private boolean isInHotSpot = false;
    private float lastMinX = 0;
    private long lastEventTime = 0;
    private float clockScaleFactor = 0;
    private boolean initWallMovement = false;


    public void mouseClicked( MouseEvent event ) {
    }

    public void mousePressed( MouseEvent event ) {

        // If the click is over the box, then start dragging
        if( isInHotSpot ) {
            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
            dragging = true;
        }
    }

    public void mouseReleased( MouseEvent event ) {
        dragging = false;

        // Make sure the wall knows it's not moving anymore
        ( (Box2D)getBody() ).setLeftWallVelocity( 0 );
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    /**
     * Tracks the movement of the the left edge of the box wwhen it's dragged
     */
    public void mouseDragged( MouseEvent event ) {

        if( dragging ) {

            Rectangle2D.Float boxGraphic = (Rectangle2D.Float)this.getRep();
            double t = event.getPoint().getX();
            t = Math.max( event.getPoint().getX(), 50 );
            t = Math.min( Math.max( event.getPoint().getX(), 50 ), boxGraphic.getMaxX() - 20 );
            boxGraphic.setFrameFromDiagonal( Math.min( Math.max( event.getPoint().getX(), 50 ), boxGraphic.getMaxX() - 20 ),
                                             boxGraphic.getMinY(),
                                             boxGraphic.getMaxX(),
                                             boxGraphic.getMaxY() );
            Box2D box = (Box2D)this.getBody();
            box.setBounds( (float)boxGraphic.getMinX(),
                           (float)boxGraphic.getMinY(),
                           (float)boxGraphic.getMaxX(),
                           (float)boxGraphic.getMaxY() );

            // Compute the velocity of the wall
            if( !initWallMovement ) {
                initWallMovement = true;

                lastMinX = (float)event.getPoint().getX();
                lastEventTime = event.getWhen();
                clockScaleFactor = PhysicalSystem.instance().getDt() / PhysicalSystem.instance().getWaitTime();
            }
            float dx = (float)event.getPoint().getX() - lastMinX;
            lastMinX = (float)event.getPoint().getX();
            long now = event.getWhen();
            long dt = now - lastEventTime;
            lastEventTime = now;
            if( dt > 0 ) {
                float vx = dx / ( dt * clockScaleFactor );
                Thread.yield();
                box.setLeftWallVelocity( vx * 2 );
                // We must yield so the PhysicalSystem thread can get the
                // update.
                Thread.yield();
            }

            this.getIdealGasApparatusPanel().movePusher( (int)( Math.abs( dx ) / dx ) );
            return;
        }
    }

    public void mouseMoved( MouseEvent event ) {

        Point mousePos = event.getPoint();

        // Determine the screen location of the box
        Rectangle2D.Float box = (Rectangle2D.Float)this.getRep();
        Rectangle2D.Float hotSpot;
        if( Math.abs( mousePos.getX() - box.getMinX() ) < 5
            && ( mousePos.getY() <= box.getMaxY() && mousePos.getY() >= box.getMinY() ) ) {
            getApparatusPanel().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            isInHotSpot = true;
        }
        else if( isInHotSpot ) {
            getApparatusPanel().setCursor( Cursor.getDefaultCursor() );
            isInHotSpot = false;
        }
    }

    public boolean isDragging() {
        return dragging;
    }

    //
    // Static fields and methods
    //
    private static Stroke s_defaultStroke = new BasicStroke( 4.0F );
    private static Color s_defaultColor = Color.black;
    private static float s_leaningManStateChangeScaleFactor = 1.75F;
}

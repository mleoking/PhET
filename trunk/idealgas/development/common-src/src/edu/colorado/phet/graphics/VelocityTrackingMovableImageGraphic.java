/**
 * Class: VelocityTrackingMovableImageGraphic
 * Class: edu.colorado.phet.lightdoppler.graphics
 * User: Ron LeMaster
 * Date: Apr 19, 2003
 * Time: 3:58:35 PM
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.graphics.MovableImageGraphic;
//import edu.colorado.phet.lightdoppler.controller.SetRocketVelocityCmd;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

abstract public class VelocityTrackingMovableImageGraphic extends MovableImageGraphic {
    private float lastEventX;
    private long lastEventTime;
    private float clockScaleFactor;
    /**
     *
     */
    private int numSamples = 5;
    private LinkedList samples = new LinkedList();

    public VelocityTrackingMovableImageGraphic( Image image, float x, float y, float minX, float minY, float maxX, float maxY ) {
        super( image, x, y, minX, minY, maxX, maxY );
    }

    /**
     *
     * @param event
     */
    public void mousePressed( MouseEvent event ) {
        super.mousePressed( event );

        if( isSelected() ) {
            // Record the frequency when we started dragging, and the point that
            // the graphic is at in the X axis. These will be used to compute
            // the Doppler-shifted frequency
            lastEventX = (float)getLocationPoint2D().getX();
            lastEventTime = event.getWhen();
            clockScaleFactor = PhysicalSystem.instance().getDt() / PhysicalSystem.instance().getWaitTime();
        }
    }

    /**
     *
     * @param event
     */
    public void mouseDragged( MouseEvent event ) {
        super.mouseDragged( event );

        if( isSelected() ) {
            // Compute the Doppler-shifted frequency based on the displacement
            // of the graphic. Note that we can't use the location of the mouse
            // event because the mouse can move after the graphic has hit the
            // end of its allowed range of motion.
            float vx = 0;
            float dx = (float)getLocationPoint2D().getX() - lastEventX;
            lastEventX = (float)getLocationPoint2D().getX();
            long now = event.getWhen();
            long dt = now - lastEventTime;
            lastEventTime = now;
            if( dt > 0 ) {
                vx = dx / ( dt * clockScaleFactor );
                samples.add( new Double( vx ) );
                if( samples.size() >= numSamples ) {
                    float aveVx = 0;
                    for( int i = 0; i < samples.size(); i++ ) {
                        Double aDouble = (Double)samples.get( i );
                        aveVx += aDouble.floatValue();
                    }
                    samples.remove( 0 );
                    notifyModel( aveVx );
                }

                // We must yield so the PhysicalSystem thread can get the update.
                Thread.yield();
            }
        }
    }

    /**
     *
     * @param e
     */
    public void mouseReleased( MouseEvent e ) {

        super.mouseReleased( e );

        // Add back for light doppler!!!!!
//        new SetRocketVelocityCmd( new Vector2D( 0, 0 ) ).doItLater();
    }


    //
    // Abstract methods
    //

    /**
     * This method is called on every MouseDragged event, giving concrete subclasses
     * to opportunity to communicate with the physical model at that time.
     * @param vx
     */
    protected abstract void notifyModel( float vx );
}

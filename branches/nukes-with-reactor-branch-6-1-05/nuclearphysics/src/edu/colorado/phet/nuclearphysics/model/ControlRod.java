/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.controller.ControlledFissionModule;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.coreadditions.EventChannel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.EventObject;
import java.util.EventListener;

/**
 * ControlRod
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlRod {
    private Rectangle2D.Double rep = new Rectangle2D.Double();
    private int orientation;
    private double thickness;

    public ControlRod( Point2D p1, Point2D p2, double thickness ) {

        this.thickness = thickness;
        // Is the rod horizontal?
        if( p1.getY() == p2.getY() ) {
            orientation = ControlledFissionModule.HORIZONTAL;
            rep.setRect( p1.getX(), p1.getY() - thickness / 2, p2.getX() - p1.getX(), thickness );
        }
        // Is the rod vertical?
        if( p1.getX() == p2.getX() ) {
            orientation = ControlledFissionModule.VERTICAL;
            rep.setRect( p1.getX() - thickness / 2, p1.getY(), thickness, p2.getY() - p1.getY() );
        }
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public double getThickness() {
        return thickness;
    }

    public Shape getShape() {
        return rep;
    }

    public int getOrientation() {
        return orientation;
    }

    public Rectangle2D getBounds() {
        return getShape().getBounds2D();
    }

    public void translate( double dx, double dy ) {
        rep.setRect( rep.getMinX() + dx, rep.getMinY() + dy, rep.getWidth(), rep.getHeight() );
        changeListenerProxy.changed( new ChangeEvent( this ) );
    }

    //----------------------------------------------------------------
    // Implementation of Body
    //----------------------------------------------------------------
    public Point2D getCM() {
        return null;
    }

    public double getMomentOfInertia() {
        return 0;
    }

    //----------------------------------------------------------------
    // Event and listener definitions
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public ControlRod getControlRod() {
            return (ControlRod)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void changed( ChangeEvent event );
    }

}

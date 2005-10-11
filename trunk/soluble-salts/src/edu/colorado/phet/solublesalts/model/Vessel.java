/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.util.EventChannel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Vessel
 * <p>
 * A rectangular vessel. It's location is specified by its upper left corner.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Vessel {
    private Rectangle2D shape;
    private Point2D location;
    private double waterLevel;

    public Vessel( double width, double depth ) {
        this( width, depth, new Point2D.Double() );
    }

    public Vessel( double width, double depth, Point2D location ) {
        shape = new Rectangle2D.Double( location.getX(), location.getY(), width, depth );
        this.location = location;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel( double waterLevel ) {
        this.waterLevel = waterLevel;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Rectangle2D getShape() {
        return shape;
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation( Point2D location ) {
        this.location = location;
    }

    public double getWidth() {
        return shape.getWidth();
    }

    public double getDepth() {
        return shape.getHeight();
    }

    //----------------------------------------------------------------
    // Events and listeners
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

        public Vessel getVessel() {
            return (Vessel)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }
}

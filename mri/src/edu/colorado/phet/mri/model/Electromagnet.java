/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Electromagnet
 * <p/>
 * Note that the location or the magnet is its midpoint
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Electromagnet extends Particle {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private double current;
    private double fieldStrength;
    private Rectangle2D bounds;
//    private FieldChangerA fieldChanger;

    /**
     * @param position
     * @param width
     * @param height
     * @param clock
     */
    public Electromagnet( Point2D position, double width, double height, IClock clock ) {
        super( position, new Vector2D.Double(), new Vector2D.Double() );
        this.bounds = new Rectangle2D.Double( position.getX() - width / 2,
                                              position.getY() - height / 2,
                                              width,
                                              height );
//        fieldChanger = new FieldChangerA( clock, MriConfig.MAGNET_FIELD_DB, MriConfig.MAGNET_FIELD_DB );
    }

    public double getCurrent() {
        return current;
    }

    public void setFieldStrength( double fieldStrength ) {
        this.fieldStrength = fieldStrength;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public double getFieldStrength() {
        return fieldStrength;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

//    public void setCurrent( double current ) {
//        this.current = current;
//        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
////        fieldChanger.setTarget( current * MriConfig.CURRENT_TO_FIELD_FACTOR );
//    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class FieldChangerA extends ClockAdapter {
        private double target;
        private double dB;
        private double eps;

        FieldChangerA( IClock clock, double dB, double eps ) {
            this.dB = dB;
            this.eps = eps;
            clock.addClockListener( this );
        }

        public void setTarget( double target ) {
            this.target = target;
        }

        public void clockTicked( ClockEvent clockEvent ) {
            double diff = target - getFieldStrength();
            if( Math.abs( diff ) > eps ) {
                double dField = dB * MathUtil.getSign( diff );
                setFieldStrength( getFieldStrength() + dField );
            }
            else if( getFieldStrength() != target ) {
                setFieldStrength( target );
            }
        }
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

        public ChangeEvent( Electromagnet source ) {
            super( source );
        }

        public Electromagnet getElectromagnet() {
            return (Electromagnet)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

}

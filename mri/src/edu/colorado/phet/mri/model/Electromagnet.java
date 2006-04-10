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

import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Electromagnet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Electromagnet extends Particle {
    private double current;
    private double fieldStrength;
    private Rectangle2D bounds;
    private FieldChangerA fieldChanger;
//    private FieldChanger fieldChanger = new FieldChanger( 100 );

    public Electromagnet( Point2D position, double width, double height, IClock clock ) {
        super( position, new Vector2D.Double(), new Vector2D.Double() );
        this.bounds = new Rectangle2D.Double( position.getX() - width / 2,
                                              position.getY() - height / 2,
                                              width,
                                              height);
        fieldChanger = new FieldChangerA( clock, 1, 1 );
    }

    public double getCurrent() {
        return current;
    }

    private void setFieldStrength( double fieldStrength ) {
        this.fieldStrength = fieldStrength;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public double getFieldStrength() {
        return fieldStrength;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setCurrent( double current ) {
        this.current = current;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
        fieldChanger.setTarget( current );
//        setFieldStrength( getCurrent() );
//        new Thread( new FieldChanger( 100)).start();
    }

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
            double diff  = target-getFieldStrength();
            if( Math.abs( diff ) > eps ) {
                setFieldStrength( getFieldStrength() + dB * MathUtil.getSign( diff ) );
            }
        }
    }

    private class FieldChanger implements Runnable {
        private int delay;
        private double targetField;

        FieldChanger( int delay ) {
            this.delay = delay;
        }

        public void run() {
            if( getFieldStrength() != targetField ) {

            }




            try {
                Thread.sleep( delay );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            setFieldStrength( getCurrent() );
        }

        synchronized void setTargetField( double targetField) {
            this.targetField = targetField;
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

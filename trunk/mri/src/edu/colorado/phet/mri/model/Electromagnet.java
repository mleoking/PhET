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
import edu.colorado.phet.common.math.Vector2D;
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

    public Electromagnet( Point2D position, double width, double height ) {
        super( position, new Vector2D.Double(), new Vector2D.Double() );
        this.bounds = new Rectangle2D.Double( position.getX() - width / 2,
                                              position.getY() - height / 2,
                                              width,
                                              height);
    }

    public double getCurrent() {
        return current;
    }

    private void setFieldStrength( double fieldStrength ) {
//    private synchronized void setFieldStrength( double fieldStrength ) {
//        System.out.println( "Electromagnet.setFieldStrength" );
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
        setFieldStrength( getCurrent() );
//        new Thread( new FieldChanger( 100)).start();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class FieldChanger implements Runnable {
        private int delay;

        FieldChanger( int delay ) {
            this.delay = delay;
        }

        public void run() {
            try {
                Thread.sleep( delay );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            setFieldStrength( getCurrent() );
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

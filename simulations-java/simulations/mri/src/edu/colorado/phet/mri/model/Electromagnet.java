// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Particle;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

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
        super( position, new MutableVector2D(), new MutableVector2D() );
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

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) changeEventChannel.getListenerProxy();

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
            return (Electromagnet) getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

}

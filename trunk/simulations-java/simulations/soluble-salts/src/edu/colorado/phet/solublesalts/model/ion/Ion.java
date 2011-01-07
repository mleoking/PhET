// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.solublesalts.model.Atom;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;


/**
 * Ion
 * <p/>
 * Ions can be bound or not. When they are not bound, they move like normal Atoms, but can bind if they
 * come into contact with an ion of a different polarity. When an Ion is bound, it moves with the velocity of
 * the crystal in which it is bound.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Ion extends Atom {

    private IonProperties ionProperties;
    private Crystal bindingCrystal;
    private Vector2D vSaveUtil = new Vector2D();

    public Ion( IonProperties ionProperties ) {
        this( new Point2D.Double( 1, 1 ),
              new Vector2D(),
              new Vector2D(),
              ionProperties );
    }

    public Ion( Point2D position, Vector2D velocity, Vector2D acceleration, IonProperties ionProperties ) {
        super( position, velocity, acceleration, ionProperties.getMass(), ionProperties.getRadius() );
        this.ionProperties = ionProperties;
    }

    public IonProperties getIonProperties() {
        return ionProperties;
    }

    public void stepInTime( double dt ) {
        if ( !isBound() ) {
            super.stepInTime( dt );
        }
        else {
            vSaveUtil.setComponents( getVelocity().getX(), getVelocity().getY() );
            setVelocity( bindingCrystal.getVelocity() );
            super.stepInTime( dt );
            setVelocity( vSaveUtil );
        }
    }

    public void bindTo( Crystal crystal ) {
        bindingCrystal = crystal;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public void unbindFrom( Crystal crystal ) {
        bindingCrystal = null;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public boolean isBound() {
        return bindingCrystal != null;
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public double getCharge() {
        return ionProperties.getCharge();
    }

    public Crystal getBindingCrystal() {
        return bindingCrystal;
    }

    //----------------------------------------------------------------
    // Events and Listeners
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
        public ChangeEvent( Ion source ) {
            super( source );
        }

        public Ion getIon() {
            return (Ion) getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        public void stateChanged( ChangeEvent event );
    }
}

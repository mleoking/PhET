/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.DefaultBody;
import edu.colorado.phet.molecularreactions.MRConfig;

import java.awt.geom.Point2D;
import java.util.EventListener;

/**
 * Launcher
 * <p/>
 * A spring-based thing for launching a molecule into the box. It is supposed to be something
 * like the launcher on a pinball machine
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Launcher extends DefaultBody implements ModelElement, PotentialEnergySource {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class MovementType {
        private MovementType() {
        }
    }

    public static MovementType ONE_DIMENSIONAL = new MovementType();
    public static MovementType TWO_DIMENSIONAL = new MovementType();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Point2D restingTipLocation;
    private double maxPlungerDraw;
    private double springK = .2;
    private SimpleMolecule bodyToLaunch;
    private MovementType movementType = TWO_DIMENSIONAL;
    private boolean enabled;
    // The distance that the launcher is drawn back from its resting position.
    private double extension;

    /**
     * @param restingTipLocation
     */
    public Launcher( Point2D restingTipLocation ) {
        this.restingTipLocation = restingTipLocation;
    }

    public void setBodyToLaunch( SimpleMolecule bodyToLaunch ) {
        this.bodyToLaunch = bodyToLaunch;
        bodyToLaunch.setVelocity( 0, 0 );
        setEnabled( true );
    }

    public void release() {
        if( bodyToLaunch != null && extension > 2 ) {
            double s = Math.sqrt( 2 * getPE() / bodyToLaunch.getMass() );
            Vector2D v = new Vector2D.Double( 0, -s );
            v.rotate( getTheta() );
            bodyToLaunch.setVelocity( v );
            bodyToLaunch = null;
            enabled = false;
        }
        setExtension( 0.0 );
    }

    private void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getPE() {
        double dl = getExtension();
        double pe = springK * dl * dl / 2;
        return pe;
    }

    public double getExtension() {
        return extension;
    }

    public Point2D getPivotPoint() {
        if( bodyToLaunch == null ) {
            return getRestingTipLocation();
        }
        else {
            return new Point2D.Double( getRestingTipLocation().getX(),
                                       getRestingTipLocation().getY() - bodyToLaunch.getRadius() );
        }
    }

    public void setTheta( double theta ) {
        if( theta >= MRConfig.LAUNCHER_MIN_THETA && theta <= MRConfig.LAUNCHER_MAX_THETA ) {
            super.setTheta( theta );
            notifyObservers();
        }
    }

    public void stepInTime( double dt ) {
        // noop
    }

    public Point2D getRestingTipLocation() {
        return restingTipLocation;
    }

    public void setExtension( double extension ) {
        if( extension >= 0 && extension <= MRConfig.LAUNCHER_MAX_EXTENSION ) { 
        this.extension = extension;
    }
    }

    public void setMovementType( MovementType movementType ) {
        this.movementType = movementType;
        changeListenerProxy.stateChanged( this );
    }

    public MovementType getMovementType() {
        return movementType;
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public interface ChangeListener extends EventListener {
        void stateChanged( Launcher launcher );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

}

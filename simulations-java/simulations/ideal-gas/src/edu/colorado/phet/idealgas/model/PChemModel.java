/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.idealgas.collision.Wall;

import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;


/**
 * PChemModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PChemModel extends IdealGasModel implements Wall.ChangeListener {
    double centerlineLoc;

    /**
     * @param dt
     */
    public PChemModel( double dt ) {
        super( dt );
    }

    /**
     * @param centerlineLoc
     */
    public void setCenterlineLoc( double centerlineLoc ) {
        this.centerlineLoc = centerlineLoc;
    }

    /**
     * @param verticalWall
     */
    public void setVerticalWall( Wall verticalWall ) {
        verticalWall.addChangeListener( this );
        Rectangle2D wallBounds = verticalWall.getBounds();
        setCenterlineLoc( wallBounds.getMinX() + wallBounds.getWidth() / 2 );
    }

    /**
     * Changes the type of an object (GasMolecule) if it crosses the centerline of the
     * energy profile
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        List bodies = super.getBodies();
        for( int i = 0; i < bodies.size(); i++ ) {
            Object o = bodies.get( i );
            if( o instanceof GasMolecule ) {
                GasMolecule molecule = (GasMolecule)o;
                if( molecule.getPosition().getX() > centerlineLoc && molecule instanceof HeavySpecies ) {
                    LightSpecies lightMolecule = new LightSpecies( molecule.getPosition(),
                                                                   molecule.getVelocity(),
                                                                   molecule.getAcceleration() );
                    Vector2D vOld = molecule.getVelocity();
                    Vector2D vNew = vOld.scale( Math.sqrt( molecule.getMass() / lightMolecule.getMass() ) );
                    lightMolecule.setVelocity( vNew );
                    addModelElement( lightMolecule );
                    removeModelElement( molecule );
                    listenerProxy.moleculeCreated( new MoleculeCreationEvent( this, lightMolecule ) );
                }
                if( molecule.getPosition().getX() < centerlineLoc && molecule instanceof LightSpecies ) {
                    HeavySpecies heavyMolecule = new HeavySpecies( molecule.getPosition(),
                                                                   molecule.getVelocity(),
                                                                   molecule.getAcceleration() );
                    Vector2D vOld = molecule.getVelocity();
                    Vector2D vNew = vOld.scale( Math.sqrt( molecule.getMass() / heavyMolecule.getMass() ) );
                    heavyMolecule.setVelocity( vNew );
                    addModelElement( heavyMolecule );
                    removeModelElement( molecule );
                    listenerProxy.moleculeCreated( new MoleculeCreationEvent( this, heavyMolecule ) );
                }
            }
        }
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void wallChanged( Wall.ChangeEvent event ) {
        Rectangle2D wallBounds = event.getWall().getBounds();
        setCenterlineLoc( wallBounds.getMinX() + wallBounds.getWidth() / 2 );
    }


    public class MoleculeCreationEvent extends EventObject {
        private GasMolecule molecule;

        public MoleculeCreationEvent( Object source, GasMolecule molecule ) {
            super( source );
            this.molecule = molecule;
        }

        public GasMolecule getMolecule() {
            return molecule;
        }
    }

    public interface Listener extends EventListener {
        public void moleculeCreated( MoleculeCreationEvent event );
    }

    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }
}

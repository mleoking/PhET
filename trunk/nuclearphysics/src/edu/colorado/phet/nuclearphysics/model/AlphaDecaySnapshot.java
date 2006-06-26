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

import edu.colorado.phet.common.math.Vector2D;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.geom.Point2D;

/**
 * AlphaDecaySnapshot
 * <p>
 * An object that saves and restores the state of the NuclearModelElements in at a point in time
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AlphaDecaySnapshot {
    private NuclearPhysicsModel model;
    private Map nuclearModelElementToSavedState = new HashMap();

    public AlphaDecaySnapshot( NuclearPhysicsModel model ) {
        this.model = model;
        List modelElements = model.getNuclearModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)modelElements.get( i );
            SavedModelElement savedModelElement = createSavedModelElement( nuclearModelElement );
            nuclearModelElementToSavedState.put( nuclearModelElement, savedModelElement );
        }
    }

    public void restore() {
        List modelElements = model.getNuclearModelElements();
        for( int i = 0; i < modelElements.size(); i++ ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)modelElements.get( i );
            if( !nuclearModelElementToSavedState.keySet().contains( nuclearModelElement ) ) {
                model.removeModelElement( nuclearModelElement );
            }
        }

        Iterator it = nuclearModelElementToSavedState.keySet().iterator();
        while( it.hasNext() ) {
            NuclearModelElement nuclearModelElement = (NuclearModelElement)it.next();
            SavedModelElement savedModelElement = (SavedModelElement)nuclearModelElementToSavedState.get( nuclearModelElement );
            savedModelElement.restore( nuclearModelElement );
            if( !model.getNuclearModelElements().contains( nuclearModelElement ) ) {
                model.addModelElement( nuclearModelElement );
            }
        }
    }

    private SavedModelElement createSavedModelElement( NuclearModelElement source ) {
        if( source instanceof AlphaParticle ) {
            return new SavedAlphParticle( (AlphaParticle)source );
        }
        else {
            return new SavedModelElement( source );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes for saving and restoring the state of NuclearModelElements and AlphaParticles
    //--------------------------------------------------------------------------------------------------

    /**
     * Class for saving and restoring the state of a NuclearModelElement
     */
    private class SavedModelElement extends NuclearModelElement {

        protected SavedModelElement( NuclearModelElement sourceModelElement ) {
            super( new Point2D.Double( sourceModelElement.getPosition().getX(),
                                       sourceModelElement.getPosition().getY() ),
                   new Vector2D.Double( sourceModelElement.getVelocity() ),
                   new Vector2D.Double( sourceModelElement.getAcceleration() ),
                   sourceModelElement.getMass(),
                   0 );
        }

        public Point2D getCM() {
            return null;
        }

        public double getMomentOfInertia() {
            return 0;
        }

        void restore( NuclearModelElement nuclearModelElement) {
            nuclearModelElement.setPosition( new Point2D.Double( getPosition().getX(),
                                                                 getPosition().getY() ) );
            nuclearModelElement.setVelocity( new Vector2D.Double( getVelocity() ) );
            nuclearModelElement.setAcceleration( new Vector2D.Double( getAcceleration() ) );
            nuclearModelElement.setMass( getMass() );
        }
    }

    /**
     * Class for saving and restoring the state of an AlphaParticle
     */
    private class SavedAlphParticle extends SavedModelElement {
        private ProfileableNucleus parentNucleus;

        protected SavedAlphParticle( AlphaParticle sourceModelElement ) {
            super( sourceModelElement );
            parentNucleus = sourceModelElement.getNucleus();
        }

        public Nucleus getParentNucleus() {
            return parentNucleus;
        }

        void restore( NuclearModelElement nuclearModelElement) {
            super.restore( nuclearModelElement );
            ((AlphaParticle)nuclearModelElement).setNucleus( parentNucleus );
        }
    }
}

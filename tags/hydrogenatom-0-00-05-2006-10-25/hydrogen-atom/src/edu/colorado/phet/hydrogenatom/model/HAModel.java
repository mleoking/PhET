/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;

/**
 * HAModel is the model for this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAModel extends Model {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ArrayList _atoms; // array of AbstractHydrogenAtom
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAModel( IClock clock ) {
        super( clock );
        
        _atoms = new ArrayList();
        _photons = new ArrayList();
        _alphaParticles = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // ModelElement management
    //----------------------------------------------------------------------------
    
    /**
     * When a model element is added, also add it to one of 
     * the lists used for collision detection.
     * 
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof AbstractHydrogenAtom ) {
            _atoms.add( modelElement );
        }
        else if ( modelElement instanceof Photon ) {
            _photons.add( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.add( modelElement );
        }
        super.addModelElement( modelElement );
    }

    /**
     * When a model element is removed, also remove it from one of 
     * the lists used for collision detection.
     * 
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof AbstractHydrogenAtom ) {
            _atoms.remove( modelElement );
        }
        else if ( modelElement instanceof Photon ) {
            _photons.remove( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.remove( modelElement );
        }
        super.removeModelElement( modelElement );
    }
    
    //----------------------------------------------------------------------------
    // ClockListener overrides
    //----------------------------------------------------------------------------
    
    /**
     * Detect collisions whenever the clock ticks.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        super.clockTicked( event );
        detectCollisions();
    }
    
    /*
     * Detect collisions by iterating through each atom-photon
     * and atom-alphaParticle pair. The atom is responsible for
     * detecting and handling any collisions.
     */
    private void detectCollisions() {
        Iterator i = _atoms.iterator();
        while ( i.hasNext() ) {
            
            AbstractHydrogenAtom atom = (AbstractHydrogenAtom)i.next();
            
            Iterator p = _photons.iterator();
            while ( p.hasNext() ) {
                Photon photon = (Photon)p.next();
                atom.detectCollision( photon );
            }
            
            Iterator a = _alphaParticles.iterator();
            while ( a.hasNext() ) {
                AlphaParticle alphaParticle = (AlphaParticle)a.next();
                atom.detectCollision( alphaParticle );
            }
        }
    }
}

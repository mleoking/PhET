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


public class HAModel extends Model {

    private ArrayList _atoms; // array of AbstractHydrogenAtom
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    public HAModel( IClock clock ) {
        super( clock );
        
        _atoms = new ArrayList();
        _photons = new ArrayList();
        _alphaParticles = new ArrayList();
    }
    
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
    
    public void clockTicked( ClockEvent clockEvent ) {
        super.clockTicked( clockEvent );
        detectCollisions();
    }
    
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

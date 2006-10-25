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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;


public class HAModel extends Model {

    private ArrayList _hydrogenAtoms; // array of AbstractHydrogenAtom
    private ArrayList _photons; // array of Photon
    private ArrayList _alphaParticles; // array of AlphaParticle
    
    public HAModel( IClock clock ) {
        super( clock );
        
        _hydrogenAtoms = new ArrayList();
        _photons = new ArrayList();
        _alphaParticles = new ArrayList();
    }
    
    public ArrayList getHydrogenAtoms() {
        return _hydrogenAtoms;
    }
    
    public ArrayList getPhotons() {
        return _photons;
    }
    
    public ArrayList getAlphaParticles() {
        return _alphaParticles;
    }
    
    public void addModelElement( ModelElement modelElement ) {
        if ( modelElement instanceof AbstractHydrogenAtom ) {
            _hydrogenAtoms.add( modelElement );
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
            _hydrogenAtoms.remove( modelElement );
        }
        else if ( modelElement instanceof Photon ) {
            _photons.remove( modelElement );
        }
        else if ( modelElement instanceof AlphaParticle ) {
            _alphaParticles.remove( modelElement );
        }
        super.removeModelElement( modelElement );
    }
}

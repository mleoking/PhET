/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;


/**
 * This class adds the ability to display force-depicting arrows to its super
 * class.
 *
 * @author John Blanco
 */
public class ParticleForceNode extends ParticleNode {

    //-----------------------------------------------------------------------------
    // Class Data
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Instance Data
    //-----------------------------------------------------------------------------

    private boolean m_showForces;
    
    //-----------------------------------------------------------------------------
    // Constructor(s)
    //-----------------------------------------------------------------------------

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt, boolean useGradient ) {
        super( particle, mvt, useGradient );
        
        m_showForces = false;
    }

    public ParticleForceNode( StatesOfMatterAtom particle, ModelViewTransform mvt ) {
        super( particle, mvt );

        m_showForces = false;
    }
    

    //-----------------------------------------------------------------------------
    // Accessor Methods
    //-----------------------------------------------------------------------------

    //-----------------------------------------------------------------------------
    // Other Public Methods
    //-----------------------------------------------------------------------------
    
    public void setShowForces( boolean showForces ){
        
        m_showForces = showForces;
    }

    //-----------------------------------------------------------------------------
    // Private Methods
    //-----------------------------------------------------------------------------




    
}

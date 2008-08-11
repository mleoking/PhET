/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import java.util.Random;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Basic representation of an alpha particle in the view.
 *
 * @author John Blanco
 */
public class AlphaParticleNode extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    protected static final Random _rand = new Random(10);

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    protected PNode _displayNode;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructor
     */
    public AlphaParticleNode() {
        if (_rand.nextBoolean()){
            _displayNode = createCompositeNode1();
        }
        else {
            _displayNode = createCompositeNode2();
        }
        
        addChild(_displayNode);
    }

    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------

    private static PNode createCompositeNode1() {
        double diameter = NuclearPhysicsConstants.NUCLEON_DIAMETER;
        SphericalNode proton1 = new SphericalNode( diameter, NuclearPhysicsConstants.PROTON_ROUND_GRADIENT, false );
        SphericalNode proton2 = new SphericalNode( diameter, NuclearPhysicsConstants.PROTON_ROUND_GRADIENT, false );
        SphericalNode neutron1 = new SphericalNode( diameter, NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT, false );
        SphericalNode neutron2 = new SphericalNode( diameter, NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT, false );
        
        PNode alphaParticle = new PNode();
        proton1.setOffset( 0, diameter/2 );
        alphaParticle.addChild(proton1);
        neutron1.setOffset( diameter/2, 0 );
        alphaParticle.addChild(neutron1);
        neutron2.setOffset( diameter/2, diameter );
        alphaParticle.addChild(neutron2);
        proton2.setOffset( diameter, diameter/2 );
        alphaParticle.addChild(proton2);
        
        return alphaParticle;
    }

    private static PNode createCompositeNode2() {
        double diameter = NuclearPhysicsConstants.NUCLEON_DIAMETER;
        SphericalNode proton1 = new SphericalNode( diameter, NuclearPhysicsConstants.PROTON_ROUND_GRADIENT, false );
        SphericalNode proton2 = new SphericalNode( diameter, NuclearPhysicsConstants.PROTON_ROUND_GRADIENT, false );
        SphericalNode neutron1 = new SphericalNode( diameter, NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT, false );
        SphericalNode neutron2 = new SphericalNode( diameter, NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT, false );

        PNode alphaParticle = new PNode();
        proton1.setOffset( diameter / 1.5, diameter / 2  );
        alphaParticle.addChild(proton1);
        neutron1.setOffset( 0, 0 );
        alphaParticle.addChild(neutron1);
        neutron2.setOffset( diameter / 1.5, diameter );
        alphaParticle.addChild(neutron2);
        proton2.setOffset( 0, diameter / 1.5 );
        alphaParticle.addChild(proton2);
        
        return alphaParticle;
    }
}
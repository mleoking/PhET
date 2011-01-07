/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicAtomNode;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.view.BucketNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * This class defines a Piccolo Node that represents an atom in "schematic"
 * (i.e. Bohr) form and allows users to add or remove neutrons from/to a
 * bucket in order to create different isotopes of a particular atom.
 *
 * @author John Blanco
 */
public class InteractiveIsotopeNode extends SchematicAtomNode {

    /**
     * Constructor.
     */
    public InteractiveIsotopeNode( final InteractiveIsotopeModel model, ModelViewTransform2D mvt, final BooleanProperty viewOrbitals ) {
        super( model.getAtom(), mvt, viewOrbitals, false, true, false );

        model.addListener( new InteractiveIsotopeModel.Adapter() {
            @Override
            public void particleAdded( SubatomicParticle subatomicParticle ) {
                addParticle( subatomicParticle );
            }
        });

        // Add the bucket that holds the neutrons.
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToViewDouble( model.getNeutronBucket().getPosition() ) );
        backLayer.addChild( neutronBucketNode.getHoleLayer() );
        frontLayer.addChild( neutronBucketNode.getContainerLayer() );
        for ( SubatomicParticle neutron : model.getNeutronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addNeutron( (Neutron) neutron );
        }
    }
}

// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicAtomNode;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.view.BucketNode;
import edu.colorado.phet.buildanatom.view.OrbitalView;
import edu.colorado.phet.buildanatom.view.OrbitalViewProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.util.PBounds;

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
    public InteractiveIsotopeNode( final InteractiveIsotopeModel model, final ModelViewTransform mvt, final Point2D bottomPoint ) {
        super( model.getAtom(), mvt, new OrbitalViewProperty( OrbitalView.ISOTOPES_RESIZING_CLOUD ), false, true, false );

        model.addListener( new InteractiveIsotopeModel.Adapter() {
            @Override
            public void particleAdded( SphericalParticle subatomicParticle ) {
                addParticleNode( subatomicParticle );
            }
        });

        // Add the bucket that holds the neutrons.
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToView( model.getNeutronBucket().getPosition() ) );
        electronShellLayer.addChild( neutronBucketNode.getHoleLayer() );
        frontLayer.addChild( neutronBucketNode.getContainerLayer() );
        for ( SphericalParticle neutron : model.getNeutronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addNeutronNode( (Neutron) neutron );
        }

        // Add the handler that keeps the bottom of the atom in one place.
        // This was added due to a request to make the atom get larger and
        // smaller but to stay on the scale.
        getIsotopeElectronCloudNode().addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                model.getAtom().setPosition( mvt.viewToModel( bottomPoint.getX(),
                        bottomPoint.getY() - getIsotopeElectronCloudNode().getFullBoundsReference().height / 2 ) );
            }
        });
    }

    public PBounds getNucleusBounds(){
        return getNucleusLayerParentNode().getFullBounds();
    }

    public void addNucleusBoundsChangeListener( PropertyChangeListener listener ){
        getNucleusLayerParentNode().addPropertyChangeListener( PROPERTY_FULL_BOUNDS, listener );
    }
}

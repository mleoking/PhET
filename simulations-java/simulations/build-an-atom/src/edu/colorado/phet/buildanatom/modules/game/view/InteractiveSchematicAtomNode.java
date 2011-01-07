// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.BucketNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo Node that represents an atom in "schematic" (i.e. Bohr) form and
 * allows users to add or remove the various subatomic particles.  This
 * extension adds buckets for holding subatomic particles that can be moved
 * into the atom.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class InteractiveSchematicAtomNode extends SchematicAtomNode {

    private final BuildAnAtomModel model;

    /**
     * Constructor.
     */
    public InteractiveSchematicAtomNode( final BuildAnAtomModel model, ModelViewTransform2D mvt, final BooleanProperty viewOrbitals ) {
        super( model.getAtom(), mvt, viewOrbitals );

        this.model = model;

        // Add a marker that depicts the center of the atom's nucleus.
        backLayer.addChild( new CenterMarkerNode( model, mvt ) );

        // Add the buckets that hold the sub-atomic particles.
        BucketNode electronBucketNode = new BucketNode( model.getElectronBucket(), mvt );
        electronBucketNode.setOffset( mvt.modelToViewDouble( model.getElectronBucket().getPosition() ) );
        backLayer.addChild( electronBucketNode.getHoleLayer() );
        frontLayer.addChild( electronBucketNode.getContainerLayer() );
        for ( SubatomicParticle electron : model.getElectronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addElectron( (Electron) electron );
        }
        BucketNode protonBucketNode = new BucketNode( model.getProtonBucket(), mvt );
        protonBucketNode.setOffset( mvt.modelToViewDouble( model.getProtonBucket().getPosition() ) );
        backLayer.addChild( protonBucketNode.getHoleLayer() );
        frontLayer.addChild( protonBucketNode.getContainerLayer() );
        for ( SubatomicParticle proton : model.getProtonBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addProton( (Proton) proton );
        }
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

    public AtomValue getAtomValue() {
        return model.getAtom().toAtomValue();
    }

    public void setAtomValue( AtomValue answer ) {
        model.setState( answer, false );
    }

    private static class CenterMarkerNode extends PNode {
        public CenterMarkerNode( final BuildAnAtomModel model, ModelViewTransform2D mvt ) {

            model.getAtom().addObserver( new SimpleObserver() {
                public void update() {
                    // Only visible when there are no nucleons in the atom.
                    setVisible (model.getAtom().getAtomicMassNumber() == 0);
                }
            });

            // Create the marker.
            DoubleGeneralPath nucleusXMarkerModelCoords = new DoubleGeneralPath();
            double xMarkerSize = Proton.RADIUS; // Arbitrary, adjust as desired.
            nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                    model.getAtom().getPosition().getY() - xMarkerSize / 2 );
            nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                    model.getAtom().getPosition().getY() + xMarkerSize / 2 );
            nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                    model.getAtom().getPosition().getY() + xMarkerSize / 2 );
            nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                    model.getAtom().getPosition().getY() - xMarkerSize / 2 );
            Shape nucleusXMarkerShape = mvt.createTransformedShape( nucleusXMarkerModelCoords.getGeneralPath() );
            PNode nucleusXMarkerNode = new PhetPPath( nucleusXMarkerShape, new BasicStroke( 4f ), new Color( 255, 0, 0, 75 ) );
            addChild( nucleusXMarkerNode );
        }
    }
}

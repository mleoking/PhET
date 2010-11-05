package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.BucketNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * TODO: consolidate this with SchematicAtomNode, could be a subclass that adds interactivity, or a constructor arg interactive:Boolean
 * @author Sam Reid
 */
public class InteractiveSchematicAtomNode extends SchematicAtomNode2 {


    /**
     * Constructor.
     */
    public InteractiveSchematicAtomNode( final BuildAnAtomModel model, ModelViewTransform2D mvt, final BooleanProperty viewOrbitals ) {
        super( model, mvt, viewOrbitals );

        // Add the atom's nucleus location to the canvas.
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
        backLayer.addChild( nucleusXMarkerNode );

        // Add the buckets that hold the sub-atomic particles.
        BucketNode electronBucketNode = new BucketNode( model.getElectronBucket(), mvt );
        electronBucketNode.setOffset( mvt.modelToViewDouble( model.getElectronBucket().getPosition() ) );
        backLayer.addChild( electronBucketNode.getHoleLayer() );
        frontLayer.addChild( electronBucketNode.getContainerLayer() );
        BucketNode protonBucketNode = new BucketNode( model.getProtonBucket(), mvt );
        protonBucketNode.setOffset( mvt.modelToViewDouble( model.getProtonBucket().getPosition() ) );
        backLayer.addChild( protonBucketNode.getHoleLayer() );
        frontLayer.addChild( protonBucketNode.getContainerLayer() );
        BucketNode neutronBucketNode = new BucketNode( model.getNeutronBucket(), mvt );
        neutronBucketNode.setOffset( mvt.modelToViewDouble( model.getNeutronBucket().getPosition() ) );
        backLayer.addChild( neutronBucketNode.getHoleLayer() );
        frontLayer.addChild( neutronBucketNode.getContainerLayer() );
    }

    public AtomValue getAtomValue() {
        return model.getAtom().toAtomValue();
    }

    public void setAtomValue( AtomValue answer ) {
        model.setState( answer, false );
    }

}

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.view.*;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * TODO: consolidate this with SchematicAtomNode, could be a subclass that adds interactivity, or a constructor arg interactive:Boolean
 * @author Sam Reid
 */
public class InteractiveSchematicAtomNode extends PNode {
    private final BuildAnAtomModel model;

    public InteractiveSchematicAtomNode( final BuildAnAtomModel model, ModelViewTransform2D mvt, final BooleanProperty viewOrbitals ) {
        this.model=model;

        PNode backLayer = new PNode( );
        PNode particleLayer = new PNode( );
        PNode frontLayer= new PNode( );
        // Layers on the canvas.
        addChild( backLayer );
        addChild( particleLayer );
        addChild( frontLayer );

        // Put up the bounds of the model.
        //        rootNode.addChild( new PhetPPath( mvt.createTransformedShape( model.getModelViewport() ), Color.PINK, new BasicStroke( 3f ), Color.BLACK ) );

        // Add the atom's nucleus location to the canvas.
        //        Shape nucleusOutlineShape = mvt.createTransformedShape( new Ellipse2D.Double(
        //                -model.getAtom().getNucleusRadius(),
        //                -model.getAtom().getNucleusRadius(),
        //                model.getAtom().getNucleusRadius() * 2,
        //                model.getAtom().getNucleusRadius() * 2 ) );
        //        PNode nucleusOutlineNode = new PhetPPath( nucleusOutlineShape, new BasicStroke(1f), Color.RED );
        //        backLayer.addChild( nucleusOutlineNode );
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

        // Add the atom's electron shells to the canvas.
        for ( ElectronShell electronShell : model.getAtom().getElectronShells() ) {
            backLayer.addChild( new ElectronShellNode( mvt, viewOrbitals, model.getAtom(), electronShell,true ) );
        }

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

        // Add the subatomic particles.
        for ( int i = 0; i < model.numElectrons(); i++ ) {
            final int finalI = i;
            particleLayer.addChild( new ElectronNode( mvt, model.getElectron( i ) ){{
                final SimpleObserver updateVisibility = new SimpleObserver() {
                    public void update() {
                        setVisible( viewOrbitals.getValue() || !model.getAtom().containsElectron( model.getElectron( finalI ) ) );
                    }
                };
                viewOrbitals.addObserver( updateVisibility );
                updateVisibility.update();

                model.getAtom().addObserver( updateVisibility );
            }} );
        }

        for ( int i = 0; i < Math.max( model.numProtons(), model.numNeutrons() ); i++ ) {
            if ( i < model.numProtons() ) {
                particleLayer.addChild( new ProtonNode( mvt, model.getProton( i ) ) );
            }
            if ( i < model.numNeutrons() ) {
                particleLayer.addChild( new NeutronNode( mvt, model.getNeutron( i ) ) );
            }
        }
    }

    public AtomValue getGuess() {
        return model.getAtom().toAtomValue();
    }

    public void displayAnswer( AtomValue answer ) {
        model.setState(answer);
    }
}

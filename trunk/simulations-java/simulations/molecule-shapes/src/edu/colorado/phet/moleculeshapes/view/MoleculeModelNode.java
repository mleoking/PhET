// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.jme.PiccoloJMENode;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Listener;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Displays a molecule
 */
public class MoleculeModelNode extends Node {
    private MoleculeModel molecule;
    private final MoleculeJMEApplication app;
    private final Camera camera;

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<LonePairNode> lonePairNodes = new ArrayList<LonePairNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private List<Spatial> angleNodes = new ArrayList<Spatial>();

    public MoleculeModelNode( final MoleculeModel molecule, final MoleculeJMEApplication app, final Camera camera ) {
        super( "Molecule Model" );
        this.molecule = molecule;
        this.app = app;
        this.camera = camera;

        // update the UI when the molecule changes electron pairs
        molecule.addListener( new Listener() {
            public void onGroupAdded( PairGroup group ) {
                addGroup( group );
            }

            public void onGroupRemoved( PairGroup group ) {
                removeGroup( group );
            }
        } );

        // add any already-existing pair groups
        for ( PairGroup pairGroup : molecule.getGroups() ) {
            addGroup( pairGroup );
        }

        // on each frame, update our view
        app.addUpdateObserver( new SimpleObserver() {
            public void update() {
                updateView();
            }
        } );

        //Create the central atom
        AtomNode center = new AtomNode( new None<PairGroup>(), app.getAssetManager() );
        attachChild( center );
    }

    private void addGroup( PairGroup group ) {
        if ( group.isLonePair ) {
            LonePairNode lonePairNode = new LonePairNode( group, app.getAssetManager() );
            lonePairNodes.add( lonePairNode );
            attachChild( lonePairNode );
        }
        else {
            AtomNode atomNode = new AtomNode( new Some<PairGroup>( group ), app.getAssetManager() );
            atomNodes.add( atomNode );
            attachChild( atomNode );
            rebuildBonds();
            rebuildAngles();
        }
    }

    private void removeGroup( PairGroup group ) {
        if ( group.isLonePair ) {
            for ( LonePairNode lonePairNode : new ArrayList<LonePairNode>( lonePairNodes ) ) {
                // TODO: associate these more closely! (comparing positions for equality is bad)
                if ( lonePairNode.position == group.position ) {
                    lonePairNodes.remove( lonePairNode );
                    detachChild( lonePairNode );
                }
            }
        }
        else {
            for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
                // TODO: associate these more closely! (comparing positions for equality is bad)
                if ( atomNode.position == group.position ) {
                    atomNodes.remove( atomNode );
                    detachChild( atomNode );
                }
            }
        }
        rebuildBonds();
        rebuildAngles();
    }

    public void updateView() {
        for ( BondNode bondNode : bondNodes ) {
            bondNode.updateView();
        }
        rebuildAngles();
    }

    private void rebuildBonds() {
        // necessary for now since just updating their geometry shows significant errors
        // TODO: we fixed this!
        for ( BondNode bondNode : bondNodes ) {
            detachChild( bondNode );
        }
        bondNodes.clear();
        for ( PairGroup pair : molecule.getGroups() ) {
            if ( !pair.isLonePair ) {
                BondNode bondNode = new BondNode(
                        new Property<ImmutableVector3D>( new ImmutableVector3D() ), // center position
                        pair.position,
                        pair.bondOrder,
                        MoleculeShapesConstants.MODEL_BOND_RADIUS, // bond radius
                        new Some<Float>( (float) PairGroup.BONDED_PAIR_DISTANCE ), // max length
                        app,
                        camera );
                attachChild( bondNode );
                bondNodes.add( bondNode );
            }
        }
    }

    private DecimalFormat angleFormat = new DecimalFormat( "##0.0" );

    private void rebuildAngles() {
        // TODO: docs and cleanup
        Vector3f dir = camera.getLocation();
        final Vector3f transformedDirection = getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector

        for ( Spatial node : angleNodes ) {
            node.getParent().detachChild( node );
        }
        angleNodes.clear();

        // TODO: separate out bond angle feature
        if ( MoleculeShapesApplication.showBondAngles.get() ) {
            // iterate over all combinations of two pair groups
            for ( int i = 0; i < molecule.getGroups().size(); i++ ) {
                PairGroup a = molecule.getGroups().get( i );
                final ImmutableVector3D aDir = a.position.get().normalized();

                for ( int j = i + 1; j < molecule.getGroups().size(); j++ ) {
                    final PairGroup b = molecule.getGroups().get( j );
                    final ImmutableVector3D bDir = b.position.get().normalized();

                    final float brightness = BondAngleNode.calculateBrightness( aDir, bDir, transformedDirection );
                    if ( brightness == 0 ) {
                        continue;
                    }

                    final BondAngleNode bondAngleNode = new BondAngleNode( app, aDir, bDir, transformedDirection );
                    attachChild( bondAngleNode );
                    angleNodes.add( bondAngleNode );

                    Vector3f globalCenter = getWorldTransform().transformVector( bondAngleNode.getCenter(), new Vector3f() );
                    Vector3f globalMidpoint = getWorldTransform().transformVector( bondAngleNode.getMidpoint(), new Vector3f() );

                    final Vector3f screenCenter = camera.getScreenCoordinates( globalCenter );
                    final Vector3f screenMidpoint = camera.getScreenCoordinates( globalMidpoint );

                    float extensionFactor = 1.3f;
                    final Vector3f displayPoint = screenMidpoint.subtract( screenCenter ).mult( extensionFactor ).add( screenCenter );

                    String labelText = angleFormat.format( Math.acos( aDir.dot( bDir ) ) * 180 / Math.PI ) + "°";
                    PiccoloJMENode label = new PiccoloJMENode( new PText( labelText ) {{
                        setFont( new PhetFont( 16 ) );
                        setTextPaint( new Color( 1f, 1f, 1f, brightness ) );
                    }}, app.getAssetManager(), app.getInputManager() ) {{
                        setLocalTranslation( displayPoint.subtract( getWidth() / 2, getHeight() / 2, 0 ) );
                    }};
                    app.getGuiNode().attachChild( label );
                    angleNodes.add( label );
                }
            }
        }
    }
}

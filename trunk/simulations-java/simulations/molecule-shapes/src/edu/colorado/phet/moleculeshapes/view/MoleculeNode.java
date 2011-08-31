// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculeshapes.MoleculeShapesApplication;
import edu.colorado.phet.moleculeshapes.jme.PiccoloJMENode;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Listener;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * Displays a molecule
 */
public class MoleculeNode extends Node {
    private MoleculeModel molecule;
    private final MoleculeJMEApplication app;
    private final Camera camera;

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<LonePairNode> lonePairNodes = new ArrayList<LonePairNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private List<Spatial> angleNodes = new ArrayList<Spatial>();

    public MoleculeNode( final MoleculeModel molecule, final MoleculeJMEApplication app, final Camera camera ) {
        this.molecule = molecule;
        this.app = app;
        this.camera = camera;

        // update the UI when the molecule changes electron pairs
        molecule.addListener( new Listener() {
            public void onGroupAdded( PairGroup group ) {
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

            public void onGroupRemoved( PairGroup group ) {
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
            }
        } );

        app.addUpdateObserver( new SimpleObserver() {
            public void update() {
                updateView();
            }
        } );
    }

    public void updateView() {
        rebuildBonds();
        rebuildAngles();
    }

    private void rebuildBonds() {
        // get our molecule-based camera position, so we can use that to compute orientation of double/triple bonds
        // TODO: refactor some of this duplicated code out!
        Vector2f click2d = app.getInputManager().getCursorPosition();
        Vector3f click3d = camera.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();

        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f transformedPosition = getWorldTransform().transformInverseVector( click3d, new Vector3f() );

        // necessary for now since just updating their geometry shows significant errors
        for ( BondNode bondNode : bondNodes ) {
            detachChild( bondNode );
        }
        bondNodes.clear();
        for ( PairGroup pair : molecule.getGroups() ) {
            if ( !pair.isLonePair ) {
                BondNode bondNode = new BondNode( pair.position.get(), pair.bondOrder, app.getAssetManager(), transformedPosition );
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

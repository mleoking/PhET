// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
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

    private int angleIndex = 0;
    private List<ReadoutNode> angleReadouts = new ArrayList<ReadoutNode>();

    public MoleculeModelNode( final MoleculeModel molecule, final MoleculeJMEApplication app, final Camera camera ) {
        super( "Molecule Model" );
        this.molecule = molecule;
        this.app = app;
        this.camera = camera;

        // update the UI when the molecule changes electron pairs
        molecule.onGroupAdded.addListener( new VoidFunction1<PairGroup>() {
            public void apply( PairGroup group ) {
                addGroup( group );
            }
        } );
        molecule.onGroupRemoved.addListener( new VoidFunction1<PairGroup>() {
            public void apply( PairGroup group ) {
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

    private Vector3f lastMidpoint = null; // used to keep the bond angle of 180 degrees stable for 2-bond molecules

    private void rebuildAngles() {
        // TODO: docs and cleanup
        Vector3f dir = camera.getLocation();
        final Vector3f localCameraPosition = getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector

        for ( Spatial node : angleNodes ) {
            node.getParent().detachChild( node );
        }
        angleNodes.clear();

        // start handling angle nodes from the beginning
        angleIndex = 0;

        final boolean showAnglesBetweenLonePairs = MoleculeShapesProperties.allowAnglesBetweenLonePairs.get();

        // TODO: separate out bond angle feature
        if ( MoleculeShapesProperties.showBondAngles.get() ) {

            // we need to handle the 2-atom case separately for proper support of 180-degree bonds
            boolean hasTwoBonds = molecule.getBondedGroups().size() == 2;
            if ( !hasTwoBonds ) {
                // if we don't have two bonds, just ignore the last midpoint
                lastMidpoint = null;
            }

            // iterate over all combinations of two pair groups
            for ( int i = 0; i < molecule.getGroups().size(); i++ ) {
                PairGroup a = molecule.getGroups().get( i );

                // skip lone pairs if necessary
                if ( a.isLonePair && !showAnglesBetweenLonePairs ) {
                    continue;
                }

                final ImmutableVector3D aDir = a.position.get().normalized();

                for ( int j = i + 1; j < molecule.getGroups().size(); j++ ) {
                    final PairGroup b = molecule.getGroups().get( j );

                    // skip lone pairs if necessary
                    if ( b.isLonePair && !showAnglesBetweenLonePairs ) {
                        continue;
                    }

                    final ImmutableVector3D bDir = b.position.get().normalized();

                    final float brightness = BondAngleNode.calculateBrightness( aDir, bDir, localCameraPosition, molecule.getBondedGroups().size() );
                    if ( brightness == 0 ) {
                        continue;
                    }

                    final BondAngleNode bondAngleNode = new BondAngleNode( app, molecule, aDir, bDir, localCameraPosition, lastMidpoint );
                    attachChild( bondAngleNode );
                    angleNodes.add( bondAngleNode );

                    // if we have two bonds, store the last midpoint so we can keep the bond midpoint stable
                    if ( hasTwoBonds ) {
                        lastMidpoint = bondAngleNode.getMidpoint().normalize();
                    }

                    // TODO: integrate the labels with the BondAngleNode?

                    Vector3f globalCenter = getWorldTransform().transformVector( bondAngleNode.getCenter(), new Vector3f() );
                    Vector3f globalMidpoint = getWorldTransform().transformVector( bondAngleNode.getMidpoint(), new Vector3f() );

                    final Vector3f screenCenter = camera.getScreenCoordinates( globalCenter );
                    final Vector3f screenMidpoint = camera.getScreenCoordinates( globalMidpoint );

                    float extensionFactor = 1.3f;
                    final Vector3f displayPoint = screenMidpoint.subtract( screenCenter ).mult( extensionFactor ).add( screenCenter );

                    String labelText = MessageFormat.format( Strings.ANGLE__DEGREES, angleFormat.format( aDir.angleBetweenInDegrees( bDir ) ) );
                    showAngleLabel( labelText, brightness, displayPoint );
                }
            }
        }

        removeRemainingLabels();
    }

    private void showAngleLabel( final String string, final float brightness, final Vector3f displayPoint ) {

        if ( angleIndex >= angleReadouts.size() ) {

            angleReadouts.add( new ReadoutNode( new PText( "..." ) ) );

        }
        angleReadouts.get( angleIndex ).attach( string, brightness, displayPoint );
        angleIndex++;

    }

    private void removeRemainingLabels() {
        for ( int i = angleIndex; i < angleReadouts.size(); i++ ) {
            angleReadouts.get( i ).detach();
        }
    }

    /**
     * Class for readouts. We need to reuse these, because they seem to hemorrhage texture memory otherwise (memory leak on our part???)
     */
    private class ReadoutNode extends PiccoloJMENode {
        private final PText text;

        private volatile boolean attached = false;

        private ReadoutNode( PText text ) {
            super( text, app );
            this.text = text;

            text.setFont( MoleculeShapesConstants.BOND_ANGLE_READOUT_FONT );

            antialiased.set( true );

            // don't listen to any user input, for performance
            ignoreInput();
        }

        public void attach( final String string, final float brightness, final Vector3f displayPoint ) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    if ( !text.getText().equals( string ) ) {
                        text.setText( string );
                    }
                    text.setScale( app.getApproximateScale() ); // change the font size based on the sim scale
                    float[] colors = MoleculeShapesConstants.BOND_ANGLE_READOUT_COLOR.getRGBColorComponents( null );
                    text.setTextPaint( new Color( colors[0], colors[1], colors[2], brightness ) );
                    text.repaint();

                    JMEUtils.invokeLater( new Runnable() {
                        public void run() {
                            setLocalTranslation( displayPoint.subtract( getWidth() / 2, getHeight() / 2, 0 ) );

                            if ( !attached ) {
                                attached = true;
                                app.getGui().getScene().attachChild( ReadoutNode.this );
                            }
                        }
                    } );
                }
            } );
        }

        public void detach() {
            JMEUtils.invokeLater( new Runnable() {
                public void run() {
                    if ( attached ) {
                        attached = false;
                        app.getGui().getScene().detachChild( ReadoutNode.this );
                    }
                }
            } );
        }
    }
}

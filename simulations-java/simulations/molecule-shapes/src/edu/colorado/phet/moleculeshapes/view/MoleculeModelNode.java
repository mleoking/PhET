// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.module.MoleculeViewModule;
import edu.umd.cs.piccolo.nodes.PText;

import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * Displays a molecule
 */
public class MoleculeModelNode extends Node {
    private MoleculeModel molecule;
    private final JMEInputHandler inputHandler;
    private final JMEView readoutView;
    private final MoleculeViewModule module;
    private final Camera camera;

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<LonePairNode> lonePairNodes = new ArrayList<LonePairNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private List<BondAngleNode> angleNodes = new ArrayList<BondAngleNode>();

    private int angleIndex = 0;
    private List<ReadoutNode> angleReadouts = new ArrayList<ReadoutNode>();
    private AtomNode centerAtomNode;

    // add the ability to override the module's scale so we can use it for icons
    private float scaleOverride = 0;

    public MoleculeModelNode( final MoleculeModel molecule, final JMEView readoutView, final MoleculeViewModule module, final Camera camera ) {
        super( "Molecule Model" );
        this.molecule = molecule;
        inputHandler = module.getInputHandler();
        this.readoutView = readoutView;
        this.module = module;
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

        //Create the central atom
        centerAtomNode = new AtomNode( new None<PairGroup>(), module.getAssetManager() );
        attachChild( centerAtomNode );
    }

    protected void setScaleOverride( float scaleOverride ) {
        this.scaleOverride = scaleOverride;
    }

    private void addGroup( PairGroup group ) {
        if ( group.isLonePair ) {
            LonePairNode lonePairNode = new LonePairNode( group, module.getAssetManager(), module.showLonePairs );
            lonePairNodes.add( lonePairNode );
            attachChild( lonePairNode );
        }
        else {
            AtomNode atomNode = new AtomNode( new Some<PairGroup>( group ), module.getAssetManager() );
            atomNodes.add( atomNode );
            attachChild( atomNode );
            rebuildBonds();
            updateAngles();

            // add a new bond angle for every other bond
            for ( PairGroup otherGroup : molecule.getBondedGroups() ) {
                if ( otherGroup != group ) {
                    BondAngleNode bondAngleNode = new BondAngleNode( module, molecule, group, otherGroup );
                    attachChild( bondAngleNode );
                    angleNodes.add( bondAngleNode );
                }
            }
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

            // remove all angle nodes that involve the specified bond
            for ( BondAngleNode angleNode : new ArrayList<BondAngleNode>( angleNodes ) ) {
                if ( angleNode.getA() == group || angleNode.getB() == group ) {
                    JMEUtils.discardTree( angleNode );
                    angleNodes.remove( angleNode );
                }
            }
        }
        rebuildBonds();
        updateAngles();
    }

    public void updateView() {
        for ( BondNode bondNode : bondNodes ) {
            bondNode.updateView();
        }
        updateAngles();
    }

    private void rebuildBonds() {
        // basically remove all of the bonds and rebuild them
        for ( BondNode bondNode : bondNodes ) {
            JMEUtils.discardTree( bondNode );
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
                        module,
                        camera );
                attachChild( bondNode );
                bondNodes.add( bondNode );
            }
        }
    }

    private DecimalFormat angleFormat = new DecimalFormat( "##0.0" );

    private Vector3f lastMidpoint = null; // used to keep the bond angle of 180 degrees stable for 2-bond molecules

    private void updateAngles() {
        // get the camera location, so that we can correctly shade the bond angles
        Vector3f dir = camera.getLocation();
        final Vector3f localCameraPosition = getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector

        // we need to handle the 2-atom case separately for proper support of 180-degree bonds
        boolean hasTwoBonds = molecule.getBondedGroups().size() == 2;
        if ( !hasTwoBonds ) {
            // if we don't have two bonds, just ignore the last midpoint
            lastMidpoint = null;
        }

        for ( BondAngleNode angleNode : angleNodes ) {
            angleNode.updateView( localCameraPosition, lastMidpoint );

            // if we have two bonds, store the last midpoint so we can keep the bond midpoint stable
            if ( hasTwoBonds ) {
                lastMidpoint = angleNode.getMidpoint().normalize();
            }
        }

        // start handling angle nodes from the beginning
        angleIndex = 0;

        // calculate position changes due to taking lone-pair distances into account
        boolean hasLonePair = !molecule.getLonePairs().isEmpty();
        final double timeEpsilon = 0.1; // a small amount of time to consider the forces
        Map<PairGroup, ImmutableVector3D> lonePairModifiedPositions = new HashMap<PairGroup, ImmutableVector3D>();
        if ( hasLonePair ) {
            for ( PairGroup group : molecule.getBondedGroups() ) {
                ImmutableVector3D position = group.position.get();
                for ( PairGroup otherGroup : molecule.getGroups() ) {
                    if ( otherGroup == group ) { continue; }

                    double lonePairFactor = otherGroup.isLonePair ? 1.2 : 1;

                    // add in impulse calculated with true from-central-atom distances
                    position = position.plus( group.getRepulsionImpulse( otherGroup, timeEpsilon, 1 ) ).times( lonePairFactor );
                }
                lonePairModifiedPositions.put( group, position.normalized() );
            }
        }

        // TODO: separate out bond angle feature
        if ( module.showBondAngles.get() ) {
            for ( BondAngleNode bondAngleNode : angleNodes ) {
                PairGroup a = bondAngleNode.getA();
                PairGroup b = bondAngleNode.getB();

                final ImmutableVector3D aDir = a.position.get().normalized();
                final ImmutableVector3D bDir = b.position.get().normalized();

                final float brightness = BondAngleNode.calculateBrightness( aDir, bDir, localCameraPosition, molecule.getBondedGroups().size() );
                if ( brightness == 0 ) {
                    continue;
                }

                // TODO: integrate the labels with the BondAngleNode?

                Vector3f globalCenter = getWorldTransform().transformVector( bondAngleNode.getCenter(), new Vector3f() );
                Vector3f globalMidpoint = getWorldTransform().transformVector( bondAngleNode.getMidpoint(), new Vector3f() );

                final Vector3f screenCenter = camera.getScreenCoordinates( globalCenter );
                final Vector3f screenMidpoint = camera.getScreenCoordinates( globalMidpoint );

                float extensionFactor = 1.3f;
                final Vector3f displayPoint = screenMidpoint.subtract( screenCenter ).mult( extensionFactor ).add( screenCenter );

                double angle = aDir.angleBetweenInDegrees( bDir );
                String labelText;
                if ( hasLonePair ) {
                    final double angleEpsilon = 0.05; // the change in angle due to lone pairs should have a difference larger than this for us to show a difference

                    double modifiedAngle = lonePairModifiedPositions.get( a ).angleBetweenInDegrees( lonePairModifiedPositions.get( b ) );
                    String formatString = Strings.ANGLE__DEGREES;

                    // for the Colorado and Utah studies, enable the "less than" and "greater than" text
                    if ( SimSharingEvents.isEnabled() ) {
                        if ( modifiedAngle - angle > angleEpsilon ) {
                            // lone-pair angle version is larger
                            formatString = Strings.ANGLE__GREATER_THAN_DEGREES;
                        }
                        else if ( modifiedAngle - angle < -angleEpsilon ) {
                            // lone-pair angle version is smaller
                            formatString = Strings.ANGLE__LESS_THAN_DEGREES;
                        }
                        else {
                            // close enough to report no difference
                            formatString = Strings.ANGLE__DEGREES;
                        }
                    }
                    labelText = MessageFormat.format( formatString, angleFormat.format( angle ) );
                }
                else {
                    labelText = MessageFormat.format( Strings.ANGLE__DEGREES, angleFormat.format( angle ) );
                }

                showAngleLabel( labelText, brightness, displayPoint );
            }
        }

        // remove any unused labels, since we need to cache them
        removeRemainingLabels();
    }

    public void detachReadouts() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JMEUtils.invokeLater( new Runnable() {
                    public void run() {
                        for ( ReadoutNode angleReadout : angleReadouts ) {
                            angleReadout.detach();
                        }
                    }
                } );
            }
        } );
        for ( ReadoutNode angleReadout : angleReadouts ) {
            angleReadout.detach();
        }
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

    public AtomNode getCenterAtomNode() {
        return centerAtomNode;
    }

    /**
     * Class for readouts. We need to reuse these, because they seem to hemorrhage texture memory otherwise (memory leak on our part???)
     */
    private class ReadoutNode extends PiccoloJMENode {
        private final PText text;

        private volatile boolean attached = false;

        private ReadoutNode( PText text ) {
            super( text, inputHandler, module );
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
                    text.setScale( scaleOverride == 0 ? module.getApproximateScale() : scaleOverride ); // change the font size based on the sim scale
                    float[] colors = MoleculeShapesColor.BOND_ANGLE_READOUT.get().getRGBColorComponents( null );
                    text.setTextPaint( new Color( colors[0], colors[1], colors[2], brightness ) );
                    text.repaint(); // TODO: this should not be necessary, however it fixes the bond angle labels. JME-Piccolo repaint issue?

                    JMEUtils.invokeLater( new Runnable() {
                        public void run() {
                            setLocalTranslation( displayPoint.subtract( getWidth() / 2, getHeight() / 2, 0 ) );

                            if ( !attached ) {
                                attached = true;
                                readoutView.getScene().attachChild( ReadoutNode.this );
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
                        readoutView.getScene().detachChild( ReadoutNode.this );
                    }
                }
            } );
        }
    }
}

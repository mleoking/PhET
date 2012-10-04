// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.common.phetcommon.math.SphereF;
import edu.colorado.phet.common.phetcommon.math.Triangle3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.ThreadedPlanarPiccoloNode;
import edu.colorado.phet.lwjglphet.shapes.ObjMesh;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.control.BondTypeOverlayNode;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.Molecule;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.tabs.MoleculeViewTab;
import edu.colorado.phet.moleculeshapes.tabs.moleculeshapes.MoleculeShapesTab;
import edu.colorado.phet.moleculeshapes.tabs.realmolecules.RealMoleculesTab;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a molecule
 */
public class MoleculeModelNode extends GLNode {
    private Molecule molecule;
    private final GLNode readoutLayer;
    private final MoleculeViewTab tab;

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<LonePairNode> lonePairNodes = new ArrayList<LonePairNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private List<BondAngleNode> angleNodes = new ArrayList<BondAngleNode>();

    private int angleIndex = 0;
    private List<ReadoutNode> angleReadouts = new ArrayList<ReadoutNode>();
    private AtomNode centerAtomNode;

    // add the ability to override the module's scale so we can use it for icons
    private float scaleOverride = 0;

    public MoleculeModelNode( final Molecule molecule, final GLNode readoutLayer, final MoleculeViewTab tab ) {
        this.molecule = molecule;
        this.readoutLayer = readoutLayer;
        this.tab = tab;

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

        molecule.onBondAdded.addListener( new VoidFunction1<Bond<PairGroup>>() {
            public void apply( Bond<PairGroup> bond ) {
                addBond( bond );
            }
        } );
        molecule.onBondRemoved.addListener( new VoidFunction1<Bond<PairGroup>>() {
            public void apply( Bond<PairGroup> bond ) {
                removeBond( bond );
            }
        } );

        // add any already-existing pair groups
        for ( PairGroup group : molecule.getRadialGroups() ) {
            addGroup( group );
        }

        // add terminal lone pairs
        for ( PairGroup lonePair : molecule.getDistantLonePairs() ) {
            addGroup( lonePair );
        }

        //Create the central atom
        if ( !molecule.isReal() ) {
            centerAtomNode = new AtomNode( new None<PairGroup>() );
        }
        else {
            centerAtomNode = new AtomNode( new Some<PairGroup>( molecule.getCentralAtom() ) );
        }
        addChild( centerAtomNode );
    }

    public GLNode intersect( Ray3F ray ) {
        GLNode result = null;
        float distance = Float.POSITIVE_INFINITY;

        for ( AtomNode atomNode : atomNodes ) {
            Ray3F transformedRay = atomNode.getGlobalTransform().inverseRay( ray );

            SphereF.SphereIntersectionResult intersection = new SphereF( Vector3F.ZERO, atomNode.getRadius() ).intersect( transformedRay, 0.00001f );
            if ( intersection != null ) {
                if ( intersection.distance < distance ) {
                    distance = intersection.distance;
                    result = atomNode;
                }
            }
        }

        ObjMesh lonePairMesh = LonePairNode.getMesh();

        for ( LonePairNode lonePairNode : lonePairNodes ) {
            Ray3F transformedRay = lonePairNode.getGlobalTransform().inverseRay( ray );

            // first check against its bounding box for computational speed
            if ( lonePairMesh.getBoundingBox().intersectedBy( transformedRay ) ) {

                // then do the naive "check all the triangles" approach
                for ( Triangle3F triangle : lonePairMesh.getTriangles() ) {
                    Option<Triangle3F.TriangleIntersectionResult> triangleIntersectionResults = triangle.intersectWith( transformedRay );
                    if ( triangleIntersectionResults.isSome() ) {
                        float myDistance = triangleIntersectionResults.get().point.distance( transformedRay.pos );
                        if ( myDistance < distance ) {
                            distance = myDistance;
                            result = lonePairNode;
                        }
                        break;
                    }
                }
            }
        }

        return result;
    }

    protected void setScaleOverride( float scaleOverride ) {
        this.scaleOverride = scaleOverride;
    }

    private void addBond( Bond<PairGroup> bond ) {
        rebuildBonds();
        updateAngles();
    }

    private void removeBond( Bond<PairGroup> bond ) {
        rebuildBonds();
        updateAngles();
    }

    private void addGroup( final PairGroup group ) {
        // ignore the central atom (for now)
        if ( group == molecule.getCentralAtom() ) {
            return;
        }
        if ( group.isLonePair ) {
            final PairGroup parentAtom = molecule.getParent( group );
            Property<Boolean> visibilityProperty = parentAtom == molecule.getCentralAtom() ? tab.showLonePairs : tab.showAllLonePairs;
            LonePairNode lonePairNode = new LonePairNode( group, parentAtom, visibilityProperty );
            lonePairNodes.add( lonePairNode );
            addChild( lonePairNode );
        }
        else {
            AtomNode atomNode = new AtomNode( new Some<PairGroup>( group ) );
            atomNodes.add( atomNode );
            addChild( atomNode );

            // TODO: why are these necessary for the bond type control nodes? remove this after fix!
            rebuildBonds();
            updateAngles();

            // TODO: change this to where it deals only with bonds?
            // add a new bond angle for every other bond
            for ( PairGroup otherGroup : molecule.getRadialAtoms() ) {
                if ( otherGroup != group ) {
                    BondAngleNode bondAngleNode = new BondAngleNode( tab, molecule, group, otherGroup );
                    addChild( bondAngleNode );
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
                    removeChild( lonePairNode );
                }
            }
        }
        else {
            for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
                // TODO: associate these more closely! (comparing positions for equality is bad)
                if ( atomNode.position == group.position ) {
                    atomNodes.remove( atomNode );
                    removeChild( atomNode );
                }
            }

            // remove all angle nodes that involve the specified bond
            for ( BondAngleNode angleNode : new ArrayList<BondAngleNode>( angleNodes ) ) {
                if ( angleNode.getA() == group || angleNode.getB() == group ) {
                    LWJGLUtils.discardTree( angleNode );
                    angleNodes.remove( angleNode );
                }
            }
        }
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
            LWJGLUtils.discardTree( bondNode );
        }
        bondNodes.clear();
        for ( final PairGroup atom : molecule.getRadialAtoms() ) {
            BondNode bondNode = new BondNode(
                    new Property<Vector3D>( new Vector3D() ), // center position
                    atom.position,
                    // TODO: redo bonds as above so we can remove this junk!
                    FunctionalUtils.first( molecule.getBonds( atom ), new Function1<Bond<PairGroup>, Boolean>() {
                        public Boolean apply( Bond<PairGroup> bond ) {
                            return bond.getOtherAtom( atom ) == molecule.getCentralAtom();
                        }
                    } ).get().order,
                    MoleculeShapesConstants.MODEL_BOND_RADIUS, // bond radius
                    molecule.getMaximumBondLength(), // max length
                    tab );
            if ( this instanceof BondTypeOverlayNode ) {
                // TODO: refactor this?
                bondNode.setFixedCamera( true );
            }
            addChild( bondNode );
            bondNodes.add( bondNode );
        }
    }

    private DecimalFormat angleFormat = new DecimalFormat( "##0.0" );

    private Vector3F lastMidpoint = null; // used to keep the bond angle of 180 degrees stable for 2-bond molecules

    private void updateAngles() {
        // get the camera location, so that we can correctly shade the bond angles
        Vector3F dir = tab.getCameraRay( 0, 0 ).pos;
        final Vector3F localCameraPosition = transform.inverseNormal( dir ).normalized(); // transpose trick to transform a unit vector

        // we need to handle the 2-atom case separately for proper support of 180-degree bonds
        boolean hasTwoBonds = molecule.getRadialAtoms().size() == 2;
        if ( !hasTwoBonds ) {
            // if we don't have two bonds, just ignore the last midpoint
            lastMidpoint = null;
        }

        for ( BondAngleNode angleNode : angleNodes ) {
            angleNode.updateView( localCameraPosition, lastMidpoint );

            // if we have two bonds, store the last midpoint so we can keep the bond midpoint stable
            if ( hasTwoBonds ) {
                lastMidpoint = angleNode.getMidpoint().normalized();
            }
        }

        // start handling angle nodes from the beginning
        angleIndex = 0;

        // calculate position changes due to taking lone-pair distances into account
        boolean hasLonePair = !molecule.getRadialLonePairs().isEmpty();
        final double timeEpsilon = 0.1; // a small amount of time to consider the forces
        Map<PairGroup, Vector3D> lonePairModifiedPositions = new HashMap<PairGroup, Vector3D>();
        if ( hasLonePair ) {
            for ( PairGroup group : molecule.getRadialAtoms() ) {
                Vector3D position = group.position.get();
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
        if ( tab.showBondAngles.get() ) {
            for ( BondAngleNode bondAngleNode : angleNodes ) {
                PairGroup a = bondAngleNode.getA();
                PairGroup b = bondAngleNode.getB();

                final Vector3D aDir = a.position.get().normalized();
                final Vector3D bDir = b.position.get().normalized();

                final float brightness = BondAngleNode.calculateBrightness( aDir, bDir, localCameraPosition, molecule.getRadialAtoms().size() );
                if ( brightness == 0 ) {
                    continue;
                }

                // TODO: integrate the labels with the BondAngleNode?

                LWJGLTransform globalTransform = bondAngleNode.getGlobalTransform();

                Vector3F globalCenter = globalTransform.transformPosition( bondAngleNode.getCenter() );
                Vector3F globalMidpoint = globalTransform.transformPosition( bondAngleNode.getMidpoint() );

//                System.out.println( "globalCenter = " + globalCenter );
//                System.out.println( "globalCenter = " + globalMidpoint );

                final Vector3F screenCenter = new Vector3F( tab.getScreenCoordinatesFromViewPoint( globalCenter ) );
                final Vector3F screenMidpoint = new Vector3F( tab.getScreenCoordinatesFromViewPoint( globalMidpoint ) );

//                System.out.println( "screenCenter = " + screenCenter );
//                System.out.println( "screenMidpoint = " + screenMidpoint );

                // add a slight extension for larger font sizes
                float extensionFactor = 1.3f * ( ( tab instanceof RealMoleculesTab ) ? 1.1f : 1.05f );
                final Vector3F displayPoint = screenMidpoint.minus( screenCenter ).times( extensionFactor ).plus( screenCenter );

                double angle = aDir.angleBetweenInDegrees( bDir );
                String labelText;
                if ( hasLonePair ) {
                    final double angleEpsilon = 0.05; // the change in angle due to lone pairs should have a difference larger than this for us to show a difference

                    // TODO: NullPointerException here. to reproduce, build 5 single bonds, 1 lone pair, turn on angles, and middle-kill a bond
                    double modifiedAngle = lonePairModifiedPositions.get( a ).angleBetweenInDegrees( lonePairModifiedPositions.get( b ) );
                    String formatString = Strings.ANGLE__DEGREES;

                    // for the Colorado and Utah studies, enable the "less than" and "greater than" text
                    if ( SimSharingManager.getInstance().isEnabled() && ( SimSharingManager.getInstance().getStudyName().equals( "colorado" ) || SimSharingManager.getInstance().getStudyName().equals( "utah" ) ) ) {
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
        for ( ReadoutNode angleReadout : angleReadouts ) {
            angleReadout.detach();
        }
    }

    private void showAngleLabel( final String string, final float brightness, final Vector3F displayPoint ) {

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
    private class ReadoutNode extends ThreadedPlanarPiccoloNode {
        private final PText text;

        private volatile boolean attached = false;

        private ReadoutNode( PText text ) {
            super( text );
            this.text = text;

            text.setFont( MoleculeShapesConstants.BOND_ANGLE_READOUT_FONT );
        }

        public void attach( final String string, final float brightness, final Vector3F displayPoint ) {
            // TODO: after completion, check threads that call this
            float extraSizeFactor = ( tab instanceof MoleculeShapesTab )
                                    ? ( ( (MoleculeShapesTab) tab ).isBasicsVersion() ? 1.2f : 1 )
                                    : 1.5f;
            if ( !text.getText().equals( string ) ) {
                text.setText( string );
            }
            float textScale = ( scaleOverride == 0 ? tab.getApproximateScale() : scaleOverride ) * extraSizeFactor;
            text.setScale( textScale ); // change the font size based on the sim scale
            float[] colors = MoleculeShapesColor.BOND_ANGLE_READOUT.get().getRGBColorComponents( null );
            text.setTextPaint( new Color( colors[0], colors[1], colors[2], brightness ) );
            text.repaint(); // TODO: this should not be necessary, however it fixes the bond angle labels. JME-Piccolo repaint issue?
            repaint();

            transform.set( Matrix4F.translation( displayPoint.minus( getWidth() / 2, getHeight() / 2, 0 ) ) );

            if ( !attached ) {
                attached = true;
                readoutLayer.addChild( ReadoutNode.this );
            }
        }

        public void detach() {
            if ( attached ) {
                attached = false;
                readoutLayer.removeChild( ReadoutNode.this );
            }
        }
    }
}

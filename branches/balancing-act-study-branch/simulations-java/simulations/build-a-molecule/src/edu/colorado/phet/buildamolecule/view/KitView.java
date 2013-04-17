// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.buildamolecule.model.Atom2D;
import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.Molecule;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.MODEL_VIEW_TRANSFORM;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.ParameterKey;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitView {
    private PNode topLayer = new PNode();
    private PNode metadataLayer = new PNode();
    private PNode atomLayer = new PNode();
    private PNode bottomLayer = new PNode();

    private final Kit kit;
    private final BuildAMoleculeCanvas canvas;

    private Map<Molecule, MoleculeMetadataNode> metadataMap = new HashMap<Molecule, MoleculeMetadataNode>();
    private Map<Molecule, MoleculeBondContainerNode> bondMap = new HashMap<Molecule, MoleculeBondContainerNode>();

    // store the node-atom relationships
    private Map<Atom, AtomNode> atomNodeMap = new HashMap<Atom, AtomNode>();

    public KitView( final Kit kit, BuildAMoleculeCanvas canvas ) {
        this.kit = kit;
        this.canvas = canvas;

        for ( Bucket bucket : kit.getBuckets() ) {
            BucketView bucketView = new BucketView( bucket, MODEL_VIEW_TRANSFORM, Color.BLACK, BucketView.DEFAULT_LABEL_FONT );

            topLayer.addChild( bucketView.getFrontNode() );
            bottomLayer.addChild( bucketView.getHoleNode() );

            for ( final Atom2D atom : bucket.getAtoms() ) {
                final AtomNode atomNode = new AtomNode( atom );
                atomNodeMap.put( atom, atomNode );
                atomLayer.addChild( atomNode );

                // Add a drag listener that will move the model element when the user
                // drags this atom.
                atomNode.addInputEventListener( new SimSharingDragHandler( UserComponentChain.chain( UserComponent.atom, atom.getId() ),
                                                                           UserComponentTypes.sprite, true ) {
                    @Override protected void startDrag( PInputEvent event ) {
                        super.startDrag( event );
                        atom.setUserControlled( true );

                        // move the atom (and its entire molecule) to the front when it starts being dragged
                        Molecule molecule = kit.getMolecule( atom );
                        if ( molecule != null ) {
                            for ( Atom moleculeAtom : molecule.getAtoms() ) {
                                atomNodeMap.get( moleculeAtom ).moveToFront();
                            }
                        }
                        else {
                            atomNode.moveToFront();
                        }
                    }

                    @Override public void mouseDragged( PInputEvent event ) {
                        super.mouseDragged( event );
                        PDimension delta = event.getDeltaRelativeTo( atomNode.getParent() );
                        Vector2D modelDelta = MODEL_VIEW_TRANSFORM.viewToModelDelta( new Vector2D( delta.width, delta.height ) );
                        kit.atomDragged( atom, modelDelta );
                    }

                    @Override protected void endDrag( PInputEvent event ) {
                        super.endDrag( event );
                        atom.setUserControlled( false );
                    }

                    @Override protected ParameterSet getParametersForAllEvents( PInputEvent event ) {
                        return super.getParametersForAllEvents( event )
                                .with( ParameterKey.atomId, atom.getId() )
                                .with( ParameterKey.atomElement, atom.getSymbol() );
                    }
                } );
            }
        }

        // handle molecule creation and destruction
        kit.addMoleculeListener( new Kit.MoleculeAdapter() {
            @Override
            public void addedMolecule( Molecule molecule ) {
                MoleculeMetadataNode moleculeMetadataNode = new MoleculeMetadataNode( kit, molecule );
                metadataLayer.addChild( moleculeMetadataNode );
                metadataMap.put( molecule, moleculeMetadataNode );

                if ( BuildAMoleculeApplication.allowBondBreaking.get() ) {
                    addMoleculeBondNodes( molecule );
                }
            }

            @Override
            public void removedMolecule( Molecule molecule ) {
                MoleculeMetadataNode moleculeMetadataNode = metadataMap.get( molecule );
                moleculeMetadataNode.destruct();
                metadataLayer.removeChild( moleculeMetadataNode );
                metadataMap.remove( molecule );

                if ( BuildAMoleculeApplication.allowBondBreaking.get() ) {
                    removeMoleculeBondNodes( molecule );
                }
            }
        } );

        // support removing bonds for molecules
        BuildAMoleculeApplication.allowBondBreaking.addObserver( new SimpleObserver() {
            public void update() {
                if ( BuildAMoleculeApplication.allowBondBreaking.get() ) {
                    // enabled, so add in bond nodes
                    for ( Molecule molecule : metadataMap.keySet() ) {
                        addMoleculeBondNodes( molecule );
                    }
                }
                else {
                    // disabled, so remove bond nodes
                    for ( Molecule molecule : bondMap.keySet() ) {
                        removeMoleculeBondNodes( molecule );
                    }
                }
            }
        } );

        // update visibility based on the kit visibility
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                Boolean visible = kit.visible.get();
                topLayer.setVisible( visible );
                metadataLayer.setVisible( visible );
                atomLayer.setVisible( visible );
                bottomLayer.setVisible( visible );
            }
        } );
    }

    public void addMoleculeBondNodes( Molecule molecule ) {
        MoleculeBondContainerNode moleculeBondContainerNode = new MoleculeBondContainerNode( kit, molecule, canvas );
        metadataLayer.addChild( moleculeBondContainerNode );
        bondMap.put( molecule, moleculeBondContainerNode );
    }

    public void removeMoleculeBondNodes( Molecule molecule ) {
        MoleculeBondContainerNode moleculeBondContainerNode = bondMap.get( molecule );
        moleculeBondContainerNode.destruct();
        metadataLayer.removeChild( moleculeBondContainerNode );
        bondMap.remove( molecule );
    }

    public PNode getTopLayer() {
        return topLayer;
    }

    public PNode getMetadataLayer() {
        return metadataLayer;
    }

    public PNode getAtomLayer() {
        return atomLayer;
    }

    public PNode getBottomLayer() {
        return bottomLayer;
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.buildamolecule.model.AtomModel;
import edu.colorado.phet.buildamolecule.model.Bucket;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitView {
    private PNode topLayer = new PNode();
    private PNode metadataLayer = new PNode();
    private PNode atomLayer = new PNode();
    private PNode bottomLayer = new PNode();

    private final Kit kit;
    private final ModelViewTransform mvt;
    private final BuildAMoleculeCanvas canvas;

    private Map<MoleculeStructure, MoleculeMetadataNode> metadataMap = new HashMap<MoleculeStructure, MoleculeMetadataNode>();
    private Map<MoleculeStructure, MoleculeBondContainerNode> bondMap = new HashMap<MoleculeStructure, MoleculeBondContainerNode>();

    public KitView( final Frame parentFrame, final Kit kit, final ModelViewTransform mvt, BuildAMoleculeCanvas canvas ) {
        this.kit = kit;
        this.mvt = mvt;
        this.canvas = canvas;

        for ( Bucket bucket : kit.getBuckets() ) {
            BucketView bucketView = new BucketView( bucket, mvt );

            topLayer.addChild( bucketView.getContainerLayer() );
            bottomLayer.addChild( bucketView.getHoleLayer() );

            for ( final AtomModel atom : bucket.getAtoms() ) {
                final AtomNode atomNode = new AtomNode( mvt, atom );
                atomLayer.addChild( atomNode );

                // Add a drag listener that will move the model element when the user
                // drags this atom.
                atomNode.addInputEventListener( new PDragEventHandler() {
                    @Override
                    protected void startDrag( PInputEvent event ) {
                        super.startDrag( event );
                        atom.setUserControlled( true );
                    }

                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        PDimension delta = event.getDeltaRelativeTo( atomNode.getParent() );
                        ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( delta.width, delta.height ) );
                        kit.atomDragged( atom, modelDelta );
                    }

                    @Override
                    protected void endDrag( PInputEvent event ) {
                        super.endDrag( event );
                        atom.setUserControlled( false );
                    }
                } );
            }
        }

        // handle molecule creation and destruction
        kit.addMoleculeListener( new Kit.MoleculeAdapter() {
            @Override
            public void addedMolecule( MoleculeStructure moleculeStructure ) {
                MoleculeMetadataNode moleculeMetadataNode = new MoleculeMetadataNode( parentFrame, kit, moleculeStructure, mvt );
                metadataLayer.addChild( moleculeMetadataNode );
                metadataMap.put( moleculeStructure, moleculeMetadataNode );

                if ( BuildAMoleculeApplication.allowBondBreaking.getValue() ) {
                    addMoleculeBondNodes( moleculeStructure );
                }
            }

            @Override
            public void removedMolecule( MoleculeStructure moleculeStructure ) {
                MoleculeMetadataNode moleculeMetadataNode = metadataMap.get( moleculeStructure );
                moleculeMetadataNode.destruct();
                metadataLayer.removeChild( moleculeMetadataNode );
                metadataMap.remove( moleculeStructure );

                if ( BuildAMoleculeApplication.allowBondBreaking.getValue() ) {
                    removeMoleculeBondNodes( moleculeStructure );
                }
            }
        } );

        // support removing bonds for molecules
        BuildAMoleculeApplication.allowBondBreaking.addObserver( new SimpleObserver() {
            public void update() {
                if ( BuildAMoleculeApplication.allowBondBreaking.getValue() ) {
                    // enabled, so add in bond nodes
                    for ( MoleculeStructure moleculeStructure : metadataMap.keySet() ) {
                        addMoleculeBondNodes( moleculeStructure );
                    }
                }
                else {
                    // disabled, so remove bond nodes
                    for ( MoleculeStructure moleculeStructure : bondMap.keySet() ) {
                        removeMoleculeBondNodes( moleculeStructure );
                    }
                }
            }
        } );

        // update visibility based on the kit visibility
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                Boolean visible = kit.visible.getValue();
                topLayer.setVisible( visible );
                metadataLayer.setVisible( visible );
                atomLayer.setVisible( visible );
                bottomLayer.setVisible( visible );
            }
        } );
    }

    public void addMoleculeBondNodes( MoleculeStructure moleculeStructure ) {
        MoleculeBondContainerNode moleculeBondContainerNode = new MoleculeBondContainerNode( kit, moleculeStructure, mvt, canvas );
        metadataLayer.addChild( moleculeBondContainerNode );
        bondMap.put( moleculeStructure, moleculeBondContainerNode );
    }

    public void removeMoleculeBondNodes( MoleculeStructure moleculeStructure ) {
        MoleculeBondContainerNode moleculeBondContainerNode = bondMap.get( moleculeStructure );
        moleculeBondContainerNode.destruct();
        metadataLayer.removeChild( moleculeBondContainerNode );
        bondMap.remove( moleculeStructure );
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

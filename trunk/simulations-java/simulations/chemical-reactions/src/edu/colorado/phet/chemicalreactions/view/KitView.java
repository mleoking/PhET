// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.view;

import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.chemicalreactions.model.Atom;
import edu.colorado.phet.chemicalreactions.model.Kit;
import edu.colorado.phet.chemicalreactions.model.Molecule;
import edu.colorado.phet.chemicalreactions.model.MoleculeBucket;
import edu.colorado.phet.chemicalreactions.module.ChemicalReactionsCanvas;
import edu.colorado.phet.chemistry.nodes.LabeledAtomNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM;

/**
 * Shows a kit (series of buckets full of different types of atoms)
 */
public class KitView {
    private PNode topLayer = new PNode();
    private PNode metadataLayer = new PNode();
    private PNode atomLayer = new PNode();
    private PNode bottomLayer = new PNode();

    private final Kit kit;
    private final ChemicalReactionsCanvas canvas;

    // store the node-atom relationships
    private Map<Atom, LabeledAtomNode> atomNodeMap = new HashMap<Atom, LabeledAtomNode>();

    public KitView( final Kit kit, ChemicalReactionsCanvas canvas ) {
        this.kit = kit;
        this.canvas = canvas;

        for ( MoleculeBucket bucket : kit.getBuckets() ) {
            MoleculeBucketNode bucketView = new MoleculeBucketNode( bucket );

            topLayer.addChild( bucketView.getFrontNode() );
            bottomLayer.addChild( bucketView.getHoleNode() );

            for ( final Atom atom : bucket.getAtoms() ) {
                final LabeledAtomNode atomNode = new LabeledAtomNode( atom.getElement() );
                atomNodeMap.put( atom, atomNode );
                atomLayer.addChild( atomNode );

                // Add a drag listener that will move the model element when the user
                // drags this atom.
                atomNode.addInputEventListener( new PDragEventHandler() {
                    @Override
                    protected void startDrag( PInputEvent event ) {
                        super.startDrag( event );
//                        atom.setUserControlled( true );

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

                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        PDimension delta = event.getDeltaRelativeTo( atomNode.getParent() );
                        ImmutableVector2D modelDelta = MODEL_VIEW_TRANSFORM.viewToModelDelta( new ImmutableVector2D( delta.width, delta.height ) );
//                        kit.atomDragged( atom, modelDelta );
                    }

                    @Override
                    protected void endDrag( PInputEvent event ) {
                        super.endDrag( event );
//                        atom.setUserControlled( false );
                    }
                } );
            }
        }

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

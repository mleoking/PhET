// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.buckets.AtomModel;
import edu.colorado.phet.buildamolecule.model.buckets.Bucket;
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
    private PNode atomLayer = new PNode();
    private PNode bottomLayer = new PNode();

    public KitView( final Kit kit, final ModelViewTransform mvt ) {
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

        // update visibility based on the kit visibility
        kit.visible.addObserver( new SimpleObserver() {
            public void update() {
                topLayer.setVisible( kit.visible.getValue() );
                atomLayer.setVisible( kit.visible.getValue() );
                bottomLayer.setVisible( kit.visible.getValue() );
            }
        } );
    }

    public PNode getTopLayer() {
        return topLayer;
    }

    public PNode getAtomLayer() {
        return atomLayer;
    }

    public PNode getBottomLayer() {
        return bottomLayer;
    }
}

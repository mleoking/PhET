// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
class BiomoleculeDragHandler extends PDragEventHandler {
    private final MobileBiomolecule biomolecule;
    private final PNode pNode;
    private final ModelViewTransform mvt;

    public BiomoleculeDragHandler( MobileBiomolecule biomolecule, PNode node, ModelViewTransform mvt ) {
        this.biomolecule = biomolecule;
        pNode = node;
        this.mvt = mvt;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        // The user is moving this, so they have control.
        biomolecule.userControlled.set( true );
    }

    @Override
    public void mouseDragged( PInputEvent event ) {
        PDimension viewDelta = event.getDeltaRelativeTo( pNode.getParent() );
        ImmutableVector2D modelDelta = mvt.viewToModelDelta( new ImmutableVector2D( viewDelta ) );
        biomolecule.translate( modelDelta );
    }

    @Override protected void endDrag( PInputEvent event ) {
        super.endDrag( event );
        // The user is no longer moving this, so they have relinquished control.
        biomolecule.userControlled.set( false );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author John Blanco
 */
public class BiomoleculeCreatorNode extends PNode {

    private MobileBiomolecule biomolecule = null;
    private final ModelViewTransform mvt;

    public BiomoleculeCreatorNode( PNode clickToAddNode, ModelViewTransform mvt, final Function1<Point2D, MobileBiomolecule> createMolecule, boolean goInvisibleOnAdd ) {
        this.mvt = mvt;
        clickToAddNode.addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                biomolecule = createMolecule.apply( getModelPosition( event.getCanvasPosition() ) );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                biomolecule.setPosition( getModelPosition( event.getCanvasPosition() ) );
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                // The user has released this node.
                biomolecule.release();
                biomolecule = null;
            }
        } );
        addChild( clickToAddNode );
    }

    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D getModelPosition( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
//        canvas.getPhetRootNode().screenToWorld( worldPos );
//        worldPos = new Vector2D( worldPos ).add( positioningOffset ).toPoint2D();
//        Point2D modelPos = mvt.viewToModel( worldPos );
//        return modelPos;
        return null;
    }
}

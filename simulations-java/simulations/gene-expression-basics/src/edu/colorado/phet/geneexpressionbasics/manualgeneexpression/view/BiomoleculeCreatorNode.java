// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This class defines a node that can be used in the view to create biomolecules
 * in the model.  It is intended for use in control panels.  It is generalized
 * so that the parameters allow it to create any sort of biomolecule.
 *
 * @author John Blanco
 */
public class BiomoleculeCreatorNode extends PComposite {

    private MobileBiomolecule biomolecule = null;
    private final ModelViewTransform mvt;
    private final PhetPCanvas canvas;

    public BiomoleculeCreatorNode( PNode appearanceNode, PhetPCanvas canvas, ModelViewTransform mvt,
                                   final Function1<Point2D, MobileBiomolecule> moleculeCreator, boolean goInvisibleOnAdd ) {
        this.canvas = canvas;
        this.mvt = mvt;
        // Cursor handler.
        addInputEventListener( new CursorHandler() );
        // Add the handler that will add elements to the model when clicked.
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
//                biomolecule = moleculeCreator.apply( getModelPosition( event.getCanvasPosition() ) );
                biomolecule = moleculeCreator.apply( new Point2D.Double( 6000, 2000 ) );
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
        addChild( appearanceNode );
    }

    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D getModelPosition( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
        canvas.getPhetRootNode().screenToWorld( worldPos );
        Point2D modelPos = mvt.viewToModel( worldPos );
        return modelPos;
    }
}

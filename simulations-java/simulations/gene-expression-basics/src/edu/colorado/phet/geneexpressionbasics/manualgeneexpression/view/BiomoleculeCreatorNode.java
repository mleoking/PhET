// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
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
    private final ManualGeneExpressionCanvas canvas;
    private VoidFunction1<Boolean> observer;
    private final PNode appearanceNode;

    /**
     * Constructor.
     *
     * @param appearanceNode       Node that represents the appearance of this
     *                             creator node, generally looks like the thing
     *                             being created.
     * @param canvas               Canvas upon which this node ultimately
     *                             resides.  This is needed for figuring out
     *                             where in model space this node exists.
     * @param mvt                  Model view transform.
     * @param moleculeCreator      Function object that knows how to create
     *                             the model element and add it to the model.
     * @param enclosingToolBoxNode Tool box in which this creator node is
     *                             contained.  This is needed in order to
     *                             determine when the created model element is
     *                             returned to the tool box.
     */
    public BiomoleculeCreatorNode( PPath appearanceNode,
                                   ManualGeneExpressionCanvas canvas,
                                   final ModelViewTransform mvt,
                                   final Function1<Point2D, MobileBiomolecule> moleculeCreator,
                                   final VoidFunction1<MobileBiomolecule> moleculeDestroyer,
                                   final PNode enclosingToolBoxNode ) {
        this.canvas = canvas;
        this.mvt = mvt;
        this.appearanceNode = appearanceNode;
        // Cursor handler.
        addInputEventListener( new CursorHandler() );
        // Add the handler that will add elements to the model when clicked.
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {

                // Set the node to look faded out so that something is still
                // visible.  This acts a a legend in the tool box.
                BiomoleculeCreatorNode.this.appearanceNode.setTransparency( 0.3f );
                setPickable( false );
                setChildrenPickable( false );

                // Create the corresponding biomolecule and add it to the model.
                biomolecule = moleculeCreator.apply( getModelPosition( event.getCanvasPosition() ) );
                biomolecule.userControlled.set( true );
                // Add an observer to watch for this model element to be returned.
                final MobileBiomolecule finalBiomolecule = biomolecule;
                observer = new VoidFunction1<Boolean>() {
                    public void apply( Boolean userControlled ) {
                        if ( !userControlled ) {
                            // The user has released this biomolecule.  If it
                            // was dropped above the return bounds (which are
                            // generally the bounds of the tool box where this
                            // creator node resides),then the model element
                            // should be removed from the model.
                            if ( enclosingToolBoxNode.getFullBoundsReference().contains( mvt.modelToView( finalBiomolecule.getPosition() ) ) ) {
                                moleculeDestroyer.apply( finalBiomolecule );
                                finalBiomolecule.userControlled.removeObserver( this );
                                BiomoleculeCreatorNode.this.appearanceNode.setTransparency( 1 );
                                setPickable( true );
                                setChildrenPickable( true );
                            }
                        }
                    }
                };
                biomolecule.userControlled.addObserver( observer );
            }

            @Override
            public void mouseDragged( PInputEvent event ) {
                biomolecule.setPosition( getModelPosition( event.getCanvasPosition() ) );
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                // The user has released this node.
                biomolecule.userControlled.set( false );
                biomolecule = null;
            }
        } );

        // Add the main node with which the user will interact.
        addChild( appearanceNode );
    }

    public void reset() {
        if ( biomolecule != null ) {
            biomolecule.userControlled.removeObserver( observer );
            biomolecule = null;
        }
        appearanceNode.setTransparency( 1 );
        setPickable( true );
        setChildrenPickable( true );
    }

    /**
     * Convert the canvas position to the corresponding location in the model.
     */
    private Point2D getModelPosition( Point2D canvasPos ) {
        Point2D worldPos = new Point2D.Double( canvasPos.getX(), canvasPos.getY() );
        canvas.getPhetRootNode().screenToWorld( worldPos );
        Point2D adjustedWorldPos = new Point2D.Double( worldPos.getX() - canvas.getViewportOffset().getX(), worldPos.getY() - canvas.getViewportOffset().getY() );
        Point2D modelPos = mvt.viewToModel( adjustedWorldPos );
        return modelPos;
    }
}

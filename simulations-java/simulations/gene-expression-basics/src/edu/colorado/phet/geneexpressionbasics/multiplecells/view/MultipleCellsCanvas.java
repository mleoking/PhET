// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.BiomoleculeToolBoxNode;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.Cell;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class MultipleCellsCanvas extends PhetPCanvas implements Resettable {

    // Stage size, based on default screen size.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    private final ModelViewTransform mvt;
    private PTransformActivity activity;
    private final Vector2D viewportOffset = new Vector2D( 0, 0 );
    private final List<BiomoleculeToolBoxNode> biomoleculeToolBoxNodeList = new ArrayList<BiomoleculeToolBoxNode>();

    public MultipleCellsCanvas( final MultipleCellsModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.5 ) ),
                1E8 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 250, 232, 189 ) );

        // Set up an observer of the list of cells in the model so that the
        // view representations can come and go as needed.
        model.cellList.addElementAddedObserver( new VoidFunction1<Cell>() {
            public void apply( final Cell addedCell ) {
                final PNode cellNode = new CellNode( addedCell, mvt );
                MultipleCellsCanvas.this.addWorldChild( cellNode );
                model.cellList.addElementRemovedObserver( new VoidFunction1<Cell>() {
                    public void apply( Cell removedCell ) {
                        if ( removedCell == addedCell ) {
                            MultipleCellsCanvas.this.removeWorldChild( cellNode );
                            model.cellList.removeElementRemovedObserver( this );
                        }
                    }
                } );
            }
        } );
    }

    public void reset() {
    }
}

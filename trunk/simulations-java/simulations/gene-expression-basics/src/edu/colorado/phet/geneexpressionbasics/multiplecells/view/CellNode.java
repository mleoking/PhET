// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.ManualGeneExpressionCanvas;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.Cell;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents a cell (as in a biological organism) in the view.
 *
 * @author John Blanco
 */
public class CellNode extends PNode {
    private static final Color FILL_COLOR = ManualGeneExpressionCanvas.CELL_INTERIOR_COLOR;
    private static final Stroke FILL_STROKE = new BasicStroke( 1 );
    private static final Color STROKE_COLOR = Color.BLACK;

    public CellNode( Cell cell, ModelViewTransform mvt ) {
        addChild( new PhetPPath( mvt.modelToView( cell.getShape() ), FILL_COLOR, FILL_STROKE, STROKE_COLOR ) );
    }
}

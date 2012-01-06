// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.Cell;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that represents a cell (as in a biological organism) in the view.
 *
 * @author John Blanco
 */
public class CellNode extends PNode {

    private static final Color NOMINAL_FILL_COLOR = new Color( 140, 162, 185 ); // Gray
    private static final Color FLORESCENT_FILL_COLOR = new Color( 173, 255, 47 );
    private static final Stroke FILL_STROKE = new BasicStroke( 1 );
    private static final Color STROKE_COLOR = Color.BLACK;

    public CellNode( Cell cell, ModelViewTransform mvt ) {
        final PhetPPath cellBody = new PhetPPath( mvt.modelToView( cell.getShape() ), NOMINAL_FILL_COLOR, FILL_STROKE, STROKE_COLOR );
        cell.proteinCount.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer proteinCount ) {
                double florescenceAmount = MathUtil.clamp( 0.0, ( (double) proteinCount - Cell.PROTEIN_LEVEL_WHERE_COLOR_CHANGE_STARTS ) /
                                                                ( Cell.PROTEIN_LEVEL_WHERE_COLOR_CHANGE_COMPLETES - Cell.PROTEIN_LEVEL_WHERE_COLOR_CHANGE_STARTS ), 1.0 );
                Color fillColor = ColorUtils.interpolateRBGA( NOMINAL_FILL_COLOR, FLORESCENT_FILL_COLOR, florescenceAmount );
                cellBody.setPaint( fillColor );
            }
        } );
        addChild( cellBody );
    }
}

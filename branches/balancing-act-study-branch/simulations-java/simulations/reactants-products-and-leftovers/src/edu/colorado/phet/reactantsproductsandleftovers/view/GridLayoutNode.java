// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.piccolophet.nodes.GridLinesNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ValueNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Places images in a grid, one image per cell in the grid.
 * The grid fits in a specified box.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GridLayoutNode extends ImageLayoutNode {
    
    private static final boolean DEBUG_SHOW_GRIDLINES = false;

    private static final int ROWS = 4;
    private static final int COLUMNS = 5;
    private static final double BOX_MARGIN = 5; // margin of space around the inside edge of the box
    private static final double CELL_MARGIN = 1; // margin of space around the inside edge of each cell
    
    private final PNode[][] cells; // 2-dimensional grid of cells
    private PDimension cellSize;
    
    public GridLayoutNode( PDimension boxSize ) {
        super( boxSize );
        cells = new PNode[ROWS][COLUMNS];
        
        // compute cell size
        double cellWidth = ( getBoxWidth() - ( 2 * BOX_MARGIN ) ) / COLUMNS;
        double cellHeight = ( getBoxHeight() - ( 2 * BOX_MARGIN ) ) / ROWS;
        cellSize = new PDimension( cellWidth, cellHeight );
        
        // visualize grid lines
        if ( DEBUG_SHOW_GRIDLINES ) {
            double gridWidth = getBoxWidth() - ( 2 * BOX_MARGIN );
            double gridHeight = getBoxHeight() - ( 2 * BOX_MARGIN );
            PNode gridLinesNode = new GridLinesNode( ROWS, COLUMNS, gridWidth, gridHeight, new BasicStroke( 1f ), Color.BLACK, null );
            addChild( gridLinesNode );
            gridLinesNode.setOffset( BOX_MARGIN, BOX_MARGIN );
        }
    }
    
    /**
     * @param node the node to be added
     * @param valueNode ignored
     */
    public void addNode( SubstanceImageNode node, ValueNode valueNode ) {
        addNode( node );
    }
    
    private void addNode( PNode node ) {
        // add the node to the scenegraph
        addChild( node );
        
        // find an empty cell in the grid
        int startRow = (int)( Math.random() * ROWS ); // start looking in a random row & column
        int startColumn = (int)( Math.random() * COLUMNS );
        int row = 0;
        int column = 0;
        boolean cellFound = false;
        
        // search from random cell to end of grid
        for ( int i = startRow; i < ROWS; i++ ) {
            for ( int j = ( i == startRow ? startColumn : 0 ); j < COLUMNS && !cellFound; j++ ) {
                if ( cells[i][j] == null ) {
                    row = i;
                    column = j;
                    cellFound = true;
                }
            }
        }
        
        // search from beginning of grid to random cell
        if ( !cellFound ) {
            for ( int i = 0; i <= startRow; i++ ) {
                for ( int j = 0; j < ( i == startRow ? startColumn : COLUMNS ) && !cellFound; j++ ) {
                    if ( cells[i][j] == null ) {
                        row = i;
                        column = j;
                        cellFound = true;
                    }
                }
            }
        }
        
        if ( !cellFound ) {
            // all cells occupied, reuse a random cell
            row = (int)( Math.random() * ROWS );
            column = (int)( Math.random() * COLUMNS );
            if ( ENABLE_DEBUG_OUTPUT ) {
               System.out.println( "GridLayoutNode.addNode: all cells are occupied, reusing cell [" + row + "," + column + "]" );
            }
        }
        cells[row][column] = node;
        
        // pick a random offset within the chosen cell, so that the layout doesn't look so uniform
        double cellXOffset = 0;
        double cellYOffset = 0;
        if ( node.getFullBoundsReference().getWidth() < cellSize.getWidth() - ( 2 * CELL_MARGIN ) ) {
            cellXOffset = CELL_MARGIN + ( Math.random() * ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() - ( 2 * CELL_MARGIN ) ) );
        }
        if ( node.getFullBoundsReference().getHeight() < cellSize.getHeight() ) {
            cellYOffset = CELL_MARGIN + ( Math.random() * ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() - ( 2 * CELL_MARGIN ) ) );
        }
        
        // set the node's offset
        double x = BOX_MARGIN + ( column * cellSize.getWidth() ) + cellXOffset;
        double y = BOX_MARGIN + ( row * cellSize.getHeight() ) + cellYOffset;
        node.setOffset( x, y );
    }
    
    public void removeNode( SubstanceImageNode node ) {
        removeChild( node );
        // remove node from the grid
        boolean removed = false;
        for ( int i = 0; i < ROWS && !removed; i++ ) {
            for ( int j = 0; j < COLUMNS && !removed; j++ ) {
                if ( node == cells[i][j] ) {
                    cells[i][j] = null;
                    removed = true;
                }
            }
        }
    }
}
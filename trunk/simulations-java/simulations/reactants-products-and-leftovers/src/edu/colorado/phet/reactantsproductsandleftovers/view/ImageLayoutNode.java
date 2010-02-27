/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.GridLinesNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for nodes that arrange images in the Before and After boxes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ImageLayoutNode extends PhetPNode {
    
    private static final boolean ENABLE_DEBUG_OUTPUT = false;
    
    private final PDimension boxSize;
    
    public ImageLayoutNode( PDimension boxSize ) {
        this.boxSize = new PDimension( boxSize );
    }
    
    protected double getBoxWidth() {
        return boxSize.getWidth();
    }
    
    protected double getBoxHeight() {
        return boxSize.getHeight();
    }
    
    /**
     * Adds node, relative to referenceNode, with knowledge of a related controlNode.
     * The node is also added to the scenegraph.
     * @param node
     * @param parent
     * @param referenceNode
     * @param controlNode
     * @return
     */
    public abstract void addNode( PNode node, PNode referenceNode, PNode controlNode );
    
    /**
     * Removes node from the layout, and from the scenegraph.
     * @param node
     * @param parent
     */
    public abstract void removeNode( PNode node );
    
    /**
     * Stacks images vertically in the box.
     */
    public static class StackedLayoutNode extends ImageLayoutNode {

        private static final double Y_MARGIN = 7;
        private static final double Y_SPACING = 28;
        
        private final PropertyChangeListener propertyChangeListener;
        private final HashMap<PNode,PBounds> fullBoundsMap; // keep track of node bounds, for adjusting the offset of dynamic images
        
        public StackedLayoutNode( PDimension boxSize ) {
            super( boxSize );
            propertyChangeListener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent event ) {
                    if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                        updateNodeOffset( (PNode) event.getSource() );
                    }
                }
            };
            fullBoundsMap = new HashMap<PNode,PBounds>();
        }
        
        /**
         * @param node the node to be added
         * @param referenceNode the node currently at the top of the stack, used for y offset.
         * @param controlNode a control below the box, used for x offset.
         */
        public void addNode( PNode node, PNode referenceNode, PNode controlNode ) {
            // add the node to the scenegraph
            addChild( node );
            // set the node's offset
            double x = controlNode.getXOffset() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = getBoxHeight() - node.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( node ) - Y_MARGIN;
            if ( referenceNode != null ) {
                y = referenceNode.getFullBoundsReference().getMinY() - PNodeLayoutUtils.getOriginYOffset( node ) - Y_SPACING;
            }
            node.setOffset( x, y );
            // listen for changes to the node's full bounds
            fullBoundsMap.put( node, node.getFullBounds() );
            node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, propertyChangeListener );
        }
        
        public void removeNode( PNode node ) {
            removeChild( node );
            fullBoundsMap.remove( node );
            node.removePropertyChangeListener( propertyChangeListener );
        }
        
        /*
         * Some images (like the sandwich) are dynamic.
         * As their full bounds change, their vertical offset must be adjusted,
         * so that the bottoms of all images in the stacks remain vertically aligned.
         * If we don't do this then (for example) increases in the complexity of a sandwich
         * will cause the images in the sandwich stack to fall outside the bottom of the box. 
         */
        private void updateNodeOffset( PNode node ) {
            // adjust the vertical offset
            PBounds oldBounds = fullBoundsMap.get( node );
            PBounds newBounds = node.getFullBounds();
            double yAdjust = oldBounds.getHeight() - newBounds.getHeight();
            node.setOffset( node.getXOffset(), node.getYOffset() + yAdjust );
            // make a record of the new bounds
            fullBoundsMap.remove( node );
            fullBoundsMap.put( node, newBounds );
        }
    }
    
    /**
     * Places images in a grid, one image per cell in the grid.
     */
    public static class GridLayoutNode extends ImageLayoutNode {
        
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
         * @param referenceNode ignored
         * @param controlNode ignored
         */
        public void addNode( PNode node, PNode referenceNode, PNode controlNode ) {
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
        
        public void removeNode( PNode node ) {
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
}
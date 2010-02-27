/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.GridLinesNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.ValueNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
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
     * Adds a substance image node to the scenegraph.
     * The corresponding ValueNode is provided in case nodes need to be aligned with it.
     * @param node
     * @param valueNode
     * @return
     */
    public abstract void addNode( SubstanceImageNode node, ValueNode valueNode );
    
    /**
     * Removes a substance image node from the scenegraph.
     * @param node
     * @param parent
     */
    public abstract void removeNode( SubstanceImageNode node );
    
    /**
     * Stacks images vertically in the box.
     */
    public static class StackedLayoutNode extends ImageLayoutNode {

        private static final double Y_MARGIN = 7;
        private static final double Y_SPACING = 28;
        
        private final PropertyChangeListener imageChangeListener;
        private final HashMap<String,ArrayList<SubstanceImageNode>> stacks;
        private final HashMap<String,PNode> valueNodes;
        
        public StackedLayoutNode( PDimension boxSize ) {
            super( boxSize );
            imageChangeListener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent event ) {
                    if ( event.getPropertyName().equals( PImage.PROPERTY_IMAGE ) ) {
                        updateLayout();
                    }
                }
            };
            stacks = new HashMap<String,ArrayList<SubstanceImageNode>>();
            valueNodes = new HashMap<String,PNode>();
        }
        
        /**
         * @param node the node to be added
         * @param referenceNode the node currently at the top of the stack, used for y offset.
         * @param valueNode ValueNode below the box, used for x offset of each stack
         */
        public void addNode( SubstanceImageNode node, ValueNode valueNode ) {
            
            // get the substance name, it's the key for our data structures
            String substanceName = node.getSubstance().getName();
            
            // add the node to the scenegraph
            addChild( node );
            
            // add the node to the proper stack
            ArrayList<SubstanceImageNode> stack = stacks.get( substanceName );
            if ( stack == null ) {
                stack = new ArrayList<SubstanceImageNode>();
                stacks.put( substanceName, stack );
            }
            stack.add( node );
            
            // remember the control node for this substance type
            if ( valueNodes.get( substanceName ) == null ) {
                valueNodes.put( substanceName, valueNode );
            }
            
            // listen for changes to the node's image
            node.addPropertyChangeListener( imageChangeListener );
            
            updateLayout();
        }
        
        public void removeNode( SubstanceImageNode node ) {
            removeChild( node );
            ArrayList<SubstanceImageNode> stack = stacks.get( node.getSubstance().getName() );
            stack.remove( node );
            node.removePropertyChangeListener( imageChangeListener );
        }
        
        /*
         * Adjust the layout all images, in all stacks.
         */
        private void updateLayout() {
            for ( String substanceName : stacks.keySet() ){
                ArrayList<SubstanceImageNode> stack = stacks.get( substanceName );
                PNode controlNode = valueNodes.get( substanceName );
                for ( int i = 0; i < stack.size(); i++ ) {
                    SubstanceImageNode node = stack.get( i );
                    double x = controlNode.getXOffset() - ( node.getFullBoundsReference().getWidth() / 2 );
                    double y = getBoxHeight() - node.getFullBoundsReference().getHeight() - Y_MARGIN - ( i * Y_SPACING );
                    node.setOffset( x, y );
                }
            }
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
}
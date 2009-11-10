package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Interface for specifying how images are arranged in the Before and After boxes.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ImageLayoutStrategy {
    
    public void setBoxNode( BoxNode boxNode );
    
    /**
     * Adds node, relative to referenceNode, with knowledge of a related controlNode.
     * @param node
     * @param referenceNode
     * @param boxNode
     * @param controlNode
     * @return
     */
    public void addNode( PNode node, PNode referenceNode, PNode controlNode );
    
    public void removeNode( PNode node );
    
    abstract class AbstractImageLayoutStrategy implements ImageLayoutStrategy {
        
        private BoxNode boxNode;
        
        public void setBoxNode( BoxNode boxNode ) {
            this.boxNode = boxNode;
        }
        
        protected BoxNode getBoxNode() {
            return boxNode;
        }
        
        public void addNode( PNode node ) {
            boxNode.addChild( node );
        }
        
        public void removeNode( PNode node ) {
            boxNode.removeChild( node );
        }
    }
    
    /**
     * Stacks images vertically in the box.
     */
    public static class StackedLayoutStrategy extends AbstractImageLayoutStrategy {

        private static final double Y_MARGIN = 8;
        private static final double Y_SPACING = 27;
        
        public void addNode( PNode node, PNode referenceNode, PNode controlNode ) {
            // add the node to the scenegraph
            super.addNode( node );
            // set the node's offset
            double x = controlNode.getXOffset() - ( node.getFullBoundsReference().getWidth() / 2 );
            double y = getBoxNode().getFullBoundsReference().getHeight() - node.getFullBoundsReference().getHeight() - PNodeLayoutUtils.getOriginYOffset( node ) - Y_MARGIN;
            if ( referenceNode != null ) {
                y = referenceNode.getFullBoundsReference().getMinY() - PNodeLayoutUtils.getOriginYOffset( node ) - Y_SPACING;
            }
            node.setOffset( x, y );
        }
    }
    
    /**
     * Randomly places images in a box, images may overlap.
     */
    public static class RandomBoxLayoutStrategy extends AbstractImageLayoutStrategy {
        
        private static final double MARGIN = 5;
        
        public void addNode( PNode node, PNode referenceNode, PNode controlNode ) {
            // add the node to the scenegraph
            super.addNode( node );
            // set the node's offset
            PBounds b = getBoxNode().getFullBoundsReference();
            double x = b.getX() + MARGIN + ( Math.random() * ( b.getWidth() - node.getFullBoundsReference().getWidth() - ( 2 * MARGIN ) ) );
            double y = b.getY() + MARGIN + ( Math.random() * ( b.getHeight() - node.getFullBoundsReference().getHeight() - ( 2 * MARGIN ) ) );
            node.setOffset( x, y );
        }
    }
    
    /**
     * Places images in a grid, one image per cell in the grid.
     */
    public static class GridLayoutStrategy extends AbstractImageLayoutStrategy {
        
        private static final double MARGIN = 5;
        private static final int ROWS = 4;
        private static final int COLUMNS = 5;
        
        private final PNode[][] cells; // 2-dimensional grid of cells
        private PDimension cellSize;
        
        public GridLayoutStrategy() {
            cells = new PNode[ROWS][COLUMNS];
        }
        
        public void setBoxNode( BoxNode boxNode ) {
            super.setBoxNode( boxNode );
            PBounds b = boxNode.getFullBoundsReference();
            double cellWidth = ( b.getWidth() - ( 2 * MARGIN ) ) / COLUMNS;
            double cellHeight = ( b.getHeight() - ( 2 * MARGIN ) ) / ROWS;
            cellSize = new PDimension( cellWidth, cellHeight );
        }
        
        public void addNode( PNode node, PNode referenceNode, PNode controlNode ) {
            // add the node to the scenegraph
            super.addNode( node );
            
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
            if ( !cellFound ) {
                // search from beginning of grid to random cell
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
                System.err.println( "GridLayoutStrategy.addNode: all cells are occupies, images will overlap" );
                row = (int)( Math.random() * ROWS );
                column = (int)( Math.random() * COLUMNS );
                cellFound = true;
            }
            else if ( cells[row][column] != null ) {
                System.err.println( "GridLayoutStrategy.addNode: bug in algorithm, cell[" + row + "," + column + "] is occupied" );
            }
            cells[row][column] = node;
            
            // set the node's offset, centered in a cell
            PBounds b = getBoxNode().getFullBoundsReference();
            double x = b.getMinX() + MARGIN + ( column * cellSize.getWidth() ) + ( ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2 );
            double y = b.getMinY() + MARGIN + ( row * cellSize.getHeight() ) + ( ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2 );
            node.setOffset( x, y );
        }
        
        public void removeNode( PNode node ) {
            super.removeNode( node );
            boolean removed = false;
            for ( int i = 0; i < ROWS && removed == false; i++ ) {
                for ( int j = 0; j < COLUMNS; j++ ) {
                    if ( node == cells[i][j] ) {
                        cells[i][j] = null;
                        removed = true;
                    }
                }
            }
        }
    }
}
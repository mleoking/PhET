package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

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
            //XXX investiage why this is thrown
//            if ( boxNode != null ) {
//                throw new IllegalStateException( "setBoxNode should only be called once" );
//            }
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
        
        private static final boolean SHOW_GRIDLINES = true;
        private static final double BOX_MARGIN = 5; // margin of space around the inside edge of the box
        private static final double CELL_MARGIN = 1; // margin of space around the inside edge of each cell
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
            double cellWidth = ( b.getWidth() - ( 2 * BOX_MARGIN ) ) / COLUMNS;
            double cellHeight = ( b.getHeight() - ( 2 * BOX_MARGIN ) ) / ROWS;
            cellSize = new PDimension( cellWidth, cellHeight );
            if ( SHOW_GRIDLINES ) {
                PBounds gridBounds = new PBounds( BOX_MARGIN, BOX_MARGIN, b.getWidth() - ( 2 * BOX_MARGIN ), b.getHeight() - ( 2 * BOX_MARGIN ) );
                getBoxNode().addChild( new GridLinesNode( gridBounds, ROWS, COLUMNS, new BasicStroke( 1f ), Color.BLACK ) );
            }
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
            
            // pick a random offset within the chosen cell, so the layout doesn't look so regular
            double cellXOffset = 0;
            double cellYOffset = 0;
            if ( node.getFullBoundsReference().getWidth() < cellSize.getWidth() - ( 2 * CELL_MARGIN ) ) {
                cellXOffset = Math.random() * ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() );
                cellXOffset = ( Math.random() < 0.5 ) ? CELL_MARGIN : ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() - CELL_MARGIN );
            }
            if ( node.getFullBoundsReference().getHeight() < cellSize.getHeight()  ) {
                cellYOffset = Math.random() * ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() );
                cellYOffset = ( Math.random() < 0.5 ) ? CELL_MARGIN : ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() - CELL_MARGIN );
            }
            
            // set the node's offset
            PBounds b = getBoxNode().getFullBoundsReference();
            double x = b.getMinX() + BOX_MARGIN + ( column * cellSize.getWidth() ) + cellXOffset;
            double y = b.getMinY() + BOX_MARGIN + ( row * cellSize.getHeight() ) + cellYOffset;
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

        /*
         * Debugging class, for visualizing the grid of cells.
         */
        private static class GridLinesNode extends PComposite {
            
            public GridLinesNode( PBounds bounds, int rows, int columns, Stroke stroke, Paint strokePaint ) {
                super();
                
                final PDimension cellSize = new PDimension( bounds.getWidth() / columns, bounds.getHeight() / rows );
                
                // outside edge
                PPath edgeNode = new PPath( bounds );
                edgeNode.setStroke( stroke );
                edgeNode.setStrokePaint( strokePaint );
                addChild( edgeNode );

                // vertical lines
                for ( int column = 0; column < columns; column++ ) {
                    GeneralPath path = new GeneralPath();
                    path.moveTo( (float) ( bounds.getMinX() + ( column * cellSize.getWidth() ) ), (float) bounds.getMinY() );
                    path.lineTo( (float) ( bounds.getMinX() + ( column * cellSize.getWidth() ) ), (float) bounds.getMaxY() );
                    PPath lineNode = new PPath( path );
                    lineNode.setStroke( stroke );
                    lineNode.setStrokePaint( strokePaint );
                    addChild( lineNode );
                }

                // horizontal lines
                for ( int row = 0; row < ROWS; row++ ) {
                    GeneralPath path = new GeneralPath();
                    path.moveTo( (float) bounds.getMinX(), (float) ( bounds.getMinY() + ( row * cellSize.getHeight() ) ) );
                    path.lineTo( (float) bounds.getMaxX(), (float) ( bounds.getMinY() + ( row * cellSize.getHeight() ) ) );
                    PPath lineNode = new PPath( path );
                    lineNode.setStroke( stroke );
                    lineNode.setStrokePaint( strokePaint );
                    addChild( lineNode );
                }
            }
        }
    }
}
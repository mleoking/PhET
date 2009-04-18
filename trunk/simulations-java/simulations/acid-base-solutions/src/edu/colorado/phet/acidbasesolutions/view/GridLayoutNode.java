/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * GridLayoutNode provides some of the layout capabilities of Swing's GridBagLayout for Piccolo nodes.
 * Nodes are added to cells in a grid, with optional constraints.
 * The grid expands to fit the number of cells, and adjusts its layout when its children change size.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GridLayoutNode extends PComposite {

    /**
     * Enumeration that specifies where a node should be anchored in a grid's cell.
     * Use the Java 1.4 typesafe enumeration pattern.
     */
    public static class Anchor {
        
        public static final Anchor CENTER = new Anchor( "center" );
        public static final Anchor NORTH = new Anchor( "north" );
        public static final Anchor NORTHEAST = new Anchor( "northeast" );
        public static final Anchor EAST = new Anchor( "east" );
        public static final Anchor SOUTHEAST = new Anchor( "southeast" );
        public static final Anchor SOUTH = new Anchor( "south" );
        public static final Anchor SOUTHWEST = new Anchor( "southwest" );
        public static final Anchor WEST = new Anchor( "west" );
        public static final Anchor NORTHWEST = new Anchor( "northwest" );

        private final String name;

        /* enumeration, private constructor */
        private Anchor( String name ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private Grid grid;
    private final Insets insets;
    private final PropertyChangeListener propertyChangeListener;

    /**
     * Layout with no insets.
     */
    public GridLayoutNode() {
        this( new Insets( 0, 0, 0, 0 ) );
    }

    /**
     * Layout with specified insets.
     * @param insets
     */
    public GridLayoutNode( Insets insets ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        this.insets = insets;
        grid = new Grid();
        this.propertyChangeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( PNode.PROPERTY_FULL_BOUNDS ) ) {
                    updateLayout();
                }
            }
        };
    }

    /**
     * Adds a node to the grid.
     * If the node is already in the grid, it is moved.
     * If another node already occupies the cell, that other node is removed from the grid.
     * @param node
     * @param row
     * @param column
     * @param anchor
     */
    public void add( PNode node, int row, int column, Anchor anchor ) {
        assert ( node != null );
        assert ( row >= 0 );
        assert ( column >= 0 );
        // remove the node if it's already in the grid
        remove( node );
        // clear the cell
        clear( row, column );
        // put the node in the cell
        Constraints constraints = new Constraints( anchor );
        Cell newCell = new Cell( node, constraints );
        grid.put( newCell, row, column );
        addChild( node );
        node.addPropertyChangeListener( propertyChangeListener );
        // update the layout
        updateLayout();
    }
    
    /**
     * Adds a node to the grid, anchored at the center of its cell.
     * @param node
     * @param row
     * @param column
     */
    public void add( PNode node, int row, int column ) {
        add( node, row, column, Anchor.CENTER );
    }

    /**
     * Removes a node from the grid.
     * If the node is not in the grid, this does nothing.
     * @param node
     */
    public void remove( PNode node ) {
        assert ( node != null );
        boolean found = false;
        for ( int row = 0; row < grid.getNumberOfRows() && !found; row++ ) {
            for ( int column = 0; column < grid.getNumberOfColumns() && !found; column++ ) {
                Cell cell = grid.get( row, column );
                if ( cell != null && cell.getNode() == node ) {
                    grid.clear( row, column );
                    removeChild( node );
                    node.removePropertyChangeListener( propertyChangeListener );
                    updateLayout();
                    found = true;
                }
            }
        }
    }

    /**
     * Clears the specified cell in the grid.
     * If nothing is in the cell, then this does nothing.
     * @param row
     * @param column
     */
    public void clear( int row, int column ) {
        Cell cell = grid.get( row, column );
        if ( cell != null ) {
            cell.getNode().removePropertyChangeListener( propertyChangeListener );
            grid.clear( row, column );
            updateLayout();
        }
    }

    /*
     * Updates the layout by setting offsets for all nodes in the grid.
     */
    private void updateLayout() {
        int numberOfRows = grid.getNumberOfRows();
        int numberOfColumns = grid.getNumberOfColumns();
        double[] rowHeights = grid.getRowHeights();
        double[] columnWidths = grid.getColumnWidths();
        int yOffset = 0;
        for ( int row = 0; row < numberOfRows; row++ ) {
            int xOffset = insets.left;
            for ( int column = 0; column < numberOfColumns; column++ ) {
                Cell cell = grid.get( row, column );
                if ( cell != null ) {
                    PNode node = cell.getNode();
                    Constraints constraints = cell.getConstraints();
                    PDimension cellSize = new PDimension( columnWidths[column], rowHeights[row] );
                    Point2D anchorOffset = constraints.getAnchorOffset( cellSize, node );
                    node.setOffset( xOffset + anchorOffset.getX(), yOffset + anchorOffset.getY() );
                }
                xOffset += insets.left + columnWidths[column] + insets.right;
            }
            yOffset += insets.top + rowHeights[row] + insets.bottom;
        }
    }

    /*
     * The grid is a 2-dimensional array of Cells.
     * If automatically grows to fit the nodes that are added to it.
     */
    private static class Grid {

        private Cell[][] cells;

        /**
         * Grid with a default size.
         */
        public Grid() {
            this( 5, 5 );
        }

        /**
         * Grid with a specified size.
         * @param rows
         * @param columns
         */
        public Grid( int rows, int columns ) {
            assert ( rows > 0 && columns > 0 );
            cells = new Cell[rows][columns];
        }

        public int getNumberOfCells() {
            return getNumberOfRows() * getNumberOfColumns();
        }

        public int getNumberOfRows() {
            return cells.length;
        }

        public int getNumberOfColumns() {
            return cells[0].length;
        }

        /**
         * Adds the specified cell.
         * @param cell
         * @param row
         * @param column
         */
        public void put( Cell cell, int row, int column ) {
            assert ( row >= 0 && column >= 0 );
            if ( !hasCell( row, column ) ) {
                cells = expand( cells, row, column );
            }
            cells[row][column] = cell;
        }

        /**
         * Gets the specified cell, may be null.
         * @param row
         * @param column
         * @return
         */
        public Cell get( int row, int column ) {
            assert ( row >= 0 && column >= 0 );
            Cell cell = null;
            if ( hasCell( row, column ) ) {
                cell = cells[row][column];
            }
            return cell;
        }

        /**
         * Clears a cell in the grid, so that it contains nothing.
         */
        public void clear( int row, int column ) {
            put( null, row, column );
        }

        /*
         * Does the specified cell exist in the grid?
         */
        private boolean hasCell( int row, int column ) {
            return ( row >= 0 && row < getNumberOfRows() && column >= 0 && column < getNumberOfColumns() );
        }

        /*
         * Expands the grid so that it is at least rows x columns.
         */
        private static Cell[][] expand( Cell[][] cells, int rows, int columns ) {
            assert ( cells != null );
            assert ( rows > 0 && columns > 0 );
            int newRows = Math.max( rows, cells.length );
            int newColumns = Math.max( columns, cells[0].length );
            Cell[][] newCells = new Cell[newRows][newColumns];
            for ( int row = 0; row < cells.length; rows++ ) {
                for ( int column = 0; column < cells[row].length; column++ ) {
                    newCells[row][column] = cells[row][column];
                }
            }
            return newCells;
        }

        /*
         * Gets an array that contains the height of each row in the grid.
         */
        public double[] getRowHeights() {
            int numberOfRows = getNumberOfRows();
            int numberOfColumns = getNumberOfColumns();
            double[] heights = new double[numberOfRows];
            for ( int row = 0; row < numberOfRows; row++ ) {
                double max = 0;
                for ( int column = 0; column < numberOfColumns; column++ ) {
                    Cell cell = get( row, column );
                    if ( cell != null ) {
                        max = Math.max( max, cell.getNode().getFullBoundsReference().getHeight() );
                    }
                }
                heights[row] = max;
            }
            return heights;
        }

        /*
         * Gets an array that contains the width of each column in the grid.
         */
        public double[] getColumnWidths() {
            int numberOfRows = getNumberOfRows();
            int numberOfColumns = getNumberOfColumns();
            double[] widths = new double[numberOfColumns];
            for ( int column = 0; column < numberOfColumns; column++ ) {
                double max = 0;
                for ( int row = 0; row < numberOfRows; row++ ) {
                    Cell cell = get( row, column );
                    if ( cell != null ) {
                        max = Math.max( max, cell.getNode().getFullBoundsReference().getWidth() );
                    }
                }
                widths[column] = max;
            }
            return widths;
        }
    }

    /*
     * The grid is composed of Cells.  
     * Each cell contains a node and some constraints.
     */
    private static class Cell {

        private final PNode node;
        private final Constraints constraints;

        public Cell( PNode node, Constraints constraints ) {
            this.node = node;
            this.constraints = constraints;
        }
        
        public PNode getNode() {
            return node;
        }
        
        public Constraints getConstraints() {
            return constraints;
        }
    }
    
    /**
     * Constraints that apply to a Cell in the Grid.
     * This is a separate class so that we can add additional constraints in the future.
     */
    private static class Constraints {

        public final Anchor anchor; // node's position relative to the cell's bounding rectangle

        public Constraints( Anchor anchor ) {
            this.anchor = anchor;
        }
        
        public Anchor getAnchor() {
            return anchor;
        }

        public Point2D getAnchorOffset( PDimension cellSize, PNode node ) {
            double x = 0, y = 0;
            if ( anchor == Anchor.CENTER ) {
                x = ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
                y = ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2;
            }
            else if ( anchor == Anchor.NORTH ) {
                x = ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
                y = 0;
            }
            else if ( anchor == Anchor.NORTHEAST ) {
                x = cellSize.getWidth() - node.getFullBoundsReference().getWidth();
                y = 0;
            }
            else if ( anchor == Anchor.EAST ) {
                x = cellSize.getWidth() - node.getFullBoundsReference().getWidth();
                y = ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2;
            }
            else if ( anchor == Anchor.SOUTHEAST ) {
                x = cellSize.getWidth() - node.getFullBoundsReference().getWidth();
                y = cellSize.getHeight() - node.getFullBoundsReference().getHeight();
            }
            else if ( anchor == Anchor.SOUTH ) {
                x = ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
                y = cellSize.getHeight() - node.getFullBoundsReference().getHeight();
            }
            else if ( anchor == Anchor.SOUTHWEST ) {
                x = 0;
                y = cellSize.getHeight() - node.getFullBoundsReference().getHeight();
            }
            else if ( anchor == Anchor.WEST ) {
                x = 0;
                y = ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2;
            }
            else if ( anchor == Anchor.NORTHWEST ) {
                x = 0;
                y = 0;
            }
            return new Point2D.Double( x, y );
        }
    }

    // test
    public static void main( String[] args ) {

        // canvas
        PCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        
        // grid
        GridLayoutNode grid = new GridLayoutNode( new Insets( 2, 10, 2, 10 ) );
        grid.scale( 1.75 );
        canvas.getLayer().addChild( grid );
        grid.setOffset( 100, 100 );
        grid.add( new PText( "centered" ), 0, 0 );
        grid.add( new PText( "northeast" ), 0, 1, Anchor.NORTHEAST );
        grid.add( new PText( "centered" ), 1, 1 );
        grid.add( new PText( "southwest!" ), 2, 2, Anchor.SOUTHWEST );
        grid.add( new PPath( new Rectangle2D.Double( 0, 0, 30, 30 ) ), 3, 1 );
        grid.add( new PText( "big-long-centered" ), 4, 1 );
        grid.add( new PPath( new Rectangle2D.Double( 0, 0, 30, 60 ) ), 4, 2 );
        
        // frame
        JFrame frame = new JFrame();
        frame.getContentPane().add( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

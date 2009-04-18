
package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;


public class GridLayoutNode extends PComposite {

    //TODO enumeration
    public static final int ANCHOR_CENTER = 0;
    public static final int ANCHOR_NORTH = 1;
    public static final int ANCHOR_NORTHEAST = 2;
    public static final int ANCHOR_EAST = 3;
    public static final int ANCHOR_SOUTHEAST = 4;
    public static final int ANCHOR_SOUTH = 5;
    public static final int ANCHOR_SOUTHWEST = 6;
    public static final int ANCHOR_WEST = 7;
    public static final int ANCHOR_NORTHWEST = 8;
    private static final int ANCHOR_MIN = 0;
    private static final int ANCHOR_MAX = 8;

    private final Insets insets;
    private final PropertyChangeListener propertyChangeListener;
    private Grid grid;

    public GridLayoutNode() {
        this( new Insets( 0, 0, 0, 0 ) );
    }

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

    public void add( PNode node, int row, int column ) {
        add( node, row, column, ANCHOR_CENTER );
    }

    public void add( PNode node, int row, int column, int anchor ) {
        assert ( node != null );
        assert ( row >= 0 );
        assert ( column >= 0 );
        assert ( anchor >= ANCHOR_MIN && anchor <= ANCHOR_MAX );
        Cell currentCell = grid.get( row, column );
        if ( currentCell != null ) {
            currentCell.node.removePropertyChangeListener( propertyChangeListener );
        }
        grid.put( new Cell( node, anchor ), row, column );
        addChild( node );
        node.addPropertyChangeListener( propertyChangeListener );
        updateLayout();
    }

    public void remove( PNode node ) {
        assert ( node != null );
        boolean found = false;
        for ( int row = 0; row < grid.getNumberOfRows() && !found; row++ ) {
            for ( int column = 0; column < grid.getNumberOfColumns() && !found; column++ ) {
                Cell cell = grid.get( row, column );
                if ( cell != null && cell.node == node ) {
                    grid.remove( row, column );
                    removeChild( node );
                    node.removePropertyChangeListener( propertyChangeListener );
                    updateLayout();
                    found = true;
                }
            }
        }
    }

    public void remove( int row, int column ) {
        Cell cell = grid.get( row, column );
        if ( cell != null ) {
            cell.node.removePropertyChangeListener( propertyChangeListener );
            grid.remove( row, column );
            updateLayout();
        }
    }

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
                    PNode node = cell.node;
                    Point2D anchorOffset = cell.getOffset( new PDimension( columnWidths[column], rowHeights[row] ) );
                    node.setOffset( xOffset + anchorOffset.getX(), yOffset + anchorOffset.getY() );
                }
                xOffset += insets.left + columnWidths[column] + insets.right;
            }
            yOffset += insets.top + rowHeights[row] + insets.bottom;
        }
    }

    private static class Grid {

        private Cell[][] cells;

        public Grid() {
            this( 5, 5 );
        }

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

        public void put( Cell cell, int row, int column ) {
            assert ( row >= 0 && column >= 0 );
            if ( !hasCell( row, column ) ) {
                cells = expand( cells, row, column );
            }
            cells[row][column] = cell;
        }

        public Cell get( int row, int column ) {
            assert ( row >= 0 && column >= 0 );
            Cell cell = null;
            if ( hasCell( row, column ) ) {
                cell = cells[row][column];
            }
            return cell;
        }

        public void remove( int row, int column ) {
            put( null, row, column );
        }

        private boolean hasCell( int row, int column ) {
            return ( row >= 0 && row < getNumberOfRows() && column >= 0 && column < getNumberOfColumns() );
        }

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

        public double[] getRowHeights() {
            int numberOfRows = getNumberOfRows();
            int numberOfColumns = getNumberOfColumns();
            double[] heights = new double[numberOfRows];
            for ( int row = 0; row < numberOfRows; row++ ) {
                double max = 0;
                for ( int column = 0; column < numberOfColumns; column++ ) {
                    Cell cell = get( row, column );
                    if ( cell != null ) {
                        max = Math.max( max, cell.node.getFullBoundsReference().getHeight() );
                    }
                }
                heights[row] = max;
            }
            return heights;
        }

        public double[] getColumnWidths() {
            int numberOfRows = getNumberOfRows();
            int numberOfColumns = getNumberOfColumns();
            double[] widths = new double[numberOfColumns];
            for ( int column = 0; column < numberOfColumns; column++ ) {
                double max = 0;
                for ( int row = 0; row < numberOfRows; row++ ) {
                    Cell cell = get( row, column );
                    if ( cell != null ) {
                        max = Math.max( max, cell.node.getFullBoundsReference().getWidth() );
                    }
                }
                widths[column] = max;
            }
            return widths;
        }
    }

    private static class Cell {

        public final PNode node;
        public final int anchor;

        public Cell( PNode node, int anchor ) {
            this.node = node;
            this.anchor = anchor;
        }
        
        public Point2D getOffset( PDimension cellSize ) {
            double x, y;
            switch( anchor ) {
            case ANCHOR_CENTER:
                x = ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
                y = ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2;
                break;
            case ANCHOR_NORTH:
                x = ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
                y = 0;
                break;
            case ANCHOR_NORTHEAST:
                x = cellSize.getWidth() - node.getFullBoundsReference().getWidth();
                y = 0;
                break;
            case ANCHOR_EAST:
                x = cellSize.getWidth() - node.getFullBoundsReference().getWidth();
                y = ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2;
                break;
            case ANCHOR_SOUTHEAST:
                x = cellSize.getWidth() - node.getFullBoundsReference().getWidth();
                y = cellSize.getHeight() - node.getFullBoundsReference().getHeight();
                break;
            case ANCHOR_SOUTH:
                x = ( cellSize.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
                y = cellSize.getHeight() - node.getFullBoundsReference().getHeight();
                break;
            case ANCHOR_SOUTHWEST:
                x = 0;
                y = cellSize.getHeight() - node.getFullBoundsReference().getHeight();
                break;
            case ANCHOR_WEST:
                x = 0;
                y = ( cellSize.getHeight() - node.getFullBoundsReference().getHeight() ) / 2;
                break;
            case ANCHOR_NORTHWEST:
                x = 0;
                y = 0;
                break;
            default:
                throw new IllegalArgumentException( "unsupported anchor: " + anchor );
            }
            return new Point2D.Double( x, y );
        }
    }
    
    public static void main( String[] args ) {
        
        // canvas
        PCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        
        // grid
        GridLayoutNode grid = new GridLayoutNode( new Insets( 10, 10, 10, 10 ) );
        grid.scale( 1.75 );
        canvas.getLayer().addChild( grid );
        grid.setOffset( 100, 100 );
        grid.add( new PText( "one" ), 0, 0 );
        grid.add( new PText( "hello" ), 0, 1, ANCHOR_NORTHEAST );
        grid.add( new PText( "two2222222222" ), 1, 1 );
        grid.add( new PText( "three!" ), 2, 2, ANCHOR_SOUTHWEST );
        
        // frame
        JFrame frame = new JFrame();
        frame.getContentPane().add( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}

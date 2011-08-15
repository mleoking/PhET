// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * This class defines a node that represents a periodic table of the elements. It is not interactive by default,
 * but provides infrastructure that can be used to add interactivity in subclasses.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class PeriodicTableNode extends PNode {

    //In screen coordinates, the default length = width of a cell square
    public static final double CELL_DIMENSION = 20;

    /**
     * Creates a new periodic table node with the specified background color and cell factory
     *
     * @param backgroundColor color to use for the background for each cell in the periodic table, may be different if highlighted
     * @param cellFactory     for creating cells to add as Piccolo nodes to show in the periodic table
     */
    public PeriodicTableNode( Color backgroundColor, CellFactory cellFactory ) {

        //See http://www.ptable.com/
        final PNode table = new PNode();
        for ( int i = 1; i <= 56; i++ ) {
            addElement( table, i, cellFactory, backgroundColor );
        }
        // Add in a single entry to represent the lanthanide series.
        addElement( table, 57, cellFactory, backgroundColor );
        for ( int i = 72; i <= 88; i++ ) {
            addElement( table, i, cellFactory, backgroundColor );
        }
        // Add in a single entry to represent the actinide series.
        addElement( table, 89, cellFactory, backgroundColor );
        for ( int i = 104; i <= 112; i++ ) {
            addElement( table, i, cellFactory, backgroundColor );
        }

        //Notify the cells that the rest of the table is complete.  This is so they highlighted or larger cells can move in front if necessary, to prevent clipping
        for ( int i = 0; i < table.getChildrenCount(); i++ ) {
            PNode child = table.getChild( i );
            if ( child instanceof ElementCell ) {
                ( (ElementCell) child ).tableInitComplete();
            }
        }
        addChild( table );
    }

    public static double getCellDimension() {
        return CELL_DIMENSION;
    }

    private void addElement( final PNode table, int atomicNumber, CellFactory cellFactory, Color backgroundColor ) {
        ElementCell elementCell = cellFactory.createCellForElement( atomicNumber, backgroundColor );
        final Point gridPoint = getPeriodicTableGridPoint( atomicNumber );
        double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
        double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
        elementCell.setOffset( x, y );
        table.addChild( elementCell );
    }

    /**
     * Returns a point that represents the row and column on a grid that corresponds to the layout of the standard periodic table.
     *
     * @param atomicNumber
     * @return
     */
    private Point getPeriodicTableGridPoint( int atomicNumber ) {
        //http://www.ptable.com/ was useful here
        if ( atomicNumber == 1 ) { return new Point( 1, 1 ); }
        if ( atomicNumber == 2 ) { return new Point( 1, 18 ); }
        else if ( atomicNumber == 3 ) { return new Point( 2, 1 ); }
        else if ( atomicNumber == 4 ) { return new Point( 2, 2 ); }
        else if ( atomicNumber >= 5 && atomicNumber <= 10 ) { return new Point( 2, atomicNumber + 8 ); }
        else if ( atomicNumber == 11 ) { return new Point( 3, 1 ); }
        else if ( atomicNumber == 12 ) { return new Point( 3, 2 ); }
        else if ( atomicNumber >= 13 && atomicNumber <= 18 ) { return new Point( 3, atomicNumber ); }
        else if ( atomicNumber >= 19 && atomicNumber <= 36 ) { return new Point( 4, atomicNumber - 18 ); }
        else if ( atomicNumber >= 37 && atomicNumber <= 54 ) { return new Point( 5, atomicNumber - 36 ); }
        else if ( atomicNumber == 55 ) { return new Point( 6, 1 ); }
        else if ( atomicNumber == 56 ) { return new Point( 6, 2 ); }
        else if ( atomicNumber >= 57 && atomicNumber <= 71 ) { return new Point( 6, 3 ); }
        else if ( atomicNumber >= 72 && atomicNumber <= 86 ) { return new Point( 6, atomicNumber - 68 ); }
        else if ( atomicNumber == 87 ) { return new Point( 7, 1 ); }
        else if ( atomicNumber == 88 ) { return new Point( 7, 2 ); }
        else if ( atomicNumber >= 89 && atomicNumber <= 103 ) { return new Point( 7, 3 ); }
        else if ( atomicNumber >= 104 && atomicNumber <= 118 ) { return new Point( 7, atomicNumber - 100 ); }
        else { return new Point( 1, 1 ); }
    }

    //Test Application that displays the PeriodicTableNode
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                addScreenChild( new PeriodicTableNode( Color.yellow, new HighlightElements( 1, 3, 7, 12 ) ) );
                setZoomEventHandler( getZoomEventHandler() );
                setPanEventHandler( getPanEventHandler() );
            }} );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            setSize( 1024, 768 );
        }}.setVisible( true );
    }
}
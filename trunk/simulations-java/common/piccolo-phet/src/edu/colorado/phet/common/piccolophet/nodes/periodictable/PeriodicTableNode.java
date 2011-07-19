// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.CellFactory.HighlightElements;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a node that represents a periodic table of the elements.
 * It is not interactive by default, but provides infrastructure that can be
 * used to add interactivity in subclasses.
 * <p/>
 * This makes some assumptions about which portions of the table to display,
 * and may not work for all situations where a periodic table is needed.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class PeriodicTableNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static final double CELL_DIMENSION = 20; // In screen coordinates, only one number because cells are square.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    public final Color backgroundColor;
    private final CellFactory cellFactory;

    public static interface PeriodicTableAtom {
        int getNumProtons();

        void addAtomListener( VoidFunction0 voidFunction0 );
    }

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public PeriodicTableNode( Color backgroundColor, CellFactory cellFactory ) {
        this.backgroundColor = backgroundColor;
        this.cellFactory = cellFactory;
        //See http://www.ptable.com/
        final PNode table = new PNode();
        for ( int i = 1; i <= 56; i++ ) {
            addElement( table, i );
        }
        // Add in a single entry to represent the lanthanide series.
        addElement( table, 57 );
        for ( int i = 72; i <= 88; i++ ) {
            addElement( table, i );
        }
        // Add in a single entry to represent the actinide series.
        addElement( table, 89 );
        for ( int i = 104; i <= 112; i++ ) {
            addElement( table, i );
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

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public static double getCellDimension() {
        return CELL_DIMENSION;
    }

    /**
     * Create a cell for an individual element.  Override this to create cells
     * that look different or implement some unique behavior.
     */
    protected final ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor ) {
        return new BasicElementCell( atomicNumberOfCell, backgroundColor );
    }

    private void addElement( final PNode table, int atomicNumber ) {
        ElementCell elementCell = cellFactory.createCellForElement( atomicNumber, backgroundColor );
        final Point gridPoint = getPeriodicTableGridPoint( atomicNumber );
        double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
        double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
        elementCell.setOffset( x, y );
        table.addChild( elementCell );
    }

    /**
     * Returns a point that represents the row and column on a grid that
     * corresponds to the layout of the standard periodic table.
     *
     * @param atomicNumber
     * @return
     */
    private Point getPeriodicTableGridPoint( int atomicNumber ) {
        //http://www.ptable.com/ was useful here
        if ( atomicNumber == 1 ) {
            return new Point( 1, 1 );
        }
        if ( atomicNumber == 2 ) {
            return new Point( 1, 18 );
        }
        else if ( atomicNumber == 3 ) {
            return new Point( 2, 1 );
        }
        else if ( atomicNumber == 4 ) {
            return new Point( 2, 2 );
        }
        else if ( atomicNumber >= 5 && atomicNumber <= 10 ) {
            return new Point( 2, atomicNumber + 8 );
        }
        else if ( atomicNumber == 11 ) {
            return new Point( 3, 1 );
        }
        else if ( atomicNumber == 12 ) {
            return new Point( 3, 2 );
        }
        else if ( atomicNumber >= 13 && atomicNumber <= 18 ) {
            return new Point( 3, atomicNumber );
        }
        else if ( atomicNumber >= 19 && atomicNumber <= 36 ) {
            return new Point( 4, atomicNumber - 18 );
        }
        else if ( atomicNumber >= 37 && atomicNumber <= 54 ) {
            return new Point( 5, atomicNumber - 36 );
        }
        else if ( atomicNumber == 55 ) {
            return new Point( 6, 1 );
        }
        else if ( atomicNumber == 56 ) {
            return new Point( 6, 2 );
        }
        else if ( atomicNumber >= 57 && atomicNumber <= 71 ) {
            return new Point( 6, 3 );
        }
        else if ( atomicNumber >= 72 && atomicNumber <= 86 ) {
            return new Point( 6, atomicNumber - 68 );
        }
        else if ( atomicNumber == 87 ) {
            return new Point( 7, 1 );
        }
        else if ( atomicNumber == 88 ) {
            return new Point( 7, 2 );
        }
        else if ( atomicNumber >= 89 && atomicNumber <= 103 ) {
            return new Point( 7, 3 );
        }
        else if ( atomicNumber >= 104 && atomicNumber <= 118 ) {
            return new Point( 7, atomicNumber - 100 );
        }
        return new Point( 1, 1 );
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Abstract base class for cells that comprise the periodic table.
     */
    public static abstract class ElementCell extends PNode {
        private final int atomicNumber;

        public ElementCell( int atomicNumber ) {
            this.atomicNumber = atomicNumber;
        }

        public int getAtomicNumber() {
            return atomicNumber;
        }

        //Callback that allows nodes to update after the table has been built.  This is used so that highlighted or larger cells can move themselves to the front so they aren't clipped on 2 sides
        public void tableInitComplete() {
        }
    }

    /**
     * Basic, non-interactive cell for periodic table.
     */
    public static class BasicElementCell extends ElementCell {
        private final Font LABEL_FONT = new PhetFont( 12 );
        private final PText text;
        private final PhetPPath box;

        public BasicElementCell( final int atomicNumber, final Color backgroundColor ) {
            super( atomicNumber );

            box = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ),
                                 backgroundColor, new BasicStroke( 1 ), Color.black );
            addChild( box );

            String abbreviation = SymbolTable.getSymbol( atomicNumber );
            text = new PText( abbreviation ) {{
                setFont( LABEL_FONT );
            }};
            text.setOffset( box.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2,
                            box.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
            addChild( text );
        }

        protected PText getText() {
            return text;
        }

        protected PhetPPath getBox() {
            return box;
        }
    }

    /**
     * Cell that watches the atom and highlights itself if the atomic number
     * matches its configuration.
     */
    public static class HighlightingElementCell extends BasicElementCell {
        public HighlightingElementCell( final PeriodicTableAtom atom, final int atomicNumber, final Color backgroundColor ) {
            super( atomicNumber, backgroundColor );
            atom.addAtomListener( new VoidFunction0() {
                public void apply() {
                    boolean match = atom.getNumProtons() == atomicNumber;
                    getText().setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
                    if ( match ) {
                        getBox().setStroke( new BasicStroke( 2 ) );
                        getBox().setStrokePaint( PhetColorScheme.RED_COLORBLIND );
                        getBox().setPaint( Color.white );
                        HighlightingElementCell.this.moveToFront();
                    }
                    else {
                        getText().setTextPaint( Color.BLACK );
                        getBox().setStrokePaint( Color.BLACK );
                        getBox().setPaint( backgroundColor );
                        getBox().setStroke( new BasicStroke( 1 ) );
                    }
                }
            } );
        }
    }

    /**
     * Cell that watches the atom and highlights itself if the atomic number
     * matches its configuration.
     */
    public static class HighlightedElementCell extends BasicElementCell {

        public HighlightedElementCell( final int atomicNumber, final Color backgroundColor ) {
            super( atomicNumber, backgroundColor );
            getText().setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
            getBox().setStroke( new BasicStroke( 2 ) );
            getBox().setStrokePaint( PhetColorScheme.RED_COLORBLIND );
            getBox().setPaint( Color.white );
        }

        //Wait until others are added so that moving to front will actually work, otherwise 2 sides would be clipped by nodes added after this
        @Override public void tableInitComplete() {
            super.tableInitComplete();

            //For unknown reasons, some nodes (Oxygen in sodium nitrate in sugar-and-salt solutions) get clipped if you don't schedule this for later
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    moveToFront();
                }
            } );
        }
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
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a node that represents a periodic table of the elements.
 * It is not interactive by default, but provides overrides that can be used
 * to add interactivity.
 *
 * This makes some assumptions about which portions of the table to display,
 * and may not work for all situations.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class PeriodicTableNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static int CELL_DIMENSION = 20;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    public Color backgroundColor = null;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     * @param backgroundColor
     */
    public PeriodicTableNode( final IDynamicAtom atom, Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
        //See http://www.ptable.com/
        final PNode table = new PNode();
        for ( int i = 1; i <= 56; i++ ) {
            addElement( atom, table, i );
        }
        // Add in a single entry to represent the lanthanide series.
        addElement( atom, table, 57 );
        for ( int i = 72; i <= 88; i++ ) {
            addElement( atom, table, i );
        }
        // Add in a single entry to represent the actinide series.
        addElement( atom, table, 89 );
        for ( int i = 104; i <= 112; i++ ) {
            addElement( atom, table, i );
        }

        addChild( table );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    /**
     * Override to create cells that look different or implement some unique
     * behavior.
     */
    protected ElementCell createCellForElement( IDynamicAtom atomBeingWatched, int atomicNumberOfCell, Color backgroundColor ){
        return new BasicElementCell( atomBeingWatched, atomicNumberOfCell, backgroundColor );
    }

    private void addElement( final IDynamicAtom atom, final PNode table, int atomicNumber ) {
        ElementCell elementCell = createCellForElement( atom, atomicNumber, backgroundColor );
        final Point gridPoint = getGridPoint( atomicNumber );
        double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
        double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
        elementCell.setOffset( x, y );
        table.addChild( elementCell );
        elementCellCreated( elementCell );
    }

    /**
     * Listener callback, override when needing notification of the creation
     * of element cells.  This is useful when creating an interactive chart,
     * since it is a good opportunity to hook up event listeners to the cell.
     *
     * @param elementCell
     */
    protected void elementCellCreated( ElementCell elementCell ) {
        // Does nothing by default.
    }

    /**
     * Reports (row,column) on the grid, with a 1-index
     *
     * @param i
     * @return
     */
    private Point getGridPoint( int i ) {
        //http://www.ptable.com/ was useful here
        if ( i == 1 ) {
            return new Point( 1, 1 );
        }
        if ( i == 2 ) {
            return new Point( 1, 18 );
        }
        else if ( i == 3 ) {
            return new Point( 2, 1 );
        }
        else if ( i == 4 ) {
            return new Point( 2, 2 );
        }
        else if ( i >= 5 && i <= 10 ) {
            return new Point( 2, i + 8 );
        }
        else if ( i == 11 ) {
            return new Point( 3, 1 );
        }
        else if ( i == 12 ) {
            return new Point( 3, 2 );
        }
        else if ( i >= 13 && i <= 18 ) {
            return new Point( 3, i );
        }
        else if ( i >= 19 && i <= 36 ) {
            return new Point( 4, i - 18 );
        }
        else if ( i >= 37 && i <= 54 ) {
            return new Point( 5, i - 36 );
        }
        else if ( i >= 19 && i <= 36 ) {
            return new Point( 4, i - 36 );
        }
        else if ( i == 55 ) {
            return new Point( 6, 1 );
        }
        else if ( i == 56 ) {
            return new Point( 6, 2 );
        }
        else if ( i >= 57 && i <= 71 ) {
            return new Point( 6, 3 );
        }
        else if ( i >= 72 && i <= 86 ) {
            return new Point( 6, i - 68 );
        }
        else if ( i == 87 ) {
            return new Point( 7, 1 );
        }
        else if ( i == 88 ) {
            return new Point( 7, 2 );
        }
        else if ( i >= 89 && i <= 103 ) {
            return new Point( 7, 3 );
        }
        else if ( i >= 104 && i <= 118 ) {
            return new Point( 7, i - 100 );
        }
        return new Point( 1, 1 );
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Abstract base class for cells that comprise the periodic table.
     */
    public static abstract class ElementCell extends PNode{
        private final int atomicNumber;
        private final IDynamicAtom atom;

        public ElementCell( IDynamicAtom atom, int atomicNumber ) {
            this.atom = atom;
            this.atomicNumber = atomicNumber;
        }

        public int getAtomicNumber() {
            return atomicNumber;
        }

        protected IDynamicAtom getAtom() {
            return atom;
        }
    }

    /**
     * Basic, non-interactive cell for periodic table.
     */
    public static class BasicElementCell extends ElementCell {
        private final PText text;
        private final PhetPPath box;

        public BasicElementCell( final IDynamicAtom atom, final int atomicNumber, final Color backgroundColor ) {
            super( atom, atomicNumber );
            box = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ),
                    backgroundColor, new BasicStroke( 1 ), Color.black );
            addChild( box );

            String abbreviation = AtomIdentifier.getSymbol( atomicNumber );
            text = new PText( abbreviation );
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
     * Cell that watches the atom and highlights the cell if the atomic number
     * matches that of the cell.
     */
    public static class HightlightingElementCell extends BasicElementCell {
        public HightlightingElementCell( final IDynamicAtom atom, final int atomicNumber, final Color backgroundColor ) {
            super( atom, atomicNumber, backgroundColor );
            getAtom().addAtomListener( new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    boolean match = getAtom().getNumProtons() == atomicNumber;
                    getText().setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
                    if ( match ) {
                        getBox().setStroke( new BasicStroke( 2 ) );
                        getBox().setStrokePaint( Color.RED );
                        getBox().setPaint( Color.white );
                        HightlightingElementCell.this.moveToFront();
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
}

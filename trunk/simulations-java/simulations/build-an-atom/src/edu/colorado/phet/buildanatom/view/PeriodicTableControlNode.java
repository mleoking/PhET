// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IConfigurableAtomModel;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 *
 * TODO: This class is a prototype that should be integrated with the other
 * periodic table node(s) once its behavior is somewhat finalized.
 *
 * This class defines a node that represents a periodic table of the elements
 * that contains some cells that are interactive and can be used to set the
 * configuration of the atom in a model.
 *
 * This makes some assumptions about which portions of the table to display,
 * and may not work for all situations.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class PeriodicTableControlNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static int CELL_DIMENSION = 20; // Cells are square, this is both width and height.

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    public Color backgroundColor = null;
    private final IConfigurableAtomModel model;
    private final int maxSettableAtomicNumber;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     * @param maxSettableAtomicNumber - The max atomic weight that will have a
     * button on it, thus making it interactive (i.e. settable).  Elements
     * with atomic weights greater than this number will not be interactive.
     * @param backgroundColor
     */
    public PeriodicTableControlNode( final IConfigurableAtomModel model, int maxSettableAtomicNumber, Color backgroundColor ) {
        this.backgroundColor = backgroundColor;
        this.maxSettableAtomicNumber = maxSettableAtomicNumber;
        this.model = model;
        IDynamicAtom atom = model.getAtom();
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

    private void addElement( final IDynamicAtom atom, final PNode table, int atomicNumber ) {
        PNode elementCell;
        if ( atomicNumber <= maxSettableAtomicNumber ){
            // Add an interactive element cell.
            final ButtonElementCell buttonElementCell = new ButtonElementCell( atom, atomicNumber );
            buttonElementCell.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
            buttonElementCell.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    model.setAtomConfiguration( buttonElementCell.getAtomConfiguration() );
                }
            });
            elementCell = buttonElementCell;
        }
        else{
            // Add a non-interactive element cell.
            elementCell = new BasicElementCell( atom, atomicNumber, backgroundColor );
        }
        final Point gridPoint = getGridPoint( atomicNumber );
        double x = ( gridPoint.getY() - 1 ) * CELL_DIMENSION;     //expansion cells render as "..." on top of each other
        double y = ( gridPoint.getX() - 1 ) * CELL_DIMENSION;
        elementCell.setOffset( x, y );
        table.addChild( elementCell );
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
     * PNode that represents a cell on the periodic table.  This version is a
     * basic square cell.
     */
    public class BasicElementCell extends PNode {
        private final int atomicNumber;
        private final PText text;
        private final PhetPPath cellBoundary;

        public BasicElementCell( final IDynamicAtom atom, final int atomicNumber, final Color backgroundColor ) {
            this.atomicNumber = atomicNumber;
            cellBoundary = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_DIMENSION, CELL_DIMENSION ),
                    backgroundColor, new BasicStroke( 1 ), Color.black );
            addChild( cellBoundary );

            String abbreviation = AtomIdentifier.getSymbol( atomicNumber );
            text = new PText( abbreviation );
            text.setOffset( cellBoundary.getFullBounds().getCenterX() - text.getFullBounds().getWidth() / 2,
                    cellBoundary.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
            addChild( text );
            cellBoundary.setStroke( new BasicStroke( 1 ) );
            cellBoundary.setPaint( backgroundColor );
        }
    }

    /**
     * PNode that represents a cell on the periodic table.  This version is a
     * cell that looks like a button, intended to convey to the user that it
     * is interactive.
     */
    public static class ButtonElementCell extends PNode {
        private static final Image SELECTED_IMAGE = BuildAnAtomResources.getImage( "selected-periodic-table-button.png" );
        private static final Image IDLE_IMAGE = BuildAnAtomResources.getImage( "periodic-table-button.png" );
        private static final Image FOCUSED_IMAGE = BuildAnAtomResources.getImage( "focused-periodic-table-button.png" );
        private static final Image PRESSED_IMAGE = BuildAnAtomResources.getImage( "pressed-periodic-table-button.png" );

        private final int atomicNumber;
        private final IDynamicAtom atom;
        private final PImage buttonNode;
        private final PText text;
        private final EventListenerList listeners;

        public ButtonElementCell( final IDynamicAtom atom, final int atomicNumber ) {
            this.atom = atom;
            this.atomicNumber = atomicNumber;
            listeners = new EventListenerList();

            // Create the node that will act as the button, receiving events
            // from the user.
            buttonNode = new PImage( IDLE_IMAGE );
            double buttonScale = CELL_DIMENSION / buttonNode.getFullBoundsReference().width;
            buttonNode.setScale( buttonScale );
            addChild( buttonNode );

            // Register a handler to watch for button state changes.
            ButtonEventHandler handler = new ButtonEventHandler(){{
                addButtonEventListener( new ButtonEventAdapter() {
                    boolean focus = false;
                    @Override
                    public void setFocus( boolean focus ) {
                        this.focus = focus;
                        boolean match = atom.getNumProtons() == atomicNumber;
                        if ( match ){
                            buttonNode.setImage( SELECTED_IMAGE );
                        }
                        else{
                            buttonNode.setImage( focus ? FOCUSED_IMAGE : IDLE_IMAGE );
                        }
                    }
                    @Override
                    public void setArmed( boolean armed ) {
                        if ( armed ) {
                            buttonNode.setImage( PRESSED_IMAGE );
                        }
                        else {
                            buttonNode.setImage( focus ? FOCUSED_IMAGE : IDLE_IMAGE );
                        }
                    }
                    @Override
                    public void fire() {
                        ActionEvent event = new ActionEvent( this, 0, "BUTTON_FIRED" );
                        for ( ActionListener listener : listeners.getListeners( ActionListener.class ) ) {
                            listener.actionPerformed( event );
                        }
                    }
                } );
            }};
            buttonNode.addInputEventListener( handler );

            // Add the text node that displays the chemical symbol.
            text = new PText( AtomIdentifier.getSymbol( atomicNumber ) );
            double buttonDimension = buttonNode.getFullBoundsReference().width;
            text.setPickable( false ); // Don't pick up mouse events intended for the button.
            // Scale the text to fit in the cell.
            if ( text.getFullBoundsReference().width >= buttonDimension || text.getFullBoundsReference().height >= buttonDimension ){
                System.out.println("Scaling for " + text.getText());
                text.setScale( Math.min( buttonDimension / text.getFullBoundsReference().width / buttonScale,
                        buttonDimension / text.getFullBoundsReference().height / buttonScale ) );
            }
            else{
                text.setScale( 1 / buttonScale );
            }
            text.centerFullBoundsOnPoint( buttonDimension / 2 / buttonScale, buttonDimension / 2 / buttonScale);
            buttonNode.addChild( text );

            atom.addAtomListener( new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    updateSelected();
                }
            } );
            updateSelected();
        }

        public void updateSelected() {
            boolean match = atom.getNumProtons() == atomicNumber;
            text.setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
            if ( match ) {
                buttonNode.setImage( SELECTED_IMAGE );
                buttonNode.moveToFront();
            }
            else {
                buttonNode.setImage( IDLE_IMAGE );
            }
        }

        /**
         * Get the atom configuration associated with this cell on the
         * periodic table.  The atom returned is a neutral version of the
         * most common isotope of the atom found presently on earth.
         *
         * @return
         */
        public ImmutableAtom getAtomConfiguration(){
            return AtomIdentifier.getMostCommonIsotope( atomicNumber );
        }

        public void addActionListener( ActionListener listener ) {
            listeners.add( ActionListener.class, listener );
        }

        public void removeActionListener( ActionListener listener ) {
            listeners.remove( ActionListener.class, listener );
        }
    }
}

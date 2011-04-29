// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.Color;
import java.awt.Image;

import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IConfigurableAtomModel;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines a node that represents a periodic table of the elements
 * that contains some cells that are interactive and can be used to set the
 * configuration of the atom in a model.
 *
 * @author John Blanco
 */
public class PeriodicTableControlNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    public Color backgroundColor = null;

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
    public PeriodicTableControlNode( final IConfigurableAtomModel model, final int maxSettableAtomicNumber, Color backgroundColor ) {
        IDynamicAtom atom = model.getAtom();
        PNode periodicTableNode = new PeriodicTableNode( atom, backgroundColor ){
            @Override
            protected ElementCell createCellForElement( IDynamicAtom atomBeingWatched, int atomicNumberOfCell, Color backgroundColor ) {
                if ( atomicNumberOfCell <= maxSettableAtomicNumber ){
                    // Create an interactive cell, i.e. one that looks like a
                    // button and allows the user to press it to select an
                    // element.
                    return new ButtonElementCell( atomBeingWatched, atomicNumberOfCell, model );
                }
                else{
                    // This atomic number is larger than the specified max for
                    // interactive cells, so use the basic cell from the base
                    // class.
                    return super.createCellForElement( atomBeingWatched, atomicNumberOfCell, backgroundColor );
                }
            }
        };
        addChild( periodicTableNode );
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * PNode that represents a cell on the periodic table.  This version is a
     * cell that looks like a button, intended to convey to the user that it
     * is interactive.
     */
    private static class ButtonElementCell extends PeriodicTableNode.ElementCell {
        private static final Image SELECTED_IMAGE = BuildAnAtomResources.getImage( "selected-periodic-table-button.png" );
        private static final Image IDLE_IMAGE = BuildAnAtomResources.getImage( "periodic-table-button.png" );
        private static final Image FOCUSED_IMAGE = BuildAnAtomResources.getImage( "focused-periodic-table-button.png" );
        private static final Image PRESSED_IMAGE = BuildAnAtomResources.getImage( "pressed-periodic-table-button.png" );

        private final PImage buttonNode;
        private final PText text;

        public ButtonElementCell( final IDynamicAtom atom, final int atomicNumber, final IConfigurableAtomModel model ) {
            super( atom, atomicNumber );
            addInputEventListener( new CursorHandler() );

            // Create the node that will act as the button, receiving events
            // from the user.
            buttonNode = new PImage( IDLE_IMAGE );
            double buttonScale = PeriodicTableNode.getCellDimension() / buttonNode.getFullBoundsReference().width;
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
                        // Set the configuration of the atom within the model
                        // to match this cell.
                        model.setAtomConfiguration( AtomIdentifier.getMostCommonIsotope( getAtomicNumber() ) );
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
            boolean match = getAtom().getNumProtons() == getAtomicNumber();
            text.setFont( new PhetFont( PhetFont.getDefaultFontSize(), match ) );
            if ( match ) {
                buttonNode.setImage( SELECTED_IMAGE );
                buttonNode.moveToFront();
            }
            else {
                buttonNode.setImage( IDLE_IMAGE );
            }
        }
    }
}

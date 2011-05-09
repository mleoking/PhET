// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IConfigurableAtomModel;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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
        private static final double CELL_WIDTH = PeriodicTableNode.getCellDimension();
        private static final Paint IDLE_PAINT = new GradientPaint( 0, (float)(CELL_WIDTH / 2), new Color( 220, 220, 220 ), 0, (float)CELL_WIDTH, new Color( 180, 180, 180 ) );
        private static final Paint FOCUSED_PAINT = new GradientPaint( 0, 0, new Color( 255, 255, 255), 0, (float)CELL_WIDTH, new Color(200, 200, 200 ) );
        private static final Paint PRESSED_PAINT = new GradientPaint( 0, 0, new Color( 170, 170, 170), 0, (float)CELL_WIDTH, new Color(210, 210, 210 ) );
        private static final Paint SELECTED_PAINT = Color.WHITE;
        private static final Stroke IDLE_STROKE = new BasicStroke( 1f );
        private static final Stroke SELECTED_STROKE = new BasicStroke( 2f );
        private final Font LABEL_FONT = new PhetFont( 12 );

        private final PPath buttonNode;
        private final PText text;

        public ButtonElementCell( final IDynamicAtom atom, final int atomicNumber, final IConfigurableAtomModel model ) {
            super( atom, atomicNumber );
            addInputEventListener( new CursorHandler() );

            // Create the node that will act as the button, receiving events
            // from the user.
            buttonNode = new PhetPPath( new Rectangle2D.Double( 0, 0, CELL_WIDTH, CELL_WIDTH ),
                    IDLE_PAINT, IDLE_STROKE, Color.BLACK );
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
                            buttonNode.setPaint( SELECTED_PAINT );
                        }
                        else{
                            buttonNode.setPaint( focus ? FOCUSED_PAINT : IDLE_PAINT );
                        }
                    }
                    @Override
                    public void setArmed( boolean armed ) {
                        if ( atom.getNumProtons() == atomicNumber ){
                            buttonNode.setPaint( SELECTED_PAINT );
                        }
                        else{
                            if ( armed ) {
                                buttonNode.setPaint( PRESSED_PAINT );
                            }
                            else {
                                buttonNode.setPaint( focus ? FOCUSED_PAINT : IDLE_PAINT );
                            }
                        }
                    }
                    @Override
                    public void fire() {
                        // Set the configuration of the atom within the model
                        // to match this cell.
                        if ( !( atom.getNumProtons() == atomicNumber ) ) {
                            model.setAtomConfiguration( AtomIdentifier.getMostCommonIsotope( getAtomicNumber() ) );
                        }
                    }
                } );
            }};
            buttonNode.addInputEventListener( handler );

            // Add the text node that displays the chemical symbol.
            text = new PText( AtomIdentifier.getSymbol( atomicNumber ) ){{
                setFont( LABEL_FONT );
            }};
            double buttonDimension = buttonNode.getFullBoundsReference().width;
            text.setPickable( false ); // Don't pick up mouse events intended for the button.
            // Scale the text to fit in the cell.
            if ( text.getFullBoundsReference().width >= buttonDimension || text.getFullBoundsReference().height >= buttonDimension ){
                text.setScale( Math.min( buttonDimension / text.getFullBoundsReference().width,
                        buttonDimension / text.getFullBoundsReference().height ) );
            }
            text.centerFullBoundsOnPoint( buttonDimension / 2, buttonDimension / 2 );
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
                buttonNode.setPaint( SELECTED_PAINT );
                buttonNode.setStroke( SELECTED_STROKE );
                buttonNode.setStrokePaint( PhetColorScheme.RED_COLORBLIND );
                moveToFront();
            }
            else {
                buttonNode.setPaint( IDLE_PAINT );
                buttonNode.setStroke( IDLE_STROKE );
                buttonNode.setStrokePaint( Color.BLACK );
            }
        }
    }
}

/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.buildanatom.view.PeriodicTableNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class defines the version of the periodic table that is used within
 * the Build an Atom game.  It add basic interactivity that allows the user
 * to select an atom on the chart, generally in answer to a question.
 *
 * @author John Blanco
 */
public class GamePeriodicTable extends PNode {
    private final ArrayList<SimpleObserver> jradioButtonListeners=new ArrayList<SimpleObserver>( );
    private static enum ChargeGuess {UNANSWERED, NEUTRAL_ATOM, ION};
    private final SimpleAtom atom = new SimpleAtom();
    private final Property<GamePeriodicTable.ChargeGuess> chargeGuessProperty = new Property<GamePeriodicTable.ChargeGuess>( ChargeGuess.UNANSWERED );
    private final PNode selectNeutralOrIonTypeNode;

    public GamePeriodicTable() {
        // Create the "neutral atom" / "ion" selection radio buttons
        final PSwing buttonPanelNode = new PSwing( new JPanel() {{
            final Font BUTTON_FONT = new PhetFont( 20 );
            setOpaque( false );
            setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
            add( new JRadioButton( BuildAnAtomStrings.GAME_NEUTRAL_ATOM ) {{
                setOpaque( false );
                setFont( BUTTON_FONT );
                setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                final SimpleObserver updateSelected = new SimpleObserver() {
                    public void update() {
                        setSelected( chargeGuessProperty.getValue() == ChargeGuess.NEUTRAL_ATOM );
                    }
                };
                chargeGuessProperty.addObserver( updateSelected );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        chargeGuessProperty.setValue( ChargeGuess.NEUTRAL_ATOM );
                        updateSelected.update();
                        notifyRadioButtonPressed();
                    }
                } );
            }} );
            add( new JRadioButton( BuildAnAtomStrings.GAME_ION ) {{
                setOpaque( false );
                setFont( BUTTON_FONT );
                setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                final SimpleObserver updateSelected = new SimpleObserver() {
                    public void update() {
                        setSelected( chargeGuessProperty.getValue() == ChargeGuess.ION );
                    }
                };
                chargeGuessProperty.addObserver( updateSelected );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        chargeGuessProperty.setValue( ChargeGuess.ION );
                        updateSelected.update();
                        notifyRadioButtonPressed();
                    }
                } );
            }} );
        }} );

        selectNeutralOrIonTypeNode = new PNode();
        PText buttonLabel = new PText( BuildAnAtomStrings.IS_IT ){{
            setFont( new PhetFont( 30, true ) );
        }};
        selectNeutralOrIonTypeNode.addChild( buttonLabel );
        buttonPanelNode.setOffset( buttonLabel.getFullBoundsReference().width + 5,
                buttonLabel.getFullBoundsReference().height * 0.6 - buttonPanelNode.getFullBoundsReference().height / 2 );
        selectNeutralOrIonTypeNode.addChild( buttonPanelNode );
        addChild(selectNeutralOrIonTypeNode);

        // This selector is initially invisible but then appears when the
        // user makes a selection in the periodic table.
        selectNeutralOrIonTypeNode.setVisible( false );

        // Create and add the periodic table.
        PNode periodicTableNode = new PeriodicTableNode( atom, BuildAnAtomConstants.CANVAS_BACKGROUND ) {
            @Override
            protected ElementCell createCellForElement( IDynamicAtom atomBeingWatched, int atomicNumberOfCell, Color backgroundColor ) {
                return new SelectableElementCell( GamePeriodicTable.this, atomBeingWatched, atomicNumberOfCell, backgroundColor );
            }
        };
        addChild( periodicTableNode );

        // Layout.
        selectNeutralOrIonTypeNode.setOffset(
                periodicTableNode.getFullBoundsReference().getCenterX() - selectNeutralOrIonTypeNode.getFullBoundsReference().width / 2,
                periodicTableNode.getFullBoundsReference().getMaxY() + 30 );
    }

    private void notifyRadioButtonPressed() {
        for ( SimpleObserver jradioButtonListener : jradioButtonListeners ) {
            jradioButtonListener.update();
        }
    }

    public int getGuessedNumberProtons() {
        return atom.getNumProtons();
    }

    public void setNumProtonsInAtom( int protons ) {
        atom.setNumProtons( protons );
    }

    public boolean doesAtomChargeMatchGuess( ImmutableAtom atomValue ) {
        if ( chargeGuessProperty.getValue() == ChargeGuess.UNANSWERED ) {
            return false;
        }
        return ( atomValue.isNeutral() && chargeGuessProperty.getValue() == ChargeGuess.NEUTRAL_ATOM ) || ( !atomValue.isNeutral() && chargeGuessProperty.getValue() == ChargeGuess.ION );
    }

    public void setGuessNeutral( boolean isNeutral ){
        // This assumes that the guess is being set to something other
        // than UNANSWERED.
        if ( isNeutral ){
            chargeGuessProperty.setValue( ChargeGuess.NEUTRAL_ATOM );
        }
        else{
            chargeGuessProperty.setValue( ChargeGuess.ION );
        }
        // Display the selector node in case it isn't already visible.
        selectNeutralOrIonTypeNode.setVisible( true );
    }

    public void addJRadioButtonListener( SimpleObserver simpleObserver ) {
        jradioButtonListeners.add(simpleObserver);
    }

    private static class SelectableElementCell extends PeriodicTableNode.HightlightingElementCell {
        public SelectableElementCell( final GamePeriodicTable table, final IDynamicAtom atom, final int atomicNumber, final Color backgroundColor ) {
            super( atom, atomicNumber, backgroundColor );
            addInputEventListener( new CursorHandler() );

            addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mousePressed( PInputEvent event ) {
                    table.setNumProtonsInAtom( getAtomicNumber() );
                    table.selectNeutralOrIonTypeNode.setVisible( true );
                }
            } );
        }
    }
}
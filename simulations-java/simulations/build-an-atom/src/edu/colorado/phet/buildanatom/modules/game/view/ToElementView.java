// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.buildanatom.view.PeriodicTableNode;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Base class for views in the game that include a periodic table on the right side.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class ToElementView extends ProblemView {
    private final ProblemDescriptionNode description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_FIND_THE_ELEMENT );

    private final GamePeriodicTable gamePeriodicTable = new GamePeriodicTable() {{
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.getWidth()*0.715 - getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.getHeight()/2-getFullBounds().getHeight()/2 );
        scale( 1.2 );
        super.addJRadioButtonListener(new SimpleObserver(){
            public void update() {
                enableCheckButton();
            }
        });
    }};

    /**
     * Constructor.
     */
    ToElementView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        description.centerAbove( gamePeriodicTable );
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( gamePeriodicTable );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( gamePeriodicTable );
    }

    @Override
    protected AtomValue getGuess() {
        boolean userGuessMatchesAnswerNeutrality = gamePeriodicTable.doesAtomChargeMatchGuess( getProblem().getAnswer() );
        return new AtomValue( gamePeriodicTable.getGuessedNumberProtons(),
                              getProblem().getAnswer().getNumNeutrons(),
                              userGuessMatchesAnswerNeutrality ? getProblem().getAnswer().getNumElectrons() ://if they guessed the right neutrality, assume they got the number of electrons right
                              getProblem().getAnswer().getNumElectrons() + 1 );//If they guessed the incorrect neutrality, then just return a number that differs from the correct # electrons
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        gamePeriodicTable.displayNumProtons( answer.getNumProtons() );
        gamePeriodicTable.setGuessNeutral( answer.isNeutral() );
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        //Disable the user from changing the answer
        gamePeriodicTable.setPickable( guessEditable );
        gamePeriodicTable.setChildrenPickable( guessEditable );
    }

    private static class GamePeriodicTable extends PNode {
        private final ArrayList<SimpleObserver> jradioButtonListeners=new ArrayList<SimpleObserver>( );
        private static enum ChargeGuess {UNANSWERED, NEUTRAL_ATOM, ION};
        private final SimpleAtom atom = new SimpleAtom();
        private final Property<ChargeGuess> chargeGuessProperty = new Property<ChargeGuess>( ChargeGuess.UNANSWERED );
        private final PNode selectNeutralOrIonTypeNode;

        private GamePeriodicTable() {
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

            PNode periodicTableNode = new PeriodicTableNode( atom, BuildAnAtomConstants.CANVAS_BACKGROUND ) {
                @Override
                protected void elementCellCreated( final PeriodicTableNode.ElementCell elementCell ) {
                    elementCell.addInputEventListener( new CursorHandler() );
                    elementCell.addInputEventListener( new PBasicInputEventHandler() {
                        @Override
                        public void mousePressed( PInputEvent event ) {
                            displayNumProtons( elementCell.getAtomicNumber() );
                            selectNeutralOrIonTypeNode.setVisible( true );
                        }
                    } );
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

        public void displayNumProtons( int protons ) {
            atom.setNumProtons( protons );
        }

        public boolean doesAtomChargeMatchGuess( AtomValue atomValue ) {
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
    }
}

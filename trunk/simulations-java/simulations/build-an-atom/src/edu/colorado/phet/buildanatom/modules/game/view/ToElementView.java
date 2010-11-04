package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
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
 * @author Sam Reid
 */
public abstract class ToElementView extends ProblemView {
    private final ProblemDescriptionNode description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_FIND_THE_ELEMENT );

    private final GamePeriodicTable gamePeriodicTable = new GamePeriodicTable() {{
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.getWidth()*0.715 - getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.getHeight()/2-getFullBounds().getHeight()/2 );
        scale( 1.2 );
    }};

    ToElementView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
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
        boolean userGuessMatchesAnswerNeutrality = gamePeriodicTable.isGuessNeutral() == getProblem().getAnswer().isNeutral();
        return new AtomValue( gamePeriodicTable.getGuessedNumberProtons(),
                              getProblem().getAnswer().getNeutrons(),
                              userGuessMatchesAnswerNeutrality ? getProblem().getAnswer().getElectrons() ://if they guessed the right neutrality, assume they got the number of electrons right
                              getProblem().getAnswer().getElectrons() + 1 );//If they guessed the incorrect neutrality, then just return a number that differs from the correct # electrons
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        gamePeriodicTable.displayNumProtons( answer.getProtons() );
        gamePeriodicTable.setGuessNeutral( answer.isNeutral() );
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        //Disable the user from changing the answer
        gamePeriodicTable.setPickable( guessEditable );
        gamePeriodicTable.setChildrenPickable( guessEditable );
    }

    private static class GamePeriodicTable extends PNode {
        final int[] numProtons = new int[] { 0 };//use an array because the reference must be final
        private final Atom atom;
        private final Property<Boolean> guessNeutralProperty = new Property<Boolean>( true );

        private GamePeriodicTable() {
            //TODO: this is too sneaky and should be rewritten
            //We should rewrite ElementIndicatorNode to use something more general than Atom, so it can use a large number of protons without creating and deleting Proton instances
            atom = new Atom( new Point2D.Double() ) {
                @Override
                public int getNumProtons() {
                    return numProtons[0];//Trick the ElementIndicatorNode into thinking there are numProtons[0] protons.
                }
            };
            addChild( new PeriodicTableNode( atom ) {
                @Override
                protected void elementCellCreated( final PeriodicTableNode.ElementCell elementCell ) {
                    elementCell.addInputEventListener( new CursorHandler() );
                    elementCell.addInputEventListener( new PBasicInputEventHandler() {
                        @Override
                        public void mousePressed( PInputEvent event ) {
                            displayNumProtons( elementCell.getAtomicNumber() );
                        }
                    } );
                }
            } );

            // Add the "neutral atom" / "ion" selection radio buttons
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
                            setSelected( guessNeutralProperty.getValue() );
                        }
                    };
                    guessNeutralProperty.addObserver( updateSelected );
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            guessNeutralProperty.setValue( true );
                            updateSelected.update();
                        }
                    } );
                }} );
                add( new JRadioButton( BuildAnAtomStrings.GAME_ION ) {{
                    setOpaque( false );
                    setFont( BUTTON_FONT );
                    setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );
                    final SimpleObserver updateSelected = new SimpleObserver() {
                        public void update() {
                            setSelected( !guessNeutralProperty.getValue() );
                        }
                    };
                    guessNeutralProperty.addObserver( updateSelected );
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            guessNeutralProperty.setValue( false );
                            updateSelected.update();
                        }
                    } );
                }} );
            }} );
            buttonPanelNode.setOffset( getFullBounds().getWidth() / 2 - buttonPanelNode.getFullBounds().getWidth() / 2, getFullBounds().getHeight() + 10 );
            addChild( buttonPanelNode );
            // Put a label in front of the button selection.
            // TODO: i18n
            PText buttonLabel = new PText("Is it:"){{
                setFont( new PhetFont( 30, true ) );
                setOffset( buttonPanelNode.getFullBoundsReference().getMinX() - getFullBoundsReference().width - 5,
                        buttonPanelNode.getFullBoundsReference().getCenterY() - getFullBoundsReference().height * 0.6 );
            }};
            addChild( buttonLabel );
        }

        public int getGuessedNumberProtons() {
            return numProtons[0];
        }

        public void displayNumProtons( int protons ) {
            numProtons[0] = protons;
            atom.notifyObservers();
        }

        public boolean isGuessNeutral() {
            return guessNeutralProperty.getValue();
        }

        public void setGuessNeutral( boolean isNeutral ){
            guessNeutralProperty.setValue( isNeutral );
        }
    }
}

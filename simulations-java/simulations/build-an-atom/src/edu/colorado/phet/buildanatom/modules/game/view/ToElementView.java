package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.buildanatom.view.ElementIndicatorNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public abstract class ToElementView extends ProblemView {
    private final PText description = new PText( "Complete the symbol:" ) {{//todo i18n
        setFont( new PhetFont( 20, true ) );
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 200 );
    }};

    private final GamePeriodicTable gamePeriodicTable = new GamePeriodicTable() {{
        setOffset( description.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, description.getFullBounds().getMaxY() + 20 );
    }};

    ToElementView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
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
        return new AtomValue( gamePeriodicTable.getGuessedNumberProtons(),
                              getProblem().getAnswer().getNeutrons(), getProblem().getAnswer().getElectrons() );//Assume the user would have guessed the right neutrons and electrons, since they don't even have that as an option
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        gamePeriodicTable.displayNumProtons( answer.getProtons() );
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

        private GamePeriodicTable() {
            //TODO: this is too sneaky and should be rewritten
            //We should rewrite ElementIndicatorNode to use something more general than Atom, so it can use a large number of protons without creating and deleting Proton instances
            atom = new Atom( new Point2D.Double() ) {
                @Override
                public int getNumProtons() {
                    return numProtons[0];//Trick the ElementIndicatorNode into thinking there are numProtons[0] protons.
                }
            };
            addChild( new ElementIndicatorNode( atom ) {
                @Override
                protected void elementCellCreated( final ElementIndicatorNode.ElementCell elementCell ) {
                    elementCell.addInputEventListener( new CursorHandler() );
                    elementCell.addInputEventListener( new PBasicInputEventHandler() {
                        @Override
                        public void mousePressed( PInputEvent event ) {
                            displayNumProtons( elementCell.getAtomicNumber() );
                        }
                    } );
                }
            } );
            addInputEventListener( new PBasicInputEventHandler() );
        }

        public int getGuessedNumberProtons() {
            return numProtons[0];
        }

        public void displayNumProtons( int protons ) {
            numProtons[0] = protons;
            atom.notifyObservers();
        }
    }
}

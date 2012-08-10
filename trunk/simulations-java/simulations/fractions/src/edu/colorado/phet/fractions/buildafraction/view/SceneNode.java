// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import fj.F;
import fj.data.List;

import java.util.ArrayList;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.buildafraction.view.shapes.IScoreBoxPair;
import edu.colorado.phet.fractions.buildafraction.view.shapes.SceneContext;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.BackButton;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas.INSET;
import static fj.Ord.doubleOrd;
import static fj.data.List.iterableList;

/**
 * Base class for a Scene (such as the picture or number game scene).
 *
 * @author Sam Reid
 */
public class SceneNode<T extends IScoreBoxPair> extends PNode {
    public List<T> pairs;
    protected final BackButton backButton;

    protected SceneNode( BooleanProperty audioEnabled, final SceneContext context ) {
        gameAudioPlayer = new GameAudioPlayer( audioEnabled.get() );
        audioEnabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean enabled ) {
                gameAudioPlayer.setEnabled( enabled );
            }
        } );

        backButton = new BackButton( new VoidFunction0() {
            public void apply() {
                context.goToLevelSelectionScreen();
            }
        } ) {{
            setOffset( AbstractFractionsCanvas.INSET, AbstractFractionsCanvas.INSET );
        }};
        addChild( backButton );
    }

    private final GameAudioPlayer gameAudioPlayer;

    protected void playSoundForOneComplete() { gameAudioPlayer.correctAnswer(); }

    protected void playSoundForAllComplete() { gameAudioPlayer.gameOverPerfectScore(); }

    protected void init( final double insetY, final ArrayList<T> _pairs ) {
        this.pairs = iterableList( _pairs );

        List<PNode> patterns = pairs.map( new F<T, PNode>() {
            @Override public PNode f( final T pair ) {
                return pair.getNode();
            }
        } );
        double maxWidth = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getWidth();
            }
        } ).maximum( doubleOrd );
        double maxHeight = patterns.map( new F<PNode, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getHeight();
            }
        } ).maximum( doubleOrd );

        //Layout for the scoring cells and target patterns
        double separation = 5;
        double rightInset = 10;
        final PBounds targetCellBounds = pairs.head().getTargetCell().getFullBounds();
        double offsetX = AbstractFractionsCanvas.STAGE_SIZE.width - maxWidth - separation - targetCellBounds.getWidth() - rightInset;
        double offsetY = INSET;
        for ( IScoreBoxPair pair : pairs ) {

            pair.getTargetCell().setOffset( offsetX, offsetY );
            pair.getNode().setOffset( offsetX + targetCellBounds.getWidth() + separation, offsetY + targetCellBounds.getHeight() / 2 - maxHeight / 2 );
            addChild( pair.getTargetCell() );
            addChild( pair.getNode() );

            offsetY += Math.max( maxHeight, targetCellBounds.getHeight() ) + insetY;
        }
    }

}

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.controls.GameSettingsPanel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.view.*;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {
    
    private final GameModel model;
    
    // these nodes are final, allocated once
    private final PSwing gameSettingsPanelWrapper;
    private final PSwing scoreboardPanelWrapper;
    private final FaceNode beforeFaceNode, afterFaceNode;
    private final RightArrowNode arrowNode;
    private final ReactionNumberLabelNode reactionNumberLabelNode;
    
    // these nodes are mutable, allocated when reaction changes
    private RealReactionEquationNode equationNode;
    private GameBeforeNode beforeNode;
    private GameAfterNode afterNode;
    
    public GameCanvas( GameModel model, Resettable resettable ) {
        super();
        
        // right-pointing arrow
        arrowNode = new RightArrowNode();
        addChild( arrowNode );
        
        // reaction number label
        reactionNumberLabelNode = new ReactionNumberLabelNode( model );
        addChild( reactionNumberLabelNode );
        
        // game settings
        GameSettingsPanel gameSettingsPanel = new GameSettingsPanel( model );
        gameSettingsPanelWrapper = new PSwing( gameSettingsPanel );
        gameSettingsPanelWrapper.scale( 1.5 ); //XXX scale
//        addChild( gameSettingsPanelWrapper );
        
        // scoreboard
        ScoreboardPanel scoreboardPanel = new ScoreboardPanel( model );
        scoreboardPanelWrapper = new PSwing( scoreboardPanel );
        scoreboardPanelWrapper.scale( 1.5 ); //XXX scale
        addChild( scoreboardPanelWrapper );
        
        // faces, for indicating correct/incorrect answers
        beforeFaceNode = new FaceNode();
        addChild( beforeFaceNode );
        afterFaceNode = new FaceNode();
        addChild( afterFaceNode );
        
        this.model = model;
        model.addGameListener( new GameAdapter() {
            @Override
            public void reactionChanged() {
                updateNodes();
            }
        } );

        updateNodes();
   }
    
    private void updateNodes() {

        removeChild( equationNode );
        equationNode = new RealReactionEquationNode( model.getReaction() );
        addChild( equationNode );

        removeChild( beforeNode );
        beforeNode = new GameBeforeNode();
        addChild( beforeNode );

        removeChild( afterNode );
        afterNode = new GameAfterNode();
        addChild( afterNode );
        
        beforeFaceNode.moveToFront();
        afterFaceNode.moveToFront();

        updateNodesLayout();
    }
    
    private void updateNodesLayout() {

        double x = 0;
        double y = 0;
        
        // reaction number label in upper right
        reactionNumberLabelNode.setOffset( x, y );
        
        // equation to right of label, vertically centered
        x = reactionNumberLabelNode.getFullBoundsReference().getWidth() + 35;
        y = reactionNumberLabelNode.getYOffset();
        equationNode.setOffset( x, y );
        
        // Before box below reaction number label, left justified
        x = reactionNumberLabelNode.getFullBoundsReference().getMinX();
        y = reactionNumberLabelNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );
        
        // arrow to the right of Before box, vertically centered with box
        final double arrowXSpacing = 20;
        x = beforeNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset() + ( beforeNode.getBoxHeight() / 2 );
        arrowNode.setOffset( x, y );

        // After box to the right of arrow, top aligned with Before box
        x = arrowNode.getFullBoundsReference().getMaxX() + arrowXSpacing;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );
        
        // game settings, horizontally and vertically centered in the play area
        x = equationNode.getXOffset();
        y = equationNode.getFullBoundsReference().getMaxY() + 20;
        gameSettingsPanelWrapper.setOffset( x, y );
        
        // scoreboard, at bottom center of play area
        x = gameSettingsPanelWrapper.getFullBoundsReference().getCenterX() - ( scoreboardPanelWrapper.getFullBoundsReference().getWidth() / 2 );
        y = gameSettingsPanelWrapper.getFullBoundsReference().getMaxY() + 20;
        scoreboardPanelWrapper.setOffset( x, y ) ;
        
        // faces in upper center of Before box
        x = beforeNode.getFullBoundsReference().getCenterX() - ( beforeFaceNode.getFullBoundsReference().getWidth() / 2 );
        y = beforeNode.getYOffset() + 20;
        beforeFaceNode.setOffset( x, y );
        
        // face in upper center of After box
        x = afterNode.getFullBoundsReference().getCenterX() - ( afterFaceNode.getFullBoundsReference().getWidth() / 2 );
        y = afterNode.getYOffset() + 20;
        afterFaceNode.setOffset( x, y );
    }

    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }
}

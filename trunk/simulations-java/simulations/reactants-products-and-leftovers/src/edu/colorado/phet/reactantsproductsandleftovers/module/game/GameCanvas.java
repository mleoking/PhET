
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.reactantsproductsandleftovers.controls.GameSettingsPanel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {
    
    private final GameModel model;
    
    // these nodes are final, allocated once
    private final PSwing gameSettingsPanelWrapper;
    private final PSwing scoreboardPanelWrapper;
    private final PNode smileyFaceNode, frownyFaceNode;
    
    // these nodes are mutable, allocated when reaction changes
    private RealReactionEquationNode equationNode;
    
    public GameCanvas( GameModel model, Resettable resettable ) {
        super();
        
        // game settings
        GameSettingsPanel gameSettingsPanel = new GameSettingsPanel( model );
        gameSettingsPanelWrapper = new PSwing( gameSettingsPanel );
        gameSettingsPanelWrapper.scale( 1.5 ); //XXX scale
        addChild( gameSettingsPanelWrapper );
        
        // scoreboard
        ScoreboardPanel scoreboardPanel = new ScoreboardPanel( model );
        scoreboardPanelWrapper = new PSwing( scoreboardPanel );
        scoreboardPanelWrapper.scale( 1.5 ); //XXX scale
        addChild( scoreboardPanelWrapper );
        
        // smiley face, for correct answers
        smileyFaceNode = new SmileyFaceNode();
        addChild( smileyFaceNode );
        
        // frowny face, for incorrect answers
        frownyFaceNode = new FrownyFaceNode();
        addChild( frownyFaceNode );
        
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

        if ( equationNode != null ) {
            removeChild( equationNode );
        }
        equationNode = new RealReactionEquationNode( model.getReaction() );
        addChild( equationNode );
        
        //XXX more dynamic allocation here

        updateNodesLayout();
    }
    
    private void updateNodesLayout() {

        double x = 0;
        double y = 0;
        
        //XXX equation to right of label
        equationNode.setOffset( x, y );
        
        // game settings, horizontally and vertically centered in the play area
        x = equationNode.getXOffset();
        y = equationNode.getFullBoundsReference().getMaxY() + 20;
        gameSettingsPanelWrapper.setOffset( x, y );
        
        // scoreboard, at bottom center of play area
        x = gameSettingsPanelWrapper.getFullBoundsReference().getCenterX() - ( scoreboardPanelWrapper.getFullBoundsReference().getWidth() / 2 );
        y = gameSettingsPanelWrapper.getFullBoundsReference().getMaxY() + 20;
        scoreboardPanelWrapper.setOffset( x, y ) ;
        
        //XXX put smiley face in upper center of Before or After box, depending on challenge type
        
        // put frowny face in same location as smiley face
        frownyFaceNode.setOffset( smileyFaceNode.getOffset() );
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

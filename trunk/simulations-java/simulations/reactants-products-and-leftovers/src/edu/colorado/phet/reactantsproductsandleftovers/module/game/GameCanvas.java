
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.reactantsproductsandleftovers.controls.GameSettingsPanel;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;
import edu.colorado.phet.reactantsproductsandleftovers.view.ScoreboardPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameCanvas extends RPALCanvas {
    
    private final PSwing gameSettingsPanelWrapper;
    private final PSwing scoreboardPanelWrapper;
    
    public GameCanvas( GameModel model, Resettable resettable ) {
        super();
        
        GameSettingsPanel gameSettingsPanel = new GameSettingsPanel( model );
        gameSettingsPanelWrapper = new PSwing( gameSettingsPanel );
        gameSettingsPanelWrapper.scale( 1.5 ); //XXX scale
        addChild( gameSettingsPanelWrapper );
        
        ScoreboardPanel scoreboardPanel = new ScoreboardPanel( model );
        scoreboardPanelWrapper = new PSwing( scoreboardPanel );
        scoreboardPanelWrapper.scale( 1.5 ); //XXX scale
        addChild( scoreboardPanelWrapper );
        
        // layout
        double x = 0;
        double y = 0;
        gameSettingsPanelWrapper.setOffset( x, y );
        x = gameSettingsPanelWrapper.getFullBoundsReference().getCenterX() - ( scoreboardPanelWrapper.getFullBoundsReference().getWidth() / 2 );
        y = gameSettingsPanelWrapper.getFullBoundsReference().getMaxY() + 20;
        scoreboardPanelWrapper.setOffset( x, y ) ;
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

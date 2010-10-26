package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;

/**
 * @author Sam Reid
 */
public class CountsToElementView extends ToElementView{
    private ParticleCountNode atomConfigurationText;

    public CountsToElementView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        atomConfigurationText = new ParticleCountNode( problem.getAnswer().getProtons(),
                problem.getAnswer().getNeutrons(), problem.getAnswer().getElectrons() );
        atomConfigurationText.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width /4 - atomConfigurationText.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - atomConfigurationText.getFullBounds().getHeight() / 2 );
    }

    @Override
    public void init() {
        super.init();
        addChild( atomConfigurationText );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( atomConfigurationText );
    }
}

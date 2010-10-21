package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.GameModel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
* @author Sam Reid
*/
public class HowManyParticlesProblemView extends ProblemView {
    private PText description = new PText( "How many particles?" ) {{
        setFont( new PhetFont( 20, true ) );
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
    }};

    public HowManyParticlesProblemView( GameCanvas canvas, GameModel.HowManyParticlesProblem howManyParticlesProblem, int problemIndex, int totalNumProblems ) {
        super( canvas, howManyParticlesProblem, problemIndex, totalNumProblems );

    }

    @Override
    public void init() {
        super.init();
        addChild( description );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
    }

}

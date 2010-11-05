package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SchematicToSymbolProblem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * @author Sam Reid
 * @author John Blanco
 */
public class SchematicToSymbolView extends ToSymbolProblemView {
    private final SchematicAtomNode2 gameAtomModelNode;

    /**
     * Constructor.
     */
    public SchematicToSymbolView( BuildAnAtomGameModel model, GameCanvas canvas, final SchematicToSymbolProblem problem ) {
        super( model, canvas, problem );
        ModelViewTransform2D mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.35 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.35 ) ),
                1.5,
                true );
        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock(), problem.getAnswer(), true );

        gameAtomModelNode = new SchematicAtomNode2( buildAnAtomModel, mvt, new BooleanProperty( true ) );
    }

    @Override
    public void init() {
        super.init();
        addChild( gameAtomModelNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( gameAtomModelNode );
    }
}

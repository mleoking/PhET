package edu.colorado.phet.workenergy;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.workenergy.model.WorkEnergyModel;
import edu.colorado.phet.workenergy.view.EnergyObjectNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class WorkEnergyCanvas extends PhetPCanvas {
    private ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 );
    private PNode rootNode;

    public WorkEnergyCanvas( WorkEnergyModel model ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        transform = new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( 10, 10 ),
                                              new Point2D.Double( STAGE_SIZE.width * 0.5, STAGE_SIZE.height * 0.9 ), new Point2D.Double( STAGE_SIZE.width, STAGE_SIZE.height * 0.1 ), true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        EnergyObjectNode node = new EnergyObjectNode( model.getObject(), transform );
        rootNode.addChild( new GroundNode( transform ) );
        rootNode.addChild( node );
    }
}

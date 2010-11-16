package edu.colorado.phet.insidemagnets;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class InsideMagnetsCanvas extends PhetPCanvas {
    protected final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;

    public InsideMagnetsCanvas( final InsideMagnetsModule module ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        double modelHeight = module.getInsideMagnetsModel().getLatticeHeight();
        double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();
//        transform = new ModelViewTransform2D( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true );
        transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );

        addChild( new LatticeView( transform, module.getInsideMagnetsModel().getLatticeProperty() ) );
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}

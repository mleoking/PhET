package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas extends PhetPCanvas {
    protected final ModelViewTransform2D transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;

    public FluidPressureAndFlowCanvas( final FluidPressureAndFlowModule module ) {
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );
        double modelHeight = Pool.DEFAULT_HEIGHT * 2.2;
        double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();
        transform = new ModelViewTransform2D( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );
    }

    protected void makeTransparent( JComponent component ) {
        if ( !( component instanceof JTextComponent ) ) {
            component.setBackground( new Color( 0, 0, 0, 0 ) );
            component.setOpaque( false );
        }
        for ( Component component1 : component.getComponents() ) {
            if ( component1 instanceof JComponent ) {
                makeTransparent( (JComponent) component1 );
            }
        }
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}

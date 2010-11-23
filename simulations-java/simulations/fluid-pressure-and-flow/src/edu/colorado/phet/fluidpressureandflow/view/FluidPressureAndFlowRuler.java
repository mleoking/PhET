package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowRuler extends PNode {
    public FluidPressureAndFlowRuler( ModelViewTransform transform, final Property<Boolean> visible, double length, String[] majorTicks, String units, Point2D rulerModelOrigin ) {
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        final RulerNode rulerNode = new RulerNode( length,
                                                   50, majorTicks, units, 4, 18 );
        rulerNode.rotate( -Math.PI / 2 );
        rulerNode.setOffset( transform.modelToViewX( rulerModelOrigin.getX() ),
                             transform.modelToViewY( rulerModelOrigin.getY() ) + rulerNode.getInsetWidth() );
        addChild( rulerNode );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width, delta.height );
            }
        } );
//        addChild( new PSwing( new JButton( new ImageIcon( PhetCommonResources.getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON ) )) {{
//            addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    visible.setValue( false );
//                }
//            } );
//        }} ){{
        addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) {{
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    visible.setValue( false );
                }
            } );
            setOffset( rulerNode.getFullBounds().getOrigin().getX(), rulerNode.getFullBounds().getOrigin().getY() - getFullBounds().getHeight() );
        }} );
    }
}

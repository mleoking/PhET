// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
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
    public FluidPressureAndFlowRuler( final ModelViewTransform transform,
                                      final ObservableProperty<Boolean> visible,//getter
                                      final Property<Boolean> setVisible, //setter, separate from getter since has to be 'and'ed with units property in FluidPressureCanvas
                                      double length,
                                      String[] majorTicks,
                                      String units,
                                      final Point2D rulerModelOrigin,
                                      ResetModel resetModel ) {
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
        final RulerNode rulerNode = new RulerNode( length, 50, majorTicks, units, 4, 15 ) {{
            setInsetWidth( 0 );//so 0 is exactly at edge of ruler
        }};
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
        addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) {{
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    setVisible.setValue( false );
                }
            } );
            setOffset( rulerNode.getFullBounds().getOrigin().getX(), rulerNode.getFullBounds().getOrigin().getY() - getFullBounds().getHeight() );
        }} );
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                setOffset( 0, 0 );
            }
        } );//just undo the part modified by user translation of the ruler
    }
}

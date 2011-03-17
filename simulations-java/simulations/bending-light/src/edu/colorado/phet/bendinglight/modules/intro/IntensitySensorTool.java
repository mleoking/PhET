// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.IntensityMeterNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class IntensitySensorTool extends PImage {
    public IntensitySensorTool( final BendingLightCanvas canvas, final ModelViewTransform transform, final IntensityMeter intensityMeter, final double modelWidth, final double modelHeight, final ToolboxNode parent, IntensityMeterNode iconNode, int sensorIconHeight ) {
        //TODO: some constants copied from BendingLightModel
        //TODO: functionality and some code copied from protractor toolbox item above
        //TODO: can this be rewritten to use class Tool?
        super( iconNode.toImage( ToolboxNode.ICON_WIDTH, sensorIconHeight, new Color( 0, 0, 0, 0 ) ) );
        final PImage sensorThumbnailRef = this;
        addInputEventListener( new PBasicInputEventHandler() {
            IntensityMeterNode node = null;
            boolean intersect = false;

            public void mousePressed( PInputEvent event ) {
                intensityMeter.enabled.setValue( true );
                if ( node == null ) {
                    node = new IntensityMeterNode( transform, intensityMeter );
                    intensityMeter.sensorPosition.setValue( new ImmutableVector2D( modelWidth * 0.3, -modelHeight * 0.3 ) );
                    intensityMeter.bodyPosition.setValue( new ImmutableVector2D( modelWidth * 0.4, -modelHeight * 0.3 ) );
                    final PropertyChangeListener pcl = new PropertyChangeListener() {
                        public void propertyChange( PropertyChangeEvent evt ) {
                            if ( node != null ) {
                                final Rectangle2D sensorBounds = node.getSensorGlobalFullBounds();
                                final Rectangle2D bodyBounds = node.getBodyGlobalFullBounds();
                                intersect = parent.getGlobalFullBounds().contains( sensorBounds.getCenterX(), sensorBounds.getCenterY() ) ||
                                            parent.getGlobalFullBounds().contains( bodyBounds.getCenterX(), bodyBounds.getCenterY() );
                            }
                        }
                    };
                    intensityMeter.enabled.addObserver( new SimpleObserver() {
                        public void update() {
                            sensorThumbnailRef.setVisible( !intensityMeter.enabled.getValue() );
                            if ( !intensityMeter.enabled.getValue() && node != null ) {//user closed it with the red 'x' button on the sensor body (also called when dragged back to toolbox, but that's okay)
                                node.removePropertyChangeListener( pcl );
                                intensityMeter.enabled.setValue( false );
                                sensorThumbnailRef.setVisible( true );
                                canvas.removeChildBehindLight( node );
                                node = null;//signify that we should create + init a new one on next drag so that it drags from the right location.
                            }
                        }
                    } );
                    final ImmutableVector2D modelPt = new ImmutableVector2D( transform.viewToModel( event.getPositionRelativeTo( getParent().getParent().getParent() ) ) );
                    final ImmutableVector2D delta = modelPt.minus( intensityMeter.sensorPosition.getValue() );
                    intensityMeter.translateAll( new PDimension( delta.getX(), delta.getY() ) );
                    node.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl );
                    node.addInputEventListener( new PBasicInputEventHandler() {
                        public void mouseReleased( PInputEvent event ) {
                            if ( intersect && node != null ) {
                                node.removePropertyChangeListener( pcl );
                                canvas.removeChildBehindLight( node );
                                intensityMeter.enabled.setValue( false );
                                sensorThumbnailRef.setVisible( true );
                                node = null;
                            }
                        }
                    } );

                    canvas.addChildBehindLight( node );
                }
            }

            public void mouseDragged( PInputEvent event ) {
                node.doTranslate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
            }

            public void mouseReleased( PInputEvent event ) {
                if ( intersect ) {
                    intensityMeter.enabled.setValue( false );
                    sensorThumbnailRef.setVisible( true );
//                        node.removePropertyChangeListener( pcl );//TODO: what to do about this?
                    canvas.removeChildBehindLight( node );
                    node = null;
                }
            }
        } );
        addInputEventListener( new CursorHandler() );
    }
}

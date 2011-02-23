// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.IntensityMeter;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.NormalLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.lightreflectionandrefraction.model.LRRModel.WAVELENGTH_RED;

/**
 * @author Sam Reid
 */
public class ToolboxNode extends PNode {
    public ToolboxNode( final LightReflectionAndRefractionCanvas canvas, final ModelViewTransform transform,
                        final BooleanProperty showProtractor, BooleanProperty showNormal, final IntensityMeter intensityMeter ) {
        final PText titleLabel = new PText( "Toolbox" ) {{
            setFont( LightReflectionAndRefractionCanvas.labelFont );
        }};
        addChild( titleLabel );
        final int ICON_HEIGHT = 100;
        final BufferedImage image = BufferedImageUtils.multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "protractor.png" ), ICON_HEIGHT );
        final PImage protractor = new PImage( image ) {{
            showProtractor.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( !showProtractor.getValue() );
                }
            } );
            final PImage protractorThumbRef = this;
            setOffset( 0, titleLabel.getFullBounds().getMaxY() );
            addInputEventListener( new PBasicInputEventHandler() {
                ProtractorNode node = null;
                boolean intersect = false;

                public void mouseDragged( PInputEvent event ) {
                    showProtractor.setValue( true );
                    setVisible( false );
                    if ( node == null ) {
                        final Point2D positionRelativeTo = event.getPositionRelativeTo( getParent().getParent().getParent() );//why?
                        Point2D model = transform.viewToModel( positionRelativeTo );
                        node = new ProtractorNode( transform, showProtractor, model.getX(), model.getY() );
                        node.translate( -node.getFullBounds().getWidth() / 2, node.getFullBounds().getHeight() / 2 );//Center on the mouse
                        final PropertyChangeListener pcl = new PropertyChangeListener() {
                            public void propertyChange( PropertyChangeEvent evt ) {
                                intersect = ToolboxNode.this.getGlobalFullBounds().intersects( node.getGlobalFullBounds() );
                            }
                        };
                        node.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl );
                        node.addInputEventListener( new PBasicInputEventHandler() {
                            public void mouseReleased( PInputEvent event ) {
                                if ( intersect ) {
                                    showProtractor.setValue( false );
                                    protractorThumbRef.setVisible( true );
                                    node.removePropertyChangeListener( pcl );
                                    canvas.removeChild( node );
                                    node = null;
                                }
                            }
                        } );

                        canvas.addChild( node );
                    }
                    node.doDrag( event );
                }

                //This is when the user drags the object out of the toolbox then drops it right back in the toolbox.
                public void mouseReleased( PInputEvent event ) {
                    if ( intersect ) {
                        showProtractor.setValue( false );
                        protractorThumbRef.setVisible( true );
                        canvas.removeChild( node );
                        node = null;
                        //TODO: how to remove pcl?
                    }
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};
        addChild( protractor );

        //TODO: some constants copied from LRRModel
        //TODO: functionality and some code copied from protractor toolbox item above
        final double modelWidth = WAVELENGTH_RED * 62;
        final double modelHeight = modelWidth * 0.7;
        final IntensityMeterNode iconNode = new IntensityMeterNode( transform, new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 ) {{
            enabled.setValue( true );
        }} );
        final PImage sensorThumbnail = new PImage( iconNode.toImage( (int) ( 100.0 * iconNode.getFullBounds().getWidth() / iconNode.getFullBounds().getHeight() ), 100, new Color( 0, 0, 0, 0 ) ) ) {{
            final PImage sensorThumbnailRef = this;
            addInputEventListener( new PBasicInputEventHandler() {
                IntensityMeterNode node = null;
                boolean intersect = false;

                public void mouseDragged( PInputEvent event ) {
                    intensityMeter.enabled.setValue( true );
                    if ( node == null ) {
                        node = new IntensityMeterNode( transform, intensityMeter );
                        intensityMeter.sensorPosition.setValue( new ImmutableVector2D( modelWidth * 0.3, -modelHeight * 0.3 ) );
                        intensityMeter.bodyPosition.setValue( new ImmutableVector2D( modelWidth * 0.4, -modelHeight * 0.3 ) );
                        final PropertyChangeListener pcl = new PropertyChangeListener() {
                            public void propertyChange( PropertyChangeEvent evt ) {
                                if ( node != null ) {
                                    intersect = ToolboxNode.this.getGlobalFullBounds().intersects( node.getSensorGlobalFullBounds() ) ||
                                                ToolboxNode.this.getGlobalFullBounds().intersects( node.getBodyGlobalFullBounds() );
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
                        final ImmutableVector2D delta = modelPt.getSubtractedInstance( intensityMeter.sensorPosition.getValue() );
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
            setOffset( protractor.getFullBounds().getMaxX() + 10, protractor.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }};
        addChild( sensorThumbnail );

        final PSwing showNormalCheckBox = new PSwing( new PropertyCheckBox( "Show Normal", showNormal ) {{
            setFont( LightReflectionAndRefractionCanvas.labelFont );
            setBackground( new Color( 0, 0, 0, 0 ) );
        }} ) {{
            setOffset( sensorThumbnail.getFullBounds().getMaxX() + 10, titleLabel.getFullBounds().getMaxY() );
        }};
        addChild( showNormalCheckBox );

        NormalLine normalLine = new NormalLine( transform, modelHeight, 4 );
        PImage normalLineThumbnail = new PImage( normalLine.toImage( 67, 67, new Color( 0, 0, 0, 0 ) ) ) {{
            setOffset( showNormalCheckBox.getFullBounds().getCenterX(), showNormalCheckBox.getFullBounds().getMaxY() );
        }};
        addChild( normalLineThumbnail );
        titleLabel.setOffset( getFullBounds().getWidth() / 2 - titleLabel.getFullBounds().getWidth() / 2, 0 );
    }
}
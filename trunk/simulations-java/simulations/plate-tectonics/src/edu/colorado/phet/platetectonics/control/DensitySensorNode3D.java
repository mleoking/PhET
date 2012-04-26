// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Cursor;
import java.text.MessageFormat;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PointSensor;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerSensorNode;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.PlanarPiccoloNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Displays a speedometer-style draggable readout.
 */
public class DensitySensorNode3D extends PlanarPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    public static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    private final LWJGLTransform modelViewTransform;
    private final PlateTectonicsTab tab;
    private final PlateModel model;

    private int scaleFactor = 1;

    public ImmutableVector2F draggedPosition = new ImmutableVector2F();

    public DensitySensorNode3D( final LWJGLTransform modelViewTransform, final PlateTectonicsTab tab, PlateModel model ) {

        //TODO: rewrite with composition instead of inheritance
        super( new DensitySensorNode2D( modelViewTransform.transformDeltaX( (float) 1000 ), tab ) {{
            scale( scaleMultiplier( tab ) );
        }} );
        this.modelViewTransform = modelViewTransform;
        this.tab = tab;
        this.model = model;

        // scale the node to handle the subsampling
        // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
        tab.zoomRatio.addObserver( new SimpleObserver() {
            public void update() {
                final ImmutableMatrix4F scaling = ImmutableMatrix4F.scaling( getScale() );
                final ImmutableMatrix4F translation = ImmutableMatrix4F.translation( draggedPosition.x - getSensorXOffset(),
                                                                                     draggedPosition.y,
                                                                                     0 );
                transform.set( translation.times( scaling ) );

                final int newScaleFactor = (int) Math.floor( ( tab.getSceneDistanceZoomFactor() - 1 ) / 10 ) + 1;
                final boolean changed = newScaleFactor != scaleFactor;
                scaleFactor = newScaleFactor;
                if ( changed ) {
                    updateReadout();

                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            final DensitySensorNode2D node = (DensitySensorNode2D) getNode();
                            node.setMultiplier( scaleFactor );
                            node.repaint();
                        }
                    } );
                }
            }
        } );

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      updateReadout();
                                                  }
                                              }, true );

        updateOnEvent( tab.beforeFrameRender );
    }

    private float getScale() {
        return tab.getSceneDistanceZoomFactor() * 0.75f / PICCOLO_PIXELS_TO_VIEW_UNIT;
    }

    public boolean allowsDrag( ImmutableVector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( ImmutableVector2F delta ) {
        transform.prepend( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );
        draggedPosition = draggedPosition.plus( delta );
        updateReadout();
//        tab.getModel().debugPing.updateListeners( getSensorModelPosition() );
    }

    private void updateReadout() {
        // get model coordinates
        // TODO: improve model/view and listening for sensor location
        final Double density = getDensityValue();
        final DensitySensorNode2D node = (DensitySensorNode2D) getNode();
        final double densityToShow = density / ( (float) scaleFactor );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                node.pointSensor.value.set( new Option.Some<Double>( densityToShow ) );
                LWJGLUtils.invoke( new Runnable() {
                    public void run() {
                        repaint();
                    }
                } );
            }
        } );
    }

    private Double getDensityValue() {
        ImmutableVector3F modelSensorPosition = getSensorModelPosition();
        return model.getDensity( modelSensorPosition.getX(), modelSensorPosition.getY() );
    }

    public ImmutableVector3F getSensorModelPosition() {
        return modelViewTransform.inversePosition( new ImmutableVector3F( draggedPosition.x, draggedPosition.y, 0 ) );
    }

    private float getSensorXOffset() {
        return (float) ( ( (DensitySensorNode2D) getNode() ).horizontalSensorOffset * getScale() * scaleMultiplier( tab ) );
    }

    public ParameterSet getCustomParameters() {
        return new ParameterSet( new Parameter( ParameterKeys.value, getDensityValue() ) );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.densitySensorInToolbox;
    }

    public ImmutableVector2F getInitialMouseOffset() {
        final double s = getScale();
        return new ImmutableVector2F( 0, ( DensitySensorNode2D.h / 3 ) * s );
    }

    public IUserComponent getUserComponent() {
        return UserComponents.densityMeter;
    }

    public void recycle() {
        getParent().removeChild( this );
    }

    private static int scaleMultiplier( PlateTectonicsTab tab ) {
        return ( tab instanceof PlateMotionTab ) ? 3 : 1;
    }

    /**
     * @author Sam Reid
     */
    public static class DensitySensorNode2D extends SpeedometerSensorNode {

        // TODO: change this to a 2D offset
        public final double horizontalSensorOffset;

        final PText modifierText;
        public static double w;
        public static double h;

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public DensitySensorNode2D( float kmToViewUnit, PlateTectonicsTab tab ) {
            super( ModelViewTransform.createIdentity(), new PointSensor<Double>( 0, 0 ) {{

                       //Start by showing needle at 0.0 instead of hiding it
                       value.set( new Some<Double>( 0.0 ) );
                   }}, "Density", 3500 );

            w = getFullBounds().getWidth();
            h = getFullBounds().getHeight();

            modifierText = new PText( "" ) {{
                setFont( new PhetFont( 16, true ) );
            }};
            addChild( modifierText );
            positionText();

            // scale it so that we achieve adherence to the model scale
            scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

            horizontalSensorOffset = getFullBounds().getWidth() / 2;

            // give it the "Hand" cursor
            addInputEventListener( new LWJGLCursorHandler() );
        }

        public void positionText() {
            modifierText.setOffset( w / 2 - modifierText.getFullBounds().getWidth() / 2,
                                    h * 6 / 11 );
        }

        public void setMultiplier( int multiplier ) {
            modifierText.setText( multiplier == 1 ? "" : MessageFormat.format( Strings.SENSOR_MULTIPLIER, multiplier ) );
            positionText();
        }
    }
}
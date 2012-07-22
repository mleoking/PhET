// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.ThreadedPlanarPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Displays a ruler in the 3D play area space
 */
public class ThermometerNode3D extends ThreadedPlanarPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    public static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much "temperature" fits in each thermometer
    public static final double DEGREES_C_IN_THERMOMETER = 2000;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    public static final float PIXEL_SCALE = 3f;

    // lowest value that can be passed in
    public static final double THERMOMETER_MIN = 0.2;

    // largest value that can be passed in
    public static final double THERMOMETER_MAX = 0.9;

    private final LWJGLTransform modelViewTransform;
    private final PlateTectonicsTab tab;
    private final PlateModel model;

    private final float sensorVerticalOffset;

    public Vector2F draggedPosition = new Vector2F();

    public ThermometerNode3D( final LWJGLTransform modelViewTransform, final PlateTectonicsTab tab, PlateModel model ) {

        //TODO: rewrite with composition instead of inheritance
        super( new ThermometerNode2D( modelViewTransform.transformDeltaX( (float) 1000 ) ) {{
            scale( scaleMultiplier( tab ) );
        }} );
        this.modelViewTransform = modelViewTransform;
        this.tab = tab;
        this.model = model;

        sensorVerticalOffset = (float) ( (ThermometerNode2D) getNode() ).sensorVerticalOffset;

        // scale the node to handle the subsampling
//        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );
        tab.zoomRatio.addObserver( new SimpleObserver() {
            public void update() {
                final ImmutableMatrix4F scaling = ImmutableMatrix4F.scaling( getScale() );
                final ImmutableMatrix4F translation = ImmutableMatrix4F.translation( draggedPosition.x,
                                                                                     draggedPosition.y - sensorVerticalOffset * getScale() * scaleMultiplier( tab ),
                                                                                     0 );
                transform.set( translation.times( scaling ) );

                ( (LiquidExpansionThermometerNode) getNode() ).setTicks( 10 / getTemperatureScale(), Color.BLACK, 1 );
            }
        } );

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

        model.modelChanged.addUpdateListener( new UpdateListener() {
            public void update() {
                final double temp = getTemperatureValue();
                updateLiquidHeight( temp );
            }
        }, true );

        repaintOnEvent( tab.beforeFrameRender );
    }

    private float getTemperatureScale() {
        return 1;
//        return 1 + ( tab.getSceneDistanceZoomFactor() - 1 ) / 8;
    }

    private float getScale() {
        return tab.getSceneDistanceZoomFactor() / PICCOLO_PIXELS_TO_VIEW_UNIT;
    }

    public boolean allowsDrag( Vector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( Vector2F delta ) {
        this.transform.prepend( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );
        draggedPosition = draggedPosition.plus( delta );
        final double temp = getTemperatureValue();
        updateLiquidHeight( temp );
//        tab.getModel().debugPing.updateListeners( getSensorModelPosition() );
    }

    private void updateLiquidHeight( double temperature ) {
        final ThermometerNode2D node = (ThermometerNode2D) getNode();
        node.setTemperature( temperature );
        repaint();
    }

    private Double getTemperatureValue() {
        Vector3F modelSensorPosition = getSensorModelPosition();
        return model.getTemperature( modelSensorPosition.getX(), modelSensorPosition.getY() );
    }

    public Vector3F getSensorModelPosition() {
        return PlateModel.convertToPlanar(
                modelViewTransform.inversePosition(
                        getSensorViewPosition() ) );
    }

    public Vector3F getSensorViewPosition() {
        return new Vector3F( draggedPosition.x, draggedPosition.y, 0 );
    }

    public Option<Vector3F> getSensorTerrainPosition() {
        // TODO: actually do this!
//        return new None<Vector3F>();
        throw new RuntimeException( "not implemented" );
    }

    public ParameterSet getCustomParameters() {
        return new ParameterSet( new Parameter( ParameterKeys.value, getTemperatureValue() ) );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.thermometerInToolbox;
    }

    public Vector2F getInitialMouseOffset() {
        final double s = getScale();

        return new Vector2F( getNode().getFullBounds().getWidth() / 2 * s, 0 );
    }

    public IUserComponent getUserComponent() {
        return UserComponents.thermometer;
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
    public static class ThermometerNode2D extends LiquidExpansionThermometerNode {

        // TODO: change this to a 2D offset
        public final double sensorVerticalOffset;

        private final PNode extraThermometerHolderNode = new PNode();

        private static final PDimension THERMOMETER_DIMENSION = new PDimension( 50 * 0.8, 150 * 0.8 );

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public ThermometerNode2D( float kmToViewUnit ) {
            super( THERMOMETER_DIMENSION );

            // add in an arrow to show where we are sensing the temperature
            final double sensorHeight = getFullBounds().getHeight() - getBulbDiameter() / 2;
            addChild( new PhetPPath( new DoubleGeneralPath( -8, sensorHeight ) {{
                lineToRelative( 5, 5 );
                lineToRelative( 0, -10 );
                lineToRelative( -5, 5 );
            }}.getGeneralPath(), Color.RED, new BasicStroke( 1 ), Color.BLACK ) );

            addChild( extraThermometerHolderNode );

            // scale it so that we achieve adherence to the model scale
            scale( PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / PIXEL_SCALE );

            sensorVerticalOffset = getBulbDiameter() / 2 * getScale();

            // give it the "Hand" cursor
            addInputEventListener( new LWJGLCursorHandler() );
        }

        public void setTemperature( double temperature ) {
            final double relativeTemp = ( temperature - PlateModel.ZERO_CELSIUS ) / DEGREES_C_IN_THERMOMETER;

            int extraThermometerQuantity = (int) Math.floor( relativeTemp );

            double liquidHeight = MathUtil.clamp( THERMOMETER_MIN,
                                                  new Function.LinearFunction( 0, 1, THERMOMETER_MIN, 0.9 ).evaluate( relativeTemp % 1 ),
                                                  THERMOMETER_MAX );

            setLiquidHeight( liquidHeight );

            extraThermometerHolderNode.removeAllChildren();
            for ( int i = 0; i < extraThermometerQuantity; i++ ) {
                final int finalI = i;
                extraThermometerHolderNode.addChild( new LiquidExpansionThermometerNode( THERMOMETER_DIMENSION ) {{
                    setOffset( 45 + finalI * 30, 0 );
                    scale( 0.4 );
                    setLiquidHeight( THERMOMETER_MAX );
                }} );
            }
        }
    }
}

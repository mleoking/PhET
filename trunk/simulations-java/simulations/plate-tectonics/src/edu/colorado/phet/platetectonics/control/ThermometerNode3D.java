// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.PlanarPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Displays a ruler in the 3D play area space
 */
public class ThermometerNode3D extends PlanarPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    public static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    public static final float PIXEL_SCALE = 3f;

    private final LWJGLTransform modelViewTransform;
    private final PlateTectonicsTab tab;
    private final PlateModel model;

    private final float sensorVerticalOffset;

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
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      updateLiquidHeight();
                                                  }
                                              }, true );

        updateOnEvent( tab.beforeFrameRender );
    }

    public boolean allowsDrag( ImmutableVector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( ImmutableVector2F delta ) {
        this.transform.prepend( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );
        updateLiquidHeight();
//        tab.getModel().debugPing.updateListeners( getSensorModelPosition() );
    }

    private void updateLiquidHeight() {
        // get model coordinates
        // TODO: improve model/view and listening for sensor location
        ImmutableVector3F modelSensorPosition = getSensorModelPosition();
        final Double temp = model.getTemperature( modelSensorPosition.getX(), modelSensorPosition.getY() );
        double liquidHeight = MathUtil.clamp( 0.2, new Function.LinearFunction( 290, 2000, 0.2, 0.8 ).evaluate( temp ), 1 );
        ( (LiquidExpansionThermometerNode) getNode() ).setLiquidHeight( liquidHeight );
        repaint();
    }

    public ImmutableVector3F getSensorModelPosition() {
        return modelViewTransform.inversePosition( transform.getMatrix().getTranslation().plus( new ImmutableVector3F( 0, sensorVerticalOffset / PICCOLO_PIXELS_TO_VIEW_UNIT * scaleMultiplier( tab ), 0 ) ) );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.thermometerInToolbox;
    }

    public ImmutableVector2F getInitialMouseOffset() {
        return new ImmutableVector2F( 10, 10 );
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

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public ThermometerNode2D( float kmToViewUnit ) {
            super( new PDimension( 50 * 0.8, 150 * 0.8 ) );

            // add in an arrow to show where we are sensing the temperature
            final double sensorHeight = getFullBounds().getHeight() - getBulbDiameter() / 2;
            addChild( new PhetPPath( new DoubleGeneralPath( -8, sensorHeight ) {{
                lineToRelative( 5, 5 );
                lineToRelative( 0, -10 );
                lineToRelative( -5, 5 );
            }}.getGeneralPath(), Color.RED, new BasicStroke( 1 ), Color.BLACK ) );

            // scale it so that we achieve adherence to the model scale
            scale( PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / PIXEL_SCALE );

            sensorVerticalOffset = getBulbDiameter() / 2 * getScale();

            // give it the "Hand" cursor
            addInputEventListener( new LWJGLCursorHandler() );
        }
    }
}

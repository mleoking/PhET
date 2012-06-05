// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;

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
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PointSensor;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerNode;
import edu.colorado.phet.common.piccolophet.nodes.SpeedometerSensorNode;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.ThreadedPlanarPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Displays a speedometer-style draggable readout.
 */
public class DensitySensorNode3D extends ThreadedPlanarPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    public static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    public static final float MAX_SPEEDOMETER_DENSITY = 3500;

    private final LWJGLTransform modelViewTransform;
    private final PlateTectonicsTab tab;
    private final PlateModel model;

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
            }
        } );

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

        model.modelChanged.addUpdateListener( new UpdateListener() {
            public void update() {
                updateReadout();
            }
        }, true );

        repaintOnEvent( tab.beforeFrameRender );
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
        node.setDensity( density );
        repaint();
    }

    private Double getDensityValue() {
        ImmutableVector3F modelSensorPosition = getSensorModelPosition();
        return model.getDensity( modelSensorPosition.getX(), modelSensorPosition.getY() );
    }

    public ImmutableVector3F getSensorModelPosition() {
        return PlateModel.convertToPlanar(
                modelViewTransform.inversePosition(
                        new ImmutableVector3F( draggedPosition.x, draggedPosition.y, 0 ) ) );
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

        public static double w;
        public static double h;

        private final PNode extraHolderNode = new PNode();

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public DensitySensorNode2D( float kmToViewUnit, PlateTectonicsTab tab ) {
            super( ModelViewTransform.createIdentity(), new PointSensor<Double>( 0, 0 ) {{

                //Start by showing needle at 0.0 instead of hiding it
                value.set( new Some<Double>( 0.0 ) );
            }}, Strings.DENSITY_VIEW, MAX_SPEEDOMETER_DENSITY );

            w = getFullBounds().getWidth();
            h = getFullBounds().getHeight();

            addChild( extraHolderNode );

            // scale it so that we achieve adherence to the model scale
            scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

            horizontalSensorOffset = getFullBounds().getWidth() / 2;

            // give it the "Hand" cursor
            addInputEventListener( new LWJGLCursorHandler() );
        }

        public void setDensity( double density ) {
            // reference into the speedometer to change it
            pointSensor.value.set( new Option.Some<Double>( density ) );

            // calculate using the speedometer what the angles are at 0 and max
            // speedometer returns angles that are actually the opposite (negative) of what is usually used in the cartesian plane
            final double minAngle = -bodyNode.speedToAngle( 0 );
            final double maxAngle = -bodyNode.speedToAngle( MAX_SPEEDOMETER_DENSITY );

            // reverse the linear transformation to figure out how much density we need to wrap all the way around
            final double anglePerUnitDensity = Math.abs( minAngle - maxAngle ) / MAX_SPEEDOMETER_DENSITY;
            final double wrapAroundDensityAmount = 2 * Math.PI / anglePerUnitDensity;

            int overflowQuantity = (int) Math.floor( density / wrapAroundDensityAmount );

            extraHolderNode.removeAllChildren();
            for ( int i = 0; i < overflowQuantity; i++ ) {
                final int finalI = i;

                // "wrapped around" image
                extraHolderNode.addChild( new SpeedometerNode( "", 100, new Property<Option<Double>>( new Some<Double>( (double) 0 ) ), MAX_SPEEDOMETER_DENSITY ) {{
                    setOffset( 110 + finalI * 50, 0 );
                    scale( 0.4 );

                    final double centerX = 50;
                    final double centerY = 50;

                    final double radius = 30;

                    double angularGap = 0.8;

                    final double angleAtEnd = minAngle + angularGap / 2;

                    addChild( new PhetPPath( new Arc2D.Double( centerX - radius, centerY - radius, // center
                                                               radius * 2, radius * 2,
                                                               Math.toDegrees( angleAtEnd ),
                                                               Math.toDegrees( 2 * Math.PI - angularGap ),
                                                               Arc2D.OPEN ),
                                             null, new BasicStroke( 3 ), Color.RED ) );

                    addChild( new PPath() {{
                        GeneralPath path = new GeneralPath();
                        double pointAngle = angleAtEnd - 0.1;
                        double backAngle = angleAtEnd + 0.4;
                        path.moveTo( Math.cos( pointAngle ) * radius,
                                     -Math.sin( pointAngle ) * radius );

                        path.lineTo( Math.cos( backAngle ) * radius * 0.75,
                                     -Math.sin( backAngle ) * radius * 0.75 );

                        path.lineTo( Math.cos( backAngle ) * radius * 1.25,
                                     -Math.sin( backAngle ) * radius * 1.25 );

                        setPathTo( path );
                        setOffset( centerX, centerY );
                        setPaint( Color.RED );
                        setStrokePaint( null );
                    }} );
                }} );
            }
        }
    }
}
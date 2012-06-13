// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.DampedMassSpringSystem;
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
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.ThreadedPlanarPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsApplication;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.umd.cs.piccolo.PCanvas;
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

        // this should guarantee running each time
        tab.beforeFrameRender.addUpdateListener( new UpdateListener() {
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
//        ImmutableVector2F localPosition = new ImmutableVector2F(
//                initialPosition.x - draggedPosition.x + getSensorXOffset(),
//                initialPosition.y - draggedPosition.y
//        );
//        System.out.println( "initialPosition = " + initialPosition );
//        System.out.println( "localPosition = " + localPosition );
//        return getNode().fullIntersects( new PBounds( localPosition.x, localPosition.y, 0.1, 0.1 ) );
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
        node.setDensity( density, tab.getClock().getSimulationTimeChange() );
        repaint();
    }

    private Double getDensityValue() {
        ImmutableVector3F modelSensorPosition = getSensorModelPosition();
        double density = model.getDensity( modelSensorPosition.getX(), modelSensorPosition.getY() );
        if ( density < 50 ) {
            // i.e. it hit air. let's see if we can ray-trace to see what terrain it hit
            ImmutableVector3F cameraViewPosition = tab.getCameraPosition();
            ImmutableVector3F viewSamplePosition = getViewSensorPosition();
            Ray3F ray = new Ray3F( cameraViewPosition, viewSamplePosition.minus( cameraViewPosition ).normalized() );
            density = model.rayTraceDensity( ray, modelViewTransform, tab.isWaterVisible() );
        }
        return density;
    }

    public ImmutableVector3F getSensorModelPosition() {
        return PlateModel.convertToPlanar( modelViewTransform.inversePosition( getViewSensorPosition() ) );
    }

    private ImmutableVector3F getViewSensorPosition() {
        return new ImmutableVector3F( draggedPosition.x, draggedPosition.y, 0 );
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
        private final SpeedometerNode miniGauge;
        private Property<Option<Double>> miniGaugeDensity;

        // we use a damped spring system to essentially add inertial to the density needle in the 1-dial case
        private double p = 0;
        private double d = 0;

        private double k = 70; // spring constant
        private double mass = 1; // "mass" for oscillation
        private double c = DampedMassSpringSystem.getCriticallyDampedDamping( mass, k );

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

            miniGaugeDensity = new Property<Option<Double>>( new Some<Double>( (double) 0 ) );
            miniGauge = new SpeedometerNode( "", 100, miniGaugeDensity, MAX_SPEEDOMETER_DENSITY ) {{
                double scale = 0.3;
                setOffset( 50 - 50 * scale, 60 );
                scale( scale );
            }};

            // scale it so that we achieve adherence to the model scale
            scale( ThermometerNode3D.PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / ThermometerNode3D.PIXEL_SCALE );

            horizontalSensorOffset = getFullBounds().getWidth() / 2;

            // give it the "Hand" cursor
            addInputEventListener( new LWJGLCursorHandler() );
        }

        private void clearExtraItems() {
            extraHolderNode.removeAllChildren();
            if ( miniGauge.getParent() != null ) {
                removeChild( miniGauge );
            }
        }

        // support multiple different density meter styles TODO remove once decided
        public void setDensity( double density, double simulationTimeChange ) {
            clearExtraItems();
            if ( PlateTectonicsApplication.useTwoDialDensityMeter.get() ) {
                setTwoDialDensity( density );
            }
            else {
                setWrapAroundDensity( density, simulationTimeChange );
            }
        }

        public void setWrapAroundDensity( double density, double simulationTimeChange ) {

            // use a damped oscillator system here to smoothly move the dial into the correct position
            DampedMassSpringSystem system = new DampedMassSpringSystem( mass, k, c, p - density, d );
            p = system.evaluatePosition( simulationTimeChange ) + density;
            d = system.evaluateVelocity( simulationTimeChange );

            // reference into the speedometer to change it
            pointSensor.value.set( new Option.Some<Double>( p ) );

            // calculate using the speedometer what the angles are at 0 and max
            // speedometer returns angles that are actually the opposite (negative) of what is usually used in the cartesian
            // lane
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

        public void setTwoDialDensity( double density ) {
            // don't add this into the toolbox version
            if ( miniGauge.getParent() == null ) {
                addChild( miniGauge );
            }

            // reference into the speedometer to change it
            pointSensor.value.set( new Option.Some<Double>( density * 4 ) );
            miniGaugeDensity.set( new Some<Double>( density / 4 ) );
        }
    }

    /*---------------------------------------------------------------------------*
    * DampedMassSpringSystem visual testing and verification of position and velocity
    *----------------------------------------------------------------------------*/
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Oscillation Test" );

        double mass = 1;
        double k = 0.001;
        double c = DampedMassSpringSystem.getCriticallyDampedDamping( mass, k ) * 0.7;

        final DampedMassSpringSystem system = new DampedMassSpringSystem( mass, k, c, 100, 10 );

        frame.setContentPane( new PCanvas() {{

            // graph things vs time
            getLayer().addChild( new PNode() {{
                setOffset( 512, 512 );

                // axes
                addChild( new PhetPPath( new Line2D.Double( -512, 0, 512, 0 ), Color.BLACK, new BasicStroke( 1 ), Color.BLACK ) );
                addChild( new PhetPPath( new Line2D.Double( 0, 512, 0, -512 ), Color.BLACK, new BasicStroke( 1 ), Color.BLACK ) );

                // position in black
                for ( int i = -512; i < 512; i++ ) {
                    addChild( new PhetPPath( new Line2D.Double(
                            i, -system.evaluatePosition( i ),
                            i + 1, -system.evaluatePosition( i + 1 )
                    ), Color.BLACK, new BasicStroke( 1 ), Color.BLACK ) );
                }

                // velocity in red
                for ( int i = -512; i < 512; i++ ) {
                    addChild( new PhetPPath( new Line2D.Double(
                            i, -system.evaluateVelocity( i ),
                            i + 1, -system.evaluateVelocity( i + 1 )
                    ), Color.RED, new BasicStroke( 1 ), Color.RED ) );
                }
            }} );
        }} );

        frame.setSize( new Dimension( 1100, 1100 ) );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setVisible( true );
    }
}
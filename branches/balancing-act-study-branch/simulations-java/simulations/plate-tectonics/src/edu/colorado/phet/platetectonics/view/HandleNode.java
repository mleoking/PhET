// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.PlaneF;
import edu.colorado.phet.common.phetcommon.math.Ray3F;
import edu.colorado.phet.common.phetcommon.math.Triangle3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.materials.ColorMaterial;
import edu.colorado.phet.lwjglphet.math.Arrow2F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.ArrowNode;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.model.Handle;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.tabs.PlateMotionTab;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector3F.Y_UNIT;
import static edu.colorado.phet.common.phetcommon.math.vector.Vector3F.Z_UNIT;
import static org.lwjgl.opengl.GL11.*;

/**
 * Displays a draggable ball-and-stick draggable handle (like a shifter in a car) that can go in various directions depending on the selected crust
 * types. This is how the user (in manual mode) selects the movement direction, and how they control speed. It does not show up in automatic mode.
 */
public class HandleNode extends GLNode {

    // what resolution the mesh will be (much coarser and it will look faceted)
    private static int radialColumns = 30;
    private static int rows = 30;
    private Vector3F[] handlePositions;
    private GridMesh handleMesh;
    private final Handle handle;
    private final Property<Vector3F> offset;
    private final PlateMotionTab tab;
    private final LWJGLTransform transform = new LWJGLTransform();  // for rotation of the handle
    private PlateMotionModel model;

    private static final float STICK_RADIUS = 4;
    private static final float STICK_HEIGHT = 40;
    private static final float BALL_RADIUS = 8;
    private final HandleArrowNode backArrow;
    private final HandleArrowNode rightArrow;
    private final HandleArrowNode leftArrow;
    private final HandleArrowNode frontArrow;

    public HandleNode( final Handle handle, final Property<Vector3F> offset, final PlateMotionTab tab ) {
        this.handle = handle;
        this.offset = offset;
        this.tab = tab;
        model = tab.getPlateMotionModel();

        requireEnabled( GL_BLEND );

        // update visibility correctly
        SimpleObserver visibilityObserver = new SimpleObserver() {
            public void update() {
                setVisible( handle.isVisible() );
            }
        };
        tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        tab.isAutoMode.addObserver( visibilityObserver );

        handlePositions = new Vector3F[radialColumns * 2];

        updateLocations();

        handleMesh = new GridMesh( 2, radialColumns, handlePositions ) {
            {
                requireEnabled( GL_LIGHTING );
                requireEnabled( GL_COLOR_MATERIAL );
                requireEnabled( GL_CULL_FACE );
            }

            @Override protected void preRender( GLOptions options ) {
                super.preRender( options );

                glColorMaterial( GL_FRONT, GL_DIFFUSE );
            }
        };
        addChild( handleMesh );

        final Property<MotionType> motionType = tab.getPlateMotionModel().motionType;

        // back arrow
        backArrow = new HandleArrowNode( MotionType.TRANSFORM, Math.PI / 2 ) {
            @Override protected boolean shouldBeVisible() {
                return motionType.get() == MotionType.TRANSFORM && handle.isRightHandle == model.isTransformMotionCCW();
            }
        };
        addChild( backArrow );

        // right arrow
        rightArrow = new HandleArrowNode( handle.getRightMotionType(), 0 ) {
            @Override protected boolean shouldBeVisible() {
                if ( getArrowMotionType() == MotionType.DIVERGENT ) {
                    // divergent handling
                    return ( motionType.get() == null && tab.getPlateMotionModel().allowsDivergentMotion() )
                           || motionType.get() == MotionType.DIVERGENT;
                }
                else {
                    // convergent handling
                    return ( motionType.get() == null && tab.getPlateMotionModel().allowsConvergentMotion() ) || motionType.get() == MotionType.CONVERGENT;
                }
            }
        };
        addChild( rightArrow );

        // left arrow
        leftArrow = new HandleArrowNode( handle.getLeftMotionType(), Math.PI ) {
            @Override protected boolean shouldBeVisible() {
                if ( getArrowMotionType() == MotionType.DIVERGENT ) {
                    // divergent handling
                    return ( motionType.get() == null && tab.getPlateMotionModel().allowsDivergentMotion() ) || motionType.get() == MotionType.DIVERGENT;
                }
                else {
                    // convergent handling
                    return ( motionType.get() == null && tab.getPlateMotionModel().allowsConvergentMotion() ) || motionType.get() == MotionType.CONVERGENT;
                }
            }
        };
        addChild( leftArrow );

        // front arrow
        frontArrow = new HandleArrowNode( MotionType.TRANSFORM, -Math.PI / 2 ) {
            @Override protected boolean shouldBeVisible() {
                return ( motionType.get() == null && tab.getPlateMotionModel().allowsTransformMotion() )
                       || ( motionType.get() == MotionType.TRANSFORM && handle.isRightHandle != model.isTransformMotionCCW() );
            }
        };
        addChild( frontArrow );

        offset.addObserver( new SimpleObserver() {
            public void update() {
                updateLocations();
            }
        } );

        handle.orientation.addObserver( new SimpleObserver() {
            public void update() {
                updateTransform( handle.orientation.get().x, handle.orientation.get().y );
            }
        } );
    }

    public void updateTransform( float xRotation, float zRotation ) {
        transform.set( Matrix4F.rotationZ( -xRotation ).times( Matrix4F.rotationX( zRotation ) ) );
        updateLocations();
    }

    private Vector3F yzHit( Ray3F ray ) {
        return new PlaneF( new Vector3F( 1, 0, 0 ), offset.get().getX() ).intersectWithRay( ray );
    }

    private Vector3F xyHit( Ray3F ray ) {
        return new PlaneF( new Vector3F( 0, 0, 1 ), offset.get().getZ() ).intersectWithRay( ray );
    }

    private Ray3F startRay = null;

    public void startArrowPress( Ray3F ray ) {
        MotionType motionType = rayIntersectsArrow( ray ).get();
        handle.startArrowPress( motionType );
    }

    public void endArrowPress() {
        handle.endArrowPress();
    }

    public void startHandleDrag( Ray3F ray ) {
        startRay = ray;
        handle.startHandleDrag();
    }

    /**
     * Called whenever this handle is dragged by the mouse. The associated ray is a 3D ray from the camera location towards wherever the mouse is
     * currently positioned
     *
     * @param ray 3D ray from camera towards mouse
     */
    public void dragHandle( Ray3F ray ) {

        // if the motion type is not currently initialized, we need to pick a direction of motion (motion type) based on how they are dragging the handle
        if ( model.motionType.get() == null ) {
            boolean success = handle.checkInitialMotion( xyHit( ray ).minus( xyHit( startRay ) ) );

            // bail if it is not significant enough
            if ( !success ) {
                return;
            }
        }

        // should now have a motion type
        assert model.motionType.get() != null;

        boolean horizontal = model.motionType.get() != MotionType.TRANSFORM;

        if ( horizontal ) {
            handle.horizontalHandleDrag( getHorizontalAngle( ray ) );
        }
        else {
            handle.verticalHandleDrag( getVerticalAngle( ray ) );
        }
    }

    private float getHorizontalAngle( Ray3F ray ) {
        Vector3F hit = xyHit( ray );
        Vector3F startHit = xyHit( startRay );
        Vector3F origin = getBase();

        Vector3F hitDir = hit.minus( origin );
        Vector3F startDir = startHit.minus( origin );

        float angle = (float) ( Math.signum( hitDir.x - startDir.x ) * Math.min( 0.8f * Math.PI / 2, Math.acos( hitDir.normalized().dot( startDir.normalized() ) ) ) );

        switch( model.motionType.get() ) {
            case CONVERGENT:
                if ( handle.isRightHandle ) {
                    angle = Math.min( 0, angle );
                }
                else {
                    angle = Math.max( 0, angle );
                }
                break;
            case DIVERGENT:
                if ( handle.isRightHandle ) {
                    angle = Math.max( 0, angle );
                }
                else {
                    angle = Math.min( 0, angle );
                }
                break;
            case TRANSFORM:
                break;
        }

        return angle;
    }

    private float getVerticalAngle( Ray3F ray ) {
        Vector3F hit = yzHit( ray );
        Vector3F startHit = yzHit( startRay );
        Vector3F origin = getBase();

        Vector3F hitDir = hit.minus( origin );
        Vector3F startDir = startHit.minus( origin );

        float angle = (float) ( Math.min( 0.8f * Math.PI / 2, Math.acos( hitDir.normalized().dot( startDir.normalized() ) ) ) );
        if ( hitDir.z < startDir.z ) {
            angle = 0;
        }

        return angle;
    }

    public void endHandleDrag() {
        handle.endHandleDrag();
    }

    public boolean rayIntersectsHandle( Ray3F ray ) {
        // transform it to intersect with a unit sphere at the origin
        Ray3F localRay = new Ray3F( ray.pos.minus( getBallCenter() ).times( 1 / BALL_RADIUS ), ray.dir );

        Vector3F centerToRay = localRay.pos;
        float tmp = localRay.dir.dot( centerToRay );
        float centerToRayDistSq = centerToRay.magnitudeSquared();
        float det = 4 * tmp * tmp - 4 * ( centerToRayDistSq - 1 ); // 1 is radius
        return det >= 0;
    }

    public Option<MotionType> rayIntersectsArrow( Ray3F ray ) {
        for ( HandleArrowNode arrowNode : Arrays.asList( frontArrow, leftArrow, rightArrow, backArrow ) ) {
            if ( arrowNode.isVisible() && rayIntersectsArrow( ray, arrowNode ) ) {
                return new Option.Some<MotionType>( arrowNode.getArrowMotionType() );
            }
        }
        return new Option.None<MotionType>();
    }

    private boolean rayIntersectsArrow( Ray3F ray, ArrowNode arrowNode ) {
        Ray3F transformedRay = arrowNode.getGlobalTransform().inverseRay( ray );
        for ( Triangle3F triangle : arrowNode.getTriangles() ) {
            final Option<Triangle3F.TriangleIntersectionResult> intersection = triangle.intersectWith( transformedRay );
            if ( intersection.isSome() ) {
                return true;
            }
        }
        return false;
    }

    private void updateLocations() {
        for ( int col = 0; col < radialColumns; col++ ) {
            float radialRatio = ( (float) col ) / ( (float) ( radialColumns - 1 ) );
            float sin = (float) ( Math.sin( 2 * Math.PI * radialRatio ) );
            float cos = (float) ( Math.cos( 2 * Math.PI * radialRatio ) );

            handlePositions[col] = convertToRadial( transform.transformPosition( new Vector3F( sin * STICK_RADIUS, STICK_HEIGHT, cos * STICK_RADIUS ) ).plus( offset.get() ) );
            handlePositions[radialColumns + col] = convertToRadial( transform.transformPosition( new Vector3F( sin * STICK_RADIUS, 0, cos * STICK_RADIUS ) ).plus( offset.get() ) );
        }

        if ( handleMesh != null ) {
            handleMesh.updateGeometry( handlePositions );
        }
    }

    private Vector3F convertToRadial( Vector3F planarViewCoordinates ) {
        Vector3F planarModelCoordinates = tab.getModelViewTransform().inversePosition( planarViewCoordinates );
        Vector3F radialModelCoordinates = PlateTectonicsModel.convertToRadial( planarModelCoordinates );
        Vector3F radialViewCoordinates = tab.getModelViewTransform().transformPosition( radialModelCoordinates );
        return radialViewCoordinates;
    }

    // TODO: we are using the rendering order very particularly. find a better way
    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        // red sphere TODO cleanup
        GL11.glPushMatrix();
        Vector3F center = getBallCenter();
        GL11.glTranslatef( center.x, center.y, center.z );
        glEnable( GL_COLOR_MATERIAL );
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        glEnable( GL_CULL_FACE );
        glEnable( GL_LIGHTING );
        GL11.glColor4f( 0.6f, 0, 0, 1 );
        new Sphere().draw( BALL_RADIUS, 25, 25 );
        glDisable( GL_LIGHTING );
        glDisable( GL_DEPTH_TEST );
        GL11.glColor4f( 1, 0, 0, 0.4f );
        new Sphere().draw( BALL_RADIUS, 25, 25 );
        glEnable( GL_DEPTH_TEST );
        glDisable( GL_COLOR_MATERIAL );
        glDisable( GL_CULL_FACE );
        GL11.glPopMatrix();

        // render the back-facing parts
        handleMesh.setMaterial( new ColorMaterial( 1, 1, 1, 0.2f ) );
        glFrontFace( GL_CW );
        handleMesh.render( options );

        // then switch back to normal
        glFrontFace( GL_CCW );
        handleMesh.setMaterial( new ColorMaterial( 1, 1, 1, 0.4f ) );
    }

    private Vector3F getBallCenter() {
        return convertToRadial( transform.transformPosition( new Vector3F( 0, STICK_HEIGHT, 0 ) ).plus( offset.get() ) );
    }

    private Vector3F getBase() {
        return convertToRadial( offset.get() );
    }

    private abstract class HandleArrowNode extends ArrowNode {

        private final MotionType motionType;

        // subclasses handle their visibility
        protected abstract boolean shouldBeVisible();

        private static final float OFFSET = 5;
        private static final float EXTENT = 50;
        private static final float HEAD_HEIGHT = 14;
        private static final float HEAD_WIDTH = 14;
        private static final float TAIL_WIDTH = 8;
        private static final float PADDING_ABOVE = 57;

        public HandleArrowNode( MotionType motionType, double rotation ) {
            super( new Arrow2F( new Vector2F( OFFSET, 0 ),
                                new Vector2F( EXTENT, 0 ), HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH ) );
            this.motionType = motionType;

            // color it based on the motion type
            setFillMaterial( new ColorMaterial( motionType.color ) );

            // black outline
            setStrokeMaterial( new ColorMaterial( 0, 0, 0, 1 ) );

            // move the tail above the handle
            Vector3F tailPlanarPosition = offset.get().plus( Y_UNIT.times( PADDING_ABOVE ) );
            Vector3F tailRadialPosition = convertToRadial( tailPlanarPosition );
            translate( tailRadialPosition.x, tailRadialPosition.y, tailRadialPosition.z );

            // and rotate it so it is pointing in the correct direction
            rotate( Y_UNIT, (float) ( rotation ) );

            // tweak the vertical angle of the arrow so that it matches the local curvature of the earth
            Vector3F tipPlanarPosition = tailPlanarPosition.plus(
                    Matrix4F.rotation( Y_UNIT, (float) rotation ).times( new Vector3F( EXTENT - OFFSET, 0, 0 ) ) );
            Vector3F tipRadialPosition = convertToRadial( tipPlanarPosition );
            Vector3F delta = tipRadialPosition.minus( tailRadialPosition ).normalized();
            float angleChange = (float) Math.atan2( delta.getY(), Math.sqrt( delta.x * delta.x + delta.z * delta.z ) );
            if ( motionType == MotionType.TRANSFORM && rotation < 0 ) {
                angleChange = (float) (-Math.PI / 50);
            }
            rotate( Z_UNIT, angleChange );

            // update visibility when necessary
            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    setVisible( shouldBeVisible() );
                }
            };
            tab.getPlateMotionModel().motionType.addObserver( visibilityObserver );
            tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }

        public MotionType getArrowMotionType() {
            return motionType;
        }
    }
}

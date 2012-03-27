// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.Arrow2F;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.math.PlaneF;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.nodes.ArrowNode;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;
import edu.colorado.phet.platetectonics.model.PlateType;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.util.ColorMaterial;

import static edu.colorado.phet.lwjglphet.math.ImmutableVector3F.Y_UNIT;
import static org.lwjgl.opengl.GL11.*;

public class HandleNode extends GLNode {
    private static int radialColumns = 30;
    private static int rows = 30;
    private ImmutableVector3F[] handlePositions;
    private ImmutableVector3F[] ballPositions;
    private GridMesh handleMesh;
    private GridMesh ballMesh;
    private final Property<ImmutableVector3F> offset;
    private final PlateMotionTab tab;
    private final boolean isRightHandle;
    private final LWJGLTransform transform = new LWJGLTransform();  // for rotation of the handle
    private PlateMotionModel model;

    public HandleNode( final Property<ImmutableVector3F> offset, final PlateMotionTab tab, final boolean isRightHandle ) {
        this.offset = offset;
        this.tab = tab;
        this.isRightHandle = isRightHandle;
        model = tab.getPlateMotionModel();

        requireEnabled( GL_BLEND );

        // update visibility correctly
        SimpleObserver visibilityObserver = new SimpleObserver() {
            public void update() {
                final boolean modesOK = tab.getPlateMotionModel().hasBothPlates.get() && !tab.isAutoMode.get();
                if ( !modesOK ) {
                    setVisible( false );
                    return;
                }
                PlateType myType = ( isRightHandle ? tab.getPlateMotionModel().rightPlateType : tab.getPlateMotionModel().leftPlateType ).get();
                PlateType otherType = ( isRightHandle ? tab.getPlateMotionModel().leftPlateType : tab.getPlateMotionModel().rightPlateType ).get();

                // set visibility based on making sure we are the most dense plate if visible
                if ( ( myType.isContinental() && otherType.isOceanic() )
                     || ( myType == PlateType.YOUNG_OCEANIC && otherType == PlateType.OLD_OCEANIC ) ) {
                    setVisible( false );
                }
                else {
                    setVisible( true );
                }
            }
        };
        tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        tab.isAutoMode.addObserver( visibilityObserver );

        handlePositions = new ImmutableVector3F[radialColumns * 2];
        ballPositions = new ImmutableVector3F[radialColumns * rows];

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

        final ColorMaterial arrowConvergentFill = new ColorMaterial( PlateTectonicsConstants.ARROW_CONVERGENT_FILL );
        final ColorMaterial arrowDivergentFill = new ColorMaterial( PlateTectonicsConstants.ARROW_DIVERGENT_FILL );
        final ColorMaterial arrowTransformFill = new ColorMaterial( PlateTectonicsConstants.ARROW_TRANSFORM_FILL );
        final ColorMaterial arrowStroke = new ColorMaterial( 0, 0, 0, 1 );

        float arrowOffset = 5;
        float arrowExtent = 50;
        float arrowHeadHeight = 14;
        float arrowHeadWidth = 14;
        float arrowTailWidth = 8;

        final float arrowPaddingAbove = 60;

        final Property<MotionType> motionType = tab.getPlateMotionModel().motionType;

        // back arrow
        addChild( new ArrowNode( new Arrow2F( new ImmutableVector2F( arrowOffset, 0 ),
                                              new ImmutableVector2F( arrowExtent, 0 ), arrowHeadHeight, arrowHeadWidth, arrowTailWidth ) ) {{
            setFillMaterial( arrowTransformFill );
            setStrokeMaterial( arrowStroke );
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( arrowPaddingAbove ) );
            ImmutableVector3F viewPosition = convertToRadial( position );
            translate( viewPosition.x, viewPosition.y, viewPosition.z );
            rotate( Y_UNIT, (float) ( Math.PI / 2 ) );

            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
//                    setVisible( ( motionType.get() == null && tab.getPlateMotionModel().allowsTransformMotion() ) || ( motionType.get() == MotionType.TRANSFORM && isRightHandle == model.isTransformMotionCCW() ) );
                    setVisible( motionType.get() == MotionType.TRANSFORM && isRightHandle == model.isTransformMotionCCW() );
                }
            };
            tab.getPlateMotionModel().motionType.addObserver( visibilityObserver );
            tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }} );

        // right arrow
        addChild( new ArrowNode( new Arrow2F( new ImmutableVector2F( arrowOffset, 0 ),
                                              new ImmutableVector2F( arrowExtent, 0 ), arrowHeadHeight, arrowHeadWidth, arrowTailWidth ) ) {{
            final boolean isConvergent = !isRightHandle;
            setFillMaterial( isConvergent ? arrowConvergentFill : arrowDivergentFill );
            setStrokeMaterial( arrowStroke );
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( arrowPaddingAbove ) );
            ImmutableVector3F viewPosition = convertToRadial( position );
            translate( viewPosition.x, viewPosition.y, viewPosition.z );

            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    if ( isConvergent ) {
                        setVisible( ( motionType.get() == null && tab.getPlateMotionModel().allowsConvergentMotion() ) || motionType.get() == MotionType.CONVERGENT );
                    }
                    else {
                        setVisible( ( motionType.get() == null && tab.getPlateMotionModel().allowsDivergentMotion() )
                                    || motionType.get() == MotionType.DIVERGENT );
                    }
                }
            };
            tab.getPlateMotionModel().motionType.addObserver( visibilityObserver );
            tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }} );

        // left arrow
        addChild( new ArrowNode( new Arrow2F( new ImmutableVector2F( -arrowOffset, 0 ),
                                              new ImmutableVector2F( -arrowExtent, 0 ), arrowHeadHeight, arrowHeadWidth, arrowTailWidth ) ) {{
            final boolean isConvergent = isRightHandle;
            setFillMaterial( isConvergent ? arrowConvergentFill : arrowDivergentFill );
            setStrokeMaterial( arrowStroke );
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( arrowPaddingAbove ) );
            ImmutableVector3F viewPosition = convertToRadial( position );
            translate( viewPosition.x, viewPosition.y, viewPosition.z );

            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    if ( isConvergent ) {
                        setVisible( ( motionType.get() == null && tab.getPlateMotionModel().allowsConvergentMotion() ) || motionType.get() == MotionType.CONVERGENT );
                    }
                    else {
                        setVisible( ( motionType.get() == null && tab.getPlateMotionModel().allowsDivergentMotion() )
                                    || motionType.get() == MotionType.DIVERGENT );
                    }
                }
            };
            tab.getPlateMotionModel().motionType.addObserver( visibilityObserver );
            tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }} );

        // front arrow
        addChild( new ArrowNode( new Arrow2F( new ImmutableVector2F( arrowOffset, 0 ),
                                              new ImmutableVector2F( arrowExtent, 0 ), arrowHeadHeight, arrowHeadWidth, arrowTailWidth ) ) {{
            setFillMaterial( arrowTransformFill );
            setStrokeMaterial( arrowStroke );
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( arrowPaddingAbove ) );
            ImmutableVector3F viewPosition = convertToRadial( position );
            translate( viewPosition.x, viewPosition.y, viewPosition.z );
            rotate( Y_UNIT, (float) ( -Math.PI / 2 ) );

            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    setVisible( ( motionType.get() == null && tab.getPlateMotionModel().allowsTransformMotion() ) || ( motionType.get() == MotionType.TRANSFORM && isRightHandle != model.isTransformMotionCCW() ) );
                }
            };
            tab.getPlateMotionModel().motionType.addObserver( visibilityObserver );
            tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }} );

        offset.addObserver( new SimpleObserver() {
            public void update() {
                updateLocations();
            }
        } );
    }

    private float stickRadius = 4;
    private float stickHeight = 40;
    private float ballRadius = 8;

    public void updateTransform( float xRotation, float zRotation ) {
        transform.set( ImmutableMatrix4F.rotationZ( -xRotation ).times( ImmutableMatrix4F.rotationX( zRotation ) ) );
        updateLocations();
    }

    private ImmutableVector3F yzHit( Ray3F ray ) {
        return new PlaneF( new ImmutableVector3F( 1, 0, 0 ), offset.get().getX() ).intersectWithRay( ray );
    }

    private ImmutableVector3F xyHit( Ray3F ray ) {
        return new PlaneF( new ImmutableVector3F( 0, 0, 1 ), offset.get().getZ() ).intersectWithRay( ray );
    }

    private Ray3F startRay = null;

    public void startDrag( Ray3F ray ) {
        startRay = ray;
    }

    public void drag( Ray3F ray ) {
        if ( model.motionType.get() == null ) {
            ImmutableVector3F xyDelta = xyHit( ray ).minus( xyHit( startRay ) );
            if ( xyDelta.magnitude() > 5 ) {
                float rightStrength = xyDelta.dot( ImmutableVector3F.X_UNIT );
                float verticalStrength = Math.abs( xyDelta.dot( Y_UNIT ) );
                if ( model.allowsTransformMotion() && verticalStrength > Math.abs( rightStrength ) ) {
                    // starting transform
                    model.setTransformMotionCCW( !isRightHandle );
                    model.motionType.set( MotionType.TRANSFORM );
                }
                else {
                    boolean pullingLeft = xyDelta.x < 0;
                    if ( model.allowsDivergentMotion() && ( pullingLeft != isRightHandle ) ) {
                        model.motionType.set( MotionType.DIVERGENT );
                    }
                    else if ( model.allowsConvergentMotion() && pullingLeft == isRightHandle ) {
                        model.motionType.set( MotionType.CONVERGENT );
                    }
                    else {
                        return;
                    }
                }
            }
            else {
                return;
            }
        }
        assert model.motionType.get() != null;

        boolean horizontal = model.motionType.get() != MotionType.TRANSFORM;

        if ( horizontal ) {
            ImmutableVector3F hit = xyHit( ray );
            ImmutableVector3F startHit = xyHit( startRay );
            ImmutableVector3F origin = getBase();

            ImmutableVector3F hitDir = hit.minus( origin );
            ImmutableVector3F startDir = startHit.minus( origin );

            float angle = (float) ( Math.signum( hitDir.x - startDir.x ) * Math.min( 0.8f * Math.PI / 2, Math.acos( hitDir.normalized().dot( startDir.normalized() ) ) ) );

            switch( model.motionType.get() ) {
                case CONVERGENT:
                    if ( isRightHandle ) {
                        angle = Math.min( 0, angle );
                    }
                    else {
                        angle = Math.max( 0, angle );
                    }
                    break;
                case DIVERGENT:
                    if ( isRightHandle ) {
                        angle = Math.max( 0, angle );
                    }
                    else {
                        angle = Math.min( 0, angle );
                    }
                    break;
                case TRANSFORM:
                    break;
            }

            tab.motionVectorRight.set( new ImmutableVector2F( isRightHandle ? angle : -angle, 0 ) );

//            updateTransform( angle, 0 );
        }
        else {
            ImmutableVector3F hit = yzHit( ray );
            ImmutableVector3F startHit = yzHit( startRay );
            ImmutableVector3F origin = getBase();

            ImmutableVector3F hitDir = hit.minus( origin );
            ImmutableVector3F startDir = startHit.minus( origin );

            float angle = (float) ( Math.min( 0.8f * Math.PI / 2, Math.acos( hitDir.normalized().dot( startDir.normalized() ) ) ) );
            if ( hitDir.z < startDir.z ) {
                angle = 0;
            }
//            updateTransform( 0, angle );
            tab.motionVectorRight.set( new ImmutableVector2F( 0, isRightHandle ? angle : -angle ) );
        }
    }

    public void endDrag() {
        tab.motionVectorRight.set( new ImmutableVector2F() );
    }

    public boolean intersectRay( Ray3F ray ) {
        // transform it to intersect with a unit sphere at the origin
        Ray3F localRay = new Ray3F( ray.pos.minus( getBallCenter() ).times( 1 / ballRadius ), ray.dir );

        ImmutableVector3F centerToRay = localRay.pos;
        float tmp = localRay.dir.dot( centerToRay );
        float centerToRayDistSq = centerToRay.magnitudeSquared();
        float det = 4 * tmp * tmp - 4 * ( centerToRayDistSq - 1 ); // 1 is radius
        return det >= 0;
    }

    private void updateLocations() {
        for ( int col = 0; col < radialColumns; col++ ) {
            float radialRatio = ( (float) col ) / ( (float) ( radialColumns - 1 ) );
            float sin = (float) ( Math.sin( 2 * Math.PI * radialRatio ) );
            float cos = (float) ( Math.cos( 2 * Math.PI * radialRatio ) );

            handlePositions[col] = convertToRadial( transform.transformPosition( new ImmutableVector3F( sin * stickRadius, stickHeight, cos * stickRadius ) ).plus( offset.get() ) );
            handlePositions[radialColumns + col] = convertToRadial( transform.transformPosition( new ImmutableVector3F( sin * stickRadius, 0, cos * stickRadius ) ).plus( offset.get() ) );
        }

        if ( handleMesh != null ) {
            handleMesh.updateGeometry( handlePositions );
        }
    }

    private ImmutableVector3F convertToRadial( ImmutableVector3F planarViewCoordinates ) {
        ImmutableVector3F planarModelCoordinates = tab.getModelViewTransform().inversePosition( planarViewCoordinates );
        ImmutableVector3F radialModelCoordinates = PlateModel.convertToRadial( planarModelCoordinates );
        ImmutableVector3F radialViewCoordinates = tab.getModelViewTransform().transformPosition( radialModelCoordinates );
        return radialViewCoordinates;
    }

    // TODO: we are using the rendering order very particularly. find a better way
    @Override public void renderSelf( GLOptions options ) {
        // red sphere TODO cleanup
        GL11.glPushMatrix();
        ImmutableVector3F center = getBallCenter();
        GL11.glTranslatef( center.x, center.y, center.z );
        glEnable( GL_COLOR_MATERIAL );
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        glEnable( GL_CULL_FACE );
        glEnable( GL_LIGHTING );
        GL11.glColor4f( 0.6f, 0, 0, 1 );
        new Sphere().draw( ballRadius, 25, 25 );
        glDisable( GL_LIGHTING );
        glDisable( GL_DEPTH_TEST );
        GL11.glColor4f( 1, 0, 0, 0.4f );
        new Sphere().draw( ballRadius, 25, 25 );
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

        super.renderSelf( options );
    }

    private ImmutableVector3F getBallCenter() {
        return convertToRadial( transform.transformPosition( new ImmutableVector3F( 0, stickHeight, 0 ) ).plus( offset.get() ) );
    }

    private ImmutableVector3F getBase() {
        return convertToRadial( offset.get() );
    }
}

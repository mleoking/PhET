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
import edu.colorado.phet.lwjglphet.nodes.ArrowNode;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;
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
    private final LWJGLTransform transform = new LWJGLTransform();

    public HandleNode( final Property<ImmutableVector3F> offset, final PlateMotionTab tab, final boolean isRightHandle ) {
        this.offset = offset;
        this.tab = tab;

        // update visibility correctly
        SimpleObserver visibilityObserver = new SimpleObserver() {
            public void update() {
                setVisible( tab.getPlateMotionModel().hasBothPlates.get() && !tab.isAutoMode.get() );
            }
        };
        tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        tab.isAutoMode.addObserver( visibilityObserver );

        handlePositions = new ImmutableVector3F[radialColumns * 2];
        ballPositions = new ImmutableVector3F[radialColumns * rows];

        updateLocations( 0, 0 );

        handleMesh = new GridMesh( 2, radialColumns, handlePositions ) {
            @Override protected void preRender( GLOptions options ) {
                super.preRender( options );

                glEnable( GL_LIGHTING );
                glEnable( GL_COLOR_MATERIAL );
                glColorMaterial( GL_FRONT, GL_DIFFUSE );
                glEnable( GL_CULL_FACE );
            }

            @Override protected void postRender( GLOptions options ) {
                super.postRender( options );

                glDisable( GL_COLOR_MATERIAL );
                glDisable( GL_LIGHTING );
                glDisable( GL_CULL_FACE );
            }
        };
        addChild( handleMesh );

        // TODO: create ball mesh

        final ColorMaterial arrowConvergentFill = new ColorMaterial( PlateTectonicsConstants.ARROW_CONVERGENT_FILL );
        final ColorMaterial arrowDivergentFill = new ColorMaterial( PlateTectonicsConstants.ARROW_DIVERGENT_FILL );
        final ColorMaterial arrowTransformFill = new ColorMaterial( PlateTectonicsConstants.ARROW_TRANSFORM_FILL );
        final ColorMaterial arrowStroke = new ColorMaterial( 0, 0, 0, 1 );

        // TODO: wrapping better?

        // TODO: refactor to motion types

        float arrowOffset = 5;
        float arrowExtent = 50;
        float arrowHeadHeight = 14;
        float arrowHeadWidth = 14;
        float arrowTailWidth = 8;

        final float arrowPaddingAbove = 80;

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
                    setVisible( ( motionType.get() == null ) || motionType.get() == MotionType.TRANSFORM );
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
                        setVisible( motionType.get() == null || motionType.get() == MotionType.CONVERGENT );
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
                        setVisible( motionType.get() == null || motionType.get() == MotionType.CONVERGENT );
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
                    setVisible( ( motionType.get() == null ) || motionType.get() == MotionType.TRANSFORM );
                }
            };
            tab.getPlateMotionModel().motionType.addObserver( visibilityObserver );
            tab.getPlateMotionModel().hasBothPlates.addObserver( visibilityObserver );
        }} );
    }

    private float stickRadius = 5;
    private float stickHeight = 50;

    public void updateLocations( float xRotation, float zRotation ) {
        transform.set( ImmutableMatrix4F.rotationZ( xRotation ).times( ImmutableMatrix4F.rotationX( zRotation ) ) );

        for ( int col = 0; col < radialColumns; col++ ) {
            float radialRatio = ( (float) col ) / ( (float) ( radialColumns - 1 ) );
            float sin = (float) ( Math.sin( 2 * Math.PI * radialRatio ) );
            float cos = (float) ( Math.cos( 2 * Math.PI * radialRatio ) );

            handlePositions[col] = convertToRadial( transform.transformPosition( new ImmutableVector3F( sin * stickRadius, stickHeight, cos * stickRadius ).plus( offset.get() ) ) );
            handlePositions[radialColumns + col] = convertToRadial( transform.transformPosition( new ImmutableVector3F( sin * stickRadius, 0, cos * stickRadius ).plus( offset.get() ) ) );
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
        ImmutableVector3F center = convertToRadial( transform.transformPosition( new ImmutableVector3F( 0, stickHeight, 0 ).plus( offset.get() ) ) );
        GL11.glTranslatef( center.x, center.y, center.z );
        glEnable( GL_COLOR_MATERIAL );
        glColorMaterial( GL_FRONT, GL_DIFFUSE );
        glEnable( GL_CULL_FACE );
        glEnable( GL_LIGHTING );
        GL11.glColor4f( 0.6f, 0, 0, 1 );
        new Sphere().draw( 10, 50, 50 );
        glDisable( GL_LIGHTING );
        glDisable( GL_DEPTH_TEST );
        GL11.glColor4f( 1, 0, 0, 0.4f );
        new Sphere().draw( 10, 50, 50 );
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

    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        glEnable( GL_BLEND );
    }

    @Override protected void postRender( GLOptions options ) {
        super.postRender( options );

        // TODO: fix blend handling, this disable was screwing other stuff up
//        glDisable( GL_BLEND );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.Arrow2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.ArrowNode;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.GridMesh;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.MotionType;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.util.ColorMaterial;

import static edu.colorado.phet.lwjglphet.math.ImmutableVector3F.Y_UNIT;
import static edu.colorado.phet.lwjglphet.math.ImmutableVector3F.Z_UNIT;
import static org.lwjgl.opengl.GL11.*;

public class HandleNode extends GLNode {
    private static int radialRows = 30;
    private static int radialColumns = 30;
    private static int columns = radialColumns * 2 + 2; // radialColumns for each bend, with one for each base
    private static float smallRadius = 8;
    private static float largeRadius = 15;
    private static float middleLength = 70;
    private static float downLength = 30;
    private ImmutableVector3F[] positions;
    private GridMesh handleMesh;
    private final Property<ImmutableVector3F> offset;
    private final PlateMotionTab tab;

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

        positions = new ImmutableVector3F[radialRows * columns];

        RingPositioner leftBase = new RingPositioner() {
            public ImmutableVector3F position( float sin, float cos ) {
                return new ImmutableVector3F(
                        -middleLength / 2 + cos,
                        -downLength,
                        -sin
                );
            }
        };
        RingPositioner rightBase = new RingPositioner() {
            public ImmutableVector3F position( float sin, float cos ) {
                return new ImmutableVector3F(
                        middleLength / 2 - cos,
                        -downLength,
                        -sin
                );
            }
        };
        int col = 0;
        apply( leftBase, col++ );
        for ( int radialCol = 0; radialCol < radialColumns; radialCol++ ) {
            apply( new CurvePositioner( ( (float) radialCol ) / ( (float) ( radialColumns - 1 ) ), false ), col++ );
        }
        for ( int radialCol = radialColumns - 1; radialCol >= 0; radialCol-- ) {
            apply( new CurvePositioner( ( (float) radialCol ) / ( (float) ( radialColumns - 1 ) ), true ), col++ );
        }
        apply( rightBase, col++ );

        handleMesh = new GridMesh( radialRows, columns, positions ) {
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

        final Property<MotionType> motionType = tab.getPlateMotionModel().motionType;

        // back arrow
        addChild( new ArrowNode( new Arrow2F( new ImmutableVector2F( arrowOffset, 0 ),
                                              new ImmutableVector2F( arrowExtent, 0 ), arrowHeadHeight, arrowHeadWidth, arrowTailWidth ) ) {{
            setFillMaterial( arrowTransformFill );
            setStrokeMaterial( arrowStroke );
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( 50 ) );
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
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( 50 ) );
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
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( 50 ) );
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
            ImmutableVector3F position = offset.get().plus( Y_UNIT.times( 50 ) );
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

    private void apply( RingPositioner positioner, int col ) {
        for ( int row = 0; row < radialRows; row++ ) {
            float radialRatio = ( (float) row ) / ( (float) ( radialRows - 1 ) );
            float sin = (float) ( smallRadius * Math.sin( 2 * Math.PI * radialRatio ) );
            float cos = (float) ( smallRadius * Math.cos( 2 * Math.PI * radialRatio ) );

            // warp the coordinates around the earth's frame
            ImmutableVector3F planarViewCoordinates = positioner.position( sin, cos ).plus( offset.get() );
            positions[row * columns + col] = convertToRadial( planarViewCoordinates );
        }
    }

    private ImmutableVector3F convertToRadial( ImmutableVector3F planarViewCoordinates ) {
        ImmutableVector3F planarModelCoordinates = tab.getModelViewTransform().inversePosition( planarViewCoordinates );
        ImmutableVector3F radialModelCoordinates = PlateModel.convertToRadial( planarModelCoordinates );
        ImmutableVector3F radialViewCoordinates = tab.getModelViewTransform().transformPosition( radialModelCoordinates );
        return radialViewCoordinates;
    }

    private static interface RingPositioner {
        public ImmutableVector3F position( float sin, float cos );
    }

    private static class CurvePositioner implements RingPositioner {
        private final float ratio;
        private final boolean flip;

        private CurvePositioner( float ratio, boolean flip ) {
            this.ratio = ratio;
            this.flip = flip;
        }

        public ImmutableVector3F position( float sin, float cos ) {
            ImmutableVector3F direction = new ImmutableVector3F(
                    (float) -Math.cos( ratio * Math.PI / 2 ),
                    (float) Math.sin( ratio * Math.PI / 2 ),
                    0
            );
            ImmutableVector3F center = direction.times( largeRadius ).plus( new ImmutableVector3F(
                    -middleLength / 2 + largeRadius,
                    0,
                    0
            ) );

            // use the direction and Z_UNIT to span the plane our circle is in
            return center.plus( direction.times( -cos ) ).plus( Z_UNIT.times( -sin ) )

                    // flip the X component if "flip" is true
                    .componentTimes( new ImmutableVector3F( flip ? -1 : 1, 1, 1 ) );
        }
    }

    // TODO: we are using the rendering order very particularly. find a better way
    @Override public void renderSelf( GLOptions options ) {
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

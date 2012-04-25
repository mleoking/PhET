// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Cursor;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.PlanarPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

/**
 * Displays a ruler in the 3D play area space
 */
public class RulerNode3D extends PlanarPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    private static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 4;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    private static final float RULER_PIXEL_SCALE = 3f;
    private final LWJGLTransform modelViewTransform;
    private final PlateTectonicsTab tab;

    private int zoomMultiplier = 1;
    public ImmutableVector2F draggedPosition = new ImmutableVector2F();

    public RulerNode3D( final LWJGLTransform modelViewTransform, final PlateTectonicsTab tab ) {
        super( new RulerNode2D( modelViewTransform.transformDeltaX( (float) 1000 ), tab ) {{
            scale( scaleMultiplier( tab ) );
        }} );
        this.modelViewTransform = modelViewTransform;
        this.tab = tab;

        tab.zoomRatio.addObserver( new SimpleObserver() {
            public void update() {
                int newZoomMultiplier = getScale();
                if ( newZoomMultiplier != zoomMultiplier ) {
                    zoomMultiplier = newZoomMultiplier;
                    final RulerNode2D ruler = (RulerNode2D) getNode();
                    ruler.setMajorTickLabels( getLabels( scaleMultiplier( tab ) * zoomMultiplier ) );
//                repaint();

                    // TODO: ideally, only repaint() here. why wasn't that working?
                    rebuildComponentImage();
                }

                final ImmutableMatrix4F scaling = ImmutableMatrix4F.scaling( zoomMultiplier / PICCOLO_PIXELS_TO_VIEW_UNIT );
                final ImmutableMatrix4F translation = ImmutableMatrix4F.translation( draggedPosition.x,
                                                                                     draggedPosition.y,
                                                                                     0 );

                // subtract height first, so we scale from the ruler TOP, and move from there
                transform.set( translation.times( scaling ).times( ImmutableMatrix4F.translation( 0, -(float) getNode().getFullBounds().getHeight(), 0 ) ) );
            }
        } );

        // scale the node to handle the subsampling
//        scale( getScale() );
        //scale( ( ( tab instanceof PlateMotionTab ) ? 4 : 1 ) / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public int getScale() {
        int result = 1;
        float cutoff = 1.3f;
        float tmp = tab.getSceneDistanceZoomFactor();

        // attempt to find a perfect scaling factor that splits on "even" decimal numbers
        // each iteration increases by 10x
        while ( tmp > cutoff ) {
            // 2x
            tmp /= 2;
            result *= 2;
            if ( tmp < cutoff ) { break; }
            // 5x
            tmp /= 5.0 / 2;
            result *= 5.0 / 2;
            if ( tmp < cutoff ) { break; }
            // 10x
            tmp /= 2;
            result *= 2;
            cutoff = 1 + ( cutoff - 1 ) * ( cutoff - 1 );
        }
        return result;
    }

    public boolean allowsDrag( ImmutableVector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( ImmutableVector2F delta ) {
        transform.prepend( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );
        draggedPosition = draggedPosition.plus( delta );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.rulerInToolbox;
    }

    public ImmutableVector2F getInitialMouseOffset() {
        return new ImmutableVector2F( 10, -10 );
    }

    public IUserComponent getUserComponent() {
        return UserComponents.ruler;
    }

    // bottom-left corner of the ruler
    public ImmutableVector3F getSensorModelPosition() {
        return modelViewTransform.inversePosition( new ImmutableVector3F( draggedPosition.x, draggedPosition.y, 0 ) );
    }

    public ParameterSet getCustomParameters() {
        // no extra parameters needed for this
        return new ParameterSet();
    }

    public void recycle() {
        getParent().removeChild( this );
    }

    public static class RulerNode2D extends RulerNode {

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public RulerNode2D( float kmToViewUnit, final PlateTectonicsTab tab ) {
            super( 100 * RulerNode3D.RULER_PIXEL_SCALE, 10 * RulerNode3D.RULER_PIXEL_SCALE, getLabels( scaleMultiplier( tab ) ),
                   Strings.RULER_UNITS, 1, 9 );

            // make it vertical
            rotate( -Math.PI / 2 );

            // scale it so that we achieve adherence to the model scale
            scale( PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / RULER_PIXEL_SCALE );

            // don't show things below the "0" mark
            setInsetWidth( 0 );

            // give it the "Hand" cursor
            addInputEventListener( new LWJGLCursorHandler() );
        }

        @Override public void setMajorTickLabels( String[] majorTickLabels ) {
            super.setMajorTickLabels( majorTickLabels );
            repaint();
        }
    }

    private static String[] getLabels( final int multiplier ) {
        List<String> labels = FunctionalUtils.map( FunctionalUtils.rangeInclusive( 0, 10 ), new Function1<Integer, String>() {
            public String apply( Integer integer ) {
                return Integer.toString( integer * 10 * multiplier );
            }
        } );
        Collections.reverse( labels );
        String[] result = new String[labels.size()];
        return labels.toArray( result );
    }

    private static int scaleMultiplier( PlateTectonicsTab tab ) {
        return ( tab instanceof PlateMotionTab ) ? 4 : 1;
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Cursor;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
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

    public RulerNode3D( final LWJGLTransform modelViewTransform, final PlateTectonicsTab tab ) {
        super( new RulerNode2D( modelViewTransform.transformDeltaX( (float) 1000 ), tab ) {{
            scale( scaleMultiplier( tab ) );
        }} );
        this.modelViewTransform = modelViewTransform;

        // scale the node to handle the subsampling
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );
        //scale( ( ( tab instanceof PlateMotionTab ) ? 4 : 1 ) / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public boolean allowsDrag( ImmutableVector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( ImmutableVector2F delta ) {
        transform.prepend( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.rulerInToolbox;
    }

    public ImmutableVector2F getInitialMouseOffset() {
        return new ImmutableVector2F( 10, 190 );
    }

    public IUserComponent getUserComponent() {
        return UserComponents.ruler;
    }

    // bottom-left corner of the ruler
    public ImmutableVector3F getSensorModelPosition() {
        return modelViewTransform.inversePosition( transform.getMatrix().getTranslation() );
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
            super( 100 * RulerNode3D.RULER_PIXEL_SCALE, 10 * RulerNode3D.RULER_PIXEL_SCALE, getLabels( tab ),
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
    }

    private static String[] getLabels( final PlateTectonicsTab tab ) {
        List<String> labels = FunctionalUtils.map( FunctionalUtils.rangeInclusive( 0, 10 ), new Function1<Integer, String>() {
            public String apply( Integer integer ) {
                return Integer.toString( integer * 10 * scaleMultiplier( tab ) );
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

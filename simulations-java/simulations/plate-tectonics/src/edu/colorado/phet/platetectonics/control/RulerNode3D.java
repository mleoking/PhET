// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Cursor;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.LWJGLModelViewTransform;

/**
 * Displays a ruler in the 3D play area space
 */
public class RulerNode3D extends OrthoPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    private static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    private static final float RULER_PIXEL_SCALE = 3f;

    public RulerNode3D( final LWJGLModelViewTransform modelViewTransform, final PlateTectonicsTab tab ) {
        super( new RulerNode2D( modelViewTransform.modelToViewDeltaX( 1000 ) ), tab, tab.getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), tab.mouseEventNotifier );

        // scale the node to handle the subsampling
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // allow antialiasing for a cleaner look
        setAntialiased( true );

        // don't forward mouse events!
//        ignoreInput();

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public boolean allowsDrag( ImmutableVector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( ImmutableVector2F delta ) {
        transform.append( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.rulerInToolbox;
    }

    public ImmutableVector2F getInitialMouseOffset() {
        return new ImmutableVector2F( 10, 10 );
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
        public RulerNode2D( float kmToViewUnit ) {
            // TODO: i18n
            super( 100 * RulerNode3D.RULER_PIXEL_SCALE, 10 * RulerNode3D.RULER_PIXEL_SCALE, new String[] { "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100" }, "km", 1, 9 );

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
}

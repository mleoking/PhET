// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Cursor;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.jmephet.JMECursorHandler;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.jmephet.hud.SwingJMENode;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.util.JMEModelViewTransform;
import edu.umd.cs.piccolo.util.PDimension;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;

/**
 * Displays a ruler in the 3D play area space
 */
public class ThermometerNode3D extends PiccoloJMENode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    private static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    private static final float RULER_PIXEL_SCALE = 3f;

    public ThermometerNode3D( final JMEModelViewTransform transform, final JMEModule module ) {
        super( new ThermometerNode2D( transform.modelToViewDeltaX( 1000 ) ), module.getInputHandler(), module, SwingJMENode.getDefaultTransform() );

        // scale the node to handle the subsampling
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // allow antialiasing for a cleaner look
        antialiased.set( true );

        // allow parts to see through
        setQueueBucket( Bucket.Transparent );

        // don't forward mouse events!
        ignoreInput();

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public boolean allowsDrag( Vector2f initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( Vector2f delta ) {
        setLocalTranslation( getLocalTranslation().add( new Vector3f( delta.x, delta.y, 0 ) ) );
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.rulerInToolbox;
    }

    public Vector2f getInitialMouseOffset() {
        return new Vector2f( 10, 10 );
    }

    public void recycle() {
        getParent().detachChild( this );
    }

    public static class ThermometerNode2D extends LiquidExpansionThermometerNode {

        /**
         * @param kmToViewUnit Number of view units (in 3D JME) that correspond to 1 km in the model. Extracted into
         *                     a parameter so that we can add a 2D version to the toolbox that is unaffected by future
         *                     model-view-transform size changes.
         */
        public ThermometerNode2D( float kmToViewUnit ) {
            super( new PDimension( 50 * 0.8, 150 * 0.8 ) );

            // scale it so that we achieve adherence to the model scale
            scale( PICCOLO_PIXELS_TO_VIEW_UNIT * kmToViewUnit / RULER_PIXEL_SCALE );

            // give it the "Hand" cursor
            addInputEventListener( new JMECursorHandler() );
        }
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.awt.Cursor;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.lwjglphet.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.LWJGLModelViewTransform;

/**
 * Displays a ruler in the 3D play area space
 */
public class ThermometerNode3D extends OrthoPiccoloNode implements DraggableTool2D {

    // how much we subsample the piccolo ruler in texture construction
    public static final float PICCOLO_PIXELS_TO_VIEW_UNIT = 3;

    // how much larger should the ruler construction values be to get a good look? we scale by the inverse to remain the correct size
    public static final float PIXEL_SCALE = 3f;

    private final LWJGLModelViewTransform modelViewTransform;
    private final PlateModel model;

    public ThermometerNode3D( final LWJGLModelViewTransform modelViewTransform, final PlateTectonicsTab tab, PlateModel model ) {

        //TODO: rewrite with composition instead of inheritance
        super( new ThermometerNode2D( modelViewTransform.modelToViewDeltaX( 1000 ) ), tab, tab.getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), tab.mouseEventNotifier );
        this.modelViewTransform = modelViewTransform;
        this.model = model;

        // scale the node to handle the subsampling
        scale( 1 / PICCOLO_PIXELS_TO_VIEW_UNIT );

        // allow antialiasing for a cleaner look
        setAntialiased( true );

        // don't forward mouse events!
//        ignoreInput();

        // since we are using the node in the main scene, mouse events don't get passed in, and we need to set our cursor manually
        getCanvas().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );

        model.modelChanged.addUpdateListener( new UpdateListener() {
                                                  public void update() {
                                                      updateLiquidHeight();
                                                  }
                                              }, true );
    }

    public boolean allowsDrag( ImmutableVector2F initialPosition ) {
        return true; // if this node is picked, always allow a drag anywhere on it
    }

    public void dragDelta( ImmutableVector2F delta ) {
        this.transform.append( ImmutableMatrix4F.translation( delta.x, delta.y, 0 ) );

        updateLiquidHeight();
    }

    private void updateLiquidHeight() {
        // get model coordinates
        ImmutableVector3F modelSensorPosition = modelViewTransform.viewToModel( transform.getMatrix().getTranslation() );
        final Double temp = model.getTemperature( modelSensorPosition.getX(), modelSensorPosition.getY() );
        double liquidHeight = new Function.LinearFunction( 290, 2000, 0.2, 0.8 ).evaluate( temp );
//        System.out.println( "liquidHeight = " + liquidHeight );
        ( (LiquidExpansionThermometerNode) getNode() ).setLiquidHeight( liquidHeight );
        repaint();
    }

    public Property<Boolean> getInsideToolboxProperty( ToolboxState toolboxState ) {
        return toolboxState.thermometerInToolbox;
    }

    public ImmutableVector2F getInitialMouseOffset() {
        return new ImmutableVector2F( 10, 10 );
    }

    public void recycle() {
        getParent().removeChild( this );
    }

}

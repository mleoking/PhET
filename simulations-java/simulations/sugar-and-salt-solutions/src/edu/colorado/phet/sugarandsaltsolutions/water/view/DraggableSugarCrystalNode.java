// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.sugarandsaltsolutions.water.model.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Draggable graphic for sugar that appears in the salt bucket and can be dragged and dropped into the play area.
 * TODO: duplicate code with DraggableSaltCrystalNode
 *
 * @author Sam Reid
 */
public class DraggableSugarCrystalNode extends PNode {
    public DraggableSugarCrystalNode( final WaterModel waterModel, final ModelViewTransform transform,
                                      //Region where dropping the crystal is allowed, in the particle box
                                      final PNode target, ObservableProperty<Boolean> showSugarAtoms ) {
        //Ask the model to create a salt crystal so it will have the correct dimensions and will work with our graphics classes
        ArrayList<Sucrose> saltCrystal = waterModel.createSugarCrystal( new Point() );

        //Disable collisions between salt crystal and waters while user is dragging it.  Couldn't get collision filtering to work, so this is our workaround
        for ( Sucrose sucrose : saltCrystal ) {
            waterModel.unhook( sucrose );
        }

        //Add graphics for the sucrose molecules in the crystal
        for ( Sucrose sucrose : saltCrystal ) {
            addChild( new MultiSucroseNode( transform, sucrose, new VoidFunction1<VoidFunction0>() {
                public void apply( VoidFunction0 voidFunction0 ) {
                    voidFunction0.apply();
                }
            }, showSugarAtoms ) );
        }

        //Overrides the cursor handler in DefaultParticleNode instances
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                translate( event.getDeltaRelativeTo( getParent() ).getWidth() / getScale(), event.getDeltaRelativeTo( getParent() ).getHeight() / getScale() );
            }

            //When the user drops the crystal in the particle box, remove the dragged salt node and add salt directly to the model, which will create a new graphic
            @Override public void mouseReleased( PInputEvent event ) {
                if ( target.getGlobalFullBounds().contains( DraggableSugarCrystalNode.this.getGlobalFullBounds().getCenter2D() ) ) {
                    Point2D point = DraggableSugarCrystalNode.this.getGlobalFullBounds().getCenter2D();
                    point = target.globalToLocal( point );
                    final Point2D model = transform.viewToModel( point );

                    waterModel.addSugar( model );

                    getParent().removeChild( DraggableSugarCrystalNode.this );
                }
            }
        } );
        scale( 0.45 );
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1.Null;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel.SaltCrystal;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Draggable graphic for salt that appears in the salt bucket and can be dragged and dropped into the play area.
 *
 * @author Sam Reid
 */
public class DraggableSaltCrystalNode extends PNode {
    public DraggableSaltCrystalNode( final WaterModel waterModel, final ModelViewTransform transform,
                                     //Region where dropping the crystal is allowed, in the particle box
                                     final PNode target ) {
        //Ask the model to create a salt crystal so it will have the correct dimensions and will work with our graphics classes
        SaltCrystal saltCrystal = waterModel.newSaltCrystal( new Point( 0, 0 ) );

        //Disable collisions between salt crystal and waters while user is dragging it.  Couldn't get collision filtering to work, so this is our workaround
        waterModel.unhook( saltCrystal );

        //Add graphics for the Na+ and Cl- particles
        addChild( new DefaultParticleNode( transform, saltCrystal.sodium, new Null<VoidFunction0>(), S3Element.NaIon ) );
        addChild( new DefaultParticleNode( transform, saltCrystal.sodium2, new Null<VoidFunction0>(), S3Element.NaIon ) );
        addChild( new DefaultParticleNode( transform, saltCrystal.chloride, new Null<VoidFunction0>(), S3Element.ClIon ) );
        addChild( new DefaultParticleNode( transform, saltCrystal.chloride2, new Null<VoidFunction0>(), S3Element.ClIon ) );

        //Overrides the cursor handler in DefaultParticleNode instances
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                translate( event.getDeltaRelativeTo( getParent() ).getWidth() / getScale(), event.getDeltaRelativeTo( getParent() ).getHeight() / getScale() );
            }

            //When the user drops the crystal in the particle box, remove the dragged salt node and add salt directly to the model, which will create a new graphic
            @Override public void mouseReleased( PInputEvent event ) {
                if ( target.getGlobalFullBounds().contains( DraggableSaltCrystalNode.this.getGlobalFullBounds().getCenter2D() ) ) {
                    Point2D point = DraggableSaltCrystalNode.this.getGlobalFullBounds().getCenter2D();
                    point = target.globalToLocal( point );
                    final Point2D model = transform.viewToModel( point );

                    waterModel.addSalt( model );
                    getParent().removeChild( DraggableSaltCrystalNode.this );
                }
            }
        } );
        scale( 0.8 );
    }
}
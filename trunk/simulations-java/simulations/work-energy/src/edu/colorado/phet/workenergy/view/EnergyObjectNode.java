package edu.colorado.phet.workenergy.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class EnergyObjectNode extends WorkEnergyObjectNode {

    public EnergyObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform, Property<Boolean> originLineVisible ) {
        super( workEnergyObject, transform, originLineVisible );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                Point2D modelDelta = transform.viewToModelDifferential( event.getDeltaRelativeTo( EnergyObjectNode.this ) );
                workEnergyObject.translate( modelDelta.getX(), modelDelta.getY() );
            }
        } );
    }
}

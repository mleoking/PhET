// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.workenergy.model.WorkEnergyObject;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class WorkObjectNode extends WorkEnergyObjectNode {
    public WorkObjectNode( final WorkEnergyObject workEnergyObject, final ModelViewTransform2D transform, Property<Boolean> originLineVisible ) {
        super( workEnergyObject, transform, originLineVisible );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                final PDimension delta = event.getDeltaRelativeTo( WorkObjectNode.this );
//                System.out.println("delta = " + delta);
                if ( delta.width > 0 ) {
                    workEnergyObject.setAppliedForce( 1, 0 );
                }
                else if ( delta.width < 0 ) {
                    workEnergyObject.setAppliedForce( -1, 0 );
                }
            }

            @Override
            public void mouseReleased( PInputEvent event ) {
                workEnergyObject.setAppliedForce( 0, 0 );
            }
        } );
    }
}

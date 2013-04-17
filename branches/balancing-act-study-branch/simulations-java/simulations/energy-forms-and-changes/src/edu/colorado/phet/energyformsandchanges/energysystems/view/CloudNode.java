// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Cloud;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a cloud in the view.
 *
 * @author John Blanco
 */
public class CloudNode extends PNode {

    public CloudNode( Cloud cloud, final ModelViewTransform mvt ) {
        addChild( new ModelElementImageNode( Cloud.CLOUD_IMAGE, mvt ) );
        setOffset( mvt.modelToViewDeltaX( cloud.offsetFromParent.getX() ),
                   mvt.modelToViewDeltaY( cloud.offsetFromParent.getY() ) );
        cloud.existenceStrength.addObserver( new VoidFunction1<Double>() {
            public void apply( Double existenceStrength ) {
                setTransparency( existenceStrength.floatValue() );
            }
        } );
    }
}
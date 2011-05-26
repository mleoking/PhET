// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.torque.teetertotter.model.ModelObject;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for displaying and interacting with any model object in the torque sim.
 * Uses the shape of the object, which will change as the object moves.
 *
 * @author Sam Reid
 */
public class ModelObjectNode extends PNode {
    public ModelObjectNode( final ModelViewTransform mvt, final ModelObject modelObject, Paint paint ) {
        addChild( new PhetPPath( paint, new BasicStroke( 1 ), Color.BLACK ) {{
            modelObject.shape.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }} );
    }
}

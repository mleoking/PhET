// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.torque.teetertotter.model.weights.Weight;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WeightNode extends PNode {
    public WeightNode( ModelViewTransform mvt, Weight weight ) {
        addChild( new PhetPPath( mvt.modelToView( weight.getShape() ), Color.RED, new BasicStroke( 2 ), Color.BLACK ) );
    }
}

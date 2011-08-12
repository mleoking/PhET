// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author John Blanco
 */
public class BrickStackNode extends ShapeMassNode {
    public BrickStackNode( final ShapeMass mass, final ModelViewTransform mvt, PhetPCanvas canvas, BooleanProperty massLabelVisibleProperty ) {
        super( mass, mvt, new Color( 205, 38, 38 ), canvas, massLabelVisibleProperty );
    }
}

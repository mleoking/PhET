/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class SchematicCapacitorGraphic extends SchematicPlatedGraphic {
    public SchematicCapacitorGraphic( Component parent, CircuitComponent component, ModelViewTransform2D transform, double wireThickness ) {
        super( parent, component, transform, wireThickness, 0.325, 3, 3 );
    }
}

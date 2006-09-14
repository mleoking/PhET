/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.model.components.Resistor;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 12:57:37 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public class ResistorGraphic extends CircuitComponentImageGraphic {
    private ColorBandsGraphic colorBandsGraphic;

    public ResistorGraphic( BufferedImage image, Component parent, Resistor component, ModelViewTransform2D transform ) {
        super( image, parent, component, transform );
        colorBandsGraphic = new ColorBandsGraphic( true, this, component );
    }

    public void paint( Graphics2D graphics2D ) {
        super.paint( graphics2D );
        colorBandsGraphic.paint( graphics2D );
    }
}

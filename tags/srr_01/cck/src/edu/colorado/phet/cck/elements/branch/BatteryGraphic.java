/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.CCK2Module;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 30, 2003
 * Time: 1:19:48 AM
 * Copyright (c) Oct 30, 2003 by Sam Reid
 */
public class BatteryGraphic extends ConnectibleImageGraphic {
    public BatteryGraphic(final Circuit circuit, ModelViewTransform2d transform, final Branch branch, final CCK2Module module, BufferedImage image, Stroke highlightStroke, Color highlightColor) {
        super(circuit, transform, branch, module, image, highlightStroke, highlightColor);
    }
}

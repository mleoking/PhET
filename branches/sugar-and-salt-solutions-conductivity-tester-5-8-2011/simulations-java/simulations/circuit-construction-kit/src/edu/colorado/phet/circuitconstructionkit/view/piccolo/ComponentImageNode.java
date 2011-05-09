// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 */

public abstract class ComponentImageNode extends RectangularComponentNode {
    private PImage pImage;

    public ComponentImageNode(final CCKModel model, final CircuitComponent circuitComponent, BufferedImage image, JComponent component, CCKModule module) {
        super(model, circuitComponent, image.getWidth(), image.getHeight(), component, module);
        pImage = new PImage(image);
        addChild(pImage);
        update();
    }

}

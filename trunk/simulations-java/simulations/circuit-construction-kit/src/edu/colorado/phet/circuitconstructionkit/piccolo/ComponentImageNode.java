package edu.colorado.phet.circuitconstructionkit.piccolo;

import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.ICCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 */

public abstract class ComponentImageNode extends RectangularComponentNode {
    private PImage pImage;

    public ComponentImageNode( final CCKModel model, final CircuitComponent circuitComponent, BufferedImage image, JComponent component, ICCKModule module ) {
        super( model, circuitComponent, image.getWidth(), image.getHeight(), component, module );
        pImage = new PImage( image );
        addChild( pImage );
        update();
    }

}

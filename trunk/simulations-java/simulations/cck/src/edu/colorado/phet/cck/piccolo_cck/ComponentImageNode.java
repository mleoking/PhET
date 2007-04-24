package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 *
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

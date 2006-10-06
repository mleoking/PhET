package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.CircuitComponent;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 17, 2006
 * Time: 7:49:22 PM
 * Copyright (c) Sep 17, 2006 by Sam Reid
 */

public abstract class ComponentImageNode extends RectangularComponentNode {
    private PImage pImage;

    public ComponentImageNode( final CCKModel model, final CircuitComponent circuitComponent, BufferedImage image, JComponent component, ICCKModule module ) {
        super( model, circuitComponent, image.getWidth(), image.getHeight(), component, module );
        pImage = new PImage( image );
        addChild( pImage );
        getBranch().addFlameListener( new Branch.FlameListener() {
            public void flameStarted() {
                update();
            }

            public void flameFinished() {
                update();
            }
        } );
        update();
    }

}

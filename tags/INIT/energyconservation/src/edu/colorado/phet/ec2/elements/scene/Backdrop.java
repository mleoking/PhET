/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.scene;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.RescaleOp;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.ec2.EC2Module;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jul 14, 2003
 * Time: 5:58:23 PM
 * Copyright (c) Jul 14, 2003 by Sam Reid
 */
public class Backdrop implements Graphic {
    BufferedImage image;
    BufferedImage rescaled;
    EC2Module module;
    private boolean visible = true;

    public Backdrop( EC2Module module, BufferedImage image ) {
        this.module = module;
        this.image = image;
        module.getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d modelViewTransform2d ) {
                viewChanged();
            }
        } );
    }

    private void viewChanged() {
        Rectangle out = module.getTransform().getViewBounds();
        rescaled = RescaleOp.rescale( Backdrop.this.image, out.width, out.height );
    }

    public void paint( Graphics2D g ) {
        if( !visible ) {
//            g.setBackground(Color.yellow);
            return;
        }
        if( rescaled != null ) {
            g.drawImage( rescaled, 0, 0, module.getApparatusPanel() );
        }
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public void setBufferedImage( BufferedImage background, edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d transform ) {
        this.image = background;
        viewChanged();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}

/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.buffering;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 6:20:30 AM
 * Copyright (c) Sep 25, 2003 by Sam Reid
 */
public class ComponentBufferSource implements BufferSource {
    Component component;

    public ComponentBufferSource( Component component ) {
        this.component = component;
    }

    public BufferedImage getBuffer( int width, int height ) {
        return (BufferedImage)component.createImage( width, height );
    }
}

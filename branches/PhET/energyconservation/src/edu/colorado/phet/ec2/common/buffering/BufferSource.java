/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.buffering;

import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 6:20:01 AM
 * Copyright (c) Sep 25, 2003 by Sam Reid
 */
public interface BufferSource {
    BufferedImage getBuffer( int width, int height );
}

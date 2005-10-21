/**
 * Class: StaticImageGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: Jun 16, 2003
 */
package edu.colorado.phet.common.view.graphics;


import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StaticImageGraphic implements Graphic {

    BufferedImage image;
    private int x;
    private int y;

    public StaticImageGraphic( String imageName, int x, int y ) {
        this( new ImageLoader().loadBufferedImage(imageName), x, y );
    }

    public StaticImageGraphic( BufferedImage image, int x, int y ) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    public void paint( Graphics2D g ) {
        g.drawImage( image, x, y, null );
    }
}

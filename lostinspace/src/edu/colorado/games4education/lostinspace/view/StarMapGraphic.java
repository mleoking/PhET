/**
 * Class: StarMapGraphic
 * Class: edu.colorado.games4education.lostinspace.view
 * User: Ron LeMaster
 * Date: Mar 17, 2004
 * Time: 8:52:52 PM
 */
package edu.colorado.games4education.lostinspace.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

public class StarMapGraphic implements Graphic, ImageObserver {

    private BufferedImage mapImage;
    private AffineTransform mapTx = new AffineTransform();

    public StarMapGraphic() {
        ImageLoader imageLoader = new ImageLoader();
        try {
            mapImage = imageLoader.loadImage( "images/star-chart.gif" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void paint( Graphics2D g ) {
        g.drawImage( mapImage, mapTx, this );
    }

    public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height ) {
        return false;
    }
}

/** Sam Reid*/
package edu.colorado.phet.cck3.grabbag;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:07:23 PM
 * Copyright (c) Sep 14, 2004 by Sam Reid
 */
public class GrabBagItem {
    URL imageURL;
    String name;
    double resistance;
    private BufferedImage image;
    double modelLength;

    public GrabBagItem( URL imageURL, String name, double resistance, double modelLength ) {
        this.modelLength = modelLength;
        if( imageURL == null ) {
            throw new RuntimeException( "Null image URL for name=" + name );
        }
        this.imageURL = imageURL;
        this.name = name;
        this.resistance = resistance;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public double getResistance() {
        return resistance;
    }

    public BufferedImage getImage() {
        if( image == null ) {
            try {
                image = ImageLoader.loadBufferedImage( imageURL );
            }
            catch( IOException e ) {
                System.out.println( "this = " + this );
                e.printStackTrace();
            }
        }
        return image;
    }

    public String toString() {
        return "name=" + name + ", imageURL=" + imageURL + ", resistance=" + resistance;
    }

    public Resistor createBranch( CCK3Module module ) {
        Point2D start = new Point2D.Double( 5, 5 );
        Vector2D.Double dir = new Vector2D.Double( 1, 0 );
        double height = modelLength / image.getWidth() * image.getHeight();
        GrabBagResistor res = new GrabBagResistor( start, dir, modelLength, height, module.getKirkhoffListener() );
        res.setResistance( getResistance() );
        return res;
    }
}

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
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:07:23 PM
 * Copyright (c) Sep 14, 2004 by Sam Reid
 */
public class GrabBagItem {
    private URL imageURL;
    private String name;
    private double resistance;
    private BufferedImage image;
    private double modelLength;

    public GrabBagItem( String imageURLString, String name, double resistance, double modelLength ) {
        this.imageURL = GrabBagItem.class.getClassLoader().getResource( imageURLString );
        this.modelLength = modelLength;
        if( imageURL == null ) {
            throw new RuntimeException( "Null image URL for name=" + name + ", imageURLString=" + imageURLString );
        }

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

    private static final Random random = new Random();

    public Resistor createBranch( CCK3Module module ) {
        double x = random.nextDouble() * 5 + 2.5;
        double y = random.nextDouble() * 5 + 2.5;
        Point2D start = new Point2D.Double( x, y );
        Vector2D.Double dir = new Vector2D.Double( 1, 0 );
        double height = modelLength / image.getWidth() * image.getHeight();
        GrabBagResistor res = new GrabBagResistor( start, dir, modelLength, height, module.getKirkhoffListener() );
        res.setResistance( getResistance() );
        return res;
    }
}

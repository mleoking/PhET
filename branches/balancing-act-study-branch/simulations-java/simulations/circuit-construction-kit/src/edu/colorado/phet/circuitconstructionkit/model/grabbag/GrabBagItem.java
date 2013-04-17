// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.grabbag;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Sep 14, 2004
 * Time: 8:07:23 PM
 */
public class GrabBagItem {
    private URL imageURL;
    private String name;
    private double resistance;
    private BufferedImage image;
    private double modelLength;

    public GrabBagItem( String imageURLString, String name, double resistance, double modelLength ) {
        this.imageURL = GrabBagItem.class.getClassLoader().getResource( "circuit-construction-kit/images/grabbag/" + imageURLString );
        this.modelLength = modelLength;
        if ( imageURL == null ) {
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
        if ( image == null ) {
            try {
                image = ImageLoader.loadBufferedImage( imageURL );
            }
            catch ( IOException e ) {
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

    public Resistor createBranch( CCKModule module ) {
        Rectangle2D mb = module.getModelBounds();

        double x = random.nextDouble() * mb.getWidth() * 0.9 + mb.getX();
        double y = random.nextDouble() * mb.getHeight() + mb.getY();
        Point2D start = new Point2D.Double( x, y );
        MutableVector2D dir = new MutableVector2D( 1, 0 );
        double height = modelLength / image.getWidth() * image.getHeight();
        GrabBagResistor res = new GrabBagResistor( start, dir, modelLength, height, module.getCircuitChangeListener(), this );
        res.setResistance( getResistance() );
        return res;
    }
}

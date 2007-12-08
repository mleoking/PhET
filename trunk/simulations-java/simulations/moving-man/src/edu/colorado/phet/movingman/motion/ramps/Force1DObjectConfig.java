/*  */
package edu.colorado.phet.movingman.motion.ramps;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Dec 15, 2004
 * Time: 12:19:35 PM
 */
public class Force1DObjectConfig {
    private String imageURL;
    private String name;
    private double defaultScale;
    private double mass;
    private double staticFriction;
    private double kineticFriction;

    public Force1DObjectConfig( String imageURL, String name, double defaultScale, double mass, double staticFriction, double kineticFriction ) {
        this.imageURL = imageURL;
        this.name = name;
        this.defaultScale = defaultScale;
        this.mass = mass;
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getName() {
        return name;
    }

    public double getDefaultScale() {
        return defaultScale;
    }

    public BufferedImage getImage() throws IOException {
        return ImageLoader.loadBufferedImage( imageURL );
    }

    public String toString() {
        return getName();
    }

    public double getMass() {
        return mass;
    }

    public double getStaticFriction() {
        return staticFriction;
    }

    public double getKineticFriction() {
        return kineticFriction;
    }
}

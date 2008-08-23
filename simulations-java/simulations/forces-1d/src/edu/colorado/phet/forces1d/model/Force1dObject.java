/*  */
package edu.colorado.phet.forces1d.model;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.forces1d.common_force1d.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Dec 15, 2004
 * Time: 12:19:35 PM
 */
public class Force1dObject {
    private String location;
    private String name;
    private double defaultScale;
    private double mass;
    private double staticFriction;
    private double kineticFriction;

    public Force1dObject( String location, String name, double defaultScale, double mass, double staticFriction, double kineticFriction ) {
        this.location = location;
        this.name = name;
        this.defaultScale = defaultScale;
        this.mass = mass;
        this.staticFriction = staticFriction;
        this.kineticFriction = kineticFriction;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public double getDefaultScale() {
        return defaultScale;
    }

    public BufferedImage getImage() throws IOException {
        return ImageLoader.loadBufferedImage( location );
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

package edu.colorado.phet.sugarandsaltsolutions.common.model;

/**
 * Beaker dimensions and location in meters, public so other classes can use them for layout.
 * This class exists so the dimensions can be passed together as a parameter when constructing model instances.
 *
 * @author Sam Reid
 */
public class BeakerDimension {
    //Width of the beaker (x-direction)
    public final double width;

    //Height (tallness) of the beaker (y-direction)
    public final double height;

    //Location of the left side of the beaker
    public final double x;

    //Depth is z-direction z-depth (into the screen)
    public final double depth;

    //Create a beaker dimension with the specified values, y is assumed to be zero, so is not provided
    public BeakerDimension( double x, double width, double height, double depth ) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.depth = depth;
    }
}

package edu.colorado.phet.sugarandsaltsolutions.common.model;

/**
 * Beaker dimensions and location in meters, public so other classes can use them for layout.
 * This class exists so the dimensions can be passed together as a parameter when constructing model instances, and so it can encode default assumptions
 * like the beaker is centered around x=0 and has a certain aspect ratio.
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

    //Width of the wall
    public final double wallThickness;

    //Create a beaker dimension with a standardized aspect ratio, where the height and depth are half the width, and centered around x=0
    //This constructor is here because Micro and Macro tabs use the same aspect ratio of beaker dimensions
    public BeakerDimension( double width ) {
        this( width, width / 2, width / 2 );
    }

    //Create a beaker dimension with the specified values, y is assumed to be zero, so is not provided.  The beaker is centered around x=0
    private BeakerDimension( double width, double height, double depth ) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        //Set the x-position so the middle of the beaker will be centered at x=0
        this.x = -width / 2;

        //Thickness of the walls
        this.wallThickness = width / 40.0;
    }
}

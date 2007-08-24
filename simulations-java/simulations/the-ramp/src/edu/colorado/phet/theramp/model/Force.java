/*  */
package edu.colorado.phet.theramp.model;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 7:41:14 PM
 */

public class Force {
    private String name;
    private Surface ramp;
    private String html;
    private double x;
    private double y;
    private double parallel;
    private double perpendicular;

    public Force( String name, Surface ramp ) {
        this.name = name;
        this.ramp = ramp;
    }

    public Force( String name, double x, double y ) {
        this( name, name, x, y );
    }

    public Force( String name, String html, double x, double y ) {
        this.name = name;
        this.html = html;
        this.x = x;
        this.y = y;
    }

    public void setParallel( double parallel ) {
        this.parallel = parallel;
        this.perpendicular = 0.0;
        this.x = Math.cos( ramp.getAngle() ) * parallel;
        this.y = Math.sin( ramp.getAngle() ) * parallel;
    }
}

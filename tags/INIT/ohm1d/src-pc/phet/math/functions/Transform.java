package phet.math.functions;

public class Transform implements Function {
    double xmin;
    double width;
    double ymin;
    double height;

    public double getDomainMin() {
        return xmin;
    }

    public double getDomainMax() {
        return xmin + width;
    }

    public String toString() {
        return "xmin=" + xmin + ", ymin=" + ymin + ", width=" + width + ", height=" + height;
    }

    public Transform( double xmin, double width, double ymin, double height ) {
        this.xmin = xmin;
        this.width = width;
        this.ymin = ymin;
        this.height = height;
    }

    public Transform invert() {
        return new Transform( ymin, height, xmin, width );
    }

    public double evaluate( double x ) {
        //Could ensure that x is in our domain.
        double slope = height / width;
        return ( x - xmin ) * slope + ymin;
    }
}



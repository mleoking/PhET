/**
 * Class: RandomGaussian
 * Class: edu.colorado.phet.coreadditions
 * User: Ron LeMaster
 * Date: Mar 1, 2004
 * Time: 3:13:41 PM
 */
package edu.colorado.phet.coreadditions;

/**
 * See: http://www.taygeta.com/random/gaussian.html
 */
public class RandomGaussian {

    public static double get() {
        double x1, x2, w, y1, y2;

        do {
            x1 = 2.0 * Math.random() - 1.0;
            x2 = 2.0 * Math.random() - 1.0;
            w = x1 * x1 + x2 * x2;
        } while( w >= 1.0 );

        w = Math.sqrt( ( -2.0 * Math.log( w ) ) / w );
        y1 = x1 * w;
        y2 = x2 * w;
        return y1;
    }
}

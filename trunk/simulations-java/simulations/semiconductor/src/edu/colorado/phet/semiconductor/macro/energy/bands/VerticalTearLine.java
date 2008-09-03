/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.util.DoubleGeneralPath;

/**
 * User: Sam Reid
 * Date: Mar 23, 2004
 * Time: 11:43:27 AM
 */
public class VerticalTearLine {
    DoubleGeneralPath path;

    public VerticalTearLine( PhetVector src, PhetVector dst, double width, int numJags ) {
        path = new DoubleGeneralPath( src );
        double totalHeight = dst.getY() - src.getY();
        //first one, move left and down half the specified amount.
        double jagHeight = totalHeight / numJags;
        path.lineToRelative( -width / 2, jagHeight / 2 );
        for( int i = 0; i < numJags - 1; i++ ) {
            path.lineToRelative( width, jagHeight );
            path.lineToRelative( -width, jagHeight );
        }
        path.lineToRelative( width / 2, jagHeight / 2 );
    }

    public DoubleGeneralPath getPath() {
        return path;
    }

//    static void test(Rectangle2D bounds){
//        PhetVector src=new PhetVector(bounds.getX()+bounds.getWidth()/2,bounds.getEnergy());
//        PhetVector dst=new PhetVector(bounds.getX()+bounds.getWidth()/2,bounds.getEnergy()+bounds.getNumFilledLevels());
//        VerticalTearLine vtl=new VerticalTearLine(src, dst,bounds.getWidth()/10,10);
//
//    }
}

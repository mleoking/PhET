/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;


import edu.colorado.phet.semiconductor.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Mar 23, 2004
 * Time: 11:43:27 AM
 */
public class VerticalTearLine {
    DoubleGeneralPath path;

    public VerticalTearLine( AbstractVector2D src, AbstractVector2D dst, double width, int numJags ) {
        path = new DoubleGeneralPath( src.getX(), src.getY());
        double totalHeight = dst.getY() - src.getY();
        //first one, move left and down half the specified amount.
        double jagHeight = totalHeight / numJags;
        path.lineToRelative( -width / 2, jagHeight / 2 );
        for ( int i = 0; i < numJags - 1; i++ ) {
            path.lineToRelative( width, jagHeight );
            path.lineToRelative( -width, jagHeight );
        }
        path.lineToRelative( width / 2, jagHeight / 2 );
    }

    public DoubleGeneralPath getPath() {
        return path;
    }

//    static void test(Rectangle2D bounds){
//        Vector2D.Double src=new Vector2D.Double(bounds.getX()+bounds.getWidth()/2,bounds.getEnergy());
//        Vector2D.Double dst=new Vector2D.Double(bounds.getX()+bounds.getWidth()/2,bounds.getEnergy()+bounds.getNumFilledLevels());
//        VerticalTearLine vtl=new VerticalTearLine(src, dst,bounds.getWidth()/10,10);
//
//    }
}

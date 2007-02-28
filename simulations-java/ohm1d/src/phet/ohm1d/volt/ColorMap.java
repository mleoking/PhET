/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Nov 13, 2002
 * Time: 6:58:08 PM
 * To change this template use Options | File Templates.
 */
package phet.ohm1d.volt;

import java.awt.*;

public interface ColorMap {
    public Color toColor( double ratio );

    public boolean isChanging();
}

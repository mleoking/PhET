/*, 2003.*/
package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;


/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 12:29:41 PM
 */
public class EntryPoint {
    Vector2D.Double source;
    EnergyCell cell;

    public EntryPoint( EnergyCell cell, Vector2D.Double offset ) {
        this( cell.getX() + offset.getX(), cell.getEnergy() + offset.getY(), cell );
    }

    public EntryPoint( double x, double y, EnergyCell cell ) {
        this.source = new Vector2D.Double( x, y );
        this.cell = cell;
    }

    public Vector2D.Double getSource() {
        return source;
    }

    public EnergyCell getCell() {
        return cell;
    }

}

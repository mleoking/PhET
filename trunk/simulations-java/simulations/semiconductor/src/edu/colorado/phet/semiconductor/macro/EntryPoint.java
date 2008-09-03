/*, 2003.*/
package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 12:29:41 PM
 */
public class EntryPoint {
    PhetVector source;
    EnergyCell cell;

    public EntryPoint( EnergyCell cell, PhetVector offset ) {
        this( cell.getX() + offset.getX(), cell.getEnergy() + offset.getY(), cell );
    }

    public EntryPoint( double x, double y, EnergyCell cell ) {
        this.source = new PhetVector( x, y );
        this.cell = cell;
    }

    public PhetVector getSource() {
        return source;
    }

    public EnergyCell getCell() {
        return cell;
    }

}

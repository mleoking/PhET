/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyCell;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 12:29:41 PM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class EntryPoint {
    PhetVector source;
    EnergyCell cell;

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

/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.xml;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.components.Bulb;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.common.math.PhetVector;


/**
 * User: Sam Reid
 * Date: Nov 23, 2003
 * Time: 12:07:44 AM
 * Copyright (c) Nov 23, 2003 by Sam Reid
 */
public class BulbData extends BranchData {
    double vecx;
    double vecy;

    public BulbData() {
    }

    public BulbData( Bulb b ) {
        super( b );
        this.vecx = b.getVecFromStartJunction().getX();
        this.vecy = b.getVecFromStartJunction().getY();
    }

    public double getVecx() {
        return vecx;
    }

    public void setVecx( double vecx ) {
        this.vecx = vecx;
    }

    public double getVecy() {
        return vecy;
    }

    public void setVecy( double vecy ) {
        this.vecy = vecy;
    }

    public Branch toBranch( Circuit parent ) {
        return new Bulb( parent, x0, y0, x1, y1, new PhetVector( vecx, vecy ), resistance );
    }
}

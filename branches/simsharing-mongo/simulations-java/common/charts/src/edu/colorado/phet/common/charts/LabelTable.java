// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.common.charts;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetGraphic;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 8:24:44 AM
 */

public class LabelTable {
    private Hashtable hashtable = new Hashtable();

    public LabelTable() {
    }

    public void put( double value, PhetGraphic label ) {
        hashtable.put( new Double( value ), label );
    }

    public double[] keys() {
        Set set = hashtable.keySet();
        ArrayList list = new ArrayList( set );
        double[] k = new double[list.size()];
        for ( int i = 0; i < list.size(); i++ ) {
            k[i] = ( (Double) list.get( i ) ).doubleValue();
        }
        return k;
    }

    public PhetGraphic get( double key ) {
        return (PhetGraphic) hashtable.get( new Double( key ) );
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model;

import edu.colorado.phet.advancedacidbasesolutions.model.Acid.*;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.Ammonia;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.Pyridine;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.SodiumHydroxide;

/**
 * Factory for creating solutes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteFactory {
    
    /*
     * NOTE: If you add a new solute here, also add one to SoluteComboBox.
     */
    // one instance of each solute used in the sim
    private static final Solute[] SOLUTES = {
        new NoSolute(),
        new AceticAcid(),
        new Ammonia(),
        new ChlorousAcid(),
        new HydrochloricAcid(),
        new HydrofluoricAcid(),
        new HypochlorusAcid(),
        new PerchloricAcid(),
        new Pyridine(),
        new SodiumHydroxide(),
        new CustomAcid(),
        new CustomBase()
    };
    
    public static Solute createSolute( String name ) {
        Solute solute = null;
        for ( int i = 0; i < SOLUTES.length; i++ ) {
            if ( SOLUTES[i].getName().equals( name ) ) {
                Class<? extends Solute> theClass = SOLUTES[i].getClass();
                try {
                    solute = theClass.newInstance();
                }
                catch ( InstantiationException e ) {
                    e.printStackTrace();
                }
                catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                }
            }
        }
        return solute;
    }
}

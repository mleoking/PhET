// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model.equilibrium;

import edu.colorado.phet.advancedacidbasesolutions.model.NoSolute;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute;
import edu.colorado.phet.advancedacidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.advancedacidbasesolutions.model.Acid.StrongAcid;
import edu.colorado.phet.advancedacidbasesolutions.model.Acid.WeakAcid;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.advancedacidbasesolutions.model.Base.WeakBase;

/**
 * Factory for creating equilibrium models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquilibriumModelFactory {
    
    // not intended for instantiation
    private EquilibriumModelFactory() {}
    
    /**
     * Gets a model that's appropriate for the Solute type.
     * @param solute
     * @return
     */
    public static AbstractEquilibriumModel getModel( Solute solute ) {
        AbstractEquilibriumModel model = null;
        if ( solute instanceof NoSolute ) {
            model = new PureWaterEquilibriumModel( (NoSolute) solute );
        }
        else if ( solute instanceof StrongAcid ) {
            model = new StrongAcidEquilibriumModel( (StrongAcid) solute );
        }
        else if ( solute instanceof WeakAcid ) {
            model = new WeakAcidEquilibriumModel( (WeakAcid) solute );
        }
        else if ( solute instanceof StrongBase ) {
            model = new StrongBaseEquilibriumModel( (StrongBase) solute );
        }
        else if ( solute instanceof WeakBase ) {
            model = new WeakBaseEquilibriumModel( (WeakBase) solute );
        }
        else if ( solute instanceof CustomAcid ) {
            CustomAcid acid = (CustomAcid) solute;
            if ( acid.isStrong() ) {
                model = new StrongAcidEquilibriumModel( acid );
            }
            else if ( acid.isWeak() ) {
                model = new WeakAcidEquilibriumModel( acid );
            }
            else if ( acid.isIntermediate() ) {
                model = new IntermediateAcidEquilibriumModel( acid );
            }
        }
        else if ( solute instanceof CustomBase ) {
            CustomBase base = (CustomBase) solute;
            if ( base.isStrong() ) {
                model = new StrongBaseEquilibriumModel( base );
            }
            else if ( base.isWeak() ) {
                model = new WeakBaseEquilibriumModel( base );
            }
            else if ( base.isIntermediate() ) {
                model = new IntermediateBaseEquilibriumModel( base );
            } 
        }
        else {
            throw new UnsupportedOperationException( "unsupported solute type: " + solute.getClass().getName() );
        }
        return model;
    }

}

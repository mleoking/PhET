package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.Acid;
import edu.colorado.phet.acidbasesolutions.model.Base;
import edu.colorado.phet.acidbasesolutions.model.NullSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.IntermediateAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.WeakAcid;
import edu.colorado.phet.acidbasesolutions.model.Base.IntermediateBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;
import edu.colorado.phet.acidbasesolutions.model.concentration.*;


public class ConcentrationModelFactory {
    
    // not intended for instantiation
    private ConcentrationModelFactory() {}
    
    /**
     * Gets a concentration model that's appropriate for the Solute type.
     * @param solute
     * @return ConcentrationModel
     */
    public static ConcentrationModel getModel( Solute solute ) {
        ConcentrationModel model = null;
        if ( solute instanceof NullSolute ) {
            model = new PureWaterConcentrationModel( (NullSolute)solute );
        }
        else if ( solute instanceof StrongAcid ) {
            model = new StrongAcidConcentrationModel( (StrongAcid)solute );
        }
        else if ( solute instanceof IntermediateAcid ) {
            model = new IntermediateAcidConcentrationModel( (IntermediateAcid)solute );
        }
        else if ( solute instanceof WeakAcid ) {
            model = new WeakAcidConcentrationModel( (WeakAcid)solute );
        }
        else if ( solute instanceof StrongBase ) {
            model = new StrongBaseConcentrationModel( (StrongBase)solute );
        }
        else if ( solute instanceof IntermediateBase ) {
            model = new IntermediateBaseConcentrationModel( (IntermediateBase)solute );
        }
        else if ( solute instanceof WeakBase ) {
            model = new WeakBaseConcentrationModel( (WeakBase)solute );
        }
        else {
            throw new UnsupportedOperationException( "unsupported solute type: " + solute.getClass().getName() );
        }
        return model;
    }

}

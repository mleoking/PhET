package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.NullSolute;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Acid.CustomAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.StrongAcid;
import edu.colorado.phet.acidbasesolutions.model.Acid.WeakAcid;
import edu.colorado.phet.acidbasesolutions.model.Base.CustomBase;
import edu.colorado.phet.acidbasesolutions.model.Base.StrongBase;
import edu.colorado.phet.acidbasesolutions.model.Base.WeakBase;


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
            model = new PureWaterConcentrationModel( solute );
        }
        else if ( solute instanceof StrongAcid ) {
            model = new StrongAcidConcentrationModel( solute );
        }
        else if ( solute instanceof WeakAcid ) {
            model = new WeakAcidConcentrationModel( solute );
        }
        else if ( solute instanceof StrongBase ) {
            model = new StrongBaseConcentrationModel( solute );
        }
        else if ( solute instanceof WeakBase ) {
            model = new WeakBaseConcentrationModel( (WeakBase)solute );
        }
        else if ( solute instanceof CustomAcid ) {
            CustomAcid acid = (CustomAcid) solute;
            if ( acid.isStrong() ) {
                model = new StrongAcidConcentrationModel( solute );
            }
            else if ( acid.isWeak() ) {
                model = new WeakAcidConcentrationModel( solute );
            }
            else if ( acid.isIntermediate() ) {
                model = new IntermediateAcidConcentrationModel( solute );
            }
        }
        else if ( solute instanceof CustomBase ) {
            CustomBase base = (CustomBase) solute;
            if ( base.isStrong() ) {
                model = new StrongBaseConcentrationModel( solute );
            }
            else if ( base.isWeak() ) {
                model = new WeakBaseConcentrationModel( solute );
            }
            else if ( base.isIntermediate() ) {
                model = new IntermediateBaseConcentrationModel( solute );
            } 
        }
        else {
            throw new UnsupportedOperationException( "unsupported solute type: " + solute.getClass().getName() );
        }
        return model;
    }

}

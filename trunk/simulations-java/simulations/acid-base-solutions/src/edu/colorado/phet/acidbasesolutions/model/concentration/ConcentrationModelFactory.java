package edu.colorado.phet.acidbasesolutions.model.concentration;

import edu.colorado.phet.acidbasesolutions.model.NoSolute;
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
        if ( solute instanceof NoSolute ) {
            model = new PureWaterConcentrationModel( (NoSolute) solute );
        }
        else if ( solute instanceof StrongAcid ) {
            model = new StrongAcidConcentrationModel( (StrongAcid) solute );
        }
        else if ( solute instanceof WeakAcid ) {
            model = new WeakAcidConcentrationModel( (WeakAcid) solute );
        }
        else if ( solute instanceof StrongBase ) {
            model = new StrongBaseConcentrationModel( (StrongBase) solute );
        }
        else if ( solute instanceof WeakBase ) {
            model = new WeakBaseConcentrationModel( (WeakBase) solute );
        }
        else if ( solute instanceof CustomAcid ) {
            CustomAcid acid = (CustomAcid) solute;
            if ( acid.isStrong() ) {
                model = new StrongAcidConcentrationModel( acid );
            }
            else if ( acid.isWeak() ) {
                model = new WeakAcidConcentrationModel( acid );
            }
            else if ( acid.isIntermediate() ) {
                model = new IntermediateAcidConcentrationModel( acid );
            }
        }
        else if ( solute instanceof CustomBase ) {
            CustomBase base = (CustomBase) solute;
            if ( base.isStrong() ) {
                model = new StrongBaseConcentrationModel( base );
            }
            else if ( base.isWeak() ) {
                model = new WeakBaseConcentrationModel( base );
            }
            else if ( base.isIntermediate() ) {
                model = new IntermediateBaseConcentrationModel( base );
            } 
        }
        else {
            throw new UnsupportedOperationException( "unsupported solute type: " + solute.getClass().getName() );
        }
        return model;
    }

}

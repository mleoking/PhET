package edu.colorado.phet.semiconductor.macro.energy;

import java.util.ArrayList;

import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.common.phetcommon.model.ModelElement;


/**
 * User: Sam Reid
 * Date: Apr 20, 2004
 * Time: 7:18:58 AM
 */
public class ChoiceStateModel implements ModelElement {
    private EnergySection energySection;
    ArrayList models = new ArrayList();

    public ChoiceStateModel( EnergySection energySection ) {
        this.energySection = energySection;
    }

    public void addModel( ModelCriteria criteria, ModelElement model ) {
        models.add( new ModelWithCriteria( model, criteria, energySection ) );
    }

    public void stepInTime( double dt ) {
        for ( int i = 0; i < models.size(); i++ ) {
            ModelWithCriteria modelWithCriteria = (ModelWithCriteria) models.get( i );
            modelWithCriteria.stepInTime( dt );
//            if( modelWithCriteria.getCriteria().isApplicable( energySection ) ) {
//                modelWithCriteria.getModel().stepInTime( dt );
//                return;
//            }
        }
    }

    public void clear() {
        models.clear();
    }

}

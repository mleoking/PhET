
package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.common_semiconductor.model.ModelElement;
import edu.colorado.phet.semiconductor_semi.macro.energy.statemodels.ModelCriteria;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 10:01:05 AM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class ModelWithCriteria {
    ModelElement model;
    ModelCriteria criteria;
    private EnergySection energySection;

    public ModelWithCriteria( ModelElement model, ModelCriteria criteria, EnergySection energySection ) {
        this.model = model;
        this.criteria = criteria;
        this.energySection = energySection;
    }

    public void stepInTime( double dt ) {
        if( criteria.isApplicable( energySection ) ) {
            model.stepInTime( dt );
        }
    }
}
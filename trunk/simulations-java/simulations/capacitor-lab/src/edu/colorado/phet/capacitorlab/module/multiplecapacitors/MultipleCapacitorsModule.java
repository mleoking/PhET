// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.CLModule;

/**
 * The "Multiple Capacitors" module.
 * </p>
 * This module was added in version 2.00. Summary of requirements:
 * <ul>
 * <li>simplified capacitors with fixed area and variable capacitance, separation changes with capacitance
 * <li>min/max of capacitance slider varies capacitance by 3x (choose min/max to look good)
 * <li>capacitance meter shows total capacitance for the circuit
 * <li>charge meter shows total stored charge for the circuit
 * <li>no dielectric (air)
 * <li>show charge on plates
 * <li>show E-field lines
 * <li>current indicators near battery poles (like in other modules)
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsModule extends CLModule {

    private final MultipleCapacitorsModel model;
    private final MultipleCapacitorsCanvas canvas;

    public MultipleCapacitorsModule( CLGlobalProperties globalProperties ) {
        super( CLStrings.MULTIPLE_CAPACITORS );

        CLModelViewTransform3D mvt = new CLModelViewTransform3D();

        model = new MultipleCapacitorsModel( getClock(), mvt );

        canvas = new MultipleCapacitorsCanvas( model, mvt, globalProperties );
        setSimulationPanel( canvas );

        setControlPanel( new MultipleCapacitorsControlPanel( this, model, canvas ) );
    }

    @Override
    public void reset() {
        super.reset();
        model.reset();
        canvas.reset();
    }
}

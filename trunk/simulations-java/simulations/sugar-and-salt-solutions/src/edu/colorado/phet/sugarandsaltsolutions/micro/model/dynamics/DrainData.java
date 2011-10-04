// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.sugarandsaltsolutions.common.model.Formula;

/**
 * DrainData helps to maintain a constant concentration as particles flow out the drain by tracking flow rate and timing
 *
 * @author Sam Reid
 */
public class DrainData {

    //initial number of formula units (such as Ca + Cl + Cl for CaCl) at time user started manipulating drain faucet, used to choose a target concentration in MicroModel
    public int initialNumberFormulaUnits;

    //initial volume at time user started manipulating drain faucet, in m^3, used to choose a target concentration in MicroModel
    public double initialVolume;

    //the previous flow rate of the drain faucet, for purposes of recording the target concentration when user starts draining fluid.
    public double previousDrainFlowRate;

    //The formula to match for the formula unit, necessary since concentration is different for each solute type
    public final Formula formula;

    public DrainData( Formula formula ) {
        this.formula = formula;
        //REVIEW should initialNumberFormulaUnits be set here to make this properly constructed?
    }
}
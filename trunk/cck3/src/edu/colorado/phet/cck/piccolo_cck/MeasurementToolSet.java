/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;

/**
 * MeasurementToolSet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MeasurementToolSet {
    private VoltmeterModel voltmeterModel;

    public MeasurementToolSet( CCKModel model ) {
        this.voltmeterModel = new VoltmeterModel( model, model.getCircuit() );
    }

    public VoltmeterModel getVoltmeterModel() {
        return voltmeterModel;
    }
}

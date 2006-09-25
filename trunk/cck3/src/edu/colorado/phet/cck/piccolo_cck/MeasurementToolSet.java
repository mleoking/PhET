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

/**
 * MeasurementToolSet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MeasurementToolSet {
    private VoltmeterModel voltmeterModel;

    public MeasurementToolSet() {
        this.voltmeterModel = new VoltmeterModel();
    }

    public VoltmeterModel getVoltmeterModel() {
        return voltmeterModel;
    }
}

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.model.BaseModel;

import java.awt.geom.Point2D;
import java.awt.*;

/**
 * SolubleSaltsModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModel extends BaseModel {

    private Vessel vessel;
    private Point2D vesselLoc = new Point2D.Double( 300, 200 );
    private double vesselWidth = 300;
    private double vesselDepth = 200;

    public SolubleSaltsModel() {
        vessel = new Vessel( vesselWidth, vesselDepth, vesselLoc );
    }

    public Vessel getVessel() {
        return vessel;
    }

}

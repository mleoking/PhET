/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.GeneralPath;
import java.awt.*;

/**
 * IEnergyProfile
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface IEnergyProfile {
    double getWidth();

    double getMinEnergy();

    double getMaxEnergy();

    GeneralPath getPotentialEnergyPath();

    Shape getTotalEnergyPath();

    double getAlphaDecayX();

    double getTotalEnergy();

    Shape[] getShape();

    double getHillY( double x );

    double getDyDx( double v );
}

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

import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.*;

/**
 * IPotentialProfile
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface IPotentialProfile extends IProfile, SimpleObserver {
    double getWidth();

    void setWidth( double width );

    double getMinEnergy();

    void setMinEnergy( double minEnergy );

    double getMaxEnergy();

    void setMaxEnergy( double maxEnergy );

    void setWellDepth( double wellDepth );

    double getDistFromHill( Point2D.Double pt );

    GeneralPath getPotentialEnergyPath();

    GeneralPath getBackgroundPath();

    double getAlphaDecayX();

    Shape[] getShape();

    double getHillY( double x );

    double getProfilePeakX();

    double getDyDx( double v );
}

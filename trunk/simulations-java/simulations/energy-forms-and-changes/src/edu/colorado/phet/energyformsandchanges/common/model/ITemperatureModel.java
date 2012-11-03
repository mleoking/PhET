// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.model;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.energyformsandchanges.intro.model.TemperatureAndColor;

/**
 * Interface for a model that provides temperature information at 2D locations
 * within the model space.
 *
 * @author John Blanco
 */
public interface ITemperatureModel {
    TemperatureAndColor getTemperatureAndColorAtLocation( Vector2D location );
}

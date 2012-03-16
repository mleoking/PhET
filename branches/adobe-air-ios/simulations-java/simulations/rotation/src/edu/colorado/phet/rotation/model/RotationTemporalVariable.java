// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;

/**
 * Created by: Sam
 * Dec 3, 2007 at 11:10:34 PM
 */
public class RotationTemporalVariable extends DefaultTemporalVariable {
    public RotationTemporalVariable() {
        super( RotationModel.getTimeSeriesFactory() );
    }
}

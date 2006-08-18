/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.theramp.model.Surface;

/**
 * User: Sam Reid
 * Date: May 31, 2005
 * Time: 7:42:30 PM
 * Copyright (c) May 31, 2005 by Sam Reid
 */

public class FloorGraphic extends SurfaceGraphic {
    public FloorGraphic( RampPanel rampPanel, Surface ground ) {
        super( rampPanel, ground );
        getSurfaceGraphic().setVisible( false );
        getAngleGraphic().setVisible( false );
        getHeightReadoutGraphic().setVisible( false );
        setPickable( false );
        setChildrenPickable( false );
    }
}

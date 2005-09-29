/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PhetPCanvas;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:51 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3PhetPCanvas extends PhetPCanvas {
    private EC3Module ec3Module;
    private EnergyConservationModel ec3Model;

    public EC3PhetPCanvas( EC3Module ec3Module ) {
        this.ec3Module = ec3Module;
        this.ec3Model = ec3Module.getEnergyConservationModel();
        for( int i = 0; i < ec3Model.numBodies(); i++ ) {
            BodyGraphic bodyGraphic = new BodyGraphic( ec3Module, ec3Model.bodyAt( i ) );
            addWorldChild( bodyGraphic );
        }

        AbstractSpline spline = new CubicSpline( 50 );

        spline.addControlPoint( 150, 300 );
        spline.addControlPoint( 200, 320 );
        spline.addControlPoint( 350, 300 );
        spline.addControlPoint( 400, 375 );
        SplineGraphic splineGraphic = new SplineGraphic( spline );
        ec3Model.addSpline( spline );

        addWorldChild( splineGraphic );
    }
}

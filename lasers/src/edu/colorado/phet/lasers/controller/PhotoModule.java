/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;

/**
 * PhotoModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoModule extends Module {

    public PhotoModule( AbstractClock clock ) {
        super( "Real Laser", clock );

        setModel( new BaseModel() );
        ApparatusPanel ap = new ApparatusPanel2( clock );
        setApparatusPanel( ap );
        PhetImageGraphic laserIG = new PhetImageGraphic( getApparatusPanel(), "images/annotated-laser.png");
        ap.setPreferredSize( laserIG.getSize());
        ap.addGraphic( laserIG );
    }
}

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.module;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;

/**
 * PhotoelectricModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModule extends Module {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private String title = "Photoelectric Effect";


    //----------------------------------------------------------------
    // Constructors and initialization
    //----------------------------------------------------------------

    public PhotoelectricModule( AbstractClock clock ) {
        super( title, clock );

        //----------------------------------------------------------------
        // Model
        //----------------------------------------------------------------
        setModel( new BaseModel() );

        //----------------------------------------------------------------
        // View
        //----------------------------------------------------------------
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );
    }
}

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.model.ElectronSink;

import java.util.List;
import java.util.ArrayList;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * PhotoelectricModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModel extends BaseModel {

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private ElectronSource cathode;
    private Point2D[] cathodeEndpoints = new Point2D[]{new Point2D.Double( 200, 200 ),
                                                       new Point2D.Double( 200, 300 )};
    private ElectronSink anode;
    private Point2D[] anodeEndpoints = new Point2D[]{new Point2D.Double( 500, 200 ),
                                                     new Point2D.Double( 500, 300 )};
    private List electrons = new ArrayList();

    //----------------------------------------------------------------
    // Contructors and initialization
    //----------------------------------------------------------------

    public PhotoelectricModel() {

        cathode = new ElectronSource( this, cathodeEndpoints[0], cathodeEndpoints[1] );
        anode = new ElectronSource( this, anodeEndpoints[0], anodeEndpoints[1] );
    }

}

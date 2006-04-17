/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.common.model.BaseModel;

/**
 * QuatumModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class QuatumModel extends BaseModel implements Photon.LeftSystemEventListener {


    public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
        removeModelElement( event.getPhoton() );
    }
}

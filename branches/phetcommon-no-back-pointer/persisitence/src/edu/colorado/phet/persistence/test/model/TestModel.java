/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.model;

import edu.colorado.phet.common.model.BaseModel;

/**
 * TestModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestModel extends BaseModel {

    //Not allowed to mess with the way we call our abstract method.
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
    }
}

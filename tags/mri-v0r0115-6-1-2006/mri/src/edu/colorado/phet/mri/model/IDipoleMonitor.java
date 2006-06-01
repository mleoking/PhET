/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import java.util.List;

/**
 * IDipoleMonitor
 * <p/>
 * Methods for tracking the dipoles in the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface IDipoleMonitor {
    List getDipoles();

    List getUpDipoles();

    List getDownDipoles();
}

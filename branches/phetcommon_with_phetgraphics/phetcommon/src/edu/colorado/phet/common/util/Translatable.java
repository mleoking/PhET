/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * Translatable
 * <p>
 * An interface provided primarilly so controllers can move model elements
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface Translatable {
    void translate( double dx, double dy );
}

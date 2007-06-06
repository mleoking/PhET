/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/Translatable.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * Translatable
 * <p>
 * An interface provided primarilly so controllers can move model elements
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public interface Translatable {
    void translate( double dx, double dy );
}

/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_sound.util;

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

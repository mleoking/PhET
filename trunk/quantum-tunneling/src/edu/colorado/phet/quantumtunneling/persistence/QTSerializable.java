/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.persistence;

import java.io.Serializable;


/**
 * QTSerializable is a marker interface for serializable objects
 * that are specific to this simulation's persistence feature.
 * <p>
 * This interface's main purpose is to inform ProGuard about
 * classes that will be accessed via reflection by XMLEncoder
 * and XMLDecoder.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface QTSerializable extends Serializable {

}

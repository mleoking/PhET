/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.persistence;

import java.io.Serializable;

/**
 * OTPersistenceManager is a marker interface for serializable objects
 * that are specific to this simulation's persistence feature.
 * <p>
 * This interface's main purpose is to inform ProGuard about
 * classes that will be accessed via reflection by XMLEncoder
 * and XMLDecoder.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface OTSerializable extends Serializable {

}

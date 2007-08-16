/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.util;

import java.io.Serializable;

/**
 * IProguardKeepClass is a marker interface related to PhET's use of Proguard.
 * When applying Proguard to JAR files, we will tell Proguard to keep
 * any class that implements this interface.  If you have a class that 
 * is dynamically loaded, you'll want to implement this interface so 
 * that Proguard keeps your class.
 * <p>
 * See the "-keep class" option in the Proguard manual.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface IProguardKeepClass extends Serializable {}

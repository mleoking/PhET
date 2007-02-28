/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.math.transforms;

/**
 * User: Sam Reid
 * Date: Jul 13, 2003
 * Time: 12:44:51 AM
 * Copyright (c) Jul 13, 2003 by Sam Reid
 */
public interface InvertibleTransform extends Transform{
    InvertibleTransform invert();
}

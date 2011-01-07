// Copyright 2002-2011, University of Colorado

/**
 * Class: RgbReporter
 * Class: edu.colorado.phet.sound.view
 * User: Ron LeMaster
 * Date: Aug 20, 2004
 * Time: 4:07:27 PM
 */
package edu.colorado.phet.sound.view;

/**
 * Reports the RGB value at a given point
 */
public interface RgbReporter {
    int rgbAt( int x, int y );
}

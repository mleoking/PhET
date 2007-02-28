/**
 * Class: PumpHandleGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Jan 14, 2004
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.graphics.MovableImageGraphic;

import java.awt.*;

/**
 * This class is provided just to overide isInHotSpot to make the pump
 * handle work correctly. The behavior for controlling mouse-related
 * movement of the pump handle is all contained in BaseIdealGasApparatusPanel.
 * This isn't really the right way to do it, but it is how things were
 * done way back when the application was originally written.
 */
public class PumpHandleGraphic extends MovableImageGraphic {

    public PumpHandleGraphic( Image image, float x, float y,
                  float minX, float minY,
                  float maxX, float maxY ) {
        super( image, x, y, minX, minY, maxX, maxY );
    }

    public boolean isInHotSpot( Point p ) {
        return false;
    }
}

// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 4, 2006
 * Time: 12:07:00 AM
 */
public interface ClipFactory {
    Shape getClip(ElectronNode electronNode);
}

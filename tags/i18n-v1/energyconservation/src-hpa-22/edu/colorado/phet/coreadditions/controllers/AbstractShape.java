package edu.colorado.phet.coreadditions.controllers;

import java.awt.*;

/**
 * Used by some MouseHandlers to determine whether a mouse can handle an event.
 */
public interface AbstractShape {
    boolean containsPoint( Point pt );
}

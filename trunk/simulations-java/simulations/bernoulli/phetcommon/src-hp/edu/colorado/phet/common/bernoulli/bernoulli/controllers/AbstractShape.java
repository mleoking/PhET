package edu.colorado.phet.common.bernoulli.bernoulli.controllers;

import java.awt.*;

/**
 * Used by some MouseHandlers to determine whether a mouse can handle an event.
 */
public interface AbstractShape {
    boolean containsPoint(Point pt);
}

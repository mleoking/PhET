// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;

/**
 * Provides the colors for a Body instance.  Implemented by Body model instances so that the colors can be specified on instantiation instead of later through logic or maps.
 *
 * @author Sam Reid
 */
public interface IBodyColors {
    Color getHighlight();//The highlight is used for the highlight of a Gradient sphere

    Color getColor();//the color is used as the base color, and for the path color.
}

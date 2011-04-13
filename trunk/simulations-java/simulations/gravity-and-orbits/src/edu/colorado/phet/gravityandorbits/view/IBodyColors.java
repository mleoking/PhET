// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;

//REVIEW it would be more appropriate to put method descriptions with the methods, rather than in the class doc.
//REVIEW why is this interface in view package, but implemented by Body in model package?

/**
 * Provides the colors for a Body instance.  The highlight is used for the highlight of a Gradient sphere,
 * and the color is used as the base color, and for the path color.
 *
 * @author Sam Reid
 */
public interface IBodyColors {
    Color getHighlight();

    Color getColor();
}

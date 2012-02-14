// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.RepNode;

/**
 * @author Sam Reid
 */
public interface Representation {
    RepNode createNode( ModelViewTransform transform, Fraction fraction );
}
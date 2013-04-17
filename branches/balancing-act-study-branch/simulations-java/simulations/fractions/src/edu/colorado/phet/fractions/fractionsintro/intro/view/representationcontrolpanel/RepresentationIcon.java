// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel;

import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;

/**
 * Icons used in the representation control panel.
 *
 * @author Sam Reid
 */
public interface RepresentationIcon {
    PNode getNode();

    Representation getRepresentation();
}

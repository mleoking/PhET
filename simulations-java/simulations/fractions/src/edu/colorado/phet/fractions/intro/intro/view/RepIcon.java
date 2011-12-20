// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Icons used in the representation control panel
 *
 * @author Sam Reid
 */
public interface RepIcon {
    PNode getNode();

    ChosenRepresentation getRepresentation();
}

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;

/**
 * Combination of arguments used in creating nodes for PieSetNode.
 * This class is used so that we can have named arguments and still use <code>F<U,V></code> for representing the function that creates nodes.
 *
 * @author Sam Reid
 */
public @Data class SliceNodeArg {
    public final PNode rootNode;
    public final SettableProperty<PieSet> model;
    public final Slice slice;
}
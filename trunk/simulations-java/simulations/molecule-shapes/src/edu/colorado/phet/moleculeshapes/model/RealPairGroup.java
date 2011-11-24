// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;

public class RealPairGroup extends PairGroup {

    private final Element element;

    /**
     * @param position  Initial 3D position
     * @param bondOrder Bond order, (0 if it is a lone pair)
     * @param element   Chemical element
     */
    public RealPairGroup( ImmutableVector3D position, int bondOrder, Element element ) {
        super( position, bondOrder, false );

        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}

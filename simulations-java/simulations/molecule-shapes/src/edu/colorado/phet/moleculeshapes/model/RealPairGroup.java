// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;

public class RealPairGroup extends PairGroup {

    private final Element element;

    /**
     * @param position   Initial 3D position
     * @param isLonePair Whether the pair group is a lone pair.
     * @param element    Chemical element
     */
    public RealPairGroup( Vector3D position, boolean isLonePair, Element element ) {
        super( position, isLonePair, false );

        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}

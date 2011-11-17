// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class RepresentationArea extends PNode {
    public RepresentationArea( Property<ChosenRepresentation> chosenRepresentation, Property<Integer> numerator, Property<Integer> denominator ) {
        addChild( new HorizontalBarChosenRepresentationNode( chosenRepresentation, numerator, denominator ) );
    }
}
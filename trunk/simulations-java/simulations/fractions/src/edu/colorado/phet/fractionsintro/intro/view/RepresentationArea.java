// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Place in the center of the play area which shows the selected representation such as pies.
 *
 * @author Sam Reid
 */
public class RepresentationArea extends PNode {
    public RepresentationArea( ObservableProperty<ChosenRepresentation> chosenRepresentation, IntegerProperty numerator, IntegerProperty denominator, SettableProperty<ContainerSet> containerSet ) {
        addChild( new HorizontalBarSetFractionNode( chosenRepresentation, containerSet ) {{
            setOffset( 0, -29 );
        }} );
        addChild( new VerticalBarSetFractionNode( chosenRepresentation, containerSet ) {{
            setOffset( 0, -73 );
        }} );
        addChild( new NumberLineNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.NUMBER_LINE ) ) {{
            setOffset( 10, 15 );
        }} );
        addChild( new CakeSetFractionNode( containerSet, chosenRepresentation.valueEquals( ChosenRepresentation.CAKE ) ) {{
            setOffset( -10, -40 );
        }} );
        addChild( new WaterGlassSetFractionNode( containerSet, chosenRepresentation.valueEquals( ChosenRepresentation.WATER_GLASSES ) ) {{
            setOffset( 15, -65 );
        }} );
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;
import edu.umd.cs.piccolo.PNode;

/**
 * Place in the center of the play area which shows the selected representation such as pies.
 *
 * @author Sam Reid
 */
public class RepresentationArea extends PNode {
    public RepresentationArea( Property<ChosenRepresentation> chosenRepresentation, Property<Integer> numerator, Property<Integer> denominator, Property<ContainerState> containerState ) {

        //Y offset values sampled with this inner class listener:
//        addInputEventListener( new PBasicInputEventHandler() {
//                        @Override public void mouseDragged( PInputEvent event ) {
//                            translate( 0, event.getDeltaRelativeTo( RepresentationArea.this.getParent() ).getHeight() );
//                            System.out.println( "offset y = " + getOffset().getY() );
//                        }
//                    } );

        addChild( new HorizontalBarSetFractionNode( chosenRepresentation, containerState ) {{
            setOffset( 0, -29 );
        }} );
        addChild( new VerticalBarSetFractionNode( chosenRepresentation, containerState ) {{
            setOffset( 0, -73 );
        }} );
        addChild( new PieSetFractionNode( containerState, chosenRepresentation.valueEquals( ChosenRepresentation.PIE ) ) {{
            setOffset( 0, -48 );
        }} );
        addChild( new NumberLineNode( numerator, denominator, chosenRepresentation.valueEquals( ChosenRepresentation.NUMBER_LINE ) ) {{
            setOffset( 10, 15 );
        }} );
        addChild( new CakeSetFractionNode( containerState, chosenRepresentation.valueEquals( ChosenRepresentation.CAKE ) ) {{
            setOffset( -10, -40 );
        }} );
        addChild( new WaterGlassSetFractionNode( containerState, chosenRepresentation.valueEquals( ChosenRepresentation.WATER_GLASSES ) ) {{
            setOffset( 15, -65 );

//            addInputEventListener( new PBasicInputEventHandler() {
//                @Override public void mouseDragged( PInputEvent event ) {
//                    translate( 0, event.getDeltaRelativeTo( RepresentationArea.this.getParent() ).getHeight() );
//                    System.out.println( "offset y = " + getOffset().getY() );
//                }
//            } );
        }} );

        //Since it is unclear how to subdivide a single grid while keeping it the same size, we have discarded this representation for now.
        //        addChild( new GridFractionNode( chosenRepresentation, numerator, denominator ) {{
        //            setOffset( 0, -50 );
        //        }} );
    }

    public CellPointer getClosestOpenCell( Shape globalShape, Point2D center2D ) {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof VisibilityNode ) {
                VisibilityNode visibilityNode = (VisibilityNode) child;
                if ( visibilityNode.visible.get() ) {
                    return visibilityNode.getClosestOpenCell( globalShape, center2D );
                }
            }
        }
        return null;
    }
}
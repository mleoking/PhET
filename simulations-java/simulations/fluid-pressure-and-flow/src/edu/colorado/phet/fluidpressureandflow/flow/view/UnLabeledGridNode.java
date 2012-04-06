// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a grid under the water with horizontal lines only so the user can see the water depth.
 *
 * @author Sam Reid
 */
public class UnLabeledGridNode extends PropertyVisibleNode {
    public UnLabeledGridNode( ObservableProperty<Boolean> visible, final ModelViewTransform transform, Property<UnitSet> units ) {
        super( visible );

        //Show meters if in metric
        addChild( new PropertyVisibleNode( units.valueEquals( UnitSet.METRIC ) ) {{
            for ( double i = 0; i >= -10; i -= 0.5 ) {
                addChild( new LineNode( i, transform ) );
            }
        }} );

        //Show feet if non-metric
        addChild( new PropertyVisibleNode( new Not( units.valueEquals( UnitSet.METRIC ) ) ) {{
            for ( int i = 0; i >= -10; i-- ) {
                addChild( new LineNode( Units.feetToMeters( i ), transform ) );
            }
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    public static class LineNode extends PNode {
        public LineNode( final double meters, final ModelViewTransform transform ) {
            final PhetPPath line = new PhetPPath( transform.modelToView( new Line2D.Double( -100, meters, 100, meters ) ), new BasicStroke( 1 ), Color.lightGray );
            addChild( line );
        }
    }
}
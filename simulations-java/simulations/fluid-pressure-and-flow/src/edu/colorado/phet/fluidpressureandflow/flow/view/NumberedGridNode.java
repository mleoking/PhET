// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.READOUT_FEET;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.READOUT_METERS;
import static java.text.MessageFormat.format;

/**
 * Shows a grid under the water with horizontal lines only so the user can see the water depth.
 *
 * @author Sam Reid
 */
public class NumberedGridNode extends PropertyVisibleNode {
    public NumberedGridNode( ObservableProperty<Boolean> visible, final ModelViewTransform transform, Property<UnitSet> units ) {
        super( visible );

        //Show meters if in metric
        addChild( new PropertyVisibleNode( units.valueEquals( UnitSet.METRIC ) ) {{
            for ( int i = 0; i >= -10; i-- ) {
                addChild( new LineNode( format( READOUT_METERS, new DecimalFormat( "0" ).format( -i ) ), i, transform ) );
            }
        }} );

        //Show feet if non-metric
        addChild( new PropertyVisibleNode( new Not( units.valueEquals( UnitSet.METRIC ) ) ) {{
            for ( int i = 0; i >= -10; i-- ) {
                addChild( new LineNode( format( READOUT_FEET, new DecimalFormat( "0" ).format( -i ) ), Units.feetToMeters( i ), transform ) );
            }
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    public static class LineNode extends PNode {
        public LineNode( String label, final double meters, final ModelViewTransform transform ) {
            final PhetPPath line = new PhetPPath( transform.modelToView( new Line2D.Double( 0, meters, Pool.WIDTH, meters ) ), new BasicStroke( 2 ), Color.gray );
            addChild( line );
            PhetPText text = new PhetPText( label, new PhetFont( 16, true ) );
            text.setOffset( line.getFullBounds().getX() - text.getFullBounds().getWidth(), line.getFullBounds().getCenterY() - text.getFullHeight() / 2 );
            addChild( text );
        }
    }
}
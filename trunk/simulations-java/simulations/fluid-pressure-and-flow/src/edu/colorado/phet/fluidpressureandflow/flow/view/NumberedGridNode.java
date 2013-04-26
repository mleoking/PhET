// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Or;
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
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.READOUT_FOOT;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.READOUT_METER;
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
        Or showMetric = units.valueEquals( UnitSet.METRIC ).or( units.valueEquals( UnitSet.ATMOSPHERES ) );
        addChild( new PropertyVisibleNode( showMetric ) {{
            for ( int i = 0; i >= -10; i-- ) {
                addChild( new LineNode( format( i == -1 ? READOUT_METER : READOUT_METERS, new DecimalFormat( "0" ).format( -i ) ), i, transform ) );
            }
        }} );

        //Show feet if non-metric
        addChild( new PropertyVisibleNode( new Not( showMetric ) ) {{
            for ( int i = 0; i >= -10; i-- ) {
                addChild( new LineNode( format( i == -1 ? READOUT_FOOT : READOUT_FEET, new DecimalFormat( "0" ).format( -i ) ), Units.feetToMeters( i ), transform ) );
            }
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    public static class LineNode extends PNode {
        public LineNode( String label, final double meters, final ModelViewTransform transform ) {
            final PNode line1 = new PhetPPath( transform.modelToView( new Line2D.Double( 0, meters, Pool.WIDTH, meters ) ), new BasicStroke( 1.5f ), Color.lightGray );
            Shape c = AffineTransform.getTranslateInstance( 0, 1 ).createTransformedShape( transform.modelToView( new Line2D.Double( 0, meters, Pool.WIDTH, meters ) ) );
            final PNode line2 = new PhetPPath( c, new BasicStroke( 1 ), Color.darkGray );
            addChild( line1 );
            addChild( line2 );
            PhetPText text = new PhetPText( label, new PhetFont( 16, true ) );
            text.setOffset( line1.getFullBounds().getX() - text.getFullBounds().getWidth(), line1.getFullBounds().getCenterY() - text.getFullHeight() / 2 );
            addChild( text );
        }
    }
}
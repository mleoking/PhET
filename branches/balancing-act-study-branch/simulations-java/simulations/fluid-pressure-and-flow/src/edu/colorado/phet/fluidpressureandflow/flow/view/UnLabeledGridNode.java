// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.READOUT_FEET;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.READOUT_METERS;
import static java.text.MessageFormat.format;

/**
 * Shows a grid under the water with horizontal lines only so the user can see the water depth.
 *
 * @author Sam Reid
 */
public class UnLabeledGridNode extends PropertyVisibleNode {
    public UnLabeledGridNode( ObservableProperty<Boolean> visible, final ModelViewTransform transform, Property<UnitSet> units ) {
        super( visible );

        //Show meters if in metric
        final int labelX = 665;
        addChild( new PropertyVisibleNode( units.valueEquals( UnitSet.METRIC ) ) {{
            for ( double i = 0; i >= -10; i -= 0.5 ) {
                final TwoToneLineNode lineNode = new TwoToneLineNode( i, transform );
                addChild( lineNode );
                if ( i == -3 ) {
                    addChild( createLabel( lineNode, format( READOUT_METERS, new DecimalFormat( "0" ).format( -i ) ), labelX ) );
                }
            }
        }} );

        //Show feet if non-metric
        addChild( new PropertyVisibleNode( new Not( units.valueEquals( UnitSet.METRIC ) ) ) {{
            for ( int i = 0; i >= -10; i-- ) {
                final TwoToneLineNode lineNode = new TwoToneLineNode( Units.feetToMeters( i ), transform );
                addChild( lineNode );
                if ( i == -10 ) {
                    addChild( createLabel( lineNode, format( READOUT_FEET, new DecimalFormat( "0" ).format( -i ) ), labelX ) );
                }
            }
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    //Label the bottom tick mark
    private ControlPanelNode createLabel( final TwoToneLineNode lineNode, final String text, final int labelX ) {
        return new ControlPanelNode( new PhetPText( text, new PhetFont( 16 ), Color.black ), new Color( 103, 162, 87 ), null, null, 3 ) {{
            setOffset( labelX, lineNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }};
    }


    public static class TwoToneLineNode extends PNode {
        public TwoToneLineNode( final double meters, final ModelViewTransform transform ) {
            final PhetPPath line = new PhetPPath( transform.modelToView( new Line2D.Double( -100, meters, 100, meters ) ), new BasicStroke( 1.5f ), Color.lightGray );
            final Shape b = transform.modelToView( new Line2D.Double( -100, meters, 100, meters ) );
            Shape c = AffineTransform.getTranslateInstance( 0, 1 ).createTransformedShape( b );
            final PhetPPath line2 = new PhetPPath( c, new BasicStroke( 1 ), Color.darkGray );
            addChild( line );
            addChild( line2 );
        }
    }
}
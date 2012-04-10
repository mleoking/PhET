// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

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
                final LineNode lineNode = new LineNode( i, transform );
                addChild( lineNode );
                if ( i == -3 ) {
                    createLabel( lineNode, "-3 meters", labelX );
                }
            }
        }} );

        //Show feet if non-metric
        addChild( new PropertyVisibleNode( new Not( units.valueEquals( UnitSet.METRIC ) ) ) {{
            for ( int i = 0; i >= -10; i-- ) {
                final LineNode lineNode = new LineNode( Units.feetToMeters( i ), transform );
                addChild( lineNode );
                if ( i == -10 ) {
                    createLabel( lineNode, "-10 feet", labelX );
                }
            }
        }} );
        setPickable( false );
        setChildrenPickable( false );
    }

    //Label the bottom tick mark
    private void createLabel( final LineNode lineNode, final String text, final int labelX ) {
        addChild( new ControlPanelNode( new PhetPText( text, new PhetFont( 16 ), Color.black ), new Color( 103, 162, 87 ), null, null, 3 ) {{
            setOffset( labelX, lineNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }

    public static class LineNode extends PNode {
        public LineNode( final double meters, final ModelViewTransform transform ) {
            final PhetPPath line = new PhetPPath( transform.modelToView( new Line2D.Double( -100, meters, 100, meters ) ), new BasicStroke( 1 ), Color.lightGray );
            addChild( line );
        }
    }
}
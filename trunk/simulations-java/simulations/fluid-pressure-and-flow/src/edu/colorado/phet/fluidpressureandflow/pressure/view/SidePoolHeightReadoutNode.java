// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.VALUE_WITH_UNITS_PATTERN;

/**
 * @author Sam Reid
 */
public class SidePoolHeightReadoutNode extends PNode {
    public SidePoolHeightReadoutNode( final ModelViewTransform transform, final Pool pool, final Property<UnitSet> units ) {
        final PhetPPath bracket = new PhetPPath( new BasicStroke( 1 ), Color.black );
        final PText text = new PText() {{
            setFont( new PhetFont( 16, true ) );
        }};

        units.addObserver( new SimpleObserver() {
            public void update() {
                DecimalFormat format = new DecimalFormat( "0.000" );
                if ( units.get().distance == Units.FEET ) {
                    format = new DecimalFormat( "0" );
                }
                text.setText( MessageFormat.format( VALUE_WITH_UNITS_PATTERN, format.format( units.get().distance.siToUnit( pool.getHeight() ) ), units.get().distance.getAbbreviation() ) );
                bracket.setPathTo( new DoubleGeneralPath() {{
                    moveTo( transform.modelToView( pool.getTopRight() ) );
                    moveToRelative( 5, 0 );
                    final double bracketInset = 10;
                    lineToRelative( bracketInset, 0 );
                    lineToRelative( 0, -transform.modelToViewDeltaY( pool.getHeight() ) );
                    lineToRelative( -bracketInset, 0 );
                }}.getGeneralPath() );
                text.setOffset( bracket.getFullBounds().getMaxX(), bracket.getFullBounds().getCenterY() - text.getFullBounds().getHeight() / 2 );
            }
        } );


        addChild( bracket );
        addChild( text );
    }
}

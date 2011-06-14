// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.workenergy.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class EnergyLegend extends PNode {
    public EnergyLegend( final Property<Boolean> visible ) {
        addChild( new PText( "Kinetic Energy" ) {{
            setTextPaint( PhetColorScheme.KINETIC_ENERGY );
            setFont( new PhetFont( 20, true ) );
        }} );
        addChild( new PText( "Potential Energy" ) {{
            setTextPaint( PhetColorScheme.POTENTIAL_ENERGY );
            setFont( new PhetFont( 20, true ) );
            setOffset( 0, EnergyLegend.this.getFullBounds().getHeight() );
        }} );
        addChild( new PText( "Thermal Energy" ) {{
            setTextPaint( PhetColorScheme.HEAT_THERMAL_ENERGY );
            setFont( new PhetFont( 20, true ) );
            setOffset( 0, EnergyLegend.this.getFullBounds().getHeight() );
        }} );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.get() );
            }
        } );
        double inset = 4;
        final PhetPPath child = new PhetPPath( new RoundRectangle2D.Double( -inset, -inset, getFullBounds().getWidth() + inset * 2, getFullBounds().getHeight() + inset * 2, 10, 10 ), Color.white, new BasicStroke( 1 ), Color.darkGray );
        addChild( child );
        child.moveToBack();
    }
}

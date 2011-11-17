// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class NumberLineNode extends PNode {
    public NumberLineNode( Property<Integer> numerator, final Property<Integer> denominator, ValueEquals<ChosenRepresentation> showing ) {
        scale( 5 );
        showing.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                setVisible( aBoolean );
            }
        } );

        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();

                double dx = 5;
                addChild( new PhetPPath( new Line2D.Double( 0, 0, dx * 10, 0 ) ) );

                int divisions = denominator.get();

                for ( int i = 0; i <= 30; i++ ) {
                    if ( i % divisions == 0 ) {
                        final PhetPPath path = new PhetPPath( new Line2D.Double( i * dx, -10, i * dx, 10 ), new BasicStroke( 1 ), Color.black );
                        addChild( path );
                        if ( i == 0 ) {
                            addChild( new PhetPText( "0", new PhetFont( 10 ) ) {{
                                setOffset( path.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, path.getFullBounds().getMaxY() );
                            }} );
                        }
                    }
                    else {
                        addChild( new PhetPPath( new Line2D.Double( i * dx, -5, i * dx, 5 ), new BasicStroke( 1 ), Color.black ) );
                    }
                }
            }
        }.observe( numerator, denominator );
    }
}

// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class CarouselDotComponent extends PNode {
    public CarouselDotComponent( final IntegerProperty selectedPage ) {
        final int circleDiameter = 16;
        addChild( new HBox( new VBox( new PhetPText( "1-5", new PhetFont( 15 ) ) {{
            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setTextPaint( integer == 0 ? Color.black : Color.gray );
                }
            } );
        }}, new PhetPPath( new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), new BasicStroke( 2 ), Color.gray ) {{
            selectedPage.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer integer ) {
                    setPaint( integer == 0 ? Color.black : BuildAFractionCanvas.TRANSPARENT );
                }
            } );
        }}
        ),
                            new VBox( new PhetPText( "6-10", new PhetFont( 15 ) ) {{
                                selectedPage.addObserver( new VoidFunction1<Integer>() {
                                    public void apply( final Integer integer ) {
                                        setTextPaint( integer == 1 ? Color.black : Color.gray );
                                    }
                                } );
                            }}, new PhetPPath( new Ellipse2D.Double( 0, 0, circleDiameter, circleDiameter ), new BasicStroke( 2 ), Color.gray ) {{
                                selectedPage.addObserver( new VoidFunction1<Integer>() {
                                    public void apply( final Integer integer ) {
                                        setPaint( integer == 1 ? Color.black : BuildAFractionCanvas.TRANSPARENT );
                                    }
                                } );
                            }}
                            ) ) );
    }
}
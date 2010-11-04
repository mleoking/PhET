package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class MassReadoutNode extends PNode {
    public MassReadoutNode( final Body body, final ModelViewTransform2D modelViewTransform2D, final Property<Boolean> visible ) {
        addChild( new PText( "1 million Planet masses" ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.white );
            body.getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    double massKG = body.getMass();
                    double planetMasses = massKG / GravityAndOrbitsModel.PLANET_MASS;
//                    System.out.println( "planetMasses = " + planetMasses );
                    String text;
                    DecimalFormat decimalFormat = new DecimalFormat( "0" );
                    if ( planetMasses > 1E3 ) {
                        text = decimalFormat.format( planetMasses / 1E3 ) + " thousand planet masses";
                    }
                    else if ( Math.abs( planetMasses - 1 ) < 1E-2 ) {
                        text = 1 + " planet mass";
                    }
                    else if ( planetMasses < 1 ) {
                        text = new DecimalFormat( "0.000" ).format( planetMasses ) + " planet masses";
                    }
                    else {
                        text = decimalFormat.format( planetMasses ) + " planet masses";
                    }
                    setText( text );
                }
            } );
        }} );
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                final Point2D centroid = modelViewTransform2D.modelToViewDouble( new ImmutableVector2D( body.getPosition().getX(), body.getPosition().getY() + body.getRadius() ) );
                setOffset( centroid.getX() - getFullBounds().getWidth() / 2, centroid.getY() + 5 );
            }
        } );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
    }

}

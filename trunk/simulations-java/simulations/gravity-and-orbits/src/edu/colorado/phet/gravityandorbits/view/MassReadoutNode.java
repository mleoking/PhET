package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class MassReadoutNode extends PNode {
    public MassReadoutNode( final Body body, final BodyNode bodyNode, final Property<ModelViewTransform> modelViewTransform, final Property<Boolean> visible ) {
        addChild( new PText( "1 million Planet masses" ) {{
            setPickable( false );
            setChildrenPickable( false );
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.white );
            body.getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    double massKG = body.getMass();
                    double planetMasses = massKG / GravityAndOrbitsModule.PLANET_MASS;
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
        bodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                setOffset( bodyNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getMaxY() );
            }
        } );
        visible.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( visible.getValue() );
            }
        } );
    }

}

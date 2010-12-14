package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class EarthMassReadoutNode extends PNode {
    public EarthMassReadoutNode( final Body body, final BodyNode bodyNode, final Property<Boolean> visible ) {
        addChild( new PText( "1 million Earth masses" ) {{
            setPickable( false );
            setChildrenPickable( false );
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.white );
            body.getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    double massKG = body.getMass();
                    double earthMasses = massKG / GravityAndOrbitsModule.EARTH_MASS;
                    String text;
                    DecimalFormat decimalFormat = new DecimalFormat( "0" );
                    if ( earthMasses > 1E3 ) {
                        text = decimalFormat.format( earthMasses / 1E3 ) + " thousand Earth masses";
                    }
                    else if ( Math.abs( earthMasses - 1 ) < 1E-2 ) {
                        text = 1 + " Earth mass";
                    }
                    else if ( earthMasses < 1 ) {
                        text = new DecimalFormat( "0.000" ).format( earthMasses ) + " Earth masses";
                    }
                    else {
                        text = decimalFormat.format( earthMasses ) + " Earth masses";
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

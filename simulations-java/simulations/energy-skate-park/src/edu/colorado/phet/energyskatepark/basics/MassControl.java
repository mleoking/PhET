// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.skaterMassSlider;

/**
 * Control PNode for setting and viewing the skater's mass.
 *
 * @author Sam Reid
 */
public class MassControl extends PNode {
    public MassControl( final EnergySkateParkBasicsModule module ) {
        final int MIN_MASS = 60;
        final int MAX_MASS = 100;
        addChild( new VBox( 10, new PhetPText( EnergySkateParkResources.getString( "skater.mass" ), EnergySkateParkBasicsModule.TITLE_FONT ),
                            new HSliderNode( skaterMassSlider, MIN_MASS, MAX_MASS, 5, 90, module.mass, new Property<Boolean>( true ) ) {{
                                // Override the gradient and fill with white.  The gradient
                                // just looked weird.
                                setTrackFillPaint( Color.white );
                                addLabel( min, new PText( EnergySkateParkResources.getString( "small" ) ) );
                                addLabel( max, new PText( EnergySkateParkResources.getString( "large" ) ) );
                            }} ) );


        module.mass.addObserver( new VoidFunction1<Double>() {
            public void apply( Double m ) {
                EnergySkateParkModel model = module.getEnergySkateParkModel();
                for ( int i = 0; i < model.getNumBodies(); i++ ) {
                    model.getBody( i ).setMass( m );
                }
            }
        } );
    }
}
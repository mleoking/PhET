// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.util.Hashtable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.bendinglight.model.LRRModel;
import edu.colorado.phet.bendinglight.model.Medium;
import edu.colorado.phet.bendinglight.model.MediumState;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

/**
 * @author Sam Reid
 */
public class IndexOfRefractionSlider extends LinearValueControl {
    public IndexOfRefractionSlider( final Property<Medium> medium, final Property<Function1<Double, Color>> colorMappingFunction, String text ) {
        super( 1, 1.6, medium.getValue().getIndexOfRefraction(), text, "0.00", "", new HorizontalLayoutStrategy() );
        setSignifyOutOfBounds( false );
        setTickLabels( new Hashtable<Object, Object>() {{
            put( LRRModel.AIR.index, new TickLabel( "Air" ) );
            put( LRRModel.WATER.index, new TickLabel( "Water" ) );
            put( LRRModel.GLASS.index, new TickLabel( "Glass" ) );
//            put( LRRModel.N_DIAMOND, new TickLabel( "Diamond" ) );//commented out while we determine how to handle overlapping labels
        }} );
        getSlider().setMinorTickSpacing( 0 );
        getSlider().setMajorTickSpacing( 0 );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( getSlider().isFocusOwner() || getTextField().isFocusOwner() || isFocusOwner() ) {//Only send events if caused by user, otherwise selecting "mystery b" causes buggy behavior
                    medium.setValue( new Medium( medium.getValue().getShape(), new MediumState( "Custom", getValue() ), colorMappingFunction.getValue().apply( getValue() ) ) );
                }
            }
        } );
        medium.addObserver( new SimpleObserver() {
            public void update() {
                setValue( medium.getValue().getIndexOfRefraction() );
            }
        } );
    }
}

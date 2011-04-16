// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;

/**
 * Control for setting size of anything that has width and height properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SizeControl extends GridPanel {

    public SizeControl( String title, final Property<Integer> width, final Property<Integer> height ) {

        setBorder( new TitledBorder( title ) );

        // width slider
        setGridXY( 0, 0 );
        add( new JLabel( "width:" ) );
        setGridXY( 1, 0 );
        add( new PropertyIntegerSlider( 20, 600, width ) );

        // height slider
        setGridXY( 0, 1 );
        add( new JLabel( "height:" ) );
        setGridXY( 1, 1 );
        add( new PropertyIntegerSlider( 20, 600, height ) );
    }

    /**
     * Slider that controls a Property<Integer>.
     */
    private static class PropertyIntegerSlider extends JSlider {

        public PropertyIntegerSlider( int min, int max, final Property<Integer> property ) {
            super( min, max, property.getValue() );
            setMajorTickSpacing( max - min );
            setPaintTicks( true );
            setPaintLabels( true );

            // when the slider changes, update the property
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    property.setValue( getValue() );
                }
            } );

            // when the property changes, update the slider
            property.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( property.getValue() );//TODO in a production app, we'd make sure this value is in the slider's range
                }
            } );
        }
    }
}

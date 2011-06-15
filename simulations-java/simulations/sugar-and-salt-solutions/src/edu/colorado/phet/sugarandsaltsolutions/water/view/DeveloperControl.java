// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.PropertySlider;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;

/**
 * Developer controls for water model tab physics.
 *
 * @author Sam Reid
 */
public class DeveloperControl extends VerticalLayoutPanel {

    public DeveloperControl( final WaterModel waterModel ) {

        //Developer controls for physics settings,
        add( new JButton( "Add Water" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    waterModel.addWater( waterModel.getRandomX(), waterModel.getRandomY(), 0 );
                }
            } );
        }} );
        add( new JPanel() {{
            add( new JLabel( "k" ) );
            add( new PropertySlider( 0, 1000, waterModel.k ) );
            add( new PropertyJLabel( waterModel.k ) );
        }} );
        add( new JPanel() {{
            add( new JLabel( "pow" ) );
            add( new PropertySlider( 0, 10, waterModel.pow ) );
            add( new PropertyJLabel( waterModel.pow ) );
        }} );

        add( new JPanel() {{
            add( new JLabel( "rand" ) );
            add( new PropertySlider( 0, 100, waterModel.randomness ) );
            add( new PropertyJLabel( waterModel.randomness ) );
        }} );
    }

    private class PropertyJLabel extends JLabel {
        public PropertyJLabel( Property<Integer> k ) {
            super( k.get() + "" );
            k.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setText( integer + "" );
                }
            } );
        }
    }
}
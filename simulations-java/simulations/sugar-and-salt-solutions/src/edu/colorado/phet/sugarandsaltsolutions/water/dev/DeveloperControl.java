// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.dev;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.sugarandsaltsolutions.common.view.PropertySlider;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;

/**
 * Developer controls for water model tab physics.
 *
 * @author Sam Reid
 */
public class DeveloperControl extends JPanel {

    public DeveloperControl( final WaterModel waterModel ) {

        add( new VerticalLayoutPanel() {{
            //Developer controls for physics settings,
            add( new JPanel() {{
                add( new JButton( "Add Water" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            waterModel.addWater( waterModel.getRandomX(), waterModel.getRandomY(), 0 );
                        }
                    } );
                }} );
                add( new JLabel( "num waters:" ) );
                add( new DoubleLabel( waterModel.waterList.count ) );
            }} );
            add( new JLabel( "coulomb strength (k)" ) );
            add( new JPanel() {{
                add( new PropertySlider( 0, 1000, waterModel.k ) );
                add( new IntLabel( waterModel.k ) );
            }} );
            add( new JLabel( "coulomb power" ) );
            add( new JPanel() {{
                add( new PropertySlider( 0, 4, waterModel.pow ) );
                add( new IntLabel( waterModel.pow ) );
            }} );

            add( new JLabel( "model randomness" ) );
            add( new JPanel() {{
                add( new PropertySlider( 0, 100, waterModel.randomness ) );
                add( new IntLabel( waterModel.randomness ) );
            }} );

            add( new JPanel() {{
                add( new DoublePropertySlider( "min interaction dist", 0, 8, waterModel.minInteractionDistance ) );
            }} );

            add( new JPanel() {{
                add( new DoublePropertySlider( "max interaction dist", 0, 8, waterModel.maxInteractionDistance ) );
            }} );

            add( new JPanel() {{
                add( new DoublePropertySlider( "prob. of interaction", 0, 1, waterModel.probabilityOfInteraction ) );
            }} );

            add( new JPanel() {{
                add( new DoublePropertySlider( "time scale", 0, 1, waterModel.timeScale ) );
            }} );
            setBorder( new LineBorder( Color.black ) );
        }} );
        add( new JSeparator( SwingConstants.VERTICAL ) );

        add( new VerticalLayoutPanel() {{
            add( new JPanel() {{
                add( new DoublePropertySlider( "oxygen charge", -2, 0, waterModel.oxygenCharge ) );
            }} );
            add( new JPanel() {{
                add( new DoublePropertySlider( "hydrogen charge", 0, 1, waterModel.hydrogenCharge ) );
            }} );
            add( new JPanel() {{
                add( new DoublePropertySlider( "ion charge", 0, 3, waterModel.ionCharge ) );
            }} );

            add( new JLabel( "iterations per time step" ) );
            add( new JPanel() {{
                add( new PropertySlider( 0, 500, waterModel.iterations ) );
                add( new IntLabel( waterModel.iterations ) );
            }} );

            add( new PropertyRadioButton<Boolean>( "Coulomb forces between all particles", waterModel.coulombForceOnAllMolecules, true ) );
            add( new PropertyRadioButton<Boolean>( "Coulomb forces only on water", waterModel.coulombForceOnAllMolecules, false ) );
            setBorder( new LineBorder( Color.black ) );
        }} );
    }

    private class IntLabel extends JLabel {
        public IntLabel( Property<Integer> k ) {
            super( k.get() + "" );
            k.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setText( integer + "" );
                }
            } );
        }
    }

    private class DoubleLabel extends JLabel {
        public DoubleLabel( Property<Double> k ) {
            super( k.get() + "" );
            k.addObserver( new VoidFunction1<Double>() {
                public void apply( Double integer ) {
                    setText( integer + "" );
                }
            } );
        }
    }
}
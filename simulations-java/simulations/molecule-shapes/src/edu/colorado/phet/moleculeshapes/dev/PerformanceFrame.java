// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.dev;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;

public class PerformanceFrame extends JFrame {
    private final MoleculeJMEApplication app;

    public PerformanceFrame( MoleculeJMEApplication app ) throws HeadlessException {
        super( "Performance" );
        this.app = app;

        JPanel container = new JPanel( new GridBagLayout() );
        setContentPane( container );

        container.add( new JLabel( "Target FPS" ), new GridBagConstraints() );
        container.add( new FrameRateButton( 60 ), new GridBagConstraints() );
        container.add( new FrameRateButton( 20 ), new GridBagConstraints() );
        container.add( new FrameRateButton( 5 ), new GridBagConstraints() );

        container.add( new JLabel( "Antialiasing Samples (not working?)" ), new GridBagConstraints() {{gridy = 1;}} );
        container.add( new AntiAliasingButton( 0 ), new GridBagConstraints() {{gridy = 1;}} );
        if ( JmeUtils.maxAllowedSamples > 0 && JmeUtils.maxAllowedSamples < 4 ) {
            container.add( new AntiAliasingButton( JmeUtils.maxAllowedSamples ), new GridBagConstraints() {{gridy = 1;}} );
        }
        else {
            container.add( new AntiAliasingButton( 4 ), new GridBagConstraints() {{gridy = 1;}} );
            if ( JmeUtils.maxAllowedSamples > 4 ) {
                container.add( new AntiAliasingButton( JmeUtils.maxAllowedSamples ), new GridBagConstraints() {{gridy = 1;}} );
            }
        }

        container.add( new JLabel( "" ) {{
                           MoleculeShapesProperties.sphereSamples.addObserver( new SimpleObserver() {
                               public void update() {
                                   setText( "Atom samples: " + MoleculeShapesProperties.sphereSamples.get() );
                               }
                           } );
                       }}, new GridBagConstraints() {{gridy = 2;}}
        );
        container.add( new PropertyIntegerSlider( 3, 50, MoleculeShapesProperties.sphereSamples ), new GridBagConstraints() {{gridy = 2;}} );

        container.add( new JLabel( "" ) {{
                           MoleculeShapesProperties.cylinderSamples.addObserver( new SimpleObserver() {
                               public void update() {
                                   setText( "Bond samples: " + MoleculeShapesProperties.cylinderSamples.get() );
                               }
                           } );
                       }}, new GridBagConstraints() {{gridy = 3;}}
        );
        container.add( new PropertyIntegerSlider( 3, 50, MoleculeShapesProperties.cylinderSamples ), new GridBagConstraints() {{gridy = 3;}} );

        pack();
        setVisible( true );
    }

    private class FrameRateButton extends JButton {
        public FrameRateButton( final int frameRate ) {
            super( frameRate + "" );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    synchronized ( app ) {
                        JmeUtils.frameRate.set( frameRate );
                    }
                }
            } );
        }
    }

    private class AntiAliasingButton extends JButton {
        public AntiAliasingButton( final int samples ) {
            super( samples + "" );

            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    synchronized ( app ) {
                        JmeUtils.antiAliasingSamples.set( samples );
                    }
                }
            } );
        }
    }

    // TODO: copied from CM's code. remove or integrate before completion
    private class PropertyIntegerSlider extends JSlider {

        public PropertyIntegerSlider( int min, int max, final Property<Integer> property ) {
            super( min, max, property.get() );
            setMajorTickSpacing( max - min );
            setPaintTicks( true );
            setPaintLabels( true );

            // when the slider changes, update the property
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    synchronized ( app ) {
                        property.set( getValue() );
                    }
                }
            } );

            // when the property changes, update the slider
            property.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( property.get() );//TODO in a production app, we'd make sure this value is in the slider's range
                }
            } );
        }

        public void cleanup() {
            //TODO call property.removeObserver to prevent memory leak
        }
    }
}

/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.single;

import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.controls.DetectorPanel;
import edu.colorado.phet.qm.controls.SchrodingerControlPanel;
import edu.colorado.phet.qm.model.Detector;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.colormaps.ImaginaryGrayColorMap;
import edu.colorado.phet.qm.view.colormaps.RealGrayColorMap;
import edu.colorado.phet.qm.view.colormaps.VisualColorMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:42 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleControlPanel extends SchrodingerControlPanel {
    public SingleParticleControlPanel( SingleParticleModule singleParticleModule ) {
        super( singleParticleModule );
        VerticalLayoutPanel detectorPanel = new DetectorPanel( singleParticleModule );
        addControlFullWidth( detectorPanel );

        VerticalLayoutPanel exp = createExpectationPanel();
        addControlFullWidth( exp );

        JButton createDetectorArray = new JButton( "Create Detector Array" );
        createDetectorArray.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                createDetectorArray();
            }
        } );
        addControl( createDetectorArray );

        final ModelSlider modelSlider = new ModelSlider( "Dectector prob-scale", "", 0, 100, 1 );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Detector.setProbabilityScaleFudgeFactor( modelSlider.getValue() );
            }
        } );
        modelSlider.setModelTicks( new double[]{0, 50, 100} );
        addControl( modelSlider );

        VerticalLayoutPanel colorPanel = createVisualizationPanel();
        addControlFullWidth( colorPanel );
    }


    private VerticalLayoutPanel createVisualizationPanel() {
        VerticalLayoutPanel colorPanel = new VerticalLayoutPanel();
        colorPanel.setBorder( BorderFactory.createTitledBorder( "Color Scheme" ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton grayMag = createVisualizationButton( "Magnitude", getSchrodingerPanel().getWavefunctionGraphic().getMagnitudeMap(), true, buttonGroup );
        colorPanel.addFullWidth( grayMag );

        JRadioButton realGray = createVisualizationButton( "Real", new RealGrayColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( realGray );

        JRadioButton complexGray = createVisualizationButton( "Imaginary", new ImaginaryGrayColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( complexGray );

        JRadioButton visualTM = createVisualizationButton( "Rainbow", new VisualColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( visualTM );

//        JRadioButton blackBackground = createVisualizationButton( "HSB on Black", new DefaultColorMap( getSchrodingerPanel() ), false, buttonGroup );
//        colorPanel.addFullWidth( blackBackground );
//
//        JRadioButton whiteBackground = createVisualizationButton( "HSB on White", new DefaultWhiteColorMap( getSchrodingerPanel() ), false, buttonGroup );
//        colorPanel.addFullWidth( whiteBackground );
        return colorPanel;
    }

    private JRadioButton createVisualizationButton( String s, final ColorMap colorMap, boolean b, ButtonGroup buttonGroup ) {
        JRadioButton radioButton = new JRadioButton( s );
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setWavefunctionColorMap( colorMap );
            }
        } );
        buttonGroup.add( radioButton );
        radioButton.setSelected( b );
        return radioButton;
    }


    private void createDetectorArray() {
        createDetectorArray( 20 );
    }

    private void createDetectorArray( int width ) {
        int height = width;
        int nx = getDiscreteModel().getGridWidth() / width;
        int ny = getDiscreteModel().getGridHeight() / height;

        for( int i = 0; i < nx; i++ ) {
            for( int j = 0; j < ny; j++ ) {
                int x = i * width;
                int y = j * height;
                Detector detector = new Detector( getDiscreteModel(), x, y, width, height );
                getSchrodingerPanel().getSchrodingerModule().addDetector( detector );
            }

        }
    }


    private VerticalLayoutPanel createExpectationPanel() {
        VerticalLayoutPanel lay = new VerticalLayoutPanel();
        lay.setBorder( BorderFactory.createTitledBorder( "Observables" ) );
        final JCheckBox x = new JCheckBox( "<X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( x.isSelected() );
            }
        } );
        lay.add( x );

        final JCheckBox y = new JCheckBox( "<Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( y.isSelected() );
            }
        } );
        lay.add( y );

        final JCheckBox c = new JCheckBox( "collapse-to" );
        c.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayCollapsePoint( c.isSelected() );
            }
        } );
        lay.add( c );

        return lay;
    }
}

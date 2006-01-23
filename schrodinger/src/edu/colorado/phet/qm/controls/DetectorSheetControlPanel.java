/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.SavedScreenGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 12:54:31 PM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class DetectorSheetControlPanel extends VerticalLayoutPanel {
    private JButton clearButton;
    private DetectorSheetPNode detectorSheetPNode;
    private JButton saveScreenJButton;
    private ModelSlider brightnessModelSlider;
    private JCheckBox fadeCheckbox;
    private HorizontalLayoutPanel displayPanel;

    public DetectorSheetControlPanel( final DetectorSheetPNode detectorSheetPNode ) {
//        setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
//        setBorder( BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ), BorderFactory.createBevelBorder( BevelBorder.RAISED ) ) );
        this.detectorSheetPNode = detectorSheetPNode;
        clearButton = new JButton( "Clear" );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                detectorSheetPNode.reset();
            }
        } );

        saveScreenJButton = new JButton( "Copy Screen" );
        saveScreenJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BufferedImage image = detectorSheetPNode.copyScreen();
                SavedScreenGraphic savedScreenGraphic = new SavedScreenGraphic( getSchrodingerPanel(), image );
                savedScreenGraphic.setOffset( 130, 130 );
                getSchrodingerPanel().getSchrodingerScreenNode().addChild( savedScreenGraphic );
            }
        } );

        brightnessModelSlider = new ModelSlider( "Screen Brightness", "", 0, 1.0, 0.2, new DecimalFormat( "0.000" ) );
        brightnessModelSlider.setModelTicks( new double[]{0, 0.25, 0.5, 0.75, 1.0} );
        this.brightnessModelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setBrightness();
            }
        } );
        setBrightness();

        fadeCheckbox = new JCheckBox( "Fade", getSchrodingerPanel().isFadeEnabled() );
        fadeCheckbox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getSchrodingerPanel().setFadeEnabled( fadeCheckbox.isSelected() );
            }
        } );
        getSchrodingerPanel().addListener( new SchrodingerPanel.Adapter() {
            public void fadeStateChanged() {
                fadeCheckbox.setSelected( getSchrodingerPanel().isFadeEnabled() );
            }
        } );

        displayPanel = new HorizontalLayoutPanel();
        displayPanel.setBorder( BorderFactory.createTitledBorder( "Display" ) );

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton showHits = new JRadioButton( "Hits", !HighIntensitySchrodingerPanel.SMOOTH_SCREEN_DEFAULT );
        JRadioButton showAverage = new JRadioButton( "Average Intensity", HighIntensitySchrodingerPanel.SMOOTH_SCREEN_DEFAULT );

        buttonGroup.add( showAverage );
        buttonGroup.add( showHits );

        showHits.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSmoothScreen( false );
            }
        } );
        showAverage.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setSmoothScreen( true );
            }
        } );

        displayPanel.add( showAverage );
        displayPanel.add( showHits );

        HorizontalLayoutPanel saveClear = new HorizontalLayoutPanel();
        saveClear.setBorder( BorderFactory.createTitledBorder( "Screen" ) );
        saveClear.add( fadeCheckbox );
        saveClear.add( clearButton );
        saveClear.add( saveScreenJButton );
        add( saveClear );
        add( brightnessModelSlider );
        add( displayPanel );
//        saveClear.setForeground( Color.blue);
    }

    public void setBrightness() {
        detectorSheetPNode.setBrightness( brightnessModelSlider.getValue() );
        if( getIntensityPanel() != null && getIntensityPanel().getSmoothIntensityDisplay() != null ) {
            getIntensityPanel().getSmoothIntensityDisplay().setBrightness( brightnessModelSlider.getValue() );
        }
    }

    private void setSmoothScreen( boolean b ) {
        if( getIntensityPanel() != null ) {
            getIntensityPanel().setSmoothScreen( b );
        }
    }

    private HighIntensitySchrodingerPanel getIntensityPanel() {
        if( detectorSheetPNode.getSchrodingerPanel() instanceof HighIntensitySchrodingerPanel ) {
            return (HighIntensitySchrodingerPanel)detectorSheetPNode.getSchrodingerPanel();
        }
        return null;
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return detectorSheetPNode.getSchrodingerPanel();
    }

    public void setClearButtonVisible( boolean b ) {
        clearButton.setVisible( b );
    }

    public void setSaveButtonVisible( boolean b ) {
        saveScreenJButton.setVisible( b );
        supervalidate();
    }

    public void setBrightnessSliderVisible( boolean b ) {
        brightnessModelSlider.setVisible( b );
    }

    public void setFadeCheckBoxVisible( boolean b ) {
        fadeCheckbox.setVisible( b );
    }

    private void supervalidate() {
        validate();
    }

    public void setTypeControlVisible( boolean b ) {
        displayPanel.setVisible( b );
        supervalidate();
    }
}

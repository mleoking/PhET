/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.swing;

import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityPanel;
import edu.colorado.phet.qm.view.piccolo.DetectorSheet;
import edu.colorado.phet.qm.view.piccolo.SavedScreenGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
    private Insets buttonInsets = new Insets( 2, 2, 2, 2 );
    private Font buttonFont = new Font( "Lucida Sans", Font.BOLD, 10 );
    private DetectorSheet detectorSheet;
    private JButton saveScreenJButton;
    private ModelSlider brightnessModelSlider;
    private JCheckBox fadeEnabled;
    private HorizontalLayoutPanel displayPanel;

    public DetectorSheetControlPanel( final DetectorSheet detectorSheet ) {
        this.detectorSheet = detectorSheet;
        clearButton = new JButton( "Clear" );
        clearButton.setMargin( buttonInsets );
        clearButton.setFont( buttonFont );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                detectorSheet.reset();
            }
        } );

        saveScreenJButton = new JButton( "Save" );
        saveScreenJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BufferedImage image = detectorSheet.copyScreen();
                SavedScreenGraphic savedScreenGraphic = new SavedScreenGraphic( getSchrodingerPanel(), image );
                savedScreenGraphic.setOffset( 130, 130 );
                getSchrodingerPanel().getScreenNode().addChild( savedScreenGraphic );
            }
        } );
        saveScreenJButton.setMargin( buttonInsets );
        saveScreenJButton.setFont( buttonFont );

        brightnessModelSlider = new ModelSlider( "Screen Brightness", "", 0, 1.0, 0.2, new DecimalFormat( "0.000" ) );
        brightnessModelSlider.setModelTicks( new double[]{0, 0.25, 0.5, 0.75, 1.0} );
        this.brightnessModelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setBrightness();
            }
        } );
        setBrightness();

        fadeEnabled = new JCheckBox( "Fade", true );
        fadeEnabled.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getSchrodingerPanel().setFadeEnabled( fadeEnabled.isSelected() );
            }
        } );

        displayPanel = new HorizontalLayoutPanel();
        displayPanel.setBorder( BorderFactory.createTitledBorder( "Display" ) );

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton showHits = new JRadioButton( "Hits", true );
        JRadioButton showAverage = new JRadioButton( "Average Intensity" );
        buttonGroup.add( showHits );
        buttonGroup.add( showAverage );

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

        displayPanel.add( showHits );
        displayPanel.add( showAverage );

        HorizontalLayoutPanel saveClear = new HorizontalLayoutPanel();
        saveClear.setBorder( BorderFactory.createTitledBorder( "Screen" ) );
        saveClear.add( saveScreenJButton );
        saveClear.add( clearButton );

        saveClear.add( fadeEnabled );
        add( saveClear );
        add( brightnessModelSlider );
        add( displayPanel );

//        brightnessModelSlider.setVisible( false );
//        displayPanel.setVisible( false );
    }

    public void setBrightness() {
        detectorSheet.setBrightness( brightnessModelSlider.getValue() );
        if( getIntensityPanel() != null && getIntensityPanel().getSmoothIntensityDisplay() != null ) {
            getIntensityPanel().getSmoothIntensityDisplay().setBrightness( brightnessModelSlider.getValue() );
        }
    }

    private void setSmoothScreen( boolean b ) {
        if( getIntensityPanel() != null ) {
            getIntensityPanel().setSmoothScreen( b );
        }
    }

    private IntensityPanel getIntensityPanel() {
        if( detectorSheet.getSchrodingerPanel() instanceof IntensityPanel ) {
            IntensityPanel intensityPanel = (IntensityPanel)detectorSheet.getSchrodingerPanel();
            return intensityPanel;
        }
        return null;
    }

    protected void putBelow( PNode obj, PNode p, int insetY ) {
        PBounds parent = p.getFullBounds();
        obj.setOffset( parent.getX(), parent.getY() + parent.getHeight() + insetY );
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return detectorSheet.getSchrodingerPanel();
    }

    public void setClearButtonVisible( boolean b ) {
        clearButton.setVisible( true );
    }

    public void setSaveButtonVisible( boolean b ) {
        saveScreenJButton.setVisible( b );
        supervalidate();
    }

    public void setBrightnessSliderVisible( boolean b ) {
        brightnessModelSlider.setVisible( b );
        supervalidate();
    }

    public void setFadeCheckBoxVisible( boolean b ) {
        fadeEnabled.setVisible( b );
        supervalidate();
    }

    private void supervalidate() {
        invalidate();
        validate();
        validateTree();
        doLayout();
    }

    public void setTypeControlVisible( boolean b ) {
        displayPanel.setVisible( b );
        supervalidate();
    }
}

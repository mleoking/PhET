/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.modules.intensity.IntensityPanel;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 12:54:31 PM
 * Copyright (c) Jul 27, 2005 by Sam Reid
 */

public class DetectorSheetControlPanelPNode extends PNode {
    private JButton clearButton;
    private Insets buttonInsets = new Insets( 2, 2, 2, 2 );
    private Font buttonFont = new Font( "Lucida Sans", Font.BOLD, 10 );
    private DetectorSheet detectorSheet;
    private JButton saveScreenJButton;
    private ModelSlider brightnessModelSlider;
    private JCheckBox fadeEnabled;
    private PSwing brightnessGraphic;
    private PSwing fadeGraphic;
    private PSwing display;
    private PSwing saveClearGraphic;
    private HorizontalLayoutPanel displayPanel;

    public DetectorSheetControlPanelPNode( final DetectorSheet detectorSheet ) {
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
                //todo piccolo
//                BufferedImage image = detectorSheet.copyScreen();
//                SavedScreenGraphic savedScreenGraphic = new SavedScreenGraphic( getSchrodingerPanel(), image );
//
//                savedScreenGraphic.setLocation( 130, 130 );
//                getSchrodingerPanel().addWorldChild( savedScreenGraphic );
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

        saveClearGraphic = new PSwing( getSchrodingerPanel(), saveClear );
        brightnessGraphic = new PSwing( getSchrodingerPanel(), brightnessModelSlider );
        fadeGraphic = new PSwing( getSchrodingerPanel(), fadeEnabled );

        display = new PSwing( getSchrodingerPanel(), displayPanel );

        addChild( saveClearGraphic );
        addChild( brightnessGraphic );
        addChild( fadeGraphic );
        addChild( display );

        putBelow( brightnessGraphic, saveClearGraphic, 1 );
        fadeGraphic.setOffset( saveClearGraphic.getX() + saveClearGraphic.getWidth() + 2, saveClearGraphic.getY() + saveClearGraphic.getHeight() / 2 - fadeGraphic.getHeight() / 2 );
        putBelow( display, brightnessGraphic, 1 );

        brightnessGraphic.setVisible( false );
        setFadeCheckBoxVisible( false );
        display.setVisible( false );
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

    protected void putBelow( PNode obj, PNode parent, int insetY ) {
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
    }

    public void setBrightnessSliderVisible( boolean b ) {
        brightnessGraphic.setVisible( b );
    }

    public void setFadeCheckBoxVisible( boolean b ) {
        fadeGraphic.setVisible( b );
    }

    public void setTypeControlVisible( boolean b ) {
        display.setVisible( b );
    }
}

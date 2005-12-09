/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.swing;

import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityPanel;
import edu.colorado.phet.qm.view.piccolo.DetectorSheetPNode;
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
import java.util.Arrays;

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
    private DetectorSheetPNode detectorSheetPNode;
    private JButton saveScreenJButton;
    private ModelSlider brightnessModelSlider;
    private JCheckBox fadeEnabled;
    private HorizontalLayoutPanel displayPanel;

    public DetectorSheetControlPanel( final DetectorSheetPNode detectorSheetPNode ) {
        this.detectorSheetPNode = detectorSheetPNode;
        clearButton = new JButton( "Clear" );
        clearButton.setMargin( buttonInsets );
        clearButton.setFont( buttonFont );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                detectorSheetPNode.reset();
            }
        } );

        saveScreenJButton = new JButton( "Save" );
        saveScreenJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BufferedImage image = detectorSheetPNode.copyScreen();
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
        JRadioButton showHits = new JRadioButton( "Hits", !IntensityPanel.SMOOTH_SCREEN_DEFAULT );
        JRadioButton showAverage = new JRadioButton( "Average Intensity", IntensityPanel.SMOOTH_SCREEN_DEFAULT );

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
        saveClear.add( fadeEnabled );
        saveClear.add( clearButton );
        saveClear.add( saveScreenJButton );
        add( saveClear );
        add( brightnessModelSlider );
        add( displayPanel );

//
//        brightnessModelSlider.setVisible( false );
//        displayPanel.setVisible( false );
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

    private IntensityPanel getIntensityPanel() {
        if( detectorSheetPNode.getSchrodingerPanel() instanceof IntensityPanel ) {
            IntensityPanel intensityPanel = (IntensityPanel)detectorSheetPNode.getSchrodingerPanel();
            return intensityPanel;
        }
        return null;
    }

    protected void putBelow( PNode obj, PNode p, int insetY ) {
        PBounds parent = p.getFullBounds();
        obj.setOffset( parent.getX(), parent.getY() + parent.getHeight() + insetY );
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return detectorSheetPNode.getSchrodingerPanel();
    }

    boolean contains( Component c ) {
        return Arrays.asList( getComponents() ).contains( c );
    }

    public void setClearButtonVisible( boolean b ) {
        clearButton.setVisible( b );
//        setVisibleByContainment( b, clearButton );
    }

    private void setVisibleByContainment( boolean b, Component component ) {
        if( b && !contains( component ) ) {
            add( component );
        }
        else if( !b && contains( component ) ) {
            remove( component );
        }
        supervalidate();
    }

    public void setSaveButtonVisible( boolean b ) {
        saveScreenJButton.setVisible( b );
        supervalidate();
    }

    public void setBrightnessSliderVisible( boolean b ) {
        brightnessModelSlider.setVisible( b );
    }

    public void setFadeCheckBoxVisible( boolean b ) {
        fadeEnabled.setVisible( b );
    }

    private void supervalidate() {
        validate();
    }

    public void setTypeControlVisible( boolean b ) {
        displayPanel.setVisible( b );
        supervalidate();
    }
}

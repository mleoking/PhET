/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.RescaleOp2;
import edu.colorado.phet.ec2.common.measuringtape.MeasuringTapeInteractiveGraphic;
import edu.colorado.phet.ec2.elements.DeveloperPanel;
import edu.colorado.phet.ec2.elements.scene.Scene;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 31, 2003
 * Time: 1:24:33 PM
 * Copyright (c) Jul 31, 2003 by Sam Reid
 */
public class ECControlPanel extends JPanel {
    private EC2Module module;
    private DeveloperPanel developerPanel;
    private JRadioButton lots;
    private JRadioButton few;
    private JButton clearHistory;
    private JCheckBox activeHistory;
    private JCheckBox autoscale;
    private JComboBox planetComboBox;

    public ECControlPanel( final EC2Module module ) {
        this.module = module;

        autoscale = new JCheckBox( "Autoscale", false );
        autoscale.setEnabled( false );
        autoscale.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setAutoScale( autoscale.isSelected() );
            }
        } );
        autoscale.setBorderPainted( true );
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 0;
        gbc.gridheight = 100;
        gbc.fill = GridBagConstraints.CENTER;

        clearHistory = new JButton( "Clear History" );
        clearHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.clearHistory();
            }
        } );

        activeHistory = new JCheckBox( "Record", module.isHistoryActive() );
        activeHistory.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clickHistory();
            }
        } );

        activeHistory.setBorderPainted( true );

        JPanel historyPanel = new JPanel();

        lots = new JRadioButton( "Lots", true );
        lots.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyDotDT( 0 );
            }
        } );
        few = new JRadioButton( "Few", false );
        few.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyDotDT( .1 );
            }
        } );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( lots );
        buttonGroup.add( few );

        historyPanel.add( activeHistory );
        historyPanel.add( few );
        historyPanel.add( lots );
        historyPanel.add( clearHistory );

        historyPanel.setLayout( new BoxLayout( historyPanel, BoxLayout.Y_AXIS ) );
        historyPanel.setBorder( BorderFactory.createTitledBorder( "History" ) );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );

        final JCheckBox chartVisible = new JCheckBox( "Show", module.isChartVisible() );
        chartVisible.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setEnergyChartVisible( chartVisible.isSelected() );
                autoscale.setEnabled( chartVisible.isSelected() );
            }
        } );

        chartVisible.setBorderPainted( true );

        JPanel chartPanel = new JPanel();
        chartPanel.setLayout( new BoxLayout( chartPanel, BoxLayout.Y_AXIS ) );
        chartPanel.add( autoscale );
        chartPanel.add( chartVisible );

        chartPanel.setBorder( BorderFactory.createTitledBorder( "Chart" ) );

        JButton reset = new JButton( "Reset Everything" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        ArrayList objects = new ArrayList();
        for( int i = 0; i < module.getScenes().length; i++ ) {
            Scene s = module.getScenes()[i];
            objects.add( s );
        }
        final Object[] array = objects.toArray();
        planetComboBox = new JComboBox( array );
        planetComboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Object selected = planetComboBox.getSelectedItem();
                if( selected instanceof Scene ) {
                    Scene s = (Scene)selected;
                    module.changeScene( s );
                }
                else {
                    planetComboBox.setSelectedItem( module.getScene() );
                }
            }
        } );

        final JCheckBox showBackground = new JCheckBox( "Background", true );
        showBackground.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setShowBackground( showBackground.isSelected() );
            }
        } );

        developerPanel = new DeveloperPanel( module );
        final JFrame devFrame = new JFrame( "Developer Tool" );
        devFrame.setContentPane( developerPanel );
        devFrame.pack();
        devFrame.setVisible( false );

        final JButton show = new JButton( "Show DeveloperPanel" );
        show.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                devFrame.setVisible( true );
            }
        } );

        JPanel massPanel = new JPanel();
        massPanel.setLayout( new BoxLayout( massPanel, BoxLayout.Y_AXIS ) );
        JRadioButton light = new JRadioButton( "1 kilogram", true );
        light.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCar().setMass( 1 );
                module.getCar().updateObservers();
                module.setChartRange();
            }
        } );
        JRadioButton heavy = new JRadioButton( "1650 kilograms" );
        heavy.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCar().setMass( 1650 );
                module.getCar().updateObservers();
                module.setChartRange();
            }
        } );
        ButtonGroup massGroup = new ButtonGroup();
        massGroup.add( light );
        massGroup.add( heavy );
        massPanel.add( light );
        massPanel.add( heavy );

        massPanel.setBorder( BorderFactory.createTitledBorder( "Mass" ) );

        JButton showHelp = new JButton( "Show Help" );
        showHelp.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showStartupGraphic();
            }
        } );

        JButton clearFriction = new JButton( "Clear Heat" );
        clearFriction.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCar().setFriction( 0 );
                module.getCar().updateObservers();
            }
        } );

        BufferedImage im = new ImageLoader().loadBufferedImage( "images/phet-tape.gif" );
        im = RescaleOp2.rescale( im, 35, 35 );
        JPanel tapePanel = new JPanel();
//        tapePanel.setl
        tapePanel.add( new JLabel( new ImageIcon( im ) ) );

        final JCheckBox tape = new JCheckBox( "Measure" );
        tapePanel.add( tape );

        tape.setSelected( MeasuringTapeInteractiveGraphic.visible );
        tape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                MeasuringTapeInteractiveGraphic.visible = tape.isSelected();
            }
        } );
        JRadioButton gameMode = new JRadioButton( "Gamestyle" );
        gameMode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setGameMode();
            }
        } );
        JRadioButton freeMode = new JRadioButton( "Freestyle" );
        freeMode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setOpenMode();
            }
        } );
        ButtonGroup bg = new ButtonGroup();
        bg.add( gameMode );
        bg.add( freeMode );
        JPanel jp = new JPanel();
        jp.setLayout( new BoxLayout( jp, BoxLayout.Y_AXIS ) );
        jp.add( gameMode );
        jp.add( freeMode );
        jp.setBorder( BorderFactory.createTitledBorder( "Mode" ) );

        if( !EC2Module.WENDY_MODE ) {
            add( chartPanel );
        }
        add( historyPanel );
        add( massPanel );
        add( tapePanel );
        add( planetComboBox );
        add( reset );
        add( clearFriction );
        add( jp );
        final JCheckBox audio = new JCheckBox( "Audio" );
        audio.setSelected( EC2Module.audioEnabled );
        audio.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                EC2Module.audioEnabled = audio.isSelected();
            }
        } );
        add( audio );

        add( showBackground );
        if( !EC2Module.WENDY_MODE ) {
            add( show );
        }

        add( showHelp );

        final JTextArea jta = new JTextArea( "", 50, 2 );
        jta.setEditable( false );
        jta.setEnabled( false );
        add( jta );
        clickHistory();
        clearHistory.setEnabled( false );
//        planetComboBox.setSize( 100,planetComboBox.getHeight() );
    }

    private void clickHistory() {
        module.setHistoryActive( activeHistory.isSelected() );
        lots.setEnabled( activeHistory.isSelected() );
        few.setEnabled( activeHistory.isSelected() );
    }

    public void numHistoryDotsChanged() {
        int num = module.numHistoryDots();
        if( clearHistory.isEnabled() ) {
            if( num == 0 ) {
                clearHistory.setEnabled( false );
            }
        }
        else {
            if( num > 0 ) {
                clearHistory.setEnabled( true );
            }
        }
    }

}

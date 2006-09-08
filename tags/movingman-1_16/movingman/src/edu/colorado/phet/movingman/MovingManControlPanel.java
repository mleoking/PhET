/*PhET, 2004.*/
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 1:11:38 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MovingManControlPanel extends JPanel {
    private MovingManModule module;
    private JPanel controllerContainer;
    private PlaybackPanel playbackPanel;
    private JButton reset;

    public MovingManControlPanel( final MovingManModule module ) throws IOException {
        this.module = module;
        final Dimension preferred = new Dimension( 200, 400 );
        setSize( preferred );
        setPreferredSize( preferred );
        setLayout( new BorderLayout() );

        controllerContainer = new JPanel();
        controllerContainer.setPreferredSize( new Dimension( 200, 200 ) );
        controllerContainer.setSize( new Dimension( 200, 200 ) );
        add( controllerContainer, BorderLayout.CENTER );

        playbackPanel = new PlaybackPanel( module );

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );

        add( panel, BorderLayout.NORTH );
//        JButton changeControl = new JButton( SimStrings.get( "MovingManControlPanel.ChangeSmoothingPointsButton" ) );
//        changeControl.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                String value = JOptionPane.showInputDialog( SimStrings.get( "MovingManControlPanel.SmoothingPointsLabel" ), "15" );
//                module.setNumSmoothingPoints( Integer.parseInt( value ) );
//            }
//        } );

        VerticalLayoutPanel northPanel = new VerticalLayoutPanel();
//        final JMenu viewMenu = new JMenu( SimStrings.get( "MovingManControlPanel.ViewMenu" ) );
//        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
//        for( int i = 0; i < items.length; i++ ) {
//            JMenuItem item = items[i];
//            viewMenu.add( item );
//        }

        new Thread( new Runnable() {
            public void run() {
                try {
                    while( module.getFrame() == null || !module.getFrame().isVisible() ) {
                        Thread.sleep( 1000 );
                    }
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
//                module.getFrame().getJMenuBar().add( viewMenu );
                module.getFrame().setExtendedState( JFrame.MAXIMIZED_HORIZ );
                module.getFrame().setExtendedState( JFrame.MAXIMIZED_BOTH );
            }
        } ).start();
        ImageIcon imageIcon = new ImageIcon( getClass().getClassLoader().getResource( "images/Phet-Flatirons-logo-3-small.jpg" ) );
        JLabel phetIconLabel = new JLabel( imageIcon );
        northPanel.add( phetIconLabel );

        final JCheckBox invertAxes = new JCheckBox( SimStrings.get( "MovingManControlPanel.InvertXAxisCheckBox" ), false );
        invertAxes.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean ok = module.confirmClear();
                if( ok ) {
                    module.setRightDirPositive( !invertAxes.isSelected() );
                }
            }
        } );
        northPanel.add( invertAxes );
        reset = new JButton( SimStrings.get( "MovingManControlPanel.ResetButton" ) );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
                reset.setEnabled( false );
            }
        } );
        reset.setEnabled( false );
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( reset );

        northPanel.setFill( GridBagConstraints.NONE );
        northPanel.setAnchor( GridBagConstraints.WEST );
        northPanel.setInsets( new Insets( 4, 4, 4, 4 ) );
        northPanel.add( buttonPanel );
        panel.add( northPanel, BorderLayout.NORTH );
    }

    public JComponent getPlaybackPanel() {
        return playbackPanel;
    }

}

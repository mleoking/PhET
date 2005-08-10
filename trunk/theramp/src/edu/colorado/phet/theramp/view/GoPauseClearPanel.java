/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesModelListenerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 5, 2005
 * Time: 5:03:07 AM
 * Copyright (c) Apr 5, 2005 by Sam Reid
 */

public class GoPauseClearPanel extends VerticalLayoutPanel {
    private TimeSeriesModel module;

    private JButton goPauseButton;
    private JButton resetButton;
    private boolean itsAGoButton = true;
    private ImageIcon goIcon;
    private ImageIcon pauseIcon;

    public GoPauseClearPanel( final TimeSeriesModel module ) {
        this.module = module;
        super.getGridBagConstraints().anchor = GridBagConstraints.CENTER;
        final ActionListener pauseHandler = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPaused( true );
//                module.requestEditInTextBox( GoPauseClearPanel.this );
            }
        };
        goPauseButton = new ControlButton( SimStrings.get( "MMPlot.PauseButton" ) );//longer text
        try {
            goIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/light3.png" ) );
            pauseIcon = new ImageIcon( ImageLoader.loadBufferedImage( "images/stop-20.png" ) );
            goPauseButton.setIcon( goIcon );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        final ActionListener goHandler = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordMode();
                module.setPaused( false );
            }
        };
        goPauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( itsAGoButton ) {
                    goHandler.actionPerformed( e );
                }
                else {
                    pauseHandler.actionPerformed( e );
                }
            }
        } );
        resetButton = new ControlButton( SimStrings.get( "Reset" ) );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.confirmAndApplyReset();
            }
        } );
        module.addListener( new TimeSeriesModelListenerAdapter() {
            public void recordingStarted() {
                setButtons( false, true, true );
            }

            public void recordingPaused() {
                setButtons( true, false, true );
            }

            public void recordingFinished() {
                setButtons( false, false, true );
            }

            public void reset() {
                setButtons( true, false, false );
            }

            public void rewind() {
                setButtons( true, false, true );
            }
        } );
        add( goPauseButton );
        add( resetButton );
        setButtons( true, false, false );
    }

    private void setButtons( boolean record, boolean pause, boolean reset ) {
        if( pause && record ) {

        }
        else if( !pause && !record ) {

        }
        if( pause ) {
            goPauseButton.setText( "Pause" );
//            goPauseButton.setText( "" );
            goPauseButton.setIcon( pauseIcon );
            super.invalidate();
            super.validate();
            super.doLayout();
            super.repaint();
            itsAGoButton = false;
        }
        else {
            goPauseButton.setText( "      Go!" );
            goPauseButton.setIcon( goIcon );
            super.invalidate();
            super.validate();
            super.doLayout();
            super.repaint();
            itsAGoButton = true;
        }

        resetButton.setEnabled( reset );
        repaintComponents();
    }

    private void repaintComponents() {
        goPauseButton.repaint();
        resetButton.repaint();
        repaint();
    }

    public JButton getGoPauseButton() {
        return goPauseButton;
    }

    static class ControlButton extends JButton {
        static Font font = new Font( "Lucida Sans", Font.BOLD, 14 );

        public ControlButton( String text ) {
            super( text );
            setFont( font );
        }
    }

}

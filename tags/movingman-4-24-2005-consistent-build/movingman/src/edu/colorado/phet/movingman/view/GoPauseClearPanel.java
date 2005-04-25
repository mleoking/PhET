/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.TimeListenerAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Apr 5, 2005
 * Time: 5:03:07 AM
 * Copyright (c) Apr 5, 2005 by Sam Reid
 */

public class GoPauseClearPanel extends VerticalLayoutPanel {
    private MovingManModule module;

    private JButton goPauseButton;
    private JButton resetButton;
    private boolean itsAGoButton = true;

    public GoPauseClearPanel( final MovingManModule module ) {
        this.module = module;
        super.getGridBagConstraints().anchor = GridBagConstraints.CENTER;
        final ActionListener pauseHandler = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPaused( true );
            }
        };
        goPauseButton = new ControlButton( SimStrings.get( "MMPlot.PauseButton" ) );//longer text
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
        resetButton = new ControlButton( SimStrings.get( "MMPlot.ResetButton" ) );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.confirmAndApplyReset();
            }
        } );
        module.addListener( new TimeListenerAdapter() {
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
            super.invalidate();
            super.validate();
            super.doLayout();
            itsAGoButton = false;
        }
        else {
            goPauseButton.setText( "   Go!   " );
            itsAGoButton = true;
        }

        resetButton.setEnabled( reset );
    }

    public JButton getGoPauseButton() {
        return goPauseButton;
    }

    static class ControlButton extends JButton {
        static Font font = MMFontManager.getFontSet().getControlButtonFont();

        public ControlButton( String text ) {
            super( text );
            setFont( font );
        }
    }

}

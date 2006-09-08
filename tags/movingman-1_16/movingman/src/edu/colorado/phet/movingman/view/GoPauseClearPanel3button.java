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

public class GoPauseClearPanel3button extends VerticalLayoutPanel {
    private MovingManModule module;

    private JButton pauseButton;
    private JButton recordButton;
    private JButton resetButton;

    static class ControlButton extends JButton {
        static Font font = MMFontManager.getFontSet().getControlButtonFont();

        public ControlButton( String text ) {
            super( text );
            setFont( font );
        }
    }

    public GoPauseClearPanel3button( final MovingManModule module ) {
        this.module = module;
        pauseButton = new ControlButton( SimStrings.get( "MMPlot.PauseButton" ) );
        pauseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setPaused( true );
            }
        } );
        recordButton = new ControlButton( SimStrings.get( "MMPlot.RecordButton" ) );
        recordButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setRecordMode();
                module.setPaused( false );
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
//            add( titleLabel );
        add( recordButton );
        add( pauseButton );
        add( resetButton );
        pauseButton.setEnabled( false );
    }

    private void setButtons( boolean record, boolean pause, boolean reset ) {
        recordButton.setEnabled( record );
        pauseButton.setEnabled( pause );
        resetButton.setEnabled( reset );
    }

    public JButton getGoButton() {
        return recordButton;
    }

}

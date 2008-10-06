/**
 * Class: MeasureControlPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 9, 2004
 */
package edu.colorado.phet.sound.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.sound.SingleSourceMeasureModule;

public class MeasureControlPanel extends SoundControlPanel {

    private String[] buttonLabels = new String[2];
    private SingleSourceMeasureModule measureModule;

    public MeasureControlPanel( SingleSourceMeasureModule module, IClock clock ) {
        super( module );

//        this.measureModule = module;
//        buttonLabels[0] =  "<html><center>Hide<br>stopwatch</center></html>";
//        buttonLabels[1] =  "<html><center>Show<br>stopwatch</center></html>";
//
//        JButton stopwatchButton = new JButton( buttonLabels[0] );
//        stopwatchButton.addActionListener( new StopwatchButtonListener( stopwatchButton, buttonLabels ));
//        add( stopwatchButton );

        // Now done with a floating window
//        ClockPanelLarge cpl = new ClockPanelLarge( clock );
//        addPanel( cpl );


    }

    private class StopwatchButtonListener implements ActionListener {
        JButton button;
        private String[] buttonLabels;
        int buttonState = 0;

        public StopwatchButtonListener( JButton button, String[] buttonLabels ) {
            this.button = button;
            this.buttonLabels = buttonLabels;
        }

        public void actionPerformed( ActionEvent e ) {
            if( buttonState == 0 ) {
                measureModule.setStopwatchVisible( false );
            }
            else {
                measureModule.setStopwatchVisible( true );
            }
            buttonState = ( buttonState + 1 ) % buttonLabels.length;
            button.setText( buttonLabels[buttonState] );
        }
    }
}

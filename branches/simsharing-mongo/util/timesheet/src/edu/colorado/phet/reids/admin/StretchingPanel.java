package edu.colorado.phet.reids.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.*;

/**
 * @author Sam Reid
 */
public class StretchingPanel extends JPanel {
    StretchingModel model = new StretchingModel();

    public StretchingPanel( final TimesheetModel timesheetModel, final File trunk ) {
        add( new JLabel( "Elapsed: MSE" ) );
        final JTextField textField = new JTextField( 8 );
        {
            textField.setEditable( false );
        }
        add( textField );
        TimesheetModel.TimeListener timeListener = new TimesheetModel.TimeListener() {
            public void timeChanged() {
                long time = model.getTimeSinceBeginningOfLastSession( timesheetModel );
                textField.setText( Util.secondsToElapsedTimeString( time ) );
                if ( timesheetModel.getEntryCount() > 0 ) {
                    if ( model.entryMatches( timesheetModel.getLastEntry() ) && time == (long) ( 4.5 * 60 ) )//TODO: this plays sound during loading CSV
                    {
                        playNotification( new File( trunk, "simulations-java\\simulations\\electric-hockey\\data\\electric-hockey\\audio\\cork.wav" ).getAbsolutePath() );
                    }
                    if ( time == 60 * 60 ) {
                        playNotification( new File( trunk, "simulations-java\\simulations\\electric-hockey\\data\\electric-hockey\\audio\\tada.WAV" ).getAbsolutePath() );
                    }
                }
            }
        };
        timesheetModel.addTimeListener( timeListener );
        timeListener.timeChanged();
        JButton button = new JButton( "S&E" );
        {
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    timesheetModel.startNewEntry( timesheetModel.getLastEntry().getCategory(), "stretching & exercise" );
                }
            } );
        }
        add( button );
        setBorder( BorderFactory.createEtchedBorder() );
    }

    public static void main( String[] args ) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        playNotification( "C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\simulations\\electric-hockey\\data\\electric-hockey\\audio\\tada.WAV" );
    }

    private static void playNotification( String pathname ) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream( new File( pathname ) );
            clip.open( inputStream );
            clip.start();
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
        }
        catch ( UnsupportedAudioFileException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
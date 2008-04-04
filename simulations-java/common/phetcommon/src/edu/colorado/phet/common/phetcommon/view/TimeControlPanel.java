package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * TimeControlPanel implements a Swing component for play/pause and step in PhET simulations.
 *
 * @author Chris Malley, Sam Reid
 * @version $Revision: 15573 $
 */
public class TimeControlPanel extends JPanel {

    private JButton playPause;
    private JButton step;
    private JPanel buttonPanel;//todo: why is there an embedded sub-panel in this panel?
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private String playString;
    private String pauseString;

    private boolean paused = false;

    private ArrayList listeners = new ArrayList();

    public TimeControlPanel() {

        // Button labels
        playString = PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_PLAY );
        pauseString = PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_PAUSE );
        String stepString = PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_STEP );

        // Button icons
        BufferedImage playImage = PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_PLAY );
        BufferedImage pauseImage = PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_PAUSE );
        BufferedImage stepImage = PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_STEP_FORWARD );
        playIcon = new ImageIcon( playImage );
        pauseIcon = new ImageIcon( pauseImage );
        ImageIcon stepIcon = new ImageIcon( stepImage );

        playPause = new JButton();
        updatePlayPauseButtonDimension();

        // Step button
        step = new JButton( stepString, stepIcon );

        SwingUtils.fixButtonOpacity( playPause );
        SwingUtils.fixButtonOpacity( step );

//        setLayout( new BorderLayout() );
        setLayout( new FlowLayout(FlowLayout.CENTER) );
        buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( playPause );
        buttonPanel.add( step );
        this.add( buttonPanel);

        //Adapter methods for event dispatch
        playPause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                paused = !paused;
                updateButtons();
                if ( paused ) {
                    notifyPausePressed();
                }
                else {
                    notifyPlayPressed();
                }
            }
        } );
        step.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStepPressed();
            }
        } );

        updateButtons();
    }

    /**
     * Sets whether the play/pause button is enabled
     *
     * @param enabled true if the play/pause button is enabled
     */
    protected void setPlayPauseButtonEnabled( boolean enabled ) {
        playPause.setEnabled( enabled );
    }

    /**
     * Sets the text of the Play mode on the Play/Pause button.
     *
     * @param playString the new text for the Play mode
     */
    public void setPlayString( String playString ) {
        this.playString = playString;
        updatePlayPauseButtonDimension();
        updateButtons();
    }

    /**
     * Sets the text of the Pause mode on the Play/Pause button.
     *
     * @param pauseString the new text for the pause mode
     */
    public void setPauseString( String pauseString ) {
        this.pauseString = pauseString;
        updatePlayPauseButtonDimension();
        updateButtons();
    }

    /**
     * Sets the text of the Step button
     *
     * @param stepString the new text for the Step button
     */
    public void setStepString( String stepString ) {
        step.setText( stepString );
        updateButtons();
    }

    /**
     * Play/Pause button
     * Set this button to its maximum size so that the contents of
     * the control panel don't horizontally shift as the button state changes.
     */
    protected void updatePlayPauseButtonDimension() {
        playPause.setPreferredSize( SwingUtils.getMaxDimension( playPause, playString, playIcon, pauseString, pauseIcon ) );
    }

    /**
     * Adds a component to the sub-panel which contains the main content for this control panel.
     *
     * @param control
     */
    public void addControl( JComponent control ) {
        buttonPanel.add( control );
    }

    //todo: remove this temporary workaround which is here only to support the animated clock graphic
    public void addControlFarLeft( JComponent control ) {
        add( control, 0 );
    }

    /**
     * Adds a component to the left of the sub-panel which contains the main content for this control panel.
     *
     * @param control
     */
    public void addControlToLeft( JComponent control ) {
        buttonPanel.add( control, 0 );
    }

    /**
     * Sets whether this TimeControlPanel should treat time as paused.
     *
     * @param paused
     */
    public void setPaused( boolean paused ) {
        this.paused = paused;
        updateButtons();
    }

    /**
     * Enables or disables the clock control panel.
     * When disabled, all buttons (play/pause/step) are also disabled.
     * When enabled, the buttons are enabled to correspond to the clock state.
     *
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateButtons();
    }

    /**
     * Updates the state of the play/pause and step buttons to reflect whether the control is paused and/or enabled.
     */
    public void updateButtons() {
        playPause.setText( paused ? playString : pauseString );
        playPause.setIcon( paused ? playIcon : pauseIcon );
        playPause.setEnabled( isEnabled() );
        step.setEnabled( isEnabled() && paused );
    }

    /**
     * Listener interface for receiving events from TimeControlPanel
     * This interface may be revised to pass a TimeControlEvent.
     */
    public static interface TimeControlListener {
        void stepPressed();

        void playPressed();

        void pausePressed();
    }

    /**
     * Convenience adapter class for TimeControlListener
     */
    public static class TimeControlAdapter implements TimeControlListener {
        public void stepPressed() {
        }

        public void playPressed() {
        }

        public void pausePressed() {
        }
    }

    public void addTimeControlListener( TimeControlListener listener ) {
        listeners.add( listener );
    }

    public void removeTimeControlListener( TimeControlListener listener ) {
        listeners.remove( listener );
    }

    private void notifyStepPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlListener) listeners.get( i ) ).stepPressed();
        }
    }

    private void notifyPlayPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlListener) listeners.get( i ) ).playPressed();
        }
    }

    private void notifyPausePressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlListener) listeners.get( i ) ).pausePressed();
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        TimeControlPanel pane = new TimeControlPanel();
        pane.addTimeControlListener( new TimeControlListener() {
            public void stepPressed() {
                System.out.println( "TimeControlPanel.stepPressed" );
            }

            public void playPressed() {
                System.out.println( "TimeControlPanel.playPressed" );
            }

            public void pausePressed() {
                System.out.println( "TimeControlPanel.pausePressed" );
            }
        } );
        frame.setContentPane( pane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
    }
}

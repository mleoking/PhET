package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * TimeControlPanel implements a Swing component for play/pause and step in PhET simulations.
 *
 * @author Chris Malley, Sam Reid
 * @version $Revision: 15573 $
 */
public class TimeControlPanel extends JPanel {
    // Image resource names
    public static final String IMAGE_PAUSE = PhetCommonResources.IMAGE_PAUSE;
    public static final String IMAGE_PLAY = PhetCommonResources.IMAGE_PLAY;
    public static final String IMAGE_STEP = PhetCommonResources.IMAGE_STEP_FORWARD;
    public static final String IMAGE_REWIND = PhetCommonResources.IMAGE_REWIND;
    public static final String IMAGE_FAST_FORWARD = PhetCommonResources.IMAGE_FAST_FORWARD;
    public static final String IMAGE_STOP = PhetCommonResources.IMAGE_STOP;
    public static final String PROPERTY_PLAY = "Common.ClockControlPanel.Play";
    public static final String PROPERTY_PAUSE = "Common.ClockControlPanel.Pause";
    public static final String PROPERTY_STEP = "Common.ClockControlPanel.Step";

    private JButton playPause;
    private JButton step;
    private JPanel buttonPanel;
    private ImageIcon playIcon;
    private ImageIcon pauseIcon;
    private String playString;
    private String pauseString;

    private boolean isPaused=false;
    private boolean enabled=true;
    
    public TimeControlPanel() {

        // Button labels
        playString = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_PLAY );
        pauseString = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_PAUSE );
        String stepString = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_STEP );

        // Button icons
        BufferedImage playImage = PhetCommonResources.getInstance().getImage( IMAGE_PLAY );
        BufferedImage pauseImage = PhetCommonResources.getInstance().getImage( IMAGE_PAUSE );
        BufferedImage stepImage = PhetCommonResources.getInstance().getImage( IMAGE_STEP );
        playIcon = new ImageIcon( playImage );
        pauseIcon = new ImageIcon( pauseImage );
        ImageIcon stepIcon = new ImageIcon( stepImage );

        playPause = new JButton();
        updatePlayPauseButtonDimension();

        // Step button
        step = new JButton( stepString, stepIcon );

        SwingUtils.fixButtonOpacity( playPause );
        SwingUtils.fixButtonOpacity( step );

        setLayout( new BorderLayout() );
        buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( playPause );
        buttonPanel.add( step );
        this.add( buttonPanel, BorderLayout.CENTER );

        updateButtons( isPaused, enabled );
    }

    /**
     * Adds a listener to the play/pause button.
     * @param actionListener the listener
     */
    protected void addPlayPauseActionListener( ActionListener actionListener ) {
        playPause.addActionListener( actionListener );
    }

    /**
     * Removes a listener to the play/pause button.
     * @param actionListener the listener
     */
    protected void removePlayPauseActionListener( ActionListener actionListener ) {
        playPause.removeActionListener( actionListener );
    }

    public void addStepActionListener( ActionListener actionListener ) {
        step.addActionListener( actionListener );
    }

    public void removeStepActionListener(ActionListener actionListener){
        step.removeActionListener( actionListener );
    }

    /**
     * Sets whether the play/pause button is enabled
     * @param enabled true if the play/pause button is enabled
     */
    protected void setPlayPauseButtonEnabled(boolean enabled){
        playPause.setEnabled( enabled );
    }

    /**
     * Sets the text of the Play mode on the Play/Pause button.
     * @param playString the new text for the Play mode
     */
    protected void setPlayString(String playString){
        this.playString=playString;
        updatePlayPauseButtonDimension();
        updateButtons(isPaused,enabled);
    }

    /**
     * Play/Pause button
     * Set this button to its maximum size so that the contents of
     * the control panel don't horizontally shift as the button state changes.
     */
    protected void updatePlayPauseButtonDimension() {
        playPause.setPreferredSize( SwingUtils.getMaxDimension(playPause, playString, playIcon, pauseString, pauseIcon) );
    }

    /**
     * Adds a component to the sub-panel which contains the main content for this control panel.
     * @param control
     */
    public void addControl(JComponent control){
        buttonPanel.add(control);
    }

    /**
     * Adds a component to the left of the sub-panel which contains the main content for this control panel.
     * @param control
     */
    public void addControlToLeft(JComponent control){
        buttonPanel.add(control,0);
    }

    public void addToLeft( JComponent component ) {
        add( component, BorderLayout.WEST );
    }

    public void addToRight( JComponent component ) {
        add( component, BorderLayout.EAST );
    }

    public void updateButtons( boolean isPaused, boolean enabled ) {
        if( isPaused ) {
            playPause.setText( playString );
            playPause.setIcon( playIcon );
        }
        else {
            playPause.setText( pauseString );
            playPause.setIcon( pauseIcon );
        }
        playPause.setEnabled( enabled );
        step.setEnabled( enabled && isPaused );
    }
}

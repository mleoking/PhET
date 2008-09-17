/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.AnimatedClockJComponent;
import edu.colorado.phet.common.phetcommon.view.TimeControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Feasibility test class for piccolo-based media control buttons.
 *
 * @author Sam Reid, Chris Malley
 */
//todo: this duplicates code with TimeControlPanel
public class PiccoloTimeControlPanel extends PhetPCanvas {

    public static final NumberFormat DEFAULT_TIME_FORMAT = new DecimalFormat( "0" );
    public static final int DEFAULT_TIME_COLUMNS = 8;

    private JButton restartButton;
    private JTextField timeTextField;
    private JLabel unitsLabel;
    private AnimatedClockJComponent animatedClockIcon;

    private NumberFormat timeFormat;
    private double time;
    private boolean paused;
    private JPanel userPanel;

    private ArrayList listeners = new ArrayList();
    private PlayPauseButton piccoloPlayPauseButton;
    private StepButton piccoloStepButton;
    private BackgroundNode backgroundNode;
    private ToolTipHandler stepToolTipHandler;
    private ToolTipHandler pauseTooltipHandler;
//    private MediaPlaybackBarNode mediaPlaybackBarNode;

    public PiccoloTimeControlPanel() {
        setBorder( null );
        setBackground( new JLabel().getBackground() );
        time = 0;
        paused = false;
        timeFormat = DEFAULT_TIME_FORMAT;

        // Restart
        String restartString = PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_RESTART );
        BufferedImage restartImage = PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_RESTART );
        ImageIcon restartIcon = new ImageIcon( restartImage );
        restartButton = new JButton( restartString, restartIcon );

        piccoloPlayPauseButton = new PlayPauseButton( (int) ( 100 * 0.7 * 0.7 ) );
        piccoloStepButton = new StepButton( (int) ( piccoloPlayPauseButton.getButtonDimension().width * 0.8 ) );

        // Put all the buttons in a button panel
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.setOpaque( false );
        buttonPanel.add( restartButton );
        PhetPCanvas buttonPCanvas = new PhetPCanvas();

        stepToolTipHandler = new ToolTipHandler( "Step", buttonPCanvas );
        piccoloStepButton.addInputEventListener( stepToolTipHandler );

        pauseTooltipHandler = new ToolTipHandler( "Pause", buttonPCanvas );
        piccoloPlayPauseButton.addInputEventListener( pauseTooltipHandler );

        buttonPCanvas.setOpaque( false );
        buttonPCanvas.setBorder( null );

        backgroundNode = new BackgroundNode();
        addScreenChild( backgroundNode );

        buttonPCanvas.addScreenChild( piccoloPlayPauseButton );
        buttonPCanvas.addScreenChild( piccoloStepButton );
        piccoloStepButton.setOffset( piccoloPlayPauseButton.getFullBounds().getMaxX(), piccoloPlayPauseButton.getFullBounds().getCenterY() - piccoloStepButton.getFullBounds().getHeight() / 2 );
        buttonPCanvas.setPreferredSize( new Dimension( (int) piccoloPlayPauseButton.getFullBounds().getWidth() * 2, (int) piccoloPlayPauseButton.getParent().getFullBounds().getHeight() ) );
        buttonPanel.add( buttonPCanvas );

        // Time display, time value & units
        timeTextField = new JTextField();
        timeTextField.setColumns( DEFAULT_TIME_COLUMNS );
        timeTextField.setEditable( false );
        timeTextField.setHorizontalAlignment( JTextField.RIGHT );
        unitsLabel = new JLabel();
        JPanel timeDisplayPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        timeDisplayPanel.add( timeTextField );
        timeDisplayPanel.add( unitsLabel );

        setOpaque( timeDisplayPanel, false );

        // Animated clock icon
        animatedClockIcon = new AnimatedClockJComponent();

        // User panel, for stuff between the time display and buttons
        userPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        userPanel.setOpaque( false );

        // for backward compatibility with existing sims
        restartButton.setVisible( false );
        timeTextField.setVisible( false );
        unitsLabel.setVisible( false );

        // Layout the button panel
        setLayout( new FlowLayout( FlowLayout.CENTER ) );
        if ( false && PhetApplication.instance().isDeveloperControlsEnabled() ) { //TODO: only in dev versions until we finish this feature
            add( animatedClockIcon );
        }
        add( timeDisplayPanel );
        add( userPanel );
        add( buttonPanel );

        piccoloPlayPauseButton.addListener( new PlayPauseButton.Listener() {
            public void playbackStateChanged() {
                paused = !piccoloPlayPauseButton.isPlaying();
                updateButtons();
                if ( paused ) {
                    notifyPausePressed();
                }
                else {
                    notifyPlayPressed();
                }
            }
        } );
        piccoloStepButton.addListener( new StepButton.Listener() {
            public void buttonPressed() {
                notifyStepPressed();
            }
        } );
        restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyRestartPressed();
            }
        } );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateShape();
            }
        } );

        updateButtons();
        updateShape();
    }

    private String getPauseString() {
        return PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_PAUSE );
    }

    private String getPlayString() {
        return PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_PLAY );
    }

    private void updateShape() {
//        mediaPlaybackBarNode.setSize( getWidth(), 3 );
        backgroundNode.setSize( getWidth(), getHeight() );
    }

    /**
     * Sets the text for the step button to the specified value.
     *
     * @param text the label text to display on the step button
     */
    public void setStepButtonText( String text ) {
        //todo: change this to tooltip
//        stepButton.setText( text );

    }

    /**
     * Advances the animated clock icon by one step.
     */
    public void advanceAnimatedClockIcon() {
        animatedClockIcon.advance();
//        mediaPlaybackBarNode.setProgress( mediaPlaybackBarNode.getProgress() + 0.001 );
    }

    /**
     * Resets the animated clock icon to its initial state.
     */
    public void resetAnimatedClockIcon() {
        animatedClockIcon.reset();
    }

    /**
     * Sets the visibility of the Restart button.
     * This button is invisible by default for backward compatibility with existing sims.
     *
     * @param visible true if the restart button should be visible
     */
    public void setRestartButtonVisible( boolean visible ) {
        restartButton.setVisible( visible );
    }

    /**
     * Sets the visibility of the time display.
     * This display is invisible by default for backward compatibility with existing sims.
     *
     * @param visible true if the time display should be visible
     */
    public void setTimeDisplayVisible( boolean visible ) {
        timeTextField.setVisible( visible );
        unitsLabel.setVisible( visible );
        if ( visible ) {
            updateTimeDisplay();
        }
    }

    /**
     * Convenience method for adding a component to the left of this panel.
     *
     * @param component
     */
    public void addToLeft( JComponent component ) {
        add( component, 0 );
    }

    /**
     * Adds component between the time display and the buttons.
     * <p/>
     * TODO: This is a hack, currently used by some sims to add a clock speed control.
     * We should figure out a better way to add components to the layout, or
     * add a standard clock speed control to this control panel.
     *
     * @param component
     */
    public void addBetweenTimeDisplayAndButtons( JComponent component ) {
        setOpaque( component, false );
        userPanel.add( component );
    }

    private void setOpaque( JComponent component, boolean opaque ) {
        if ( !( component instanceof JTextComponent ) ) {
            component.setOpaque( opaque );
            for ( int i = 0; i < component.getComponentCount(); i++ ) {
                Component c = component.getComponent( i );
                if ( c instanceof JComponent ) {
                    setOpaque( (JComponent) c, opaque );
                }
            }
        }
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
     * Gets the "Restart" component, used for attaching help items.
     *
     * @return
     */
    public JComponent getRestartComponent() {
        return restartButton;
    }

    /**
     * Sets the format of the time display.
     * See DecimalFormat for specification of pattern syntax.
     *
     * @param formatPattern
     */
    public void setTimeFormat( String formatPattern ) {
        setTimeFormat( new DecimalFormat( formatPattern ) );
    }

    /**
     * Sets the format of the time display.
     *
     * @param format
     */
    public void setTimeFormat( NumberFormat format ) {
        timeFormat = format;
        updateTimeDisplay();
    }

    /**
     * Sets the font used to display the time.
     *
     * @param font
     */
    public void setTimeFont( Font font ) {
        timeTextField.setFont( font );
    }

    /**
     * Sets the number of columns used to display the time.
     *
     * @param columns
     */
    public void setTimeColumns( int columns ) {
        timeTextField.setColumns( columns );
    }

    /**
     * Sets the time units.
     *
     * @param units
     */
    public void setUnits( String units ) {
        unitsLabel.setText( units );
    }

    /**
     * Sets the font for the time units.
     *
     * @param font
     */
    public void setUnitsFont( Font font ) {
        unitsLabel.setFont( font );
    }

    /**
     * Sets the time displayed.
     */
    public void setTimeDisplay( double time ) {
        if ( time != this.time ) {
            this.time = time;
            updateTimeDisplay();
        }
    }

    /*
    * Updates the time display.
    */
    private void updateTimeDisplay() {
        if ( timeTextField.isVisible() ) {
            String sValue = timeFormat.format( time );
            timeTextField.setText( sValue );
        }
    }

    /*
     * Updates the state of the play/pause and step buttons to reflect whether the control is paused and/or enabled.
     */
    private void updateButtons() {
        piccoloStepButton.setEnabled( isEnabled() && paused );
        restartButton.setEnabled( isEnabled() );

        pauseTooltipHandler.setText( paused ? getPlayString() : getPauseString() );
        stepToolTipHandler.setText( paused ? getStepString() : null );
    }

    private String getStepString() {
        return PhetCommonResources.getInstance().getLocalizedString( PhetCommonResources.STRING_CLOCK_STEP );
    }

    public void addTimeControlListener( TimeControlPanel.TimeControlListener listener ) {
        listeners.add( listener );
    }

    public void removeTimeControlListener( TimeControlPanel.TimeControlListener listener ) {
        listeners.remove( listener );
    }

    private void notifyStepPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlPanel.TimeControlListener) listeners.get( i ) ).stepPressed();
        }
    }

    private void notifyPlayPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlPanel.TimeControlListener) listeners.get( i ) ).playPressed();
        }
    }

    private void notifyPausePressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlPanel.TimeControlListener) listeners.get( i ) ).pausePressed();
        }
    }

    private void notifyRestartPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlPanel.TimeControlListener) listeners.get( i ) ).restartPressed();
        }
    }

    /**
     * Returns the component responsible for handling play/pause button presses.
     *
     * @return the play/pause button
     */
    public PNode getPlayPauseButton() {
        return piccoloPlayPauseButton;
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PiccoloTimeControlPanel pane = new PiccoloTimeControlPanel();
//        pane.setRestartButtonVisible( true );
        pane.addTimeControlListener( new TimeControlPanel.TimeControlListener() {
            public void stepPressed() {
                System.out.println( "TimeControlPanel.stepPressed" );
            }

            public void playPressed() {
                System.out.println( "TimeControlPanel.playPressed" );
            }

            public void pausePressed() {
                System.out.println( "TimeControlPanel.pausePressed" );
            }

            public void restartPressed() {
                System.out.println( "TimeControlPanel.restartPressed" );
            }
        } );
        frame.setContentPane( pane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

    private class BackgroundNode extends PNode {
        private PhetPPath backgroundNode = new PhetPPath( new JLabel().getBackground() );
        private PhetPPath tabNode = new PhetPPath();
        private int width;
        private int height;

        private BackgroundNode() {
            addChild( backgroundNode );
            addChild( tabNode );
        }

        public void setSize( int width, int height ) {
            this.width = width;
            this.height = height - 2;
            tabNode.setPathTo( createPath() );
            tabNode.setPaint( getGradientPaint( height ) );

            backgroundNode.setPathToRectangle( 0, 0, width, height );
            tabNode.setStrokePaint( getGradientPaintBorder( height ) );
        }


        private Shape createPath() {
            double dw = 10;
            DoubleGeneralPath path = new DoubleGeneralPath( 0, 0 );
            path.lineToRelative( width, 0 );
            path.lineToRelative( -dw, height );
            path.lineTo( +dw, height );
            path.lineTo( 0, 0 );
            return path.getGeneralPath();
        }
    }

    private GradientPaint getGradientPaintBorder( int height ) {
        return new GradientPaint( 0, height / 4, new JLabel().getBackground(), 0, height, darker( darker( new JLabel().getBackground() ) ) );
    }

    private GradientPaint getGradientPaint( int height ) {
        return new GradientPaint( 0, height / 4, new JLabel().getBackground(), 0, height, darker( new JLabel().getBackground() ) );
    }

    private Color darker( Color orig ) {
        int dred = 30;
        int dgreen = 40;
        int dblue = 40;
        return new Color( Math.min( orig.getRed() - dred, 255 )
                , Math.min( orig.getGreen() - dgreen, 255 )
                , Math.min( orig.getBlue() - dblue, 255 ) );
    }
}
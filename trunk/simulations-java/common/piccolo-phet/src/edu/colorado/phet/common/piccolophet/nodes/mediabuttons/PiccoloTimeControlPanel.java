/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Piccolo-based time controls.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class PiccoloTimeControlPanel extends JPanel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final String PLAY_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_PLAY );
    private static final String PAUSE_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_PAUSE );
    private static final String STEP_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_STEP );
    private static final String STEP_BACK_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_STEP_BACK );
    private static final String REWIND_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_REWIND );

    private static final double BUTTON_X_SPACING = 5;

    private static final NumberFormat DEFAULT_TIME_FORMAT = new DecimalFormat( "0" );
    private static final int DEFAULT_TIME_COLUMNS = 8;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    private BackgroundNode backgroundNode;
    private final PlayPauseButton playPauseButton;
    private final StepButton stepButton;
    private final StepBackButton stepBackButton;
    private final RewindButton rewindButton;
    private JTextField timeTextField;
    private JLabel unitsLabel;
    private JPanel userPanel;

    private ToolTipHandler playPauseTooltipHandler, stepTooltipHandler, rewindTooltipHandler, stepBackTooltipHandler;
    private NumberFormat timeFormat;
    private double time;
    private boolean paused;
    private ArrayList listeners = new ArrayList();
    private PhetPCanvas buttonCanvas;
    private final ArrayList buttonList;
    private JPanel timeDisplayPanel;
    private boolean enableStepWhileRunning;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------

    public PiccoloTimeControlPanel() {
        setBorder( null );
        setBackground( new JLabel().getBackground() );

        time = 0;
        paused = false;
        enableStepWhileRunning = false;
        timeFormat = DEFAULT_TIME_FORMAT;

        // Background
        backgroundNode = new BackgroundNode();

        // Play/Pause
        playPauseButton = new PlayPauseButton( (int) ( 100 * 0.7 * 0.7 ) );

        // Step
        stepButton = new StepButton( (int) ( playPauseButton.getButtonDimension().width * 0.8 ) );
        
        // Step back
        stepBackButton = new StepBackButton( (int) ( playPauseButton.getButtonDimension().width * 0.8 ) );

        // Restart
        rewindButton = new RewindButton( (int) ( playPauseButton.getButtonDimension().width * 0.8 ) );

        // Time display, time value & units
        timeTextField = new JTextField();
        timeTextField.setColumns( DEFAULT_TIME_COLUMNS );
        timeTextField.setEditable( false );
        timeTextField.setHorizontalAlignment( JTextField.RIGHT );
        unitsLabel = new JLabel();
        timeDisplayPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        timeDisplayPanel.add( timeTextField );
        timeDisplayPanel.add( unitsLabel );
        SwingUtils.setOpaqueDeep( timeDisplayPanel, false );

        // User panel, for stuff between the time display and buttons
        userPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        userPanel.setOpaque( false );

        // Layout piccolo buttons on a canvas
        buttonList = new ArrayList();
        buttonCanvas = new PhetPCanvas();
        buttonCanvas.setOpaque( false );
        buttonCanvas.setBorder( null );
        addButton( rewindButton );
        addButton( stepBackButton );
        addButton( playPauseButton );
        addButton( stepButton );

        // Layout piccolo and Swing buttons in a panel
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.setOpaque( false );
        buttonPanel.add( buttonCanvas );

        // Layout of this canvas
        setLayout( new FlowLayout( FlowLayout.CENTER ) );
        add( timeDisplayPanel );
        add( userPanel );
        add( buttonPanel );

        // tool tips on piccolo buttons
        stepBackTooltipHandler = new ToolTipHandler( STEP_BACK_TOOLTIP, buttonCanvas );
        stepBackButton.addInputEventListener( stepBackTooltipHandler );
        stepTooltipHandler = new ToolTipHandler( STEP_TOOLTIP, buttonCanvas );
        stepButton.addInputEventListener( stepTooltipHandler );
        playPauseTooltipHandler = new ToolTipHandler( PAUSE_TOOLTIP, buttonCanvas );
        playPauseButton.addInputEventListener( playPauseTooltipHandler );
        rewindTooltipHandler = new ToolTipHandler( REWIND_TOOLTIP, buttonCanvas );
        rewindButton.addInputEventListener( rewindTooltipHandler );

        // listeners
        playPauseButton.addListener( new PlayPauseButton.Listener() {
            public void playbackStateChanged() {
                paused = !playPauseButton.isPlaying();
                updateButtons();
                if ( paused ) {
                    notifyPausePressed();
                }
                else {
                    notifyPlayPressed();
                }
            }
        } );

        stepButton.addListener( new StepButton.Listener() {
            public void buttonPressed() {
                notifyStepPressed();
            }
        } );

        stepBackButton.addListener( new StepButton.Listener() {
            public void buttonPressed() {
                notifyStepBackPressed();
            }
        } );

        rewindButton.addListener( new RewindButton.Listener() {
            public void buttonPressed() {
                notifyRewindPressed();
            }
        } );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateButtonLayout();
            }
        } );

        // for backward compatibility with existing sims
        rewindButton.setVisible( false );
        stepBackButton.setVisible( false );
        timeDisplayPanel.setVisible( false );
        userPanel.setVisible( false );

        updateButtons();
        updateButtonLayout();
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        backgroundNode.fullPaint( new PPaintContext( (Graphics2D) g ) );
    }

    public PCanvas getButtonCanvas() {
        return buttonCanvas;
    }

    private void addButton( PNode button ) {
        buttonList.add( button );
        buttonCanvas.addScreenChild( button );
    }

    private void updateButtonLayout() {

        double maxHeight = 0;
        Iterator i = buttonList.iterator();
        while ( i.hasNext() ) {
            PNode button = (PNode) i.next();
            if ( button.getVisible() ) {
                maxHeight = Math.max( maxHeight, button.getFullBoundsReference().getHeight() );
            }
        }

        double previousX = 0;
        i = buttonList.iterator();
        while ( i.hasNext() ) {
            PNode button = (PNode) i.next();
            if ( button.getVisible() ) {
                button.setOffset( previousX, ( maxHeight - button.getFullBoundsReference().getHeight() ) / 2 );
                previousX += button.getFullBoundsReference().getWidth() + BUTTON_X_SPACING;
            }
        }

        buttonCanvas.setPreferredSize( new Dimension( (int) previousX, (int) maxHeight ) );
        backgroundNode.setSize( getWidth(), getHeight() );
    }

    //------------------------------------------------------------------------
    // Setters & getters
    //------------------------------------------------------------------------

    /**
     * Returns the component responsible for handling play/pause button presses.
     *
     * @return the play/pause button
     */
    public PNode getPlayPauseButton() {
        return playPauseButton;
    }

    /**
     * Sets the visibility of the Rewind button.
     * This button is invisible by default for backward compatibility with existing sims.
     *
     * @param visible true if the restart button should be visible
     */
    public void setRewindButtonVisible( boolean visible ) {
        rewindButton.setVisible( visible );
        updateButtonLayout();
    }

    /**
     * Sets the visibility of the step back button. This button is invisible
     * by default, since many sims do not need it.
     *
     * @param visible true if the restart button should be visible
     */
    public void setStepBackButtonVisible( boolean visible ) {
        stepBackButton.setVisible( visible );
        updateButtonLayout();
    }

    /**
     * Sets the visibility of the time display.
     * This display is invisible by default for backward compatibility with existing sims.
     *
     * @param visible true if the time display should be visible
     */
    public void setTimeDisplayVisible( boolean visible ) {
        timeDisplayPanel.setVisible( visible );
        if ( visible ) {
            updateTimeDisplay();
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
     * When disabled, all buttons are also disabled.
     * When enabled, the buttons are enabled to correspond to the clock state.
     *
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateButtons();
    }

    /**
     * Gets the "Rewind" button, used for attaching help items.
     *
     * @return
     */
    public PNode getRewindButton() {
        return rewindButton;
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

    public void setStepButtonTooltip( String tooltip ) {
        stepTooltipHandler.setText( tooltip );
    }

    public void setRewindButtonTooltip( String tooltip ) {
        rewindTooltipHandler.setText( tooltip );
    }

    public void setEnableStepWhileRunning( boolean value ) {
        boolean needsUpdate = enableStepWhileRunning != value;
        enableStepWhileRunning = value;
        if ( needsUpdate ) {
            updateButtons();
        }
    }

    //------------------------------------------------------------------------
    // Adding components
    //------------------------------------------------------------------------

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
        SwingUtils.setOpaqueDeep( component, false );
        userPanel.add( component );
        userPanel.setVisible( true );
    }

    //------------------------------------------------------------------------
    // Updaters
    //------------------------------------------------------------------------

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
        playPauseButton.setPlaying( !paused );
        playPauseButton.setEnabled( isEnabled() );
        playPauseTooltipHandler.setText( paused ? PLAY_TOOLTIP : PAUSE_TOOLTIP );
        stepButton.setEnabled( isEnabled() && ( paused || enableStepWhileRunning ) );
        stepBackButton.setEnabled( isEnabled() && ( paused || enableStepWhileRunning ) );
        rewindButton.setEnabled( isEnabled() );
    }

    //------------------------------------------------------------------------
    // Listeners
    //------------------------------------------------------------------------

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

    private void notifyStepBackPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlListener) listeners.get( i ) ).stepBackPressed();
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

    private void notifyRewindPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlListener) listeners.get( i ) ).restartPressed();
        }
    }

    //------------------------------------------------------------------------
    // Inner classes
    //------------------------------------------------------------------------

    public static class BackgroundNode extends PNode {

        private final PhetPPath backgroundNode = new PhetPPath( new JLabel().getBackground() );
        private final PhetPPath tabNode = new PhetPPath();
        private int width;
        private int height;

        public BackgroundNode() {
            addChild( backgroundNode );
            addChild( tabNode );
        }

        public void fullPaint( PPaintContext paintContext ) {
            super.fullPaint( paintContext );

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
            double dw = 0;
            DoubleGeneralPath path = new DoubleGeneralPath( 0, 0 );
            path.lineToRelative( width, 0 );
            path.lineToRelative( -dw, height );
            path.lineTo( +dw, height );
            path.lineTo( 0, 0 );
            return path.getGeneralPath();
        }

        private GradientPaint getGradientPaintBorder( int height ) {
            return new GradientPaint( 0, height / 4, darker( new JLabel().getBackground() ), 0, height, darker( darker( new JLabel().getBackground() ) ) );
        }

        private GradientPaint getGradientPaint( int height ) {
            return new GradientPaint( 0, height / 4, new JLabel().getBackground(), 0, height, darker( new JLabel().getBackground() ) );
        }

        private Color darker( Color orig ) {
            int dred = 30;
            int dgreen = 40;
            int dblue = 40;
            return new Color( Math.max( orig.getRed() - dred, 0 )
                    , Math.max( orig.getGreen() - dgreen, 0 )
                    , Math.max( orig.getBlue() - dblue, 0 ) );
        }

        private Color lighter( Color orig ) {
            int dred = 30;
            int dgreen = 40;
            int dblue = 40;
            return new Color( Math.min( orig.getRed() + dred, 255 )
                    , Math.min( orig.getGreen() + dgreen, 255 )
                    , Math.min( orig.getBlue() + dblue, 255 ) );
        }
    }

    // test
    public static void main( String[] args ) {

        TimeControlListener listener = new TimeControlListener() {
            public void stepPressed() {
                System.out.println( "stepPressed" );
            }

            public void playPressed() {
                System.out.println( "playPressed" );
            }

            public void pausePressed() {
                System.out.println( "pausePressed" );
            }

            public void stepBackPressed() {
                System.out.println( "stepBackPressed" );
            }
            public void restartPressed() {
                System.out.println( "restartPressed" );
            }
        };


        PiccoloTimeControlPanel controls1 = new PiccoloTimeControlPanel();
        controls1.setStepButtonTooltip( "tooltip test" );
        controls1.addTimeControlListener( listener );

        PiccoloTimeControlPanel controls2 = new PiccoloTimeControlPanel();
        controls2.setRewindButtonVisible( true );
        controls2.addTimeControlListener( listener );

        PiccoloTimeControlPanel controls3 = new PiccoloTimeControlPanel();
        controls3.addBetweenTimeDisplayAndButtons( new JLabel( "test" ) );
        controls3.addTimeControlListener( listener );

        PiccoloTimeControlPanel controls4 = new PiccoloTimeControlPanel();
        controls4.setTimeDisplayVisible( true );
        controls4.addTimeControlListener( listener );

        PiccoloTimeControlPanel controls5 = new PiccoloTimeControlPanel();
        controls5.setStepButtonTooltip( "tooltip test" );
        controls5.setRewindButtonVisible( true );
        controls5.setTimeDisplayVisible( true );
        controls5.addBetweenTimeDisplayAndButtons( new JLabel( "test" ) );
        controls5.addTimeControlListener( listener );

        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( controls1, row++, column );
        layout.addComponent( controls2, row++, column );
        layout.addComponent( controls3, row++, column );
        layout.addComponent( controls4, row++, column );
        layout.addComponent( controls5, row++, column );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
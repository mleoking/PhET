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

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Piccolo-based time controls.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class PiccoloTimeControlPanel extends JPanel{

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    public static final String PLAY_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_PLAY );
    public static final String PAUSE_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_PAUSE );
    public static final String STEP_TOOLTIP = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_STEP );
    public static final String RESTART_LABEL = PhetCommonResources.getString( PhetCommonResources.STRING_CLOCK_RESTART );
    
    public static final NumberFormat DEFAULT_TIME_FORMAT = new DecimalFormat( "0" );
    public static final int DEFAULT_TIME_COLUMNS = 8;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private BackgroundNode backgroundNode;
    private final PlayPauseButton playPauseButton;
    private final StepButton stepButton;
    private final JButton restartButton;
    private JTextField timeTextField;
    private JLabel unitsLabel;
    private JPanel userPanel;

    private ToolTipHandler playPauseTooltipHandler;
    private NumberFormat timeFormat;
    private double time;
    private boolean paused;
    private ArrayList listeners = new ArrayList();
    private ToolTipHandler stepTooltipHandler;
    private PhetPCanvas buttonPCanvas;

    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
    
    public PiccoloTimeControlPanel() {
        setBorder( null );
        setBackground( new JLabel().getBackground() );
        
        time = 0;
        paused = false;
        timeFormat = DEFAULT_TIME_FORMAT;
        
        // Background
        backgroundNode = new BackgroundNode();
        
        // Play/Pause
        playPauseButton = new PlayPauseButton( (int) ( 100 * 0.7 * 0.7 ) );
        
        // Step
        stepButton = new StepButton( (int) ( playPauseButton.getButtonDimension().width * 0.8 ) );
        
        // Restart
        //TODO this should be a piccolo button too
        BufferedImage restartImage = PhetCommonResources.getImage( PhetCommonResources.IMAGE_RESTART );
        ImageIcon restartIcon = new ImageIcon( restartImage );
        restartButton = new JButton( RESTART_LABEL, restartIcon );

        // Time display, time value & units
        timeTextField = new JTextField();
        timeTextField.setColumns( DEFAULT_TIME_COLUMNS );
        timeTextField.setEditable( false );
        timeTextField.setHorizontalAlignment( JTextField.RIGHT );
        unitsLabel = new JLabel();
        JPanel timeDisplayPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        timeDisplayPanel.add( timeTextField );
        timeDisplayPanel.add( unitsLabel );
        SwingUtils.setOpaqueDeep( timeDisplayPanel, false );

        // User panel, for stuff between the time display and buttons
        userPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        userPanel.setOpaque( false );

        // Layout piccolo buttons on a canvas
        buttonPCanvas = new PhetPCanvas();
        buttonPCanvas.setOpaque( false );
        buttonPCanvas.setBorder( null );
        buttonPCanvas.addScreenChild( playPauseButton );
        buttonPCanvas.addScreenChild( stepButton );
        stepButton.setOffset( playPauseButton.getFullBounds().getMaxX(), playPauseButton.getFullBounds().getCenterY() - stepButton.getFullBounds().getHeight() / 2 );
        buttonPCanvas.setPreferredSize( new Dimension( (int) (  playPauseButton.getFullBounds().getWidth() * 1.92 ), (int) playPauseButton.getParent().getFullBounds().getHeight() ) );
        
        // Layout piccolo and Swing buttons in a panel
        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.setOpaque( false );
        buttonPanel.add( restartButton );
        buttonPanel.add( buttonPCanvas );

        // Layout of this canvas
        setLayout( new FlowLayout( FlowLayout.CENTER ) );
        //TODO: the next two lines make the shape of the panel asymmetric, even if the time display panel and userpanel are invisible
        add( timeDisplayPanel );
        add( userPanel );
        add( buttonPanel );
        
        // tool tips on piccolo buttons
        stepTooltipHandler = new ToolTipHandler( STEP_TOOLTIP, buttonPCanvas );
        stepButton.addInputEventListener( stepTooltipHandler );
        playPauseTooltipHandler = new ToolTipHandler( PAUSE_TOOLTIP, buttonPCanvas );
        playPauseButton.addInputEventListener( playPauseTooltipHandler );

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
        
        restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyRestartPressed();
            }
        } );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateSize();
            }
        } );
        
        // for backward compatibility with existing sims
        restartButton.setVisible( false );
        timeTextField.setVisible( false );
        unitsLabel.setVisible( false );

        updateButtons();
        updateSize();
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        backgroundNode.fullPaint( new PPaintContext( (Graphics2D) g ) );
    }

    public PCanvas getButtonCanvas(){
        return buttonPCanvas;
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
    
    public void setStepButtonTooltip( String tooltip ) {
        stepTooltipHandler.setText( tooltip );
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

    private void updateSize() {
        backgroundNode.setSize( getWidth(), getHeight() );
    }
    
    /*
     * Updates the state of the play/pause and step buttons to reflect whether the control is paused and/or enabled.
     */
    private void updateButtons() {
        playPauseButton.setPlaying( !paused );
        playPauseButton.setEnabled( isEnabled() );
        playPauseTooltipHandler.setText( paused ? PLAY_TOOLTIP : PAUSE_TOOLTIP );
        stepButton.setEnabled( isEnabled() && paused );
        restartButton.setEnabled( isEnabled() );
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

    private void notifyRestartPressed() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (TimeControlListener) listeners.get( i ) ).restartPressed();
        }
    }

    //------------------------------------------------------------------------
    // Inner classes
    //------------------------------------------------------------------------
    
    private static class BackgroundNode extends PNode {
        
        private final PhetPPath backgroundNode = new PhetPPath( new JLabel().getBackground() );
        private final PhetPPath tabNode = new PhetPPath();
        private int width;
        private int height;

        private BackgroundNode() {
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
            return new GradientPaint( 0, height / 4, darker(new JLabel().getBackground()), 0, height, darker( darker( new JLabel().getBackground() ) ) );
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
    
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        PiccoloTimeControlPanel pane = new PiccoloTimeControlPanel();
        pane.setStepButtonTooltip( "step forward the simulation" );
//        pane.setRestartButtonVisible( true );
        pane.addTimeControlListener( new TimeControlListener() {
            public void stepPressed() {
                System.out.println( "stepPressed" );
            }

            public void playPressed() {
                System.out.println( "playPressed" );
            }

            public void pausePressed() {
                System.out.println( "pausePressed" );
            }

            public void restartPressed() {
                System.out.println( "restartPressed" );
            }
        } );
        frame.setContentPane( pane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
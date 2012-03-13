// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterValue;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterValues;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SpectrumImageFactory.LinearSpectrumImageFactory;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler;
import edu.colorado.phet.common.piccolophet.event.SliderThumbDragHandler.Orientation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.textFieldCorrected;

/**
 * WavelengthControl is a slider-like control used for setting wavelength.
 * It handles visible wavelengths, plus optional UV and IR wavelengths.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WavelengthControl extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Dimension THUMB_SIZE = new Dimension( 20, 20 );
    private static final Stroke THUMB_STROKE = new BasicStroke( 1f );
    private static final Color THUMB_STROKE_COLOR = Color.WHITE;

    private static final DecimalFormat VALUE_FORMAT = new DecimalFormat( "0" );
    private static final double VALUE_Y_OFFSET = 2;

    private static final String UNITS_LABEL = PhetCommonResources.getString( "Common.wavelengthControl.nm" );

    private static final double CURSOR_WIDTH = 3;
    private static final Stroke CURSOR_STROKE = new BasicStroke( 1f );
    private static final Color CURSOR_COLOR = Color.BLACK;

    private static final String UV_STRING = PhetCommonResources.getString( "Common.wavelengthControl.UV" );
    private static final String IR_STRING = PhetCommonResources.getString( "Common.wavelengthControl.IR" );
    private static final Color UV_TRACK_COLOR = Color.LIGHT_GRAY;
    private static final Color UV_LABEL_COLOR = Color.BLACK;
    private static final Color IR_TRACK_COLOR = UV_TRACK_COLOR;
    private static final Color IR_LABEL_COLOR = UV_LABEL_COLOR;
    // how tall the UV/IR labels should be relative to the track height
    private static final double LABEL_TRACK_RATIO = 0.70;

    private static final int TEXT_FIELD_COLUMNS = 3;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final double minWavelength, maxWavelength; // range, in nanometers
    private final Color uvColor, irColor; // colors used to represent UV and IR wavelengths
    private final Thumb thumb; // slider thumb, what the user drags
    private final Track track; // track that the thumb moves along
    private final PPath trackBorder; // black border around the track, can be augmented by subclasses
    private final ValueDisplay valueDisplay; // editable value displayed above the track
    private final Cursor cursor; // cursor that appears in the track, directly above the thumb
    private final EventListenerList listenerList; // for notification of listeners
    private double wavelength; // the current wavelength value displayed by this control
    private final IUserComponent userComponent; // sim-sharing user component

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    // Creates a wavelength control for the visible spectrum.
    public WavelengthControl( IUserComponent userComponent, boolean sendDragMessages,
                              int trackWidth, int trackHeight ) {
        this( userComponent, sendDragMessages, trackWidth, trackHeight,
              VisibleColor.MIN_WAVELENGTH, VisibleColor.MAX_WAVELENGTH,
              UV_TRACK_COLOR, UV_LABEL_COLOR,
              IR_TRACK_COLOR, IR_LABEL_COLOR );
    }

    // Creates a wavelength control for a specified range of wavelengths.
    public WavelengthControl( IUserComponent userComponent, boolean sendDragMessages,
                              int trackWidth, int trackHeight,
                              double minWavelength, double maxWavelength ) {
        this( userComponent, sendDragMessages,
              trackWidth, trackHeight,
              minWavelength, maxWavelength,
              UV_TRACK_COLOR, UV_LABEL_COLOR,
              IR_TRACK_COLOR, IR_LABEL_COLOR );
    }

    /**
     * Creates a wavelength control for a specified range of wavelengths.
     * Specified colors are used for UV and IR ranges.
     *
     * @param userComponent
     * @param sendDragMessages
     * @param trackWidth
     * @param trackHeight
     * @param minWavelength minimum wavelength, in nanometers
     * @param maxWavelength maximum wavelength, in nanometers
     * @param uvTrackColor  color used for UV label
     * @param uvLabelColor  color used for UV track
     * @param irTrackColor  color used for IR label
     * @param irLabelColor  color used for IR track
     * @throws IllegalArgumentException if minWavelength >= maxWavelength
     */
    public WavelengthControl( IUserComponent userComponent, boolean sendDragMessages,
                              int trackWidth, int trackHeight,
                              double minWavelength, double maxWavelength,
                              Color uvTrackColor, Color uvLabelColor,
                              Color irTrackColor, Color irLabelColor ) {
        super();

        if ( minWavelength >= maxWavelength ) {
            throw new IllegalArgumentException( "have you reversed the minWavelength and maxWavelength args?" );
        }

        this.userComponent = userComponent;
        this.minWavelength = minWavelength;
        this.maxWavelength = maxWavelength;
        this.wavelength = this.minWavelength;
        uvColor = uvTrackColor;
        irColor = irTrackColor;
        listenerList = new EventListenerList();

        thumb = new Thumb( THUMB_SIZE.width, THUMB_SIZE.height );
        track = new Track( trackWidth, trackHeight, minWavelength, maxWavelength, uvTrackColor, uvLabelColor, irTrackColor, irLabelColor );
        valueDisplay = new ValueDisplay();
        cursor = new Cursor( CURSOR_WIDTH, track.getFullBounds().getHeight() );

        /*
         * Put a border around the track.
         * We don't stroke the track itself because stroking the track will affect its bounds,
         * and will thus affect the drag handle behavior.
         * Having a separate border also gives subclasses a place to add markings (eg, tick marks)
         * without affecting the track's bounds.
         */
        trackBorder = new PPath();
        trackBorder.setPathTo( new Rectangle2D.Double( 0, 0, track.getFullBounds().getWidth(), track.getFullBounds().getHeight() ) );
        trackBorder.setStroke( new BasicStroke( 1f ) );
        trackBorder.setStrokePaint( Color.BLACK );

        // rendering order
        addChild( track );
        addChild( trackBorder );
        addChild( valueDisplay );
        addChild( cursor );
        addChild( thumb );

        // Track position never changes and defines the origin.
        track.setOffset( 0, 0 );

        // Track interactivity
        track.addInputEventListener( new CursorHandler() );
        track.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mousePressed( PInputEvent event ) {
                handleTrackClick( event.getPositionRelativeTo( track ) );
            }
        } );

        // Knob interactivity
        thumb.addInputEventListener( new CursorHandler() );
        thumb.addInputEventListener( new SliderThumbDragHandler( this.userComponent, sendDragMessages,
                                                                 Orientation.HORIZONTAL, this, track, thumb, new DoubleRange( minWavelength, maxWavelength ),
                                                                 new VoidFunction1<Double>() {
                                                                     public void apply( Double wavelength ) {
                                                                         setWavelength( wavelength );
                                                                     }
                                                                 } ) );

        // Value Display interactivity
        valueDisplay.addInputEventListener( new CursorHandler() );

        // Default state
        updateUI();
    }

    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------

    public double getMinWavelength() {
        return minWavelength;
    }

    public double getMaxWavelength() {
        return maxWavelength;
    }

    /**
     * Sets the wavelength.
     *
     * @param wavelength wavelength in nanometers
     * @throws IllegalArgumentException if wavelength is outside of min/max range
     */
    public void setWavelength( double wavelength ) {

        if ( wavelength < minWavelength || wavelength > maxWavelength ) {
            throw new IllegalArgumentException( "wavelength out of range: " + wavelength );
        }

        if ( wavelength != this.wavelength ) {
            this.wavelength = wavelength;
            updateUI();
            fireChangeEvent( new ChangeEvent( this ) );
        }
    }

    public double getWavelength() {
        return wavelength;
    }

    /**
     * Gets the color that corresponds to a specified wavelength.
     *
     * @param wavelength the wavelength, in nanometers
     * @return Color
     */
    public Color getWavelengthColor( double wavelength ) {
        Color color;
        if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
            color = uvColor;
        }
        else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
            color = irColor;
        }
        else {
            color = VisibleColor.wavelengthToColor( wavelength );
        }
        return color;
    }

    /**
     * Gets the color that corresponds to the selected wavelength.
     *
     * @return Color
     */
    public Color getWavelengthColor() {
        return getWavelengthColor( wavelength );
    }

    /**
     * Sets the foreground and background colors of the
     * text field used to display the current value.
     */
    public void setTextFieldColors( Color foreground, Color background ) {
        valueDisplay.formattedTextField.setForeground( foreground );
        valueDisplay.formattedTextField.setBackground( background );
    }

    /**
     * Sets the font of the text field used to display the current value.
     *
     * @param font
     */
    public void setTextFieldFont( Font font ) {
        valueDisplay.formattedTextField.setFont( font );
        valueDisplay.computeBounds();
        updateUI();
    }

    /**
     * Set the number of columns in the editable text field.
     *
     * @param columns
     */
    public void setTextFieldColumns( int columns ) {
        valueDisplay.formattedTextField.setColumns( columns );
        valueDisplay.computeBounds();
        updateUI();
    }

    /**
     * Sets the foreground color of the units label that appears to
     * the right of the text field that displays the current value.
     * The background is transparent.
     *
     * @param color
     */
    public void setUnitsForeground( Color color ) {
        valueDisplay.unitsLabel.setForeground( color );
    }

    /**
     * Sets the font of the units label that appears to
     * the right of the text field that displays the current value.
     *
     * @param font
     */
    public void setUnitsFont( Font font ) {
        valueDisplay.unitsLabel.setFont( font );
        valueDisplay.computeBounds();
        updateUI();
    }

    /**
     * Sets the color of the cursor that appears above the slider thumb.
     *
     * @param color
     */
    public void setCursorColor( Color color ) {
        cursor.setStrokePaint( color );
    }

    /**
     * Sets the size of the slider thumb.
     *
     * @param width
     * @param height
     */
    public void setThumbSize( float width, float height ) {
        thumb.setSize( width, height );
    }

    /**
     * Sets the stroke used to outline the slider thumb.
     *
     * @return
     */
    public void setThumbStroke( Stroke stroke ) {
        thumb.setStroke( stroke );
    }

    /**
     * Sets the color used to stroke the slider thumb.
     *
     * @return
     */
    public void setThumbStrokeColor( Color strokeColor ) {
        thumb.setStrokePaint( strokeColor );
    }

    /**
     * Adds a listener to the thumb, so we tell when we're dragging it.
     *
     * @param listener
     */
    public void addThumbListener( PInputEventListener listener ) {
        thumb.addInputEventListener( listener );
    }

    /**
     * Controls visibility of the cursor, the small rectangle that moves in
     * the track to indicate where the thumb is pointing.
     *
     * @param visible true or false
     */
    public void setCursorVisible( boolean visible ) {
        cursor.setVisible( visible );
    }

    /*
     * Gets the track's border.
     * The border can be augmented with additional markings (eg, tick marks) by subclasses.
     *
     * @return PNode
     */
    protected PNode getTrackBorder() {
        return trackBorder;
    }

    /*
     * Gets the full bounds of the track.
     * Needed for properly aligning marks that subclasses might add to track border.
     * @return PBounds
     */
    protected PBounds getTrackFullBounds() {
        return track.getFullBounds();
    }

    //----------------------------------------------------------------------------
    // Private methods
    //----------------------------------------------------------------------------

    // Handles a mouse click on the track.
    private void handleTrackClick( Point2D trackPoint ) {
        final double bandwidth = maxWavelength - minWavelength;
        final double trackWidth = track.getFullBounds().getWidth();
        final double wavelength = minWavelength + ( ( trackPoint.getX() / trackWidth ) * bandwidth );
        setWavelength( wavelength );
    }

    // Commits the text field by validating the user's input, reverting if the input is bogus.
    private void commitTextField( IParameterValue commitAction ) {
        String text = valueDisplay.getText();
        try {
            final double wavelength = Double.valueOf( text );
            if ( wavelength >= minWavelength && wavelength <= maxWavelength ) {
                sendCommittedMessage( commitAction, wavelength );
                setWavelength( wavelength );
            }
            else {
                // Input was a number, but it was out of range
                revertTextField( ParameterValues.rangeError, text );
            }
        }
        catch ( NumberFormatException nfe ) {
            // input was not a number
            revertTextField( ParameterValues.numberFormatException, text );
        }
    }

    // Reverts the text field when it contains bogus input
    private void revertTextField( IParameterValue errorType, String bogusValue ) {
        sendCorrectedMessage( errorType, bogusValue, wavelength );
        badInput();
        valueDisplay.setValue( wavelength );
    }

    // Updates the UI to match a the current wavelength.
    private void updateUI() {

        final double bandwidth = maxWavelength - minWavelength;

        PBounds trackBounds = track.getFullBounds();
        final double valueDisplayWidth = valueDisplay.getFullBounds().getWidth();
        final double valueDisplayHeight = valueDisplay.getFullBounds().getHeight();

        // Knob color
        Color wavelengthColor = getWavelengthColor();
        thumb.setPaint( wavelengthColor );

        // Knob position: below the track with tip positioned at wavelength
        final double trackX = trackBounds.getX();
        final double trackWidth = trackBounds.getWidth();
        final double thumbX = trackX + ( trackWidth * ( ( wavelength - minWavelength ) / bandwidth ) );
        final double thumbY = trackBounds.getHeight();
        thumb.setOffset( thumbX, thumbY );

        // Value display: above the track, centered above the thumb
        valueDisplay.setValue( wavelength );
        final double valueX = thumbX - ( valueDisplayWidth / 2 );
        final double valueY = -( valueDisplayHeight + VALUE_Y_OFFSET );
        valueDisplay.setOffset( valueX, valueY );

        // Cursor position: inside the track, centered above the thumb
        cursor.setOffset( thumbX, 0 );
    }

    // Produces an audible beep, used to indicate invalid text entry.
    private void badInput() {
        Toolkit.getDefaultToolkit().beep();
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    // The slider thumb (aka knob).
    private static class Thumb extends PPath {

        // Constructor
        public Thumb( float width, float height ) {
            super();
            setStroke( THUMB_STROKE );
            setPaint( THUMB_STROKE_COLOR );
            setSize( width, height );
        }

        // Sets the size of the thumb by rebuilding the thumb's path. Origin (0,0) is at tip of the thumb.
        public void setSize( float width, float height ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( 0f, 0f ); // tip of the thumb
            path.lineTo( 0.5f * width, 0.3f * height );
            path.lineTo( 0.5f * width, 1f * height );
            path.lineTo( -0.5f * width, 1f * height );
            path.lineTo( -0.5f * width, 0.3f * height );
            path.closePath();
            setPathTo( path );
        }
    }

    // The track that the slider thumb moves in.
    private static class Track extends PComposite {

        // Constructor
        public Track( int trackWidth, int trackHeight,
                      double minWavelength, double maxWavelength,
                      Color uvTrackColor, Color uvLabelColor,
                      Color irTrackColor, Color irLabelColor ) {
            super();

            final double totalBandwidth = maxWavelength - minWavelength;
            final double uvBandwidth = VisibleColor.MIN_WAVELENGTH - minWavelength;
            final double irBandwidth = maxWavelength - VisibleColor.MAX_WAVELENGTH;
            final double uvTrackWidth = ( uvBandwidth / totalBandwidth ) * trackWidth;
            final double irTrackWidth = ( irBandwidth / totalBandwidth ) * trackWidth;

            // Track image for the entire spectrum
            Image trackImage = new LinearSpectrumImageFactory().createHorizontalSpectrum( trackWidth, trackHeight, minWavelength, maxWavelength, uvTrackColor, irTrackColor );
            PImage trackNode = new PImage( trackImage );
            trackNode.setOffset( 0, 0 );
            addChild( trackNode );

            // Label the UV portion of the track
            if ( uvTrackWidth > 0 ) {

                PText uvLabel = new PText( UV_STRING );
                uvLabel.setTextPaint( uvLabelColor );

                // Scale to fit the track height
                uvLabel.scale( ( trackHeight * LABEL_TRACK_RATIO ) / uvLabel.getFullBounds().getHeight() );

                // Add the UV label if the UV portion of the track is wide enough
                if ( uvTrackWidth > uvLabel.getFullBounds().getWidth() ) {

                    addChild( uvLabel );

                    // center in the UV portion of the track
                    uvLabel.setOffset( ( uvTrackWidth - uvLabel.getFullBounds().getWidth() ) / 2,
                                       ( trackHeight - uvLabel.getFullBounds().getHeight() ) / 2 );
                }
            }

            // Label the IR portion of the track
            if ( irTrackWidth > 0 ) {

                PText irLabel = new PText( IR_STRING );
                irLabel.setTextPaint( irLabelColor );

                // Scale label to fit the track height
                irLabel.scale( ( trackHeight * LABEL_TRACK_RATIO ) / irLabel.getFullBounds().getHeight() );

                // Add the IR label if the IR portion of the track is wide enough
                if ( irTrackWidth > irLabel.getFullBounds().getWidth() ) {

                    addChild( irLabel );

                    // center in the IR portion of the track
                    irLabel.setOffset( trackWidth - irTrackWidth + ( ( irTrackWidth - irLabel.getFullBounds().getWidth() ) / 2 ),
                                       ( trackHeight - irLabel.getFullBounds().getHeight() ) / 2 );
                }
            }
        }
    }

    // Displays and edits the wavelength value as text.
    private class ValueDisplay extends PNode {

        public final JFormattedTextField formattedTextField;
        public final JLabel unitsLabel;
        private final PSwing pswing;

        // Constructor
        public ValueDisplay() {
            super();

            // units label, appears to the right of the text field
            unitsLabel = new JLabel( UNITS_LABEL );

            // editable text field
            formattedTextField = new JFormattedTextField();
            formattedTextField.setColumns( TEXT_FIELD_COLUMNS );
            formattedTextField.setHorizontalAlignment( JTextField.RIGHT );

            // text entry
            formattedTextField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    commitTextField( ParameterValues.enterKey );
                }
            } );

            // focus
            formattedTextField.addFocusListener( new FocusListener() {
                // Selects the entire value text field when it gains focus.
                public void focusGained( FocusEvent e ) {
                    formattedTextField.selectAll();
                }

                // Processes the text field when it loses focus.
                public void focusLost( FocusEvent e ) {
                    commitTextField( ParameterValues.focusLost );
                }
            } );

            // up/down arrow keys
            formattedTextField.addKeyListener( new KeyAdapter() {
                @Override
                public void keyPressed( KeyEvent event ) {
                    if ( event.getKeyCode() == KeyEvent.VK_UP ) {
                        final double newWavelength = wavelength + 1;
                        if ( newWavelength <= maxWavelength ) {
                            sendCommittedMessage( ParameterValues.upKey, newWavelength );
                            setWavelength( newWavelength );
                        }
                        else {
                            sendCorrectedMessage( ParameterValues.rangeError, String.valueOf( newWavelength ), wavelength );
                            badInput();
                        }
                    }
                    else if ( event.getKeyCode() == KeyEvent.VK_DOWN ) {
                        final double newWavelength = wavelength - 1;
                        if ( newWavelength >= minWavelength ) {
                            sendCommittedMessage( ParameterValues.downKey, newWavelength );
                            setWavelength( newWavelength );
                        }
                        else {
                            sendCorrectedMessage( ParameterValues.rangeError, String.valueOf( newWavelength ), wavelength );
                            badInput();
                        }
                    }
                }
            } );

            // Layout
            JPanel panel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            layout.setInsets( new Insets( 0, 3, 0, 0 ) );
            panel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            layout.addComponent( formattedTextField, row, col++ );
            layout.addComponent( unitsLabel, row, col );

            // Opacity
            panel.setOpaque( false );
            unitsLabel.setOpaque( false );

            // Piccolo wrapper
            pswing = new PSwing( panel );
            addChild( pswing );
        }

        // Sets the value displayed by the text field.
        public void setValue( double wavelength ) {
            String s = VALUE_FORMAT.format( wavelength );
            formattedTextField.setText( s );
        }

        public String getText() {
            return formattedTextField.getText().toLowerCase();
        }

        // WORKAROUND: Call this after doing something that changes the size of a Swing component
        public void computeBounds() {
            pswing.updateBounds();
        }
    }

    // Rectangular "cursor" that appears in the track directly above the thumb. Origin is at top center of cursor.
    private static class Cursor extends PPath {

        // Constructor
        public Cursor( double width, double height ) {
            super();
            setPathTo( new Rectangle2D.Double( -width / 2, 0, width, height ) );
            setStroke( CURSOR_STROKE );
            setStrokePaint( CURSOR_COLOR );
        }
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    public void addChangeListener( ChangeListener listener ) {
        listenerList.add( ChangeListener.class, listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        listenerList.remove( ChangeListener.class, listener );
    }

    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Data collection
    //----------------------------------------------------------------------------

    // User does something to commit the text field.
    protected void sendCommittedMessage( IParameterValue commitAction, double value ) {
        SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.textField, UserActions.textFieldCommitted,
                                           ParameterSet.parameterSet( ParameterKeys.commitAction, commitAction ).add( ParameterKeys.value, value ) );
    }

    // Invalid input is encountered and corrected in the text field.
    protected void sendCorrectedMessage( IParameterValue errorType, String bogusValue, double correctedValue ) {
        sendUserMessage( userComponent, UserComponentTypes.textField, textFieldCorrected,
                         parameterSet( ParameterKeys.errorType, errorType ).add( ParameterKeys.value, bogusValue ).add( ParameterKeys.correctedValue, correctedValue ) );
    }

    // Simple test, see TestWavelengthControl for a more extensive test.
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                getLayer().addChild( new WavelengthControl( new UserComponent( "wavelengthControl" ), true, 200, 50 ) {{setOffset( 100, 100 );}} );
            }} );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            setSize( 500, 500 );
        }}.setVisible( true );
    }
}
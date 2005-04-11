/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * FieldMeterGraphic is a meter that measure a magnetic field.
 * It displays the magnitude (total and X/Y components) and direction of
 * the magnetic field at a point in 2D space. Values are displayed in
 * a coordinate system in which +X is to the right, +Y is up, and 
 * +direction is counterclockwise, and 0 degrees points down the
 * positive X axis.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FieldMeterGraphic extends CompositePhetGraphic
    implements SimpleObserver, ApparatusPanel2.ChangeListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Center of the crosshairs (MUST MATCH IMAGE FILE!)
    private static final Point CROSSHAIRS_LOCATION = new Point( 71, 27 );
    
    // Bottom center of the title (MUST MATCH IMAGE FILE!)
    private static final Point TITLE_LOCATION = new Point( 0, 84 );
    
    // Bottom right of the top-most field (MUST MATCH IMAGE FILE!)
    private static final Point FIELD_LOCATION = new Point( 35, 112 );
    
    // Horizontal spacing between fields (MUST MATCH IMAGE FILE!)
    private static final int FIELD_SPACING = 24;
    
    // Title color
    private static final Color TITLE_COLOR = Color.WHITE;
    
    // Field color
    private static final Color FIELD_COLOR = Color.WHITE;
    
    // Title font
    private static final Font TITLE_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    
    // Field font
    private static final Font FIELD_FONT = new Font( "SansSerif", Font.PLAIN, 15 );
    
    // Field format
    private static final String FIELD_FORMAT = "###0.00";
    
    // Offensive formatted values and their corrections. Dependent on FIELD_FORMAT !!
    private static final String STRING_NEGATIVE_ZERO = "-0.00";
    private static final String STRING_POSITIVE_ZERO = "0.00";
    private static final String STRING_NEGATIVE_PI = "-180.00";
    private static final String STRING_POSITIVE_PI = "180.00";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle _parentBounds;
    private AbstractMagnet _magnetModel;
    private PhetTextGraphic _bText, _bxText, _byText, _angleText;
    private NumberFormat _formatter;
    private Point _point; // reusable point
    private Vector2D _fieldVector; // reusable vector
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param magnetModel the magnet whose field is being probed
     */
    public FieldMeterGraphic( Component component, AbstractMagnet magnetModel ) {
        super( component );
        assert( component != null );
        assert( magnetModel != null );
        
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _parentBounds = new Rectangle( 0, 0, component.getWidth(), component.getHeight() );
        
        _formatter = new DecimalFormat( FIELD_FORMAT );
        
        _point = new Point();
        _fieldVector = new Vector2D();
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Probe body, registration point at center of crosshairs.
        PhetImageGraphic body = new PhetImageGraphic( component, FaradayConfig.FIELD_METER_IMAGE );
        body.setRegistrationPoint( CROSSHAIRS_LOCATION.x, CROSSHAIRS_LOCATION.y );
        addGraphic( body );
        
        // Title text, registration point at bottom center.
        String titleString = SimStrings.get( "FieldMeter.title" );
        PhetTextGraphic titleText = new PhetTextGraphic( component, TITLE_FONT, titleString, TITLE_COLOR, 0, 0 );
        int width = titleText.getBounds().width;
        int height = titleText.getBounds().height;
        titleText.setRegistrationPoint( width /2, height ); // bottom center
        titleText.setLocation( TITLE_LOCATION.x, TITLE_LOCATION.y );
        addGraphic( titleText );
        
        // Fields, registration point is set in update method.
        {
            int x = FIELD_LOCATION.x;
            int y = FIELD_LOCATION.y;
            
            // B text
            _bText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _bText );

            // Bx text
            _bxText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _bxText );

            // By text
            _byText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _byText );

            // Angle text
            _angleText = new PhetTextGraphic( component, FIELD_FONT, "", FIELD_COLOR, x, y );
            y = y + FIELD_SPACING;
            addGraphic( _angleText );
        }
        
        // Setup interactivity.
        InteractivityListener listener = new InteractivityListener();
        super.setCursorHand();
        super.addTranslationListener( listener );
        super.addMouseInputListener( listener );
        
        // Synchronize view with model.
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // overrides
    //----------------------------------------------------------------------------
    
    /**
     * When the graphic is made visible, update it to match the model.
     * 
     * @param visible true or false
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        if ( isVisible() ) {
            
            // Get the values, adjust the coordinate system.
            _point.setLocation( getX(), getY() );
            _magnetModel.getStrength( _point, _fieldVector /* output */ );
            double b = _fieldVector.getMagnitude();
            double bx = _fieldVector.getX();
            double by = -( _fieldVector.getY() ); // +Y is up
            double angle = -( _fieldVector.getAngle() );  // +angle is counterclockwise
    
            // Format the values.
            String bString = _formatter.format( b );
            String bxString = _formatter.format( bx );
            String byString = _formatter.format( by );
            String angleString = _formatter.format( Math.toDegrees( angle ) );
            
            /*
             * Correct some offensive looking values.
             * We need to perform this format-dependent check on the strings
             * because a value like -0.00005 will display as -0.00.
             */
            {
                // B
                if ( bString.equals( STRING_NEGATIVE_ZERO ) ) {
                    bString = STRING_POSITIVE_ZERO;
                }

                if ( bString.equals( STRING_POSITIVE_ZERO ) ) {
                    // If B displays 0, all others should display zero.
                    bxString = STRING_POSITIVE_ZERO;
                    byString = STRING_POSITIVE_ZERO;
                    angleString = STRING_POSITIVE_ZERO;
                }
                else {
                    // Bx
                    if ( bxString.equals( STRING_NEGATIVE_ZERO ) ) {
                        bxString = STRING_POSITIVE_ZERO;
                    }

                    // By
                    if ( byString.equals( STRING_NEGATIVE_ZERO ) ) {
                        byString = STRING_POSITIVE_ZERO;
                    }

                    // Theta
                    if ( angleString.equals( STRING_NEGATIVE_ZERO ) ) {
                        angleString = STRING_POSITIVE_ZERO;
                    }
                    else if ( angleString.equals( STRING_NEGATIVE_PI ) ) {
                        angleString = STRING_POSITIVE_PI;
                    }
                }
            }
            
            // Set the text graphics.
            _bText.setText( bString );
            _bxText.setText( bxString );
            _byText.setText( byString );
            _angleText.setText( angleString );
            
            // Right justify the text graphics (registration point at bottom right)
            _bText.setRegistrationPoint( _bText.getWidth(), _bText.getHeight() );
            _bxText.setRegistrationPoint( _bxText.getWidth(), _bxText.getHeight() );
            _byText.setRegistrationPoint( _byText.getWidth(), _byText.getHeight() );
            _angleText.setRegistrationPoint( _angleText.getWidth(), _angleText.getHeight() );
        }
    }
    
    //----------------------------------------------------------------------------
    // ApparatusPanel2.ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.view.ApparatusPanel2.ChangeListener#canvasSizeChanged(edu.colorado.phet.common.view.ApparatusPanel2.ChangeEvent)
     */
    public void canvasSizeChanged( ChangeEvent event ) {
        _parentBounds.setBounds( 0, 0, event.getCanvasSize().width, event.getCanvasSize().height );   
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /**
     * InteractivityListener is an inner class that handles interactivity.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class InteractivityListener extends MouseInputAdapter implements TranslationListener {
        
        private boolean _dragEnabled;
        
        public InteractivityListener() {
            super();
            _dragEnabled = true;
        }
        
        public void translationOccurred( TranslationEvent e ) {
            if ( _dragEnabled ) {
                
                if ( ! _parentBounds.contains( e.getMouseEvent().getPoint() ) ) {
                    // Ignore the translate if the mouse is outside the apparatus panel.
                    _dragEnabled = false;
                }
                else {
                    // Translate if the mouse cursor is inside the parent component.
                    int x = getX() + e.getDx();
                    int y = getY() + e.getDy();
                    setLocation( x, y );
                    update();
                }
            }
        }
        
        public void mouseDragged( MouseEvent event ) {
            if ( !_dragEnabled && getBounds().contains( event.getPoint() ) ) {
                _dragEnabled = true;
            }
        }
        
        public void mouseReleased( MouseEvent event ) {
            _dragEnabled = true;
        }
    }
}
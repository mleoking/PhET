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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;


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
public class FieldMeterGraphic extends CompositePhetGraphic implements SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Center of the crosshairs (MUST MATCH IMAGE FILE!)
    private static final Point CROSSHAIRS_LOCATION = new Point( 71, 27 );
    
    // Bottom center of the title (MUST MATCH IMAGE FILE!)
    private static final Point TITLE_LOCATION = new Point( 0, 84 );
    
    // Bottom right of the top-most field (MUST MATCH IMAGE FILE!)
    private static final Point FIELD_LOCATION = new Point( 35, 111 );
    
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    private PhetTextGraphic _bText, _bxText, _byText, _angleText;
    private NumberFormat _formatter;
    
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
        
        _formatter = new DecimalFormat( FIELD_FORMAT );
        
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
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                // Translate if the mouse cursor is inside the parent component.
                if ( getComponent().contains( e.getMouseEvent().getPoint() ) ) {
                    int x = getX() + e.getDx();
                    int y = getY() + e.getDy();
                    setLocation( x, y );
                    update();
                }
            }
        } );
        
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
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        if ( isVisible() ) {
            AbstractVector2D B = _magnetModel.getStrength( getLocation() );
            
            // Format field values, adjust coordinate system.
            String b = _formatter.format( new Double( B.getMagnitude() ) );
            String bx = _formatter.format( new Double( B.getX() ) );
            String by = _formatter.format( new Double( -(B.getY()) ) ); // +Y is up
            String angle = _formatter.format( new Double( Math.toDegrees( -(B.getAngle()) ) ) ); // +angle is counterclockwise
            
            // Set field values
            _bText.setText( b );
            _bxText.setText( bx );
            _byText.setText( by );
            _angleText.setText( angle );
            
            // Right justify (registration point at bottom right)
            _bText.setRegistrationPoint( _bText.getBounds().width, _bText.getBounds().height );
            _bxText.setRegistrationPoint( _bxText.getBounds().width, _bxText.getBounds().height );
            _byText.setRegistrationPoint( _byText.getBounds().width, _byText.getBounds().height );
            _angleText.setRegistrationPoint( _angleText.getBounds().width, _angleText.getBounds().height );
        }
    }
}
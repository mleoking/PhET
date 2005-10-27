/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.Random;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;


/**
 * MoleculeAnimation is the animation of a molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MoleculeAnimation extends CompositePhetGraphic implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension BACKGROUND_SIZE = new Dimension( 250, 250 );
    
    private static final double MAX_DISTANCE = 10;  // distance in pixels we would move if closeness=1
    
    private static final double EXPLOSION_ACCELERATION_RATE = 0.10;
    
    private static final Point MOLECULE_POINT = new Point( -70, 300 );
   
    private static final float DASH_SIZE = 3f;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PhetShapeGraphic _animationFrame;
    private CompositePhetGraphic _molecule;
    private PhetImageGraphic _moleculePart1;
    private PhetImageGraphic _moleculePart2;
    private PhetImageGraphic _moleculePart3;
    private HTMLGraphic _closenessGraphic;
    private String _closenessFormat;
    private GameManager _gameManager;
    private PhetImageGraphic _explosionGraphic;
    
    private double _closeness;
    private Point _moleculeHome;
    private Random _random;
    private boolean _enabled;
    private double _dx1, _dy1, _dx2, _dy2, _dx3, _dy3;
    private boolean _isExploding;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MoleculeAnimation( Component component ) {
        super( component );
        
        setIgnoreMouse( true );
        
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        _random = new Random();
        
        // background that everything sits on
        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        background.setColor(  new Color( 215, 215, 215 ) );
        addGraphic( background );
        
        // Visual cues to indicate that this is a "zoomed in" view.
        {
            // Dashed line strokes
            float[] dashArray = { DASH_SIZE, DASH_SIZE };
            BasicStroke whiteDashStroke = new BasicStroke( 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0f, dashArray, 0f );
            BasicStroke blackDashStroke = new BasicStroke( 1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 0f, dashArray, DASH_SIZE );

            /* 
             * 2 lines starting at the top-level corner, 
             * the inverse of each other so they show up on all backgrounds.
             */
            {
                GeneralPath topLineShape = new GeneralPath();
                topLineShape.moveTo( 0, 0 );
                topLineShape.lineTo( MOLECULE_POINT.x, MOLECULE_POINT.y );

                PhetShapeGraphic topWhiteDashedLine = new PhetShapeGraphic( component );
                topWhiteDashedLine.setShape( topLineShape );
                topWhiteDashedLine.setColor( Color.WHITE );
                topWhiteDashedLine.setStroke( whiteDashStroke );

                addGraphic( topWhiteDashedLine );
                PhetShapeGraphic topBlackDashedLine = new PhetShapeGraphic( component );
                topBlackDashedLine.setShape( topLineShape );
                topBlackDashedLine.setColor( Color.BLACK );
                topBlackDashedLine.setStroke( blackDashStroke );
                addGraphic( topBlackDashedLine );
            }

            /* 
             * 2 lines starting at the bottom-right corner, 
             * the inverse of each other so they show up on all backgrounds.
             */
            {
                GeneralPath bottomLineShape = new GeneralPath();
                bottomLineShape.moveTo( BACKGROUND_SIZE.width, BACKGROUND_SIZE.height );
                bottomLineShape.lineTo( MOLECULE_POINT.x, MOLECULE_POINT.y );

                PhetShapeGraphic bottomWhiteDashedLine = new PhetShapeGraphic( component );
                bottomWhiteDashedLine.setShape( bottomLineShape );
                bottomWhiteDashedLine.setColor( Color.WHITE );
                bottomWhiteDashedLine.setStroke( whiteDashStroke );
                addGraphic( bottomWhiteDashedLine );

                PhetShapeGraphic bottomBlackDashedLine = new PhetShapeGraphic( component );
                bottomBlackDashedLine.setShape( bottomLineShape );
                bottomBlackDashedLine.setColor( Color.BLACK );
                bottomBlackDashedLine.setStroke( blackDashStroke );
                addGraphic( bottomBlackDashedLine );
            }

            /* 
             * Tiny rectangle that indicates where the molecule actually is.
             * All of the above lines terminate at the center of this rectangle.
             */
            Rectangle2D r = new Rectangle2D.Double( 0, 0, 8, 8 );
            PhetShapeGraphic tinyMolecule = new PhetShapeGraphic( component );
            tinyMolecule.setShape( r );
            tinyMolecule.setStroke( new BasicStroke( 1f ) );
            tinyMolecule.setBorderColor( Color.WHITE );
            tinyMolecule.centerRegistrationPoint();
            tinyMolecule.setLocation( MOLECULE_POINT.x, MOLECULE_POINT.y );
            addGraphic( tinyMolecule );
        }
        
        // The frame around the molecule animation
        _animationFrame = new PhetShapeGraphic( component );
        Rectangle frameShape = new Rectangle( 0, 0, BACKGROUND_SIZE.width - 30, BACKGROUND_SIZE.height - 45 );
        _animationFrame.setShape( frameShape );
        _animationFrame.setColor( Color.BLACK );
        _animationFrame.setBorderColor( Color.BLACK );
        _animationFrame.setStroke( new BasicStroke(1f) );
        addGraphic( _animationFrame );
        _animationFrame.setRegistrationPoint( _animationFrame.getWidth()/2, 0 ); // top center
        _animationFrame.setLocation( BACKGROUND_SIZE.width/2, 15 );
        
        // The display that shows how "close" we are to matching the output pulse.
        _closenessGraphic = new HTMLGraphic( component );
        _closenessGraphic.setColor( Color.BLACK );
        _closenessGraphic.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        _closenessFormat = SimStrings.get( "closenessReadout" );
        Object[] args = { "00" };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
        addGraphic( _closenessGraphic );
        _closenessGraphic.setRegistrationPoint( _closenessGraphic.getWidth()/2, _closenessGraphic.getHeight() ); // bottom center
        _closenessGraphic.setLocation( BACKGROUND_SIZE.width/2, BACKGROUND_SIZE.height - 5 );
        
        // explosion
        _explosionGraphic = new PhetImageGraphic( component, ShaperConstants.KABOOM_IMAGE );
        _explosionGraphic.setRegistrationPoint( _explosionGraphic.getWidth()/2, 0 ); // top center
        _explosionGraphic.scale( 0.4 ); // scale after setting registration point!
        _explosionGraphic.setLocation( _animationFrame.getLocation() );
        addGraphic( _explosionGraphic );
        
        // molecule
        _molecule = new CompositePhetGraphic( component );
        {           
            _moleculePart1 = new PhetImageGraphic( component );
            _moleculePart2 = new PhetImageGraphic( component );
            _moleculePart3 = new PhetImageGraphic( component );
            _molecule.addGraphic( _moleculePart1 );
            _molecule.addGraphic( _moleculePart2 );
            _molecule.addGraphic( _moleculePart3 );
        }
        _moleculeHome = new Point( 30, 15 );
        _molecule.setLocation( _moleculeHome );
        _molecule.scale( 0.50 );
        addGraphic( _molecule );
        
        reset();
    }
    
    //----------------------------------------------------------------------------
    // Reset
    //----------------------------------------------------------------------------
    
    public void reset() {
        _enabled = true;
        _isExploding = false;
        setCloseness( 0 );
        _explosionGraphic.setVisible( false );
        _molecule.setLocation( _moleculeHome );
        _moleculePart1.setLocation( 0, 0 );
        _moleculePart2.setLocation( 0, 0 );
        _moleculePart3.setLocation( 0, 0 );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setMolecule( int index ) {
        String part1 = ShaperConstants.IMAGES_DIRECTORY + "molecule" + index + "_part1.png";
        String part2 = ShaperConstants.IMAGES_DIRECTORY + "molecule" + index + "_part2.png";
        String part3 = ShaperConstants.IMAGES_DIRECTORY + "molecule" + index + "_part3.png";
        _moleculePart1.setImageResourceName( part1 );
        _moleculePart2.setImageResourceName( part2 );
        _moleculePart3.setImageResourceName( part3 );
    }
    
    /**
     * Enables or disables clipping of the molecules animation.
     * If clipping is enabled, then the molecule will be drawn
     * only inside the animation frame.
     */
    public void setClipEnabled( boolean enabled ) {
        if ( enabled ) {
            Shape clip = _animationFrame.getNetTransform().createTransformedShape( _animationFrame.getShape() );
            _molecule.setClip( clip );
        }
        else {
            _molecule.setClip( null );
        }
    }
    
    public void setCloseness( double closeness ) {
        
        _closeness = closeness;
        
        // Set the "How close am I?" label.
        if ( closeness < 0 || closeness > 1 ) {
            throw new IllegalArgumentException( "closeness is out of range: " + closeness );
        }
        int percent = (int)( 100 * closeness );
        Object[] args = { new Integer( percent ) };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
    }
    
    public boolean isExploding() {
        return _isExploding;
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    public void setGameManager( GameManager gameManager ) {
        _gameManager = gameManager;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves the molecule when the clock ticks.
     */
    public void stepInTime( double dt ) {

        if ( _enabled ) {
            if ( _isExploding ) {
                
                // Continue the "explode" animation
                _moleculePart1.setLocation( (int) ( _moleculePart1.getX() + _dx1 ), (int) ( _moleculePart1.getY() + _dy1 ) );
                _moleculePart2.setLocation( (int) ( _moleculePart2.getX() + _dx2 ), (int) ( _moleculePart2.getY() + _dy2 ) );
                _moleculePart3.setLocation( (int) ( _moleculePart3.getX() + _dx3 ), (int) ( _moleculePart3.getY() + _dy3 ) );
                
                // Accelerate
                final double a = EXPLOSION_ACCELERATION_RATE;
                _dx1 += _dx1 * a;
                _dy1 += _dy1 * a;
                _dx2 += _dx2 * a;
                _dy2 += _dy2 * a;
                _dx3 += _dx3 * a;
                _dy3 += _dy3 * a;
                    
                if ( _molecule.getClip() != null ) {
                    // Are we still visbile in the animation frame?
                    if ( Math.abs( _moleculePart1.getX() ) > 2 * _animationFrame.getWidth() &&
                         Math.abs( _moleculePart1.getY() ) > 2 * _animationFrame.getHeight() ) {
                        _enabled = false; // animation is done
                        _gameManager.gameOver();
                    }
                }
                else {
                    // Are we still visible in the apparatus panel?
                    if ( Math.abs( _moleculePart1.getX() ) > 2 * ShaperConstants.APP_FRAME_WIDTH &&
                         Math.abs( _moleculePart1.getY() ) > 2 * ShaperConstants.APP_FRAME_HEIGHT ) {
                        _enabled = false; // animation is done
                        _gameManager.gameOver();
                    }
                }
            }
            else if ( _closeness < ShaperConstants.CLOSENESS_MATCH ) {
                // Randomly move the molecule around.
                double d = ( _random.nextGaussian() * _closeness * _closeness * MAX_DISTANCE ) * ( Math.random() > 0.5 ? 1 : -1 );
                double theta = Math.random() * Math.PI * 2;
                double dx = d * Math.cos( theta );
                double dy = d * Math.sin( theta );
                _molecule.setLocation( (int) ( _moleculeHome.x + dx ), (int) ( _moleculeHome.y + dy ) );
            }
            else {
                // Start the "explode" animation.
                _explosionGraphic.setVisible( true );
                _isExploding = true;
                double theta1 = Math.random() * Math.PI * 2;
                double theta2 = theta1 + Math.toRadians( 120 );
                double theta3 = theta2 + Math.toRadians( 120 );
                _dx1 = _closeness * _closeness * MAX_DISTANCE * Math.cos( theta1 );
                _dy1 = _closeness * _closeness * MAX_DISTANCE * Math.sin( theta1 );
                _dx2 = _closeness * _closeness * MAX_DISTANCE * Math.cos( theta2 );
                _dy2 = _closeness * _closeness * MAX_DISTANCE * Math.sin( theta2 );
                _dx3 = _closeness * _closeness * MAX_DISTANCE * Math.cos( theta3 );
                _dy3 = _closeness * _closeness * MAX_DISTANCE * Math.sin( theta3 );
            }
        }
    }
}

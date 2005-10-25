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
        
        _closeness = 0;
        _random = new Random();
        _enabled = true;
        _isExploding = false;
        
        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        background.setColor(  new Color( 215, 215, 215 ) );
        addGraphic( background );
        
        _animationFrame = new PhetShapeGraphic( component );
        Rectangle frameShape = new Rectangle( 0, 0, BACKGROUND_SIZE.width - 30, BACKGROUND_SIZE.height - 45 );
        _animationFrame.setShape( frameShape );
        _animationFrame.setColor( Color.WHITE );
        _animationFrame.setBorderColor( Color.BLACK );
        _animationFrame.setStroke( new BasicStroke(1f) );
        addGraphic( _animationFrame );
        _animationFrame.setRegistrationPoint( _animationFrame.getWidth()/2, 0 ); // top center
        _animationFrame.setLocation( BACKGROUND_SIZE.width/2, 15 );
        
        _closenessGraphic = new HTMLGraphic( component );
        _closenessGraphic.setColor( Color.BLACK );
        _closenessGraphic.setFont( new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 18 ) );
        _closenessFormat = SimStrings.get( "closenessReadout" );
        Object[] args = { "00" };
        String text = MessageFormat.format( _closenessFormat, args );
        _closenessGraphic.setHTML( text );
        addGraphic( _closenessGraphic );
        _closenessGraphic.setRegistrationPoint( _closenessGraphic.getWidth()/2, _closenessGraphic.getHeight() );
        _closenessGraphic.setLocation( BACKGROUND_SIZE.width/2, BACKGROUND_SIZE.height - 5 );
        
        // molecule
        _molecule = new CompositePhetGraphic( component );
        {           
            _moleculePart1 = new PhetImageGraphic( component, ShaperConstants.MOLECULE1_PART1_IMAGE );
            _moleculePart2 = new PhetImageGraphic( component, ShaperConstants.MOLECULE1_PART2_IMAGE );
            _moleculePart3 = new PhetImageGraphic( component, ShaperConstants.MOLECULE1_PART3_IMAGE );
            _molecule.addGraphic( _moleculePart1 );
            _molecule.addGraphic( _moleculePart2 );
            _molecule.addGraphic( _moleculePart3 );
        }
        _moleculeHome = new Point( 50, 40 );
        _molecule.setLocation( _moleculeHome );
        _molecule.scale( 0.40 );
        addGraphic( _molecule );
    }
    
    /**
     * This must be called whenever the MoleculeAnimation's location
     * or parent graphic is changed.  In this simulation, call it after
     * adding to the apparatus panel and setting its location.
     */
    public void updateClip() {
        Shape clip = _animationFrame.getNetTransform().createTransformedShape( _animationFrame.getShape() );
        _molecule.setClip( clip );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
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

    public void reset() {
        _enabled = true;
        _isExploding = false;
        setCloseness( 0 );
        _molecule.setLocation( _moleculeHome );
        _moleculePart1.setLocation( 0, 0 );
        _moleculePart2.setLocation( 0, 0 );
        _moleculePart3.setLocation( 0, 0 );
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
                _dx1 += _dx1;
                _dy1 += _dy1;
                _dx2 += _dx2;
                _dy2 += _dy2;
                _dx3 += _dx3;
                _dy3 += _dy3;
                // Are we still visbile in the animation frame?
                if ( Math.abs( _moleculePart1.getX() ) > 2 * _animationFrame.getWidth() &&
                     Math.abs( _moleculePart1.getY() ) > 2 * _animationFrame.getHeight() ) {
                    _enabled = false; // animation is done
                    _gameManager.gameOver();
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

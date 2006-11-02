/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.util.RandomUtils;


public class PlumPuddingModel extends AbstractHydrogenAtom {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ELECTRON_POSITION = "electronPosition";
    public static final double PHOTON_EMISSION_PROBABILITY = 0.1;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* default size of the atom, tweaked to match image size of PlumPuddingNode */
    private static final Dimension DEFAULT_SIZE = new Dimension( 225, 180 );
    
    /* maximum number of photons that can be absorbed */
    private static final int MAX_PHOTONS_ABSORBED = 5;
    
    /* wavelength of emitted photons */
    private static final double PHOTON_EMISSION_WAVELENGTH = 150; // nm
    
    /* range of the deflection angle for alpha particles */
    private static final double MIN_DEFLECTION_ANGLE = Math.toRadians( 2 );
    private static final double MAX_DEFLECTION_ANGLE = Math.toRadians( 5 );
    
    /* number of discrete steps in the electron line */
    private static final int ELECTRON_LINE_SEGMENTS = 30;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfPhotonsAbsorbed;
    private Dimension _size;
    private Shape _shape;
    private Point2D _electronOffset; // relative to atom's center
    private Line2D _electronLine; // line on which the electron ocsillates
    private double _electronDistanceDelta; // change in distance when amplitude is 1.0
    private boolean _electronDirectionPositive; // determines the electron's direction
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PlumPuddingModel( Point2D position ) {
        this( position, DEFAULT_SIZE );
    }
    
    public PlumPuddingModel( Point2D position, Dimension size ) {
        super( position, 0 /* orientation */ );
        _numberOfPhotonsAbsorbed = 0;
        _size = new Dimension( size );
        _electronOffset = new Point2D.Double( 0, 0 );
        _electronLine = new Line2D.Double();
        updateShape();
        updateElectronLine();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public void setPosition( Point2D p ) {
        setPosition( p.getX(), p.getY() );
    }
    
    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
        updateShape();
    }
    
    public Point2D getElectronOffset() {
        return _electronOffset;
    }
    
    public Dimension getSize() {
        return _size;
    }
    
    //----------------------------------------------------------------------------
    // utilities
    //----------------------------------------------------------------------------
    
    private void updateElectronLine() {
        //XXX fix this code, the line isn't adjusted for the atom's position
        _electronLine.setLine( -_size.getWidth()/2, 0, _size.getWidth()/2, 0 );//XXX randomize
        double electronLineLength = _electronLine.getP1().distance( _electronLine.getP2() );
        _electronDistanceDelta = electronLineLength / ELECTRON_LINE_SEGMENTS;
        _electronDirectionPositive = RandomUtils.nextBoolean();
    }
    
    private void updateShape() {
        double w = _size.getWidth();
        double h = _size.getHeight();
        _shape = new Ellipse2D.Double( getX() - ( w / 2 ), getY() - ( h / 2 ), w, h );
    }
    
    private double getElectronAmplitude() {
        return ( _numberOfPhotonsAbsorbed / (double)MAX_PHOTONS_ABSORBED );
    }
    
    private int getElectronDirectionSign() {
        return ( _electronDirectionPositive == true ) ? +1 : -1;
    }
    
    private Point2D getRandomPointInsideAtom() {
        double x = getX() + ( RandomUtils.nextSign() * _size.getWidth() * Math.random() / 2 );
        double y = getY() + ( RandomUtils.nextSign() * _size.getHeight() * Math.random() / 2 );
        Point2D p = new Point2D.Double( x, y );
        return p;
    }
    
    //----------------------------------------------------------------------------
    // Photon absorption and emission
    //----------------------------------------------------------------------------
    
    private void absorbPhoton( Photon photon ) {
        _numberOfPhotonsAbsorbed += 1;
        PhotonAbsorbedEvent event = new PhotonAbsorbedEvent( this, photon );
        firePhotonAbsorbedEvent( event );
    }
    
    private void emitPhoton() {
        if ( _numberOfPhotonsAbsorbed > 0 ) {
            
            _numberOfPhotonsAbsorbed -= 1;
            
            // Pick a random point inside the atom
            Point2D position = getRandomPointInsideAtom();
            
            // Pick a random orientation
            double orientation = RandomUtils.nextOrientation();
            
            // Create and emit the photon
            Photon photon = new Photon( position, orientation, PHOTON_EMISSION_WAVELENGTH, true );
            PhotonEmittedEvent event = new PhotonEmittedEvent( this, photon );
            firePhotonEmittedEvent( event );
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Detects and handles collision with a photon.
     * If a collision occurs, the photon may be absorbed or it may pass through.
     * 
     * @param photon
     */
    public void detectCollision( Photon photon ) {
        if ( _numberOfPhotonsAbsorbed < MAX_PHOTONS_ABSORBED && !photon.wasEmitted() ) {
            Point2D position = photon.getPosition();
            if ( _shape.contains( position ) ) {
                absorbPhoton( photon );
            }
        }
    }
    
    /**
     * Detects and handles collision with an alpha particle.
     * If a collision occurs, the alpha particle is deflected as it passes through the pudding.
     * 
     * @param alphaParticle
     */
    public void detectCollision( AlphaParticle alphaParticle ) {
        Point2D position = alphaParticle.getPosition();
        if ( _shape.contains( position ) ) {
            final int sign = ( position.getX() > getX() ) ? 1 : -1;
            final double deflection = sign * RandomUtils.nextDouble( MIN_DEFLECTION_ANGLE, MAX_DEFLECTION_ANGLE );
            final double orientation = alphaParticle.getOrientation() + deflection;
            alphaParticle.setOrientation( orientation );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Oscillates the electron inside the atom.
     * Emits photon at random time.
     */
    public void stepInTime( double dt ) {
        if ( _numberOfPhotonsAbsorbed > 0 ) {
            
            // Move the electron
            {
                final double a = getElectronAmplitude();
                
                // Electron's new offset
                final double distanceDelta = dt * ( ( a * _electronDistanceDelta ) );
                final double sign = getElectronDirectionSign();
                double x = _electronOffset.getX() + ( sign * distanceDelta ); //XXX assumes a horizontal line
                double y = 0;//XXX assumes a horizontal line

                //XXX fix this code, it assumes that atom position is (0,0)
                // Is the new offset past the ends of the oscillation line?
                if ( x < a * _electronLine.getX1() || y < a * _electronLine.getY1() ) {
                    x = a * _electronLine.getX1();
                    y = a * _electronLine.getY1();
                    _electronDirectionPositive = !_electronDirectionPositive;
                }
                else if ( x > a * _electronLine.getX2() || y > a * _electronLine.getY2() ) {
                    x = a * _electronLine.getX2();
                    y = a * _electronLine.getY2();
                    _electronDirectionPositive = !_electronDirectionPositive;
                }
                
                _electronOffset.setLocation( x, y );
                notifyObservers( PROPERTY_ELECTRON_POSITION );
            }
           
            // Randomly emit a photon
            if ( Math.random() < PHOTON_EMISSION_PROBABILITY ) {
                emitPhoton();
            }
        }
    }
}

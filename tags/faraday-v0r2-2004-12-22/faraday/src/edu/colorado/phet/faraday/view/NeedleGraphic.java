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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;


/**
 * NeedleGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class NeedleGraphic extends PhetGraphic {

    private Point _location;
    private double _direction;
    private Dimension _size;
    private Shape _northPole, _southPole;
    private double _strength;  // 0.0 - 1.0
    
    /**
     * @param component
     */
    public NeedleGraphic( Component component ) {
        
        super( component );
        
        _location = new Point( 0, 0 );
        _direction = 0.0;
        _size = new Dimension( 40, 20 );
        _strength = 1.0;
        
        updateShape();
    }
    
    private void updateShape() {
        
        GeneralPath northPath = new GeneralPath();
        northPath.moveTo( 0, -(_size.height/2) );
        northPath.lineTo( (_size.width/2), 0 );
        northPath.lineTo( 0, (_size.height/2) );
        northPath.closePath();
        _northPole = northPath;
        
        GeneralPath southPath = new GeneralPath();
        southPath.moveTo( 0, -(_size.height/2) );
        southPath.lineTo( 0, (_size.height/2) );
        southPath.lineTo( -(_size.width/2), 0 );
        southPath.closePath();
        _southPole = southPath;
    }

    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }
    
    public void setLocation( int x, int y ) {
        if ( _location == null ) {
            _location = new Point(x,y);
        }
        else {
            _location.setLocation( x, y );
        }
        repaint();
    }
    
    public Point getLocation() {
        return new Point( _location );
    }
    
    public int getX() { 
        return _location.x;
    }
    
    public int getY() {
        return _location.y;
    }
    
    public void setDirection( double direction ) {
        _direction = direction;
        repaint();
    }
    
    public double getDirection() {
        return _direction;
    }

    public void setSize( Dimension size ) {
        _size = new Dimension( size );
        updateShape();
        repaint();
    }
    
    public Dimension getSize() {
        return new Dimension( _size );
    }
    
    public void setStrength( double strength ) {
        if ( strength < 0 || strength > 1 ) {
            throw new IllegalArgumentException( "strength must be 0.0-1.0 : " + strength );
        }
        _strength = strength;
    }
    
    public double getStrength() {
        return _strength;
    }
    
    /*
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#determineBounds()
     */
    protected Rectangle determineBounds() {     
        AffineTransform transform = new AffineTransform();
        transform.translate( _location.x, _location.y );
        transform.rotate( _direction );
        Rectangle r = new Rectangle( _northPole.getBounds() );
        r.add( _southPole.getBounds() );
        return transform.createTransformedShape( r ).getBounds();
    }

    /*
     * @see edu.colorado.phet.common.view.phetgraphics.PhetGraphic#paint(java.awt.Graphics2D)
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            super.saveGraphicsState( g2 );
            {
                Color northColor = new Color( 255, 0, 0, (int) ( 255 * _strength ) );
                Color southColor = new Color( 0, 0, 255, (int) ( 255 * _strength ) );
                
                RenderingHints hints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHints( hints );
                g2.translate( _location.x, _location.y );
                g2.rotate( Math.toRadians( _direction ) );
                g2.setPaint( northColor );
                g2.fill( _northPole );
                g2.setPaint( southColor );
                g2.fill( _southPole );
            }
            super.restoreGraphicsState();
        }
    }
}

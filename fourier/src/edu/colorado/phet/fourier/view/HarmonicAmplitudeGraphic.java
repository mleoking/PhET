/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * HarmonicAmplitudeSlider
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicAmplitudeGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double BAR_LAYER = 1;
    private static final double KNOB_LAYER = 2;
    
    private static final Color KNOB_COLOR = Color.BLACK;
    private static final int KNOB_HEIGHT = 5;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Harmonic _harmonic;
    private Dimension _maxSize;
    private Rectangle _barRectangle;
    private Color _barColor;
    private PhetShapeGraphic _bar;
    private PhetShapeGraphic _knob;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public HarmonicAmplitudeGraphic( Component component, Harmonic harmonic ) {
         super( component );
         
         assert( harmonic != null );
         _harmonic = harmonic;
         _harmonic.addObserver( this );
         
         _maxSize = new Dimension( 40, 100 );
         
         _barRectangle = new Rectangle();
         _barColor = Color.RED;
         _bar = new PhetShapeGraphic( component );
         _bar.setLocation( 0, 0 );
         _bar.setShape( _barRectangle );
         _bar.setPaint( _barColor );
         _bar.setBorderColor( Color.BLACK );
         _bar.setStroke( new BasicStroke( 1f ) );

         _knob = new PhetShapeGraphic( component );
         _knob.setLocation( 0, 0 );
         _knob.setShape( new Ellipse2D.Double( -5, -5, 10, 10 ) );
         _knob.setPaint( Color.BLACK );

         // Interactivity
         _knob.setCursorHand();
         _knob.addMouseInputListener( new MouseHandler() );
         
         addGraphic( _bar, BAR_LAYER );
         addGraphic( _knob, KNOB_LAYER );
         
         update();
    }
    
    public void finalize() {
        _harmonic.removeObserver( this );
        _harmonic = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setMaxSize( Dimension maxSize ) {
        if ( ! maxSize.equals( _maxSize ) ) {
            _maxSize.setSize( maxSize );
            update();
        }
    }
    
    public Dimension getMaxSize() {
        return new Dimension( _maxSize );
    }
    
    public void setBarColor( Color barColor ) {
        if ( ! barColor.equals( _barColor ) ) {
            _barColor = barColor;
            update();
        }
    }
    
    public Color getBarColor() {
        return _barColor;
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        
        int knobX = _knob.getX();
        int knobY = (int) -( _maxSize.height * _harmonic.getAmplitude() );
        _knob.setLocation( knobX, knobY );
        
        double amplitude = _harmonic.getAmplitude();
        int barWidth = _maxSize.width;
        int barHeight = (int) Math.abs( _maxSize.height * amplitude );
        int barX = -barWidth / 2;
        int barY = ( amplitude > 0 ) ? -barHeight : 0;
        _barRectangle.setBounds( barX, barY, barWidth, barHeight );
        _bar.setShape( _barRectangle );
        _bar.setPaint( _barColor );
        
        repaint();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private class MouseHandler extends MouseInputAdapter {
        
        private int _previousY;
        
        public MouseHandler() {
            super();
        }
        
        public void mousePressed( MouseEvent event ) {
            _previousY = event.getY();
        }
        
        public void mouseDragged( MouseEvent event ) {
            int mouseY = event.getY();
            if ( Math.abs( getY() - mouseY ) <= _maxSize.height ) {
                int dy = mouseY - _previousY;
                int y = _knob.getY() + dy;
                double amplitude = (double) y / _maxSize.height;
                amplitude = MathUtil.clamp( -1, amplitude, +1 );
                _harmonic.setAmplitude( -amplitude );
            }
            _previousY = event.getY();
        }
    }
}

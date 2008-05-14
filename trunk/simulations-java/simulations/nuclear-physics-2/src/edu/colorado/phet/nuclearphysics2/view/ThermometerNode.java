/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.nuclearphysics2.module.nuclearreactor.NuclearReactorModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * This class represents a node that looks like a thermometer and can
 * interpret temperature data to make the 'liquid' within the thermometer go
 * up and down.
 *
 * @author John Blanco
 */
public class ThermometerNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final double       SHAFT_WIDTH_PROPORTION = 0.5; 
    private static final Color        THERMOMETER_LIQUID_COLOR = Color.RED; 
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Reference to the nuclear reactor that this node is monitoring.
    private NuclearReactorModel _nuclearReactorModel;
    
    // The overall dimensions of this thermometer.
    PDimension _dimension;
    
    // The shapes and the node that represent the various parts of the thermometer.
    private Rectangle2D _thermometerLiquidShape;
    private PPath _thermometerLiquidNode;
    private PPath _shaftNode;
    
    // Max temp for this thermometer.
    private double _maxTemperature;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public ThermometerNode(NuclearReactorModel nuclearReactorModel, PDimension size, double maxTemperature){
        
        _nuclearReactorModel = nuclearReactorModel;
        _maxTemperature = maxTemperature;
        _dimension = size;
        
        // Register as a listener to the nuclear reactor so that we get
        // notified of temperature changes.
        _nuclearReactorModel.addListener( new NuclearReactorModel.Adapter(){
            public void energyChanged(){
                setTemperature(_nuclearReactorModel.getTemperature());
            }
        });
        
        // Create the shaft of the thermometer.
        RoundRectangle2D shaftShape = new RoundRectangle2D.Double(SHAFT_WIDTH_PROPORTION / 2 * size.width, 0,
                SHAFT_WIDTH_PROPORTION * size.width, size.height, size.width / 2, size.width / 2);
        _shaftNode = new PPath(shaftShape);
        _shaftNode.setPaint( Color.WHITE );
        addChild( _shaftNode );

        // Create the rectangle that will represent the liquid that moves
        // up and down the shaft of the thermometer.
        _thermometerLiquidShape = new Rectangle2D.Double(SHAFT_WIDTH_PROPORTION / 2 * size.width, 
                size.height - size.width, SHAFT_WIDTH_PROPORTION * size.width, size.height - (size.width / 2));
        _thermometerLiquidNode = new PPath(_thermometerLiquidShape);
        _thermometerLiquidNode.setStrokePaint( new Color(0, 0, 0, 0) );
        _thermometerLiquidNode.setPaint( THERMOMETER_LIQUID_COLOR );
        addChild( _thermometerLiquidNode );

        // Create the bulb of the thermometer.
        Ellipse2D bulbShape = new Ellipse2D.Double(0, size.height - size.width,
                size.width, size.width);
        PPath bulbNode = new PPath(bulbShape);
        bulbNode.setPaint( THERMOMETER_LIQUID_COLOR );
        addChild( bulbNode );
        
        // Set the initial temperature.
        setTemperature( _nuclearReactorModel.getTemperature() );
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the temperature that this thermometer should display.  The input
     * value is arbitrary, and the thermometer displays the value as the
     * proportion of the max temperature.
     */
    private void setTemperature(double temperature){
        
        if (temperature > _maxTemperature){
            // Limiter.
            temperature = _maxTemperature;
            _shaftNode.setPaint(THERMOMETER_LIQUID_COLOR);
        }
        else{
            _shaftNode.setPaint( Color.WHITE );
        }
        
        double liquidLength = 
            temperature/_maxTemperature * (_dimension.height - _dimension.width - (_dimension.width / 4) );
        
        _thermometerLiquidShape.setRect( SHAFT_WIDTH_PROPORTION / 2 * _dimension.width, 
                _dimension.height - _dimension.width - liquidLength,
                SHAFT_WIDTH_PROPORTION * _dimension.width, liquidLength + _dimension.width / 2);
        _thermometerLiquidNode.setPathTo( _thermometerLiquidShape );
    }
}

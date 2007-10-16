/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


/**
 * MVCModel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MVCModel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final double MODEL_TO_VIEW_SCALE = 0.5;
    
    public static final Point2D A_POSITION = new Point2D.Double( 400, 400 );
    public static final double A_ORIENTATION = 0; // radians
    
    public static final Point2D B_POSITION = new Point2D.Double( 400, 600 );
    public static final double B_ORIENTATION = 0; // radians
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final MVCClock _clock;
    private final ArrayList _modelElements; // array of ModelElement
    
    private final ModelViewTransform _modelViewTransform;
    private final AModelElement _aModelElement;
    private final BModelElement _bModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MVCModel( MVCClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                stepModelElements( event.getSimulationTimeChange() );
            }
        });
        
        _modelElements = new ArrayList();
        
        _aModelElement = new AModelElement( A_POSITION, A_ORIENTATION );
        addModelElement( _aModelElement );
        
        _bModelElement = new BModelElement( B_POSITION, B_ORIENTATION );
        addModelElement( _bModelElement  );
        
         _modelViewTransform = new ModelViewTransform( MODEL_TO_VIEW_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public MVCClock getClock() {
        return _clock;
    }
    
    protected void addModelElement( ModelElement element ) {
        _modelElements.add( element );
    }
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    public AModelElement getAModelElement() {
        return _aModelElement;
    }
    
    public BModelElement getBModelElement() {
        return _bModelElement;
    }
    
    private void stepModelElements( double dt ) {
        Iterator i = _modelElements.iterator();
        while ( i.hasNext() ) {
            ModelElement modelElement = (ModelElement) i.next();
            modelElement.stepInTime( dt );
        }
    }
}

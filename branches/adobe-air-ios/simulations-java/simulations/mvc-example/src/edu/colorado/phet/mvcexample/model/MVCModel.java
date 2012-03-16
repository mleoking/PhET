// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.model;

import java.awt.Color;
import java.awt.Dimension;
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
    
    public static final Color A_COLOR = Color.RED;
    public static final Point2D A_POSITION = new Point2D.Double( 400, 400 );
    public static final double A_ORIENTATION = 0; // radians
    public static final Dimension A_SIZE = new Dimension( 200, 100 );
    
    public static final Color B_COLOR = Color.BLUE;
    public static final Point2D B_POSITION = new Point2D.Double( 400, 600 );
    public static final double B_ORIENTATION = 0; // radians
    public static final Dimension B_SIZE = new Dimension( 200, 100 );
    
    public static final Color C_COLOR = Color.GREEN;
    public static final Point2D C_POSITION = new Point2D.Double( 400, 800 );
    public static final double C_ORIENTATION = 0; // radians
    public static final Dimension C_SIZE = new Dimension( 200, 100 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final MVCClock _clock;
    private final ArrayList _modelElements; // array of ModelElement
    
    private final AModelElement _aModelElement;
    private final BModelElement _bModelElement;
    private final CModelElement _cModelElement;

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
        
        _aModelElement = new AModelElement( A_POSITION, A_ORIENTATION, A_SIZE, A_COLOR );
        addModelElement( _aModelElement );
        
        _bModelElement = new BModelElement( B_POSITION, B_ORIENTATION, B_SIZE, B_COLOR );
        addModelElement( _bModelElement  );
        
        _cModelElement = new CModelElement( C_POSITION, C_ORIENTATION, C_SIZE, C_COLOR );
        addModelElement( _cModelElement );
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
    
    public AModelElement getAModelElement() {
        return _aModelElement;
    }
    
    public BModelElement getBModelElement() {
        return _bModelElement;
    }
    
    public CModelElement getCModelElement() {
        return _cModelElement;
    }
    
    private void stepModelElements( double dt ) {
        Iterator i = _modelElements.iterator();
        while ( i.hasNext() ) {
            ModelElement modelElement = (ModelElement) i.next();
            modelElement.stepInTime( dt );
        }
    }
}

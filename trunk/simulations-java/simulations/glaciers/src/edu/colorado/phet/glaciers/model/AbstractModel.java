/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;

/**
 * AbstractModel is the base class for all models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final Valley _valley;
    private final Glacier _glacier;
    private final Climate _climate;
    private final ModelViewTransform _modelViewTransform;
    private final ArrayList _tools; // array of AbstractTool
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractModel( GlaciersClock clock, Valley valley, Glacier glacier, Climate climate, ModelViewTransform modelViewTransform ) {
        super();
        _clock = clock;
        _valley = valley;
        _glacier = glacier;
        _clock.addClockListener( glacier );
        _climate = climate;
        _clock.addClockListener( climate );
        _modelViewTransform = modelViewTransform;
        _tools = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
        return _clock;
    }
    
    public Valley getValley() {
        return _valley;
    }
    
    public Glacier getGlacier() {
        return _glacier;
    }
    
    public Climate getClimate() {
        return _climate;
    }
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    public void addTool( AbstractTool tool ) {
        _tools.add( tool );
        _clock.addClockListener( tool );
    }
    
    public void removeTool( AbstractTool tool ) {
        _clock.removeClockListener( tool );
        _tools.remove( tool );
    }
}

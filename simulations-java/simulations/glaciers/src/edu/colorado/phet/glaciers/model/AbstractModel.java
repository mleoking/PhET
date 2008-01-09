/* Copyright 2007, University of Colorado */

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
    private final ArrayList _tools; // array of AbstractTool
    private Glacier _glacier;
    private Climate _climate;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractModel( GlaciersClock clock, Glacier glacier, Climate climate ) {
        super();
        _clock = clock;
        setGlacier( glacier );
        setClimate( climate );
        _tools = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
        return _clock;
    }
    
    public void setGlacier( Glacier glacier ) {
        if ( _glacier != null ) {
            _clock.removeClockListener( _glacier );
            _glacier = null;
        }
        if ( glacier != _glacier ) {
            _glacier = glacier;
            _clock.addClockListener( _glacier );
        }
    }
    
    public Glacier getGlacier() {
        return _glacier;
    }
    
    public void setClimate( Climate climate ) {
        if ( _climate != null ) {
            _clock.removeClockListener( _climate );
            _climate = null;
        }
        if ( climate != _climate ) {
            _climate = climate;
            _clock.addClockListener( _climate );
        }
    }
    
    public Climate getClimate() {
        return _climate;
    }
    
    public void addTool( AbstractTool tool ) {
        _tools.add( tool );
        _clock.addClockListener( tool );
    }
    
    public void removeTool( AbstractTool tool ) {
        _tools.remove( tool );
        _clock.removeClockListener( tool );
    }
}

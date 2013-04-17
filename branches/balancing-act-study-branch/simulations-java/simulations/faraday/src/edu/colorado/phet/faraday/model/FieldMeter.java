// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;


/**
 * FieldMeter is the model of a B-field meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FieldMeter extends FaradayObservable implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Magnet that the field meter is observing.
    private AbstractMagnet _magnetModel;
    // B-field vector at the field meter's location.
    private MutableVector2D _fieldVector;
    // The field meter's location.
    private Point2D _location;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FieldMeter( AbstractMagnet magnetModel ) {
        super();

        assert ( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );

        _fieldVector = new MutableVector2D();
        _location = new Point2D.Double();

        update();
    }

    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the strength at the field meter's location.
     *
     * @param vector strength value is copied here
     */
    public void getStrength( MutableVector2D vector /* output */ ) {
        assert ( vector != null );
        vector.setComponents( _fieldVector.getX(), _fieldVector.getY() );
    }

    //----------------------------------------------------------------------------
    // FaradayObservable overrides
    //----------------------------------------------------------------------------

    /**
     * Respond to changes in superclass attributes (eg, location).
     */
    public void notifySelf() {
        update();
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /*
    * Updates the field meter's location and takes a B-field reading at that location.
    */
    public void update() {
        if ( isEnabled() ) {
            getLocation( _location /* output */ );
            _magnetModel.getBField( _location, _fieldVector /* output */ );
            notifyObservers();
        }
    }
}

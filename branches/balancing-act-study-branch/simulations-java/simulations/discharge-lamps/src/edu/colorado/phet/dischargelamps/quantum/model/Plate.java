// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.quantum.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.quantum.model.ElectromotiveForce;

/**
 * Plate
 * <p/>
 * A composite Electrode that comprises an ElectronSource and an ElectronSink
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Plate extends Electrode {
    private ElectronSource source;
    private ElectronSink sink;

    public Plate( BaseModel model, ElectromotiveForce emf, Point2D p1, Point2D p2 ) {
        super( p1, p2 );
        source = new ElectronSource( emf, p1, p2, this );
        model.addModelElement( source );
        sink = new ElectronSink( model, p1, p2 );
        model.addModelElement( sink );
    }

    public void setCurrent( double current ) {
        source.setCurrent( current );
    }

    public ElectronSource getSource() {
        return source;
    }

    public void setEmittingLength( double l ) {
        source.setLength( l );
    }

    public Electron produceElectron() {
        return source.produceElectron();
    }

    public void addElectronProductionListener( ElectronSource.ElectronProductionListener listener ) {
        source.addListener( listener );
    }

    public void addElectronAbsorptionListener( ElectronSink.ElectronAbsorptionListener listener ) {
        sink.addListener( listener );
    }
}

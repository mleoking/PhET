/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import java.awt.geom.Point2D;

/**
 * Plate
 * <p>
 * A composite Electrode that comprises an ElectronSource and an ElectronSink
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Plate extends Electrode {
    private ElectronSource source;
    private ElectronSink sink;

    public Plate( DischargeLampModel model, Point2D p1, Point2D p2 ) {
        super( p1, p2 );
        source = new ElectronSource( model, p1, p2, this );
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
}

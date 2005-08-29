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

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.dischargelamps.view.ElectronGraphicManager;

import java.awt.geom.Point2D;

/**
 * Plate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Plate extends Electrode {
    private ElectronSource source;
    private ElectronSink sink;

    public Plate( DischargeLampModel model, Point2D p1, Point2D p2 ) {
        super( p1, p2 );
        source = new ElectronSource( model, p1, p2 );
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

    public ElectronSink getSink() {
        return sink;
    }

    public void addStateChangeListener( StateChangeListener listener ) {
//        super.addStateChangeListener( listener );
        source.addStateChangeListener( listener );
        sink.addStateChangeListener( listener );
    }

    public void addElectronProductionListener( ElectronSource.ElectronProductionListener listener ) {
        source.addListener( listener );
    }

    public void setLength( double l ) {
        source.setLength( l );
    }

    public void setPotential( double potential ) {
        source.setPotential( potential );
        sink.setPotential( potential );
        super.setPotential( potential );
    }

    public Electron produceElectron() {
        return source.produceElectron();
    }
}

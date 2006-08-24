/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.waveinterference.WaveInterferenceModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 8:24:31 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class MeasurementToolSet extends PhetPNode {
    private WaveMeasuringTape measuringTape;
    private PNode stopwatchGraphic;
    private PSwingCanvas pSwingCanvas;
    private ArrayList listeners = new ArrayList();
    private StopwatchPanelDectorator stopwatchDecorator;
    private PSwing pswing;

    public MeasurementToolSet( PSwingCanvas pSwingCanvas, IClock clock, LatticeScreenCoordinates latticeScreenCoordinates, WaveInterferenceModel waveInterferenceModel ) {
        this( pSwingCanvas, clock, latticeScreenCoordinates, waveInterferenceModel.getDistanceUnits(), waveInterferenceModel.getPhysicalWidth(), waveInterferenceModel.getPhysicalHeight(), waveInterferenceModel.getTimeUnits(), waveInterferenceModel.getTimeScale() );
    }

    public MeasurementToolSet( PSwingCanvas pSwingCanvas, IClock clock, LatticeScreenCoordinates latticeScreenCoordinates, String units, double physicalWidth, double physicalHeight, String timeUnits, double timeScale ) {

        this.pSwingCanvas = pSwingCanvas;
        measuringTape = new WaveMeasuringTape( latticeScreenCoordinates, physicalWidth, physicalHeight );
        measuringTape.setVisible( false );
        addChild( measuringTape );

        stopwatchDecorator = new StopwatchPanelDectorator( clock, timeScale, timeUnits );
        pswing = new PSwing( pSwingCanvas, stopwatchDecorator );
        stopwatchGraphic = new PhetPNode( pswing );
        stopwatchGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatchGraphic.addInputEventListener( new PDragEventHandler() );
        stopwatchGraphic.setVisible( false );

        setDistanceUnits( units );
        addChild( stopwatchGraphic );
        initStopwatchLocation();
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
        notifyVisibilityChanged();
    }

    public boolean isStopwatchVisible() {
        return stopwatchGraphic.getVisible();
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchGraphic.setVisible( selected );
        notifyVisibilityChanged();
    }

    public void setDistanceUnits( String distanceUnits ) {
        measuringTape.setUnits( distanceUnits );
    }

    public WaveMeasuringTape getWaveMeasuringTape() {
        return measuringTape;
    }

    public WaveMeasuringTape getMeasuringTape() {
        return measuringTape;
    }

    public void reset() {
        setMeasuringTapeVisible( false );
        setStopwatchVisible( false );
        measuringTape.reset();
        stopwatchDecorator.reset();
        initStopwatchLocation();
    }

    private void initStopwatchLocation() {
        pswing.setOffset( 0, 0 );
        stopwatchGraphic.setOffset( 50, 50 );
    }

    public static interface Listener {
        void toolVisibilitiesChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyVisibilityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.toolVisibilitiesChanged();
        }
    }
}

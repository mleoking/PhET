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

    public MeasurementToolSet( PSwingCanvas pSwingCanvas, IClock clock, LatticeScreenCoordinates latticeScreenCoordinates, WaveInterferenceModel waveInterferenceModel ) {
        this( pSwingCanvas, clock, latticeScreenCoordinates, waveInterferenceModel.getDistanceUnits(), waveInterferenceModel.getPhysicalWidth(), waveInterferenceModel.getPhysicalHeight(), waveInterferenceModel.getTimeUnits(), waveInterferenceModel.getTimeScale() );
    }

    public MeasurementToolSet( PSwingCanvas pSwingCanvas, IClock clock, LatticeScreenCoordinates latticeScreenCoordinates, String units, double physicalWidth, double physicalHeight, String timeUnits, double timeScale ) {

        this.pSwingCanvas = pSwingCanvas;
        measuringTape = new WaveMeasuringTape( latticeScreenCoordinates, physicalWidth, physicalHeight );
        measuringTape.setVisible( false );
//        measuringTape.setOffset( 100, 100 );
        addChild( measuringTape );

        stopwatchGraphic = new PhetPNode( new PSwing( pSwingCanvas, new StopwatchPanelDectorator( clock, timeScale, timeUnits ) ) );
        stopwatchGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatchGraphic.addInputEventListener( new PDragEventHandler() );
        stopwatchGraphic.setVisible( false );

        setDistanceUnits( units );


        addChild( stopwatchGraphic );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
    }

    public boolean isStopwatchVisible() {
        return stopwatchGraphic.getVisible();
    }

    public void setStopwatchVisible( boolean selected ) {
        stopwatchGraphic.setVisible( selected );
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
}

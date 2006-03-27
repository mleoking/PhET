/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.tests.RotationWaveGraphic;
import edu.colorado.phet.waveinterference.view.IndexColorMap;
import edu.colorado.phet.waveinterference.view.RotationGlyph;
import edu.colorado.phet.waveinterference.view.WaveModelGraphic;
import edu.colorado.phet.waveinterference.view.WaveSideViewFull;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:31:39 PM
 * Copyright (c) Mar 26, 2006 by Sam Reid
 */

public class WaterSimulationPanel extends WaveInterferenceCanvas {
    private WaterModule waterModule;
    private WaveModelGraphic waveModelGraphic;
    private WaveSideViewFull waveSideView;
    private RotationGlyph rotationGlyph;
    private RotationWaveGraphic rotationWaveGraphic;

    public WaterSimulationPanel( WaterModule waterModule ) {
        this.waterModule = waterModule;
        waveModelGraphic = new WaveModelGraphic( getWaveModel(), 10, 10, new IndexColorMap( getLattice() ) );
        waveSideView = new WaveSideViewFull( getLattice(), waveModelGraphic.getLatticeScreenCoordinates() );
        rotationGlyph = new RotationGlyph();
        rotationWaveGraphic = new RotationWaveGraphic( waveModelGraphic, waveSideView, rotationGlyph );
        rotationWaveGraphic.setOffset( 50, 20 );
        waterModule.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                rotationWaveGraphic.update();
            }
        } );
        addScreenChild( rotationWaveGraphic );
    }

    private Lattice2D getLattice() {
        return getWaveModel().getLattice();
    }

    private WaveModel getWaveModel() {
        return waterModule.getWaveModel();
    }

    public RotationWaveGraphic getRotationWaveGraphic() {
        return rotationWaveGraphic;
    }
}

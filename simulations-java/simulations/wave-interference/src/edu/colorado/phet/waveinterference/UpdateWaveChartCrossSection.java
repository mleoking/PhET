// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.view.CrossSectionGraphic;
import edu.colorado.phet.waveinterference.view.WaveChartGraphic;

/**
 * Created by: Sam
 * Feb 5, 2008 at 12:51:12 PM
 */
public class UpdateWaveChartCrossSection implements CrossSectionGraphic.Listener {
    private WaveChartGraphic waveChartGraphic;

    public UpdateWaveChartCrossSection( WaveChartGraphic waveChartGraphic ) {
        this.waveChartGraphic = waveChartGraphic;
    }

    public void changed( int crossSectionY ) {
        waveChartGraphic.setCrossSectionYValue(crossSectionY);
    }
}

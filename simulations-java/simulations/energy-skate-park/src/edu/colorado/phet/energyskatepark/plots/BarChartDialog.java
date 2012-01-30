// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;

/**
 * Author: Sam Reid
 * Jun 29, 2007, 11:12:57 PM
 */
public class BarChartDialog extends PaintImmediateDialog {
    private final BarGraphCanvas barGraphCanvas;
    private boolean visibleOnActivation = false;

    public BarChartDialog( PhetFrame phetFrame, String title, boolean modal, AbstractEnergySkateParkModule module ) {
        super( phetFrame, title, modal );
        barGraphCanvas = new BarGraphCanvas( module );
        setContentPane( barGraphCanvas );

        //Hide the bar chart when the user switches tabs
        module.addListener( new Module.Listener() {
            public void activated() {
                setVisible( visibleOnActivation );
            }

            public void deactivated() {
                visibleOnActivation = isVisible();
                setVisible( false );
            }
        } );
    }

    @Override public void setVisible( boolean b ) {
        super.setVisible( b );
    }

    public void reset() {
        visibleOnActivation = false;
        setVisible( false );
        barGraphCanvas.reset();
    }
}

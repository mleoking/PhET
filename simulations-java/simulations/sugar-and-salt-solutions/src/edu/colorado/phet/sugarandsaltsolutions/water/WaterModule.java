// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.view.WaterCanvas;

/**
 * Module for "water" tab of sugar and salt solutions module
 *
 * @author Sam Reid
 */
public class WaterModule extends SugarAndSaltSolutionsModule {

    public WaterModule( GlobalState state ) {
        this( new WaterModel(), state );
    }

    public WaterModule( final WaterModel model, GlobalState state ) {
        super( SugarAndSaltSolutionsResources.Strings.WATER, model.clock, model.moduleActive );
        setSimulationPanel( new WaterCanvas( model, state ) {{

            //Listen for when the user changes tabs so the JMolDialog can be hidden when switching away and shown when switching to this tab (if it was already shown)
            addListener( new Listener() {
                public void activated() {
                    moduleActivated();
                }

                public void deactivated() {
                    moduleDeactivated();
                }
            } );
        }} );
    }
}
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsModule;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.MicroCanvas;

/**
 * Micro tab that shows the NaCl ions and Sucrose molecules.
 * <p/>
 * In order to efficiently re-use pre existing code from the Soluble Salts (AKA Salts and Solubility) project, we make the following inaccurate encodings:
 * 1. Sugar is a subclass of Salt
 * 2. Sugar has two constituents, a "positive" sugar molecule and a "negative" sugar molecule
 *
 * @author Sam Reid
 */
public class MicroModule extends SugarAndSaltSolutionsModule {

    private final MicroModel model;
    public static final double SIZE_SCALE = 0.35;

    public MicroModule( GlobalState globalState ) {
        this( globalState, new MicroModel() );
    }

    public MicroModule( GlobalState globalState, MicroModel model ) {
        super( SugarAndSaltSolutionsResources.Strings.MICRO, model.clock, model.moduleActive,

               //The factor by which to scale particle sizes, so they look a bit smaller in the graphics
               SIZE_SCALE );

        this.model = model;

        setSimulationPanel( new MicroCanvas( model, globalState ) {{
            addListener( this );
        }} );
    }

    @Override public void reset() {
        super.reset();
        model.reset();
    }
}
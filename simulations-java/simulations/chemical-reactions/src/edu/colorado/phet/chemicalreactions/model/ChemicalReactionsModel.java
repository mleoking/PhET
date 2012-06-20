// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

import static edu.colorado.phet.chemicalreactions.model.MoleculeShape.*;

public class ChemicalReactionsModel {
    public final KitCollection kitCollection;
    private final LayoutBounds layoutBounds;

    public ChemicalReactionsModel( IClock clock, final LayoutBounds layoutBounds ) {
        this.layoutBounds = layoutBounds;

        kitCollection = new KitCollection() {{
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( O2, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( H2, new Dimension( 600, 200 ), 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( H2O, new Dimension( 600, 200 ), 5 ) );
                             }}
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( N2, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( H2, new Dimension( 600, 200 ), 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( NH3, new Dimension( 600, 200 ), 5 ) );
                             }}
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( Cl2, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( H2, new Dimension( 600, 200 ), 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( HCl, new Dimension( 600, 200 ), 5 ) );
                             }}
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( ClNO2, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( NO, new Dimension( 600, 200 ), 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( NO2, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( ClNO, new Dimension( 600, 200 ), 5 ) );
                             }}
            ) );
            addKit( new Kit( layoutBounds,
                             // reactants
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( CH4, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( O2, new Dimension( 600, 200 ), 5 ) );
                             }},

                             // products
                             new ArrayList<MoleculeBucket>() {{
                                 add( new MoleculeBucket( CO2, new Dimension( 600, 200 ), 5 ) );
                                 add( new MoleculeBucket( H2O, new Dimension( 600, 200 ), 5 ) );
                             }}
            ) );
        }};

        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( final ClockEvent clockEvent ) {
                super.clockTicked( clockEvent );

                kitCollection.getCurrentKit().tick( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    public KitCollection getKitCollection() {
        return kitCollection;
    }
}

/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron;

import static edu.colorado.phet.neuron.NeuronResources.getString;

/**
 * NeuronStrings is the collection of localized strings used by this
 * simulation. * We load all strings as statics so that we will be warned at
 * startup time of any missing strings.
 *
 * @author John Blanco
 */
public class NeuronStrings {
    
    /* not intended for instantiation */
    private NeuronStrings() {}
    
    public static final String TITLE_MEMBRANE_DIFFUSION_MODULE = getString( "ModuleTitle.MembraneDiffusionModule" );
    public static final String TITLE_AXON_CROSS_SECTION_MODULE = getString( "ModuleTitle.AxonCrossSection" );

    public static final String POTASSIUM_CHEMICAL_SYMBOL = getString( "PotassiumChemicalSymbol" );
    public static final String SODIUM_CHEMICAL_SYMBOL = getString( "SodiumChemicalSymbol" );
    
    public static final String SHOW_ALL_IONS = getString( "ShowAllIons" );
    public static final String SHOW_POTENTIAL_CHART = getString( "ShowPotentialChart" );
    public static final String SHOW_CHARGES = getString( "ShowCharges" );
    public static final String SHOW_CONCENTRATIONS = getString( "ShowConcentrations" );

    public static final String CONTROL = getString( "Control.title" );

    public static final String LEGEND_TITLE = getString( "Legend.title" );
    public static final String LEGEND_SODIUM_ION = getString( "Legend.sodiumIon" );
    public static final String LEGEND_POTASSIUM_ION = getString( "Legend.potassiumIon" );
    public static final String LEGEND_SODIUM_GATED_CHANNEL = getString( "Legend.sodiumGatedChannel" );
    public static final String LEGEND_POTASSIUM_GATED_CHANNEL = getString( "Legend.potassiumGatedChannel" );
    public static final String LEGEND_SODIUM_LEAK_CHANNEL = getString( "Legend.sodiumLeakChannel" );
    public static final String LEGEND_POTASSIUM_LEAK_CHANNEL = getString( "Legend.potassiumLeakChannel" );

    public static final String MEMBRANE_POTENTIAL_CHART_TITLE = getString( "Chart.title" );
    public static final String MEMBRANE_POTENTIAL_Y_AXIS_LABEL = getString( "Chart.yAxisLabel" );
    public static final String MEMBRANE_POTENTIAL_X_AXIS_LABEL = getString( "Chart.xAxisLabel" );
    public static final String MEMBRANE_POTENTIAL_CLEAR_CHART = getString( "Chart.clear" );

    public static final String STIMULATE_BUTTON_CAPTION = getString( "StimulateButton.caption" );
}

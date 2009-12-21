/* Copyright 2008, University of Colorado */

package edu.colorado.phet.genenetwork;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class GeneNetworkStrings {
    
    /* not intended for instantiation */
    private GeneNetworkStrings() {}
    
    public static final String TITLE_LACTOSE_REGULATION = GeneNetworkResources.getString( "ModuleTitle.LactoseRegulation" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );

    public static final String UNITS_TIME = GeneNetworkResources.getString( "units.time" );
    
    public static final String MESSAGE_NOT_A_CONFIG = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
    
    // Captions for items in the DNA segment tool box.
    public static final String POLYMERASE_BINDING_REGION_TOOL_BOX_CAPTION = GeneNetworkResources.getString( "Toolbox.PolymeraseBindingRegion" );
    public static final String LACI_BINDING_REGION_TOOL_BOX_CAPTION = GeneNetworkResources.getString( "Toolbox.LacIBindingRegion" );
    
    // Captions for items in the legend.
    public static final String POLYMERASE_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.Polymerase" );
    public static final String LAC_Z_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.LacZ" );
    public static final String MESSENGER_RNA_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.MessengerRna" );
    public static final String TRANSFORMATION_ARROW_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.TransformationArrow" );

    public static final String CLOCK_SPEED_CONTROL_CAPTION = GeneNetworkResources.getString("ClockSpeedControlCaption");

    public static final String LEGEND_VISIBILITY_CONTROL_CAPTION = GeneNetworkResources.getString("LegendVisibilityControlCaption");
}

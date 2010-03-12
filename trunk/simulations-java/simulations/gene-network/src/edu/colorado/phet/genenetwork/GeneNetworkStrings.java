/* Copyright 2008, University of Colorado */

package edu.colorado.phet.genenetwork;


/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class GeneNetworkStrings {
    
    /* not intended for instantiation */
    private GeneNetworkStrings() {}
    
    public static final String TITLE_LACTOSE_REGULATION = GeneNetworkResources.getString( "ModuleTitle.LactoseRegulation" );

    public static final String UNITS_TIME = GeneNetworkResources.getString( "units.time" );
    
    // Labels for the model elements that have them.
    public static final String LAC_Z_GENE_LABEL = GeneNetworkResources.getString( "ModelElementLabel.LacZGene" );
    public static final String LAC_I_GENE_LABEL = GeneNetworkResources.getString( "ModelElementLabel.LacIGene" );
    public static final String LAC_Y_GENE_LABEL = GeneNetworkResources.getString( "ModelElementLabel.LacYGene" );
    
    // Captions for items in the DNA segment tool box.
    public static final String LAC_PROMOTER_TOOL_BOX_CAPTION = GeneNetworkResources.getString( "Toolbox.LacPromoter" );
    public static final String LAC_I_PROMOTER_TOOL_BOX_CAPTION = GeneNetworkResources.getString( "Toolbox.LacIPromoter" );
    public static final String LACI_BINDING_REGION_TOOL_BOX_CAPTION = GeneNetworkResources.getString( "Toolbox.LacIBindingRegion" );
    
    // Captions for items in the legend.
    public static final String MACRO_MOLECULE_LEGEND_TITLE = GeneNetworkResources.getString( "Legend.Title" );
    public static final String POLYMERASE_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.Polymerase" );
    public static final String LAC_Z_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.LacZ" );
    public static final String LAC_I_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.LacI" );
    public static final String LACTOSE_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.Lactose" );
    public static final String MESSENGER_RNA_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.MessengerRna" );
    public static final String TRANSFORMATION_ARROW_LEGEND_CAPTION = GeneNetworkResources.getString( "Legend.TransformationArrow" );

    // Various other captions.
    public static final String LEGEND_VISIBILITY_CONTROL_CAPTION = GeneNetworkResources.getString("LegendVisibilityControlCaption");
    public static final String LACTOSE_METER_VISIBILITY_CONTROL_CAPTION = GeneNetworkResources.getString("LactoseMeterVisibilityControlCaption");
    public static final String LACTOSE_METER_CAPTION = GeneNetworkResources.getString("LactoseMeterCaption");
    public static final String RESET = GeneNetworkResources.getString("Reset");
}

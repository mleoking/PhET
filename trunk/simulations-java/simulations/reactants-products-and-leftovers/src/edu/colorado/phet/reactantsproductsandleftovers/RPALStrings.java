/* Copyright 2008, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers;


/**
 * Collection of localized strings used by this project. 
 * Statically loaded so we can easily see if any are missing.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALStrings {

    /* not intended for instantiation */
    private RPALStrings() {}

    // labels
    public static final String LABEL_SANDWICH_FORMULA = RPALResources.getString( "label.sandwichFormula" );
    public static final String LABEL_REACTION_FORMULA = RPALResources.getString( "label.reactionFormula" );
    public static final String LABEL_BEFORE_SANDWICH = RPALResources.getString( "label.beforeSandwich" );
    public static final String LABEL_AFTER_SANDWICH = RPALResources.getString( "label.afterSwndwich" );
    public static final String LABEL_BEFORE_REACTION = RPALResources.getString( "label.beforeReaction" );
    public static final String LABEL_AFTER_REACTION = RPALResources.getString( "label.afterReaction" );
    public static final String LABEL_NO_REACTION = RPALResources.getString( "label.noReaction" );
    public static final String LABEL_REACTANTS = RPALResources.getString( "label.reactants" );
    public static final String LABEL_PRODUCTS = RPALResources.getString( "label.products" );
    public static final String LABEL_LEFTOVERS = RPALResources.getString( "label.leftovers" );

    // radio buttons
    public static final String RADIO_BUTTON_WATER = RPALResources.getString( "radioButton.water" );
    public static final String RADIO_BUTTON_AMMONIA = RPALResources.getString( "radioButton.ammonia" );
    public static final String RADIO_BUTTON_METHANE = RPALResources.getString( "radioButton.methane" );
    
    // messages 
    public static final String MESSAGE_VALUE_OUT_OF_RANGE = RPALResources.getString( "message.valueOutOfRange" );
    
    // titles
    public static final String TITLE_SANDWICH_SHOP = RPALResources.getString( "title.sandwichShop" );
    public static final String TITLE_REAL_REACTION = RPALResources.getString( "title.realReaction" );
    
    // these strings are not visible in the sim, but needed by the general model
    public static final String BREAD = "Bread";
    public static final String MEAT = "Meat";
    public static final String CHEESE = "Cheese";
    public static final String SANDWICH = "Sandwich";
}

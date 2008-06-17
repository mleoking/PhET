/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale;

import java.text.MessageFormat;

import javax.swing.plaf.basic.BasicHTML;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;


/**
 * PHScaleStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PHScaleStrings {

    /* not intended for instantiation */
    private PHScaleStrings() {}
    
    public static final String BUTTON_RESET_ALL = PHScaleResources.getCommonString( PhetCommonResources.STRING_RESET_ALL );

    public static final String CHOICE_CHOOSE_LIQUID = PHScaleResources.getString( "choice.chooseLiquid" );
    public static final String CHOICE_MILK = PHScaleResources.getString( "choice.milk" );
    public static final String CHOICE_BATTERY_ACID = PHScaleResources.getString( "choice.batteryAcid" );
    public static final String CHOICE_GASTRIC_ACID = PHScaleResources.getString( "choice.gastricAcid" );
    public static final String CHOICE_LEMON_JUICE = PHScaleResources.getString( "choice.lemonJuice" );
    public static final String CHOICE_COLA = PHScaleResources.getString( "choice.cola" );
    public static final String CHOICE_VINEGAR = PHScaleResources.getString( "choice.vinegar" );
    public static final String CHOICE_ORANGE_JUICE = PHScaleResources.getString( "choice.orangeJuice" );
    public static final String CHOICE_BEER = PHScaleResources.getString( "choice.beer" );
    public static final String CHOICE_COFFEE = PHScaleResources.getString( "choice.coffee" );
    public static final String CHOICE_TEA = PHScaleResources.getString( "choice.tea" );
    public static final String CHOICE_ACID_RAIN = PHScaleResources.getString( "choice.acidRain" );
    public static final String CHOICE_HUMAN_SALIVA = PHScaleResources.getString( "choice.humanSaliva" );
    public static final String CHOICE_BLOOD = PHScaleResources.getString( "choice.blood" );
    public static final String CHOICE_SEA_WATER = PHScaleResources.getString( "choice.seaWater" );
    public static final String CHOICE_HAND_SOAP = PHScaleResources.getString( "choice.handSoap" );
    public static final String CHOICE_AMMONIA = PHScaleResources.getString( "choice.ammonia" );
    public static final String CHOICE_BLEACH = PHScaleResources.getString( "choice.bleach" );
    public static final String CHOICE_LYE = PHScaleResources.getString( "choice.lye" );
    public static final String CHOICE_WATER = PHScaleResources.getString( "choice.water" );

    public static final String LABEL_PH = PHScaleResources.getString( "label.pH" );
    public static final String LABEL_ACID = PHScaleResources.getString( "label.acid" );
    public static final String LABEL_BASE = PHScaleResources.getString( "label.base" );
    public static final String LABEL_H2O = PHScaleResources.getString( "label.H2O" );
    public static final String LABEL_H3O = PHScaleResources.getString( "label.H3O" );
    public static final String LABEL_OH = PHScaleResources.getString( "label.OH" );

    public static final String CHECK_BOX_MOLECULE_COUNT = PHScaleResources.getString( "checkBox.moleculeCount" );
    private static final String CHECK_BOX_H3O_OH_RATIO = PHScaleResources.getString( "checkBox.H3O_OH_ratio" );

    private static final String RADIO_BUTTON_CONCENTRATION = PHScaleResources.getString( "radioButton.concentration" );
    private static final String RADIO_BUTTON_NUMBER_OF_MOLES = PHScaleResources.getString( "radioButton.numberOfMoles" );
    public static final String RADIO_BUTTON_LOGARITHMIC_SCALE = PHScaleResources.getString( "radioButton.logarithmicScale" );
    public static final String RADIO_BUTTON_LINEAR_SCALE = PHScaleResources.getString( "radioButton.linearScale" );

    public static final String TITLE_WATER_COMPONENTS = PHScaleResources.getString( "title.waterComponents" );
    
    public static final String UNITS_LITERS = PHScaleResources.getString( "units.liters" );
    public static final String UNITS_MOLES = PHScaleResources.getString( "units.moles" );
    public static final String UNITS_MOLES_PER_LITER = PHScaleResources.getString( "units.molesPerLiter" );

    public static final String getBeakerViewRatioString() {
        String s = CHECK_BOX_H3O_OH_RATIO;
        if ( !BasicHTML.isHTMLString( CHECK_BOX_H3O_OH_RATIO ) ) {
            s = "<html>" + s + "</html>";
        }
        Object[] args = { LABEL_H3O, LABEL_OH };
        return MessageFormat.format( s, args );
    }
    
    public static final String getConcentrationString() {
        String s = RADIO_BUTTON_CONCENTRATION;
        Object[] args = { UNITS_MOLES_PER_LITER };
        return MessageFormat.format( s, args );
    }
    
    public static final String getNumberOfMolesString() {
        String s = RADIO_BUTTON_NUMBER_OF_MOLES;
        Object[] args = { UNITS_MOLES };
        return MessageFormat.format( s, args );
    }
}

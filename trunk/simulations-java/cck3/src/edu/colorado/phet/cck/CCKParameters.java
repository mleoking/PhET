package edu.colorado.phet.cck;

import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:22:23 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */
public class CCKParameters {
    private String[] args;
    private boolean virtualLab = false;
    private boolean grabBag = true;
    private boolean allowPlainResistors = true;
    private boolean hugeBatteries = true;
    private boolean allowShowReadouts = true;
    private boolean allowSchematicMode = true;
    private boolean useAdvancedControlPanel = true;
    private boolean useNonContactAmmeter = true;
    private boolean hideAllElectrons = false;
    private boolean grabBagMode = false;
    private boolean useVisualControlPanel = true;
    private boolean dynamics = false;

    public CCKParameters( ICCKModule module, String[] args ) {
        this.args = args;
        if( containsArg( "-dynamics" ) ) {
            dynamics = true;
        }
        if( containsArg( "-virtuallab" ) ) {
            virtualLab = true;
        }
        if( containsArg( "-grabbag" ) ) {
            grabBagMode = true;
        }
        if( containsArg( "-noElectrons" ) ) {
            module.setElectronsVisible( false );
            hideAllElectrons = true;
        }
        if( containsArg( "-exp1" ) ) {
            module.setElectronsVisible( true );
            hideAllElectrons = false;
            allowSchematicMode = false;
            useNonContactAmmeter = false;
            grabBag = true;
            hugeBatteries = false;
            allowPlainResistors = true;
            useAdvancedControlPanel = false;
            useVisualControlPanel = false;
        }
        else if( containsArg( "-exp2" ) ) {
            module.setElectronsVisible( false );
            hideAllElectrons = true;
            allowSchematicMode = false;
            useNonContactAmmeter = false;
            grabBag = true;
            hugeBatteries = false;
            allowPlainResistors = true;
            useAdvancedControlPanel = false;
            useVisualControlPanel = false;
        }
        if( virtualLab ) {
            allowShowReadouts = false;
            allowSchematicMode = false;
            useNonContactAmmeter = false;
        }
        if( grabBagMode ) {
            grabBag = true;
            hugeBatteries = true;
            allowPlainResistors = false;
            allowShowReadouts = true;
            allowSchematicMode = false;
            useAdvancedControlPanel = false;
            useNonContactAmmeter = true;
            hideAllElectrons = false;
        }
    }

    public boolean isUseVisualControlPanel() {
        return useVisualControlPanel;
    }

    private boolean containsArg( String s ) {
        return Arrays.asList( args ).contains( s );
    }

    public boolean hideAllElectrons() {
        return hideAllElectrons;
    }

    public boolean useNonContactAmmeter() {
        return useNonContactAmmeter;
    }

    public boolean showGrabBag() {
        return grabBag;
    }

    public boolean allowSchematicMode() {
        return allowSchematicMode;
    }

    public boolean allowShowReadouts() {
        return allowShowReadouts;
    }

    public boolean hugeRangeOnBatteries() {
        return hugeBatteries;
    }

    public boolean allowPlainResistors() {
        return allowPlainResistors;
    }

    public boolean getUseAdvancedControlPanel() {
        return useAdvancedControlPanel;
    }

    public boolean getAllowDynamics() {
        return dynamics;
    }
}

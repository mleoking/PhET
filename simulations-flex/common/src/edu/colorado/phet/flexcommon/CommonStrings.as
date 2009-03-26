package edu.colorado.phet.flexcommon {
import flash.events.Event;
import mx.core.Application;

public class CommonStrings {

    [Bindable(event="CommonStringEvent")]
    public static var labelSoftwareAgreement : String = "Default";

    [Bindable(event="CommonStringEvent")]
    public static var labelCredits : String = "Default";

    [Bindable(event="CommonStringEvent")]
    public static var labelOK : String = "Default";

    [Bindable(event="CommonStringEvent")]
    public static var labelAbout : String = "Default";

    [Bindable(event="CommonStringEvent")]
    public static var labelPreferences : String = "Default";

    public function CommonStrings() {

    }

    public static function initialize() : void {
        trace( "CommonStrings initialize()" );

        if( true ) {
            labelSoftwareAgreement = "Software Agreement";
            labelCredits = "Credits";
            labelOK = "OK";
            labelAbout = "About";
            labelPreferences = "Preferences";
        } else {
            labelSoftwareAgreement = "Acuerdo del software";
            labelCredits = "Créditos";
            labelOK = "OK";
            labelAbout = "Sobre el programa";
            labelPreferences = "Preferencias";
        }

        Application.application.dispatchEvent( new Event( "CommonStringEvent" ) );
    }

}

}
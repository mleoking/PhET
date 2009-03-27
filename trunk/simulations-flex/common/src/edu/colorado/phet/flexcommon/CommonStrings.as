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
    public static var labelCancel : String = "Default";

    [Bindable(event="CommonStringEvent")]
    public static var labelAbout : String = "Default";

    [Bindable(event="CommonStringEvent")]
    public static var labelPreferences : String = "Default";

    public function CommonStrings() {

    }

    public static function initialize() : void {
        trace( "CommonStrings initialize()" );

        var vars : Object = Application.application.parameters;

        labelSoftwareAgreement = "Software Agreement";
        labelCredits = "Credits";
        labelOK = "OK";                                                         
        labelCancel = "Cancel";
        labelAbout = "About";
        labelPreferences = "Preferences";

        if( vars.languageCode == "pg" ) {
            labelSoftwareAgreement = "Oftware-say Agreement-ay";
            labelCredits = "Edits-cray";
            labelOK = "OK-ay";
            labelCancel = "Ancel-cay";
            labelAbout = "About-ay";
            labelPreferences = "Eferences-pray";
        }

        Application.application.dispatchEvent( new Event( "CommonStringEvent" ) );
        
    }

}

}
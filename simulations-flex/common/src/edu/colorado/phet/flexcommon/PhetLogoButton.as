package edu.colorado.phet.flexcommon {
import flash.events.MouseEvent;

import flash.net.URLRequest;

import flash.net.navigateToURL;

import mx.controls.Button;

public class PhetLogoButton extends Button {
    [Embed(source='../../../../../../../simulations-flash/common-as3/data/common-as3/images/phet-logo-120x50.jpg')]
    private var logoClass:Class;

    public function PhetLogoButton() {
        setStyle("icon", logoClass);
        addEventListener(MouseEvent.CLICK, visitPhet)
    }

    private function visitPhet(event:MouseEvent):void {
        navigateToURL(new URLRequest("http://phet.colorado.edu"), "_blank");//Blank means to open a new page
    }
}
}
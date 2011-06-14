package edu.colorado.phet.flexcommon {
import flash.display.Bitmap;
import flash.events.MouseEvent;
import flash.net.URLRequest;
import flash.net.navigateToURL;

import mx.core.UIComponent;

public class PhetLogoButton extends UIComponent {
    [Embed(source='../../../../../../../simulations-flash/common-as3/data/common-as3/images/phet-logo-120x50.jpg')]
    private var logoClass: Class;

    public function PhetLogoButton( height: Number = 50 ) {
        addEventListener( MouseEvent.CLICK, function ( event: MouseEvent ): void {
            navigateToURL( new URLRequest( "http://phet.colorado.edu" ), "_blank" );//Blank means to open a new page
        } );
        useHandCursor = true;
        buttonMode = true;
        var bitmap: Bitmap = new logoClass();
        var scale: Number = height / bitmap.height;
        bitmap.scaleX = scale;
        bitmap.scaleY = scale;
        addChild( bitmap );
        this.width = bitmap.width;
        this.height = bitmap.height;
    }
}
}
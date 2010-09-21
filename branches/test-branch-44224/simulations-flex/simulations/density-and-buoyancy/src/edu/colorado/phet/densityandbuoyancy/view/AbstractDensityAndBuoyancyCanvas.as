package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.controls.Button;
import mx.controls.TextInput;

/**
 * Base class for the main UI of the density and buoyancy sims.
 */
public class AbstractDensityAndBuoyancyCanvas extends Canvas {
    protected var background:Canvas;

    protected var phetLogoButton:PhetLogoButton

    /**
     * Override in sub-classes
     */
    public function resetAll():void {
    }

    /**
     * Override in sub-classes
     */
    public function init():void {
    }

    public function AbstractDensityAndBuoyancyCanvas() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle( "fontSize", 12 );
    }

    protected function addBackground():void {
        background = new Canvas();
        background.percentWidth = 100;
        background.percentHeight = 100;
        background.setStyle( "backgroundColor", 0x5dc0ff );
        addChild( background );
    }

    protected function addResetAll():void {
        var resetAllControlPanel:DensityVBox = new DensityVBox();
        resetAllControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        resetAllControlPanel.setStyle( "bottom", DensityConstants.CONTROL_INSET );

        var resetAllButton:Button = new Button();
        resetAllButton.label = FlexSimStrings.get( 'application.resetAll', 'Reset All' );
        resetAllButton.addEventListener( MouseEvent.CLICK, function():void {
            // this is a separate callback because direct access to resetAll() does not select the subclass' resetAll()
            resetAll();
        });
        resetAllControlPanel.addChild( resetAllButton );
        addChild( resetAllControlPanel );
    }

    protected function addLogo():void {
        phetLogoButton = new PhetLogoButton();
        phetLogoButton.setStyle( "left", DensityConstants.CONTROL_INSET );
        phetLogoButton.setStyle( "bottom", DensityConstants.CONTROL_INSET );
        addChild( phetLogoButton );
    }

    protected function refocusCallback( event: MouseEvent ):void {
        //Text fields should lose focus when the user clicks outside of them, so they will accept their value if the user was editing them
        if ( focusManager.getFocus() is TextInput ) {
            focusManager.setFocus( focusManager.getNextFocusManagerComponent() )
        }
    }

    public function onApplicationComplete():void {
        init();

        background.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );
    }

}
}
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.flashcommon.CommonStrings;
import edu.colorado.phet.flexcommon.FlexCommon;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.ColorTransform;

import mx.containers.Canvas;
import mx.controls.Button;
import mx.controls.TextInput;
import mx.core.Application;
import mx.core.UIComponent;

/**
 * Base class for the main UI of the density and buoyancy sims.
 */
public class AbstractDensityAndBuoyancyCanvas extends Canvas {
    protected var background: Canvas;

    protected var phetLogoButton: PhetLogoButton

    /**
     * Override in sub-classes
     */
    public function resetAll(): void {
    }

    /**
     * Override in sub-classes
     */
    public function init(): void {
    }

    public function AbstractDensityAndBuoyancyCanvas() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle( "fontSize", 12 );
    }

    protected function addBackground(): void {
        background = new Canvas();
        background.percentWidth = 100;
        background.percentHeight = 100;
        background.setStyle( "backgroundColor", 0x5dc0ff );
        addChild( background );
    }

    protected function addResetAll(): void {
        var resetAllControlPanel: DensityVBox = new DensityVBox();
        resetAllControlPanel.setStyle( "right", DensityConstants.CONTROL_INSET );
        resetAllControlPanel.setStyle( "bottom", DensityConstants.CONTROL_INSET );

        var resetAllButton: Button = new Button();
        resetAllButton.label = FlexSimStrings.get( 'application.resetAll', 'Reset All' );
        resetAllButton.addEventListener( MouseEvent.CLICK, function(): void {
            // this is a separate callback because direct access to resetAll() does not select the subclass' resetAll()
            resetAll();
        } );
        resetAllControlPanel.addChild( resetAllButton );
        addChild( resetAllControlPanel );
    }

    protected function addLogo(): void {
        phetLogoButton = new PhetLogoButton();
        phetLogoButton.setStyle( "left", DensityConstants.CONTROL_INSET );
        phetLogoButton.setStyle( "bottom", DensityConstants.CONTROL_INSET );
        addChild( phetLogoButton );
    }

    protected function refocusCallback( event: MouseEvent ): void {
        //Text fields should lose focus when the user clicks outside of them, so they will accept their value if the user was editing them
        if ( focusManager.getFocus() is TextInput ) {
            focusManager.setFocus( focusManager.getNextFocusManagerComponent() )
        }
    }

    public function onApplicationComplete(): void {
        init();

        background.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

        addFlashCommon();
    }

    private function addFlashCommon(): void {
        var ui: UIComponent = new UIComponent(); // used for FlashCommon UI
        addChild( ui );

        // TODO: have FlexCommon initialize these types of things?
        var commonStrings: * = Application.application.parameters.commonStrings;
        if ( commonStrings != null && commonStrings != undefined ) {
            CommonStrings.initDocument( new XML( commonStrings ) );
        }
        var common: FlexCommon = FlexCommon.getInstance();
        common.initialize( ui );

        common.highContrastFunction = function ( contrast: Boolean ): void {
            if ( contrast ) {
                var stretch: Number = 2.0;
                var newCenter: Number = 128;
                var offset: Number = newCenter - 128 * stretch;
                root.stage.transform.colorTransform = new ColorTransform( stretch, stretch, stretch, 1, offset, offset, offset, 1 );
            }
            else {
                root.stage.transform.colorTransform = new ColorTransform( 1, 1, 1, 1, 0, 0, 0, 0 );
            }
        }

        function positionButtons(): void {
            if ( common.commonButtons == null || common.commonButtons == undefined ) {
                return;
            }
            var height: int = common.commonButtons.getPreferredHeight();
            common.commonButtons.setLocationXY( DensityConstants.CONTROL_INSET, stage.stageHeight - height - 60 - DensityConstants.CONTROL_INSET );
        }

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, positionButtons );
        positionButtons();
    }

}
}
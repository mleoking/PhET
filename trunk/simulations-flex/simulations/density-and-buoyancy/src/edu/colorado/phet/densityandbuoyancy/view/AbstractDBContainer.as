//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityAndBuoyancyConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.flashcommon.AbstractMethodError;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.display.GradientType;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.geom.Matrix;

import mx.containers.Canvas;
import mx.controls.Button;
import mx.controls.TextInput;

/**
 * Base class for the main UI of the density and buoyancy sims.
 */
public class AbstractDBContainer extends Canvas {
    protected var background: Canvas;

    protected var phetLogoButton: PhetLogoButton; //REVIEW this is apparently optional in multi-tab sims (eg BuoyancyContainer), so why is this in the base class?

    /**
     * Override in sub-classes
     */
    public function resetAll(): void {
    }

    public function AbstractDBContainer() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle( "fontSize", 12 );
    }

    protected function addBackground(): void {
        background = new Canvas();
        background.percentWidth = 100;
        background.percentHeight = 100;
        background.addEventListener( MouseEvent.MOUSE_DOWN, refocusCallback );

        //Add a gradient sky like in projectile motion
        const updateSize: Function = function(): void {
            var matrix: Matrix = new Matrix();
            matrix.createGradientBox( background.width, background.height / 2, Math.PI / 2, 0, 0 );
            background.graphics.clear();
            background.graphics.beginGradientFill( GradientType.LINEAR, [0x5dc0ff,0xFFFFFF], [1,1], [0x00,0xFF], matrix );
            background.graphics.drawRect( 0, 0, background.width, background.height );
        };
        background.addEventListener( Event.RESIZE, updateSize );
        updateSize();

        addChild( background );
    }

    protected function addResetAll(): void {
        var resetAllControlPanel: DensityVBox = new DensityVBox();
        resetAllControlPanel.setStyle( "right", DensityAndBuoyancyConstants.CONTROL_INSET );
        resetAllControlPanel.setStyle( "bottom", DensityAndBuoyancyConstants.CONTROL_INSET );

        var resetAllButton: Button = new Button();
        resetAllButton.label = FlexSimStrings.get( 'application.resetAll', 'Reset All' );
        resetAllButton.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            // this is a separate callback because direct access to resetAll() does not select the subclass' resetAll()
            resetAll();
        } );
        resetAllControlPanel.addChild( resetAllButton );
        addChild( resetAllControlPanel );
    }

    protected function addLogo(): void {
        throw new AbstractMethodError();
    }

    protected function refocusCallback( event: MouseEvent ): void {
        //Text fields should lose focus when the user clicks outside of them, so they will accept their value if the user was editing them
        if ( focusManager.getFocus() is TextInput ) {
            focusManager.setFocus( focusManager.getNextFocusManagerComponent() )
        }
    }
}
}
package edu.colorado.phet.densityandbuoyancy.view {
import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.components.DensityVBox;
import edu.colorado.phet.flexcommon.FlexCommon;
import edu.colorado.phet.flexcommon.FlexSimStrings;
import edu.colorado.phet.flexcommon.PhetLogoButton;

import flash.events.Event;
import flash.events.MouseEvent;

import mx.containers.Canvas;
import mx.controls.Button;
import mx.controls.Label;
import mx.controls.RadioButton;
import mx.controls.TextInput;

public class AbstractDensityAndBuoyancyCanvas extends Canvas {
    var densityModule:DensityModule;
    var background:Canvas;

    public function resetAll():void {
    }

    public function init():void {
    }

    public function AbstractDensityAndBuoyancyCanvas() {
        super();
        percentWidth = 100;
        percentHeight = 100;
        setStyle("fontSize", 12);

        background = new Canvas();
        background.percentWidth = 100;
        background.percentHeight = 100;
        background.setStyle("backgroundColor", 0x5dc0ff);
        addChild(background);

        densityModule = new DensityModule();
        addChild(densityModule);

        var modeControlPanel:DensityVBox = new DensityVBox();
        modeControlPanel.setStyle("right", DensityConstants.CONTROL_INSET);
        modeControlPanel.y = DensityConstants.CONTROL_INSET;

        var label:Label = new Label();
        label.text = FlexSimStrings.get('mode.title', 'Objects');
        label.setStyle("fontWeight", "bold");
        modeControlPanel.addChild(label);

        var customButton:RadioButton = new RadioButton();
        customButton.groupName = "modes";
        customButton.label = FlexSimStrings.get('mode.customObject', 'Custom');
        customButton.addEventListener(MouseEvent.CLICK, function():void {
            densityModule.switchToCustomObject()
        });
        customButton.selected = true;
        modeControlPanel.addChild(customButton);

        var sameMassButton:RadioButton = new RadioButton();
        sameMassButton.groupName = "modes";
        sameMassButton.label = FlexSimStrings.get('mode.objectsOfSameMass', 'Same Mass');
        sameMassButton.addEventListener(MouseEvent.CLICK, function():void {
            densityModule.switchToSameMass()
        });
        modeControlPanel.addChild(sameMassButton);

        var sameVolumeButton:RadioButton = new RadioButton();
        sameVolumeButton.groupName = "modes";
        sameVolumeButton.label = FlexSimStrings.get('mode.objectsOfSameVolume', 'Same Volume');
        sameVolumeButton.addEventListener(MouseEvent.CLICK, function():void {
            densityModule.switchToSameVolume()
        });
        modeControlPanel.addChild(sameVolumeButton);

        var sameDensityButton:RadioButton = new RadioButton();
        sameDensityButton.groupName = "modes";
        sameDensityButton.label = FlexSimStrings.get('mode.objectsOfSameDensity', 'Same Density');
        sameDensityButton.addEventListener(MouseEvent.CLICK, function():void {
            densityModule.switchToSameDensity()
        });
        modeControlPanel.addChild(sameDensityButton);

        var mysteryObjectsButton:RadioButton = new RadioButton();
        mysteryObjectsButton.groupName = "modes";
        mysteryObjectsButton.label = FlexSimStrings.get('mode.mysteryObjects', 'Mystery');
        mysteryObjectsButton.addEventListener(MouseEvent.CLICK, function():void {
            densityModule.switchToMysteryObjects()
        });
        modeControlPanel.addChild(mysteryObjectsButton);

        addChild(modeControlPanel);

        var resetAllControlPanel:DensityVBox = new DensityVBox();
        resetAllControlPanel.setStyle("right", DensityConstants.CONTROL_INSET);
        resetAllControlPanel.setStyle("bottom", DensityConstants.CONTROL_INSET);

        var resetAllButton:Button = new Button();
        resetAllButton.label = FlexSimStrings.get('application.resetAll', 'Reset All');
        resetAllButton.addEventListener(MouseEvent.CLICK, function():void {
            customButton.selected = true;
            densityModule.resetAll();
        });
        resetAllControlPanel.addChild(resetAllButton);
        addChild(resetAllControlPanel);

        const phetLogoButton:PhetLogoButton = new PhetLogoButton();
        phetLogoButton.setStyle("left", DensityConstants.CONTROL_INSET);
        phetLogoButton.setStyle("bottom", DensityConstants.CONTROL_INSET);
        addChild(phetLogoButton);
    }

    private var oldIndex:Number = 0;
    private var common:FlexCommon = new FlexCommon();

    private function getAllTabs():Array {
        return new Array(densityModule);
    }

    public function onApplicationComplete():void {
        for each (var module:AbstractDensityModule in getAllTabs()) {
            module.init();
            module.pause();
        }
        getAllTabs()[0].start();

        densityModule.doInit(this);
        densityModule.switchToCustomObject();

        //Text fields should lose focus when the user clicks outside of them, so they will accept their value if the user was editing them
        const refocuser:Function = function():void {
            if (focusManager.getFocus() is TextInput) {
                focusManager.setFocus(focusManager.getNextFocusManagerComponent())
            }
        };
        densityModule.addEventListener(MouseEvent.MOUSE_DOWN, refocuser);
        background.addEventListener(MouseEvent.MOUSE_DOWN, refocuser);
    }

    private function onSwitch(e:Event):void {
        var newIndex:Number = e.currentTarget.selectedIndex;
        if (oldIndex == newIndex) {
            return;
        }

        const tabList:Array = getAllTabs();
        const oldTab:AbstractDensityModule = tabList[oldIndex];
        const newTab:AbstractDensityModule = tabList[newIndex];

        oldTab.pause();
        newTab.start();

        oldIndex = newIndex;
    }

}
}
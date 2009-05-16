
import flash.geom.Rectangle;

import edu.colorado.phet.flashcommon.*;

class PendulumLabAccessibility {
    public var pendulum1 : TabEntry;
    public var pendulum2 : TabEntry;

    public var length1Text : TabEntry;
    public var length1Slider : TabEntry;
    public var mass1Text : TabEntry;
    public var mass1Slider : TabEntry;

    public var pendulum2Check : TabEntry;

    public var length2Text : TabEntry;
    public var length2Slider : TabEntry;
    public var mass2Text : TabEntry;
    public var mass2Slider : TabEntry;

    public var frictionSlider : TabEntry;

    public var realTimeRadio : TabEntry;
    public var quarterTimeRadio : TabEntry;
    public var sixteenthTimeRadio : TabEntry;

    public var moonRadio : TabEntry;
    public var earthRadio : TabEntry;
    public var jupiterRadio : TabEntry;
    public var planetXRadio : TabEntry;
    public var noGravityRadio : TabEntry;

    public var velocityCheck : TabEntry;
    public var accelerationCheck : TabEntry;

    public var energy1Radio : TabEntry;
    public var energy2Radio : TabEntry;
    public var energyNoneRadio : TabEntry;

    public var photoGate : TabEntry;
    public var otherTools : TabEntry;

    public var reset : TabEntry;

    public var playPause : TabEntry;

    public var tab : TabHandler;

    public function PendulumLabAccessibility() {
        _level0.debug("Initializing PendulumLabAccessibility\n");
        
        tab = _level0.tabHandler;

        pendulum1 = new PendulumTabEntry( _level0.pendulum1Model, _level0.pendulum1, _level0.pendulum1View );
        pendulum2 = new PendulumTabEntry( _level0.pendulum2Model, _level0.pendulum2, _level0.pendulum2View );

        length1Text = new TabEntry(_root.panel_mc.length1Slider.value_txt);
        length1Slider = new TabEntry(_root.panel_mc.length1Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        mass1Text = new TabEntry(_root.panel_mc.mass1Slider.value_txt);
        mass1Slider = new TabEntry(_root.panel_mc.mass1Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);

        pendulum2Check = new TabEntry(_root.panel_mc.pendulum2_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _root.panel_mc.pendulum2_ch);
        pendulum2Check.buttonlike = true;

        length2Text = new TabEntry(_root.panel_mc.length2Slider.value_txt);
        length2Slider = new TabEntry(_root.panel_mc.length2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        mass2Text = new TabEntry(_root.panel_mc.mass2Slider.value_txt);
        mass2Slider = new TabEntry(_root.panel_mc.mass2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);

        frictionSlider = new TabEntry( _level0.panel_mc.frictionSlider_mc, TabHandler.HIGHLIGHT_LOCAL, _level0.panel_mc.frictionSlider_mc.knob_mc );

        realTimeRadio = new TabEntry( _level0.panel_mc.timeGroup_mc.radio1, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.timeGroup_mc.label1_txt );
        initRadio( realTimeRadio );
        quarterTimeRadio = new TabEntry( _level0.panel_mc.timeGroup_mc.radio2, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.timeGroup_mc.label2_txt );
        initRadio( quarterTimeRadio );
        sixteenthTimeRadio = new TabEntry( _level0.panel_mc.timeGroup_mc.radio3, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.timeGroup_mc.label3_txt );
        initRadio( sixteenthTimeRadio );

        moonRadio = new TabEntry( _level0.panel_mc.gravityGroup_mc.radio1, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.gravityGroup_mc.label1_txt );
        initRadio( moonRadio );
        earthRadio = new TabEntry( _level0.panel_mc.gravityGroup_mc.radio2, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.gravityGroup_mc.label2_txt );
        initRadio( earthRadio );
        jupiterRadio = new TabEntry( _level0.panel_mc.gravityGroup_mc.radio3, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.gravityGroup_mc.label3_txt );
        initRadio( jupiterRadio );
        planetXRadio = new TabEntry( _level0.panel_mc.gravityGroup_mc.radio4, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.gravityGroup_mc.label4_txt );
        initRadio( planetXRadio );
        noGravityRadio = new TabEntry( _level0.panel_mc.gravityGroup_mc.radio5, TabHandler.HIGHLIGHT_GLOBAL, _level0.panel_mc.gravityGroup_mc.label5_txt );
        initRadio( noGravityRadio );

        velocityCheck = new TabEntry( _root.panel_mc.velocity_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _root.panel_mc.velocity_ch );
        velocityCheck.buttonlike = true;
        accelerationCheck = new TabEntry( _root.panel_mc.acceleration_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _root.panel_mc.acceleration_ch );
        accelerationCheck.buttonlike = true;

        var checkBounds : Rectangle = new Rectangle( -7, -7, 14, 14 );
        energy1Radio = new TabEntry( _level0.panel_mc.energyGroup_mc.radio1, TabHandler.HIGHLIGHT_LOCAL );
        energy1Radio.manualBounds = checkBounds;
        initRadio( energy1Radio );
        energy2Radio = new TabEntry( _level0.panel_mc.energyGroup_mc.radio2, TabHandler.HIGHLIGHT_LOCAL );
        energy2Radio.manualBounds = checkBounds;
        initRadio( energy2Radio );
        energyNoneRadio = new TabEntry( _level0.panel_mc.energyGroup_mc.radio3, TabHandler.HIGHLIGHT_LOCAL );
        energyNoneRadio.manualBounds = checkBounds;
        initRadio( energyNoneRadio );

        photoGate = new TabEntry( _level0.panel_mc.photoGate_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _level0.panel_mc.photoGate_ch );
        photoGate.buttonlike = true;
        otherTools = new TabEntry( _level0.panel_mc.showTools_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _level0.panel_mc.showTools_ch );
        otherTools.buttonlike = true;

        reset = new TabEntry( _level0.panel_mc.resetButton_mc );
        reset.buttonlike = true;

        playPause = new TabEntry( _level0.pausePlay_mc.button_mc, TabHandler.HIGHLIGHT_GLOBAL );
        playPause.buttonlike = true;
        playPause.highlightInset = -5;
        playPause.highlightWidth = 4;

        var idx : Number = 0;
        
        tab.insertEntry( pendulum1, idx++ );

        tab.insertEntry( playPause, idx++ );

        tab.insertEntry( length1Text, idx++ );
        tab.insertEntry( length1Slider, idx++ );
        tab.insertEntry( mass1Text, idx++ );
        tab.insertEntry( mass1Slider, idx++ );

        tab.insertEntry( pendulum2Check, idx++ );

        tab.insertEntry( frictionSlider, idx++ );

        tab.insertEntry( realTimeRadio, idx++ );
        tab.insertEntry( quarterTimeRadio, idx++ );
        tab.insertEntry( sixteenthTimeRadio, idx++ );

        tab.insertEntry( moonRadio, idx++ );
        tab.insertEntry( earthRadio, idx++ );
        tab.insertEntry( jupiterRadio, idx++ );
        tab.insertEntry( planetXRadio, idx++ );
        tab.insertEntry( noGravityRadio, idx++ );

        tab.insertEntry( velocityCheck, idx++ );
        tab.insertEntry( accelerationCheck, idx++ );

        tab.insertEntry( energy1Radio, idx++ );
        tab.insertEntry( energy2Radio, idx++ );
        tab.insertEntry( energyNoneRadio, idx++ );

        tab.insertEntry( photoGate, idx++ );
        tab.insertEntry( otherTools, idx++ );

        tab.insertEntry( reset, idx++ );
        
    }

    public function initRadio( entry : TabEntry ) {
        entry.buttonlike = true;
        entry.highlightInset = -1;
    }

    public function showPendulum2() {

        var pendulum1Index = tab.findEntryIndex( pendulum1 );
        tab.insertEntry( pendulum2, pendulum1Index + 1 );

        var pendulum2CheckIndex = tab.findEntryIndex( pendulum2Check );

        tab.insertEntry( length2Text, pendulum2CheckIndex + 1 );
        tab.insertEntry( length2Slider, pendulum2CheckIndex + 2 );
        tab.insertEntry( mass2Text, pendulum2CheckIndex + 3 );
        tab.insertEntry( mass2Slider, pendulum2CheckIndex + 4 );



    }

    public function hidePendulum2() {
        tab.removeEntry( length2Text );
        tab.removeEntry( length2Slider );
        tab.removeEntry( mass2Text );
        tab.removeEntry( mass2Slider );
        tab.removeEntry( pendulum2 );
    }

    public function initializeSlider( slider : MovieClip ) {
        _level0.debug( "Initializing Slider: " + slider + "\n" );

        var entry : TabEntry;

        if( slider.puck_mc == length1Slider.control ) {
            entry = length1Slider;
        } else if( slider.puck_mc == mass1Slider.control ) {
            entry = mass1Slider;
        } else if( slider.puck_mc == length2Slider.control ) {
            entry = length2Slider;
        } else if( slider.puck_mc == mass2Slider.control ) {
            entry = mass2Slider;
        } else {
            _level0.debug( "Slider not found!!!\n" );
        }

        entry.keys[ 37 ] = slider.puck_mc.pressLeft;
        entry.keys[ 39 ] = slider.puck_mc.pressRight;
    }

    public function initializeFrictionSlider() {
        frictionSlider.keys[ 37 ] = frictionSlider.control.pressLeft;
        frictionSlider.keys[ 39 ] = frictionSlider.control.pressRight;
    }
}

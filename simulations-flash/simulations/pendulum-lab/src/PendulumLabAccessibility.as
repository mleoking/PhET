
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

        playPause = new TabEntry( _level0.pausePlay_mc.button_mc, TabHandler.HIGHLIGHT_GLOBAL, _level0.pausePlay_mc.button_mc );
        playPause.buttonlike = true;


        tab.insertEntry( pendulum1, 0 );

        tab.insertEntry( length1Text, 1 );
        tab.insertEntry( length1Slider, 2 );
        tab.insertEntry( mass1Text, 3 );
        tab.insertEntry( mass1Slider, 4 );

        tab.insertEntry( pendulum2Check, 5 );

        tab.insertEntry( frictionSlider, 6 );

        tab.insertEntry( realTimeRadio, 7 );
        tab.insertEntry( quarterTimeRadio, 8 );
        tab.insertEntry( sixteenthTimeRadio, 9 );

        tab.insertEntry( moonRadio, 10 );
        tab.insertEntry( earthRadio, 11 );
        tab.insertEntry( jupiterRadio, 12 );
        tab.insertEntry( planetXRadio, 13 );
        tab.insertEntry( noGravityRadio, 14 );

        tab.insertEntry( velocityCheck, 15 );
        tab.insertEntry( accelerationCheck, 16 );

        tab.insertEntry( energy1Radio, 17 );
        tab.insertEntry( energy2Radio, 18 );
        tab.insertEntry( energyNoneRadio, 19 );

        tab.insertEntry( photoGate, 20 );
        tab.insertEntry( otherTools, 21 );

        tab.insertEntry( reset, 22 );

        tab.insertEntry( playPause, 23 );
    }

    public function initRadio( entry : TabEntry ) {
        entry.buttonlike = true;
        entry.highlightInset = -1;
    }

    public function showPendulum2() {

        tab.insertEntry( length2Text, 6 );
        tab.insertEntry( length2Slider, 7 );
        tab.insertEntry( mass2Text, 8 );
        tab.insertEntry( mass2Slider, 9 );

        tab.insertEntry( pendulum2, 1 );

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

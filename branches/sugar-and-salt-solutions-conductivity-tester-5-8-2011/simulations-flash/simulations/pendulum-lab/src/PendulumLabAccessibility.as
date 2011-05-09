
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

    public var ruler : TabEntry;

    public var photogateRadio1 : TabEntry;
    public var photogateRadio2 : TabEntry;
    public var photogateButton : TabEntry;

    public var stopWatchPlay : TabEntry;
    public var stopWatchStop : TabEntry;

    public var tapeMeasureBody : TabEntry;
    public var tapeMeasureEnd : TabEntry;



    public var tab : TabHandler;

    public function PendulumLabAccessibility() {
        _level0.debug("Initializing PendulumLabAccessibility\n");
        
        tab = _level0.tabHandler;

        pendulum1 = new PendulumTabEntry( _level0.pendulum1Model, _level0.pendulum1, _level0.pendulum1View );
        pendulum2 = new PendulumTabEntry( _level0.pendulum2Model, _level0.pendulum2, _level0.pendulum2View );

        length1Text = new TabEntry(_root.panel_mc.length1Slider.value_txt);
        _root.panel_mc.length1Slider.value_txt.onSetFocus = function( old : Object ) { _level0.tabHandler.setFocus( this ); }
        length1Slider = new TabEntry(_root.panel_mc.length1Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        mass1Text = new TabEntry(_root.panel_mc.mass1Slider.value_txt);
        _root.panel_mc.mass1Slider.value_txt.onSetFocus = function( old : Object ) { _level0.tabHandler.setFocus( this ); }
        mass1Slider = new TabEntry(_root.panel_mc.mass1Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);

        pendulum2Check = new TabEntry(_root.panel_mc.pendulum2_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _root.panel_mc.pendulum2_ch);
        pendulum2Check.buttonlike = true;

        length2Text = new TabEntry(_root.panel_mc.length2Slider.value_txt);
        _root.panel_mc.length2Slider.value_txt.onSetFocus = function( old : Object ) { _level0.tabHandler.setFocus( this ); }
        length2Slider = new TabEntry(_root.panel_mc.length2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        mass2Text = new TabEntry(_root.panel_mc.mass2Slider.value_txt);
        _root.panel_mc.mass2Slider.value_txt.onSetFocus = function( old : Object ) { _level0.tabHandler.setFocus( this ); }
        mass2Slider = new TabEntry(_root.panel_mc.mass2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);

        frictionSlider = new TabEntry( _level0.panel_mc.frictionSlider_mc, TabHandler.HIGHLIGHT_LOCAL, _level0.panel_mc.frictionSlider_mc.knob_mc );
        frictionSlider.highlightWidth = 3;
        frictionSlider.highlightInset = 0;

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
        reset.highlightWidth = 4;

        playPause = new TabEntry( _level0.pausePlay_mc.button_mc, TabHandler.HIGHLIGHT_GLOBAL );
        playPause.buttonlike = true;
        playPause.highlightInset = -5;
        playPause.highlightWidth = 4;

        ruler = new TabEntry( _level0.meterScale_mc, TabHandler.HIGHLIGHT_LOCAL );
        ruler.keys[ Key.LEFT ] = function() { _level0.meterScale_mc._x -= 5; updateAfterEvent(); }
        ruler.keys[ Key.RIGHT ] = function() { _level0.meterScale_mc._x += 5; updateAfterEvent(); }
        ruler.keys[ Key.UP ] = function() { _level0.meterScale_mc._y -= 5; updateAfterEvent(); }
        ruler.keys[ Key.DOWN ] = function() { _level0.meterScale_mc._y += 5; updateAfterEvent(); }
        ruler.manualBounds = new Rectangle( -8.9, -175.9, 20, 400 );

        photogateRadio1 = new TabEntry( _level0.photogate_mc.gateRadioGroup_mc.radio1, TabHandler.HIGHLIGHT_LOCAL );
        photogateRadio1.manualBounds = checkBounds;
        initRadio( photogateRadio1 );
        photogateRadio2 = new TabEntry( _level0.photogate_mc.gateRadioGroup_mc.radio2, TabHandler.HIGHLIGHT_LOCAL );
        photogateRadio2.manualBounds = checkBounds;
        initRadio( photogateRadio2 );
        photogateButton = new TabEntry( _level0.photogate_mc.periodButton_mc, TabHandler.HIGHLIGHT_LOCAL );
        photogateButton.highlightWidth = 4;
        photogateButton.buttonlike = true;

        stopWatchPlay = new TabEntry( _level0.stopWatch_mc.play_btn, TabHandler.HIGHLIGHT_LOCAL, _level0.stopWatch_mc );
        stopWatchPlay.manualBounds = new Rectangle( 13.9, 0.7, 19.4, 19.4 );
        stopWatchPlay.buttonlike = true;
        stopWatchStop = new TabEntry( _level0.stopWatch_mc.stop_btn, TabHandler.HIGHLIGHT_LOCAL, _level0.stopWatch_mc );
        stopWatchStop.manualBounds = new Rectangle( -36.0, -0.1, 21.0, 21.0 );
        stopWatchStop.buttonlike = true;
        
        tapeMeasureBody = new TabEntry( _level0.tapeMeasure_mc.tapeMeasure_mc.tapeBody_mc, TabHandler.HIGHLIGHT_LOCAL );
        tapeMeasureBody.keys[ Key.RIGHT ] = function() { _level0.tapeMeasure_mc._x += 5; updateAfterEvent(); }
        tapeMeasureBody.keys[ Key.LEFT ] = function() { _level0.tapeMeasure_mc._x -= 5; updateAfterEvent(); }
        tapeMeasureBody.keys[ Key.UP ] = function() { _level0.tapeMeasure_mc._y -= 5; updateAfterEvent(); }
        tapeMeasureBody.keys[ Key.DOWN ] = function() { _level0.tapeMeasure_mc._y += 5; updateAfterEvent(); }
        tapeMeasureEnd = new TabEntry( _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc, TabHandler.HIGHLIGHT_LOCAL );
        tapeMeasureEnd.keys[ Key.RIGHT ] = function() {
            _level0.tapeMeasure_mc.tapeMeasure_mc._rotation += 200 / _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc._x;
            updateAfterEvent();
        }
        tapeMeasureEnd.keys[ Key.LEFT ] = function() {
            _level0.tapeMeasure_mc.tapeMeasure_mc._rotation -= 200 / _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc._x;
            updateAfterEvent();
        }
        tapeMeasureEnd.keys[ Key.UP ] = function() {
            var curLength = _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc._x;
            curLength += 3;
            _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc._x = curLength;
            _level0.tapeMeasure_mc.tapeMeasure_mc.tapeLength_mc._width = curLength;
            _level0.tapeMeasure_mc.updateReadout();
        }
        tapeMeasureEnd.keys[ Key.DOWN ] = function() {
            var curLength = _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc._x;
            curLength -= 3;
            _level0.tapeMeasure_mc.tapeMeasure_mc.tapeEnd_mc._x = curLength;
            _level0.tapeMeasure_mc.tapeMeasure_mc.tapeLength_mc._width = curLength;
            _level0.tapeMeasure_mc.updateReadout();
        }







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

        tab.insertEntry( ruler, idx++ );
        
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

    public function showPhotogate() {
        var rulerIndex : Number = tab.findEntryIndex( ruler );

        if( rulerIndex == -1 ) {
            return;
        }
        tab.insertEntry( photogateRadio1, rulerIndex + 1 );
        tab.insertEntry( photogateRadio2, rulerIndex + 2 );
        tab.insertEntry( photogateButton, rulerIndex + 3 );
    }

    public function hidePhotogate() {
        tab.removeEntry( photogateRadio1 );
        tab.removeEntry( photogateRadio2 );
        tab.removeEntry( photogateButton );
    }

    public function showTools() {
        var rulerIndex : Number = tab.findEntryIndex( ruler );

        if( rulerIndex == -1 ) {
            return;
        }
        tab.insertEntry( stopWatchStop, rulerIndex + 1 );
        tab.insertEntry( stopWatchPlay, rulerIndex + 2 );        
        tab.insertEntry( tapeMeasureBody, rulerIndex + 3 );
        tab.insertEntry( tapeMeasureEnd, rulerIndex + 4 );
    }

    public function hideTools() {
        tab.removeEntry( stopWatchPlay );
        tab.removeEntry( stopWatchStop );
        tab.removeEntry( tapeMeasureBody );
        tab.removeEntry( tapeMeasureEnd );
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


import flash.geom.Rectangle;

import edu.colorado.phet.flashcommon.*;

class ProjectileMotionAccessibility {

    public var projectileList : TabEntry;

    public var magButton : TabEntry;
    public var demagButton : TabEntry;

    public var fireButton : TabEntry;
    public var eraseButton : TabEntry;

    public var angleText : TabEntry;
    public var speedText : TabEntry;
    public var massText : TabEntry;
    public var diameterText : TabEntry;

    public var airResistanceCheck : TabEntry;
    public var dragCoefficientText : TabEntry;
    public var altitudeText : TabEntry;

    public var soundCheck : TabEntry;

    public var tab : TabHandler;

    public function ProjectileMotionAccessibility() {
        _level0.debug("Initializing ProjectileMotionAccessibility\n");

        if( _level0.focusManager ) {
            _level0.debug( "Deactivating MX focus manager" );
            _level0.focusManager.deactivate();
        }
        
        tab = _level0.tabHandler;

        projectileList = new TabEntry( _level0.dropList, TabHandler.HIGHLIGHT_LOCAL, _level0.dropList.vScroller );
        // use its own accessibility keys!!! MUAHAHAHA
        //projectileList.keys[40] = new function() { _level0.dropList.moveSelBy(1); };
        //projectileList.keys[38] = new function() { _level0.dropList.moveSelBy(-1); };

        angleText = new TabEntry( _level0.gui_mc.theta_txt );
        speedText = new TabEntry( _level0.gui_mc.vInit_txt );
        massText = new TabEntry( _level0.gui_mc.mass_txt );
        diameterText = new TabEntry( _level0.gui_mc.diameter_txt );

        airResistanceCheck = new TabEntry( _level0.airResistance_ch, TabHandler.HIGHLIGHT_LOCAL, _level0.airResistance_ch );
        dragCoefficientText = new TabEntry( _level0.gui_mc.dragC_txt );
        altitudeText = new TabEntry( _level0.gui_mc.altitude_txt );


        fireButton = new TabEntry( _level0.fireButton_mc, TabHandler.HIGHLIGHT_LOCAL );
        fireButton.buttonlike = true;

        eraseButton = new TabEntry( _level0.eraseButton_mc, TabHandler.HIGHLIGHT_LOCAL );
        eraseButton.buttonlike = true;

        magButton = new TabEntry( _level0.mag1_mc, TabHandler.HIGHLIGHT_LOCAL );
        magButton.buttonlike = true;

        demagButton = new TabEntry( _level0.demag1_mc, TabHandler.HIGHLIGHT_LOCAL );
        demagButton.buttonlike = true;

        soundCheck = new TabEntry( _level0.sound_ch, TabHandler.HIGHLIGHT_LOCAL );

        var idx : Number = 0;

        tab.insertEntry( projectileList, idx++ );       

        tab.insertEntry( angleText, idx++ );
        tab.insertEntry( speedText, idx++ );
        tab.insertEntry( massText, idx++ );
        tab.insertEntry( diameterText, idx++ );

        tab.insertEntry( airResistanceCheck, idx++ );

        tab.insertEntry( demagButton, idx++ );
        tab.insertEntry( magButton, idx++ );

        tab.insertEntry( fireButton, idx++ );
        tab.insertEntry( eraseButton, idx++ );

        tab.insertEntry( soundCheck, idx++ );
    }

    public function showAir() {
        var idx = tab.findEntryIndex( airResistanceCheck );

        tab.insertEntry( dragCoefficientText, idx + 1 );
        tab.insertEntry( altitudeText, idx + 2 );
    }

    public function hideAir() {
        tab.removeEntry( dragCoefficientText );
        tab.removeEntry( altitudeText );
    }

    /*
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
    */
}


import edu.colorado.phet.flashcommon.*;

class PendulumLabAccessibility {
    public function PendulumLabAccessibility() {
        _level0.debug("Initializing PendulumLabAccessibility\n");

        _level0.L2T = 7;
        _level0.L2S = 8;
        _level0.M2T = 9;
        _level0.M2S = 10;

        _level0.tabHandler.insertControl(_level0.pendulum1.pendulum_mc.mass_mc, 0, TabHandler.HIGHLIGHT_LOCAL);
        _level0.tabHandler.insertControl(_level0.pendulum2.pendulum_mc.mass_mc, 1, TabHandler.HIGHLIGHT_LOCAL);

        _level0.tabHandler.insertControl(_root.panel_mc.length1Slider.value_txt, 2);
        _level0.tabHandler.insertControl(_root.panel_mc.length1Slider.puck_mc, 3, TabHandler.HIGHLIGHT_LOCAL);
        _level0.tabHandler.insertControl(_root.panel_mc.mass1Slider.value_txt, 4);
        _level0.tabHandler.insertControl(_root.panel_mc.mass1Slider.puck_mc, 5, TabHandler.HIGHLIGHT_LOCAL);

        var p2 : TabEntry = new TabEntry(_root.panel_mc.pendulum2_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _root.panel_mc.pendulum2_ch);
        _level0.tabHandler.insertEntry(p2, 6);
        _level0.tabHandler.registerButton(_root.panel_mc.pendulum2_ch.box_mc);

        _level0.length2TextEntry = new TabEntry(_root.panel_mc.length2Slider.value_txt);
        _level0.length2SliderEntry = new TabEntry(_root.panel_mc.length2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        _level0.mass2TextEntry = new TabEntry(_root.panel_mc.mass2Slider.value_txt);
        _level0.mass2SliderEntry = new TabEntry(_root.panel_mc.mass2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
    }

    public function showPendulum2() {
        _level0.tabHandler.insertEntry( _level0.length2TextEntry, _level0.L2T );
		_level0.tabHandler.insertEntry( _level0.length2SliderEntry, _level0.L2S );
		_level0.tabHandler.insertEntry( _level0.mass2TextEntry, _level0.M2T );
		_level0.tabHandler.insertEntry( _level0.mass2SliderEntry, _level0.M2S );
		_level0.tabHandler.registerKey(_root.panel_mc.length2Slider.puck_mc, 37, _root.panel_mc.length2Slider.puck_mc.pressLeft);
		_level0.tabHandler.registerKey(_root.panel_mc.length2Slider.puck_mc, 39, _root.panel_mc.length2Slider.puck_mc.pressRight);
		_level0.tabHandler.registerKey(_root.panel_mc.mass2Slider.puck_mc, 37, _root.panel_mc.mass2Slider.puck_mc.pressLeft);
		_level0.tabHandler.registerKey(_root.panel_mc.mass2Slider.puck_mc, 39, _root.panel_mc.mass2Slider.puck_mc.pressRight);
    }

    public function hidePendulum2() {
        _level0.tabHandler.removeEntry( _level0.length2TextEntry );
		_level0.tabHandler.removeEntry( _level0.length2SliderEntry );
		_level0.tabHandler.removeEntry( _level0.mass2TextEntry );
		_level0.tabHandler.removeEntry( _level0.mass2SliderEntry );
    }

    public function initializeSlider( slider : MovieClip ) {
        _level0.debug( "Initializing Slider: " + slider + "\n" );

        _level0.tabHandler.registerKey( slider.puck_mc, 37, slider.puck_mc.pressLeft );
        _level0.tabHandler.registerKey( slider.puck_mc, 39, slider.puck_mc.pressRight );

    }
}

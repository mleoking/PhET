class SingleBulbModule extends Module {
	private var bulbIsMonochromatic:Boolean;
	public function setBulbMonochromatic(bulbIsMonochromatic:Boolean):Void {
		this.bulbIsMonochromatic = bulbIsMonochromatic;

		if( !bulbIsMonochromatic ){
			_root.spectrumSlider._visible = false;
			var ctx = {rb:255, gb:255, bb:255};
			_root.bulb1.setColor(ctx);
			_root.bulb1.setWavelength(0);
		}

	}
	public function isBulbMonochromatic():Boolean {
		return this.bulbIsMonochromatic;
	}
	public function activate():Void {
		super.activate();
		setBulbMonochromatic( !_root.whiteLight.selected );
		for (var i = 0; i < elements.length; i++) {
			if (elements[i] == _level0.monochromaticLight) {
			}
		}
	}
	public function getFilteredCtx() {
		var ctx;
		if (_root.bulb1.getWavelength() == 0) {			
			ctx = _root.getCtx(_root.filter1.getTransmissionPeak());
		}
		else {
			ctx = _root.bulb1.getCtx();
		}		
		ctx.aa = _root.filter1.percentPassed( _root.bulb1.getWavelength() );
		return ctx;
	}
}

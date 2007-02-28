class SingleBulbModule extends Module {
	private var bulbIsMonochromatic:Boolean = false;
	public static var solidBeamView:Number = 1;
	public static var photonBeamView:Number = 2;
	private var beamView:Number = solidBeamView;
	public function activate():Void {
		super.activate();
		setBulbMonochromatic(!_root.bulbControlPanel.whiteLight.selected);
		setBeamView(beamView);
		_root.photonGenerator.start();
		for (var i = 0; i < elements.length; i++) {
			if (elements[i] == _level0.monochromaticLight) {
			}
		}
		_root.filter1.setEnabled(true);
		setBulbMonochromatic(_root.bulbControlPanel.bulbControlGraphic.monochromaticLight.selected);
		if (_root.beamControlPanel.photonBeamView.selected) {
			setBeamView(SingleBulbModule.photonBeamView);
		}
		else {
			setBeamView(SingleBulbModule.solidBeamView);
		}
	}
	public function deactivate():Void {
		super.deactivate();
		_root.filter1.stop();
		_root.filter1.setEnabled(false);
	}
	public function setBulbMonochromatic(bulbIsMonochromatic:Boolean):Void {
		this.bulbIsMonochromatic = bulbIsMonochromatic;
		if (!bulbIsMonochromatic) {
			var ctx = {rb:255, gb:255, bb:255};
			_root.bulb1.setColor(ctx);
			_root.bulb1.setWavelength(0);
		}
		_root.wavelengthControlLabel._visible = bulbIsMonochromatic;
		_root.wavelengthControlBulbConnector._visible = bulbIsMonochromatic;
		_root.spectrumSlider._visible = bulbIsMonochromatic;
	}
	public function isBulbMonochromatic():Boolean {
		return this.bulbIsMonochromatic;
	}
	public function getFilteredCtx() {
		var ctx;
		if (_root.bulb1.getWavelength() == 0) {
			ctx = ColorUtil.getCtx(_root.filter1.getTransmissionPeak());
		}
		else {
			ctx = _root.bulb1.getCtx();
		}
		ctx.aa = _root.filter1.percentPassed(_root.bulb1.getWavelength());
		return ctx;
	}
	public function setBeamView(viewType:Number):Void {
		beamView = viewType;
		if (viewType == SingleBulbModule.solidBeamView) {
			_root.beam1_A._visible = true;
			_root.beam1_B._visible = true;
		}
		else if (viewType == SingleBulbModule.photonBeamView) {
			_root.beam1_A._visible = false;
			_root.beam1_B._visible = false;
			_root.photonGenerator = _root.attachMovie("PhotonGeneratorID", "pg1", 1);
			_root.photonGenerator.setLocation(_root.greenBulb._x, _root.greenBulb._y);
			_root.photonGenerator.setTheta(0);
			_root.photonGenerator.setWavelength(_root.bulb1.getWavelength());
			_root.photonGenerator.setRate(8);
			_root.bulb1.addColorListener(_root.photonGenerator);
			_root.photonGenerator.start();
		}
	}
}

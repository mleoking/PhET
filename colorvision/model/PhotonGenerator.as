class PhotonGenerator extends MovieClip implements ColorListener {
	private var running:Boolean;
	private var rate:Number;
	private var photons:Array;
	private var photonCanvas_mc:MovieClip;
	private var redColor:Color;
	private var red = {rb:255, gb:0, bb:0};
	private var xLoc:Number;
	private var yLoc:Number;
	private var theta:Number;
	private var color:Number;
	private var wavelength:Number;
	public function PhotonGenerator() {
		rate = 4;
		photons = new Array();
		photonCanvas_mc = _root.createEmptyMovieClip("clock", 1);
		redColor = new Color(photonCanvas_mc);
		redColor.setTransform(red);
		var cnt = 0;
		running = true;
	}
	public function setLocation(xLoc:Number, yLoc:Number) {
		this.xLoc = xLoc;
		this.yLoc = yLoc;
	}
	public function setTheta( theta:Number){
		this.theta = theta;
	}
	public function setColor( color:Number){
		this.color = color;
		var ctx = ColorUtil.colorToCtx(color);		
		this.wavelength = ColorUtil.ctxToWavelength(ctx);
		if(color == 0xFFFFFF) {
			this.wavelength = 0;
		}
	}
	public function setWavelength( wavelength:Number){
		this.wavelength = wavelength;
		var ctx = ColorUtil.getCtx( wavelength);
		this.color = ColorUtil.ctxToColor( ctx );
	}
	public function onEnterFrame() {
		photonCanvas_mc.clear();
		if (running == true) {
			var c;
			var wl;
			if( this.wavelength == 0 ) {
				wl = Math.random()*(_root.maxWavelength - _root.minWavelength) + _root.minWavelength;
				c = ColorUtil.getColor(wl);
			}
			else {
				c = this.color;
			}
				
			for (var n = 0; n < rate; n++) {
				var photon = new Photon(xLoc, yLoc, genTheta(theta * Math.PI / 180), c);
				photons.push(photon);
			}
			// Paint the photons we've got
			for (var i = 0; i < photons.length; i++) {
				// Paint or prune the photons, as need be
				if (photons[i]._x <= _root.head._x) {
					photons[i].paint(photonCanvas_mc);
				}
				else {
					var p = photons[i];
					photons.splice(i, 1);
//					removeMovieClip(p);
				}
			}
		}
	}
	function stop() {
		running = false;
	}
	function start() {
		running = true;
	}
	function isRunning():Boolean{
		return running;
	}
	function activate() {
		super.activate();
		this.start();
	}
	function deactivate() {
		super.deactivate();
		this.stop();
	}
	function getMaxRate():Number {
		return 8;
	}
	function setRate(rate:Number) {
		rate = rate;
	}
	function genTheta(theta0) {
		var d_theta = Math.random() * Math.PI / 16 - Math.PI / 32;
		var angle = theta0 + d_theta;
		return angle;
	}
	function colorChanged( wavelength:Number ):Void{
		this.setWavelength( wavelength);
	}
	function onUnload(){
		this.photonCanvas_mc.clear();
		super.onUnload();
	}
}

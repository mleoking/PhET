class Bulb {
	private var theta:Number;
	private var tick:Number;
	private var photons:Array;
	private var photonBoundsX:Number;
	private var photonBoundsY:Number;
	private var photonBoundsWidth:Number;
	private var photonBoundsHeight:Number;
	private var myColorTransform:Object;
	private var myColor:Color;
	private var filteredBeamColor:Object;
	private var passedColor:Object;
	private var filter:Object;
	private var wavelength:Number;

	function Bulb() {
		this.tick = 0;
		this.photons = new Array();
		this.myColor = new Color(this);
		this.myColorTransform = {rb:0, bb:0, gb:0};
	}
	function setWavelength( wl:Number){
		this.wavelength = wl;
	}	
	function getWavelength():Number{
		return this.wavelength;
	}
	function setColor(ctx):Void {
		this.myColorTransform = ctx;
		this.myColor.setTransform(ctx);
	}
	function setFilter(f) {
		this.filter = f;
	}
	function setFilteredBeamColor(ctx) {
		this.filteredBeamColor = ctx;
	}
	function getFilteredBeamColor() {
		return this.filteredBeamColor;
	}
	function getCtx() {
		return this.myColorTransform;
	}
	// This function never gets called!!!
	function onEnterFrame() {
		passedColor = this.filter.colorPassed(this.myColorTransform);
		this.filteredBeamColor = passedColor;
		//	_root.beam1_ColorpassedColor;
	}
	/*
	cnt0;
	isFilteredfunction( f, p ) {
			if( this._x + p.x >= f._x - f._width / 2 && this._x + p.x <= f._x + f._width * 2
					&& this._y + p.y >= f._y - f._height / 2 && this._y + p.y <= f._y + f._height / 2 ) {
				passedColorf.colorPassed( p.getColor() );
	//			p.setColor( passedColor );
	//			this.setColor( passedColor );
				_root.beam1_ColorpassedColor;			
				trace("$$$ " + cnt++);
			}
			else {
				trace("!!! " + cnt++);
	//			_root.beam1_Colorthis.myColor.getTransform();
				return false;
			}
	}
	*/
	function setDirection(theta) {
		this.theta = theta;
	}
	function setPhotonBounds(x, y, width, height) {
		this.photonBoundsX = x;
		this.photonBoundsY = y;
		this.photonBoundsWidth = width;
		this.photonBoundsHeight = height;
	}
}

class PhotonGeneratorRecordEntry {
	private var p:Photon;
	 var rate:Number;
	private var inUse:Boolean;
	function PhotonGeneratorRecordEntry(p:Photon, rate:Number) {
		this.p = p;
		this.rate = rate;
		this.inUse = true;
	}
	function getPhoton():Photon{
		return this.p;
	}
	function getRate():Number{
		return this.rate;
	}
	function setRate(rate:Number):Void{
		this.rate = rate;
	}
	function setInUse(inUse:Boolean):Void{
		this.inUse = inUse;
	}
	function isInUse():Boolean{
		return this.inUse;
	}
}

class PhotonGeneratorRecordEntry {
	private var p:Photon;
	private var rate:Number;
	function PhotonGeneratorRecordEntry(p:Photon, rate:Number) {
		this.p = p;
		this.rate = rate;
	}
	function getPhoton():Photon{
		return this.p;
	}
	function getRate():Number{
		return this.rate;
	}
}

class ObserverTileClip extends MovieClip implements Observer{
	private var selfRef_mc;
	public function update(model:ChargeGroup):Void {
		//trace(this._name);
		var RGBnbr = model.getV(this._x,this._y)[1];
		selfRef_mc.paneColor.setRGB(RGBnbr);
	}
}
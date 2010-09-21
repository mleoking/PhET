class ObserverVoltageClip extends MovieClip implements Observer {
	var selfRef_mc:MovieClip;
	var voltageTileWidth:Number = 10;

	public function update(model:ChargeGroup):Void{	
		var V:Number = model.getV(this._x,this._y)[0];
		var RGBnbr = model.getV(this._x,this._y)[1];
		selfRef_mc.paneColor.setRGB(RGBnbr);
		//display voltage 
		var Vscaled:Number=V*1.917E-3;
		selfRef_mc.voltage_txt.text = String(0.1*Math.round(10*Vscaled))+" V";//0.01*Math.round(100*Vscaled));
		//unused function
		function my3Round(x:Number):Number{
			var ordMag = Math.round(Math.log(x)/2.303)+1;  //order of magnitude of number; e.g. ordMag(523) = 3; ordMag(0.0743) = -2
			var multiplier = Math.pow(10, ordMag - 3);
			if (x == 0){return 0;}
			else {return Math.round(x/multiplier)*multiplier;}
		}
		//Color the hi-rez voltage tile
		/*
		var i = Math.floor(this._x/voltageTileWidth);
		var j = Math.floor(this._y/voltageTileWidth);
		//trace("X:"+xTileNbr+"     Y:"+yTileNbr);
		_root.voltageHiRezMosaic_mc["tileY"+i+"tileX"+j].update(model);
		_root.voltageHiRezMosaic_mc["tileY"+(i+1)+"tileX"+j].update(model);
		_root.voltageHiRezMosaic_mc["tileY"+i+"tileX"+(j+1)].update(model);
		_root.voltageHiRezMosaic_mc["tileY"+(i-1)+"tileX"+j].update(model);
		_root.voltageHiRezMosaic_mc["tileY"+i+"tileX"+(j-1)].update(model);
		*/
	}
}
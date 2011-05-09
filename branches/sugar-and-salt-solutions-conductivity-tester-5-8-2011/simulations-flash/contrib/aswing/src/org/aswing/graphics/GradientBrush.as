/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.graphics.Brush;

/**
 * @author iiley
 */
class org.aswing.graphics.GradientBrush implements Brush{
	public static var LINEAR:String = "linear";
	public static var RADIAL:String = "radial";
	
	public static function createMatrix(x:Number, y:Number, width:Number, height:Number, direction:Number):Object {
		return {matrixType:"box", x:x, y:y, w:width, h:height, r:direction};	
	}
	
	private var fillType:String;
	private var colors:Array;
	private var alphas:Array;
	private var ratios:Array;
	private var matrix:Object;
	
	public function GradientBrush(fillType:String, colors:Array, alphas:Array, ratios:Array, matrix:Object){
		this.fillType = fillType;
		this.colors = colors;
		this.alphas = alphas;
		this.ratios = ratios;
		this.matrix = matrix;
	}
	
	public function getFillType():String{
		return fillType;
	}
	public function setFillType(t:String):Void{
		fillType = t;
	}
		
	public function getColors():Array{
		return colors;
	}
	public function setColors(cs:Array):Void{
		colors = cs;
	}
	
	public function getAlphas():Array{
		return alphas;
	}
	public function setAlphas(as:Array):Void{
		alphas = as;
	}
	
	public function getRatios():Array{
		return ratios;
	}
	public function setRatios(rs:Array):Void{
		ratios = rs;
	}
	
	public function getMatrix():Object{
		return matrix;
	}
	public function setMatrix(m:Object):Void{
		matrix = m;
	}
	
	public function beginFill(target:MovieClip):Void{
		target.beginGradientFill(fillType, colors, alphas, ratios, matrix);
	}
	
	public function endFill(target:MovieClip):Void{
		target.endFill();
	}	
}

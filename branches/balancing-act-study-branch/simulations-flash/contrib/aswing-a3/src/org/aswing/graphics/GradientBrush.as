/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.graphics{

import org.aswing.graphics.IBrush;
import flash.display.Graphics;
import flash.geom.Matrix;
import flash.display.GradientType;

/**
 * GradientBrush encapsulate the fill paramters for flash.display.Graphics.beginGradientFill()
 * @see http://livedocs.macromedia.com/flex/2/langref/flash/display/Graphics.html#beginGradientFill()
 * @author iiley
 */
public class GradientBrush implements IBrush{
	
	public static const LINEAR:String = GradientType.LINEAR;
	public static const RADIAL:String = GradientType.RADIAL;
	
	private var fillType:String;
	private var colors:Array;
	private var alphas:Array;
	private var ratios:Array;
	private var matrix:Matrix;
	private var spreadMethod:String;
	private var interpolationMethod:String;
	private var focalPointRatio:Number;
	
	/**
	 * Create a GradientBrush object<br>
	 * you can refer the explaination for the paramters from Adobe's doc
	 * to create a Matrix, you can use matrix.createGradientBox() from the matrix object itself
	 * 
	 * @see http://livedocs.macromedia.com/flex/2/langref/flash/display/Graphics.html#beginGradientFill()
	 * @see http://livedocs.macromedia.com/flex/2/langref/flash/geom/Matrix.html#createGradientBox()
	 */
	public function GradientBrush(fillType:String , colors:Array, alphas:Array, ratios:Array, matrix:Matrix, 
					spreadMethod:String = "pad", interpolationMethod:String = "rgb", focalPointRatio:Number = 0){
		this.fillType = fillType;
		this.colors = colors;
		this.alphas = alphas;
		this.ratios = ratios;
		this.matrix = matrix;
		this.spreadMethod = spreadMethod;
		this.interpolationMethod = interpolationMethod;
		this.focalPointRatio = focalPointRatio;
	}
	
	public function getFillType():String{
		return fillType;
	}
	
	/**
	 * 
	 */
	public function setFillType(t:String):void{
		fillType = t;
	}
		
	public function getColors():Array{
		return colors;
	}
	
	/**
	 * 
	 */
	public function setColors(cs:Array):void{
		colors = cs;
	}
	
	public function getAlphas():Array{
		return alphas;
	}
	
	/**
	 * Pay attention that the value in the array should be between 0-1. if the value is greater than 1, 1 will be used, if the value is less than 0, 0 will be used
	 */
	public function setAlphas(alphas:Array):void{
		this.alphas = alphas;
	}
	
	public function getRatios():Array{
		return ratios;
	}
	
	/**
	 * Ratios should be between 0-255, if the value is greater than 255, 255 will be used, if the value is less than 0, 0 will be used
	 */
	public function setRatios(ratios:Array):void{
		ratios = ratios;
	}
	
	public function getMatrix():Object{
		return matrix;
	}
	
	/**
	 * 
	 */
	public function setMatrix(m:Matrix):void{
		matrix = m;
	}
	
	/**
	 * @inheritDoc 
	 */
	public function beginFill(target:Graphics):void{
		target.beginGradientFill(fillType, colors, alphas, ratios, matrix, 
			spreadMethod, interpolationMethod, focalPointRatio);
	}
	
	/**
	 * @inheritDoc 
	 */
	public function endFill(target:Graphics):void{
		target.endFill();
	}	
}

}

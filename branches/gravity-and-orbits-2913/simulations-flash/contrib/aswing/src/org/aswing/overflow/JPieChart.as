/*
 Copyright aswing.org, see the LICENCE.txt.
*/

 /**
* @author firdosh
*/

import org.aswing.Component;
import org.aswing.ElementCreater;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.util.Delegate;

class org.aswing.overflow.JPieChart extends Component
{
	private var _dataSet : Array;
	private var _colorSet : Array;
	private var _angles : Array;
	private var _wedges : Array;
	private var _legends : Array;
	private var _legend_txt : Array;
	private var _title : String;
	private var _showLegend : Boolean;
	private var MAX : Number = 0;
	
	public function JPieChart (title : String, dataSet : Array, colorSet : Array)
	{
		_root.DEBUG.text += "In Constructor";
		super ();
		setName ("JPieChart");
		if (title != null)
		{
			_title = title;
		} 
		else
		{
			_title = "JPieChart";
		}
		if (colorSet != null)
		{
			_colorSet = colorSet;
		} 
		else
		{
			_colorSet = new Array ();
			if (dataSet != null)
			{
				for (var i : Number = 0; i < dataSet.length; i ++)
				{
					_colorSet.push (getRandomnColor ());
				}
			}
		}
		if (dataSet != null)
		{
			_dataSet = dataSet;
		} 
		else
		{
			_dataSet = new Array ();
		}
		_angles = new Array ();
		_wedges = new Array ();
		_legend_txt = new Array ();
	}
	
	/**
	 * Calculates the rotation angle for the value
	 */
	
	private function getRotation (val : Number) : Number
	{
		var rot : Number = 0;
		for (var i : Number = 0; i <= val; i ++)
		{
			rot += _angles [i];
		}
		return rot;
	}
	/**
	 * Calculates the sum of the dataset and the angle
	 */
	private function sortAndLoadDataSet (Void) : Void
	{
		MAX = 0;
		_angles = new Array ();
		_dataSet.sort (Array.NUMERIC);
		for (var i : Number = 0; i < _dataSet.length; i ++)
		{
			MAX += _dataSet [i];
		}
		for (var i : Number = 0; i < _dataSet.length; i ++)
		{
			_angles [i] = (360 * _dataSet [i]) / MAX;			
		}
		
	}
	
	/**
	 * Re paints the component
	 */
	private function paint (b : Rectangle) : Void
	{
		_root.DEBUG.text += "\nIn Paint";
		sortAndLoadDataSet ();
		for (var i : Number = 0; i < _wedges.length; i ++)
		{
			_wedges [i].removeMovieClip ();
			_legends [i].removeMovieClip ();
			_legend_txt [i].removeMovieClip ();
		}
		_wedges = new Array ();
		var graphics : Graphics;
		var loactionY : Number = this.getY ();
		//_root.DEBUG.text+="\n" + "Angles -> " + _angles.length;
		for (var i : Number = 0; i < _angles.length; i ++)
		{
			//_root.DEBUG.text+="In for " +_colorSet[i] + "  " + _angles[i]+"\n";
			var wedge : MovieClip = ElementCreater.getInstance ().createMC (root_mc, "wedge" + i);
			wedge.lineStyle (1, 0x000000, 50);
			wedge.beginFill (_colorSet [i] , 100);
			graphics = new Graphics (wedge);
			graphics.wedge (this.getWidth () / 2, this.getWidth () / 2, this.getHeight () / 2, _angles [i] , getRotation (i - 1));
			wedge.onPress=Delegate.create(this,__onPress,i);
			_wedges.push (wedge);
		}
	}
	
	/**
	 * Dispatches the onPress event
	 */
	private function __onPress (index:Number) : Void
	{
		dispatchEvent(this.createEventObj(ON_PRESS, _dataSet[index]));
	}
		
	/**
	 * Generates a randomn color
	 */
	private function getRandomnColor (Void) : Number
	{
		var col : String = "0x";
		for (var i : Number = 0; i < 6; i ++)
		{
			var digit : Number = Math.floor (Math.random () * 15);
			if (digit == 10)
			{
				col += "A";
			} 
			else if (digit == 11)
			{
				col += "B";
			} 
			else if (digit == 12)
			{
				col += "C";
			} 
			else if (digit == 13)
			{
				col += "D";
			} 
			else if (digit == 14)
			{
				col += "E";
			} 
			else if (digit == 15)
			{
				col += "F";
			} 
			else
			{
				col += digit + "";
			}
		}
		return new Number (col);
	}
	private function getRandomnGreyScaleColor (Void) : Number
	{
		var col : String = "0x";
		var digit : Number = Math.floor (Math.random () * (9 - 3)) + 3;
		for (var i : Number = 0; i < 6; i ++)
		{
			col += digit + "";
		}
		//_root.DEBUG.text += "\nCOLOR = " + col + "\n";
		return new Number (col);
	}
	
	public function setDataSet(val:Array):Void{
		_dataSet=new Array();
		_dataSet=val;
		paint();
	}
	
	public function getDataSet(Void):Array{
		return _dataSet;
	}
	
	public function setColorSet(val:Array):Void{
		_colorSet=new Array();
		_colorSet=val;
		paint();
	}
	
	public function getColorSet(Void):Array{
		return _colorSet;
	}
	
}

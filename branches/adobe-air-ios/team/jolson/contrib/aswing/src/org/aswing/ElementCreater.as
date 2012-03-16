/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.util.DepthManager;
import org.aswing.util.MathUtils;
import org.aswing.util.MCUtils;

/**
 * The creater use to create UI elements like movieclips, textfield...
 * @author iiley
 */
class org.aswing.ElementCreater{
	
	private static var _instance:ElementCreater;
	private var _prefix:String="aswing_e_";
	private var _nextHandle:Number=0;
	
	private var _defaultMovieClip:Object={tabEnabled:false, _focusrect:false, useHandCursor:false};
	private var _defaultTextField:Object={tabEnabled:false, autoSize:"left", embedFonts:false, selectable:false, textColor:0xB333C, antiAliasType: "advanced"};	
	
	private function ElementCreater(){};
	
	public static function getInstance():ElementCreater{
		if(_instance==undefined){
			_instance=new ElementCreater();
		}
		return _instance;
	}
	
	/**
	 * Creates and returns a new empty MovieClip.
	 * <p>
	 * createMC(position_mc:MovieClip, nameStart:String, depth:Number)<br>
	 * createMC(position_mc:MovieClip, nameStart:String,)<br>
	 * createMC(position_mc:MovieClip)<br>
	 * 
	 * @param position_mc new mc's parent.
	 * @param nameStart (optional)the new mc's name prefix. Default is aswing_e_
	 * @param depth (optional)new mc's depth. Default is getNextHighestDepth
	 * @return the new empty MC created.
	 */
	public function createMC(position_mc:MovieClip, nameStart:String, depth:Number):MovieClip{
		return createMCWithName(position_mc, getAvailableName(position_mc, nameStart), depth);
	}
	
	/**
	 * Creates and returns a new empty MovieClip with specified name.
	 * <p>
	 * createMCWithName(position_mc:MovieClip, name:String, depth:Number)<br>
	 * createMCWithName(position_mc:MovieClip, name:String,)<br>
	 * 
	 * @param position_mc new mc's parent.
	 * @param name the new mc's name.
	 * @param depth (optional)new mc's depth. Default is getNextHighestDepth
	 * @return the new empty MC created.
	 */	
	public function createMCWithName(position_mc:MovieClip, name:String, depth:Number):MovieClip{
		if(depth == undefined) depth = DepthManager.getNextAvailableDepth(position_mc);
		var result_mc:MovieClip = position_mc.createEmptyMovieClip(name, depth);
		initObjectParams(result_mc, _defaultMovieClip);
		return result_mc;
	}
	
	/**
	 * Attaches and returns a new MovieClip by given linkage id.
	 * <p>
	 * attachMC(position_mc:MovieClip, linkage:String, nameStart:String, depth:Number)<br>
	 * attachMC(position_mc:MovieClip, linkage:String, nameStart:String,)<br>
	 * attachMC(position_mc:MovieClip, linkage:String,)<br>
	 * 
	 * @param position_mc new mc's parent.
	 * @param linkage the linkage id.
	 * @param nameStart (optional)the new mc's name prefix. Default is aswing_e_
	 * @param depth (optional)new mc's depth. Default is getNextHighestDepth
	 * @return the new MC attached.
	 */	
	public function attachMC(position_mc:MovieClip, linkage:String, nameStart:String, depth:Number):MovieClip{
		return attachMCWithName(position_mc, linkage, getAvailableName(position_mc, nameStart), depth);
	}
	
	/**
	 * Attaches and returns a new MovieClip by given linkage id and specified name.
	 * <p>
	 * attachMCWithName(position_mc:MovieClip, linkage:String, name:String, depth:Number)<br>
	 * attachMCWithName(position_mc:MovieClip, linkage:String, name:String,)<br>
	 * 
	 * @param position_mc new mc's parent.
	 * @param linkage the linkage id.
	 * @param name the new mc's name.
	 * @param depth (optional)new mc's depth. Default is getNextHighestDepth
	 * @return the new MC attached.
	 */		
	public function attachMCWithName(position_mc:MovieClip, linkage:String, name:String, depth:Number):MovieClip{
		if(depth == undefined) depth = DepthManager.getNextAvailableDepth(position_mc);
		var result_mc:MovieClip = position_mc.attachMovie(linkage, name, depth);
		initObjectParams(result_mc, _defaultMovieClip);
		return result_mc;
	}	
	
	/**
	 * Creates and returns a new TextField.
	 * <p>
	 * createTF(position_mc:MovieClip, nameStart:String, depth:Number)<br>
	 * createTF(position_mc:MovieClip, nameStart:String,)<br>
	 * createTF(position_mc:MovieClip)<br>
	 * 
	 * @param position_mc new textField's parent.
	 * @param nameStart (optional)the new textField's name prefix. Default is aswing_e_
	 * @param depth (optional)new textField's depth. Default is getNextHighestDepth
	 * @return the new textField created.
	 */	
	public function createTF(position_mc:MovieClip, nameStart:String, depth:Number):TextField{
		return createTFWithName(position_mc, getAvailableName(position_mc, nameStart), depth);
	}
	
	/**
	 * Creates and returns a new TextField with specified name.
	 * <p>
	 * createTF(position_mc:MovieClip, name:String, depth:Number)<br>
	 * createTF(position_mc:MovieClip, name:String,)<br>
	 * 
	 * @param position_mc new textField's parent.
	 * @param name the new textField's name.
	 * @param depth (optional)new textField's depth. Default is getNextHighestDepth
	 * @return the new textField created.
	 */		
	public function createTFWithName(position_mc:MovieClip, name:String, depth:Number):TextField{
		if(depth == undefined) depth = DepthManager.getNextAvailableDepth(position_mc);
		position_mc.createTextField(name, depth,0,0,0,0);
		var result_txt:TextField = position_mc[name];
		initObjectParams(result_txt, _defaultTextField);
		return result_txt;
	}
	
	private function getAvailableName(position_mc:MovieClip, nameStart:String):String{
		if(nameStart == undefined) nameStart = _prefix;
		while(_nextHandle < MathUtils.STRING_REPRESENTABLE_MAX){
			var name:String = nameStart + (_nextHandle++);
			if(!MCUtils.isMovieClipExist(position_mc[name])){
				return name;
			}
		}
		_nextHandle = -1;
		while(_nextHandle > MathUtils.STRING_REPRESENTABLE_MIN){
			var name:String = nameStart + (_nextHandle--);
			if(!MCUtils.isMovieClipExist(position_mc[name])){
				return name;
			}
		}
		//this should be never happen, since no one can use whole numbers for MCs 
		//between MathUtils.STRING_REPRESENTABLE_MIN to MathUtils.STRING_REPRESENTABLE_MAX
		trace("there_is_not_any_number_available_for_naming");
		return nameStart + "_there_is_not_any_number_available_for_naming";
	}
	
	private function initObjectParams(target_obj:Object, params_obj:Object):Void{
		if(params_obj == undefined) return;
		for(var i:String in params_obj){
			target_obj[i] = params_obj[i];
		}
	}
}

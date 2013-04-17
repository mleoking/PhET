/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASTextFormat;
import org.aswing.util.HashMap;

/**
 * ASFont
 * @author Tomato
 */
class org.aswing.ASFont{
 	
 	/**
 	 * default values
 	 */
	public static var DEFAULT_NAME:String = "Tahoma";
	public static var DEFAULT_SIZE:Number = 11;
 	
 	
 	/**
 	 * a font's basic attributes
 	 */
 	private var fontName:String;
 	private var fontSize:Number;

 	/**
 	 * a font's features
 	 */
 	private var fontBold:Boolean;
 	private var fontItalic:Boolean;
 	private var fontUnderline:Boolean;
 	
 	private var embedFonts:Boolean;
 	
 	private var asTF:ASTextFormat;
 	
 	
 	/**
 	 * Create a ASFont by parameters : name, size, blod, italic, underline, embedFonts, Each of 
 	 * can be missed, a default value will be set, if missed.
 	 */
 	public function ASFont(_name:String , _size:Number , _bold:Boolean , _italic:Boolean , _underline:Boolean, _embedFonts:Boolean){
 		this.setName(_name);
 		this.setSize(_size);
 		this.setBold(_bold);
 		this.setItalic(_italic);
 		this.setUnderline(_underline);
 		this.setEmbedFonts(_embedFonts);
 		asTF = ASTextFormat.getEmptyASTextFormat();
 		asTF.setASFont(this);
 	}
 	
 	public static function getASFont(_name:String , _size:Number):ASFont{
 		return new ASFont(_name , _size , false , false , false, false);
 	}
 	 	
 	public static function getASFontFromASTextFormat(ASTF:ASTextFormat):ASFont{
 		return ASTF.getASFont();
 	}
 	
 	
 	// setter and getter methods
 	private function setName(_name:String):Void{
 		if(_name == null){
 			this.fontName = ASFont.DEFAULT_NAME;
 		}else{
	 		this.fontName = _name;
 		}
 	}
 	
 	public function getName():String{
 		return this.fontName;
 	}
 	
 	private function setSize(_size:Number):Void{
 		if(_size == null){
	 		this.fontSize = ASFont.DEFAULT_SIZE;
 		}else{
	 		this.fontSize = _size;
 		}
 	}
 	
 	public function getSize():Number{
 		return this.fontSize;
 	}
 	
 	private function setBold(_bold:Boolean):Void{
 		this.fontBold = _bold;
 		if(this.fontBold == null){
 			this.fontBold = false;
 		}
 	}
 	
 	public function getBold():Boolean{
 		return this.fontBold;
 	}
 	
 	private function setItalic(_italic:Boolean):Void{
 		this.fontItalic = _italic;
 		if(this.fontItalic == null){
 			this.fontItalic = false;
 		}
 	}
 	
 	public function getItalic():Boolean{
 		return this.fontItalic;
 	}
 	
 	private function setUnderline(_underline:Boolean):Void{
 		this.fontUnderline = _underline;
 		if(this.fontUnderline == null){
 			this.fontUnderline = false;
 		}
 	}
 	
 	public function getUnderline():Boolean{
 		return this.fontUnderline;
 	}
 	
 	private function setEmbedFonts(b:Boolean):Void{
 		if(b == undefined) b = false;
 		embedFonts = b;
 	}
 	
 	public function getEmbedFonts():Boolean{
 		return embedFonts;
 	}
 	
 	/**
 	 * @return a HashMap which contains the feature's value
 	 */
 	public function getFeatures():HashMap{
 		var features:HashMap = new HashMap();
 		features.put("bold" , this.getBold());
 		features.put("italic" , this.getItalic());
 		features.put("underline" , this.getUnderline());
 		features.put("embedFonts" , this.getEmbedFonts());
 		return features;
 	}
 	
 	public function getASTextFormat():ASTextFormat{
 		return asTF;
 	}
 	 	
 	public function toString():String{
 		return "ASFont[name:" + fontName 
 			+ ", size:" + fontSize
 			+ ", bold:" + fontBold 
 			+ ", italic:" + fontItalic 
 			+ ", underline:" + fontUnderline 
 			+ ", embedFonts:" + embedFonts + "]";  
 	}
 	
 }

/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.ASTextExtent;

/**
 * ASTextFormat
 * @author Tomato
 * @author Igor Sadovskiy
 */
 
 /**
  * unHandled TextFormat Properties :
  * 	
  * 	<li>TextFormat.leading</li>
  * 	<li>TextFormat.tabStops</li>
  * 	<li>TextFormat.target</li>
  * 	<li>TextFormat.url</li>
  */
 
 class org.aswing.ASTextFormat{
 	
 	/**
 	 * align -> left
 	 */
 	public static var LEFT:Number = -1;
 	/**
 	 * align -> center
 	 */
 	public static var CENTER:Number = 0;
 	/**
 	 * align -> right
 	 */
 	public static var RIGHT:Number = 1;
 	
 	/**
 	 * Default values
 	 * You can customize these values to match your major text format style
 	 * And then just use little codes to set some special ones
 	 */
 	public static var DEFAULT_NAME:String = "Tahoma";
 	public static var DEFAULT_SIZE:Number = 12;
 	public static var DEFAULT_BOLD:Boolean = false;
 	public static var DEFAULT_ITALIC:Boolean = false;
 	public static var DEFAULT_UNDERLINE:Boolean = false;
 	public static var DEFAULT_EMBEDFONTS:Boolean = false;
 	public static var DEFAULT_ALIGN:Number = LEFT;
 	public static var DEFAULT_BLOCKINDENT:Number = 0;
 	public static var DEFAULT_INDENT:Number = 0;
 	public static var DEFAULT_BULLET:Boolean = false;
 	public static function get DEFAULT_COLOR():Number{
 		return ASColor.BLACK.getRGB();
 	}
 	public static var DEFAULT_LEFTMARGIN:Number = 0;
 	public static var DEFAULT_RIGHTMARGIN:Number = 0;
 	
 	/**
 	 * 
 	 */
 	private var _name:String;
 	private var _size:Number;
 	private var _bold:Boolean;
 	private var _italic:Boolean;
 	private var _underline:Boolean;
 	private var _align:Number;
 	private var _blockIndent:Number;
 	private var _indent:Number;
 	private var _bullet:Boolean;
 	private var _color:Number;
 	private var _leftMargin:Number;
 	private var _rightMargin:Number;
 	private var _embedFonts:Boolean;
 	
 	/**
 	 * private constructor
 	 */
 	private function ASTextFormat(){
 		
 	}
 	
 	/**
 	 * use these create methods to get an ASTextFormat instance
 	 */
 	public static function getEmptyASTextFormat():ASTextFormat{
 		return new ASTextFormat();
 	}
 	
 	public static function getDefaultASTextFormat():ASTextFormat{
 		var asTF:ASTextFormat = new ASTextFormat();
 		asTF.setName(DEFAULT_NAME);
 		asTF.setSize(DEFAULT_SIZE);
 		asTF.setColor(DEFAULT_COLOR);
 		asTF.setBold(DEFAULT_BOLD);
 		asTF.setItalic(DEFAULT_ITALIC);
 		asTF.setUnderline(DEFAULT_UNDERLINE);
 		asTF.setEmbedFonts(DEFAULT_EMBEDFONTS);
 		asTF.setAlign(DEFAULT_ALIGN);
 		asTF.setBlockIndent(DEFAULT_BLOCKINDENT);
 		asTF.setIndent(DEFAULT_INDENT);
 		asTF.setLeftMargin(DEFAULT_LEFTMARGIN);
 		asTF.setRightMargin(DEFAULT_RIGHTMARGIN);
 		return asTF;
 	}
 	
 	public static function getASTextFormatWithName(name:String):ASTextFormat{
 		var asTF:ASTextFormat = new ASTextFormat();
 		asTF.setName(name);
 		return asTF;
 	}
 	
 	public static function getASTextFormatWithNameSize(name:String , size:Number):ASTextFormat{
 		var asTF:ASTextFormat = new ASTextFormat();
 		asTF.setName(name);
 		asTF.setSize(size);
 		return asTF;
 	}
 	
 	public static function getASTextFormatWithNameColor(name:String , color:Number):ASTextFormat{
 		var asTF:ASTextFormat = new ASTextFormat();
 		asTF.setName(name);
 		asTF.setColor(color);
 		return asTF;
 	}
 	
 	public static function getASTextFormatWithNameSizeColor(name:String , size:Number , color:Number):ASTextFormat{
 		var asTF:ASTextFormat = new ASTextFormat();
 		asTF.setName(name);
 		asTF.setSize(size);
 		asTF.setColor(color);
 		return asTF;
 	}
 	
 	public static function getASTextFormatWithASFont(font:ASFont):ASTextFormat{
 		var asTF:ASTextFormat = new ASTextFormat();
 		asTF.setASFont(font);
 		return asTF;
 	}
 	
 	public static function getASTextFormatWithTextFormat(tf:TextFormat):ASTextFormat{
 		var astf:ASTextFormat = new ASTextFormat();
 		
 		astf.setName((tf.font != null) ? tf.font : ASTextFormat.DEFAULT_NAME);
 		astf.setSize((tf.size != null) ? tf.size : ASTextFormat.DEFAULT_SIZE);
		astf.setBold((tf.bold != null) ? tf.bold : ASTextFormat.DEFAULT_BOLD);
		astf.setItalic((tf.italic != null) ? tf.italic : ASTextFormat.DEFAULT_ITALIC);
	 	astf.setUnderline((tf.underline != null) ? tf.underline : ASTextFormat.DEFAULT_UNDERLINE);
	 	switch(tf.align){
	 		case "left":
	 			astf.setAlign(LEFT);
	 			break;
	 		case "center":
	 			astf.setAlign(CENTER);
	 			break;
	 		case "right":
	 			astf.setAlign(RIGHT);
	 			break;
	 		default:
	 			astf.setAlign(DEFAULT_ALIGN);
	 			break;
	 	}
	 	astf.setBlockIndent((tf.blockIndent != null) ? tf.blockIndent : ASTextFormat.DEFAULT_BLOCKINDENT);
	 	astf.setIndent((tf.indent != null) ? tf.indent : ASTextFormat.DEFAULT_INDENT);
	 	astf.setBullet((tf.bullet != null) ? tf.bullet : ASTextFormat.DEFAULT_BULLET);
	 	astf.setColor((tf.color != null) ? tf.color : ASTextFormat.DEFAULT_COLOR);
	 	astf.setLeftMargin((tf.leftMargin != null) ? tf.leftMargin : ASTextFormat.DEFAULT_LEFTMARGIN);
	 	astf.setRightMargin((tf.rightMargin != null) ? tf.rightMargin : ASTextFormat.DEFAULT_RIGHTMARGIN);
	 	
	 	return astf;
 	} 	
 	
 	public static function getTextFormatWithASTextFormat(tf:ASTextFormat):TextFormat {
 		return tf.getTextFormat();
 	}
 	 	 
 	private function getTextFormat():TextFormat{
 		var tf:TextFormat = new TextFormat();
 		
 		tf.font = ASTextFormat.DEFAULT_NAME;
 		tf.size = ASTextFormat.DEFAULT_SIZE;
		tf.bold = ASTextFormat.DEFAULT_BOLD;
		tf.italic = ASTextFormat.DEFAULT_ITALIC;
	 	tf.underline = ASTextFormat.DEFAULT_UNDERLINE;
	 	switch(DEFAULT_ALIGN){
	 		case ASTextFormat.LEFT:
	 			tf.align = "left";
	 			break;
	 		case ASTextFormat.CENTER:
	 			tf.align = "center";
	 			break;
	 		case ASTextFormat.RIGHT:
	 			tf.align = "right";
	 			break;
	 		default:
	 			tf.align = "left";
	 			break;
	 	}
	 	tf.blockIndent = ASTextFormat.DEFAULT_BLOCKINDENT;
	 	tf.indent = ASTextFormat.DEFAULT_INDENT;
	 	tf.bullet = ASTextFormat.DEFAULT_BULLET;
	 	tf.color = ASTextFormat.DEFAULT_COLOR;
	 	tf.leftMargin = ASTextFormat.DEFAULT_LEFTMARGIN;
	 	tf.rightMargin = ASTextFormat.DEFAULT_RIGHTMARGIN;
	 	
	 	if(this._name != null){
	 		tf.font = this._name;
	 	}
	 	if(this._size != null){
	 		tf.size = this._size;
	 	}
	 	if(this._bold != null){
	 		tf.bold = this._bold;
	 	}
	 	if(this._italic != null){
	 		tf.italic = this._italic;
	 	}
	 	if(this._underline != null){
	 		tf.underline = this._underline;
	 	}
	 	if(this._align != null){
	 		switch(this._align){
		 		case ASTextFormat.LEFT:
		 			tf.align = "left";
		 			break;
		 		case ASTextFormat.CENTER:
		 			tf.align = "center";
		 			break;
		 		case ASTextFormat.RIGHT:
		 			tf.align = "right";
		 			break;
		 		default:
		 			tf.align = "left";
		 			break;
	 		}
	 	}
	 	if(this._blockIndent != null){
	 		tf.blockIndent = this._blockIndent;
	 	}
	 	if(this._indent != null){
	 		tf.indent = this._indent;
	 	}
	 	if(this._bullet != null){
	 		tf.bullet = this._bullet;
	 	}
	 	if(this._color != null){
	 		tf.color = this._color;
	 	}
	 	if(this._leftMargin != null){
	 		tf.leftMargin = this._leftMargin;
	 	}
	 	if(this._rightMargin != null){
	 		tf.rightMargin = this._rightMargin;
	 	}
	 	return tf;
 	} 	
 	 
 	public function setName(newName:String):Void{
 		this._name = newName;
 	}
 	
 	public function getName():String{
 		return this._name;
 	}
 	
 	public function setSize(newSize:Number):Void{
 		this._size = newSize;
 	}
 	
 	public function getSize():Number{
 		return this._size;
 	}
 	
 	public function setBold(newBold:Boolean):Void{
 		this._bold = newBold;
 	}
 	
 	public function getBold():Boolean{
 		return this._bold;
 	}
 	
 	public function setItalic(newItalic:Boolean):Void{
 		this._italic = newItalic;
 	}
 	
 	public function getItalic():Boolean{
 		return this._italic;
 	}
 	
 	public function setUnderline(newUnderline:Boolean):Void{
 		this._underline = newUnderline;
 	}
 	
 	public function getUnderline():Boolean{
 		return this._underline;
 	}
 	
 	public function setAlign(newAlign:Number):Void{
 		this._align = newAlign;
 	}
 	
 	public function getAlign():Number{
 		return this._align;
 	}
 	
 	public function setBlockIndent(newBlockIndent:Number):Void{
 		this._blockIndent = newBlockIndent;
 	}
 	
 	public function getBlockIndent():Number{
 		return this._blockIndent;
 	}
 	
 	public function setIndent(newIndent:Number):Void{
 		this._indent = newIndent;
 	}
 	
 	public function getIndent():Number{
 		return this._indent;
 	}
 	
 	public function setBullet(newBullet:Boolean):Void{
 		this._bullet = newBullet;
 	}
 	
 	public function getBullet():Boolean{
 		return this._bullet;
 	}
 	
 	public function setColor(newColor:Number):Void{
 		this._color = newColor;
 	}
 	
 	public function getColor():Number{
 		return this._color;
 	}
 	
 	public function setLeftMargin(newLeftMargin:Number):Void{
 		this._leftMargin = newLeftMargin;
 	}
 	
 	public function getLeftMargin():Number{
 		return this._leftMargin;
 	}
 	
 	public function setRightMargin(newRightMargin:Number):Void{
 		this._rightMargin = newRightMargin;
 	}
 	
 	public function getRightMargin():Number{
 		return this._rightMargin;
 	}
 	
 	public function setEmbedFonts(b:Boolean):Void{
 		if(b == undefined) b = DEFAULT_EMBEDFONTS;
 		_embedFonts = b;
 	}
 	
 	public function getEmbedFonts():Boolean{
 		return _embedFonts;
 	}
 	 	 	
 	public function getTextExtent(text:String, wrapWidth:Number):ASTextExtent{
 		var tempTextFormat:TextFormat = this.getTextFormat();
 		var infos:Object;
 		if(wrapWidth != null){
 			infos = tempTextFormat.getTextExtent(text , wrapWidth);
 		}else{
 			infos = tempTextFormat.getTextExtent(text);
 		}
 		return new ASTextExtent(infos.width, infos.height, infos.ascent, infos.descent, 
 								infos.textFieldWidth, infos.textFieldHeight);
 	}
 	
 	public function setASFont(font:ASFont):Void{
 		this.setName(font.getName());
 		this.setSize(font.getSize());
 		this.setBold(font.getBold());
 		this.setItalic(font.getItalic());
 		this.setUnderline(font.getUnderline());
 		this.setEmbedFonts(font.getEmbedFonts());
 	}
 	
 	public function getASFont():ASFont{
 		return new ASFont(this.getName() , this.getSize() , this.getBold() , this.getItalic() , this.getUnderline(), this.getEmbedFonts());
 	}
 	
 	
 	
 	/**
 	 * apply to the whole TextField
 	 */
 	public function applyToText(text:TextField):Void{
 		text.setTextFormat(getTextFormat());
 		text.embedFonts = getEmbedFonts();
 	}
 	
 	/**
 	 * apply to part of the TextField
 	 */
 	public function applyToPartOfText(text:TextField , begin:Number , end:Number):Void{
 		text.setTextFormat(begin , end , this.getTextFormat());
 		text.embedFonts = getEmbedFonts();
 	}
 	
 	/**
 	 * apply one char of the TextField
 	 */
 	public function applyToOneOfText(text:TextField , index:Number):Void{
 		text.setTextFormat(index , this.getTextFormat());
 		text.embedFonts = getEmbedFonts();
 	}
 	
 	/**
 	 * Applies to the whole TextField and New coming text.
 	 */
 	public function applyToTextCurrentAndNew(text:TextField):Void{
 		var tf:TextFormat = getTextFormat();
 		text.setTextFormat(tf);
 		text.setNewTextFormat(tf);
 		text.embedFonts = getEmbedFonts();
 	}
 	
 	/**
 	 * apply for new Text of the TextField
 	 */
 	public function applyToTextForNew(text:TextField):Void{
 		text.setNewTextFormat(getTextFormat());
 		text.embedFonts = getEmbedFonts();
 	}
 	
 	public function clone():ASTextFormat{
 		var t:ASTextFormat = new ASTextFormat();
 		t._align = _align;
 		t._blockIndent = _blockIndent;
 		t._bold = _bold;
 		t._bullet = _bullet;
 		t._color = _color;
 		t._indent = _indent;
 		t._italic = _italic;
 		t._leftMargin = _leftMargin;
 		t._name = _name;
 		t._rightMargin = _rightMargin;
 		t._size = _size;
 		t._underline = _underline;
 		t._embedFonts = _embedFonts;
 		return t;
 	}
 		
 	public function toString():String{
		return "ASTextFormat";
	}
 	
 }

import org.aswing.graphics.Pen;

/**
 * AdvancedPen, support flash8 lineStyle.
 * @author iiley
 */
class org.aswing.graphics.AdvancedPen extends Pen {
	
	private var pixelHinting:Boolean;
	private var noScale:String;
	private var capsStyle:String;
	private var jointStyle:String;
	private var miterLimit:Number;
	
	public function AdvancedPen(color:Object, thickness:Number, pixelHinting:Boolean, noScale:String, capsStyle:String, jointStyle:String, miterLimit:Number) {
		super(color, thickness);
		this.pixelHinting = pixelHinting;
		this.noScale      = noScale;
		this.capsStyle    = capsStyle;
		this.jointStyle   = jointStyle;
		this.miterLimit   = miterLimit;
	}
	
	public function setTo(target:MovieClip):Void{
		target.lineStyle(_thickness, _color, _alpha, pixelHinting, noScale, capsStyle, jointStyle, miterLimit);
	}
	
	public function getNoScale():String {
		return noScale;
	}

	public function setNoScale(noScale:String):Void {
		this.noScale = noScale;
	}

	public function getPixelHinting():Boolean {
		return pixelHinting;
	}

	public function setPixelHinting(pixelHinting:Boolean):Void {
		this.pixelHinting = pixelHinting;
	}

	public function getCapsStyle():String {
		return capsStyle;
	}

	public function setCapsStyle(capsStyle:String):Void {
		this.capsStyle = capsStyle;
	}

	public function getMiterLimit():Number {
		return miterLimit;
	}

	public function setMiterLimit(miterLimit:Number):Void {
		this.miterLimit = miterLimit;
	}

	public function getJointStyle():String {
		return jointStyle;
	}

	public function setJointStyle(jointStyle:String):Void {
		this.jointStyle = jointStyle;
	}
	

}
package{
	import flash.display.*;
	
	public class PreviewCurve extends Sprite{
		
		private var myModel:Model;
		
		public function PreviewCurve(model:Model){
			this.myModel = model;
			this.initialize();
		}//end of constructor
		
		public function initialize():void{
			this.myModel.registerPreview(this);
		}
		
		public function update():void{
			with(this.graphics){
				clear();
				lineStyle(1.5, 0x0000ff);
				moveTo(0, 30-this.myModel.pre_arr[xp]);
			}
			for(var xp:int; xp < this.myModel.nbrPtsPreview; xp++){
					this.graphics.lineTo(xp, 30-this.myModel.pre_arr[xp]);
					//trace("x: "+xp+"    yp:"+this.myModel.pre_arr[xp]);
			}
		}//end of update();
		
	}//end of class
}//end of package
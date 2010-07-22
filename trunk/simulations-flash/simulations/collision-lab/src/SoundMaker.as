package{
	import flash.media.Sound;
	public class SoundMaker{
		var myModel:Model;
		var myMainView:MainView;
		var clickSound:Sound;
		
		public function SoundMaker(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.clickSound = new ClickSound();
			this.myModel.registerView(this);
		}//end of constructor
		
		public function update():void{
			if(this.myModel.sounding){
				this.clickSound.play();
				//trace("SoundMaker.update() called");
			}
		}
	}//end of class
	
}//end of package
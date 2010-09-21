class ReadOutView{
	var model:Object;
	var readOutPanel:MovieClip;
	var N:Number;
	var mean:Number;
	var trueMean:Number;
	var sigma:Number;
	var stdev:Number;
	var histStdev:Number;
	var stdev_mean:Number;
	var stageW:Number;
	var stageH:Number;
	
	function ReadOutView(model:Object){
		this.model = model;
		this.model.registerReadOutView(this);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.readOutPanel = _root.attachMovie("readOutPanel", "readOutPanel_mc",Util.getNextDepth());
		Util.setXYPosition(this.readOutPanel, 0.1*this.stageW, 0.05*this.stageH);
	}
	
	function update():Void{
		this.N = this.model.trialNbr;
		this.trueMean = this.model.trueMean;
		this.mean = this.model.bigView.histMean;
		this.mean = Math.round(this.mean*1000)/1000;
		this.sigma = this.model.sigma;
		this.stdev_mean = this.model.stdev_mean;
		//trace("stdev_mean: "+stdev_mean);
		//trace("readOutView.sigma"+sigma);
		this.readOutPanel.n_txt.text = this.N;
		
		
		if(this.model.controlPanelView.showState == 1){  //if Show Ball
			this.histStdev = this.model.bigView.histStdev;
			this.histStdev = Math.round(100*this.histStdev)/100;
			this.readOutPanel.stdev_txt.text = this.histStdev;
		}else{	//if show path or show none
			this.readOutPanel.stdev_txt.text = Math.round(100*this.model.stdev)/100;
		}
		this.readOutPanel.stdevMean_txt.text = Math.round(1000*this.model.stdev_mean)/1000;
		
		if(this.N != 0){
			this.readOutPanel.mean_txt.text = this.mean;
		}else{
			this.readOutPanel.mean_txt.text = "?";
		}
		if(this.N == 0 || this.N == 1){
			this.readOutPanel.stdev_txt.text = "?";
			this.readOutPanel.stdevMean_txt.text = "?";
		}
		//function displayMean():Void{
		//}
		
		this.readOutPanel.trueMean_txt.text = Math.round(100*this.trueMean)/100;
		this.readOutPanel.sigma_txt.text = Math.round(100*this.sigma)/100;
		
	}
}//end of class
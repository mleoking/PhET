class ObservableColorTransform extends Observable {
	private var ctx:Object;
	function ObservableColorTransform() {
		ctx = {rb:200, gb:120, bb:200 };
	}
	
	function addObserver(observer):Void{
//		trace( observer );
		super.addObserver(observer);
	}
	
	function getCtx():Object {
		return ctx;
	}
}
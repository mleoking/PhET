class Observable {
	private var observers:Array;
	function Observable() {
		this.observers = new Array();
	}
	public function addObserver(observer) {
		observers.push(observer);
	}
	public function removeObserver(observer) {
		for (var i = 0; i<observers.length; i++) {
			if (observers[i] == observer) {
				observers.splice(i, 1);
				break;
			}
		}
	}
	public function notifyObservers() {
		for (var i = 0; i<observers.length; i++) {
			observers[i].update();
		}
	}
}

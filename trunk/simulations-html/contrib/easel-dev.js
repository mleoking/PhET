/*
* EaselJS
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2011 gskinner.com, inc.
* 
* Distributed under the terms of the MIT license.
* http://www.opensource.org/licenses/mit-license.html
*
* This notice shall be included in all copies or substantial portions of the Software.
*/
/*
* UID
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/
// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* Global utility for generating sequential unique ID numbers.
* The UID class uses a static interface (ex. UID.get()) and should not be instantiated.
* @class UID
* @static
**/
var UID = function() {
	throw "UID cannot be instantiated";
}

	/**
	 * @property _nextID
	 * @type Number
	 * @protected
	 **/
	UID._nextID = 0;

	/**
	 * Returns the next unique id.
	 * @method get
	 * @return {Number} The next unique id
	 * @static
	 **/
	UID.get = function() {
		return UID._nextID++;
	}

createjs.UID = UID;
}());/*
* Ticker
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

// constructor:
/**
* The Ticker class uses a static interface (ex. Ticker.getPaused()) and should not be instantiated.
* Provides a centralized tick or heartbeat broadcast at a set interval. Listeners can subscribe
* to the tick event to be notified when a set time interval has elapsed.
* Note that the interval that the tick event is called is a target interval, and may be broadcast
* at a slower interval during times of high CPU load.
* @class Ticker
* @static
**/
var Ticker = function() {
	throw "Ticker cannot be instantiated.";
}

// public static properties:
	/**
	 * Indicates whether Ticker should use requestAnimationFrame if it is supported in the browser. If false, Ticker
	 * will use setTimeout. If you use RAF, it is recommended that you set the framerate to a divisor of 60 (ex. 15,
	 * 20, 30, 60).
	 * @property useRAF
	 * @static
	 * @type Boolean
	 **/
	Ticker.useRAF = null;
	
	/**
	 * Event broadcast  once each tick / interval. The interval is specified via the 
	 * .setInterval(ms) or setFPS methods.
	 * @event tick
	 * @param {Number} timeElapsed The time elapsed in milliseconds since the last tick event.
	*/	
	
// private static properties:

	
	/** 
	 * @property _listeners
	 * @type Array[Object]
	 * @protected 
	 **/
	Ticker._listeners = null;
	
	/** 
	 * @property _pauseable
	 * @type Array[Boolean]
	 * @protected 
	 **/
	Ticker._pauseable = null;
	
	/** 
	 * @property _paused
	 * @type Boolean
	 * @protected 
	 **/
	Ticker._paused = false;
	
	/** 
	 * @property _inited
	 * @type Boolean
	 * @protected 
	 **/
	Ticker._inited = false;
	
	/** 
	 * @property _startTime
	 * @type Number
	 * @protected 
	 **/
	Ticker._startTime = 0;
	
	/** 
	 * @property _pausedTime
	 * @type Number
	 * @protected 
	 **/
	Ticker._pausedTime=0;
	
	/** 
	 * Number of ticks that have passed
	 * @property _ticks
	 * @type Number
	 * @protected 
	 **/
	Ticker._ticks = 0;
	
	/**
	 * Number of ticks that have passed while Ticker has been paused
	 * @property _pausedTicks
	 * @type Number
	 * @protected 
	 **/
	Ticker._pausedTicks = 0;
	
	/** 
	 * @property _interval
	 * @type Number
	 * @protected 
	 **/
	Ticker._interval = 50; // READ-ONLY
	
	/** 
	 * @property _lastTime
	 * @type Number
	 * @protected 
	 **/
	Ticker._lastTime = 0;
	
	/** 
	 * @property _times
	 * @type Array[Number]
	 * @protected 
	 **/
	Ticker._times = null;
	
	/** 
	 * @property _tickTimes
	 * @type Array[Number]
	 * @protected 
	 **/
	Ticker._tickTimes = null;
	
	/** 
	 * @property _rafActive
	 * @type Boolean
	 * @protected 
	 **/
	Ticker._rafActive = false;
	
	/** 
	 * @property _timeoutID
	 * @type Number
	 * @protected 
	 **/
	Ticker._timeoutID = null;
	
	
// public static methods:
	/**
	 * Adds a listener for the tick event. The listener must be either an object exposing a .tick() method,
	 * or a function. The listener will be called once each tick / interval. The interval is specified via the 
	 * .setInterval(ms) method.
	 * The tick method or function is passed two parameters: the elapsed time between the 
	 * previous tick and the current one, and a boolean indicating whether Ticker is paused.
	 * @method addListener
	 * @static
	 * @param {Object} o The object or function to add as a listener.
	 * @param {Boolean} pauseable If false, the listener will continue to have tick called 
	 * even when Ticker is paused via Ticker.pause(). Default is true.
	 **/
	Ticker.addListener = function(o, pauseable) {
		if (o == null) { return; }
		if (!Ticker._inited) { Ticker.init(); }
		Ticker.removeListener(o);
		Ticker._pauseable[Ticker._listeners.length] = (pauseable == null) ? true : pauseable;
		Ticker._listeners.push(o);
	}
	
	/**
	 * Initializes or resets the timer, clearing all associated listeners and fps measuring data, starting the tick.
	 * This is called automatically when the first listener is added.
	 * @method init
	 * @static
	 **/
	Ticker.init = function() {
		Ticker._inited = true;
		Ticker._times = [];
		Ticker._tickTimes = [];
		Ticker._pauseable = [];
		Ticker._listeners = [];
		Ticker._times.push(Ticker._lastTime = Ticker._startTime = Ticker._getTime());
		Ticker.setInterval(Ticker._interval);
	}
	
	/**
	 * Removes the specified listener.
	 * @method removeListener
	 * @static
	 * @param {Object} o The object or function to remove from listening from the tick event.
	 **/
	Ticker.removeListener = function(o) {
		var listeners = Ticker._listeners;
		if (!listeners) { return; }
		var index = listeners.indexOf(o);
		if (index != -1) {
			listeners.splice(index, 1);
			Ticker._pauseable.splice(index, 1);
		}
	}
	
	/**
	 * Removes all listeners.
	 * @method removeAllListeners
	 * @static
	 **/
	Ticker.removeAllListeners = function() {
		Ticker._listeners = [];
		Ticker._pauseable = [];
	}
	
	/**
	 * Sets the target time (in milliseconds) between ticks. Default is 50 (20 FPS).
	 * Note actual time between ticks may be more than requested depending on CPU load.
	 * @method setInterval
	 * @static
	 * @param {Number} interval Time in milliseconds between ticks. Default value is 50.
	 **/
	Ticker.setInterval = function(interval) {
		Ticker._interval = interval;
		if (!Ticker._inited) { return; }
		Ticker._setupTick();
	}
	
	/**
	 * Returns the current target time between ticks, as set with setInterval.
	 * @method getInterval
	 * @static
	 * @return {Number} The current target interval in milliseconds between tick events.
	 **/
	Ticker.getInterval = function() {
		return Ticker._interval;
	}
	
	/**
	 * Sets the target frame rate in frames per second (FPS). For example, with an interval of 40, getFPS() will 
	 * return 25 (1000ms per second divided by 40 ms per tick = 25fps).
	 * @method setFPS
	 * @static
	 * @param {Number} value Target number of ticks broadcast per second.
	 **/	
	Ticker.setFPS = function(value) {
		Ticker.setInterval(1000/value);
	}
	
	/**
	 * Returns the target frame rate in frames per second (FPS). For example, with an 
	 * interval of 40, getFPS() will return 25 (1000ms per second divided by 40 ms per tick = 25fps).
	 * @method getFPS
	 * @static
	 * @return {Number} The current target number of frames / ticks broadcast per second.
	 **/
	Ticker.getFPS = function() {
		return 1000/Ticker._interval;
	}
	
	/**
	 * Returns the actual frames / ticks per second.
	 * @method getMeasuredFPS
	 * @static
	 * @param {Number} ticks Optional. The number of previous ticks over which to measure the actual 
	 * frames / ticks per second. Defaults to the number of ticks per second.
	 * @return {Number} The actual frames / ticks per second. Depending on performance, this may differ
	 * from the target frames per second.
	 **/
	Ticker.getMeasuredFPS = function(ticks) {
		if (Ticker._times.length < 2) { return -1; }
		
		// by default, calculate fps for the past 1 second:
		if (ticks == null) { ticks = Ticker.getFPS()|0; }
		ticks = Math.min(Ticker._times.length-1, ticks);
		return 1000/((Ticker._times[0]-Ticker._times[ticks])/ticks);
	}
	
	/**
	 * While Ticker is paused, pausable listeners are not ticked. See addListener for more information.
	 * @method setPaused
	 * @static
	 * @param {Boolean} value Indicates whether to pause (true) or unpause (false) Ticker.
	 **/
	Ticker.setPaused = function(value) {
		Ticker._paused = value;
	}
	
	/**
	 * Returns a boolean indicating whether Ticker is currently paused, as set with setPaused.
	 * @method getPaused
	 * @static
	 * @return {Boolean} Whether the Ticker is currently paused.
	 **/
	Ticker.getPaused = function() {
		return Ticker._paused;
	}
	
	/**
	 * Returns the number of milliseconds that have elapsed since the first tick event listener was added to
	 * Ticker. For example, you could use this in a time synchronized animation to determine the exact amount of 
	 * time that has elapsed.
	 * @method getTime
	 * @static
	 * @param {Boolean} pauseable Indicates whether to include time elapsed
	 * while Ticker was paused. If false only time elapsed while Ticker is not paused will be returned.
	 * If true, the value returned will be total time elapsed since the first tick event listener was added.
	 * @return {Number} Number of milliseconds that have elapsed since Ticker was begun.
	 **/
	Ticker.getTime = function(pauseable) {
		return Ticker._getTime() - Ticker._startTime - (pauseable ? Ticker._pausedTime : 0);
	}
	
	/**
	 * Returns the number of ticks that have been broadcast by Ticker.
	 * @method getTicks
	 * @static
	 * @param {Boolean} pauseable Indicates whether to include ticks that would have been broadcast
	 * while Ticker was paused. If false only tick events broadcast while Ticker is not paused will be returned.
	 * If true, tick events that would have been broadcast while Ticker was paused will be included in the return
	 * value. The default value is false.
	 * @return {Number} of ticks that have been broadcast.
	 **/
	Ticker.getTicks = function(pauseable) {
		return  Ticker._ticks - (pauseable ?Ticker._pausedTicks : 0);
	}
	
// private static methods:
	/**
	 * @method _handleAF
	 * @protected
	 **/
	Ticker._handleAF = function() {
		Ticker._rafActive = false;
		Ticker._setupTick();
		// run if enough time has elapsed, with a little bit of flexibility to be early, because RAF seems to run a little faster than 60hz:
		if (Ticker._getTime() - Ticker._lastTime >= (Ticker._interval-1)*0.97) {
			Ticker._tick();
		}
	}
	
	/**
	 * @method _handleTimeout
	 * @protected
	 **/
	Ticker._handleTimeout = function() {
		Ticker.timeoutID = null;
		Ticker._setupTick();
		Ticker._tick();
	}
	
	/**
	 * @method _setupTick
	 * @protected
	 **/
	Ticker._setupTick = function() {
		if (Ticker._rafActive || Ticker.timeoutID != null) { return; } // avoid duplicates
		if (Ticker.useRAF) {
			var f = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame;
			if (f) {
				f(Ticker._handleAF);
				Ticker._rafActive = true;
				return;
			}
		}
		Ticker.timeoutID = setTimeout(Ticker._handleTimeout, Ticker._interval);
	}
	
	/**
	 * @method _tick
	 * @protected
	 **/
	Ticker._tick = function() {
		var time = Ticker._getTime();
		Ticker._ticks++;
		
		var elapsedTime = time-Ticker._lastTime;
		
		var paused = Ticker._paused;
		
		if (paused) {
			Ticker._pausedTicks++;
			Ticker._pausedTime += elapsedTime;
		}
		Ticker._lastTime = time;
		
		var pauseable = Ticker._pauseable;
		var listeners = Ticker._listeners.slice();
		var l = listeners ? listeners.length : 0;
		for (var i=0; i<l; i++) {
			var listener = listeners[i];
			if (listener == null || (paused && pauseable[i])) { continue; }
			if (listener.tick) { listener.tick(elapsedTime, paused); }
			else if (listener instanceof Function) { listener(elapsedTime, paused); }
		}
		
		Ticker._tickTimes.unshift(Ticker._getTime()-time);
		while (Ticker._tickTimes.length > 100) { Ticker._tickTimes.pop(); }
		
		Ticker._times.unshift(time);
		while (Ticker._times.length > 100) { Ticker._times.pop(); }
	}
	
	/**
	 * @method _getTime
	 * @protected
	 **/
	var now = window.performance && (performance.now || performance.mozNow || performance.msNow || performance.oNow || performance.webkitNow);
	Ticker._getTime = function() {
		return (now&&now.call(performance))||(new Date().getTime());
	}

createjs.Ticker = Ticker;
}());
/*
* EventDispatcher
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
 * Provides methods for managing prioritized queues of event listeners and dispatching events. You can either extend
 * this class or mix its methods into an existing prototype or instance by using EventDispatcher.initialize(target).
 * @class EventDispatcher
 * @constructor
 **/
var EventDispatcher = function() {
  this.initialize();
};
var p = EventDispatcher.prototype;


	/**
	 * Static initializer to mix in EventDispatcher methods.
	 * @method initialize
	 * @static
	 * @param {Object} target The target object to inject EventDispatcher methods into. This can be an instance or a prototype.
	 **/
	EventDispatcher.initialize = function(target) {
		target.addEventListener = p.addEventListener;
		target.removeEventListener = p.removeEventListener;
		target.removeAllEventListeners = p.removeAllEventListeners;
		target.hasEventListener = p.hasEventListener;
		target.dispatchEvent = p.dispatchEvent;
	};

	/**
	* Inner class used by the EventDispatcher class.
	 * @protected
	 * @class Listener
	 * @for EventDispatcher
	 * @constructor
	 **/
	function Listener(f, scope, priority) {
		this.f = f;
		this.scope = scope;
		this.priority = priority;
	};

// private properties:
	/**
	 * @protected
	 * @property _listeners
	 * @type Object
	 **/
	p._listeners = null;

// constructor:
	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function() {};

// public methods:
	/**
	 * Adds the specified event listener.
	 * @method addEventListener
	 * @param {String} type The string type of the event.
	 * @param {Function} callback The function that will be called when this event is dispatched.
	 * @param {Object} scope Optional. The scope/context that the callback will be called in (ie. the "this").
	 * @param {Number} priority Optional. Listeners with a higher priority will be called before those with lower priority. Default is 0.
	 **/
	p.addEventListener = function(type, callback, scope, priority) {
		priority = priority || 0;
		var o = new Listener(callback, scope || this, priority);
		var listeners = this._listeners;
		if (!listeners) { listeners = this._listeners = {}; }
		var arr = listeners[type];
		if (!arr) { arr = listeners[type] = [o]; return; }
		this.removeEventListener(type, callback, scope);
		for (var i=0,l=arr.length; i<l && arr[i].priority >= priority; i++) {}
		arr.splice(i, 0, o);
	};
	
	/**
	 * Removes the specified event listener.
	 * @method removeEventListener
	 * @param {String} type The string type of the event.
	 * @param {Function} callback The listener function.
	 * @param {Object} scope Optional. The listener scope. This must be the same as for addEventListener.
	 **/
	p.removeEventListener = function(type, callback, scope) {
		var listeners = this._listeners;
		if (!listeners) { return; }
		var arr = listeners[type];
		if (!arr) { return; }
		scope = scope || this;
		for (var i=0,l=arr.length; i<l; i++) {
			var o = arr[i];
			if (o.f == callback && o.scope == scope) {
				if (l==1) { delete(listeners[type]); } // allows for faster checks.
				else { arr.splice(i,1); }
				break;
			}
		}
	};
	
	/**
	 * Removes all listeners for the specified type, or all listeners of all types.
	 * @method removeEventListener
	 * @param {String} type The string type of the event. If omitted, all listeners for all types will be removed.
	 **/
	p.removeAllEventListeners = function(type) {
		if (!type) { this._listeners = null; }
		else if (this._listeners) { delete(this._listeners[type]); }
	};
	
	/**
	 * Dispatches the specified event.
	 * @method dispatchEvent
	 * @param {Object | String} eventObj An object with a "type" property, or a string type. If a string is used, dispatchEvent will contstruct a generic event object with "type" and "params" properties.
	 * @param {Function} callback Optional. If specified, dispatchEvent will call the specified function in this scope, with the parameters specified in params.
	 * @param {Array} params Optional. An array of parameters to call the handler with specified by handlerName. This will also be added as a "params" property on the constructed event object if a string is passed to eventObj.
	 * @return {Boolean} Returns true if any listener returned true.
	 **/
	p.dispatchEvent = function(eventObj, callback, params) {
		// NOTE: DisplayObject._tick inlines some of this logic. Update both if this changes.
		callback&&callback.apply(this, params);
		
		var ret = false;
		var listeners = this._listeners;
		if (eventObj && listeners) {
			if (typeof eventObj == "string") { eventObj = {type:eventObj, params:params}; }
			//eventObj.target = this;
			var arr = listeners[eventObj.type];
			if (!arr) { return ret; }
			for (var i=0,l=arr.length; i<l; i++) {
				var o = arr[i];
				ret = ret||o.f.apply(o.scope, [eventObj]);
			}
		}
		return !!ret;
	};
	
	/**
	 * Indicates whether there is at least one listener for the specified event type or a defined callback.
	 * @method hasEventListener
	 * @param {String} type The string type of the event.
	 * @param {Function} callback Optional. The callback function related to this event.
	 * @return {Boolean} Returns true if there is at least one listener for the specified event.
	 **/
	p.hasEventListener = function(type, callback) {
		return !!(callback || (this._listeners && this._listeners[type]));
	};

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[EventDispatcher]";
	};

createjs.EventDispatcher = EventDispatcher;
}());/*
* MouseEvent
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
 * This is passed as the parameter to onPress, onMouseMove, onMouseUp, onMouseDown, onMouseOver, onMouseOut and onClick
 * handlers on DisplayObject instances.
 * @class MouseEvent
 * @uses EventDispatcher
 * @constructor
 * @param {String} type The event type.
 * @param {Number} stageX The normalized x position relative to the stage.
 * @param {Number} stageY The normalized y position relative to the stage.
 * @param {DisplayObject} target The display object this event relates to.
 * @param {MouseEvent} nativeEvent The native DOM event related to this mouse event.
 * @param {Number} pointerID The unique id for the pointer.
 * @param {Boolean} primary Indicates whether this is the primary pointer in a multitouch environment.
 * @param {Number} rawX The raw x position relative to the stage.
 * @param {Number} rawY The raw y position relative to the stage.
 **/
var MouseEvent = function(type, stageX, stageY, target, nativeEvent, pointerID, primary, rawX, rawY) {
  this.initialize(type, stageX, stageY, target, nativeEvent, pointerID, primary, rawX, rawY);
}
var p = MouseEvent.prototype;

// public properties:

	/**
	 * The normalized x position on the stage. This will always be within the range 0 to stage width.
	 * @property stageX
	 * @type Number
	*/
	p.stageX = 0;

	/**
	 * The normalized y position on the stage. This will always be within the range 0 to stage height.
	 * @property stageY
	 * @type Number
	 **/
	p.stageY = 0;
	
	/**
	 * The raw x position relative to the stage. Normally this will be the same as the stageX value, unless
	 * stage.mouseMoveOutside is true and the pointer is outside of the stage bounds.
	 * @property rawX
	 * @type Number
	*/
	p.rawX = 0;

	/**
	 * The raw y position relative to the stage. Normally this will be the same as the stageY value, unless
	 * stage.mouseMoveOutside is true and the pointer is outside of the stage bounds.
	 * @property rawY
	 * @type Number
	*/
	p.rawY = 0;

	/**
	 * The type of mouse event. This will be the same as the handler it maps to (onPress,
	 * onMouseDown, onMouseUp, onMouseMove, or onClick).
	 * @property type
	 * @type String
	 **/
	p.type = null;

	/**
	 * The native MouseEvent generated by the browser. The properties and API for this
	 * event may differ between browsers. This property will be null if the
	 * EaselJS property was not directly generated from a native MouseEvent.
	 * @property nativeEvent
	 * @type MouseEvent
	 * @default null
	 **/
	p.nativeEvent = null;

	/**
	 * For events of type "onPress" only you can assign a handler to the onMouseMove
	 * property. This handler will be called every time the mouse is moved until the mouse is released.
	 * This is useful for operations such as drag and drop.
	 * @event onMouseMove
	 * @param {MouseEvent} event A MouseEvent instance with information about the current mouse event.
	 **/
	p.onMouseMove = null;

	/**
	 * For events of type "onPress" only you can assign a handler to the onMouseUp
	 * property. This handler will be called every time the mouse is moved until the mouse is released.
	 * This is useful for operations such as drag and drop.
	 * @event onMouseUp
	 * @param {MouseEvent} event A MouseEvent instance with information about the current mouse event.
	*/
	p.onMouseUp = null;

	/**
	 * The display object this event relates to.
	 * @property target
	 * @type DisplayObject
	 * @default null
	*/
	p.target = null;

	/**
	 * The unique id for the pointer (touch point or cursor). This will be either -1 for the mouse, or the system
	 * supplied id value.
	 * @property pointerID
	 * @type {Number}
	 */
	p.pointerID = 0;

	/**
	 * Indicates whether this is the primary pointer in a multitouch environment. This will always be true for the mouse.
	 * For touch pointers, the first pointer in the current stack will be considered the primary pointer.
	 * @property primaryPointer
	 * @type {Boolean}
	 */
	p.primary = false;
	
// mix-ins:
	// EventDispatcher methods:
	p.addEventListener = null;
	p.removeEventListener = null;
	p.removeAllEventListeners = null;
	p.dispatchEvent = null;
	p.hasEventListener = null;
	p._listeners = null;
	
	// we only use EventDispatcher if it's available:
	createjs.EventDispatcher && createjs.EventDispatcher.initialize(p); // inject EventDispatcher methods.

// constructor:
	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function(type, stageX, stageY, target, nativeEvent, pointerID, primary, rawX, rawY) {
		this.type = type;
		this.stageX = stageX;
		this.stageY = stageY;
		this.target = target;
		this.nativeEvent = nativeEvent;
		this.pointerID = pointerID;
		this.primary = primary;
		this.rawX = (rawX==null)?stageX:rawX;
		this.rawY = (rawY==null)?stageY:rawY;
	}

// public methods:
	/**
	 * Returns a clone of the MouseEvent instance.
	 * @method clone
	 * @return {MouseEvent} a clone of the MouseEvent instance.
	 **/
	p.clone = function() {
		return new MouseEvent(this.type, this.stageX, this.stageY, this.target, this.nativeEvent, this.pointerID, this.primary, this.rawX, this.rawY);
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[MouseEvent (type="+this.type+" stageX="+this.stageX+" stageY="+this.stageY+")]";
	}

createjs.MouseEvent = MouseEvent;
}());/*
* Matrix2D
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* Represents an affine transformation matrix, and provides tools for constructing and concatenating matrixes.
* @class Matrix2D
* @constructor
* @param {Number} a Specifies the a property for the new matrix.
* @param {Number} b Specifies the b property for the new matrix.
* @param {Number} c Specifies the c property for the new matrix.
* @param {Number} d Specifies the d property for the new matrix.
* @param {Number} tx Specifies the tx property for the new matrix.
* @param {Number} ty Specifies the ty property for the new matrix.
**/
var Matrix2D = function(a, b, c, d, tx, ty) {
  this.initialize(a, b, c, d, tx, ty);
}
var p = Matrix2D.prototype;

// static public properties:

	/**
	 * An identity matrix, representing a null transformation. Read-only.
	 * @property identity
	 * @static
	 * @type Matrix2D
	 **/
	Matrix2D.identity = null; // set at bottom of class definition.

	/**
	 * Multiplier for converting degrees to radians. Used internally by Matrix2D. Read-only.
	 * @property DEG_TO_RAD
	 * @static
	 * @final
	 * @type Number
	 **/
	Matrix2D.DEG_TO_RAD = Math.PI/180;


// public properties:
	/**
	 * Position (0, 0) in a 3x3 affine transformation matrix.
	 * @property a
	 * @type Number
	 **/
	p.a = 1;

	/**
	 * Position (0, 1) in a 3x3 affine transformation matrix.
	 * @property b
	 * @type Number
	 **/
	p.b = 0;

	/**
	 * Position (1, 0) in a 3x3 affine transformation matrix.
	 * @property c
	 * @type Number
	 **/
	p.c = 0;

	/**
	 * Position (1, 1) in a 3x3 affine transformation matrix.
	 * @property d
	 * @type Number
	 **/
	p.d = 1;

	/**
	 * Position (2, 0) in a 3x3 affine transformation matrix.
	 * @property atx
	 * @type Number
	 **/
	p.tx = 0;

	/**
	 * Position (2, 1) in a 3x3 affine transformation matrix.
	 * @property ty
	 * @type Number
	 **/
	p.ty = 0;

	/**
	 * Property representing the alpha that will be applied to a display object. This is not part of matrix
	 * operations, but is used for operations like getConcatenatedMatrix to provide concatenated alpha values.
	 * @property alpha
	 * @type Number
	 **/
	p.alpha = 1;

	/**
	 * Property representing the shadow that will be applied to a display object. This is not part of matrix
	 * operations, but is used for operations like getConcatenatedMatrix to provide concatenated shadow values.
	 * @property shadow
	 * @type Shadow
	 **/
	p.shadow  = null;

	/**
	 * Property representing the compositeOperation that will be applied to a display object. This is not part of
	 * matrix operations, but is used for operations like getConcatenatedMatrix to provide concatenated
	 * compositeOperation values. You can find a list of valid composite operations at:
	 * <a href="https://developer.mozilla.org/en/Canvas_tutorial/Compositing">https://developer.mozilla.org/en/Canvas_tutorial/Compositing</a>
	 * @property compositeOperation
	 * @type String
	 **/
	p.compositeOperation = null;

// constructor:
	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	*/
	p.initialize = function(a, b, c, d, tx, ty) {
		if (a != null) { this.a = a; }
		this.b = b || 0;
		this.c = c || 0;
		if (d != null) { this.d = d; }
		this.tx = tx || 0;
		this.ty = ty || 0;
		return this;
	}

// public methods:
	/**
	 * Concatenates the specified matrix properties with this matrix. All parameters are required.
	 * @method prepend
	 * @param {Number} a
	 * @param {Number} b
	 * @param {Number} c
	 * @param {Number} d
	 * @param {Number} tx
	 * @param {Number} ty
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.prepend = function(a, b, c, d, tx, ty) {
		var tx1 = this.tx;
		if (a != 1 || b != 0 || c != 0 || d != 1) {
			var a1 = this.a;
			var c1 = this.c;
			this.a  = a1*a+this.b*c;
			this.b  = a1*b+this.b*d;
			this.c  = c1*a+this.d*c;
			this.d  = c1*b+this.d*d;
		}
		this.tx = tx1*a+this.ty*c+tx;
		this.ty = tx1*b+this.ty*d+ty;
		return this;
	}

	/**
	 * Appends the specified matrix properties with this matrix. All parameters are required.
	 * @method append
	 * @param {Number} a
	 * @param {Number} b
	 * @param {Number} c
	 * @param {Number} d
	 * @param {Number} tx
	 * @param {Number} ty
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.append = function(a, b, c, d, tx, ty) {
		var a1 = this.a;
		var b1 = this.b;
		var c1 = this.c;
		var d1 = this.d;

		this.a  = a*a1+b*c1;
		this.b  = a*b1+b*d1;
		this.c  = c*a1+d*c1;
		this.d  = c*b1+d*d1;
		this.tx = tx*a1+ty*c1+this.tx;
		this.ty = tx*b1+ty*d1+this.ty;
		return this;
	}

	/**
	 * Prepends the specified matrix with this matrix.
	 * @method prependMatrix
	 * @param {Matrix2D} matrix
	 **/
	p.prependMatrix = function(matrix) {
		this.prepend(matrix.a, matrix.b, matrix.c, matrix.d, matrix.tx, matrix.ty);
		this.prependProperties(matrix.alpha, matrix.shadow,  matrix.compositeOperation);
		return this;
	}

	/**
	 * Appends the specified matrix with this matrix.
	 * @method appendMatrix
	 * @param {Matrix2D} matrix
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.appendMatrix = function(matrix) {
		this.append(matrix.a, matrix.b, matrix.c, matrix.d, matrix.tx, matrix.ty);
		this.appendProperties(matrix.alpha, matrix.shadow,  matrix.compositeOperation);
		return this;
	}

	/**
	 * Generates matrix properties from the specified display object transform properties, and prepends them with this matrix.
	 * For example, you can use this to generate a matrix from a display object: var mtx = new Matrix2D();
	 * mtx.prependTransform(o.x, o.y, o.scaleX, o.scaleY, o.rotation);
	 * @method prependTransform
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} scaleX
	 * @param {Number} scaleY
	 * @param {Number} rotation
	 * @param {Number} skewX
	 * @param {Number} skewY
	 * @param {Number} regX Optional.
	 * @param {Number} regY Optional.
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.prependTransform = function(x, y, scaleX, scaleY, rotation, skewX, skewY, regX, regY) {
		if (rotation%360) {
			var r = rotation*Matrix2D.DEG_TO_RAD;
			var cos = Math.cos(r);
			var sin = Math.sin(r);
		} else {
			cos = 1;
			sin = 0;
		}

		if (regX || regY) {
			// append the registration offset:
			this.tx -= regX; this.ty -= regY;
		}
		if (skewX || skewY) {
			// TODO: can this be combined into a single prepend operation?
			skewX *= Matrix2D.DEG_TO_RAD;
			skewY *= Matrix2D.DEG_TO_RAD;
			this.prepend(cos*scaleX, sin*scaleX, -sin*scaleY, cos*scaleY, 0, 0);
			this.prepend(Math.cos(skewY), Math.sin(skewY), -Math.sin(skewX), Math.cos(skewX), x, y);
		} else {
			this.prepend(cos*scaleX, sin*scaleX, -sin*scaleY, cos*scaleY, x, y);
		}
		return this;
	}

	/**
	 * Generates matrix properties from the specified display object transform properties, and appends them with this matrix.
	 * For example, you can use this to generate a matrix from a display object: var mtx = new Matrix2D();
	 * mtx.appendTransform(o.x, o.y, o.scaleX, o.scaleY, o.rotation);
	 * @method appendTransform
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} scaleX
	 * @param {Number} scaleY
	 * @param {Number} rotation
	 * @param {Number} skewX
	 * @param {Number} skewY
	 * @param {Number} regX Optional.
	 * @param {Number} regY Optional.
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.appendTransform = function(x, y, scaleX, scaleY, rotation, skewX, skewY, regX, regY) {
		if (rotation%360) {
			var r = rotation*Matrix2D.DEG_TO_RAD;
			var cos = Math.cos(r);
			var sin = Math.sin(r);
		} else {
			cos = 1;
			sin = 0;
		}

		if (skewX || skewY) {
			// TODO: can this be combined into a single append?
			skewX *= Matrix2D.DEG_TO_RAD;
			skewY *= Matrix2D.DEG_TO_RAD;
			this.append(Math.cos(skewY), Math.sin(skewY), -Math.sin(skewX), Math.cos(skewX), x, y);
			this.append(cos*scaleX, sin*scaleX, -sin*scaleY, cos*scaleY, 0, 0);
		} else {
			this.append(cos*scaleX, sin*scaleX, -sin*scaleY, cos*scaleY, x, y);
		}

		if (regX || regY) {
			// prepend the registration offset:
			this.tx -= regX*this.a+regY*this.c; 
			this.ty -= regX*this.b+regY*this.d;
		}
		return this;
	}

	/**
	 * Applies a rotation transformation to the matrix.
	 * @method rotate
	 * @param {Number} angle The angle in degrees.
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.rotate = function(angle) {
		var cos = Math.cos(angle);
		var sin = Math.sin(angle);

		var a1 = this.a;
		var c1 = this.c;
		var tx1 = this.tx;

		this.a = a1*cos-this.b*sin;
		this.b = a1*sin+this.b*cos;
		this.c = c1*cos-this.d*sin;
		this.d = c1*sin+this.d*cos;
		this.tx = tx1*cos-this.ty*sin;
		this.ty = tx1*sin+this.ty*cos;
		return this;
	}

	/**
	 * Applies a skew transformation to the matrix.
	 * @method skew
	 * @param {Number} skewX The amount to skew horizontally in degrees.
	 * @param {Number} skewY The amount to skew vertically in degrees.
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	*/
	p.skew = function(skewX, skewY) {
		skewX = skewX*Matrix2D.DEG_TO_RAD;
		skewY = skewY*Matrix2D.DEG_TO_RAD;
		this.append(Math.cos(skewY), Math.sin(skewY), -Math.sin(skewX), Math.cos(skewX), 0, 0);
		return this;
	}

	/**
	 * Applies a scale transformation to the matrix.
	 * @method scale
	 * @param {Number} x
	 * @param {Number} y
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.scale = function(x, y) {
		this.a *= x;
		this.d *= y;
		this.tx *= x;
		this.ty *= y;
		return this;
	}

	/**
	 * Translates the matrix on the x and y axes.
	 * @method translate
	 * @param {Number} x
	 * @param {Number} y
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.translate = function(x, y) {
		this.tx += x;
		this.ty += y;
		return this;
	}

	/**
	 * Sets the properties of the matrix to those of an identity matrix (one that applies a null transformation).
	 * @method identity
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.identity = function() {
		this.alpha = this.a = this.d = 1;
		this.b = this.c = this.tx = this.ty = 0;
		this.shadow = this.compositeOperation = null;
		return this;
	}

	/**
	 * Inverts the matrix, causing it to perform the opposite transformation.
	 * @method invert
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	 **/
	p.invert = function() {
		var a1 = this.a;
		var b1 = this.b;
		var c1 = this.c;
		var d1 = this.d;
		var tx1 = this.tx;
		var n = a1*d1-b1*c1;

		this.a = d1/n;
		this.b = -b1/n;
		this.c = -c1/n;
		this.d = a1/n;
		this.tx = (c1*this.ty-d1*tx1)/n;
		this.ty = -(a1*this.ty-b1*tx1)/n;
		return this;
	}

	/**
	 * Returns true if the matrix is an identity matrix.
	 * @method isIdentity
	 * @return {Boolean}
	 **/
	p.isIdentity = function() {
		return this.tx == 0 && this.ty == 0 && this.a == 1 && this.b == 0 && this.c == 0 && this.d == 1;
	}

	/**
	 * Decomposes the matrix into transform properties (x, y, scaleX, scaleY, and rotation). Note that this these values
	 * may not match the transform properties you used to generate the matrix, though they will produce the same visual
	 * results.
	 * @method decompose
	 * @param {Object} target The object to apply the transform properties to. If null, then a new object will be returned.
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	*/
	p.decompose = function(target) {
		// TODO: it would be nice to be able to solve for whether the matrix can be decomposed into only scale/rotation
		// even when scale is negative
		if (target == null) { target = {}; }
		target.x = this.tx;
		target.y = this.ty;
		target.scaleX = Math.sqrt(this.a * this.a + this.b * this.b);
		target.scaleY = Math.sqrt(this.c * this.c + this.d * this.d);

		var skewX = Math.atan2(-this.c, this.d);
		var skewY = Math.atan2(this.b, this.a);

		if (skewX == skewY) {
			target.rotation = skewY/Matrix2D.DEG_TO_RAD;
			if (this.a < 0 && this.d >= 0) {
				target.rotation += (target.rotation <= 0) ? 180 : -180;
			}
			target.skewX = target.skewY = 0;
		} else {
			target.skewX = skewX/Matrix2D.DEG_TO_RAD;
			target.skewY = skewY/Matrix2D.DEG_TO_RAD;
		}
		return target;
	}

	/**
	 * Reinitializes all matrix properties to those specified.
	 * @method appendProperties
	 * @param {Number} a
	 * @param {Number} b
	 * @param {Number} c
	 * @param {Number} d
	 * @param {Number} tx
	 * @param {Number} ty
	 * @param {Number} alpha desired alpha value
	 * @param {Shadow} shadow desired shadow value
	 * @param {String} compositeOperation desired composite operation value
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	*/
	p.reinitialize = function(a,b,c,d,tx,ty,alpha,shadow,compositeOperation) {
		this.initialize(a,b,c,d,tx,ty);
		this.alpha = alpha || 1;
		this.shadow = shadow;
		this.compositeOperation = compositeOperation;
		return this;
	}

	/**
	 * Appends the specified visual properties to the current matrix.
	 * @method appendProperties
	 * @param {Number} alpha desired alpha value
	 * @param {Shadow} shadow desired shadow value
	 * @param {String} compositeOperation desired composite operation value
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	*/
	p.appendProperties = function(alpha, shadow, compositeOperation) {
		this.alpha *= alpha;
		this.shadow = shadow || this.shadow;
		this.compositeOperation = compositeOperation || this.compositeOperation;
		return this;
	}

	/**
	 * Prepends the specified visual properties to the current matrix.
	 * @method prependProperties
	 * @param {Number} alpha desired alpha value
	 * @param {Shadow} shadow desired shadow value
	 * @param {String} compositeOperation desired composite operation value
	 * @return {Matrix2D} This matrix. Useful for chaining method calls.
	*/
	p.prependProperties = function(alpha, shadow, compositeOperation) {
		this.alpha *= alpha;
		this.shadow = this.shadow || shadow;
		this.compositeOperation = this.compositeOperation || compositeOperation;
		return this;
	}

	/**
	 * Returns a clone of the Matrix2D instance.
	 * @method clone
	 * @return {Matrix2D} a clone of the Matrix2D instance.
	 **/
	p.clone = function() {
		var mtx = new Matrix2D(this.a, this.b, this.c, this.d, this.tx, this.ty);
		mtx.shadow = this.shadow;
		mtx.alpha = this.alpha;
		mtx.compositeOperation = this.compositeOperation;
		return mtx;
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Matrix2D (a="+this.a+" b="+this.b+" c="+this.c+" d="+this.d+" tx="+this.tx+" ty="+this.ty+")]";
	}

	// this has to be populated after the class is defined:
	Matrix2D.identity = new Matrix2D(1, 0, 0, 1, 0, 0);

createjs.Matrix2D = Matrix2D;
}());/*
* Point
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* Represents a point on a 2 dimensional x / y coordinate system.
* @class Point
* @constructor
* @param {Number} x X position. Default is 0.
* @param {Number} y Y position. Default is 0.
**/
var Point = function(x, y) {
  this.initialize(x, y);
}
var p = Point.prototype;
	
// public properties:

	/** 
	 * X position. 
	 * @property x
	 * @type Number
	 **/
	p.x = 0;
	
	/** 
	 * Y position. 
	 * @property y
	 * @type Number
	 **/
	p.y = 0;
	
// constructor:
	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function(x, y) {
		this.x = (x == null ? 0 : x);
		this.y = (y == null ? 0 : y);
	}
	
// public methods:
	/**
	 * Returns a clone of the Point instance.
	 * @method clone
	 * @return {Point} a clone of the Point instance.
	 **/
	p.clone = function() {
		return new Point(this.x, this.y);
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Point (x="+this.x+" y="+this.y+")]";
	}
	
createjs.Point = Point;
}());/*
* Rectangle
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* Represents a rectangle as defined by the points (x, y) and (x+width, y+height).
* @class Rectangle
* @constructor
* @param {Number} x X position. Default is 0.
* @param {Number} y Y position. Default is 0.
* @param {Number} width Width. Default is 0.
* @param {Number} height Height. Default is 0.
**/
var Rectangle = function(x, y, width, height) {
  this.initialize(x, y, width, height);
}
var p = Rectangle.prototype;
	
// public properties:
	/** 
	 * X position. 
	 * @property x
	 * @type Number
	 **/
	p.x = 0;
	
	/** 
	 * Y position. 
	 * @property y
	 * @type Number
	 **/
	p.y = 0;
	
	/** 
	 * Width.
	 * @property width
	 * @type Number
	 **/
	p.width = 0;
	
	/** 
	 * Height.
	 * @property height
	 * @type Number
	 **/
	p.height = 0;
	
// constructor:
	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function(x, y, width, height) {
		this.x = (x == null ? 0 : x);
		this.y = (y == null ? 0 : y);
		this.width = (width == null ? 0 : width);
		this.height = (height == null ? 0 : height);
	}
	
// public methods:
	/**
	 * Returns a clone of the Rectangle instance.
	 * @method clone
	 * @return {Rectangle} a clone of the Rectangle instance.
	 **/
	p.clone = function() {
		return new Rectangle(this.x, this.y, this.width, this.height);
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Rectangle (x="+this.x+" y="+this.y+" width="+this.width+" height="+this.height+")]";
	}
	
createjs.Rectangle = Rectangle;
}());/*
* Shadow
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* Encapsulates the properties required to define a shadow to apply to a DisplayObject via it's .shadow property.
* @class Shadow
* @constructor
* @param {String} color The color of the shadow.
* @param {Number} offsetX The x offset of the shadow.
* @param {Number} offsetY The y offset of the shadow.
* @param {Number} blur The size of the blurring effect.
**/
var Shadow = function(color, offsetX, offsetY, blur) {
  this.initialize(color, offsetX, offsetY, blur);
}
var p = Shadow.prototype;
	
// static public properties:
	/**
	 * An identity shadow object (all properties are set to 0). Read-only.
	 * @property identity
	 * @type Shadow
	 * @static
	 * @final
	 **/
	Shadow.identity = null; // set at bottom of class definition.
	
// public properties:
	/** The color of the shadow.
	 * property color
	 * @type String
	 * @default null
	*/
	p.color = null;
	
	/** The x offset of the shadow.
	 * property offsetX
	 * @type Number
	 * @default 0
	*/
	p.offsetX = 0;
	
	/** The y offset of the shadow.
	 * property offsetY
	 * @type Number
	 * @default 0
	*/
	p.offsetY = 0;
	
	/** The blur of the shadow.
	 * property blur
	 * @type Number
	 * @default 0
	*/
	p.blur = 0;
	
// constructor:
	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	 * @param {String} color The color of the shadow.
	 * @param {Number} offsetX The x offset of the shadow.
	 * @param {Number} offsetY The y offset of the shadow.
	 * @param {Number} blur The size of the blurring effect.
	 **/
	p.initialize = function(color, offsetX, offsetY, blur) {
		this.color = color;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.blur = blur;
	}
	
// public methods:
	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Shadow]";
	}
	
	
	/**
	 * Returns a clone of this Shadow instance.
	 * @method clone
	 * @return {Shadow} A clone of the current Shadow instance.
	 **/
	p.clone = function() {
		return new Shadow(this.color, this.offsetX, this.offsetY, this.blur);
	}
	
	// this has to be populated after the class is defined:
	Shadow.identity = new Shadow("transparent", 0, 0, 0);
	
createjs.Shadow = Shadow;
}());/*
* SpriteSheet
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {
/**
 * Encapsulates the properties and methods associated with a sprite sheet. A sprite sheet is a series of images (usually animation frames) combined
 * into a larger image (or images). For example, an animation consisting of 8 100x100 images could be combined into a 400x200
 * sprite sheet (4 frames across by 2 high).<br/><br/>
 * The data passed to the SpriteSheet constructor defines three critical pieces of information:<OL>
 *    <LI> The image or images to use.</LI>
 *    <LI> The positions of individual image frames. This data can be represented in one of two ways:
 *    As a regular grid of sequential, equal-sized frames, or as individually defined, variable sized frames arranged in an irregular (non-sequential) fashion.</LI>
 *    <LI> Likewise, animations can be represented in two ways: As a series of sequential frames, defined by a start and end frame [0,3], or as a list of frames [0,1,2,3].
 * </OL>
 * The easiest way to understand the data format is to see an example:
 * <pre><code>data = {
&nbsp;
// DEFINING IMAGES:
&#9;// list of images or image URIs to use. SpriteSheet can handle preloading.
&#9;// the order dictates their index value for frame definition.
&#9;images: [image1, "path/to/image2.png"],
&nbsp;
// DEFINING FRAMES:
&nbsp;
&#9;// the simple way to define frames, only requires frame size because frames are consecutive:
&#9;// define frame width/height, and optionally the frame count and registration point x/y.
&#9;// if count is omitted, it will be calculated automatically based on image dimensions.
&#9;frames: {width:64, height:64, count:20, regX: 32, regY:64},
&nbsp;
&#9;// OR, the complex way that defines individual rects for frames.
&#9;// The 5th value is the image index per the list defined in "images" (defaults to 0).
&#9;frames: [
&#9;	// x, y, width, height, imageIndex, regX, regY
&#9;	[0,0,64,64,0,32,64],
&#9;	[64,0,96,64,0]
&#9;],
&nbsp;
// DEFINING ANIMATIONS:
&nbsp;
&#9;// simple animation definitions. Define a consecutive range of frames.
&#9;// also optionally define a "next" animation name for sequencing.
&#9;// setting next to false makes it pause when it reaches the end.
&#9;animations: {
&#9;	// start, end, next, frequency
&#9;	run: [0,8],
&#9;	jump: [9,12,"run",2],
&#9;	stand: [13]
&#9;}
&nbsp;
&#9;// the complex approach which specifies every frame in the animation by index.
&#9;animations: {
&#9;	run: {
&#9;		frames: [1,2,3,3,2,1]
&#9;	},
&#9;	jump: {
&#9;		frames: [1,4,5,6,1],
&#9;		next: "run",
&#9;		frequency: 2
&#9;	},
&#9;	stand: { frames: [7] }
&#9;}
&nbsp;
&#9;// the above two approaches can be combined, you can also use a single frame definition:
&#9;animations: {
&#9;	run: [0,8,true,2],
&#9;	jump: {
&#9;		frames: [8,9,10,9,8],
&#9;		next: "run",
&#9;		frequency: 2
&#9;	},
&#9;	stand:7
&#9;}
}</code></pre>
 * &nbsp;
 * For example, to define a simple sprite sheet, with a single image "sprites.jpg" arranged in a regular 50x50 grid
 * with two animations, "run" looping from frame 0-4 inclusive, and "jump" playing from frame 5-8 and sequencing back to run:
 * <pre><code>data = {
&#9;images: ["sprites.jpg"],
&#9;frames: {width:50, height:50},
&#9;animations: {run:[0,4], jump:[5,8,"run"]}
}</code></pre>
 
 * @class SpriteSheet
 * @constructor
 * @param data
 **/
var SpriteSheet = function(data) {
  this.initialize(data);
}
var p = SpriteSheet.prototype;

// public properties:
	/**
	 * Read-only property indicating whether all images are finished loading.
	 * @property complete
	 * @type Boolean
	 **/
	p.complete = true;
	
	
	/**
	 * The onComplete callback is called when all images are loaded. Note that this only fires if the images
	 * were not fully loaded when the sprite sheet was initialized. You should check the complete property 
	 * to prior to adding an onComplete handler. Ex.
	 * <pre><code>var sheet = new SpriteSheet(data);
	 * if (!sheet.complete) {
	 *  &nbsp; // not preloaded, listen for onComplete:
	 *  &nbsp; sheet.onComplete = handler;
	 * }</code></pre>
	 * 
	 * @event onComplete
	 **/
	p.onComplete = null;
	
// private properties:
	/**
	 * @property _animations
	 * @protected
	 **/
	p._animations = null;
	
	/**
	 * @property _frames
	 * @protected
	 **/
	p._frames = null;
	
	/**
	 * @property _images
	 * @protected
	 **/
	p._images = null;
	
	/**
	 * @property _data
	 * @protected
	 **/
	p._data = null;
	
	/**
	 * @property _loadCount
	 * @protected
	 **/
	p._loadCount = 0;
	
	// only used for simple frame defs:
	/**
	 * @property _frameHeight
	 * @protected
	 **/
	p._frameHeight = 0;
	
	/**
	 * @property _frameWidth
	 * @protected
	 **/
	p._frameWidth = 0;
	
	/**
	 * @property _numFrames
	 * @protected
	 **/
	p._numFrames = 0;
	
	/**
	 * @property _regX
	 * @protected
	 **/
	p._regX = 0;
	
	/**
	 * @property _regY
	 * @protected
	 **/
	p._regY = 0;

// constructor:
	/**
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function(data) {
		var i,l,o,a;
		if (data == null) { return; }
		
		// parse images:
		if (data.images && (l=data.images.length) > 0) {
			a = this._images = [];
			for (i=0; i<l; i++) {
				var img = data.images[i];
				if (typeof img == "string") {
					var src = img;
					img = new Image();
					img.src = src;
				}
				a.push(img);
				if (!img.getContext && !img.complete) {
					this._loadCount++;
					this.complete = false;
					(function(o) { img.onload = function() { o._handleImageLoad(); } })(this);
				}
			}
		}
		
		// parse frames:
		if (data.frames == null) { // nothing
		} else if (data.frames instanceof Array) {
			this._frames = [];
			a = data.frames;
			for (i=0,l=a.length;i<l;i++) {
				var arr = a[i];
				this._frames.push({image:this._images[arr[4]?arr[4]:0], rect:new createjs.Rectangle(arr[0],arr[1],arr[2],arr[3]), regX:arr[5]||0, regY:arr[6]||0 });
			}
		} else {
			o = data.frames;
			this._frameWidth = o.width;
			this._frameHeight = o.height;
			this._regX = o.regX||0;
			this._regY = o.regY||0;
			this._numFrames = o.count;
			if (this._loadCount == 0) { this._calculateFrames(); }
		}
		
		// parse animations:
		if ((o=data.animations) != null) {
			this._animations = [];
			this._data = {};
			var name;
			for (name in o) {
				var anim = {name:name};
				var obj = o[name];
				if (!isNaN(obj)) { // single frame
					a = anim.frames = [obj];
				} else if (obj instanceof Array) { // simple
					anim.frequency = obj[3];
					anim.next = obj[2];
					a = anim.frames = [];
					for (i=obj[0];i<=obj[1];i++) {
						a.push(i);
					}
				} else { // complex
					anim.frequency = obj.frequency;
					anim.next = obj.next;
					var frames = obj.frames;
					a = anim.frames = !isNaN(frames) ? [frames] : frames.slice(0);
				}
				anim.next = (a.length < 2 || anim.next == false) ? null : (anim.next == null || anim.next == true) ? name : anim.next;
				if (!anim.frequency) { anim.frequency = 1; }
				this._animations.push(name);
				this._data[name] = anim;
			}
		}
		
	}

// public methods:
	/**
	 * Returns the total number of frames in the specified animation, or in the whole sprite
	 * sheet if the animation param is omitted.
	 * @param {String} animation The name of the animation to get a frame count for.
	 * @return {Number} The number of frames in the animation, or in the entire sprite sheet if the animation param is omitted.
	*/
	p.getNumFrames = function(animation) {
		if (animation == null) {
			return this._frames ? this._frames.length : this._numFrames;
		} else {
			var data = this._data[animation];
			if (data == null) { return 0; }
			else { return data.frames.length; }
		}
	}
	
	/**
	 * Returns an array of all available animation names as strings.
	 * @method getAnimations
	 * @return {Array} an array of animation names available on this sprite sheet.
	 **/
	p.getAnimations = function() {
		return this._animations.slice(0);
	}
	
	/**
	 * Returns an object defining the specified animation. The returned object has a
	 * frames property containing an array of the frame id's in the animation, a frequency
	 * property indicating the advance frequency for this animation, a name property, 
	 * and a next property, which specifies the default next animation. If the animation
	 * loops, the name and next property will be the same.
	 * @method getAnimation
	 * @param {String} name The name of the animation to get.
	 * @return {Object} a generic object with frames, frequency, name, and next properties.
	 **/
	p.getAnimation = function(name) {
		return this._data[name];
	}
	
	/**
	 * Returns an object specifying the image and source rect of the specified frame. The returned object
	 * has an image property holding a reference to the image object in which the frame is found,
	 * and a rect property containing a Rectangle instance which defines the boundaries for the
	 * frame within that image.
	 * @method getFrame
	 * @param {Number} frameIndex The index of the frame.
	 * @return {Object} a generic object with image and rect properties. Returns null if the frame does not exist, or the image is not fully loaded.
	 **/
	p.getFrame = function(frameIndex) {
		var frame;
		if (this.complete && this._frames && (frame=this._frames[frameIndex])) { return frame; }
		return null;
	}
	
	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[SpriteSheet]";
	}

	/**
	 * Returns a clone of the SpriteSheet instance.
	 * @method clone
	 * @return {SpriteSheet} a clone of the SpriteSheet instance.
	 **/
	p.clone = function() {
		// TODO: there isn't really any reason to clone SpriteSheet instances, because they can be reused.
		var o = new SpriteSheet();
		o.complete = this.complete;
		o._animations = this._animations;
		o._frames = this._frames;
		o._images = this._images;
		o._data = this._data;
		o._frameHeight = this._frameHeight;
		o._frameWidth = this._frameWidth;
		o._numFrames = this._numFrames;
		o._loadCount = this._loadCount;
		return o;
	}
	
// private methods:
	/**
	 * @method _handleImageLoad
	 * @protected
	 **/
	p._handleImageLoad = function() {
		if (--this._loadCount == 0) {
			this._calculateFrames();
			this.complete = true;
			this.onComplete&&this.onComplete();
		}
	}
	
	/**
	 * @method _calculateFrames
	 * @protected
	 **/
	p._calculateFrames = function() {
		if (this._frames || this._frameWidth == 0) { return; }
		this._frames = [];
		var ttlFrames = 0;
		var fw = this._frameWidth;
		var fh = this._frameHeight;
		for (var i=0,imgs = this._images; i<imgs.length; i++) {
			var img = imgs[i];
			var cols = (img.width+1)/fw|0;
			var rows = (img.height+1)/fh|0;
			var ttl = this._numFrames>0 ? Math.min(this._numFrames-ttlFrames,cols*rows) : cols*rows;
			for (var j=0;j<ttl;j++) {
				this._frames.push({image:img, rect:new createjs.Rectangle(j%cols*fw,(j/cols|0)*fh,fw,fh), regX:this._regX, regY:this._regY });
			}
			ttlFrames += ttl;
		}
		this._numFrames = ttlFrames;
	}

createjs.SpriteSheet = SpriteSheet;
}());
/*
* Graphics
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

// used to create the instruction lists used in Graphics:


/**
* Inner class used by the Graphics class. Used to create the instruction lists used in Graphics:
* @class Command
* @protected
* @for Graphics
* @constructor
**/
function Command(f, params, path) {
	this.f = f;
	this.params = params;
	this.path = path==null ? true : path;
}

/**
* @method exec
* @protected
* @param {Object} scope
**/
Command.prototype.exec = function(scope) { this.f.apply(scope, this.params); }

/**
* The Graphics class exposes an easy to use API for generating vector drawing instructions and drawing them to a specified context.
* Note that you can use Graphics without any dependency on the Easel framework by calling draw() directly,
* or it can be used with the Shape object to draw vector graphics within the context of an Easel display list.<br/><br/>
* <pre><code>var g = new Graphics();
*	g.setStrokeStyle(1);
*	g.beginStroke(Graphics.getRGB(0,0,0));
*	g.beginFill(Graphics.getRGB(255,0,0));
*	g.drawCircle(0,0,3);
*
*	var s = new Shape(g);
*		s.x = 100;
*		s.y = 100;
*
*	stage.addChild(s);
*	stage.update();</code></pre><br />
* Note that all drawing methods in Graphics return the Graphics instance, so they can be chained together. For example, the following 
* line of code would generate the instructions to draw a rectangle with a red stroke and blue fill, then render it to the specified 
* context2D:<br />
* <pre><code>myGraphics.beginStroke("#F00").beginFill("#00F").drawRect(20, 20, 100, 50).draw(myContext2D);
* @class Graphics
* @constructor
* @for Graphics
**/
var Graphics = function() {
	this.initialize();
}
var p = Graphics.prototype;

// static public methods:
	
	
	/**
	 * Returns a CSS compatible color string based on the specified RGB numeric color values in the format 
	 * "rgba(255,255,255,1.0)", or if alpha is null then in the format "rgb(255,255,255)". For example,
	 * Graphics.getRGB(50, 100, 150, 0.5) will return "rgba(50,100,150,0.5)". It also supports passing a single hex color 
	 * value as the first param, and an optional alpha value as the second param. For example, Graphics.getRGB(0xFF00FF, 0.2)
	 * will return "rgba(255,0,255,0.2)".
	 * @method getRGB
	 * @static
	 * @param {Number} r The red component for the color, between 0 and 0xFF (255).
	 * @param {Number} g The green component for the color, between 0 and 0xFF (255).
	 * @param {Number} b The blue component for the color, between 0 and 0xFF (255).
	 * @param {Number} alpha Optional. The alpha component for the color where 0 is fully transparent and 1 is fully opaque.
	 * @return {String} A CSS compatible color string based on the specified RGB numeric color values in the format 
	 * "rgba(255,255,255,1.0)", or if alpha is null then in the format "rgb(255,255,255)".
	 **/
	Graphics.getRGB = function(r, g, b, alpha) {
		if (r != null && b == null) {
			alpha = g;
			b = r&0xFF;
			g = r>>8&0xFF;
			r = r>>16&0xFF;
		}
		if (alpha == null) {
			return "rgb("+r+","+g+","+b+")";
		} else {
			return "rgba("+r+","+g+","+b+","+alpha+")";
		}
	}
	
	/**
	 * Returns a CSS compatible color string based on the specified HSL numeric color values in the format "hsla(360,100,100,1.0)", 
	 * or if alpha is null then in the format "hsl(360,100,100)". For example, Graphics.getHSL(150, 100, 70) will return 
	 * "hsl(150,100,70)".
	 * @method getHSL
	 * @static
	 * @param {Number} hue The hue component for the color, between 0 and 360.
	 * @param {Number} saturation The saturation component for the color, between 0 and 100.
	 * @param {Number} lightness The lightness component for the color, between 0 and 100.
	 * @param {Number} alpha Optional. The alpha component for the color where 0 is fully transparent and 1 is fully opaque.
	 * @return {String} A CSS compatible color string based on the specified HSL numeric color values in the format 
	 * "hsla(360,100,100,1.0)", or if alpha is null then in the format "hsl(360,100,100)". For example, 
	 * Graphics.getHSL(150, 100, 70) will return "hsl(150,100,70)".
	 **/
	Graphics.getHSL = function(hue, saturation, lightness, alpha) {
		if (alpha == null) {
			return "hsl("+(hue%360)+","+saturation+"%,"+lightness+"%)";
		} else {
			return "hsla("+(hue%360)+","+saturation+"%,"+lightness+"%,"+alpha+")";
		}
	}
	
	/**
	 * Map of Base64 characters to values. Used by decodePath().
	 * @property BASE_64
	 * @static
	 * @final
	 * @type Object
	 **/
	Graphics.BASE_64 = {"A":0,"B":1,"C":2,"D":3,"E":4,"F":5,"G":6,"H":7,"I":8,"J":9,"K":10,"L":11,"M":12,"N":13,"O":14,"P":15,"Q":16,"R":17,"S":18,"T":19,"U":20,"V":21,"W":22,"X":23,"Y":24,"Z":25,"a":26,"b":27,"c":28,"d":29,"e":30,"f":31,"g":32,"h":33,"i":34,"j":35,"k":36,"l":37,"m":38,"n":39,"o":40,"p":41,"q":42,"r":43,"s":44,"t":45,"u":46,"v":47,"w":48,"x":49,"y":50,"z":51,"0":52,"1":53,"2":54,"3":55,"4":56,"5":57,"6":58,"7":59,"8":60,"9":61,"+":62,"/":63};
		
	
	/**
	 * Maps numeric values for the caps parameter of setStrokeStyle to corresponding string values.
	 * This is primarily for use with the tiny API. The mappings are as follows: 0 to "butt",
	 * 1 to "round", and 2 to "square".
	 * For example, myGraphics.ss(16, 2) would set the line caps to "square".
	 * @property STROKE_CAPS_MAP
	 * @static
	 * @final
	 * @type Array[String]
	 **/
	Graphics.STROKE_CAPS_MAP = ["butt", "round", "square"];
	
	/**
	 * Maps numeric values for the joints parameter of setStrokeStyle to corresponding string values.
	 * This is primarily for use with the tiny API. The mappings are as follows: 0 to "miter",
	 * 1 to "round", and 2 to "bevel".
	 * For example, myGraphics.ss(16, 0, 2) would set the line joints to "bevel".
	 * @property STROKE_JOINTS_MAP
	 * @static
	 * @final
	 * @type Array[String]
	 **/
	Graphics.STROKE_JOINTS_MAP = ["miter", "round", "bevel"];
	
	/**
	 * @property _ctx
	 * @static
	 * @protected
	 * @type CanvasRenderingContext2D
	 **/
	Graphics._ctx = (createjs.createCanvas?createjs.createCanvas():document.createElement("canvas")).getContext("2d");
	
	/**
	 * @property beginCmd
	 * @static
	 * @protected
	 * @type Command
	 **/
	Graphics.beginCmd = new Command(Graphics._ctx.beginPath, [], false);
	
	/**
	 * @property fillCmd
	 * @static
	 * @protected
	 * @type Command
	 **/
	Graphics.fillCmd = new Command(Graphics._ctx.fill, [], false);
	
	/**
	 * @property strokeCmd
	 * @static
	 * @protected
	 * @type Command
	 **/
	Graphics.strokeCmd = new Command(Graphics._ctx.stroke, [], false);
	
// public properties

// private properties
	/**
	 * @property _strokeInstructions
	 * @protected
	 * @type Array[Command]
	 **/
	p._strokeInstructions = null;

	/**
	 * @property _strokeStyleInstructions
	 * @protected
	 * @type Array[Command]
	 **/
	p._strokeStyleInstructions = null;
	
	/**
	 * @property _fillInstructions
	 * @protected
	 * @type Array[Command]
	 **/
	p._fillInstructions = null;
	
	/**
	 * @property _instructions
	 * @protected
	 * @type Array[Command]
	 **/
	p._instructions = null;
	
	/**
	 * @property _oldInstructions
	 * @protected
	 * @type Array[Command]
	 **/
	p._oldInstructions = null;
	
	/**
	 * @property _activeInstructions
	 * @protected
	 * @type Array[Command]
	 **/
	p._activeInstructions = null;
	
	/**
	 * @property _active
	 * @protected
	 * @type Boolean
	 * @default false
	 **/
	p._active = false;
	
	/**
	 * @property _dirty
	 * @protected
	 * @type Boolean
	 * @default false
	 **/
	p._dirty = false;
	
	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function() {
		this.clear();
		this._ctx = Graphics._ctx;
	}
	
	/**
	 * Draws the display object into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 **/
	p.draw = function(ctx) {
		if (this._dirty) { this._updateInstructions(); }
		var instr = this._instructions;
		for (var i=0, l=instr.length; i<l; i++) {
			instr[i].exec(ctx);
		}
	}
	
	/**
	 * Draws only the path described for this Graphics instance, skipping any
	 * non-path instructions, including fill and stroke descriptions.
	 * Used by DisplayObject.clippingPath to draw the clipping path, for example.
	 * @method drawAsPath
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 **/
	p.drawAsPath = function(ctx) {
		if (this._dirty) { this._updateInstructions(); }
		var instr, instrs = this._instructions;
		for (var i=0, l=instrs.length; i<l; i++) {
			// the first command is always a beginPath command.
			if ((instr = instrs[i]).path || i==0) { instr.exec(ctx); }
		}
	}
	
// public methods that map directly to context 2D calls:
	/**
	 * Moves the drawing point to the specified position.
	 * @method moveTo
	 * @param {Number} x The x coordinate the drawing point should move to.
	 * @param {Number} y The y coordinate the drawing point should move to.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.moveTo = function(x, y) {
		this._activeInstructions.push(new Command(this._ctx.moveTo, [x, y]));
		return this;
	}
	
	/**
	 * Draws a line from the current drawing point to the specified position, which become the new current drawing point. 
	 * For detailed information, read the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#complex-shapes-(paths)">
	 * whatwg spec</a>.
	 * @method lineTo
	 * @param {Number} x The x coordinate the drawing point should draw to.
	 * @param {Number} y The y coordinate the drawing point should draw to.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.lineTo = function(x, y) {
		this._dirty = this._active = true;
		this._activeInstructions.push(new Command(this._ctx.lineTo, [x, y]));
		return this;
	}
	
	/**
	 * Draws an arc with the specified control points and radius.  For detailed information, read the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#dom-context-2d-arcto">
	 * whatwg spec</a>.
	 * @method arcTo
	 * @param {Number} x1
	 * @param {Number} y1
	 * @param {Number} x2
	 * @param {Number} y2
	 * @param {Number} radius
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.arcTo = function(x1, y1, x2, y2, radius) {
		this._dirty = this._active = true;
		this._activeInstructions.push(new Command(this._ctx.arcTo, [x1, y1, x2, y2, radius]));
		return this;
	}
	
	/**
	 * Draws an arc defined by the radius, startAngle and endAngle arguments, centered at the position (x, y). For example 
	 * arc(100, 100, 20, 0, Math.PI*2) would draw a full circle with a radius of 20 centered at (100, 100). For detailed 
	 * information, read the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#dom-context-2d-arc">whatwg spec</a>.
	 * @method arc
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} radius
	 * @param {Number} startAngle Measured in radians.
	 * @param {Number} endAngle Measured in radians.
	 * @param {Boolean} anticlockwise
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.arc = function(x, y, radius, startAngle, endAngle, anticlockwise) {
		this._dirty = this._active = true;
		if (anticlockwise == null) { anticlockwise = false; }
		this._activeInstructions.push(new Command(this._ctx.arc, [x, y, radius, startAngle, endAngle, anticlockwise]));
		return this;
	}
	
	/**
	 * Draws a quadratic curve from the current drawing point to (x, y) using the control point (cpx, cpy).  For detailed information, 
	 * read the <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#dom-context-2d-quadraticcurveto">
	 * whatwg spec</a>.
	 * @method quadraticCurveTo
	 * @param {Number} cpx
	 * @param {Number} cpy
	 * @param {Number} x
	 * @param {Number} y
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.quadraticCurveTo = function(cpx, cpy, x, y) {
		this._dirty = this._active = true;
		this._activeInstructions.push(new Command(this._ctx.quadraticCurveTo, [cpx, cpy, x, y]));
		return this;
	}
	
	/**
	 * Draws a bezier curve from the current drawing point to (x, y) using the control points (cp1x, cp1y) and (cp2x, cp2y).  
	 * For detailed information, read the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#dom-context-2d-beziercurveto">
	 * whatwg spec</a>.
	 * method @bezierCurveTo
	 * @param {Number} cp1x
	 * @param {Number} cp1y
	 * @param {Number} cp2x
	 * @param {Number} cp2y
	 * @param {Number} x
	 * @param {Number} y
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.bezierCurveTo = function(cp1x, cp1y, cp2x, cp2y, x, y) {
		this._dirty = this._active = true;
		this._activeInstructions.push(new Command(this._ctx.bezierCurveTo, [cp1x, cp1y, cp2x, cp2y, x, y]));
		return this;
	}
	
	/**
	 * Draws a rectangle at (x, y) with the specified width and height using the current fill and/or stroke.
	 *  For detailed information, read the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#dom-context-2d-rect">
	 * whatwg spec</a>.
	 * @method rect
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} w Width of the rectangle
	 * @param {Number} h Height of the rectangle
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.rect = function(x, y, w, h) {
		this._dirty = this._active = true;
		this._activeInstructions.push(new Command(this._ctx.rect, [x, y, w, h]));
		return this;
	}
	
	/**
	 * Closes the current path, effectively drawing a line from the current drawing point to the first drawing point specified
	 * since the fill or stroke was last set.
	 * @method closePath
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.closePath = function() {
		if (this._active) {
			this._dirty = true;
			this._activeInstructions.push(new Command(this._ctx.closePath, []));
		}
		return this;
	}
	
	
// public methods that roughly map to Flash graphics APIs:
	/**
	 * Clears all drawing instructions, effectively reseting this Graphics instance.
	 * @method clear
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.clear = function() {
		this._instructions = [];
		this._oldInstructions = [];
		this._activeInstructions = [];
		this._strokeStyleInstructions = this._strokeInstructions = this._fillInstructions = null;
		this._active = this._dirty = false;
		return this;
	}
	
	/**
	 * Begins a fill with the specified color. This ends the current subpath.
	 * @method beginFill
	 * @param {String} color A CSS compatible color value (ex. "#FF0000" or "rgba(255,0,0,0.5)"). Setting to null will 
	 * result in no fill.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.beginFill = function(color) {
		if (this._active) { this._newPath(); }
		this._fillInstructions = color ? [new Command(this._setProp, ["fillStyle", color], false)] : null;
		return this;
	}
	
	/**
	 * Begins a linear gradient fill defined by the line (x0, y0) to (x1, y1). This ends the current subpath. For example, the
	 * following code defines a black to white vertical gradient ranging from 20px to 120px, and draws a square to display it:<br/>
	 * myGraphics.beginLinearGradientFill(["#000","#FFF"], [0, 1], 0, 20, 0, 120).drawRect(20, 20, 120, 120);
	 * @method beginLinearGradientFill
	 * @param {Array[String]} colors An array of CSS compatible color values. For example, ["#F00","#00F"] would define a gradient 
	 * drawing from red to blue.
	 * @param {Array[Number]} ratios An array of gradient positions which correspond to the colors. For example, [0.1, 0.9] would draw 
	 * the first color to 10% then interpolating to the second color at 90%.
	 * @param {Number} x0 The position of the first point defining the line that defines the gradient direction and size.
	 * @param {Number} y0 The position of the first point defining the line that defines the gradient direction and size.
	 * @param {Number} x1 The position of the second point defining the line that defines the gradient direction and size.
	 * @param {Number} y1 The position of the second point defining the line that defines the gradient direction and size.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.beginLinearGradientFill = function(colors, ratios, x0, y0, x1, y1) {
		if (this._active) { this._newPath(); }
		var o = this._ctx.createLinearGradient(x0, y0, x1, y1);
		for (var i=0, l=colors.length; i<l; i++) {
			o.addColorStop(ratios[i], colors[i]);
		}
		this._fillInstructions = [new Command(this._setProp, ["fillStyle", o], false)];
		return this;
	}
	
	/**
	 * Begins a radial gradient fill. This ends the current subpath. For example, the following code defines a red to blue radial 
	 * gradient centered at (100, 100), with a radius of 50, and draws a circle to display it:<br/>
	 * myGraphics.beginRadialGradientFill(["#F00","#00F"], [0, 1], 100, 100, 0, 100, 100, 50).drawCircle(100, 100, 50);
	 * @method beginRadialGradientFill
	 * @param {Array[String]} colors An array of CSS compatible color values. For example, ["#F00","#00F"] would define a gradient 
	 * drawing from red to blue.
	 * @param {Array[Number]} ratios An array of gradient positions which correspond to the colors. For example, [0.1, 0.9] would 
	 * draw the first color to 10% then interpolating to the second color at 90%.
	 * @param {Number} x0 Center position of the inner circle that defines the gradient.
	 * @param {Number} y0 Center position of the inner circle that defines the gradient.
	 * @param {Number} r0 Radius of the inner circle that defines the gradient.
	 * @param {Number} x1 Center position of the outer circle that defines the gradient.
	 * @param {Number} y1 Center position of the outer circle that defines the gradient.
	 * @param {Number} r1 Radius of the outer circle that defines the gradient.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.beginRadialGradientFill = function(colors, ratios, x0, y0, r0, x1, y1, r1) {
		if (this._active) { this._newPath(); }
		var o = this._ctx.createRadialGradient(x0, y0, r0, x1, y1, r1);
		for (var i=0, l=colors.length; i<l; i++) {
			o.addColorStop(ratios[i], colors[i]);
		}
		this._fillInstructions = [new Command(this._setProp, ["fillStyle", o], false)];
		return this;
	}
	
	/**
	 * Begins a pattern fill using the specified image. This ends the current subpath.
	 * @method beginBitmapFill
	 * @param {HTMLImageElement | HTMLCanvasElement | HTMLVideoElement} image The Image, Canvas, or Video object to use as the pattern.
	 * @param {String} repetition Optional. Indicates whether to repeat the image in the fill area. One of "repeat", "repeat-x",
	 * "repeat-y", or "no-repeat". Defaults to "repeat".
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.beginBitmapFill = function(image, repetition) {
		if (this._active) { this._newPath(); }
		repetition = repetition || "";
		var o = this._ctx.createPattern(image, repetition);
		this._fillInstructions = [new Command(this._setProp, ["fillStyle", o], false)];
		return this;
	}
	
	/**
	 * Ends the current subpath, and begins a new one with no fill. Functionally identical to beginFill(null).
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.endFill = function() {
		return this.beginFill();
	}
	
	/**
	 * Sets the stroke style for the current subpath. Like all drawing methods, this can be chained, so you can define the stroke style and color in a single line of code like so:
	 * myGraphics.setStrokeStyle(8,"round").beginStroke("#F00");
	 * @method setStrokeStyle
	 * @param {Number} thickness The width of the stroke.
	 * @param {String | Number} caps Optional. Indicates the type of caps to use at the end of lines. One of butt, round, or square. Defaults to "butt". Also accepts the values 0 (butt), 1 (round), and 2 (square) for use with the tiny API.
	 * @param {String | Number} joints Optional. Specifies the type of joints that should be used where two lines meet. One of bevel, round, or miter. Defaults to "miter". Also accepts the values 0 (miter), 1 (round), and 2 (bevel) for use with the tiny API.
	 * @param {Number} miter Optional. If joints is set to "miter", then you can specify a miter limit ratio which controls at what point a mitered joint will be clipped.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.setStrokeStyle = function(thickness, caps, joints, miterLimit) {
		if (this._active) { this._newPath(); }
		this._strokeStyleInstructions = [
			new Command(this._setProp, ["lineWidth", (thickness == null ? "1" : thickness)], false),
			new Command(this._setProp, ["lineCap", (caps == null ? "butt" : (isNaN(caps) ? caps : Graphics.STROKE_CAPS_MAP[caps]))], false),
			new Command(this._setProp, ["lineJoin", (joints == null ? "miter" : (isNaN(joints) ? joints : Graphics.STROKE_JOINTS_MAP[joints]))], false),
			new Command(this._setProp, ["miterLimit", (miterLimit == null ? "10" : miterLimit)], false)
			];
		return this;
	}
	
	/**
	 * Begins a stroke with the specified color. This ends the current subpath.
	 * @method beginStroke
	 * @param {String} color A CSS compatible color value (ex. "#FF0000" or "rgba(255,0,0,0.5)"). Setting to null will result in no stroke.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.beginStroke = function(color) {
		if (this._active) { this._newPath(); }
		this._strokeInstructions = color ? [new Command(this._setProp, ["strokeStyle", color], false)] : null;
		return this;
	}
	
	/**
	 * Begins a linear gradient stroke defined by the line (x0, y0) to (x1, y1). This ends the current subpath. For example, the following code defines a black to white vertical gradient ranging from 20px to 120px, and draws a square to display it:<br/>
	 * myGraphics.setStrokeStyle(10).beginLinearGradientStroke(["#000","#FFF"], [0, 1], 0, 20, 0, 120).drawRect(20, 20, 120, 120);
	 * @method beginLinearGradientStroke
	 * @param {Array[String]} colors An array of CSS compatible color values. For example, ["#F00","#00F"] would define a gradient drawing from red to blue.
	 * @param {Array[Number]} ratios An array of gradient positions which correspond to the colors. For example, [0.1, 0.9] would draw the first color to 10% then interpolating to the second color at 90%.
	 * @param {Number} x0 The position of the first point defining the line that defines the gradient direction and size.
	 * @param {Number} y0 The position of the first point defining the line that defines the gradient direction and size.
	 * @param {Number} x1 The position of the second point defining the line that defines the gradient direction and size.
	 * @param {Number} y1 The position of the second point defining the line that defines the gradient direction and size.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.beginLinearGradientStroke = function(colors, ratios, x0, y0, x1, y1) {
		if (this._active) { this._newPath(); }
		var o = this._ctx.createLinearGradient(x0, y0, x1, y1);
		for (var i=0, l=colors.length; i<l; i++) {
			o.addColorStop(ratios[i], colors[i]);
		}
		this._strokeInstructions = [new Command(this._setProp, ["strokeStyle", o], false)];
		return this;
	}

	
	/**
	 * Begins a radial gradient stroke. This ends the current subpath. For example, the following code defines a red to blue radial gradient centered at (100, 100), with a radius of 50, and draws a rectangle to display it:<br/>
	 * myGraphics.setStrokeStyle(10).beginRadialGradientStroke(["#F00","#00F"], [0, 1], 100, 100, 0, 100, 100, 50).drawRect(50, 90, 150, 110);
	 * @method beginRadialGradientStroke
	 * @param {Array[String]} colors An array of CSS compatible color values. For example, ["#F00","#00F"] would define a gradient drawing from red to blue.
	 * @param {Array[Number]} ratios An array of gradient positions which correspond to the colors. For example, [0.1, 0.9] would draw the first color to 10% then interpolating to the second color at 90%, then draw the second color to 100%.
	 * @param {Number} x0 Center position of the inner circle that defines the gradient.
	 * @param {Number} y0 Center position of the inner circle that defines the gradient.
	 * @param {Number} r0 Radius of the inner circle that defines the gradient.
	 * @param {Number} x1 Center position of the outer circle that defines the gradient.
	 * @param {Number} y1 Center position of the outer circle that defines the gradient.
	 * @param {Number} r1 Radius of the outer circle that defines the gradient.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)	
	 **/
	p.beginRadialGradientStroke = function(colors, ratios, x0, y0, r0, x1, y1, r1) {
		if (this._active) { this._newPath(); }
		var o = this._ctx.createRadialGradient(x0, y0, r0, x1, y1, r1);
		for (var i=0, l=colors.length; i<l; i++) {
			o.addColorStop(ratios[i], colors[i]);
		}
		this._strokeInstructions = [new Command(this._setProp, ["strokeStyle", o], false)];
		return this;
	}
	
	/**
	 * Begins a pattern fill using the specified image. This ends the current subpath.
	 * @method beginBitmapStroke
	 * @param {HTMLImageElement | HTMLCanvasElement | HTMLVideoElement} image The Image, Canvas, or Video object to use as the pattern.
	 * @param {String} repetition Optional. Indicates whether to repeat the image in the fill area. One of "repeat", "repeat-x",
	 * "repeat-y", or "no-repeat". Defaults to "repeat".
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)	
	 **/
	p.beginBitmapStroke = function(image, repetition) {
		if (this._active) { this._newPath(); }
		var o = this._ctx.createPattern(image, repetition || "");
		this._strokeInstructions = [new Command(this._setProp, ["strokeStyle", o], false)];
		return this;
	}
	
	
	/**
	 * Ends the current subpath, and begins a new one with no stroke. Functionally identical to beginStroke(null).
	 * @method endStroke
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.endStroke = function() {
		this.beginStroke();
		return this;
	}
	
	/**
	 * Maps the familiar ActionScript curveTo() method to the functionally similar quatraticCurveTo() method.
	 * @property curveTo
	 * @type Function
	 **/
	p.curveTo = p.quadraticCurveTo;
	
	/**
	 * Maps the familiar ActionScript drawRect() method to the functionally similar rect() method.
	 * @property drawRect
	 * @type Function
	 **/
	p.drawRect = p.rect;
	
	/**
	 * Draws a rounded rectangle with all corners with the specified radius.
	 * @method drawRoundRect
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} w
	 * @param {Number} h
	 * @param {Number} radius Corner radius.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.drawRoundRect = function(x, y, w, h, radius) {
		this.drawRoundRectComplex(x, y, w, h, radius, radius, radius, radius);
		return this;
	}
	
	/**
	 * Draws a rounded rectangle with different corner radii. Supports positive and negative corner radii.
	 * @method drawRoundRectComplex
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} w
	 * @param {Number} h
	 * @param {Number} radiusTL Top left corner radius.
	 * @param {Number} radiusTR Top right corner radius.
	 * @param {Number} radiusBR Bottom right corner radius.
	 * @param {Number} radiusBL Bottom left corner radius.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.drawRoundRectComplex = function(x, y, w, h, radiusTL, radiusTR, radiusBR, radiusBL) {
		var max = (w<h?w:h)/2;
		var mTL=0, mTR=0, mBR=0, mBL=0;
		if (radiusTL < 0) { radiusTL *= (mTL=-1); }
		if (radiusTL > max) { radiusTL = max; }
		if (radiusTR < 0) { radiusTR *= (mTR=-1); }
		if (radiusTR > max) { radiusTR = max; }
		if (radiusBR < 0) { radiusBR *= (mBR=-1); }
		if (radiusBR > max) { radiusBR = max; }
		if (radiusBL < 0) { radiusBL *= (mBL=-1); }
		if (radiusBL > max) { radiusBL = max; }
		
		this._dirty = this._active = true;
		var arcTo=this._ctx.arcTo, lineTo=this._ctx.lineTo;
		this._activeInstructions.push(
			new Command(this._ctx.moveTo, [x+w-radiusTR, y]),
			new Command(arcTo, [x+w+radiusTR*mTR, y-radiusTR*mTR, x+w, y+radiusTR, radiusTR]),
			new Command(lineTo, [x+w, y+h-radiusBR]),
			new Command(arcTo, [x+w+radiusBR*mBR, y+h+radiusBR*mBR, x+w-radiusBR, y+h, radiusBR]),
			new Command(lineTo, [x+radiusBL, y+h]),
			new Command(arcTo, [x-radiusBL*mBL, y+h+radiusBL*mBL, x, y+h-radiusBL, radiusBL]),
			new Command(lineTo, [x, y+radiusTL]),
			new Command(arcTo, [x-radiusTL*mTL, y-radiusTL*mTL, x+radiusTL, y, radiusTL]),
			new Command(this._ctx.closePath)
		);
		return this;
	} 
	
	/**
	 * Draws a circle with the specified radius at (x, y).
	*
	 * <pre><code>var g = new Graphics();
	*	g.setStrokeStyle(1);
	*	g.beginStroke(Graphics.getRGB(0,0,0));
	*	g.beginFill(Graphics.getRGB(255,0,0));
	*	g.drawCircle(0,0,3);
	*
	*	var s = new Shape(g);
	*		s.x = 100;
	*		s.y = 100;
	*
	*	stage.addChild(s);
	*	stage.update();</code></pre>
	 * @method drawCircle
	 * @param {Number} x x coordinate center point of circle.
	 * @param {Number} y y coordinate center point of circle.
	 * @param {Number} radius Radius of circle.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.drawCircle = function(x, y, radius) {
		this.arc(x, y, radius, 0, Math.PI*2);
		return this;
	}
	
	/**
	 * Draws an ellipse (oval).
	 * @method drawEllipse
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} w
	 * @param {Number} h
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.drawEllipse = function(x, y, w, h, startAngle, endAngle, innerRadius) {
		this._dirty = this._active = true;
		var k = 0.5522848;
		var ox = (w / 2) * k;
		var oy = (h / 2) * k;
		var xe = x + w;
		var ye = y + h;
		var xm = x + w / 2;
		var ym = y + h / 2;
			
		this._activeInstructions.push(
			new Command(this._ctx.moveTo, [x, ym]),
			new Command(this._ctx.bezierCurveTo, [x, ym-oy, xm-ox, y, xm, y]),
			new Command(this._ctx.bezierCurveTo, [xm+ox, y, xe, ym-oy, xe, ym]),
			new Command(this._ctx.bezierCurveTo, [xe, ym+oy, xm+ox, ye, xm, ye]),
			new Command(this._ctx.bezierCurveTo, [xm-ox, ye, x, ym+oy, x, ym])
		);
		return this;
	}
	
	/**
	 * Draws a star if pointSize is greater than 0 or a regular polygon if pointSize is 0 with the specified number of points.
	 * For example, the following code will draw a familiar 5 pointed star shape centered at 100, 100 and with a radius of 50:
	 * myGraphics.beginFill("#FF0").drawPolyStar(100, 100, 50, 5, 0.6, -90); // -90 makes the first point vertical
	 * @method drawPolyStar
	 * @param {Number} x Position of the center of the shape.
	 * @param {Number} y Position of the center of the shape.
	 * @param {Number} radius The outer radius of the shape.
	 * @param {Number} sides The number of points on the star or sides on the polygon.
	 * @param {Number} pointSize The depth or "pointy-ness" of the star points. A pointSize of 0 will draw a regular polygon (no points), 
	 * a pointSize of 1 will draw nothing because the points are infinitely pointy.
	 * @param {Number} angle The angle of the first point / corner. For example a value of 0 will draw the first point directly to the 
	 * right of the center.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.drawPolyStar = function(x, y, radius, sides, pointSize, angle) {
		this._dirty = this._active = true;
		if (pointSize == null) { pointSize = 0; }
		pointSize = 1-pointSize;
		if (angle == null) { angle = 0; }
		else { angle /= 180/Math.PI; }
		var a = Math.PI/sides;
		
		this._activeInstructions.push(new Command(this._ctx.moveTo, [x+Math.cos(angle)*radius, y+Math.sin(angle)*radius]));
		for (var i=0; i<sides; i++) {
			angle += a;
			if (pointSize != 1) {
				this._activeInstructions.push(new Command(this._ctx.lineTo, [x+Math.cos(angle)*radius*pointSize, y+Math.sin(angle)*radius*pointSize]));
			}
			angle += a;
			this._activeInstructions.push(new Command(this._ctx.lineTo, [x+Math.cos(angle)*radius, y+Math.sin(angle)*radius]));
		}
		return this;
	}
	
	/**
	 * Decodes a compact encoded path string into a series of draw instructions.
	 * This format is not intended to be human readable, and is meant for use by authoring tools.
	 * The format uses a base64 character set, with each character representing 6 bits, to define a series of draw commands.
	 * <br/><br/>
	 * Each command is comprised of a single "header" character followed by a variable number of alternating x and y position values.
	 * Reading the header bits from left to right (most to least significant): bits 1 to 3 specify the type of operation
	 * (0-moveTo, 1-lineTo, 2-quadraticCurveTo, 3-bezierCurveTo, 4-closePath, 5-7 unused). Bit 4 indicates whether position values use 12 bits (2 characters) 
	 * or 18 bits (3 characters), with a one indicating the latter. Bits 5 and 6 are currently unused.
	 * <br/><br/>
	 * Following the header is a series of 0 (closePath), 2 (moveTo, lineTo), 4 (quadraticCurveTo), or 6 (bezierCurveTo) parameters.
	 * These parameters are alternating x/y positions represented by 2 or 3 characters (as indicated by the 4th bit in the command char).
	 * These characters consist of a 1 bit sign (1 is negative, 0 is positive), followed by an 11 (2 char) or 17 (3 char) bit integer value.
	 * All position values are in tenths of a pixel.
	 * Except in the case of move operations which are absolute, this value is a delta from the previous x or y position (as appropriate).
	 * <br/><br/>
	 * For example, the string "A3cAAMAu4AAA" represents a line starting at -150,0 and ending at 150,0.
	 * A - bits 000000. First 3 bits (000) indicate a moveTo operation. 4th bit (0) indicates 2 chars per parameter.
	 * n0 - 110111011100. Absolute x position of -150.0px. First bit indicates a negative value, remaining bits indicate 1500 tenths of a pixel. 
	 * AA - 000000000000. Absolute y position of 0.
	 * I - 001100. First 3 bits (001) indicate a lineTo operation. 4th bit (1) indicates 3 chars per parameter.
	 * Au4 - 000000101110111000. An x delta of 300.0px, which is added to the previous x value of -150.0px to provide an absolute position of +150.0px.
	 * AAA - 000000000000000000. A y delta value of 0.
	 * 
	 * @method decodePath
	 * @param {String} str The path string to decode.
	 * @return {Graphics} The Graphics instance the method is called on (useful for chaining calls.)
	 **/
	p.decodePath = function(str) {
		var instructions = [this.moveTo, this.lineTo, this.quadraticCurveTo, this.bezierCurveTo, this.closePath];
		var paramCount = [2, 2, 4, 6, 0];
		var i=0, l=str.length;
		var params = [];
		var x=0, y=0;
		var base64 = Graphics.BASE_64;
		
		while (i<l) {
			var c = str.charAt(i);
			var n = base64[c];
			var fi = n>>3; // highest order bits 1-3 code for operation.
			var f = instructions[fi];
			// check that we have a valid instruction & that the unused bits are empty:
			if (!f || (n&3)) { throw("bad path data (@"+i+"): "+c); }
			var pl = paramCount[fi];
			if (!fi) { x=y=0; } // move operations reset the position.
			params.length = 0;
			i++;
			var charCount = (n>>2&1)+2;  // 4th header bit indicates number size for this operation.
			for (var p=0; p<pl; p++) {
				var num = base64[str.charAt(i)];
				var sign = (num>>5) ? -1 : 1;
				num = ((num&31)<<6)|(base64[str.charAt(i+1)]);
				if (charCount == 3) { num = (num<<6)|(base64[str.charAt(i+2)]); }
				num = sign*num/10;
				if (p%2) { x = (num += x); }
				else { y = (num += y); }
				params[p] = num;
				i += charCount;
			}
			f.apply(this,params);
		}
		return this;
	}
	
	/**
	 * Returns a clone of this Graphics instance.
	 * @method clone
	 * @return {Graphics} A clone of the current Graphics instance.
	 **/
	p.clone = function() {
		var o = new Graphics();
		o._instructions = this._instructions.slice();
		o._activeInstructions = this._activeInstructions.slice();
		o._oldInstructions = this._oldInstructions.slice();
		if (this._fillInstructions) { o._fillInstructions = this._fillInstructions.slice(); }
		if (this._strokeInstructions) { o._strokeInstructions = this._strokeInstructions.slice(); }
		if (this._strokeStyleInstructions) { o._strokeStyleInstructions = this._strokeStyleInstructions.slice(); }
		o._active = this._active;
		o._dirty = this._dirty;
		o.drawAsPath = this.drawAsPath;
		return o;
	}
		
	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Graphics]";
	}
	
	
// tiny API:
	/** Shortcut to moveTo.
	 * @property mt
	 * @protected
	 * type Function
	 **/
	p.mt = p.moveTo;
	
	/** Shortcut to lineTo.
	 * @property lt
	 * @protected
	 * type Function
	 **/
	p.lt = p.lineTo;
	
	/** Shortcut to arcTo.
	 * @property at
	 * @protected
	 * type Function
	 **/
	p.at = p.arcTo;
	
	/** Shortcut to bezierCurveTo.
	 * @property bt
	 * @protected
	 * type Function
	 **/
	p.bt = p.bezierCurveTo;
	
	/** Shortcut to quadraticCurveTo / curveTo.
	 * @property qt
	 * @protected
	 * type Function
	 **/
	p.qt = p.quadraticCurveTo;
	
	/** Shortcut to arc.
	 * @property a
	 * @protected
	 * type Function
	 **/
	p.a = p.arc;
	
	/** Shortcut to rect.
	 * @property r
	 * @protected
	 * type Function
	 **/
	p.r = p.rect;
	
	/** Shortcut to closePath.
	 * @property cp
	 * @protected
	 * type Function
	 **/
	p.cp = p.closePath;
	
	/** Shortcut to clear.
	 * @property c
	 * @protected
	 * type Function
	 **/
	p.c = p.clear;
	
	/** Shortcut to beginFill.
	 * @property f
	 * @protected
	 * type Function
	 **/
	p.f = p.beginFill;
	
	/** Shortcut to beginLinearGradientFill.
	 * @property lf
	 * @protected
	 * type Function
	 **/
	p.lf = p.beginLinearGradientFill;
	
	/** Shortcut to beginRadialGradientFill.
	 * @property rf
	 * @protected
	 * type Function
	 **/
	p.rf = p.beginRadialGradientFill;
	
	/** Shortcut to beginBitmapFill.
	 * @property bf
	 * @protected
	 * type Function
	 **/
	p.bf = p.beginBitmapFill;
	
	/** Shortcut to endFill.
	 * @property ef
	 * @protected
	 * type Function
	 **/
	p.ef = p.endFill;
	
	/** Shortcut to setStrokeStyle.
	 * @property ss
	 * @protected
	 * type Function
	 **/
	p.ss = p.setStrokeStyle;
	
	/** Shortcut to beginStroke.
	 * @property s
	 * @protected
	 * type Function
	 **/
	p.s = p.beginStroke;
	
	/** Shortcut to beginLinearGradientStroke.
	 * @property ls
	 * @protected
	 * type Function
	 **/
	p.ls = p.beginLinearGradientStroke;
	
	/** Shortcut to beginRadialGradientStroke.
	 * @property rs
	 * @protected
	 * type Function
	 **/
	p.rs = p.beginRadialGradientStroke;
	
	/** Shortcut to beginBitmapStroke.
	 * @property bs
	 * @protected
	 * type Function
	 **/
	p.bs = p.beginBitmapStroke;
	
	/** Shortcut to endStroke.
	 * @property es
	 * @protected
	 * type Function
	 **/
	p.es = p.endStroke;
	
	/** Shortcut to drawRect.
	 * @property dr
	 * @protected
	 * type Function
	 **/
	p.dr = p.drawRect;
	
	/** Shortcut to drawRoundRect.
	 * @property rr
	 * @protected
	 * type Function
	 **/
	p.rr = p.drawRoundRect;
	
	/** Shortcut to drawRoundRectComplex.
	 * @property rc
	 * @protected
	 * type Function
	 **/
	p.rc = p.drawRoundRectComplex;
	
	/** Shortcut to drawCircle.
	 * @property dc
	 * @protected
	 * type Function
	 **/
	p.dc = p.drawCircle;
	
	/** Shortcut to drawEllipse.
	 * @property de
	 * @protected
	 * type Function
	 **/
	p.de = p.drawEllipse;
	
	/** Shortcut to drawPolyStar.
	 * @property dp
	 * @protected
	 * type Function
	 **/
	p.dp = p.drawPolyStar;
	
	/** Shortcut to decodePath.
	 * @property p
	 * @protected
	 * type Function
	 **/
	p.p = p.decodePath;
	
	
// private methods:
	/**
	 * @method _updateInstructions
	 * @protected
	 **/
	p._updateInstructions = function() {
		this._instructions = this._oldInstructions.slice();
		this._instructions.push(Graphics.beginCmd);
		 
		if (this._fillInstructions) { this._instructions.push.apply(this._instructions, this._fillInstructions); }
		if (this._strokeInstructions) {
			this._instructions.push.apply(this._instructions, this._strokeInstructions);
			if (this._strokeStyleInstructions) {
				this._instructions.push.apply(this._instructions, this._strokeStyleInstructions);
			}
		}
		
		this._instructions.push.apply(this._instructions, this._activeInstructions);
		
		if (this._fillInstructions) { this._instructions.push(Graphics.fillCmd); }
		if (this._strokeInstructions) { this._instructions.push(Graphics.strokeCmd); }
	}
	
	p._getEllipseArc = function(x, y, w, h, startAngle, endAngle) {
		var k = 0.5522848;
		var ox = (w / 2) * k;
		var oy = (h / 2) * k;
		var xe = x + w;
		var ye = y + h;
		var xm = x + w / 2;
		var ym = y + h / 2;
			
		this._activeInstructions.push(
			new Command(this._ctx.moveTo, [x, ym]),
			new Command(this._ctx.bezierCurveTo, [x, ym-oy, xm-ox, y, xm, y]),
			new Command(this._ctx.bezierCurveTo, [xm+ox, y, xe, ym-oy, xe, ym]),
			new Command(this._ctx.bezierCurveTo, [xe, ym+oy, xm+ox, ye, xm, ye]),
			new Command(this._ctx.bezierCurveTo, [xm-ox, ye, x, ym+oy, x, ym])
		);
		return this;
	}
	
	/**
	 * @method _newPath
	 * @protected
	 **/
	p._newPath = function() {
		if (this._dirty) { this._updateInstructions(); }
		this._oldInstructions = this._instructions;
		this._activeInstructions = [];
		this._active = this._dirty = false;
	}
	
	// used to create Commands that set properties:
	/**
	 * used to create Commands that set properties
	 * @method _setProp
	 * @param {String} name
	 * @param {String} value
	 * @protected
	 **/
	p._setProp = function(name, value) {
		this[name] = value;
	}

createjs.Graphics = Graphics;
}());
/*
* DisplayObject
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

/**
* The EaselJS Javascript library provides a retained graphics mode for canvas
* including a full, hierarchical display list, a core interaction model, and
* helper classes to make working with 2D graphics in Canvas much easier.
* @module EaselJS
**/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* DisplayObject is an abstract class that should not be constructed directly. Instead construct subclasses such as
* Container, Bitmap, and Shape. DisplayObject is the base class for all display classes in the EaselJS library.
* It defines the core properties and methods that are shared between all display objects.
* @class DisplayObject
* @uses EventDispatcher
* @constructor
**/
var DisplayObject = function() {
  this.initialize();
}
var p = DisplayObject.prototype;

	/**
	 * Suppresses errors generated when using features like hitTest, onPress/onClick, and getObjectsUnderPoint with cross
	 * domain content
	 * @property suppressCrossDomainErrors
	 * @static
	 * @type Boolean
	 * @default false
	 **/
	DisplayObject.suppressCrossDomainErrors = false;

	/**
	 * @property _hitTestCanvas
	 * @type HTMLCanvasElement | Object
	 * @static
	 * @protected
	 **/
	DisplayObject._hitTestCanvas = createjs.createCanvas?createjs.createCanvas():document.createElement("canvas");
	DisplayObject._hitTestCanvas.width = DisplayObject._hitTestCanvas.height = 1;

	/**
	 * @property _hitTestContext
	 * @type CanvasRenderingContext2D
	 * @static
	 * @protected
	 **/
	DisplayObject._hitTestContext = DisplayObject._hitTestCanvas.getContext("2d");

	/**
	 * @property _nextCacheID
	 * @type Number
	 * @static
	 * @protected
	 **/
	DisplayObject._nextCacheID = 1;

	/**
	 * The alpha (transparency) for this display object. 0 is fully transparent, 1 is fully opaque.
	 * @property alpha
	 * @type Number
	 * @default 1
	 **/
	p.alpha = 1;

	/**
	 * If a cache is active, this returns the canvas that holds the cached version of this display object. See cache()
	 * for more information. READ-ONLY.
	 * @property cacheCanvas
	 * @type HTMLCanvasElement | Object
	 * @default null
	 **/
	p.cacheCanvas = null;

	/**
	 * Unique ID for this display object. Makes display objects easier for some uses.
	 * @property id
	 * @type Number
	 * @default -1
	 **/
	p.id = -1;

	/**
	 * Indicates whether to include this object when running Stage.getObjectsUnderPoint(), and thus for mouse
	 * interactions. Setting this to true for
	 * Containers will cause the Container to be returned (not its children) regardless of whether it's mouseChildren property
	 * is true.
	 * @property mouseEnabled
	 * @type Boolean
	 * @default true
	 **/
	p.mouseEnabled = true;

	/**
	 * An optional name for this display object. Included in toString(). Useful for debugging.
	 * @property name
	 * @type String
	 * @default null
	 **/
	p.name = null;

	/**
	 * A reference to the Container or Stage object that contains this display object, or null if it has not been added to
	 * one. READ-ONLY.
	 * @property parent
	 * @final
	 * @type DisplayObject
	 * @default null
	 **/
	p.parent = null;

	/**
	 * The x offset for this display object's registration point. For example, to make a 100x100px Bitmap rotate around
	 * it's center, you would set regX and regY to 50.
	 * @property regX
	 * @type Number
	 * @default 0
	 **/
	p.regX = 0;

	/**
	 * The y offset for this display object's registration point. For example, to make a 100x100px Bitmap rotate around
	 * it's center, you would set regX and regY to 50.
	 * @property regY
	 * @type Number
	 * @default 0
	 **/
	p.regY = 0;

	/**
	 * The rotation in degrees for this display object.
	 * @property rotation
	 * @type Number
	 * @default 0
	 **/
	p.rotation = 0;

	/**
	 * The factor to stretch this display object horizontally. For example, setting scaleX to 2 will stretch the display
	 * object to twice it's nominal width.
	 * @property scaleX
	 * @type Number
	 * @default 1
	 **/
	p.scaleX = 1;

	/**
	 * The factor to stretch this display object vertically. For example, setting scaleY to 0.5 will stretch the display
	 * object to half it's nominal height.
	 * @property scaleY
	 * @type Number
	 * @default 1
	 **/
	p.scaleY = 1;

	/**
	 * The factor to skew this display object horizontally.
	 * @property skewX
	 * @type Number
	 * @default 0
	 **/
	p.skewX = 0;

	/**
	 * The factor to skew this display object vertically.
	 * @property skewY
	 * @type Number
	 * @default 0
	 **/
	p.skewY = 0;

	/**
	 * A shadow object that defines the shadow to render on this display object. Set to null to remove a shadow. If
	 * null, this property is inherited from the parent container.
	 * @property shadow
	 * @type Shadow
	 * @default null
	 **/
	p.shadow = null;

	/**
	 * Indicates whether this display object should be rendered to the canvas and included when running
	 * Stage.getObjectsUnderPoint().
	 * @property visible
	 * @type Boolean
	 * @default true
	 **/
	p.visible = true;

	/**
	 * The x (horizontal) position of the display object, relative to its parent.
	 * @property x
	 * @type Number
	 * @default 0
	 **/
	p.x = 0;

	/** The y (vertical) position of the display object, relative to its parent.
	 * @property y
	 * @type Number
	 * @default 0
	 **/
	p.y = 0;

	/**
	 * The composite operation indicates how the pixels of this display object will be composited with the elements
	 * behind it. If null, this property is inherited from the parent container. For more information, read the
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#compositing">
	 * whatwg spec on compositing</a>.
	 * @property compositeOperation
	 * @type String
	 * @default null
	 **/
	p.compositeOperation = null;

	/**
	 * Indicates whether the display object should have it's x & y position rounded prior to drawing it to stage.
	 * Snapping to whole pixels can result in a sharper and faster draw for images (ex. Bitmap & cached objects).
	 * This only applies if the enclosing stage has snapPixelsEnabled set to true. The snapToPixel property is true
	 * by default for Bitmap and BitmapAnimation instances, and false for all other display objects.
	 * <br/><br/>
	 * Note that this applies only rounds the display object's local position. You should
	 * ensure that all of the display object's ancestors (parent containers) are also on a whole pixel. You can do this
	 * by setting the ancestors' snapToPixel property to true.
	 * @property snapToPixel
	 * @type Boolean
	 * @default false
	 **/
	p.snapToPixel = false;

	/**
	 * The onPress callback is called when the user presses down on their mouse over this display object. The handler
	 * is passed a single param containing the corresponding MouseEvent instance. You can subscribe to the onMouseMove
	 * and onMouseUp callbacks of the event object to receive these events until the user releases the mouse button.
	 * If an onPress handler is set on a container, it will receive the event if any of its children are clicked.
	 * @event onPress
	 * @param {MouseEvent} event MouseEvent with information about the event.
	 **/
	p.onPress = null;

	/**
	 * The onClick callback is called when the user presses down on and then releases the mouse button over this
	 * display object. The handler is passed a single param containing the corresponding MouseEvent instance. If an
	 * onClick handler is set on a container, it will receive the event if any of its children are clicked.
	 * @event onClick
	 * @param {MouseEvent} event MouseEvent with information about the event.
	 **/
	p.onClick = null;

	/**
	 * The onDoubleClick callback is called when the user double clicks over this display object. The handler is
	 * passed a single param containing the corresponding MouseEvent instance. If an onDoubleClick handler is set
	 * on a container, it will receive the event if any of its children are clicked.
	 * @event onDoubleClick
	 * @param {MouseEvent} event MouseEvent with information about the event.
	 **/
	p.onDoubleClick = null;

	/**
	 * The onMouseOver callback is called when the user rolls over the display object. You must enable this event using
	 * stage.enableMouseOver(). The handler is passed a single param containing the corresponding MouseEvent instance.
	 * @event onMouseOver
	 * @param {MouseEvent} event MouseEvent with information about the event.
	 **/
	p.onMouseOver = null;

	/**
	 * The onMouseOut callback is called when the user rolls off of the display object. You must enable this event using
	 * stage.enableMouseOver(). The handler is passed a single param containing the corresponding MouseEvent instance.
	 * @event onMouseOut
	 * @param {MouseEvent} event MouseEvent with information about the event.
	 **/
	p.onMouseOut = null;

	/**
	 * The onTick callback is called on each display object on a stage whenever the stage updates.
	 * This occurs immediately before the rendering (draw) pass. When stage.update() is called, first all display objects
	 * on the stage have onTick called, then all of the display objects are drawn to stage. Children will have their
	 * onTick called in order of their depth prior to onTick being called on their parent.
	 * <br/><br/>
	 * Any parameters passed in to stage.update() are passed on to the onTick() handlers. For example, if you call
	 * stage.update("hello"), all of the display objects with a handler will have onTick("hello") called.
	 * @event onTick
	 **/
	p.onTick = null;

	/**
	 * An array of Filter objects to apply to this display object. Filters are only applied / updated when cache() or
	 * updateCache() is called on the display object, and only apply to the area that is cached.
	 * @property filters
	 * @type Array[Filter]
	 * @default null
	 **/
	p.filters = null;

	/**
	* Returns an ID number that uniquely identifies the current cache for this display object.
	* This can be used to determine if the cache has changed since a previous check.
	* @property cacheID
	* @type Number
	* @default 0
	*/
	p.cacheID = 0;
	
	/**
	 * A Shape instance that defines a vector mask (clipping path) for this display object.  The shape's transformation
	 * will be applied relative to the display object's parent coordinates (as if it were a child of the parent).
	 * @property mask
	 * @type Shape
	 * @default null
	 */
	p.mask = null;
	
	/**
	 * A display object that will be tested when checking mouse interactions or testing getObjectsUnderPoint. The hit area
	 * will have its transformation applied relative to this display object's coordinate space (as though the hit test object were a child of this
	 * display object and relative to its regX/Y). It is NOT used for hitTest().
	 * @property hitArea
	 * @type DisplayObject
	 * @default null
	 */
	p.hitArea = null;
	
	/**
	 * A CSS cursor (ex. "pointer", "help", "text", etc) that will be displayed when the user hovers over this display object. You must enable
	 * mouseover events using the stage.enableMouseOver() method to use this property. If null it will use the default cursor.
	 * @property cursor
	 * @type String
	 * @default null
	 */
	p.cursor = null;
	
	
// mix-ins:
	// EventDispatcher methods:
	p.addEventListener = null;
	p.removeEventListener = null;
	p.removeAllEventListeners = null;
	p.dispatchEvent = null;
	p.hasEventListener = null;
	p._listeners = null;
	
	// we only use EventDispatcher if it's available:
	createjs.EventDispatcher && createjs.EventDispatcher.initialize(p); // inject EventDispatcher methods.
	

// private properties:

	/**
	 * @property _cacheOffsetX
	 * @protected
	 * @type Number
	 * @default 0
	 **/
	p._cacheOffsetX = 0;

	/**
	 * @property _cacheOffsetY
	 * @protected
	 * @type Number
	 * @default 0
	 **/
	p._cacheOffsetY = 0;
	
	/**
	 * @property _cacheScale
	 * @protected
	 * @type Number
	 * @default 1
	 **/
	p._cacheScale = 1;

	/**
	* @property _cacheDataURLID
	* @protected
	* @type Number
	* @default 0
	*/
	p._cacheDataURLID = 0;
	
	/**
	* @property _cacheDataURL
	* @protected
	* @type String
	* @default null
	*/
	p._cacheDataURL = null;

	/**
	 * @property _matrix
	 * @protected
	 * @type Matrix2D
	 * @default null
	 **/
	p._matrix = null;
	

// constructor:
	// separated so it can be easily addressed in subclasses:

	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function() {
		this.id = createjs.UID.get();
		this._matrix = new createjs.Matrix2D();
	}

// public methods:
	
	p.set = function(opts) {
		for(var opt in opts) {
			var value = opts[opt];
			var setter = 'set' + opt.substring(0,1).toUpperCase() + opt.substring(1);
			if(typeof this[setter] === 'function') {
				this[setter](value);
			} else if(opt in this) {
				this[opt] = value;
			}
		}
		return this;
	}
	
	/**
	 * Returns true or false indicating whether the display object would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the display object would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0;
	}

	/**
	 * Draws the display object into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache.
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		var cacheCanvas = this.cacheCanvas;
		if (ignoreCache || !cacheCanvas) { return false; }
		var scale = this._cacheScale;
		ctx.drawImage(cacheCanvas, this._cacheOffsetX, this._cacheOffsetY, cacheCanvas.width/scale, cacheCanvas.height/scale);
		return true;
	}
	
	/**
	 * Applies this display object's transformation, alpha, globalCompositeOperation, clipping path (mask), and shadow to the specified
	 * context. This is typically called prior to draw.
	 * @method setupContext
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D to update.
	 **/
	p.updateContext = function(ctx) {
		var mtx, mask=this.mask, o=this;
		
		if (mask && mask.graphics) {
			mtx = mask.getMatrix(mask._matrix);
			ctx.transform(mtx.a,  mtx.b, mtx.c, mtx.d, mtx.tx, mtx.ty);
			
			mask.graphics.drawAsPath(ctx);
			ctx.clip();
			
			mtx.invert();
			ctx.transform(mtx.a,  mtx.b, mtx.c, mtx.d, mtx.tx, mtx.ty);
		}
		
		mtx = o._matrix.identity().appendTransform(o.x, o.y, o.scaleX, o.scaleY, o.rotation, o.skewX, o.skewY, o.regX, o.regY);
		// TODO: should be a better way to manage this setting. For now, using dynamic access to avoid circular dependencies:
		if (createjs["Stage"]._snapToPixelEnabled && o.snapToPixel) { ctx.transform(mtx.a,  mtx.b, mtx.c, mtx.d, mtx.tx+0.5|0, mtx.ty+0.5|0); }
		else { ctx.transform(mtx.a,  mtx.b, mtx.c, mtx.d, mtx.tx, mtx.ty); }
		ctx.globalAlpha *= o.alpha;
		if (o.compositeOperation) { ctx.globalCompositeOperation = o.compositeOperation; }
		if (o.shadow) { this._applyShadow(ctx, o.shadow); }
	}

	/**
	 * Draws the display object into a new canvas, which is then used for subsequent draws. For complex content
	 * that does not change frequently (ex. a Container with many children that do not move, or a complex vector Shape),
	 * this can provide for much faster rendering because the content does not need to be re-rendered each tick. The
	 * cached display object can be moved, rotated, faded, etc freely, however if it's content changes, you must manually
	 * update the cache by calling updateCache() or cache() again. You must specify the cache area via the x, y, w,
	 * and h parameters. This defines the rectangle that will be rendered and cached using this display object's
	 * coordinates. For example if you defined a Shape that drew a circle at 0, 0 with a radius of 25, you could call
	 * myShape.cache(-25, -25, 50, 50) to cache the full shape.
	 * @method cache
	 * @param {Number} x The x coordinate origin for the cache region.
	 * @param {Number} y The y coordinate origin for the cache region.
	 * @param {Number} width The width of the cache region.
	 * @param {Number} height The height of the cache region.
	 * @param {Number} scale Optional. The scale at which the cache will be created. For example, if you cache a vector shape using
	 * 	myShape.cache(0,0,100,100,2) then the resulting cacheCanvas will be 200x200 px. This lets you scale and rotate
	 * 	cached elements with greater fidelity. Default is 1.
	 **/
	p.cache = function(x, y, width, height, scale) {
		// draw to canvas.
		scale = scale||1;
		if (!this.cacheCanvas) { this.cacheCanvas = createjs.createCanvas?createjs.createCanvas():document.createElement("canvas"); }
		this.cacheCanvas.width = Math.ceil(width*scale);
		this.cacheCanvas.height = Math.ceil(height*scale);
		this._cacheOffsetX = x;
		this._cacheOffsetY = y;
		this._cacheScale = scale||1;
		this.updateCache();
	}

	/**
	 * Redraws the display object to its cache. Calling updateCache without an active cache will throw an error.
	 * If compositeOperation is null the current cache will be cleared prior to drawing. Otherwise the display object
	 * will be drawn over the existing cache using the specified compositeOperation.
	 * @method updateCache
	 * @param {String} compositeOperation The compositeOperation to use, or null to clear the cache and redraw it.
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#compositing">
	 * whatwg spec on compositing</a>.
	 **/
	p.updateCache = function(compositeOperation) {
		var cacheCanvas = this.cacheCanvas, offX = this._cacheOffsetX, offY = this._cacheOffsetY, scale = this._cacheScale;
		if (!cacheCanvas) { throw "cache() must be called before updateCache()"; }
		var ctx = cacheCanvas.getContext("2d");
		ctx.save();
		if (!compositeOperation) { ctx.clearRect(0, 0, cacheCanvas.width, cacheCanvas.height); }
		ctx.globalCompositeOperation = compositeOperation;
		ctx.setTransform(scale, 0, 0, scale, -offX, -offY);
		this.draw(ctx, true);
		this._applyFilters();
		ctx.restore();
		this.cacheID = DisplayObject._nextCacheID++;
	}

	/**
	 * Clears the current cache. See cache() for more information.
	 * @method uncache
	 **/
	p.uncache = function() {
		this._cacheDataURL = this.cacheCanvas = null;
		this.cacheID = this._cacheOffsetX = this._cacheOffsetY = 0;
		this._cacheScale = 1;
	}
	
	/**
	* Returns a data URL for the cache, or null if this display object is not cached.
	* Uses cacheID to ensure a new data URL is not generated if the cache has not changed.
	* @method getCacheDataURL.
	**/
	p.getCacheDataURL = function() {
		if (!this.cacheCanvas) { return null; }
		if (this.cacheID != this._cacheDataURLID) { this._cacheDataURL = this.cacheCanvas.toDataURL(); }
		return this._cacheDataURL;
	}

	/**
	 * Returns the stage that this display object will be rendered on, or null if it has not been added to one.
	 * @method getStage
	 * @return {Stage} The Stage instance that the display object is a descendent of. null if the DisplayObject has not
	 * been added to a Stage.
	 **/
	p.getStage = function() {
		var o = this;
		while (o.parent) {
			o = o.parent;
		}
		// using dynamic access to avoid circular dependencies;
		if (o instanceof createjs["Stage"]) { return o; }
		return null;
	}

	/**
	 * Transforms the specified x and y position from the coordinate space of the display object
	 * to the global (stage) coordinate space. For example, this could be used to position an HTML label
	 * over a specific point on a nested display object. Returns a Point instance with x and y properties
	 * correlating to the transformed coordinates on the stage.
	 * @method localToGlobal
	 * @param {Number} x The x position in the source display object to transform.
	 * @param {Number} y The y position in the source display object to transform.
	 * @return {Point} A Point instance with x and y properties correlating to the transformed coordinates
	 * on the stage.
	 **/
	p.localToGlobal = function(x, y) {
		var mtx = this.getConcatenatedMatrix(this._matrix);
		if (mtx == null) { return null; }
		mtx.append(1, 0, 0, 1, x, y);
		return new createjs.Point(mtx.tx, mtx.ty);
	}

	/**
	 * Transforms the specified x and y position from the global (stage) coordinate space to the
	 * coordinate space of the display object. For example, this could be used to determine
	 * the current mouse position within the display object. Returns a Point instance with x and y properties
	 * correlating to the transformed position in the display object's coordinate space.
	 * @method globalToLocal
	 * @param {Number} x The x position on the stage to transform.
	 * @param {Number} y The y position on the stage to transform.
	 * @return {Point} A Point instance with x and y properties correlating to the transformed position in the
	 * display object's coordinate space.
	 **/
	p.globalToLocal = function(x, y) {
		var mtx = this.getConcatenatedMatrix(this._matrix);
		if (mtx == null) { return null; }
		mtx.invert();
		mtx.append(1, 0, 0, 1, x, y);
		return new createjs.Point(mtx.tx, mtx.ty);
	}

	/**
	 * Transforms the specified x and y position from the coordinate space of this display object to the
	 * coordinate space of the target display object. Returns a Point instance with x and y properties
	 * correlating to the transformed position in the target's coordinate space. Effectively the same as calling
	 * var pt = this.localToGlobal(x, y); pt = target.globalToLocal(pt.x, pt.y);
	 * @method localToLocal
	 * @param {Number} x The x position in the source display object to transform.
	 * @param {Number} y The y position on the stage to transform.
	 * @param {DisplayObject} target The target display object to which the coordinates will be transformed.
	 * @return {Point} Returns a Point instance with x and y properties correlating to the transformed position
	 * in the target's coordinate space.
	 **/
	p.localToLocal = function(x, y, target) {
		var pt = this.localToGlobal(x, y);
		return target.globalToLocal(pt.x, pt.y);
	}

	/**
	 * Shortcut method to quickly set the transform properties on the display object. All parameters are optional.
	 * Omitted parameters will have the default value set (ex. 0 for x/y, 1 for scaleX/Y).
	 * @method setTransform
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Number} scaleX
	 * @param {Number} scaleY
	 * @param {Number} rotation
	 * @param {Number} skewX
	 * @param {Number} skewY
	 * @param {Number} regX
	 * @param {Number} regY
	 * @return {DisplayObject} Returns this instance. Useful for chaining commands.
	*/
	p.setTransform = function(x, y, scaleX, scaleY, rotation, skewX, skewY, regX, regY) {
		this.x = x || 0;
		this.y = y || 0;
		this.scaleX = scaleX == null ? 1 : scaleX;
		this.scaleY = scaleY == null ? 1 : scaleY;
		this.rotation = rotation || 0;
		this.skewX = skewX || 0;
		this.skewY = skewY || 0;
		this.regX = regX || 0;
		this.regY = regY || 0;
		return this;
	}
	
	/**
	 * Returns a matrix based on this object's transform.
	 * @method getMatrix
	 * @param {Matrix2D} matrix Optional. A Matrix2D object to populate with the calculated values. If null, a new
	 * Matrix object is returned.
	 * @return {Matrix2D} A matrix representing this display object's transform.
	 **/
	p.getMatrix = function(matrix) {
		var o = this;
		return (matrix ? matrix.identity() : new createjs.Matrix2D()).appendTransform(o.x, o.y, o.scaleX, o.scaleY, o.rotation, o.skewX, o.skewY, o.regX, o.regY).appendProperties(o.alpha, o.shadow, o.compositeOperation);
	}
	
	/**
	 * Generates a concatenated Matrix2D object representing the combined transform of
	 * the display object and all of its parent Containers up to the highest level ancestor
	 * (usually the stage). This can be used to transform positions between coordinate spaces,
	 * such as with localToGlobal and globalToLocal.
	 * @method getConcatenatedMatrix
	 * @param {Matrix2D} mtx Optional. A Matrix2D object to populate with the calculated values. If null, a new
	 * Matrix object is returned.
	 * @return {Matrix2D} a concatenated Matrix2D object representing the combined transform of
	 * the display object and all of its parent Containers up to the highest level ancestor (usually the stage).
	 **/
	p.getConcatenatedMatrix = function(matrix) {
		if (matrix) { matrix.identity(); }
		else { matrix = new createjs.Matrix2D(); }
		var o = this;
		while (o != null) {
			matrix.prependTransform(o.x, o.y, o.scaleX, o.scaleY, o.rotation, o.skewX, o.skewY, o.regX, o.regY).prependProperties(o.alpha, o.shadow, o.compositeOperation);
			o = o.parent;
		}
		return matrix;
	}

	/**
	 * Tests whether the display object intersects the specified local point (ie. draws a pixel with alpha > 0 at
	 * the specified position). This ignores the alpha, shadow and compositeOperation of the display object, and all
	 * transform properties including regX/Y.
	 * @method hitTest
	 * @param {Number} x The x position to check in the display object's local coordinates.
	 * @param {Number} y The y position to check in the display object's local coordinates.
	 * @return {Boolean} A Boolean indicting whether a visible portion of the DisplayObject intersect the specified
	 * local Point.
	*/
	p.hitTest = function(x, y) {
		var ctx = DisplayObject._hitTestContext;
		var canvas = DisplayObject._hitTestCanvas;

		ctx.setTransform(1,  0, 0, 1, -x, -y);
		this.draw(ctx);

		var hit = this._testHit(ctx);

		canvas.width = 0;
		canvas.width = 1;
		return hit;
	}

	/**
	 * Returns a clone of this DisplayObject. Some properties that are specific to this instance's current context are
	 * reverted to their defaults (for example .parent).
	 * @method clone
	 * @return {DisplayObject} A clone of the current DisplayObject instance.
	 **/
	p.clone = function() {
		var o = new DisplayObject();
		this.cloneProps(o);
		return o;
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[DisplayObject (name="+  this.name +")]";
	}

// private methods:

	// separated so it can be used more easily in subclasses:
	/**
	 * @method cloneProps
	 * @protected
	 * @param {DisplayObject} o The DisplayObject instance which will have properties from the current DisplayObject
	 * instance copied into.
	 **/
	p.cloneProps = function(o) {
		o.alpha = this.alpha;
		o.name = this.name;
		o.regX = this.regX;
		o.regY = this.regY;
		o.rotation = this.rotation;
		o.scaleX = this.scaleX;
		o.scaleY = this.scaleY;
		o.shadow = this.shadow;
		o.skewX = this.skewX;
		o.skewY = this.skewY;
		o.visible = this.visible;
		o.x  = this.x;
		o.y = this.y;
		o.mouseEnabled = this.mouseEnabled;
		o.compositeOperation = this.compositeOperation;
		if (this.cacheCanvas) {
			o.cacheCanvas = this.cacheCanvas.cloneNode(true);
			o.cacheCanvas.getContext("2d").putImageData(this.cacheCanvas.getContext("2d").getImageData(0,0,this.cacheCanvas.width,this.cacheCanvas.height),0,0);
		}
	}

	/**
	 * @method _applyShadow
	 * @protected
	 * @param {CanvasRenderingContext2D} ctx
	 * @param {Shadow} shadow
	 **/
	p._applyShadow = function(ctx, shadow) {
		shadow = shadow || Shadow.identity;
		ctx.shadowColor = shadow.color;
		ctx.shadowOffsetX = shadow.offsetX;
		ctx.shadowOffsetY = shadow.offsetY;
		ctx.shadowBlur = shadow.blur;
	}
	
	
	/**
	 * @method _tick
	 * @protected
	 **/
	p._tick = function(params) {
		// because onTick can be really performance sensitive, we'll inline some of the dispatchEvent work.
		// this can probably go away at some point. It only has a noticeable impact with thousands of objects in modern browsers.
		this.onTick&&this.onTick.apply(this, params);
		var ls = this._listeners;
		if (ls&&ls["tick"]) { this.dispatchEvent({type:"tick",params:params}); }
		
		// otherwise, we'd just do this:
		//this.dispatchEvent("tick", this.onTick, params);
	}

	/**
	 * @method _testHit
	 * @protected
	 * @param {CanvasRenderingContext2D} ctx
	 * @return {Boolean}
	 **/
	p._testHit = function(ctx) {
		try {
			var hit = ctx.getImageData(0, 0, 1, 1).data[3] > 1;
		} catch (e) {
			if (!DisplayObject.suppressCrossDomainErrors) {
				throw "An error has occurred. This is most likely due to security restrictions on reading canvas pixel data with local or cross-domain images.";
			}
		}
		return hit;
	}

	/**
	 * @method _applyFilters
	 * @protected
	 **/
	p._applyFilters = function() {
		if (!this.filters || this.filters.length == 0 || !this.cacheCanvas) { return; }
		var l = this.filters.length;
		var ctx = this.cacheCanvas.getContext("2d");
		var w = this.cacheCanvas.width;
		var h = this.cacheCanvas.height;
		for (var i=0; i<l; i++) {
			this.filters[i].applyFilter(ctx, 0, 0, w, h);
		}
	};
	
	/**
	 * Indicates whether the display object has a mouse listener of the corresponding types.
	 * @method _hasMouseHandler
	 * @param {Number} typeMask A bitmask indicating which mouseEvent types to look for. Bit 1 specifies onPress &
	 * onClick & onDoubleClick, bit 2 specifies it should look for onMouseOver and onMouseOut. This implementation may change.
	 * @return {Boolean}
	 * @protected
	 **/
	p._hasMouseHandler = function(typeMask) {
		var ls = this._listeners;
		return !!(
				 (typeMask&1 && (this.onPress || this.onClick || this.onDoubleClick || 
				 (ls && (this.hasEventListener("press") || this.hasEventListener("click") || this.hasEventListener("doubleClick")))))
				 ||
				 (typeMask&2 && (this.onMouseOver || this.onMouseOut || this.cursor ||
				 (ls && (this.hasEventListener("mouseOver") || this.hasEventListener("mouseOut")))))
				 );
	};
	 

createjs.DisplayObject = DisplayObject;
}());/*
* Container
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* A Container is a nestable display lists that allows you to work with compound display elements. For
* example you could group arm, leg, torso and head Bitmaps together into a Person Container, and
* transform them as a group, while still being able to move the individual parts relative to each
* other. Children of containers have their transform and alpha properties concatenated with their
* parent Container. For example, a Shape with x=100 and alpha=0.5, placed in a Container with
* x=50 and alpha=0.7 will be rendered to the canvas at x=150 and alpha=0.35. Containers have some
* overhead, so you generally shouldn't create a Container to hold a single child.
* @class Container
* @extends DisplayObject
* @constructor
**/
var Container = function() {
  this.initialize();
}
var p = Container.prototype = new createjs.DisplayObject();

// public properties:
	/**
	 * The array of children in the display list. You should usually use the child management methods,
	 * rather than accessing this directly, but it is included for advanced users.
	 * @property children
	 * @type Array[DisplayObject]
	 * @default null
	 **/
	p.children = null;

// constructor:

	/**
	 * @property DisplayObject_initialize
	 * @type Function
	 * @private
	 **/
	p.DisplayObject_initialize = p.initialize;

	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function() {
		this.DisplayObject_initialize();
		this.children = [];
	}

// public methods:

	/**
	 * Returns true or false indicating whether the display object would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the display object would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		return this.visible && this.alpha > 0 && this.children.length && this.scaleX != 0 && this.scaleY != 0;
	}

	/**
	 * @property DisplayObject_draw
	 * @type Function
	 * @private
	 **/
	p.DisplayObject_draw = p.draw;

	/**
	 * Draws the display object into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache.
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		if (this.DisplayObject_draw(ctx, ignoreCache)) { return true; }
		
		// this ensures we don't have issues with display list changes that occur during a draw:
		var list = this.children.slice(0);
		for (var i=0,l=list.length; i<l; i++) {
			var child = list[i];
			if (!child.isVisible()) { continue; }
			
			// draw the child:
			ctx.save();
			child.updateContext(ctx);
			child.draw(ctx);
			ctx.restore();
		}
		return true;
	}
	
	/**
	 * Adds a child to the top of the display list. You can also add multiple children, such as "addChild(child1, child2, ...);".
	 * Returns the child that was added, or the last child if multiple children were added.
	 * @method addChild
	 * @param {DisplayObject} child The display object to add.
	 * @return {DisplayObject} The child that was added, or the last child if multiple children were added.
	 **/
	p.addChild = function(child) {
		if (child == null) { return child; }
		var l = arguments.length;
		if (l > 1) {
			for (var i=0; i<l; i++) { this.addChild(arguments[i]); }
			return arguments[l-1];
		}
		if (child.parent) { child.parent.removeChild(child); }
		child.parent = this;
		this.children.push(child);
		return child;
	}

	/**
	 * Adds a child to the display list at the specified index, bumping children at equal or greater indexes up one, and setting
	 * its parent to this Container. You can also add multiple children, such as "addChildAt(child1, child2, ..., index);". The
	 * index must be between 0 and numChildren. For example, to add myShape under otherShape in the display list, you could use:
	 * container.addChildAt(myShape, container.getChildIndex(otherShape)). This would also bump otherShape's index up by one.
	 * Returns the last child that was added, or the last child if multiple children were added. Fails silently if the index 
	 * is out of range.
	 * @method addChildAt
	 * @param {DisplayObject} child The display object to add.
	 * @param {Number} index The index to add the child at.
	 * @return {DisplayObject} The child that was added, or the last child if multiple children were added.
	 **/
	p.addChildAt = function(child, index) {
		var l = arguments.length;
		var indx = arguments[l-1]; // can't use the same name as the index param or it replaces arguments[1]
		if (indx < 0 || indx > this.children.length) { return arguments[l-2]; }
		if (l > 2) {
			for (var i=0; i<l-1; i++) { this.addChildAt(arguments[i], indx+i); }
			return arguments[l-2];
		}
		if (child.parent) { child.parent.removeChild(child); }
		child.parent = this;
		this.children.splice(index, 0, child);
		return child;
	}

	/**
	 * Removes the specified child from the display list. Note that it is faster to use removeChildAt() if the index is already
	 * known. You can also remove multiple children, such as "removeChild(child1, child2, ...);". Returns true if the child
	 * (or children) was removed, or false if it was not in the display list.
	 * @method removeChild
	 * @param {DisplayObject} child The child to remove.
	 * @return {Boolean} true if the child (or children) was removed, or false if it was not in the display list.
	 **/
	p.removeChild = function(child) {
		var l = arguments.length;
		if (l > 1) {
			var good = true;
			for (var i=0; i<l; i++) { good = good && this.removeChild(arguments[i]); }
			return good;
		}
		return this.removeChildAt(this.children.indexOf(child));
	}

	/**
	 * Removes the child at the specified index from the display list, and sets its parent to null. You can also remove multiple
	 * children, such as "removeChildAt(2, 7, ...);". Returns true if the child (or children) was removed, or false if any index
	 * was out of range.
	 * @param {Number} index The index of the child to remove.
	 * @return {Boolean} true if the child (or children) was removed, or false if any index was out of range.
	 **/
	p.removeChildAt = function(index) {
		var l = arguments.length;
		if (l > 1) {
			var a = [];
			for (var i=0; i<l; i++) { a[i] = arguments[i]; }
			a.sort(function(a, b) { return b-a; });
			var good = true;
			for (var i=0; i<l; i++) { good = good && this.removeChildAt(a[i]); }
			return good;
		}
		if (index < 0 || index > this.children.length-1) { return false; }
		var child = this.children[index];
		if (child) { child.parent = null; }
		this.children.splice(index, 1);
		return true;
	}

	/**
	 * Removes all children from the display list.
	 * @method removeAllChildren
	 **/
	p.removeAllChildren = function() {
		var kids = this.children;
		while (kids.length) { kids.pop().parent = null; }
	}

	/**
	 * Returns the child at the specified index.
	 * @method getChildAt
	 * @param {Number} index The index of the child to return.
	 * @return {DisplayObject} The child at the specified index.
	 **/
	p.getChildAt = function(index) {
		return this.children[index];
	}
	
	/**
	 * Returns the child with the specified name.
	 * @method getChildByName
	 * @param {String} name The name of the child to return.
	 * @return {DisplayObject} The child with the specified name.
	 **/
	p.getChildByName = function(name) {
		var kids = this.children;
		for (var i=0,l=kids.length;i<l;i++) {
			if(kids[i].name == name) { return kids[i]; }
		}
		return null;
	}

	/**
	 * Performs an array sort operation on the child list.
	 * @method sortChildren
	 * @param {Function} sortFunction the function to use to sort the child list. See javascript's Array.sort documentation
	 * for details.
	 **/
	p.sortChildren = function(sortFunction) {
		this.children.sort(sortFunction);
	}

	/**
	 * Returns the index of the specified child in the display list, or -1 if it is not in the display list.
	 * @method getChildIndex
	 * @param {DisplayObject} child The child to return the index of.
	 * @return {Number} The index of the specified child. -1 if the child is not found.
	 **/
	p.getChildIndex = function(child) {
		return this.children.indexOf(child);
	}

	/**
	 * Returns the number of children in the display list.
	 * @method getNumChildren
	 * @return {Number} The number of children in the display list.
	 **/
	p.getNumChildren = function() {
		return this.children.length;
	}
	
	/**
	 * Swaps the children at the specified indexes. Fails silently if either index is out of range.
	 * @param {Number} index1
	 * @param {Number} index2
	 * @method swapChildrenAt
	 **/
	p.swapChildrenAt = function(index1, index2) {
		var kids = this.children;
		var o1 = kids[index1];
		var o2 = kids[index2];
		if (!o1 || !o2) { return; }
		kids[index1] = o2;
		kids[index2] = o1;
	}
	
	/**
	 * Swaps the specified children's depth in the display list. Fails silently if either child is not a child of this Container.
	 * @param {DisplayObject} child1
	 * @param {DisplayObject} child2
	 * @method swapChildren
	 **/
	p.swapChildren = function(child1, child2) {
		var kids = this.children;
		var index1,index2;
		for (var i=0,l=kids.length;i<l;i++) {
			if (kids[i] == child1) { index1 = i; }
			if (kids[i] == child2) { index2 = i; }
			if (index1 != null && index2 != null) { break; }
		}
		if (i==l) { return; } // TODO: throw error?
		kids[index1] = child2;
		kids[index2] = child1;
	}
	
	/**
	 * Changes the depth of the specified child. Fails silently if the child is not a child of this container, or the index is out of range.
	 * @param {DisplayObject} child
	 * @param {Number} index  
	 * @method setChildIndex
	 **/
	p.setChildIndex = function(child, index) {
		var kids = this.children, l=kids.length;
		if (child.parent != this || index < 0 || index >= l) { return; }
		for (var i=0;i<l;i++) {
			if (kids[i] == child) { break; }
		}
		if (i==l || i == index) { return; }
		kids.splice(i,1);
		if (index<i) { index--; }
		kids.splice(index,0,child);
	}

	/**
	 * Returns true if the specified display object either is this container or is a descendent.
	 * (child, grandchild, etc) of this container.
	 * @method contains
	 * @param {DisplayObject} child The DisplayObject to be checked.
	 * @return {Boolean} true if the specified display object either is this container or is a descendent.
	 **/
	p.contains = function(child) {
		while (child) {
			if (child == this) { return true; }
			child = child.parent;
		}
		return false;
	}

	/**
	 * Tests whether the display object intersects the specified local point (ie. draws a pixel with alpha > 0 at the specified
	 * position). This ignores the alpha, shadow and compositeOperation of the display object, and all transform properties
	 * including regX/Y.
	 * @method hitTest
	 * @param {Number} x The x position to check in the display object's local coordinates.
	 * @param {Number} y The y position to check in the display object's local coordinates.
	 * @return {Boolean} A Boolean indicating whether there is a visible section of a DisplayObject that overlaps the specified
	 * coordinates.
	 **/
	p.hitTest = function(x, y) {
		// TODO: optimize to use the fast cache check where possible.
		return (this.getObjectUnderPoint(x, y) != null);
	}

	/**
	 * Returns an array of all display objects under the specified coordinates that are in this container's display list.
	 * This routine ignores any display objects with mouseEnabled set to false. The array will be sorted in order of visual
	 * depth, with the top-most display object at index 0. This uses shape based hit detection, and can be an expensive operation
	 * to run, so it is best to use it carefully. For example, if testing for objects under the mouse, test on tick (instead of on
	 * mousemove), and only if the mouse's position has changed.
	 * @method getObjectsUnderPoint
	 * @param {Number} x The x position in the container to test.
	 * @param {Number} y The y position in the container to test.
	 * @return {Array[DisplayObject]} An Array of DisplayObjects under the specified coordinates.
	 **/
	p.getObjectsUnderPoint = function(x, y) {
		var arr = [];
		var pt = this.localToGlobal(x, y);
		this._getObjectsUnderPoint(pt.x, pt.y, arr);
		return arr;
	}

	/**
	 * Similar to getObjectsUnderPoint(), but returns only the top-most display object. This runs significantly faster than
	 * getObjectsUnderPoint(), but is still an expensive operation. See getObjectsUnderPoint() for more information.
	 * @method getObjectUnderPoint
	 * @param {Number} x The x position in the container to test.
	 * @param {Number} y The y position in the container to test.
	 * @return {DisplayObject} The top-most display object under the specified coordinates.
	 **/
	p.getObjectUnderPoint = function(x, y) {
		var pt = this.localToGlobal(x, y);
		return this._getObjectsUnderPoint(pt.x, pt.y);
	}

	/**
	 * Returns a clone of this Container. Some properties that are specific to this instance's current context are reverted to
	 * their defaults (for example .parent).
	 * @param {Boolean} recursive If true, all of the descendants of this container will be cloned recursively. If false, the
	 * properties of the container will be cloned, but the new instance will not have any children.
	 * @return {Container} A clone of the current Container instance.
	 **/
	p.clone = function(recursive) {
		var o = new Container();
		this.cloneProps(o);
		if (recursive) {
			var arr = o.children = [];
			for (var i=0, l=this.children.length; i<l; i++) {
				var clone = this.children[i].clone(recursive);
				clone.parent = o;
				arr.push(clone);
			}
		}
		return o;
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Container (name="+  this.name +")]";
	}

// private properties:
	/**
	 * @property DisplayObject__tick
	 * @type Function
	 * @private
	 **/
	p.DisplayObject__tick = p._tick;
	
	/**
	 * @method _tick
	 * @protected
	 **/
	p._tick = function(params) {
		for (var i=this.children.length-1; i>=0; i--) {
			var child = this.children[i];
			if (child._tick) { child._tick(params); }
		}
		this.DisplayObject__tick(params);
	}

	/**
	 * @method _getObjectsUnderPoint
	 * @param {Number} x
	 * @param {Number} y
	 * @param {Array} arr
	 * @param {Number} mouseEvents A bitmask indicating which mouseEvent types to look for. Bit 1 specifies onPress &
	 * onClick & onDoubleClick, bit 2 specifies it should look for onMouseOver and onMouseOut. This implementation may change.
	 * @return {Array[DisplayObject]}
	 * @protected
	 **/
	p._getObjectsUnderPoint = function(x, y, arr, mouseEvents) {
		var ctx = createjs.DisplayObject._hitTestContext;
		var canvas = createjs.DisplayObject._hitTestCanvas;
		var mtx = this._matrix;
		var hasHandler = this._hasMouseHandler(mouseEvents);

		// if we have a cache handy & this has a handler, we can use it to do a quick check.
		// we can't use the cache for screening children, because they might have hitArea set.
		if (this.cacheCanvas && hasHandler) {
			this.getConcatenatedMatrix(mtx);
			ctx.setTransform(mtx.a,  mtx.b, mtx.c, mtx.d, mtx.tx-x, mtx.ty-y);
			ctx.globalAlpha = mtx.alpha;
			this.draw(ctx);
			if (this._testHit(ctx)) {
				canvas.width = 0;
				canvas.width = 1;
				return this;
			}
		};

		// draw children one at a time, and check if we get a hit:
		var l = this.children.length;
		for (var i=l-1; i>=0; i--) {
			var child = this.children[i];
			if (!child.isVisible() || !child.mouseEnabled) { continue; }
			var hitArea = child.hitArea;
			var childHasHandler;
			
			// if a child container has a handler and a hitArea then we only need to check its hitArea:
			if (child instanceof Container && !(hitArea && (childHasHandler = child._hasMouseHandler(mouseEvents)))) {
				var result;
				if (hasHandler) {
					// only concerned about the first hit, because this container is going to claim it anyway:
					result = child._getObjectsUnderPoint(x, y);
					if (result) { return this; }
				} else {
					result = child._getObjectsUnderPoint(x, y, arr, mouseEvents);
					if (!arr && result) { return result; }
				}
			} else if (!mouseEvents || hasHandler || (childHasHandler || child._hasMouseHandler(mouseEvents))) {
				child.getConcatenatedMatrix(mtx);
				
				if (hitArea) {
					mtx.appendTransform(hitArea.x, hitArea.y, hitArea.scaleX, hitArea.scaleY, hitArea.rotation, hitArea.skewX, hitArea.skewY, hitArea.regX, hitArea.regY);
					mtx.alpha *= hitArea.alpha/child.alpha;
				}
				
				ctx.globalAlpha = mtx.alpha;
				ctx.setTransform(mtx.a,  mtx.b, mtx.c, mtx.d, mtx.tx-x, mtx.ty-y);
				(hitArea||child).draw(ctx);
				if (!this._testHit(ctx)) { continue; }
				canvas.width = 0;
				canvas.width = 1;
				if (hasHandler) { return this; }
				else if (arr) { arr.push(child); }
				else { return child; }
			}
		}
		return null;
	};

createjs.Container = Container;
}());/*
* Stage
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* A stage is the root level Container for a display list. Each time its tick method is called, it will render its display
* list to its target canvas.
* @class Stage
* @extends Container
* @constructor
* @param {HTMLCanvasElement | String | Object} canvas A canvas object that the Stage will render to, or the string id of a canvas object in the current document.
**/
var Stage = function(canvas) {
  this.initialize(canvas);
}
var p = Stage.prototype = new createjs.Container();

// static properties:
	/**
	 * @property _snapToPixelEnabled
	 * @protected
	 * @static
	 * @type Boolean
	 * @default false
	 **/
	Stage._snapToPixelEnabled = false; // snapToPixelEnabled is temporarily copied here during a draw to provide global access.

// public properties:
	/**
	 * Indicates whether the stage should automatically clear the canvas before each render. You can set this to false to manually
	 * control clearing (for generative art, or when pointing multiple stages at the same canvas for example).
	 * @property autoClear
	 * @type Boolean
	 * @default true
	 **/
	p.autoClear = true;

	/**
	 * The canvas the stage will render to. Multiple stages can share a single canvas, but you must disable autoClear for all but the
	 * first stage that will be ticked (or they will clear each other's render).
	 * @property canvas
	 * @type HTMLCanvasElement | Object
	 **/
	p.canvas = null;

	/**
	 * READ-ONLY. The current mouse X position on the canvas. If the mouse leaves the canvas, this will indicate the most recent
	 * position over the canvas, and mouseInBounds will be set to false.
	 * @property mouseX
	 * @type Number
	 **/
	p.mouseX = 0;

	/**
	 * READ-ONLY. The current mouse Y position on the canvas. If the mouse leaves the canvas, this will indicate the most recent
	 * position over the canvas, and mouseInBounds will be set to false.
	 * @property mouseY
	 * @type Number
	 **/
	p.mouseY = 0;

	/**
	 * The onMouseMove callback is called when the user moves the mouse over the canvas.  The handler is passed a single param
	 * containing the corresponding MouseEvent instance.
	 * @event onMouseMove
	 * @param {MouseEvent} event A MouseEvent instance with information about the current mouse event.
	 **/
	p.onMouseMove = null;

	/**
	 * The onMouseUp callback is called when the user releases the mouse button anywhere that the page can detect it.  The handler
	 * is passed a single param containing the corresponding MouseEvent instance.
	 * @event onMouseUp
	 * @param {MouseEvent} event A MouseEvent instance with information about the current mouse event.
	 **/
	p.onMouseUp = null;

	/**
	 * The onMouseDown callback is called when the user presses the mouse button over the canvas.  The handler is passed a single
	 * param containing the corresponding MouseEvent instance.
	 * @event onMouseDown
	 * @param {MouseEvent} event A MouseEvent instance with information about the current mouse event.
	 **/
	p.onMouseDown = null;

	/**
	 * Indicates whether this stage should use the snapToPixel property of display objects when rendering them. See
	 * DisplayObject.snapToPixel for more information.
	 * @property snapToPixelEnabled
	 * @type Boolean
	 * @default false
	 **/
	p.snapToPixelEnabled = false;

	/**
	 * Indicates whether the mouse is currently within the bounds of the canvas.
	 * @property mouseInBounds
	 * @type Boolean
	 * @default false
	 **/
	p.mouseInBounds = false;

	/**
	 * If true, tick callbacks will be called on all display objects on the stage prior to rendering to the canvas.
	 * You can call 
	 * @property tickOnUpdate
	 * @type Boolean
	 * @default true
	 **/
	p.tickOnUpdate = true;
	
	/**
	 * If true, onMouseMove handlers will continue to be called when the mouse leaves the target canvas. See
	 * mouseInBounds, and MouseEvent.x/y/rawX/rawY.
	 * @property mouseMoveOutside
	 * @type Boolean
	 * @default false
	 **/
	p.mouseMoveOutside = false;

// private properties:

	/**
	 * Holds objects with data for each active pointer id. Each object has the following properties:
	 * x, y, event, target, overTarget, overX, overY, inBounds
	 * @property _pointerData
	 * @type {Object}
	 * @private
	 */
	p._pointerData = null;
	
	/**
	 * Number of active pointers.
	 * @property _pointerCount
	 * @type {Object}
	 * @private
	 */
	p._pointerCount = 0;
	
	/**
	 * Number of active pointers.
	 * @property _pointerCount
	 * @type {Object}
	 * @private
	 */
	p._primaryPointerID = null;

	/**
	 * @property _mouseOverIntervalID
	 * @protected
	 * @type Number
	 **/
	p._mouseOverIntervalID = null;

// constructor:
	/**
	 * @property DisplayObject_initialize
	 * @type Function
	 * @private
	 **/
	p.Container_initialize = p.initialize;

	/**
	 * Initialization method.
	 * @method initialize
	 * @param {HTMLCanvasElement | String | Object} canvas A canvas object, or the string id of a canvas object in the current document.
	 * @protected
	 **/
	p.initialize = function(canvas) {
		this.Container_initialize();
		this.canvas = (typeof canvas == "string") ? document.getElementById(canvas) : canvas;
		this._pointerData = {};
		this._enableMouseEvents(true);
	}

// public methods:

	/**
	 * @event tick
	 * Broadcast to children when the stage is updated.
	 **/

	/**
	 * Each time the update method is called, the stage will tick any descendants exposing a tick method (ex. BitmapAnimation)
	 * and render its entire display list to the canvas. Any parameters passed to update will be passed on to any
	 * onTick handlers.
	 * @method update
	 **/
	p.update = function() {
		if (!this.canvas) { return; }
		if (this.autoClear) { this.clear(); }
		Stage._snapToPixelEnabled = this.snapToPixelEnabled;
		if (this.tickOnUpdate) { this._tick((arguments.length ? arguments : null)); }
		var ctx = this.canvas.getContext("2d");
		ctx.save();
		this.updateContext(ctx);
		this.draw(ctx, false);
		ctx.restore();
	}

	/**
	 * Calls the update method. Useful for adding stage as a listener to Ticker directly.
	 * @property tick
	 * @private
	 * @type Function
	 **/
	p.tick = p.update;

	/**
	 * Clears the target canvas. Useful if autoClear is set to false.
	 * @method clear
	 **/
	p.clear = function() {
		if (!this.canvas) { return; }
		var ctx = this.canvas.getContext("2d");
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
	}

	/**
	 * Returns a data url that contains a Base64 encoded image of the contents of the stage. The returned data url can be
	 * specified as the src value of an image element.
	 * @method toDataURL
	 * @param {String} backgroundColor The background color to be used for the generated image. The value can be any value HTML color
	 * value, including HEX colors, rgb and rgba. The default value is a transparent background.
	 * @param {String} mimeType The MIME type of the image format to be create. The default is "image/png". If an unknown MIME type
	 * is passed in, or if the browser does not support the specified MIME type, the default value will be used.
	 * @return {String} a Base64 encoded image.
	 **/
	p.toDataURL = function(backgroundColor, mimeType) {
		if(!mimeType) {
			mimeType = "image/png";
		}

		var ctx = this.canvas.getContext('2d');
		var w = this.canvas.width;
		var h = this.canvas.height;

		var data;

		if(backgroundColor) {

			//get the current ImageData for the canvas.
			data = ctx.getImageData(0, 0, w, h);

			//store the current globalCompositeOperation
			var compositeOperation = ctx.globalCompositeOperation;

			//set to draw behind current content
			ctx.globalCompositeOperation = "destination-over";

			//set background color
			ctx.fillStyle = backgroundColor;

			//draw background on entire canvas
			ctx.fillRect(0, 0, w, h);
		}

		//get the image data from the canvas
		var dataURL = this.canvas.toDataURL(mimeType);

		if(backgroundColor) {
			//clear the canvas
			ctx.clearRect (0, 0, w, h);

			//restore it with original settings
			ctx.putImageData(data, 0, 0);

			//reset the globalCompositeOperation to what it was
			ctx.globalCompositeOperation = compositeOperation;
		}

		return dataURL;
	}

	/**
	 * Enables or disables (by passing a frequency of 0) mouse over handlers (onMouseOver and onMouseOut) for this stage's display
	 * list. These events can be expensive to generate, so they are disabled by default, and the frequency of the events
	 * can be controlled independently of mouse move events via the optional frequency parameter.
	 * @method enableMouseOver
	 * @param {Number} frequency Optional param specifying the maximum number of times per second to broadcast mouse over/out events. Set to 0 to disable mouse
	 * over events completely. Maximum is 50. A lower frequency is less responsive, but uses less CPU. Default is 20.
	 **/
	p.enableMouseOver = function(frequency) {
		if (this._mouseOverIntervalID) {
			clearInterval(this._mouseOverIntervalID);
			this._mouseOverIntervalID = null;
		}
		if (frequency == null) { frequency = 20; }
		else if (frequency <= 0) { return; }
		var o = this;
		this._mouseOverIntervalID = setInterval(function(){ o._testMouseOver(); }, 1000/Math.min(50,frequency));
	}

	/**
	 * Returns a clone of this Stage.
	 * @return {Stage} A clone of the current Container instance.
	 **/
	p.clone = function() {
		var o = new Stage(null);
		this.cloneProps(o);
		return o;
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Stage (name="+  this.name +")]";
	}

	// private methods:

	/**
	 * @method _enableMouseEvents
	 * @protected
	 **/
	p._enableMouseEvents = function() {
		var o = this;
		var evtTarget = window.addEventListener ? window : document;
		evtTarget.addEventListener("mouseup", function(e) { o._handleMouseUp(e); }, false);
		evtTarget.addEventListener("mousemove", function(e) { o._handleMouseMove(e); }, false);
		evtTarget.addEventListener("dblclick", function(e) { o._handleDoubleClick(e); }, false);
		// this is to facilitate extending Stage:
		if (this.canvas) { this.canvas.addEventListener("mousedown", function(e) { o._handleMouseDown(e); }, false); }
	}
	
	/**
	 * @method _getPointerData
	 * @protected
	 * @param {Number} id
	 **/
	p._getPointerData = function(id) {
		var data = this._pointerData[id];
		if (!data) {
			data = this._pointerData[id] = {x:0,y:0};
			// if it's the mouse (id == NaN) or the first new touch, then make it the primary pointer id:
			if (this._primaryPointerID == null) { this._primaryPointerID = id; }
		}
		return data;
	}

	/**
	 * @method _handleMouseMove
	 * @protected
	 * @param {MouseEvent} e
	 **/
	p._handleMouseMove = function(e) {
		if(!e){ e = window.event; }
		this._handlePointerMove(-1, e, e.pageX, e.pageY);
	}
	
	/**
	 * @method _handlePointerMove
	 * @protected
	 * @param {Number} id
	 * @param {Event} e
	 * @param {Number} pageX
	 * @param {Number} pageY
	 **/
	p._handlePointerMove = function(id, e, pageX, pageY) {
		if (!this.canvas) { return; } // this.mouseX = this.mouseY = null;
		var o = this._getPointerData(id);

		var inBounds = o.inBounds;
		this._updatePointerPosition(id, pageX, pageY);
		if (!inBounds && !o.inBounds && !this.mouseMoveOutside) { return; }
		var evt = new createjs.MouseEvent("mouseMove", o.x, o.y, this, e, id, id == this._primaryPointerID, o.rawX, o.rawY);
	
		this.onMouseMove&&this.onMouseMove(evt);
		this._listeners&&this.dispatchEvent(evt);
		
		var oEvt = o.event;
		if (oEvt && (oEvt.onMouseMove || (oEvt._listeners && oEvt.hasEventListener("mouseMove")))) {
			evt = evt.clone(); // not sure this is entirely necessary.
			evt.target = o.event.target;
			oEvt.onMouseMove&&oEvt.onMouseMove(evt);
			oEvt._listeners&&oEvt.dispatchEvent(evt);
		}
	}

	/**
	 * @method _updatePointerPosition
	 * @protected
	 * @param {Number} id
	 * @param {Number} pageX
	 * @param {Number} pageY
	 **/
	p._updatePointerPosition = function(id, pageX, pageY) {
		var rect = this._getElementRect(this.canvas);
		pageX -= rect.left;
		pageY -= rect.top;
		
		var w = this.canvas.width;
		var h = this.canvas.height;
		pageX /= (rect.right-rect.left)/w;
		pageY /= (rect.bottom-rect.top)/h;
		var o = this._getPointerData(id);
		if (o.inBounds = (pageX >= 0 && pageY >= 0 && pageX <= w-1 && pageY <= h-1)) {
			o.x = pageX;
			o.y = pageY;
		} else if (this.mouseMoveOutside) {
			o.x = pageX < 0 ? 0 : (pageX > w-1 ? w-1 : pageX);
			o.y = pageY < 0 ? 0 : (pageY > h-1 ? h-1 : pageY);
		}
		
		o.rawX = pageX;
		o.rawY = pageY;
		
		if (id == this._primaryPointerID) {
			this.mouseX = o.x;
			this.mouseY = o.y;
			this.mouseInBounds = o.inBounds;
		}
	}
	
	/**
	 * @method _getElementRect
	 * @protected
	 * @param {HTMLElement} e
	 **/
	p._getElementRect = function(e) {
		var bounds = e.getBoundingClientRect();
		var offX = (window.pageXOffset || document.scrollLeft || 0) - (document.clientLeft || document.body.clientLeft || 0);
		var offY = (window.pageYOffset || document.scrollTop || 0) - (document.clientTop  || document.body.clientTop  || 0);
		
		var styles = window.getComputedStyle ? getComputedStyle(e) : e.currentStyle; // IE <9 compatibility.
		var padL = parseInt(styles.paddingLeft)+parseInt(styles.borderLeftWidth);
		var padT = parseInt(styles.paddingTop)+parseInt(styles.borderTopWidth);
		var padR = parseInt(styles.paddingRight)+parseInt(styles.borderRightWidth);
		var padB = parseInt(styles.paddingBottom)+parseInt(styles.borderBottomWidth);
		
		// note: in some browsers bounds properties are read only.
		return {
			left: bounds.left+offX+padL,
			right: bounds.right+offX-padR,
			top: bounds.top+offY+padT,
			bottom: bounds.bottom+offY-padB
		}
	}

	/**
	 * @method _handleMouseUp
	 * @protected
	 * @param {MouseEvent} e
	 **/
	p._handleMouseUp = function(e) {
		this._handlePointerUp(-1, e, false);
	}
	
	/**
	 * @method _handlePointerUp
	 * @protected
	 * @param {Number} id
	 * @param {Event} e
	 * @param {Boolean} clear
	 **/
	p._handlePointerUp = function(id, e, clear) {
		var o = this._getPointerData(id);
		
		var evt = new createjs.MouseEvent("mouseUp", o.x, o.y, this, e, id, id==this._primaryPointerID, o.rawX, o.rawY);
		this.onMouseUp&&this.onMouseUp(evt);
		if (this._listeners) { this.dispatchEvent(evt); }
		
		var oEvt = o.event;
		if (oEvt) {
			evt = evt.clone(); // not sure this is entirely necessary.
			evt.target = oEvt.target;
			oEvt.onMouseUp&&oEvt.onMouseUp(evt);
			oEvt._listeners&&oEvt.dispatchEvent(evt);
		}
		
		var oTarget = o.target;
		if (oTarget && (oTarget.onClick  || (oTarget._listeners && oTarget.hasEventListener("click"))) && this._getObjectsUnderPoint(o.x, o.y, null, true, (this._mouseOverIntervalID ? 3 : 1)) == oTarget) {
			evt = new createjs.MouseEvent("click", o.x, o.y, oTarget, e, id, id==this._primaryPointerID, o.rawX, o.rawY);
			oTarget.onClick&&oTarget.onClick(evt);
			oTarget._listeners&&oTarget.dispatchEvent(evt);
		}
		
		if (clear) {
			if (id == this._primaryPointerID) { this._primaryPointerID = null; }
			delete(this._pointerData[id]);
		} else { o.event = o.target = null; }
	}

	/**
	 * @method _handleMouseDown
	 * @protected
	 * @param {MouseEvent} e
	 **/
	p._handleMouseDown = function(e) {
		this._handlePointerDown(-1, e, false);
	}
	
	/**
	 * @method _handlePointerDown
	 * @protected
	 * @param {Number} id
	 * @param {Event} e
	 * @param {Number} x
	 * @param {Number} y
	 **/
	p._handlePointerDown = function(id, e, x, y) {
		var o = this._getPointerData(id);
		if (y != null) { this._updatePointerPosition(id, x, y); }
		
		if (this.onMouseDown || (this._listeners && this.hasEventListener("mouseDown"))) {
			var evt = new createjs.MouseEvent("mouseDown", o.x, o.y, this, e, id, id==this._primaryPointerID, o.rawX, o.rawY);
			this.onMouseDown&&this.onMouseDown(evt);
			this._listeners&&this.dispatchEvent(evt);
		}
		
		var target = this._getObjectsUnderPoint(o.x, o.y, null, (this._mouseOverIntervalID ? 3 : 1));
		if (target) {
			if (target.onPress || (target._listeners && target.hasEventListener("press"))) {
				evt = new createjs.MouseEvent("press", o.x, o.y, target, e, id, id==this._primaryPointerID, o.rawX, o.rawY);
				target.onPress&&target.onPress(evt);
				target._listeners&&target.dispatchEvent(evt);
				
				if (evt.onMouseMove || evt.onMouseUp || (evt._listeners&&(evt.hasEventListener("mouseMove") || evt.hasEventListener("mouseUp")))) { o.event = evt; }
			}
			o.target = target;
		}
	}

	/**
	 * @method _testMouseOver
	 * @protected
	 **/
	p._testMouseOver = function() {
		// for now, this only tests the mouse.
		if (this._primaryPointerID != -1) { return; }
		
		if (this.mouseX == this._mouseOverX && this.mouseY == this._mouseOverY && this.mouseInBounds) { return; }
		var target = null;
		if (this.mouseInBounds) {
			target = this._getObjectsUnderPoint(this.mouseX, this.mouseY, null, 3);
			this._mouseOverX = this.mouseX;
			this._mouseOverY = this.mouseY;
		}
		
		var mouseOverTarget = this._mouseOverTarget;
		if (mouseOverTarget != target) {
			var o = this._getPointerData(-1);
			if (mouseOverTarget && (mouseOverTarget.onMouseOut || (mouseOverTarget._listeners && mouseOverTarget.hasEventListener("mouseOut")))) {
				var evt = new createjs.MouseEvent("mouseOut", o.x, o.y, mouseOverTarget, null, -1, o.rawX, o.rawY);
				mouseOverTarget.onMouseOut&&mouseOverTarget.onMouseOut(evt);
				mouseOverTarget._listeners&&mouseOverTarget.dispatchEvent(evt);
			}
			if (mouseOverTarget) { this.canvas.style.cursor = ""; }
			
			if (target && (target.onMouseOver || (target._listeners && target.hasEventListener("mouseOver")))) {
				evt = new createjs.MouseEvent("mouseOver", o.x, o.y, target, null, -1, o.rawX, o.rawY);
				target.onMouseOver&&target.onMouseOver(evt);
				target._listeners&&target.dispatchEvent(evt);
			}
			if (target) { this.canvas.style.cursor = target.cursor||""; }
			
			this._mouseOverTarget = target;
		}
	}

	/**
	 * @method _handleDoubleClick
	 * @protected
	 * @param {MouseEvent} e
	 **/
	p._handleDoubleClick = function(e) {
		var o = this._getPointerData(-1);
		var target = this._getObjectsUnderPoint(o.x, o.y, null, (this._mouseOverIntervalID ? 3 : 1));
		if (target && (target.onDoubleClick || (target._listeners && target.hasEventListener("doubleClick")))) {
			evt = new createjs.MouseEvent("doubleClick", o.x, o.y, target, e, -1, true, o.rawX, o.rawY);
			target.onDoubleClick&&target.onDoubleClick(evt);
			target._listeners&&target.dispatchEvent(evt);
		}
	}

createjs.Stage = Stage;
}());/*
* Bitmap
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* A Bitmap represents an Image, Canvas, or Video in the display list.
* @class Bitmap
* @extends DisplayObject
* @constructor
* @param {Image | HTMLCanvasElement | HTMLVideoElement | String} imageOrUri The source object or URI to an image to display. This can be either an Image, Canvas, or Video object, or a string URI to an image file to load and use. If it is a URI, a new Image object will be constructed and assigned to the .image property.
**/
var Bitmap = function(imageOrUri) {
  this.initialize(imageOrUri);
}
var p = Bitmap.prototype = new createjs.DisplayObject();

// public properties:
	/**
	 * The image to render. This can be an Image, a Canvas, or a Video.
	 * @property image
	 * @type Image | HTMLCanvasElement | HTMLVideoElement
	 **/
	p.image = null;
	
	/**
	 * Whether or not the Bitmap should be draw to the canvas at whole pixel coordinates.
	 * @property snapToPixel
	 * @type Boolean
	 * @default true
	 **/
	p.snapToPixel = true;

	/**
	 * Specifies an area of the source image to draw. If omitted, the whole image will be drawn.
	 * @property sourceRect
	 * @type Rectangle
	 * @default null
	 */
	p.sourceRect = null;
	
	// constructor:

	/**
	 * @property DisplayObject_initialize
	 * @type Function
    * @private
	 **/
	p.DisplayObject_initialize = p.initialize;

	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function(imageOrUri) {
		this.DisplayObject_initialize();
		if (typeof imageOrUri == "string") {
			this.image = new Image();
			this.image.src = imageOrUri;
		} else {
			this.image = imageOrUri;
		}
	}
	
// public methods:

	/**
	 * Returns true or false indicating whether the display object would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the display object would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.image && (this.image.complete || this.image.getContext || this.image.readyState >= 2);
	}

	/**
	 * @property DisplayObject_draw
	 * @type Function
	 * @private
	 **/
	p.DisplayObject_draw = p.draw;
	
	/**
	 * Draws the display object into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache. 
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		if (this.DisplayObject_draw(ctx, ignoreCache)) { return true; }
		var rect = this.sourceRect;
		if (rect) {
			ctx.drawImage(this.image, rect.x, rect.y, rect.width, rect.height, 0, 0, rect.width, rect.height);
		} else {
			ctx.drawImage(this.image, 0, 0);
		}
		return true;
	}
	
	//Note, the doc sections below document using the specified APIs (from DisplayObject)  from
	//Bitmap. This is why they have no method implementations.
	
	/**
	 * Because the content of a Bitmap is already in a simple format, cache is unnecessary for Bitmap instances.
	 * You should not cache Bitmap instances as it can degrade performance.
	 * @method cache
	 **/
	
	/**
	 * Because the content of a Bitmap is already in a simple format, cache is unnecessary for Bitmap instances.
	 * You should not cache Bitmap instances as it can degrade performance.
	 * @method updateCache
	 **/
	
	/**
	 * Because the content of a Bitmap is already in a simple format, cache is unnecessary for Bitmap instances.
	 * You should not cache Bitmap instances as it can degrade performance.
	 * @method uncache
	 **/
	
	/**
	 * Returns a clone of the Bitmap instance.
	 * @method clone
	 * @return {Bitmap} a clone of the Bitmap instance.
	 **/
	p.clone = function() {
		var o = new Bitmap(this.image);
		if (this.sourceRect) { o.sourceRect = this.sourceRect.clone(); }
		this.cloneProps(o);
		return o;
	}
	
	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Bitmap (name="+  this.name +")]";
	}

// private methods:

createjs.Bitmap = Bitmap;
}());/*
* BitmapAnimation
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {
	
/**
* Displays frames or sequences of frames (ie. animations) from a sprite sheet image. A sprite sheet is a series of images
* (usually animation frames) combined into a single image. For example, an animation
* consisting of 8 100x100 images could be combined into a 400x200 sprite sheet (4 frames across by 2 high).
* You can display individual frames, play frames as an animation, and even sequence animations
* together. See the SpriteSheet class for more information on setting up frames and animations.
* @class BitmapAnimation
* @extends DisplayObject
* @constructor
* @param {SpriteSheet} spriteSheet The SpriteSheet instance to play back. This includes the source image(s), frame
* dimensions, and frame data. See SpriteSheet for more information.
**/
var BitmapAnimation = function(spriteSheet) {
  this.initialize(spriteSheet);
}
var p = BitmapAnimation.prototype = new createjs.DisplayObject();

// public properties:

	/**
	 * Specifies a function to call whenever any animation reaches its end. It will be called with two
	 * params: the first will be a reference to this instance, the second will be the name of the animation
	 * that just ended.
	 * @property onAnimationEnd
	 * @type Function
	 **/
	p.onAnimationEnd = null;

	/**
	 * The frame that will be drawn when draw is called. Note that with some SpriteSheet data, this
	 * will advance non-sequentially. READ-ONLY.
	 * @property currentFrame
	 * @type Number
	 * @default -1
	 **/
	p.currentFrame = -1;

	/**
	 * Returns the currently playing animation. READ-ONLY.
	 * @property currentAnimation
	 * @type String
	 * @final
	 **/
	p.currentAnimation = null; // READ-ONLY

	/**
	 * Prevents the animation from advancing each tick automatically. For example, you could create a sprite
	 * sheet of icons, set paused to true, and display the appropriate icon by setting currentFrame.
	 * @property paused
	 * @type Boolean
	 * @default false
	 **/
	p.paused = true;

	/**
	 * The SpriteSheet instance to play back. This includes the source image, frame dimensions, and frame
	 * data. See SpriteSheet for more information.
	 * @property spriteSheet
	 * @type SpriteSheet
	 **/
	p.spriteSheet = null;

	/**
	 * Whether or not the Bitmap should be draw to the canvas at whole pixel coordinates.
	 * @property snapToPixel
	 * @type Boolean
	 * @default true
	 **/
	p.snapToPixel = true;
	
	/** 
	 * When used in conjunction with animations having an frequency greater than 1, this lets you offset which tick the playhead will
	 * advance on. For example, you could create two BitmapAnimations, both playing an animation with a frequency of 2, but one
	 * having offset set to 1. Both instances would advance every second tick, but they would advance on alternating
	 * ticks (effectively, one instance would advance on odd ticks, the other on even ticks).
	 * @property offset
	 * @type Number
	 * @default 0
	 */
	p.offset = 0;
	
	
	/**
	 * Specifies the current frame index within the current playing animation. When playing normally, this will
	 * increase successively from 0 to n-1, where n is the number of frames in the current animation.
	 * @property currentAnimationFrame
	 * @type Number
	 * @default 0
	 **/
	p.currentAnimationFrame = 0;

// private properties:
	/**
	 * @property _advanceCount
	 * @protected
	 * @type Number
	 * @default 0
	 **/
	p._advanceCount = 0;
	
	/**
	 * @property _animation
	 * @protected
	 * @type Object
	 * @default null
	 **/
	p._animation = null;

// constructor:
	/**
	 * @property DisplayObject_initialize
	 * @type Function
	 * @private
	 **/
	p.DisplayObject_initialize = p.initialize;

	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function(spriteSheet) {
		this.DisplayObject_initialize();
		this.spriteSheet = spriteSheet;
	}

	/**
	 * Returns true or false indicating whether the display object would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the display object would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		return this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.spriteSheet.complete && this.currentFrame >= 0;
	}

	/**
	 * @property DisplayObject_draw
	 * @type Function
	 * @private
	 **/
	p.DisplayObject_draw = p.draw;

/**
	 * Draws the display object into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache.
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		if (this.DisplayObject_draw(ctx, ignoreCache)) { return true; }
		this._normalizeFrame();
		var o = this.spriteSheet.getFrame(this.currentFrame);
		if (o == null) { return; }
		var rect = o.rect;
		// TODO: implement snapToPixel on regX/Y?
		ctx.drawImage(o.image, rect.x, rect.y, rect.width, rect.height, -o.regX, -o.regY, rect.width, rect.height);
		return true;
	}

	//Note, the doc sections below document using the specified APIs (from DisplayObject)  from
	//Bitmap. This is why they have no method implementations.

	/**
	 * Because the content of a Bitmap is already in a simple format, cache is unnecessary for Bitmap instances.
	 * You should not cache Bitmap instances as it can degrade performance.
	 * @method cache
	 **/

	/**
	 * Because the content of a Bitmap is already in a simple format, cache is unnecessary for Bitmap instances.
	 * You should not cache Bitmap instances as it can degrade performance.
	 * @method updateCache
	 **/

	/**
	 * Because the content of a Bitmap is already in a simple format, cache is unnecessary for Bitmap instances.
	 * You should not cache Bitmap instances as it can degrade performance.
	 * @method uncache
	 **/
	
	/**
	 * TODO: Doc.
	 * @method play
	 **/
	p.play = function() {
		this.paused = false;
	}
	
	/**
	 * TODO: Doc.
	 * @method stop
	 **/
	p.stop = function() {
		this.paused = true;
	}

	/**
	 * Sets paused to false and plays the specified animation name, named frame, or frame number.
	 * @method gotoAndPlay
	 * @param {String|Number} frameOrAnimation The frame number or animation name that the playhead should move to
	 * and begin playing.
	 **/
	p.gotoAndPlay = function(frameOrAnimation) {
		this.paused = false;
		this._goto(frameOrAnimation);
	}

	/**
	 * Sets paused to true and seeks to the specified animation name, named frame, or frame number.
	 * @method gotoAndStop
	 * @param {String|Number} frameOrAnimation The frame number or animation name that the playhead should move to
	 * and stop.
	 **/
	p.gotoAndStop = function(frameOrAnimation) {
		this.paused = true;
		this._goto(frameOrAnimation);
	}

	/**
	 * Advances the playhead. This occurs automatically each tick by default.
	 * @method advance
	*/
	p.advance = function() {
		if (this._animation) { this.currentAnimationFrame++; }
		else { this.currentFrame++; }
		this._normalizeFrame();
	}

	/**
	 * Returns a clone of the BitmapAnimation instance.
	 * @method clone
	 * @return {BitmapAnimation} a clone of the BitmapAnimation instance.
	 **/
	p.clone = function() {
		var o = new BitmapAnimation(this.spriteSheet);
		this.cloneProps(o);
		return o;
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[BitmapAnimation (name="+  this.name +")]";
	}

// private methods:
	/**
	 * @property DisplayObject__tick
	 * @type Function
	 * @private
	 **/
	p.DisplayObject__tick = p._tick;
	
	/**
	 * Advances the currentFrame if paused is not true. This is called automatically when the Stage ticks.
	 * @protected
	 * @method _tick
	 **/
	p._tick = function(params) {
		var f = this._animation ? this._animation.frequency : 1;
		if (!this.paused && ((++this._advanceCount)+this.offset)%f == 0) {
			this.advance();
		}
		this.DisplayObject__tick(params);
	}
	
	
	/**
	 * Normalizes the current frame, advancing animations and dispatching callbacks as appropriate.
	 * @protected
	 * @method _normalizeCurrentFrame
	 **/
	p._normalizeFrame = function() { 
		var a = this._animation;
		if (a) {
			if (this.currentAnimationFrame >= a.frames.length) {
				if (a.next) {
					this._goto(a.next);
				} else {
					this.paused = true;
					this.currentAnimationFrame = a.frames.length-1;
					this.currentFrame = a.frames[this.currentAnimationFrame];
				}
				if (this.onAnimationEnd) { this.onAnimationEnd(this,a.name); }
			} else {
				this.currentFrame = a.frames[this.currentAnimationFrame];
			}
		} else {
			if (this.currentFrame >= this.spriteSheet.getNumFrames()) {
				this.currentFrame = 0;
				if (this.onAnimationEnd) { this.onAnimationEnd(this,null); }
			}
		}
	}

	/**
	 * @property DisplayObject_cloneProps
	 * @private
	 * @type Function
	 **/
	p.DisplayObject_cloneProps = p.cloneProps;

	/**
	 * @method cloneProps
	 * @param {Text} o
	 * @protected
	 **/
	p.cloneProps = function(o) {
		this.DisplayObject_cloneProps(o);
		o.onAnimationEnd = this.onAnimationEnd;
		o.currentFrame = this.currentFrame;
		o.currentAnimation = this.currentAnimation;
		o.paused = this.paused;
		o.offset = this.offset;
		o._animation = this._animation;
		o.currentAnimationFrame = this.currentAnimationFrame;
	}

	/**
	 * Moves the playhead to the specified frame number or animation.
	 * @method _goto
	 * @param {String|Number} frameOrAnimation The frame number or animation that the playhead should move to.
	 * @protected
	 **/
	p._goto = function(frameOrAnimation) {
		if (isNaN(frameOrAnimation)) {
			var data = this.spriteSheet.getAnimation(frameOrAnimation);
			if (data) {
				this.currentAnimationFrame = 0;
				this._animation = data;
				this.currentAnimation = frameOrAnimation;
				this._normalizeFrame();
			}
		} else {
			this.currentAnimation = this._animation = null;
			this.currentFrame = frameOrAnimation;
		}
	}

createjs.BitmapAnimation = BitmapAnimation;
}());
/*
* Shape
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* A Shape allows you to display vector art in the display list. It composites a Graphics instance which exposes all of the vector
* drawing methods. The Graphics instance can be shared between multiple Shape instances to display the same vector graphics with different
* positions or transforms. If the vector art will not change between draws, you may want to use the cache() method to reduce the rendering cost.
* @class Shape
* @extends DisplayObject
* @constructor
* @param {Graphics} graphics Optional. The graphics instance to display. If null, a new Graphics instance will be created.
**/
var Shape = function(graphics) {
  this.initialize(graphics);
}
var p = Shape.prototype = new createjs.DisplayObject();

// public properties:
	/**
	 * The graphics instance to display.
	 * @property graphics
	 * @type Graphics
	 **/
	p.graphics = null;
	
// constructor:
	/**
	 * @property DisplayObject_initialize
	 * @private
	 * @type Function
	 **/
	p.DisplayObject_initialize = p.initialize;

	/** 
	 * Initialization method.
	 * @method initialize
	 * @param {Graphics} graphics
	 * @protected
	 **/
	p.initialize = function(graphics) {
		this.DisplayObject_initialize();
		this.graphics = graphics ? graphics : new createjs.Graphics();
	}
	
	p.DisplayObject_set = p.set;
	
	/*
	 * Shape.set supports the special options strokeWidth, strokeCap, strokeJoin, strokeMiterLimit, fillColor, and strokeColor,
	 * which modify the default graphics object. You can combine these with drawing commands specified in a callback function:
	 * 
	 *	  new createjs.Shape({
	 *		  strokeWidth: 3,
	 *		  fillColor: 'red',
	 *		  graphics: function(g) { g.drawCircle(0, 0, 1); }
	 *	  })
	 */
	p.set = function(opts) {
		var graphicsCallback;
		if (opts) {
			graphics = null;
			if (typeof opts.graphics === 'function') {
				graphicsCallback = opts.graphics;
				delete opts.graphics;
			}
		}
		
		this.DisplayObject_set(opts);
		
		if (opts) {
			if (opts.strokeWidth || opts.strokeCap || opts.strokeJoin || opts.strokeMiterLimit) {
				this.graphics.setStrokeStyle(opts.strokeWidth, opts.strokeCap, opts.strokeJoin, opts.strokeMiterLimit);
			}
			if (opts.fillColor) {
				this.graphics.beginFill(opts.fillColor);
			}
			if (opts.strokeColor) {
				this.graphics.beginStroke(opts.strokeColor);
			}
			if (graphicsCallback) {
				graphicsCallback(this.graphics);
			}
		}
		
		return this;
	}

	/**
	 * Returns true or false indicating whether the Shape would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the Shape would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		return Boolean(this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.graphics);
	}

	/**
	 * @property DisplayObject_draw
	 * @private
	 * @type Function
	 **/
	p.DisplayObject_draw = p.draw;
	
	/**
	 * Draws the Shape into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache. 
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		if (this.DisplayObject_draw(ctx, ignoreCache)) { return true; }
		this.graphics.draw(ctx);
		return true;
	}
	
	/**
	 * Returns a clone of this Shape. Some properties that are specific to this instance's current context are reverted to 
	 * their defaults (for example .parent).
	 * @method clone
	 * @param {Boolean} recursive If true, this Shape's Graphics instance will also be cloned. If false, the Graphics instance 
	 * will be shared with the new Shape.
	 **/
	p.clone = function(recursive) {
		var o = new Shape((recursive && this.graphics) ? this.graphics.clone() : this.graphics);
		this.cloneProps(o);
		return o;
	}
		
	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Shape (name="+  this.name +")]";
	}

createjs.Shape = Shape;
}());/*
* Text
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {
	
/**
* Allows you to display one or more lines of dynamic text (not user editable) in the display list.
* Line wrapping support (using the lineWidth is very basic, wrapping on spaces and tabs only. Note
* that as an alternative to Text, you can position HTML text above or below the canvas relative to 
* items in the display list using the localToGlobal() method.
* @class Text
* @extends DisplayObject
* @constructor
* @param {String} text Optional. The text to display.
* @param {String} font Optional. The font style to use. Any valid value for the CSS font attribute is 
* acceptable (ex. "bold 36px Arial").
* @param {String} color Optional. The color to draw the text in. Any valid value for the CSS color attribute
* is acceptable (ex. "#F00").
**/
var Text = function(text, font, color) {
  this.initialize(text, font, color);
}
var p = Text.prototype = new createjs.DisplayObject();


	/**
	 * @property _workingContext
	 * @type CanvasRenderingContext2D
	 * @private
	 **/
	Text._workingContext = (createjs.createCanvas?createjs.createCanvas():document.createElement("canvas")).getContext("2d");

// public properties:
	/**
	 * The text to display.
	 * @property text
	 * @type String
	 **/
	p.text = "";
	
	/**
	 * The font style to use. Any valid value for the CSS font attribute is acceptable (ex. "bold 36px Arial"). 
	 * @property font
	 * @type String
	 **/
	p.font = null;
	
	/**
	 * The color to draw the text in. Any valid value for the CSS color attribute is acceptable (ex. "#F00"). Default is "#000".
	 * @property color
	 * @type String
	 **/
	p.color = "#000";
	
	/**
	 * The horizontal text alignment. Any of "start", "end", "left", "right", and "center". For detailed 
	 * information view the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#text-styles">
	 * whatwg spec</a>. Default is "left".
	 * @property textAlign
	 * @type String
	 **/
	p.textAlign = "left";
	
	/** The vertical alignment point on the font. Any of "top", "hanging", "middle", "alphabetic", 
	 * "ideographic", or "bottom". For detailed information view the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#text-styles">
	 * whatwg spec</a>. Default is "top".
	 * @property textBaseline
	 * @type String
	*/
	p.textBaseline = "top";
	
	/** The maximum width to draw the text. If maxWidth is specified (not null), the text will be condensed or 
	 * shrunk to make it fit in this width. For detailed information view the 
	 * <a href="http://www.whatwg.org/specs/web-apps/current-work/multipage/the-canvas-element.html#text-styles">
	 * whatwg spec</a>.
	 * @property maxWidth
	 * @type Number
	*/
	p.maxWidth = null;
	
	/** If true, the text will be drawn as a stroke (outline). If false, the text will be drawn as a fill.
	 * @property outline
	 * @type Boolean
	 **/
	p.outline = false;
	
	/** Indicates the line height (vertical distance between baselines) for multi-line text. If null or 0, 
	 * the value of getMeasuredLineHeight is used.
	 * @property lineHeight
	 * @type Number
	 **/
	p.lineHeight = 0;
	
	/**
	 * Indicates the maximum width for a line of text before it is wrapped to multiple lines. If null, 
	 * the text will not be wrapped.
	 * @property lineWidth
	 * @type Number
	 **/
	p.lineWidth = null;
	
// constructor:
	/**
	 * @property DisplayObject_initialize
	 * @private
	 * @type Function
	 **/
	p.DisplayObject_initialize = p.initialize;
	
	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function(text, font, color) {
		this.DisplayObject_initialize();
		this.text = text;
		this.font = font;
		this.color = color ? color : "#000";
	}
	
	/**
	 * Returns true or false indicating whether the display object would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the display object would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		// Note: this.text = "0" will evaluate to false in JS.
		return Boolean(this.visible && this.alpha > 0 && this.scaleX != 0 && this.scaleY != 0 && this.text != null && this.text !== "");
	}

	/**
	 * @property DisplayObject_draw
	 * @private
	 * @type Function
	 **/
	p.DisplayObject_draw = p.draw;
	
	/**
	 * Draws the Text into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache. 
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		if (this.DisplayObject_draw(ctx, ignoreCache)) { return true; }
		
		if (this.outline) { ctx.strokeStyle = this.color; }
		else { ctx.fillStyle = this.color; }
		ctx.font = this.font;
		ctx.textAlign = this.textAlign||"start";
		ctx.textBaseline = this.textBaseline||"alphabetic";

		this._drawText(ctx);
		return true;
	}
	
	/**
	 * Returns the measured, untransformed width of the text without wrapping.
	 * @method getMeasuredWidth
	 * @return {Number} The measured, untransformed width of the text.
	 **/
	p.getMeasuredWidth = function() {
		return this._getWorkingContext().measureText(this.text).width;
	}

	/**
	 * Returns an approximate line height of the text, ignoring the lineHeight property. This is based 
	 * on the measured width of a "M" character multiplied by 1.2, which approximates em for most fonts.
	 * @method getMeasuredLineHeight
	 * @return {Number} an approximate line height of the text, ignoring the lineHeight property. This is 
	 * based on the measured width of a "M" character multiplied by 1.2, which approximates em for most fonts.
	 **/
	p.getMeasuredLineHeight = function() {
		return this._getWorkingContext().measureText("M").width*1.2;
	}

	/**
	 * Returns the approximate height of multiline text by multiplying the number of lines against
	 * either the lineHeight (if specified) or getMeasuredLineHeight(). Note that this operation
	 * requires the text flowing logic to run, which has an associated CPU cost.
	 * @method getMeasuredHeight
	 * @return {Number} The approximate height of the drawn multiline text.
	 **/
	p.getMeasuredHeight = function() {
		return this._drawText()*(this.lineHeight||this.getMeasuredLineHeight());
	}
	
	/**
	 * Returns a clone of the Text instance.
	 * @method clone
	 * @return {Text} a clone of the Text instance.
	 **/
	p.clone = function() {
		var o = new Text(this.text, this.font, this.color);
		this.cloneProps(o);
		return o;
	}
		
	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Text (text="+  (this.text.length > 20 ? this.text.substr(0, 17)+"..." : this.text) +")]";
	}
	
// private methods:
	
	/**
	 * @property DisplayObject_cloneProps
	 * @private
	 * @type Function
	 **/
	p.DisplayObject_cloneProps = p.cloneProps;

	/** 
	 * @method cloneProps
	 * @param {Text} o
	 * @protected 
	 **/
	p.cloneProps = function(o) {
		this.DisplayObject_cloneProps(o);
		o.textAlign = this.textAlign;
		o.textBaseline = this.textBaseline;
		o.maxWidth = this.maxWidth;
		o.outline = this.outline;
		o.lineHeight = this.lineHeight;
		o.lineWidth = this.lineWidth;
	}

	/** 
	 * @method _getWorkingContext
	 * @protected 
	 **/
	p._getWorkingContext = function() {
		var ctx = Text._workingContext;
		ctx.font = this.font;
		ctx.textAlign = this.textAlign||"start";
		ctx.textBaseline = this.textBaseline||"alphabetic";
		return ctx;
	}
	 
	/**
	 * Draws multiline text.
	 * @method _getWorkingContext
	 * @protected
	 * @return {Number} The number of lines drawn.
	 **/
	p._drawText = function(ctx) {
		var paint = !!ctx;
		if (!paint) { ctx = this._getWorkingContext(); }
		var lines = String(this.text).split(/(?:\r\n|\r|\n)/);
		var lineHeight = this.lineHeight||this.getMeasuredLineHeight();
		var count = 0;
		for (var i=0, l=lines.length; i<l; i++) {
			var w = ctx.measureText(lines[i]).width;
			if (this.lineWidth == null || w < this.lineWidth) {
				if (paint) { this._drawTextLine(ctx, lines[i], count*lineHeight); }
				count++;
				continue;
			}

			// split up the line
			var words = lines[i].split(/(\s)/);
			var str = words[0];
			for (var j=1, jl=words.length; j<jl; j+=2) {
				// Line needs to wrap:
				if (ctx.measureText(str + words[j] + words[j+1]).width > this.lineWidth) {
					if (paint) { this._drawTextLine(ctx, str, count*lineHeight); }
					count++;
					str = words[j+1];
				} else {
					str += words[j] + words[j+1];
				}
			}
			if (paint) { this._drawTextLine(ctx, str, count*lineHeight); } // Draw remaining text
			count++;
		}
		return count;
	}
	
	/** 
	 * @method _drawTextLine
	 * @param {CanvasRenderingContext2D} ctx
	 * @param {Text} text
	 * @param {Number} y
	 * @protected 
	 **/
	p._drawTextLine = function(ctx, text, y) {
		// Chrome 17 will fail to draw the text if the last param is included but null, so we feed it a large value instead:
			if (this.outline) { ctx.strokeText(text, 0, y, this.maxWidth||0xFFFF); }
			else { ctx.fillText(text, 0, y, this.maxWidth||0xFFFF); }
		
	}

createjs.Text = Text;
}());/*
* SpriteSheetUtils
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {
// constructor:
/**
* The SpriteSheetUtils class is a collection of static methods for working
* with sprite sheets.  A sprite sheet is a series of images (usually animation frames)
* combined into a single image on a regular grid. For example, an animation consisting
* of 8 100x100 images could be combined into a 400x200 sprite sheet (4 frames across by 2 high).
* The SpriteSheetUtils class uses a static interface and should not be instantiated.
* @class SpriteSheetUtils
* @static
**/
var SpriteSheetUtils = function() {
	throw "SpriteSheetUtils cannot be instantiated";
}

	/**
	 * @property _workingCanvas
	 * @static
	 * @type HTMLCanvasElement | Object
	 * @protected
	*/
	SpriteSheetUtils._workingCanvas = createjs.createCanvas?createjs.createCanvas():document.createElement("canvas");

	/**
	 * @property _workingContext
	 * @static
	 * @type CanvasRenderingContext2D
	 * @protected
	*/
	SpriteSheetUtils._workingContext = SpriteSheetUtils._workingCanvas.getContext("2d");

// public static methods:
	/**
	 * <b>This is an experimental method, and may be buggy. Please report issues.</b><br/><br/>
	 * Extends the existing sprite sheet by flipping the original frames horizontally, vertically, or both,
	 * and adding appropriate animation & frame data. The flipped animations will have a suffix added to their names
	 * (_h, _v, _hv as appropriate). Make sure the sprite sheet images are fully loaded before using this method.
	 * <br/><br/>
	 * For example:<br/>
	 * SpriteSheetUtils.addFlippedFrames(mySpriteSheet, true, true);
	 * The above would add frames that are flipped horizontally AND frames that are flipped vertically.
	 * <br/><br/>
	 * Note that you can also flip any display object by setting its scaleX or scaleY to a negative value. On some
	 * browsers (especially those without hardware accelerated canvas) this can result in slightly degraded performance,
	 * which is why addFlippedFrames is available. 
	 * @method addFlippedFrames
	 * @static
	 * @param {SpriteSheet} spriteSheet 
	 * @param {Boolean} horizontal If true, horizontally flipped frames will be added.
	 * @param {Boolean} vertical If true, vertically flipped frames will be added.
	 * @param {Boolean} both If true, frames that are flipped both horizontally and vertically will be added.
	 **/
	SpriteSheetUtils.addFlippedFrames = function(spriteSheet, horizontal, vertical, both) {
		if (!horizontal && !vertical && !both) { return; }
		
		var count = 0;
		if (horizontal) { SpriteSheetUtils._flip(spriteSheet,++count,true,false); }
		if (vertical) { SpriteSheetUtils._flip(spriteSheet,++count,false,true); }
		if (both) { SpriteSheetUtils._flip(spriteSheet,++count,true,true); }
	}

	/**
	 * Returns a single frame of the specified sprite sheet as a new PNG image.
	 * @method extractFrame
	 * @static
	 * @param {Image} spriteSheet The SpriteSheet instance to extract a frame from.
	 * @param {Number} frame The frame number or animation name to extract. If an animation
	 * name is specified, only the first frame of the animation will be extracted.
	 * @return {Image} a single frame of the specified sprite sheet as a new PNG image.
	*/
	SpriteSheetUtils.extractFrame = function(spriteSheet, frame) {
		if (isNaN(frame)) {
			frame = spriteSheet.getAnimation(frame).frames[0];
		}
		var data = spriteSheet.getFrame(frame);
		if (!data) { return null; }
		var r = data.rect;
		var canvas = SpriteSheetUtils._workingCanvas;
		canvas.width = r.width;
		canvas.height = r.height;
		SpriteSheetUtils._workingContext.drawImage(data.image, r.x, r.y, r.width, r.height, 0, 0, r.width, r.height);
		var img = new Image();
		img.src = canvas.toDataURL("image/png");
		return img;
	}

	/**
	 * Merges the rgb channels of one image with the alpha channel of another. This can be used to combine a compressed
	 * JPEG image containing color data with a PNG32 monochromatic image containing alpha data. With certain types of
	 * images (those with detail that lend itself to JPEG compression) this can provide significant file size savings
	 * versus a single RGBA PNG32. This method is very fast (generally on the order of 1-2 ms to run).
	 * @method mergeAlpha
	 * @static
	 * @param {Image} rbgImage The image (or canvas) containing the RGB channels to use.
	 * @param {Image} alphaImage The image (or canvas) containing the alpha channel to use.
	 * @param {Canvas} canvas Optional. If specified, this canvas will be used and returned. If not, a new canvas will be created.
	 * @return {Canvas} A canvas with the combined image data. This can be used as a source for Bitmap or SpriteSheet.
	*/
	SpriteSheetUtils.mergeAlpha = function(rgbImage, alphaImage, canvas) {
		if (!canvas) { canvas = createjs.createCanvas?createjs.createCanvas():document.createElement("canvas"); }
		canvas.width = Math.max(alphaImage.width, rgbImage.width);
		canvas.height = Math.max(alphaImage.height, rgbImage.height);
		var ctx = canvas.getContext("2d");
		ctx.save();
		ctx.drawImage(rgbImage,0,0);
		ctx.globalCompositeOperation = "destination-in";
		ctx.drawImage(alphaImage,0,0);
		ctx.restore();
		return canvas;
	}

	
// private static methods:
	SpriteSheetUtils._flip = function(spriteSheet, count, h, v) {
		var imgs = spriteSheet._images;
		var canvas = SpriteSheetUtils._workingCanvas;
		var ctx = SpriteSheetUtils._workingContext;
		var il = imgs.length/count;
		for (var i=0;i<il;i++) {
			var src = imgs[i];
			src.__tmp = i; // a bit hacky, but faster than doing indexOf below.
			canvas.width = 0; // make sure it clears.
			canvas.width = src.width;
			canvas.height = src.height;
			ctx.setTransform(h?-1:1, 0, 0, v?-1:1, h?src.width:0, v?src.height:0);
			ctx.drawImage(src,0,0);
			var img = new Image();
			img.src = canvas.toDataURL("image/png");
			// work around a strange bug in Safari:
			img.width = src.width;
			img.height = src.height;
			imgs.push(img);
		}
		
		var frames = spriteSheet._frames;
		var fl = frames.length/count;
		for (i=0;i<fl;i++) {
			src = frames[i];
			var rect = src.rect.clone();
			img = imgs[src.image.__tmp+il*count];
			
			var frame = {image:img,rect:rect,regX:src.regX,regY:src.regY};
			if (h) {
				rect.x = img.width-rect.x-rect.width; // update rect
				frame.regX = rect.width-src.regX; // update registration point
			}
			if (v) {
				rect.y = img.height-rect.y-rect.height;  // update rect
				frame.regY = rect.height-src.regY; // update registration point
			}
			frames.push(frame);
		}
		
		var sfx = "_"+(h?"h":"")+(v?"v":"");
		var names = spriteSheet._animations;
		var data = spriteSheet._data;
		var al = names.length/count;
		for (i=0;i<al;i++) {
			var name = names[i];
			src = data[name];
			var anim = {name:name+sfx,frequency:src.frequency,next:src.next,frames:[]};
			if (src.next) { anim.next += sfx; }
			frames = src.frames;
			for (var j=0,l=frames.length;j<l;j++) {
				anim.frames.push(frames[j]+fl*count);
			}
			data[anim.name] = anim;
			names.push(anim.name);
		}
	}
	

createjs.SpriteSheetUtils = SpriteSheetUtils;
}());/*
* SpriteSheetBuilder
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
 * The SpriteSheetBuilder allows you to generate sprite sheets at run time from any display object. This can allow
 * you to maintain your assets as vector graphics (for low file size), and render them at run time as sprite sheets
 * for better performance.
 * <br/><br/>
 * Sprite sheets can be built either synchronously, or asynchronously, so that large sprite sheets can be generated
 * without locking the UI.
 * <br/><br/>
 * Note that the "images" used in the generated sprite sheet are actually canvas elements, and that they will be sized
 * to the nearest power of 2 up to the value of maxWidth or maxHeight.
 * @class SpriteSheetBuilder
 * @constructor
 **/
var SpriteSheetBuilder = function() {
  this.initialize();
}
var p = SpriteSheetBuilder.prototype;

// constants:
	SpriteSheetBuilder.ERR_DIMENSIONS = "frame dimensions exceed max spritesheet dimensions";
	SpriteSheetBuilder.ERR_RUNNING = "a build is already running";

// public properties:

	/**
	 * The maximum width for the images (not individual frames) in the generated sprite sheet. It is recommended to use
	 * a power of 2 for this value (ex. 1024, 2048, 4096). If the frames cannot all fit within the max dimensions, then
	 * additional images will be created as needed.
	 * @property maxWidth
	 * @type Number
	 * @default 2048
	*/
	p.maxWidth = 2048;

	/**
	 * The maximum height for the images (not individual frames) in the generated sprite sheet. It is recommended to use
	 * a power of 2 for this value (ex. 1024, 2048, 4096). If the frames cannot all fit within the max dimensions, then
	 * additional images will be created as needed.
	 * @property maxHeight
	 * @type Number
	 * @default 2048
	 **/
	p.maxHeight = 2048;

	/**
	 * The sprite sheet that was generated. This will be null before a build is completed successfully.
	 * @property spriteSheet
	 * @type SpriteSheet
	 **/
	p.spriteSheet = null;
	
	/**
	 * The scale to apply when drawing all frames to the sprite sheet. This is multiplied against any scale specified
	 * in the addFrame call. This can be used, for example, to generate a sprite sheet at run time that is tailored to
	 * the a specific device resolution (ex. tablet vs mobile).
	 * @property defaultScale
	 * @type Number
	 * @default 1
	 **/
	p.scale = 1;
	
	/**
	* The padding to use between frames. This is helpful to preserve antialiasing on drawn vector content.
	* @property padding
	* @type Number
	* @default 1
	**/
	p.padding = 1;

// private properties:

	/**
	 * @property _frames
	 * @protected
	 * @type Array
	 **/
	p._frames = null;
	
	/**
	 * @property _animations
	 * @protected
	 * @type Array
	 **/
	p._animations = null;
	
	/**
	 * @property _data
	 * @protected
	 * @type Array
	 **/
	p._data = null;
	
	/**
	 * @property _nextFrameIndex
	 * @protected
	 * @type Number
	 **/
	p._nextFrameIndex = 0;
	
	/**
	 * @property _index
	 * @protected
	 * @type Number
	 **/
	p._index = 0;
	
	/**
	 * @property _callback
	 * @protected
	 * @type Function
	 **/
	p._callback = null;
	
	/**
	 * @property _timeSlice
	 * @protected
	 * @type Number
	 **/
	p._timeSlice = null;
	
	/**
	 * @property _timerID
	 * @protected
	 * @type Number
	 **/
	p._timerID = null;
	
	/**
	 * @property _scale
	 * @protected
	 * @type Number
	 **/
	p._scale = 1;

// constructor:
	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function() {
		this._frames = [];
		this._animations = {};
	}

// public methods:
	
	/**
	 * Adds a frame to the sprite sheet. Note that the frame will not be drawn until you call build. The optional
	 * setup params allow you to have a function run immediately before the draw occurs. For example, this allows you to
	 * add a single source multiple times, but manipulate it or it's children to change it to generate different frames.
	 * <br/><br/>
	 * Note that the source's transformations (x,y,scale,rotate,alpha) will be ignored, except for regX/Y. To apply
	 * transforms to a source object and have them captured in the sprite sheet, simply place it into a Container
	 * and pass in the Container as the source.
	 * @method addFrame
	 * @param {DisplayObject} source The source display object to draw as the frame.
	 * @param {Rectangle} sourceRect Optional. A rectangle defining the portion of the source to draw to the frame. If
	 * not specified, it will look for a getBounds method, bounds property, or nominalBounds property on the source to use.
	 * If one is not found, the frame will be skipped.
	 * @param {Number} scale Optional. The scale to draw this frame at. Default is 1.
	 * @param {Function} setupFunction Optional. A function to call immediately before drawing this frame.
	 * @param {Array} setupParams Optional. Parameters to pass to the setup function.
	 * @param {Object} setupScope Optional. The scope to call the setupFunction in.
	 * @return {Number} The index of the frame that was just added, or null if a sourceRect could not be determined.
	 **/
	p.addFrame = function(source, sourceRect, scale, setupFunction, setupParams, setupScope) {
		if (this._data) { throw SpriteSheetBuilder.ERR_RUNNING; }
		var rect = sourceRect||source.bounds||source.nominalBounds;
		if (!rect&&source.getBounds) { rect = source.getBounds(); }
		if (!rect) { return null; }
		scale = scale||1;
		return this._frames.push({source:source, sourceRect:rect, scale:scale, funct:setupFunction, params:setupParams, scope:setupScope, index:this._frames.length, height:rect.height*scale})-1;
	}
	
	/**
	 * Adds an animation that will be included in the created sprite sheet.
	 * @method addFrame
	 * @param {String} name The name for the animation.
	 * @param {Array} frames An array of frame indexes that comprise the animation. Ex. [3,6,5] would describe an animation
	 * that played frame indexes 3, 6, and 5 in that order.
	 * @param {String} next Optional. Specifies the name of the animation to continue to after this animation ends. You can
	 * also pass false to have the animation stop when it ends. By default it will loop to the start of the same animation.
	 * @param {Number} frequency Optional. Specifies a frame advance frequency for this animation. For example, a value
	 * of 2 would cause the animation to advance every second tick.
	 **/
	p.addAnimation = function(name, frames, next, frequency) {
		if (this._data) { throw SpriteSheetBuilder.ERR_RUNNING; }
		this._animations[name] = {frames:frames, next:next, frequency:frequency};
	}
	
	/**
	 * This will take a MovieClip, and add its frames and labels to this builder. Labels will be added as an animation
	 * running from the label index to the next label. For example, if there is a label named "foo" at frame 0 and a label
	 * named "bar" at frame 10, in a MovieClip with 15 frames, it will add an animation named "foo" that runs from frame
	 * index 0 to 9, and an animation named "bar" that runs from frame index 10 to 14.
	 * <br/><br/>
	 * Note that this will iterate through the full MovieClip with actionsEnabled set to false, ending on the last frame.
	 * @method addMovieClip
	 * @param {MovieClip} source The source MovieClip to add to the sprite sheet.
	 * @param {Rectangle} sourceRect Optional. A rectangle defining the portion of the source to draw to the frame. If
	 * not specified, it will look for a getBounds method, frameBounds array, bounds property, or nominalBounds property
	 * on the source to use. If one is not found, the MovieClip will be skipped.
	 * @param {Number} scale Optional. The scale to draw the movie clip at. Default is 1.
	 **/
	p.addMovieClip = function(source, sourceRect, scale) {
		if (this._data) { throw SpriteSheetBuilder.ERR_RUNNING; }
		var rects = source.frameBounds;
		var rect = sourceRect||source.bounds||source.nominalBounds;
		if (!rect&&source.getBounds) { rect = source.getBounds(); }
		if (!rect && !rects) { return null; }
		
		var duration = source.timeline.duration;
		for (var i=0; i<duration; i++) {
			var r = (rects&&rects[i]) ? rects[i] : rect;
			this.addFrame(source, r, scale, function(frame) {
				var ae = this.actionsEnabled;
				this.actionsEnabled = false;
				this.gotoAndStop(frame);
				this.actionsEnabled = ae;
			}, [i], source);
		}
		var labels = source.timeline._labels;
		var lbls = [];
		for (var n in labels) {
			lbls.push({index:labels[n], label:n});
		}
		if (lbls.length) {
			lbls.sort(function(a,b){ return a.index-b.index; });
			for (var i=0,l=lbls.length; i<l; i++) {
				var label = lbls[i].label;
				var start = lbls[i].index;
				var end = (i == l-1) ? duration : lbls[i+1].index;
				var frames = [];
				for (var j=start; j<end; j++) { frames.push(j); }
				this.addAnimation(label, frames, true); // for now, this loops all animations.
			}
		}
	}
	
	/**
	 * Builds a SpriteSheet instance based on the current frames.
	 * @method build
	 * @return SpriteSheet The created SpriteSheet instance, or null if a build is already running or an error occurred.
	 **/
	p.build = function() {
		if (this._data) { throw SpriteSheetBuilder.ERR_RUNNING; }
		this._callback = null;
		this._startBuild();
		while (this._drawNext()) {}
		this._endBuild();
		return this.spriteSheet;
	}
	
	/**
	 * Asynchronously builds a SpriteSheet instance based on the current frames. It will run 20 times per second, using
	 * an amount of time defined by timeSlice. When it is complete it will call the specified callback.
	 * @method buildAsync
	 * @param {Function} callback Optional. The function to call when the build operation completes. It will be called
	 * with a single parameter providing a reference back to the builder.
	 * @param {Number} timeSlice Optional. A number from 0.01 to 1 that indicates what percentage of time the builder can use. This can be
	 * thought of as the number of seconds per second the builder will use. For example, with a timeSlice value of 0.3,
	 * the builder will run 20 times per second, using approximately 15ms per build (30% of available time, or 0.3s per second).
	 * Defaults to 0.3.
	 **/
	p.buildAsync = function(callback, timeSlice) {
		if (this._data) { throw SpriteSheetBuilder.ERR_RUNNING; }
		this._callback = callback;
		this._startBuild();
		this._timeSlice = Math.max(0.01, Math.min(0.99, timeSlice||0.3))*50;
		var _this = this;
		this._timerID = setTimeout(function() { _this._run(); }, 50-this._timeSlice);
	}
	
	/**
	 * Stops the current asynchronous build.
	 * @method stopAsync
	 **/
	p.stopAsync = function() {
		clearTimeout(this._timerID);
		this._data = null;
	}
	
	/**
	 * SpriteSheetBuilder instances cannot be cloned.
	 * @method clone
	 **/
	p.clone = function() {
		throw("SpriteSheetBuilder cannot be cloned.");
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[SpriteSheetBuilder]";
	}

// private methods:
	/**
	 * @method _startBuild
	 * @protected
	 **/
	p._startBuild = function() {
		var pad = this.padding||0;
		this.spriteSheet = null;
		this._index = 0;
		this._scale = this.scale;
		var dataFrames = [];
		this._data = {
			images: [],
			frames: dataFrames,
			animations: this._animations // TODO: should we "clone" _animations in case someone adds more animations after a build?
		};
		
		var frames = this._frames.slice();
		frames.sort(function(a,b) { return (a.height<=b.height) ? -1 : 1; });
		
		if (frames[frames.length-1].height+pad*2 > this.maxHeight) { throw SpriteSheetBuilder.ERR_DIMENSIONS; }
		var y=0, x=0;
		var img = 0;
		while (frames.length) {
			var o = this._fillRow(frames, y, img, dataFrames, pad);
			if (o.w > x) { x = o.w; }
			y += o.h;
			if (!o.h || !frames.length) {
				var canvas = createjs.createCanvas?createjs.createCanvas():document.createElement("canvas");
				canvas.width = this._getSize(x,this.maxWidth);
				canvas.height = this._getSize(y,this.maxHeight);
				this._data.images[img] = canvas;
				if (!o.h) {
					x=y=0;
					img++;
				}
			}
		}
	}
	
	/**
	 * @method _fillRow
	 * @protected
	 * @return {Number} The width & height of the row.
	 **/
	p._getSize = function(size,max) {
		var pow = 4;
		while (Math.pow(2,++pow) < size){}
		return Math.min(max,Math.pow(2,pow));
	}
	
	/**
	 * @method _fillRow
	 * @protected
	 * @return {Number} The width & height of the row.
	 **/
	p._fillRow = function(frames, y, img, dataFrames, pad) {
		var w = this.maxWidth;
		var maxH = this.maxHeight;
		var h = maxH-y;
		var x = pad;
		y += pad;
		var height = 0;
		for (var i=frames.length-1; i>=0; i--) {
			var frame = frames[i];
			var sc = this._scale*frame.scale;
			var rect = frame.sourceRect;
			var source = frame.source;
			var rx = Math.floor(sc*rect.x-pad);
			var ry = Math.floor(sc*rect.y-pad);
			var rh = Math.ceil(sc*rect.height+pad*2);
			var rw = Math.ceil(sc*rect.width+pad*2);
			if (rw > w) { throw SpriteSheetBuilder.ERR_DIMENSIONS; }
			if (rh > h || x+rw > w) { continue; }
			frame.img = img;
			frame.rect = new createjs.Rectangle(x,y,rw,rh);
			height = height || rh;
			frames.splice(i,1);
			dataFrames[frame.index] = [x,y,rw,rh,img,Math.round(-rx+sc*source.regX-pad),Math.round(-ry+sc*source.regY-pad)];
			x += rw;
		}
		return {w:x, h:height};
	}
	
	/**
	 * @method _endBuild
	 * @protected
	 **/
	p._endBuild = function() {
		this.spriteSheet = new createjs.SpriteSheet(this._data);
		this._data = null;
		if (this._callback) { this._callback(this); }
	}
	
	/**
	 * @method _run
	 * @protected
	 **/
	p._run = function() {
		var t = (new Date()).getTime()+this._timeSlice;
		var complete = false;
		while (t > (new Date()).getTime()) {
			if (!this._drawNext()) { complete = true; break; }
		}
		if (complete) {
			this._endBuild();
		} else {
			var _this = this;
			this._timerID = setTimeout(function() { _this._run(); }, 50-this._timeSlice);
		}
	}
	
	/**
	 * @method _drawNext
	 * @protected
	 * @return Boolean Returns false if this is the last draw.
	 **/
	p._drawNext = function() {
		var frame = this._frames[this._index];
		var sc = frame.scale*this._scale;
		var rect = frame.rect;
		var sourceRect = frame.sourceRect;
		var canvas = this._data.images[frame.img];
		var ctx = canvas.getContext("2d");
		frame.funct&&frame.funct.apply(frame.scope, frame.params);
		ctx.save();
		ctx.beginPath();
		ctx.rect(rect.x, rect.y, rect.width, rect.height);
		ctx.clip();
		ctx.translate(Math.ceil(rect.x-sourceRect.x*sc), Math.ceil(rect.y-sourceRect.y*sc));
		ctx.scale(sc,sc);
		frame.source.draw(ctx); // display object will draw itself.
		ctx.restore();
		return (++this._index) < this._frames.length;
	}

createjs.SpriteSheetBuilder = SpriteSheetBuilder;
}());/*
* DOMElement
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {
// TODO: fix problems with rotation.
// TODO: exclude from getObjectsUnderPoint

/**
 * <b>This class is still experimental, and more advanced use is likely to be buggy. Please report bugs.</b><br/><br/>
* A DOMElement allows you to associate a HTMLElement with the display list. It will be transformed
* within the DOM as though it is child of the Container it is added to. However, it is not rendered
* to canvas, and as such will retain whatever z-index it has relative to the canvas (ie. it will be
* drawn in front of or behind the canvas).<br/><br/>
* The position of a DOMElement is relative to their parent node in the DOM. It is recommended that
* the DOM Object be added to a div that also contains the canvas so that they share the same position
* on the page.<br/><br/>
* DOMElement is useful for positioning HTML elements over top of canvas content, and for elements
* that you want to display outside the bounds of the canvas. For example, a tooltip with rich HTML
* content.<br/><br/>
* DOMElement instances are not full EaselJS display objects, and do not participate in EaselJS mouse
* events or support methods like hitTest.
* @class DOMElement
* @extends DisplayObject
* @constructor
* @param {HTMLElement} htmlElement A reference or id for the DOM element to manage.
**/
var DOMElement = function(htmlElement) {
  this.initialize(htmlElement);
}
var p = DOMElement.prototype = new createjs.DisplayObject();

// public properties:
	/**
	 * The DOM object to manage.
	 * @property htmlElement
	 * @type HTMLElement
	 **/
	p.htmlElement = null;

// private properties:
	/**
	 * @property _style
	 * @protected
	 **/
	p._style = null;

// constructor:
	/**
	 * @property DisplayObject_initialize
	 * @type Function
   * @private
	 **/
	p.DisplayObject_initialize = p.initialize;

	/**
	 * Initialization method.
	 * @method initialize
	 * @protected
	*/
	p.initialize = function(htmlElement) {
		if (typeof(htmlElement)=="string") { htmlElement = document.getElementById(htmlElement); }
		this.DisplayObject_initialize();
		this.mouseEnabled = false;
		this.htmlElement = htmlElement;
		if (htmlElement) {
			this._style = htmlElement.style;
			this._style.position = "absolute";
			this._style.transformOrigin = this._style.WebkitTransformOrigin = this._style.msTransformOrigin = this._style.MozTransformOrigin = this._style.OTransformOrigin = "0% 0%";
		}
	}

// public methods:
	// TODO: fix this. Right now it's used internally to determine if it should be drawn, but DOMElement always needs to be drawn.
	/**
	 * Returns true or false indicating whether the display object would be visible if drawn to a canvas.
	 * This does not account for whether it would be visible within the boundaries of the stage.
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method isVisible
	 * @return {Boolean} Boolean indicating whether the display object would be visible if drawn to a canvas
	 **/
	p.isVisible = function() {
		return this.htmlElement != null;
	}

	/**
	 * Draws the display object into the specified context ignoring it's visible, alpha, shadow, and transform.
	 * Returns true if the draw was handled (useful for overriding functionality).
	 * NOTE: This method is mainly for internal use, though it may be useful for advanced uses.
	 * @method draw
	 * @param {CanvasRenderingContext2D} ctx The canvas 2D context object to draw into.
	 * @param {Boolean} ignoreCache Indicates whether the draw operation should ignore any current cache.
	 * For example, used for drawing the cache (to prevent it from simply drawing an existing cache back
	 * into itself).
	 **/
	p.draw = function(ctx, ignoreCache) {
		// TODO: possibly save out previous matrix values, to compare against new ones (so layout doesn't need to fire if there is no change)
		if (this.htmlElement == null) { return; }
		var mtx = this.getConcatenatedMatrix(this._matrix);
		
		var o = this.htmlElement;
		o.style.opacity = ""+mtx.alpha;
		// this relies on the _tick method because draw isn't called if a parent is not visible.
		o.style.visibility = this.visible ? "visible" : "hidden";
		o.style.transform = o.style.WebkitTransform = o.style.OTransform =  o.style.msTransform = ["matrix("+mtx.a,mtx.b,mtx.c,mtx.d,(mtx.tx+0.5|0),(mtx.ty+0.5|0)+")"].join(",");
		o.style.MozTransform = ["matrix("+mtx.a,mtx.b,mtx.c,mtx.d,(mtx.tx+0.5|0)+"px",(mtx.ty+0.5|0)+"px)"].join(",");
		return true;
	}

	/**
	 * Not applicable to DOMElement.
	 * @method cache
	 */
	p.cache = function() {}

	/**
	 * Not applicable to DOMElement.
	 * @method uncache
	 */
	p.uncache = function() {}

	/**
	 * Not applicable to DOMElement.
	 * @method updateCache
	 */
	p.updateCache = function() {}

	/**
	 * Not applicable to DOMElement.
	 * @method updateCache
	 */
	p.hitTest = function() {}

	/**
	 * Not applicable to DOMElement.
	 * @method localToGlobal
	 */
	p.localToGlobal = function() {}

	/**
	 * Not applicable to DOMElement.
	 * @method globalToLocal
	 */
	p.globalToLocal = function() {}

	/**
	 * Not applicable to DOMElement.
	 * @method localToLocal
	 */
	p.localToLocal = function() {}

	/**
	 * This presently clones the DOMElement instance, but not the associated HTMLElement.
	 * @method clone
	 * @return {DOMElement} a clone of the DOMElement instance.
	 **/
	p.clone = function() {
		var o = new DOMElement();
		this.cloneProps(o);
		return o;
	}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[DOMElement (name="+  this.name +")]";
	}

// private methods:
	p._tick = function(data) {
		if (this.htmlElement == null) { return; }
		this.htmlElement.style.visibility = "hidden";
		if (this.onTick) { this.onTick(data); }
	}

	/* Not needed with current setup:
	p._calculateVisible = function() {
		var p = this;
		while (p) {
			if (!p.visible) { return false; }
			p = p.parent;
		}
		return true;
	}
	*/
createjs.DOMElement = DOMElement;
}());/*
* Filter
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

// namespace:
this.createjs = this.createjs||{};

(function() {

/**
* Base class that all filters should inherit from.
* @class Filter
* @constructor
**/
var Filter = function() {
  this.initialize();
}
var p = Filter.prototype;
	
// constructor:
	/** 
	 * Initialization method.
	 * @method initialize
	 * @protected
	 **/
	p.initialize = function() {}
	
// public methods:
	/**
	 * Returns a rectangle with values indicating the margins required to draw the filter.
	 * For example, a filter that will extend the drawing area 4 pixels to the left, and 7 pixels to the right
	 * (but no pixels up or down) would return a rectangle with (x=-4, y=0, width=11, height=0).
	 * @method getBounds
	 * @return {Rectangle} a rectangle object indicating the margins required to draw the filter.
	 **/
	p.getBounds = function() {
		return new createjs.Rectangle(0,0,0,0);
	}
	
	/**
	 * Applies the filter to the specified context.
	 * @method applyFilter
	 * @param {CanvasRenderingContext2D} ctx The 2D context to use as the source.
	 * @param {Number} x The x position to use for the source rect.
	 * @param {Number} y The y position to use for the source rect.
	 * @param {Number} width The width to use for the source rect.
	 * @param {Number} height The height to use for the source rect.
	 * @param {CanvasRenderingContext2D} targetCtx Optional. The 2D context to draw the result to. Defaults to the context passed to ctx.
	 * @param {Number} targetX Optional. The x position to draw the result to. Defaults to the value passed to x.
	 * @param {Number} targetY Optional. The y position to draw the result to. Defaults to the value passed to y.
	 * @return {Boolean}
	 **/
	p.applyFilter = function(ctx, x, y, width, height, targetCtx, targetX, targetY) {}

	/**
	 * Returns a string representation of this object.
	 * @method toString
	 * @return {String} a string representation of the instance.
	 **/
	p.toString = function() {
		return "[Filter]";
	}
	
	
	/**
	 * Returns a clone of this Filter instance.
	 * @method clone
	 @return {Filter} A clone of the current Filter instance.
	 **/
	p.clone = function() {
		return new Filter();
	}
	
createjs.Filter = Filter;
}());/*
* Touch
* Visit http://createjs.com/ for documentation, updates and examples.
*
* Copyright (c) 2010 gskinner.com, inc.
* 
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
* 
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/


// namespace:
this.createjs = this.createjs||{};

(function() {

// TODO: support for double tap.
/**
 * Global utility for working with multi-touch enabled devices in EaselJS. Currently supports W3C Touch API
 * (iOS & modern Android browser) and IE10.
 * @class Touch
 * @static
 **/
var Touch = function() {
	throw "Touch cannot be instantiated";
};

// Public static methods:
	/**
	 * Returns true if touch is supported in the current browser.
	 * @method isSupported
	 * @return {Boolean} A boolean indicating whether touch is supported in the current browser.
	 * @static
	 **/
	Touch.isSupported = function() {
		return	('ontouchstart' in window) || // iOS
					(window.navigator['msPointerEnabled']); // IE10
	};

	/**
	 * Enables touch interaction for the specified EaselJS stage. Currently supports iOS (and compatible browsers, such
	 * as modern Android browsers), and IE10.
	 * Supports both single touch and multi-touch modes. Extends the EaselJS MouseEvent model, but without support for
	 * double click or over/out events. See MouseEvent.pointerID for more information.
	 * @method enable
	 * @param {Stage} stage The stage to enable touch on.
	 * @param {Boolean} singleTouch If true, only a single touch will be active at a time. Default is false.
	 * @param {Boolean} allowDefault If true, then default gesture actions (ex. scrolling, zooming) will be allowed when the user is interacting with the target canvas. Default is false.
	 * @return {Boolean} Returns true if touch was successfully enabled on the target stage.
	 * @static
	 **/
	Touch.enable = function(stage, singleTouch, allowDefault) {
		if (!stage || !stage.canvas || !Touch.isSupported()) { return false; }

		// inject required properties on stage:
		stage.__touch = {pointers:{}, multitouch:!singleTouch, preventDefault:!allowDefault, count:0};
		
		// note that in the future we may need to disable the standard mouse event model before adding
		// these to prevent duplicate calls. It doesn't seem to be an issue with iOS devices though.
		if ('ontouchstart' in window) { Touch._IOS_enable(stage); }
		else if (window.navigator['msPointerEnabled']) { Touch._IE_enable(stage); }
		return true;
	};
	
/**
	 * Removes all listeners that were set up when calling Touch.enable on a stage.
	 * @method disable
	 * @param {Stage} stage The stage to disable touch on.
	 * @static
	 **/
	Touch.disable = function(stage) {
		if (!stage) { return; }
		if ('ontouchstart' in window) { Touch._IOS_disable(stage); }
		else if (window.navigator['msPointerEnabled']) { Touch._IE_disable(stage); }
	};
	
// Private static methods:

	/**
	 * @method _IOS_enable
	 * @protected
	 * @param {Stage} stage
	 * @static
	 **/
	Touch._IOS_enable = function(stage) {
		var canvas = stage.canvas;
		var f = stage.__touch.f = function(e) { Touch._IOS_handleEvent(stage,e); };
		canvas.addEventListener("touchstart", f, false);
		canvas.addEventListener("touchmove", f, false);
		canvas.addEventListener("touchend", f, false);
		canvas.addEventListener("touchcancel", f, false);
	};
	
	/**
	 * @method _IOS_disable
	 * @protected
	 * @param {Stage} stage
	 * @static
	 **/
	Touch._IOS_disable = function(stage) {
		var canvas = stage.canvas;
		if (!canvas) { return; }
		var f = stage.__touch.f;
		canvas.removeEventListener("touchstart", f, false);
		canvas.removeEventListener("touchmove", f, false);
		canvas.removeEventListener("touchend", f, false);
		canvas.removeEventListener("touchcancel", f, false);
	};

	/**
	 * @method _IOS_handleEvent
	 * @protected
	 * @static
	 **/
	Touch._IOS_handleEvent = function(stage, e) {
		if (!stage) { return; }
		if (stage.__touch.preventDefault) { e.preventDefault&&e.preventDefault(); }
		var touches = e.changedTouches;
		var type = e.type;
		for (var i= 0,l=touches.length; i<l; i++) {
			var touch = touches[i];
			var id = touch.identifier;
			if (touch.target != stage.canvas) { continue; }
			
			if (type == "touchstart") {
				this._handleStart(stage, id, e, touch.pageX, touch.pageY);
			} else if (type == "touchmove") {
				this._handleMove(stage, id, e, touch.pageX, touch.pageY);
			} else if (type == "touchend" || type == "touchcancel") {
				this._handleEnd(stage, id, e);
			}
		}
	};
	
	/**
	 * @method _IE_enable
	 * @protected
	 * @param {Stage} stage
	 * @static
	 **/
	Touch._IE_enable = function(stage) {
		var canvas = stage.canvas;
		var f = stage.__touch.f = function(e) { Touch._IE_handleEvent(stage,e); };
		canvas.addEventListener("MSPointerDown", f, false);
		window.addEventListener("MSPointerMove", f, false);
		window.addEventListener("MSPointerUp", f, false);
		window.addEventListener("MSPointerCancel", f, false);
		if (stage.__touch.preventDefault) { canvas.style.msTouchAction = "none"; }
		stage.__touch.activeIDs = {};
	};
	
	/**
	 * @method _IE_enable
	 * @protected
	 * @param {Stage} stage
	 * @static
	 **/
	Touch._IE_disable = function(stage) {
		var f = stage.__touch.f;
		window.removeEventListener("MSPointerMove", f, false);
		window.removeEventListener("MSPointerUp", f, false);
		window.removeEventListener("MSPointerCancel", f, false);
		if (stage.canvas) {
			stage.canvas.removeEventListener("MSPointerDown", f, false);
		}
	};
	
	/**
	 * @method _IE_handleEvent
	 * @protected
	 * @static
	 **/
	Touch._IE_handleEvent = function(stage, e) {
		if (!stage) { return; }
		if (stage.__touch.preventDefault) { e.preventDefault&&e.preventDefault(); }
		var type = e.type;
		var id = e.pointerId;
		var ids = stage.__touch.activeIDs;
		
		if (type == "MSPointerDown") {
			if (e.srcElement != stage.canvas) { return; }
			ids[id] = true;
			this._handleStart(stage, id, e, e.pageX, e.pageY);
		} else if (ids[id]) { // it's an id we're watching
			if (type == "MSPointerMove") {
				this._handleMove(stage, id, e, e.pageX, e.pageY);
			} else if (type == "MSPointerUp" || type == "MSPointerCancel") {
				delete(ids[id]);
				this._handleEnd(stage, id, e);
			}
		}
	};
	
	
	/**
	 * @method _handleStart
	 * @protected
	 **/
	Touch._handleStart = function(stage, id, e, x, y) {
		var props = stage.__touch;
		if (!props.multitouch && props.count) { return; }
		var ids = props.pointers;
		if (ids[id]) { return; }
		ids[id] = true;
		props.count++;
		stage._handlePointerDown(id, e, x, y);
	};
	
	/**
	 * @method _handleMove
	 * @protected
	 **/
	Touch._handleMove = function(stage, id, e, x, y) {
		if (!stage.__touch.pointers[id]) { return; }
		stage._handlePointerMove(id, e, x, y);
	};
	
	/**
	 * @method _handleEnd
	 * @protected
	 **/
	Touch._handleEnd = function(stage, id, e) {
		// TODO: cancel should be handled differently for proper UI (ex. an up would trigger a click, a cancel would more closely resemble an out).
		var props = stage.__touch;
		var ids = props.pointers;
		if (!ids[id]) { return; }
		props.count--;
		stage._handlePointerUp(id, e, true);
		delete(ids[id]);
	};


createjs.Touch = Touch;
}());
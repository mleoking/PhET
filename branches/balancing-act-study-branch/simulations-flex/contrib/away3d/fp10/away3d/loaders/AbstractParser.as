package away3d.loaders
{
	import away3d.arcane;
	import away3d.containers.*;
	import away3d.core.base.*;
	import away3d.core.utils.*;
	import away3d.events.*;
	
	import flash.display.*;
	import flash.events.*;
	import flash.utils.*;
	
	use namespace arcane;
    
	 /**
	 * Dispatched when the 3d object parser completes a file parse successfully.
	 * 
	 * @eventType away3d.events.ParserEvent
	 */
	[Event(name="parseSuccess",type="away3d.events.ParserEvent")]
    			
	 /**
	 * Dispatched when the 3d object parser fails to parse a file.
	 * 
	 * @eventType away3d.events.ParserEvent
	 */
	[Event(name="parseError",type="away3d.events.ParserEvent")]
	    			
	 /**
	 * Dispatched when the 3d object parser progresses by one chunk.
	 * 
	 * @eventType away3d.events.ParserEvent
	 */
	[Event(name="parseProgress",type="away3d.events.ParserEvent")]
	
    /**
    * Abstract parsing object used as a base class for all loaders to extend from.
    */
	public class AbstractParser extends EventDispatcher
	{
		/** @private */
    	arcane var binary:Boolean;
		/** @private */
    	arcane var _totalChunks:int = 0;
        /** @private */
    	arcane var _parsedChunks:int = 0;
		/** @private */
    	arcane var _parsesuccess:ParserEvent;
		/** @private */
    	arcane var _parseerror:ParserEvent;
		/** @private */
    	arcane var _parseprogress:ParserEvent;
		/** @private */
    	arcane function notifyProgress():void
		{
        	_parseTime = getTimer() - _parseStart;
        	
        	if (_parseTime < parseTimeout) {
        		parseNext();
        	}else {
        		_parseStart = getTimer();
	        	
				if (!_parseprogress)
	        		_parseprogress = new ParserEvent(ParserEvent.PARSE_PROGRESS, this, container);
	        	
	        	dispatchEvent(_parseprogress);
        	}
		}
		/** @private */
    	arcane function notifySuccess():void
		{
			_broadcaster.removeEventListener(Event.ENTER_FRAME, update);
			
			if (!_parsesuccess)
        		_parsesuccess = new ParserEvent(ParserEvent.PARSE_SUCCESS, this, container);
        	
        	dispatchEvent(_parsesuccess);
		}
		/** @private */
    	arcane function notifyError():void
		{
			_broadcaster.removeEventListener(Event.ENTER_FRAME, update);
			
			if (!_parseerror)
        		_parseerror = new ParserEvent(ParserEvent.PARSE_ERROR, this, container);
        	
        	dispatchEvent(_parseerror);
		}
        /** @private */
		arcane function prepareData(data:*):void
        {
        }
        /** @private */
		arcane function parseNext():void
        {
        	notifySuccess();
        }
        
        private var _broadcaster:Sprite = new Sprite();
        private var _parseStart:int;
        private var _parseTime:int;
        
        private function update(event:Event):void
        {
        	parseNext();
        }
        
        /**
         * Instance of the Init object used to hold and parse default property values
         * specified by the initialiser object in the parser constructor.
         */
		protected var ini:Init;
		
        /**
        * 3d container object used for storing the parsed 3d object.
        */
		public var container:Object3D;
		
		/**
		 * Defines a timeout period for file parsing (in milliseconds).
		 */
		public var parseTimeout:int;
		
    	/**
    	 * Returns the total number of data chunks parsed
    	 */
		public function get parsedChunks():int
		{
			return _parsedChunks;
		}
    	
    	/**
    	 * Returns the total number of data chunks available
    	 */
		public function get totalChunks():int
		{
			return _totalChunks;
		}
		
		/**
		 * Creates a new <code>AbstractParser</code> object.
		 *
		 * @param	init	[optional]	An initialisation object for specifying default instance properties.
		 */
        public function AbstractParser(init:Object = null)
        {
        	ini = Init.parse(init);
        	
        	parseTimeout = ini.getNumber("parseTimeout", 40000);
        }
        
		/**
         * Parses 3d file data.
         * 
		 * @param	data		The file data to be parsed. Can be in text or binary form.
		 * 
         * @return				The parsed 3d object.
         */
        public function parse(data:*):Object3D
        {
        	_broadcaster.addEventListener(Event.ENTER_FRAME, update);
        	
        	prepareData(data);
        	
        	//start parsing
        	_parseStart = getTimer();
        	parseNext();
        	
        	return container;
        }
        
		/**
		 * Default method for adding a parseSuccess event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnSuccess(listener:Function):void
        {
            addEventListener(ParserEvent.PARSE_SUCCESS, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a parseSuccess event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnSuccess(listener:Function):void
        {
            removeEventListener(ParserEvent.PARSE_SUCCESS, listener, false);
        }
		
		/**
		 * Default method for adding a parseError event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnError(listener:Function):void
        {
            addEventListener(ParserEvent.PARSE_ERROR, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a parseError event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnError(listener:Function):void
        {
            removeEventListener(ParserEvent.PARSE_ERROR, listener, false);
        }
        
		/**
		 * Default method for adding a parseProgress event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function addOnProgress(listener:Function):void
        {
            addEventListener(ParserEvent.PARSE_PROGRESS, listener, false, 0, true);
        }
		
		/**
		 * Default method for removing a parseProgress event listener
		 * 
		 * @param	listener		The listener function
		 */
        public function removeOnProgress(listener:Function):void
        {
            removeEventListener(ParserEvent.PARSE_PROGRESS, listener, false);
        }
	}
}
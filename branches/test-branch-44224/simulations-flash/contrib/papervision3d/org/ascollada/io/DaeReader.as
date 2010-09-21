/*
 * Copyright 2007 (c) Tim Knip, ascollada.org.
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
 
package org.ascollada.io {
	import org.ascollada.core.DaeDocument;
	import org.ascollada.namespaces.collada;
	import org.ascollada.utils.Logger;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IOErrorEvent;
	import flash.events.ProgressEvent;
	import flash.events.TimerEvent;
	import flash.net.URLLoader;
	import flash.net.URLRequest;
	import flash.utils.Timer;

	/**
	 * @author Tim Knip
	 * 
	 */
	public class DaeReader extends EventDispatcher
	{
		use namespace collada;
		
		public var document:DaeDocument;
		
		public var async:Boolean;
		
		public var parseMessage : String;
		
		public var baseUrl : String;
		
		/**
		 * 
		 */
		public function DaeReader( async:Boolean = false )
		{
			this.async = async;
			
			_sourceTimer = new Timer(1);
			_sourceTimer.addEventListener( TimerEvent.TIMER, loadNextSource );
		}
		
		/**
		 * 
		 * @param	filename
		 */
		public function read( filename:String, fileSearchPaths : Array=null ):void
		{
			baseUrl = filename;
			
			if(filename.indexOf("/") != -1)
			{
				var parts : Array = filename.split("/");
				parts.pop();
				baseUrl = parts.join("/");
			}
			
			Logger.log( "reading: " + baseUrl );
			
			this.parseMessage = "reading COLLADA";
			
			_fileSearchPaths = fileSearchPaths;
			
			if( _sourceTimer.running )
				_sourceTimer.stop();
				
			var loader:URLLoader = new URLLoader();
			addListenersToLoader(loader);
			loader.load( new URLRequest(filename) );
		}
		
		
		/**
		 * Loads the COLLADA document.
		 * 
		 * @param	data
		 * @param fileSearchPaths
		 * 
		 * @return
		 */
		public function loadDocument( data:*, fileSearchPaths : Array=null ):DaeDocument
		{
			this.document = new DaeDocument( data, this.async );
			this.document.baseUrl = this.baseUrl;
			
			if(fileSearchPaths && fileSearchPaths.length)
			{
				for(var i : int = 0; i < fileSearchPaths.length; i++)
				{
					this.document.addFileSearchPath(fileSearchPaths[i]);
				}	
			}
			
			this.parseMessage = "reading data sources";
			
			_sourceTimer.start();
			
			return this.document;
		}
		
		/**
		 * 
		 * @param	event
		 * @return
		 */
		private function completeHandler( event:Event ):void
		{
			var loader:URLLoader = event.target as URLLoader;
			
			Logger.log( "complete!" );
			removeListenersFromLoader(loader);

			loadDocument( loader.data, _fileSearchPaths);
		}
		
		private function progressHandler( event:ProgressEvent ):void
		{
			if(hasEventListener(ProgressEvent.PROGRESS))
			{
				dispatchEvent(event);
			}
		}
		
		private function handleIOError( event:IOErrorEvent ):void
		{
			removeListenersFromLoader(URLLoader(event.target));
			dispatchEvent(event);
		}
		
		private function loadNextSource( event:TimerEvent ):void
		{
			var num : int = this.document.waitingSources ? this.document.waitingSources.length : 0;
			
			if(this.document.readNextSource()) 
			{
				dispatchEvent(new ProgressEvent(ProgressEvent.PROGRESS, false, false, this.document.numSources-num, this.document.numSources));
			}
			else 
			{
				_sourceTimer.stop();
				if(_sourceTimer.hasEventListener(TimerEvent.TIMER_COMPLETE))
				{
					_sourceTimer.removeEventListener(TimerEvent.TIMER_COMPLETE, loadNextSource);
				}
				
				this.document.addEventListener(Event.COMPLETE, onImagesComplete);
			
				this.parseMessage = "reading images";
				this.document.readNextImage();	
			}
		}
		
		/**
		 * 
		 */
		private function onImagesComplete(event : Event) : void
		{
			this.document.readAfterSources();
			
			if(this.document.hasEventListener(Event.COMPLETE))
			{
				this.document.removeEventListener(Event.COMPLETE, onImagesComplete);
			}
			
			dispatchEvent( new Event(Event.COMPLETE) );	
		}
		
		// added by harveysimon
		private function addListenersToLoader(loader:URLLoader):void
		{
			loader.addEventListener( Event.COMPLETE, completeHandler );
			loader.addEventListener( ProgressEvent.PROGRESS, progressHandler );
			loader.addEventListener( IOErrorEvent.IO_ERROR, handleIOError );
		}
		
		private function removeListenersFromLoader(loader:URLLoader):void
		{
			loader.removeEventListener( Event.COMPLETE, completeHandler );
			loader.removeEventListener( ProgressEvent.PROGRESS, progressHandler );
			loader.removeEventListener( IOErrorEvent.IO_ERROR, handleIOError );
		}
		
		private var _sourceTimer : Timer; 
		private var _fileSearchPaths : Array;
	}	
}

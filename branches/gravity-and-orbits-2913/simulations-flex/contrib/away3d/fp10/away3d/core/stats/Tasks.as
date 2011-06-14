package away3d.core.stats 
{
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.display.Sprite;
	import flash.events.Event;
	import flash.geom.Rectangle;
	import flash.system.System;
	import flash.utils.Dictionary;
	import flash.utils.getTimer;
	
    /**
     * 
     * Task monitor
     * 
     * @source http://code.google.com/p/mrdoob/wiki/stats
     * @author katopz@sleepydesign.com
     * 
     */	
    public class Tasks extends Sprite
    {
		public static var desc	:String = "";
		
		private var tasks	:Dictionary;
		private var times	:Dictionary;
		
		private var fs		:Number = 0;
		private var fps		:Number = 0;
		private var timer	:Number = 0;
		private var ms		:Number = 0;
		private var msPrev	:Number = 0;
		private var mem		:Number = 0;
		
		private var _width	:Number = 0;
		private var _height	:Number = 20;
		
		private var graphBitmap		:Bitmap;
		private var graphBitmapData	:BitmapData;
		
		public var isDirty	:Boolean = false;
		
		public static var instance : Tasks;
        public static function getInstance() : Tasks 
        {
            if ( instance == null ) instance = new Tasks();
            return instance as Tasks;
        }
        
        public function Tasks()
        {
        	tasks = new Dictionary(true);
        	times = new Dictionary(true);
        	
        	graphBitmap = new Bitmap();
        	addChild(graphBitmap);
        	
        	var logo:Logo = new Logo();
        	logo.x = 1;
        	logo.y = 1;
        	addChild(logo);
        	
        	_width = width+2;
        	
        	// default task
        	addTask("DESC", "000000", _width,	1, ""+desc);
        	addTask("FPS",	"CC0000", _width, 	1, "", 80);
        	addTask("MS",	"00CC00", _width, 	1, "", 60);
        	addTask("MEM",	"0000CC", _width, 	1, "", 90);
			
			// graph
			graphBitmapData = graphBitmap.bitmapData = new BitmapData(100, _height, false, 0x000000);
			
			addEventListener(Event.ENTER_FRAME, update, false, 0, true);
			
			draw();
        }
        
        public static function init(container:Sprite, desc:String = ""):Tasks
        {
        	if(!container)return null;
        	
        	Tasks.desc = desc;
        	
        	var tasks:Tasks = getInstance();
        	container.addChild(tasks);
        	return tasks;
        }
        
        public function addTask(id:String, rgb:String="", x:Number=-1, y:Number=1, text:String="", span:Number=-1):StaticTextField
        {
        	x = (x>0)?x:_width;
        	rgb = (rgb!="")?rgb:"FFFF00";
        	
        	text = (text!="")?text:"<FONT COLOR='#999999'> | </FONT><FONT COLOR='#"+rgb+"'><b>"+id+" : </b></FONT>";
        	
        	var _text:StaticTextField = new StaticTextField(text);
        	_text.x = x;
        	_text.y = y;
        	
        	span = (span>0)?span:Math.max(_text.width+20,60);
        	_width += span;
        	
        	addChild(_text);
        	tasks[id] = _text;
        	
        	_text.defaultText = text;
        	
        	draw();
        	
        	return _text;
        }
        
        public static function begin(id:String):void
        {
        	//mark
        	getInstance().times[id] = getTimer();
        }
        
        public static function end(id:String, rgb:String="FFFF00"):void
        {
        	//current 
        	var _time:Number = getTimer();
        	
        	//old
        	var tasks:Tasks = getInstance();
        	var time:Number = tasks.times[id];
        	var task:StaticTextField = tasks.tasks[id];
        	
        	//init
        	if(!task)
        		task = tasks.addTask(id, rgb);
        	
        	if(!time)
        		time = _time;
        		
        	//diff
        	if(tasks.isDirty)
        		tasks.graphBitmapData.setPixel(0, tasks.graphBitmapData.height - ((_time - time)*.5 << 0), Number("0x"+rgb));
        	
        	task.htmlText = task.defaultText + Number(_time-time);
        }
        
        private function update( event:Event ):void
        {
			if(!stage)return;
			
			timer = getTimer();
			fs+=4;
			
			isDirty = ( timer - 250 > msPrev );
			
			if(isDirty)
			{
				msPrev = timer;

				var fsGraph:Number = Math.min( _height, _height / stage.frameRate * fs );
				fps = Math.min( stage.frameRate, fs );
				
				mem = Number((System.totalMemory / 1048576).toFixed(3));
				
				var memGraph:Number =  Math.min(_height, Math.sqrt( Math.sqrt( mem * 5000 ))) - 2;
				
				graphBitmapData.scroll(1, 0);
				graphBitmapData.fillRect(new Rectangle(0, 0, 1, _height), 0x333333);
				
				//FPS
				graphBitmapData.setPixel(0, _height - fsGraph, 0xFF0000);
				//MS
				graphBitmapData.setPixel(0, _height - ((timer - ms)*.5 << 0), 0x00FF00);
				//MEM				
				graphBitmapData.setPixel(0, int(_height - memGraph), 0x0000FF);
				
				tasks["FPS"].htmlText = tasks["FPS"].defaultText + fps + "/" + stage.frameRate;
				tasks["MEM"].htmlText = tasks["MEM"].defaultText + mem;
				
				fs = 0;
			}
			
			tasks["MS"].htmlText = tasks["MS"].defaultText + (timer - ms);
			ms = timer;
        }
        
        private function draw():void
        {
        	// Graph
        	graphBitmap.x = _width;
        	
			// Background
			graphics.clear();
			graphics.beginFill(0xCCCCCC);
			graphics.drawRect(0, 0, width, _height);
			graphics.endFill();
			
			//Position
			if(stage)
				x = Math.max(0,stage.stageWidth*.5 - width*.5);
        }
    }
}
package away3d.core.stats 
{
	import away3d.cameras.*;
	import away3d.containers.*;
	import away3d.core.base.*;
	
	import flash.display.Graphics;
	import flash.display.Shape;
	import flash.display.Sprite;
	import flash.events.*;
	import flash.geom.ColorTransform;
	import flash.geom.Matrix;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	import flash.net.*;
	import flash.system.System;
	import flash.text.TextField;
	import flash.text.TextFormat;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	import flash.utils.*;
    
    public class Stats extends Sprite
    {
        private var totalElements:int = 0;
        private var meshes:int = 0;
        public var scopeMenu:View3D = null;
        public var displayMenu:Sprite = null;
        public var geomMenu:Sprite = null;
        private var lastrender:int;
        private var fpsLabel:StaticTextField;
        private var titleField:StaticTextField;
        private var perfLabel:StaticTextField;
        private var ramLabel:StaticTextField;
        private var swfframerateLabel:StaticTextField;
        private var avfpsLabel:StaticTextField;
        private var peakLabel:StaticTextField;
        private var faceLabel:StaticTextField;
        private var faceRenderLabel:StaticTextField;
        private var geomDetailsLabel:TextField;
        private var meshLabel:StaticTextField;
        private var fpstotal:int = 0;
        private var refreshes:int = 0;
        private var bestfps:int = 0;
        private var lowestfps:int = 999;
        private var bar:Sprite;
        private var barwidth:int = 0;
        private var closebtn:Sprite;
        private var cambtn:Sprite;
        private var clearbtn:Sprite;
        private var geombtn:Sprite;
        private var barscale:int = 0;
        private var stageframerate:Number;
        private var displayState:int;
        private var camLabel:TextField;
        private var camMenu:Sprite;
        private var camProp:Array;
        private var rectclose:Rectangle = new Rectangle(228,4,18,17);
        private var rectcam:Rectangle = new Rectangle(207,4,18,17);
        private var rectclear:Rectangle = new Rectangle(186,4,18,17);
        private var rectdetails:Rectangle = new Rectangle(165,4,18,17);
        private var geomLastAdded:String;
        private var defautTF:TextFormat = new TextFormat("Verdana", 10, 0x000000);
		private var defautTFBold:TextFormat = new TextFormat("Verdana", 10, 0x000000, true);
        //
        private const VERSION:String = "3";
        private const REVISION:String = "4.0";
        private const APPLICATION_NAME:String = "Away3D.com";
        
        public var sourceURL:String;
        
        private var menu0:ContextMenuItem;
        private var menu1:ContextMenuItem;
        private var menu2:ContextMenuItem;
         
        public function Stats(scope:View3D, framerate:Number = 0)
        {
            scopeMenu = scope;
            stageframerate = (framerate)? framerate : 30;
            displayState = 0;
            sourceURL = scope.sourceURL;
			tabEnabled = false;
            
            menu0 = new ContextMenuItem("Away3D Project stats", true, true, true);
            menu1 = new ContextMenuItem("View Source", true, true, true); 
            menu2 = new ContextMenuItem(APPLICATION_NAME+"\tv" + VERSION +"."+REVISION, true, true, true);
            
			var scopeMenuContextMenu:ContextMenu = new ContextMenu();
            scopeMenuContextMenu = new ContextMenu();
            scopeMenuContextMenu.customItems = sourceURL? [menu0, menu1, menu2] : [menu0, menu2];
            
            menu0.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, displayStats);
            menu1.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, viewSource);
            menu2.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, visitWebsite);
            
            scopeMenuContextMenu.hideBuiltInItems();
			scopeMenu.contextMenu = scopeMenuContextMenu;
        }
        
        public function addSourceURL(url:String):void
        {
        	sourceURL = url;
			var scopeMenuContextMenu:ContextMenu = new ContextMenu();
			scopeMenuContextMenu.customItems = sourceURL? [menu0, menu1, menu2] : [menu0, menu2];
			scopeMenu.contextMenu = scopeMenuContextMenu;
        }
        
        //Displays stats
        public function displayStats(e:ContextMenuEvent=null):void
        {
             if(!displayMenu){
             	scopeMenu.statsOpen = true;
                generateSprite();
                addEventMouse();
                //applyShadow();
             }
        }
        
        //Redirect to site
        public function visitWebsite(e:ContextMenuEvent):void 
        {
            var url:String = "http://www.away3d.com";
            var request:URLRequest = new URLRequest(url);
            try {
                navigateToURL(request);
            } catch (error:Error) {
                
            }
        }
        
        //View Source files
        public function viewSource(e:ContextMenuEvent):void 
        {
            var request:URLRequest = new URLRequest(sourceURL);
            try {
                navigateToURL(request, "_blank");
            } catch (error:Error) {
                
            }
        }
                
        //Closes stats and cleans up a bit...
        private function closeStats():void
        {
        	scopeMenu.statsOpen = false;
      		displayState = 0;
            scopeMenu.removeEventListener(MouseEvent.MOUSE_DOWN, onCheckMouse);
            scopeMenu.removeEventListener(MouseEvent.MOUSE_MOVE, updateTips);
            scopeMenu.removeChild(displayMenu);
            displayMenu = null;
        }
        
        //Mouse Events
        private function addEventMouse():void
        {  
            scopeMenu.addEventListener(MouseEvent.MOUSE_DOWN, onCheckMouse);
            scopeMenu.addEventListener(MouseEvent.MOUSE_MOVE, updateTips);
        }
        
        private function updateTips(me:MouseEvent):void
        { 
            if(scopeMenu != null){
                var x:Number = displayMenu.mouseX;
                var y:Number = displayMenu.mouseY;
                var pt:Point = new Point(x,y);
                try {
                    if(rectcam.containsPoint(pt)){
                        titleField.text = "CAMERA INFO";
                    } else if(rectclose.containsPoint(pt)){
                        titleField.text = "CLOSE STATS";
                    } else if(rectclear.containsPoint(pt)){
                        titleField.text = "CLEAR AVERAGES";
                    } else if(rectdetails.containsPoint(pt)){
                        titleField.text = "MESH INFO";
                    } else{
                        titleField.text = "AWAY3D PROJECT";
                    }
                } catch (e:Error) {
                    
                }
            }
        }
        
        
        private function onCheckMouse(me:MouseEvent):void
        { 
            var x:Number = displayMenu.mouseX;
            var y:Number = displayMenu.mouseY;
            var pt:Point = new Point(x,y);
            
            if(rectcam.containsPoint(pt)){
                if(displayState != 1){
                    closeOtherScreen(displayState);
                    displayState = 1;
                    showCamInfo();
                } else{
                    displayState = 0;
                    hideCamInfo();
                }
            } else if(rectdetails.containsPoint(pt)){
                if(displayState != 2){
                    closeOtherScreen(displayState);
                    displayState = 2;
                    showGeomInfo();
                } else{
                    displayState = 0;
                    hideGeomInfo();
                }
            } else if(rectclose.containsPoint(pt)){
                closeStats();
            } else if(rectclear.containsPoint(pt)){
                clearStats();
            } else{
            	if(displayMenu.mouseY<=20)
            	{
                	displayMenu.startDrag();
                	scopeMenu.addEventListener(MouseEvent.MOUSE_UP, mouseReleased);
             	}
            }
        }
        
        private function closeOtherScreen(actual:int):void {
             switch(actual){
                case 1:
                hideCamInfo();
                break;
                case 2:
                hideGeomInfo();
             }
        }
        
        private function mouseReleased(event:MouseEvent):void {
            displayMenu.stopDrag();
            scopeMenu.removeEventListener(MouseEvent.MOUSE_UP, mouseReleased);
        }
        
        //drawing the stats container
        private function generateSprite():void
        {  
          
            displayMenu = new Sprite();
            var myMatrix:Matrix = new Matrix();
            myMatrix.rotate(90 * Math.PI/180); 
            displayMenu.graphics.beginGradientFill("linear", [0x333366, 0xCCCCCC], [1,1], [0,255], myMatrix, "pad", "rgb", 0);
            displayMenu.graphics.drawRect(0, 0, 250, 86);
            
            displayMenu.graphics.beginFill(0x333366);
            displayMenu.graphics.drawRect(3, 3, 244, 20);
             
            scopeMenu.addChild(displayMenu);
             
            displayMenu.x -= displayMenu.width*.5;
            displayMenu.y -= displayMenu.height*.5;
            
            // generate closebtn
            closebtn = new Sprite();
            closebtn.graphics.beginFill(0x666666);
            closebtn.graphics.drawRect(0, 0, 18, 17);
            var cross:Sprite = new Sprite();
            cross.graphics.beginFill(0xC6D0D8);
            cross.graphics.drawRect(2, 7, 14, 4);
            cross.graphics.endFill();
            cross.graphics.beginFill(0xC6D0D8);
            cross.graphics.drawRect(7, 2, 4, 14);
            cross.graphics.endFill();
            cross.rotation = 45;
            cross.x+=9;
            cross.y-=4;
            closebtn.addChild(cross);
            displayMenu.addChild(closebtn);
            closebtn.x = 228;
            closebtn.y = 4;
            
            // generate cam btn
            cambtn = new Sprite();
            var cam:Graphics = cambtn.graphics;
            cam.beginFill(0x666666);
            cam.drawRect(0, 0, 18, 17);
            cam.endFill();
            cam.beginFill(0xC6D0D8);
            cam.moveTo(10,8);
            cam.lineTo(16,4);
            cam.lineTo(16,14);
            cam.lineTo(10,10);
            cam.lineTo(10,8);
            cam.drawRect(2, 6, 8, 6);
            cam.endFill();
            displayMenu.addChild(cambtn);
            cambtn.x = 207;
            cambtn.y = 4;
            
            // generate clear btn
            clearbtn = new Sprite();
            var clear_btn:Graphics = clearbtn.graphics;
            clear_btn.beginFill(0x666666);
            clear_btn.drawRect(0, 0, 18, 17);
            clear_btn.endFill();
            clear_btn.beginFill(0xC6D0D8);
            clear_btn.drawRect(6, 6, 6, 6);
            clear_btn.endFill();
            displayMenu.addChild(clearbtn);
            clearbtn.x = 186;
            clearbtn.y = 4;
            
            // generate geometrie btn
            geombtn = new Sprite();
            var geom_btn:Graphics = geombtn.graphics;
            geom_btn.beginFill(0x666666);
            geom_btn.drawRect(0, 0, 18, 17);
            geom_btn.endFill();
            geom_btn.beginFill(0xC6D0D8, 0.7);
            geom_btn.moveTo(3,4);
            geom_btn.lineTo(11,2);
            geom_btn.lineTo(16,5);
            geom_btn.lineTo(7,7);
            geom_btn.lineTo(3,4);
            geom_btn.beginFill(0x7D8489, 0.8);
            geom_btn.moveTo(3,4);
            geom_btn.lineTo(7,7);
            geom_btn.lineTo(7,16);
            geom_btn.lineTo(3,12);
            geom_btn.lineTo(3,4);
            geom_btn.beginFill(0xC6D0D8,1);
            geom_btn.moveTo(7,7);
            geom_btn.lineTo(16,5);
            geom_btn.lineTo(15,13);
            geom_btn.lineTo(7,16);
            geom_btn.lineTo(7,7);
            geom_btn.endFill();
             
            geom_btn.endFill();
            displayMenu.addChild(geombtn);
            geombtn.x = 165;
            geombtn.y = 4;
            
            // generate bar
            displayMenu.graphics.beginGradientFill("linear", [0x000000, 0xFFFFFF], [1,1], [0,255], new Matrix(), "pad", "rgb", 0);
            displayMenu.graphics.drawRect(3, 22, 244, 4);
            displayMenu.graphics.endFill();
            bar = new Sprite();
            bar.graphics.beginFill(0xFFFFFF);
            bar.graphics.drawRect(0, 0, 244, 4);
            displayMenu.addChild(bar);
            bar.x = 3;
            bar.y = 22;
            barwidth = 244;
            barscale = int(barwidth/stageframerate);
            
            // displays Away logo
            displayPicto();
            
            // Generate textfields
            // title
            titleField = new StaticTextField("AWAY3D PROJECT", new TextFormat("Verdana", 10, 0xFFFFFF, true));
            titleField.height = 20;
            titleField.width = 140;
            titleField.x = 22;
            titleField.y = 4;
            displayMenu.addChild(titleField);
            
            // fps
            var fpst:StaticTextField = new StaticTextField("FPS:",defautTFBold);
            fpsLabel = new StaticTextField();
            displayMenu.addChild(fpst);
            displayMenu.addChild(fpsLabel);
            fpst.x = 3;
            fpst.y = fpsLabel.y = 30;
            fpsLabel.x = fpst.x+fpst.width-2;
            
            //average perf
            var afpst:StaticTextField = new StaticTextField("AFPS:",defautTFBold);
            avfpsLabel = new StaticTextField();
            displayMenu.addChild(afpst);
            displayMenu.addChild(avfpsLabel);
            afpst.x = 52;
            afpst.y = avfpsLabel.y = fpsLabel.y;
            avfpsLabel.x = afpst.x+afpst.width-2;
            
            //Max peak
            var peakfps:StaticTextField = new StaticTextField("Max:",defautTFBold);
            peakLabel = new StaticTextField();
            displayMenu.addChild(peakfps);
            displayMenu.addChild(peakLabel);
            peakfps.x = 107;
            peakfps.y = peakLabel.y = avfpsLabel.y;
            peakfps.autoSize = "left";
            peakLabel.x = peakfps.x+peakfps.width-2;
            
            //MS
            var pfps:StaticTextField = new StaticTextField("MS:",defautTFBold);
            perfLabel = new StaticTextField();
            perfLabel.defaultTextFormat = defautTF;
            displayMenu.addChild(pfps);
            displayMenu.addChild(perfLabel);
            pfps.x = 177;
            pfps.y = perfLabel.y = fpsLabel.y;
            pfps.autoSize = "left";
            perfLabel.x = pfps.x+pfps.width-2;
             
            //ram usage
            var ram:StaticTextField = new StaticTextField("RAM:",defautTFBold);
            ramLabel = new StaticTextField();
            displayMenu.addChild(ram);
            displayMenu.addChild(ramLabel);
            ram.x = 3;
            ram.y = ramLabel.y = 46;
            ram.autoSize = "left";
            ramLabel.x = ram.x+ram.width-2;
            
            //meshes count
            var meshc:StaticTextField = new StaticTextField("MESHES:",defautTFBold);
            meshLabel = new StaticTextField();
            displayMenu.addChild(meshc);
            displayMenu.addChild(meshLabel);
            meshc.x = 90;
            meshc.y = meshLabel.y = ramLabel.y;
            meshc.autoSize = "left";
            meshLabel.x = meshc.x+meshc.width-2;
            
            //swf framerate
            var rate:StaticTextField = new StaticTextField("SWF FR:",defautTFBold);
            swfframerateLabel = new StaticTextField();
            displayMenu.addChild(rate);
            displayMenu.addChild(swfframerateLabel);
            rate.x = 170;
            rate.y = swfframerateLabel.y = ramLabel.y;
            rate.autoSize = "left";
            swfframerateLabel.x = rate.x+rate.width-2;
            
            //faces
            var faces:StaticTextField = new StaticTextField("T ELEMENTS:",defautTFBold);
            faceLabel = new StaticTextField();
            displayMenu.addChild(faces);
            displayMenu.addChild(faceLabel);
            faces.x = 3;
            faces.y = faceLabel.y = 62;
            faces.autoSize = "left";
            faceLabel.x = faces.x+faces.width-2;
            
            //shown faces
            var facesrender:StaticTextField = new StaticTextField("R ELEMENTS:",defautTFBold);
            faceRenderLabel = new StaticTextField();
            displayMenu.addChild(facesrender);
            displayMenu.addChild(faceRenderLabel);
            facesrender.x = 115;
            facesrender.y = faceRenderLabel.y = faceLabel.y;
            facesrender.autoSize = "left";
            faceRenderLabel.x = facesrender.x+facesrender.width-2;
        }
        
        public function updateStats(renderedfaces:int, camera:Camera3D):void
        {
            var now:int = getTimer();
            var perf:int = now - lastrender;
            lastrender = now;
            
            if (perf < 1000) {
                var fps:int = int(1000 / (perf+0.001));
                fpstotal += fps;
                refreshes ++;
                var average:int = fpstotal/refreshes;
                bestfps = (fps>bestfps)? fps : bestfps;
                lowestfps = (fps<lowestfps)? fps : lowestfps;
                var w:int = barscale*fps;
                bar.width = (w<=barwidth)? w : barwidth;
            }
            //color
            var procent:int = (bar.width/barwidth)*100;
            var colorTransform:ColorTransform = bar.transform.colorTransform;
            colorTransform.color =  255-(2.55*procent) << 16 | 2.55*procent << 8 | 40;
            bar.transform.colorTransform = colorTransform;
                
            if(displayState == 0){
                avfpsLabel.text = ""+average;
                ramLabel.text = ""+int(System.totalMemory/1024/102.4)/10+"MB";
                peakLabel.text = lowestfps+"/"+bestfps;
                fpsLabel.text = "" + fps; 
                perfLabel.text = "" + perf;
                faceLabel.text = ""+totalElements;
                faceRenderLabel.text = ""+renderedfaces;
                meshLabel.text = ""+meshes;
                swfframerateLabel.text = ""+stageframerate;
            } else if(displayState == 1){
                var caminfo:String = "";
                var _length:int = camProp.length;
                for(var i:int = 0;i<_length;++i){
                    try{
                        if(i>12){
                            caminfo += String(camera[camProp[i]])+"\n";
                        } else {
                            var info:String = String(camera[camProp[i]]);
                            caminfo += info.substring(0, 19)+"\n";
                        }
                    } catch(e:Error){
                        caminfo += "\n";
                    }
                }
                camLabel.text = caminfo;
            } else if(displayState == 2){
                geomDetailsLabel.text = stats;
                //geomDetailsLabel.scrollV = geomDetailsLabel.maxScrollV;
            }
        }
        
        //clear peaks
        private function clearStats():void
        {
            fpstotal = 0;
            refreshes = 0;
            bestfps = 0;
            lowestfps = 999;
        }
        
        //geometrie info
        private function showGeomInfo():void
        {
            if(geomMenu == null){
                createGeometryMenu();
            } else{
                displayMenu.addChild(geomMenu);
                geomMenu.y = 26;
            }
        }
        
        private function hideGeomInfo():void
        {   
            if(geomMenu != null){
                displayMenu.removeChild(geomMenu);
            }
        }
        private function createGeometryMenu():void{
            geomMenu = new Sprite();
            var myMatrix:Matrix = new Matrix();
            myMatrix.rotate(90 * Math.PI/180);
            geomMenu.graphics.beginGradientFill("linear", [0x333366, 0xCCCCCC], [1,1], [0,255], myMatrix, "pad", "rgb", 0);
            geomMenu.graphics.drawRect(0, 0, 250, 200);
            displayMenu.addChild(geomMenu);
            geomMenu.y = 26;
            geomDetailsLabel = new TextField();
            geomDetailsLabel.x = 3;
            geomDetailsLabel.y = 3;
            geomDetailsLabel.defaultTextFormat = defautTF;
            geomDetailsLabel.text = stats;
            geomDetailsLabel.height = 200;
            geomDetailsLabel.width = 235;
            geomDetailsLabel.multiline = true;
            geomDetailsLabel.selectable = true;
            geomDetailsLabel.wordWrap = true;
            geomMenu.addChild(geomDetailsLabel);
        }
        
        //cam info
        private function showCamInfo():void
        {
            if(camMenu == null){
                createCamMenu();
            } else{
                displayMenu.addChild(camMenu);
                camMenu.y = 26;
            }
        }
        
        private function hideCamInfo():void
        {   
            if(camMenu != null){
                displayMenu.removeChild(camMenu);
            }
        }
        
        // cam info menu
        private function createCamMenu():void
        {
            camMenu = new Sprite();
            var myMatrix:Matrix = new Matrix();
            myMatrix.rotate(90 * Math.PI/180);
            camMenu.graphics.beginGradientFill("linear", [0x333366, 0xCCCCCC], [1,1], [0,255], myMatrix, "pad", "rgb", 0);
            camMenu.graphics.drawRect(0, 0, 250, 220);
            displayMenu.addChild(camMenu);
            camMenu.y = 26;
            
            camLabel = new TextField();
            camLabel.height = 210;
            camLabel.width = 170;
            camLabel.multiline = true;
            camLabel.selectable = false;
            var tf:TextFormat = defautTF;
            tf.leading = 1.5;
            camLabel.defaultTextFormat = tf;
            camLabel.wordWrap = true;
            camMenu.addChild(camLabel);
            camLabel.x = 100;
            camLabel.y = 3;
            camProp = ["x","y","z","zoom","focus","distance","panangle","tiltangle","targetpanangle","targettiltangle","mintiltangle","maxtiltangle","steps","target"];
            //props
            var campropfield:TextField = new TextField();
            tf = new TextFormat("Verdana", 10, 0x000000, true);
            tf.leading = 1.5;
            tf.align = "right";
            campropfield.defaultTextFormat = tf;
            campropfield.x = campropfield.y = 3;
            campropfield.multiline = true;
            campropfield.selectable = false;
            campropfield.autoSize = "left";
            campropfield.height = 210;
            var _length:int = camProp.length;
            for(var i:int = 0;i<_length;++i){
                campropfield.appendText(camProp[i]+":\n");
            }
            camMenu.addChild(campropfield);
        }
        
        private function displayPicto():void
        {
            var logoShape:Shape = new Logo();
            displayMenu.addChild(logoShape);
            logoShape.x = logoShape.y = 4;
        }
        
        internal var type:String;
        internal var elementcount:int;
        internal var url:String;
        
        public var stats:String = "";
        
        public function clearObjects():void
        {
        	stats = "";
        	totalElements = 0;
        	meshes = 0;
        	geomLastAdded = "";
        }
        
        // registration faces and types
        public function addObject(node:Mesh):void
        {
        	type = node.type;
        	elementcount = node.elements.length;
        	url = node.url;
            if (type != null && elementcount != 0) {
                stats += " - " + type + " , elements: " + elementcount + ", url: " + url + "\n";
	            geomLastAdded = " - " + type + " , elements: " + elementcount + ", url: " + url + "\n";
                totalElements += elementcount;
                meshes += 1;
            } else {
            	stats += " - " + type + " , url: " + url + "\n";
            	geomLastAdded = " - " + type + " , url: " + url + "\n";
            }
        }
        
        //TODO: generateClipBoardInfo not implemented yet
        /*
        private function generateClipBoardInfo():void{
            var strReport:String = "-- AWAY3D STATS REPORT --\n\n";
            strReport+= "GEOMETRY:\n";
            strReport+= stats ;
            strReport+= "\nCAMERA:\n";
            var camera:Camera3D = scopeMenu.camera; 
            //System.setClipboard(strReport);
        }
        */
    }
}
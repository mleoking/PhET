package away3d.core.utils
{
    import away3d.arcane;
    import away3d.core.base.*;
    import away3d.core.math.*;
    import away3d.materials.*;
    import away3d.primitives.data.*;
    
    import flash.display.BitmapData;

	use namespace arcane;
	
    /** Convinient object initialization support */
    public class Init
    {
		/** @private */
        arcane var init:Object;

        public function Init(init:Object)
        {
            this.init = init;
        }

        public static function parse(init:Object):Init
        {
            if (init == null)
                return new Init(null);
            if (init is Init)
                return init as Init;

            inits.push(init);
            return new Init(init);
        }

        public function getInt(name:String, def:int, bounds:Object = null):int
        {
            if (init == null)
                return def;
        
            if (!init.hasOwnProperty(name))
                return def;
        
            var result:int = init[name];

            if (bounds != null)
            {
                if (bounds.hasOwnProperty("min"))
                {
                    var min:int = bounds["min"];
                    if (result < min)
                        result = min;
                }
                if (bounds.hasOwnProperty("max"))
                {
                    var max:int = bounds["max"];
                    if (result > max)
                        result = max;
                }
            }
        
            delete init[name];
        
            return result;
        }

        public function getNumber(name:String, def:Number, bounds:Object = null):Number
        {
            if (init == null)
                return def;
        
            if (!init.hasOwnProperty(name))
                return def;
        
            var result:Number = init[name];
                                        
            if (bounds != null)
            {
                if (bounds.hasOwnProperty("min"))
                {
                    var min:Number = bounds["min"];
                    if (result < min)
                        result = min;
                }
                if (bounds.hasOwnProperty("max"))
                {
                    var max:Number = bounds["max"];
                    if (result > max)
                        result = max;
                }
            }
        
            delete init[name];
        
            return result;
        }

        public function getString(name:String, def:String):String
        {
            if (init == null)
                return def;
        
            if (!init.hasOwnProperty(name))
                return def;
        
            var result:String = init[name];

            delete init[name];
        
            return result;
        }

        public function getBoolean(name:String, def:Boolean):Boolean
        {
            if (init == null)
                return def;
        
            if (!init.hasOwnProperty(name))
                return def;
        
            var result:Boolean = init[name];

            delete init[name];
        
            return result;
        }

        public function getObject(name:String, type:Class = null):Object
        {
            if (init == null)
                return null;
        
            if (!init.hasOwnProperty(name))
                return null;
        
            var result:Object = init[name];

            delete init[name];

            if (result == null)
                return null;

            if (type != null)
                if (!(result is type))
                    throw new CastError("Parameter \""+name+"\" is not of class "+type+": "+result);

            return result;
        }

        public function getObjectOrInit(name:String, type:Class = null):Object
        {
            if (init == null)
                return null;
        
            if (!init.hasOwnProperty(name))
                return null;
        
            var result:Object = init[name];

            delete init[name];

            if (result == null)
                return null;

            if (type != null)
                if (!(result is type))
                    return new type(result);

            return result;
        }

        public function getObject3D(name:String):Object3D
        {
            return getObject(name, Object3D) as Object3D;
        }

        public function getNumber3D(name:String):Number3D
        {
            return getObject(name, Number3D) as Number3D;
        }

        public function getPosition(name:String):Number3D
        {
            var value:Object = getObject(name);

            if (value == null)
                return null;

            if (value is Number3D)
                return value as Number3D;

            if (value is Object3D)
            {
                var o:Object3D = value as Object3D;
                return o.scene ? o.scenePosition : o.position;
            }

            if (value is String)
                if (value == "center")
                    return new Number3D();

            throw new CastError("Cast get position of "+value);
        }

        public function getArray(name:String):Array
        {
            if (init == null)
                return [];
        
            if (!init.hasOwnProperty(name))
                return [];
        
            var result:Array = init[name];

            delete init[name];
        
            return result;
        }

        public function getInit(name:String):Init
        {
            if (init == null)
                return new Init(null);
        
            if (!init.hasOwnProperty(name))
                return new Init(null);
        
            var result:Init = Init.parse(init[name]);

            delete init[name];
        
            return result;
        }
		
        public function getCubeMaterials(name:String):CubeMaterialsData
        {
            if (init == null)
                return null;
        
            if (!init.hasOwnProperty(name))
                return null;
        	
        	var result:CubeMaterialsData;
        	
        	if (init[name] is CubeMaterialsData)
        		result = init[name] as CubeMaterialsData;
        	else if (init[name] is Object)
        		result = new CubeMaterialsData(init[name]);

            delete init[name];
        
            return result;
        }
        
        public function getColor(name:String, def:uint):uint
        {
            if (init == null)
                return def;
        
            if (!init.hasOwnProperty(name))
                return def;
        
            var result:uint = Cast.color(init[name]);

            delete init[name];
        
            return result;
        }

        public function getBitmap(name:String):BitmapData
        {
            if (init == null)
                return null;
        
            if (!init.hasOwnProperty(name))
                return null;
        
            var result:BitmapData = Cast.bitmap(init[name]);

            delete init[name];
        
            return result;
        }

        public function getMaterial(name:String):IMaterial
        {
            if (init == null)
                return null;
        
            if (!init.hasOwnProperty(name))
                return null;
        
            var result:IMaterial = Cast.material(init[name]);

            delete init[name];
        
            return result;
        }

        public function getSegmentMaterial(name:String):ISegmentMaterial
        {
            if (init == null)
                return null;
        
            if (!init.hasOwnProperty(name))
                return null;
        
            var result:ISegmentMaterial = Cast.wirematerial(init[name]);

            delete init[name];
        
            return result;
        }

        private static var inits:Array = [];

        arcane function removeFromCheck():void
        {
            if (init == null)
                return;

            init["dontCheckUnused"] = true;
        }

        arcane function addForCheck():void
        {
            if (init == null)
                return;

            init["dontCheckUnused"] = false;
            inits.push(init);
        }

        arcane static function checkUnusedArguments():void
        {
            if (inits.length == 0)
                return;

            var list:Array = inits;
            inits = [];
            for each (var init:Object in list)
            {
                if (init.hasOwnProperty("dontCheckUnused"))
                    if (init["dontCheckUnused"])
                        continue;
                        
                var s:String = null;
                for (var name:String in init)
                {
                    if (name == "dontCheckUnused")
                        continue;

                    if (s == null)
                        s = "";
                    else
                        s +=", ";
                    s += name+":"+init[name];
                }
                if (s != null)
                {
                    Debug.warning("Unused arguments: {"+s+"}");
                }
            }
        }
    }
}

package away3d.loaders.utils
{
	import flash.display.Loader;
	import flash.net.URLRequest;
	import flash.system.LoaderContext;
	
	/**
	 * Used to store the name and loader reference of an external texture image.
	 */
	public class TextureLoader extends Loader
	{
		public function TextureLoader()
		{
			super();
		}
		
		private var _filename:String;
		
		public function get filename():String
		{
			return _filename;
		}
		
		override public function load(request:URLRequest, context:LoaderContext=null):void
		{
			_filename = request.url;
			super.load(request, context);
		}
	}
}
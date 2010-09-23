package edu.colorado.phet.flashcommon {
	
	import flash.display.LoaderInfo;
	
	public class CommonStrings {
		private static var document : XML;
		
		public static function init( li : LoaderInfo ):void {
			initDocument(new XML( li.parameters.internationalization ));
		}
        public static function initDocument(_document:XML): void {
            document =_document; 
        }
		
		public static function get( key : String, defaultString : String ) : String {
            if( document == undefined || document == null ) {
                return defaultString;
            }
			var list : XMLList = document.descendants( "string" );

			for each ( var item : XML in list ) {
				if( item.attribute( "key" ).toString() == key ) {
					return item.attribute( "value" ).toString();
				}
			}

			return defaultString;
		}
	}
}
package org.papervision3d
{
	import org.papervision3d.core.log.PaperLogger;
	

	/**
	* The Papervision3D class contains global properties and settings.
	*/
	public class Papervision3D
	{
		// ___________________________________________________________________ SETTINGS
		
		
		
		/**
		* Indicates if the angles are expressed in degrees (true) or radians (false). The default value is true, degrees.
		*/
		public static var useDEGREES  :Boolean = true;
	
		/**
		* Indicates if the scales are expressed in percent (true) or from zero to one (false). The default value is false, i.e. units.
		*/
		public static var usePERCENT  :Boolean = false;
	
		/**
		 * 
		 */
		public static var useRIGHTHANDED :Boolean = false;
		 
		// ___________________________________________________________________ STATIC
	
		/**
		* Enables engine name to be retrieved at runtime or when reviewing a decompiled swf.
		*/
		public static var NAME     :String = 'Papervision3D';
	
		/**
		* Enables version to be retrieved at runtime or when reviewing a decompiled swf.
		*/
		public static var VERSION  :String = '2.1 rev920';
	
		/**
		* Enables version date to be retrieved at runtime or when reviewing a decompiled swf.
		*/
		public static var DATE     :String = 'August 11th, 2009';
	
		/**
		* Enables copyright information to be retrieved at runtime or when reviewing a decompiled swf.
		*/
		public static var AUTHOR   :String = '(c) 2006-2008 Copyright by Carlos Ulloa | John Grden | Ralph Hauwert | Tim Knip | Andy Zupko';
		
		/**
		 * This is the main Logger Controller.
		 */
		public static var PAPERLOGGER : PaperLogger = PaperLogger.getInstance();
		
		
	}
}
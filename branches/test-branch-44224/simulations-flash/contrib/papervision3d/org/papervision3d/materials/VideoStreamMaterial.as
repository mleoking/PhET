/*
 *  PAPER    ON   ERVIS  NPAPER ISION  PE  IS ON  PERVI IO  APER  SI  PA
 *  AP  VI  ONPA  RV  IO PA     SI  PA ER  SI NP PE     ON AP  VI ION AP
 *  PERVI  ON  PE VISIO  APER   IONPA  RV  IO PA  RVIS  NP PE  IS ONPAPE
 *  ER     NPAPER IS     PE     ON  PE  ISIO  AP     IO PA ER  SI NP PER
 *  RV     PA  RV SI     ERVISI NP  ER   IO   PE VISIO  AP  VISI  PA  RV3D
 *  ______________________________________________________________________
 *  papervision3d.org  blog.papervision3d.org  osflash.org/papervision3d
 */

/*
 * Copyright 2006 (c) Carlos Ulloa Matesanz, noventaynueve.com.
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
 * @author Patrick Pietens
 * @author John Grden
 * @author Carlos Ulloa
 * 
 * @note Special thanks to Patrick Pietens for putting this together and flushing it out!
 */ 

// __________________________________________________________________________ VIDEO MATERIAL

package org.papervision3d.materials
{
	import flash.display.DisplayObject;
	import flash.events.NetStatusEvent;
	import flash.geom.Matrix;
	import flash.media.Video;
	import flash.net.NetStream;
	
	import org.papervision3d.core.render.draw.ITriangleDrawer;

	/*
	* The VideoMaterial class creates a texture from an existing Video instance and is for use with a Video and NetStream objects with an RTMP stream.
	* <p/>
	* The texture can be animated and/or transparent.
	* <p/>
	* Materials collects data about how objects appear when rendered.
	*/
	public class VideoStreamMaterial extends MovieMaterial implements ITriangleDrawer
	{		
		// ______________________________________________________________________ PUBLIC
	
		/**
		 * The NetStream and Vdeo that are used as a texture.
		 */		
		public var stream:NetStream;
		public var video:Video;
		
		
		// ______________________________________________________________________ NEW
	
		/**
		* The MovieMaterial class creates a texture from an existing Video instance.
		*
		* @param	video			A video object that display the FLV file
		* @param	stream			Stream that is used to play the FLV file
		* @param 	transparent		Whether we're using a transparent video or not. 
		*/
		public function VideoStreamMaterial ( video:Video, stream:NetStream , precise:Boolean = false, transparent:Boolean = false )
		{			
			// store the values
			this.stream = stream;
			this.video = video;
			animated = true;
			this.precise = precise;
			// init the material with a listener for the NS object 
			initMaterial ( video, stream );
						
			super ( DisplayObject(video), transparent );
		}
	

		// ______________________________________________________________________ INITIALISE
		
		/**
		 * Executes when the VideoMaterial is instantiated
		 */
		private function initMaterial ( video:Video, stream:NetStream ):void
		{
			
			stream.addEventListener ( NetStatusEvent.NET_STATUS, onStreamStatus );
		}
		

		// ______________________________________________________________________ UPDATE
	
		/**
		* Updates Video Bitmap
		*
		* Draws the current Video frame onto bitmap.
		*/	
		public override function updateBitmap ():void
		{
			try
			{
				// copies the scale properties of the video
				var myMatrix:Matrix = new Matrix();
				myMatrix.scale( this.video.scaleX, this.video.scaleY );

				// Fills the rectangle with a background color
				this.bitmap.fillRect ( this.bitmap.rect, this.fillColor );

				// Due to security reasons the BitmapData cannot access RTMP content like a NetStream using a FMS server.
				// The next three lines are a simple but effective workaround to get pass Flash its security sandbox.
				this.video.attachNetStream ( null );
				this.bitmap.draw( this.video, myMatrix, this.video.transform.colorTransform );
				this.video.attachNetStream ( this.stream );
			}catch(e:Error)
			{
				//
			}
		}
		
		
		// ______________________________________________________________________ STREAM STATUS
	
		/**
		* Executes when the status of the NetStream object changes
		*
		* @param Event that invoked the handler
		*/			
		private function onStreamStatus ( event:NetStatusEvent ):void
		{
			switch ( event.info.code )
			{
				case "NetStream.Play.Start":
					animated = true;
					break;
				case "NetStream.Unpause.Notify":
					animated = true;	
					break;
				case "NetStream.Play.Failed":
					animated = false;
					break;
				case "NetStream.Play.Stop":
					animated = false;
					break;
				case "NetStream.Play.StreamNotFound":
					animated = false;
					break;
				case "NetStream.Pause.Notify":
					animated = false;
					break;
			}			
		}	
		
		// ______________________________________________________________________ TO STRING
	
		/**
		* Returns a string value representing the material properties in the specified VideoMaterial object.
		*
		* @return	A string.
		*/
		public override function toString():String
		{
			return 'Texture:' + this.texture;
		}
		
		
	}
}
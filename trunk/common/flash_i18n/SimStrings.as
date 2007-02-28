/* SimStrings.as */

/**
 * SimStrings provides a mapping between string "keys" and their
 * localized values.  
 *
 * Caveats:
 * --------
 * The getSystemLocale method requires much more testing, on a variety
 * of systems and with different version of the Flash Player.  See 
 * the reference page for System.capabilities.language for an enumeration
 * of possible pitfalls.  If you prefer to set the locale explicitly
 * (perhaps based on user selection) you can do so in the constructor.
 *
 * Localization files:
 * -------------------
 * Key/value pairs are stored in XML files.
 * The XML files have names based on locale tags.  For example,
 * if we support English and French, then our XML file names
 * might be strings_en.xml and strings_fr.xml respectively.
 * The XML files have the following general format:
 *
 *   <?xml version="1.0" ?>
 *   <SimStrings>
 *     <string key="key1" value="value1">
 *     <string key="key2" value="value2">
 *   </SimStrings>
 *
 * The document can contain any number of <string> tags. 
 * If the document contains duplicate keys, then the first one
 * encountered is used.
 *
 * Usage:
 * -------------
 * 1. Create your UI components in Flash, and size them large enough
 *    to fit your longest text strings.  For labels, you can set 
 *    the autoSize parameter.
 *
 * 2. Create a layer named 'scripts'. This is where all the 
 *    related Actionscript will be placed.
 *
 * 3. In the first frame of the scripts layer, create a SimString:
 *
 *     var simStrings:SimStrings = new SimStrings("strings");
 *
 * 4. Two frames laters, wait for the XML file to be loaded:
 *
 *      if ( simStrings.isLoaded() == false ) {
 *         gotToAndPlay( prevFrame() );
 *      }
 *
 * 5. When the XML file has loaded, localize your UI components like this:
 *
 *    myLabel.text = simStrings.get("myLabelText"); // a Label instance
 *    myButton.label = simStrings.get("myButtonLabel"); // a Button instance
 *
 */
class SimStrings
{
  /** Suffix for XML files */
  private static var SUFFIX:String = ".xml";
  
  /** Basename of the XML files containing localized strings. */
  private var mBasename = null;
  
  /** Locale tag. See System.capabilities.properties doc. */ 
  private var mLocale = null;
  
  /** XML DOM document */
  private var mDocument:XML = null;
  
  /**
   * Constructor.  Omitting the locale parameter causes this
   * constructor to use the locale of the system that is running 
   * the Flash player.
   *
   * @param basename the basename of the XML files
   * @param locale the desired locale tag (optional) 
   */
  public function SimStrings( basename:String, locale:String )
  {
    mBasename = basename;
    mLocale = locale;
    if ( mLocale == null )
    {
      mLocale = getSystemLocale();
    }
    load();
  }
  
  /**
   * Gets the value for a specified key.
   * If there is no matching value, then the key itself is returned.
   *
   * @param key the key
   * @return the value, key if not found
   */
  public function get( key:String ):String
  {  
    var value:String = key;
    var nodes:Array = mDocument.firstChild.childNodes; // array of XMLNode
    var node:XMLNode = null;
    for ( var i = 0; i < nodes.length; i++ )
    {
      node = XMLNode( nodes[i] );
      if ( node.attributes.key == key )
      {
        value = node.attributes.value;
        if ( value != null )
        {
          break;
        }
      }
    }
    return value;
  }
   
  /** 
   * Gets the locale that is currently being used.
   * See System.capabilities.language for a list of valid locale tags.
   *
   * @return the locale tag
   */
  public function getLocale():String
  {
    return mLocale;
  }
  
  /**
   * Sets the locale tag. Note that no validation is performed,
   * since Flash doesn't provide us with a way to get the valid
   * set of locale tags.  If you provide a bogus locale, then
   * you won't find your strings.
   *
   * @param the locale tag.
   */
  public function setLocale( locale:String ):Void
  {
    mLocale = locale;
    load();
  }
  
  /**
   * Gets the basename of the XML files.
   *
   * @return the basename
   */
  public function getBasename():String
  {
    return mBasename;
  }
  
  /**
   * Sets the basename of the XML files.
   * 
   * @param basename the basename
   */
  public function setBasename( basename:String ):Void
  {
    mBasename = basename;
    load();
  }
  
  /** 
   * Gets the XML file name, which is based on the basename 
   * and locale.
   *
   * @return the filename
   */
  public function getFilename():String
  {
    return mBasename + "_" + mLocale + SUFFIX;
  }
  
  /**
   * Loads the XML file containing the strings for the current locale.
   * Note that loading of XML files occurs asynchronously in Flash.
   * You will need to call isLoaded before attempting to retrieve 
   * any strings.
   */
  public function load():Void
  {
    var filename:String = getFilename();
    
    mDocument = new XML();
    mDocument.ignoreWhite = true;
    
    // Adapted from the XML.status Actionscript Reference page.
    // Indicates problems in the Output window.
    mDocument.onLoad = function( success:Boolean )
      {
        // In this scope, 'this' reference to mDocument.
        var status:Number = this.status;
        
        if ( ! success )
        {
          trace("Unable to load XML file.");
        }
        else if ( status == 0 ) 
        {
          return;
        } 
        else 
        {
          trace("XML was loaded successfully, but contains parsing errors.");
      
          var errorMessage:String;
          switch (status) 
          {
          case -2 :
            errorMessage = "A CDATA section was not properly terminated.";
            break;
          case -3 :
            errorMessage = "The XML declaration was not properly terminated.";
            break;
          case -4 :
            errorMessage = "The DOCTYPE declaration was not properly terminated.";
            break;
          case -5 :
            errorMessage = "A comment was not properly terminated.";
            break;
          case -6 :
            errorMessage = "An XML element was malformed.";
            break;
          case -7 :
            errorMessage = "Out of memory.";
            break;
          case -8 :
            errorMessage = "An attribute value was not properly terminated.";
            break;
          case -9 :
            errorMessage = "A start-tag was not matched with an end-tag.";
            break;
          case -10 :
            errorMessage = "An end-tag was encountered without a matching start-tag.";
            break;
          default :
            errorMessage = "An unknown error has occurred.";
            break;
          }
          trace("status: "+ status + " (" + errorMessage + ")");
        } 
      } // onload
      
    // Load the XML file.
    mDocument.load( filename );
  }
  
  /**
   * Indicates whether the XML file has completed loading.
   *
   * @return true or false
   */
  public function isLoaded():Boolean
  {
    return mDocument.loaded;
  }
  
  /**
   * Indicates the result of attempting to load the XML file.
   * See XML.status() for a list of possible return values.
   * 0 indicates success, anything else is some type of failure.
   * This value is undefined until isLoaded returns true.
   *
   * @return 0 for success, others for failure
   */
  public function getStatus():Number
  {
    return mDocument.status;
  }
  
  /**
   * Gets the locale as configured on the System running the 
   * Flash Player.  See System.capabilities.language for a 
   * discussion of the pitfalls and platform dependencies.
   *
   * @return the locale tag
   */
  public function getSystemLocale():String
  {
    return System.capabilities.language;
  }

}

/* end of file */
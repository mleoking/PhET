import org.aswing.JTextComponent;
import org.aswing.util.Delegate;
import org.aswing.util.HashMap;

import TextField.StyleSheet;
/**
 * Handles CSS file loading process and initialize {@link org.aswing.JTextComponent}
 * instances with <code>StyleSheet</code> objects.
 * 
 * @author Sadovskiy
 */
class org.aswing.awml.CSSLoadManager {
	
	/** Singleton class instance */
	private static var instance:CSSLoadManager;
	
	/**
	 * Starts to load CSS file from the specified <code>cssURL</code> and
	 * initializes specified <code>textComponent</text> with style sheet
	 * once CSS file is loaded.
	 * 
	 * @param cssURL the URL of the css file to load
	 * @param textComponent the text component to initialize with loaded CSS
	 */
	public static function load(cssURL:String, textComponent:JTextComponent):Void {
		if (instance == null) {
			instance = new CSSLoadManager();	
		}
		instance.createCSSLoader(cssURL, textComponent);
	}
	
	/** Internal CSS loaders set */
	private var cssLoaders:HashMap;
	
	/**
	 * Private Constructor.
	 */
	private function CSSLoadManager() {
		cssLoaders = new HashMap();
	}

	/** 
	 * Creates CSS file loader and starts loading process 
	 */
	private function createCSSLoader(cssURL:String, textComponent:JTextComponent):Void {
		var css:StyleSheet = new StyleSheet();
		css.onLoad = Delegate.create(this, onCSSLoaded, css, textComponent);
		if (css.load(cssURL)) {
			cssLoaders.put(css, textComponent);	
		}
	}

	/**
	 * Handles CSS loading process.
	 */
	private function onCSSLoaded(success:Boolean, css:StyleSheet, textComponent:JTextComponent):Void {
		if (success) {
			textComponent.setCSS(css);	
		}
		cssLoaders.remove(css);
	}
	
}
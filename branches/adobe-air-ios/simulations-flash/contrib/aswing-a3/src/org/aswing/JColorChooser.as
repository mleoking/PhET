/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing { 

import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.geom.*;
import org.aswing.event.*;
import org.aswing.plaf.basic.BasicColorChooserUI;

/**
 * JColorChooser provides a pane of controls designed to allow a user to manipulate and 
 * select a color. 
 * <p>
 * You can add your color chooser pane to the chooser panels' tabbedPane container, or 
 * remove defaults.
 * @author iiley
 */
public class JColorChooser extends AbstractColorChooserPanel {
	
	private var chooserPanels:Array;
	private var tabbedPane:JTabbedPane;
	private var okButton:JButton;
	private var cancelButton:JButton;
	
	public function JColorChooser() {
		super();
		chooserPanels = new Array();
		tabbedPane    = new JTabbedPane();
		okButton      = new JButton("OK");
		cancelButton  = new JButton("Cancel");
		updateUI();
	}
	
	override public function updateUI():void{
		setUI(UIManager.getUI(this));
	}
	
    override public function getDefaultBasicUIClass():Class{
    	return org.aswing.plaf.basic.BasicColorChooserUI;
    }
	
	override public function getUIClassID():String{
		return "ColorChooserUI";
	}
	
	/**
	 * Returns the tabbedPane which contains the color chooser panels.
	 * @return the tabbedPane 
	 */
	public function getTabbedPane():JTabbedPane{
		return tabbedPane;
	}
	
	/**
	 * Returns the ok button to finish the choosing.
	 * @return the ok button
	 */
	public function getOkButton():JButton{
		return okButton;
	}
	
	/**
	 * Returns the cancel button which use to cancel the choosing.
	 * @return the cancel button
	 */
	public function getCancelButton():JButton{
		return cancelButton;
	}
	
	/**
	 * Adds a chooser panel to the tabbedPane.
	 * <p>
	 * By default, the LAFs implements will add default chooser panels.
	 * @param tabTitle the tab title
	 * @param panel the chooser panel
	 * @see #removeChooserPanel()
	 */
	public function addChooserPanel(tabTitle:String, panel:AbstractColorChooserPanel):void{
		getTabbedPane().appendTab(panel, tabTitle);
		panel.setModel(getModel());
	}
	
	/**
	 * Removes a chooser panel from the tabbedPane.
	 * @param panel the chooser panel to be removed
	 * @return the removed chooser panel, null means the panel is not in the tabbedPane.
	 * @see #addChooserPanel()
	 */
	public function removeChooserPanel(panel:AbstractColorChooserPanel):AbstractColorChooserPanel{
		return AbstractColorChooserPanel(getTabbedPane().remove(panel));
	}
	
	/**
	 * Removes a chooser panel by index from the tabbedPane.
	 * @param index the index of the chooser panel to be removed
	 * @return the removed chooser panel, null means no such index exists.
	 * @see #addChooserPanel()
	 */
	public function removeChooserPanelAt(index:Number):AbstractColorChooserPanel{
		return AbstractColorChooserPanel(getTabbedPane().removeAt(index));
	}
	
	/**
	 * Removes all color chooser panels.
	 */
	public function removeAllChooserPanels():void{
		getTabbedPane().removeAll();
	}
	
	/**
	 * Gets a chooser panel by index from the tabbedPane.
	 * @param index the index of the chooser panel
	 * @return the chooser panel, null means no such index exists.
	 * @see #addChooserPanel()
	 */
	public function getChooserPanelAt(index:Number):AbstractColorChooserPanel{
		return AbstractColorChooserPanel(getTabbedPane().getComponent(index));
	}
	
	/**
	 * Returns the count of chooser panels.
	 * @return the count of chooser panels.
	 */
	public function getChooserPanelCount():Number{
		return getTabbedPane().getComponentCount();
	}
	
	/**
	 * Shows a modal color-chooser dialog to let user to choose a color. 
	 * If the user presses the "OK" button, then the dialog will be disposed
	 * and invoke the <code>okHandler(selectedColor:ASColor)</code>. 
	 * If the user presses the "Cancel" button or closes 
	 * the dialog without pressing "OK", then the dialog will be disposed and 
	 * invoke the <code>cancelHandler()</code>.
	 * <p>
	 * <code>selectedColor</code> may be null, null means user selected no-color.
	 * <p>
	 * This method always create new Dialog and new JColorChooser, so its openning 
	 * may performance slowly.
	 * @param component determines the Frame in which the
	 *		dialog is displayed; can be null
	 * @param title the title of the box
	 * @param initialColor the initial color when start choosing
	 * @param (optional)modal is the window modal, default is true
	 * @param (optional)okHandler the function will be called when user finished the choosing with a specified color.
	 * 			the selected color will be passed to the function as a parammeter.
	 * @param (optional)cancelHandler the function will be called when user canceled the choosing
	 * @param (optional)location the location of the dialog to show, default is the center of the screen
	 * @return the color chooser
	 */
	public static function showDialog(component:Component, title:String, initialColor:ASColor, modal:Boolean=true, 
										okHandler:Function=null, cancelHandler:Function=null, location:IntPoint=null):JColorChooser{
		var chooser:JColorChooser = new JColorChooser();
		chooser.setSelectedColor(initialColor);
		var frame:JFrame = createDialog(chooser, 
				component, title, modal, 
				okHandler, cancelHandler
		);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		if(location == null){
			location = AsWingUtils.getScreenCenterPosition();
		}
		location.x = Math.round(location.x - frame.getWidth()/2);
		location.y = Math.round(location.y - frame.getHeight()/2);
		frame.setLocation(location);
		frame.show();
		return chooser;
	}
	
	/**
	 * Creates a dialog to be the color chooser dialog. 
	 * If the user presses the "OK" button, then the dialog will be disposed
	 * and invoke the <code>okHandler(selectedColor:ASColor)</code>. 
	 * If the user presses the "Cancel" button or closes 
	 * the dialog without pressing "OK", then the dialog will be disposed and 
	 * invoke the <code>cancelHandler()</code>.
	 * <p>
	 * <code>selectedColor</code> may be null, null means user selected no-color.
	 * <p>
	 * This dialog will be default to HIDE_ON_CLOSE, so it can be opened very fast 
	 * after first openning.
	 * @param chooser the color chooser
	 * @param component determines the Frame in which the
	 *		dialog is displayed; can be null
	 * @param title the title of the box
	 * @param (optional)modal is the window modal, default is true
	 * @param (optional)okHandler the function will be called when user finished the choosing with a specified color.
	 * 			the selected color will be passed to the function as a parammeter.
	 * @param (optional)cancelHandler the function will be called when user canceled the choosing
	 * @return the chooser dialog
	 */	
	public static function createDialog(chooser:JColorChooser, component:Component, title:String, modal:Boolean=true, 
										okHandler:Function=null, cancelHandler:Function=null):JFrame{
		var frame:JFrame = new JFrame(AsWingUtils.getOwnerAncestor(component), title, modal);
		
		frame.setContentPane(chooser);
		frame.setResizable(false);
		
		chooser.getOkButton().addActionListener(function():void{
			if(okHandler != null) okHandler(chooser.getSelectedColor());
			frame.tryToClose();
		});
		chooser.getCancelButton().addActionListener(function():void{
			frame.tryToClose();
			if(cancelHandler != null) cancelHandler();
		});
		frame.addEventListener(FrameEvent.FRAME_CLOSING, function():void{
			if(cancelHandler != null) cancelHandler();
		});
		frame.pack();
		return frame;
	}
}
}
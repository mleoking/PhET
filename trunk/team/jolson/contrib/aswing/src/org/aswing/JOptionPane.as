﻿/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.ASWingUtils;
import org.aswing.border.EmptyBorder;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.FlowLayout;
import org.aswing.geom.Point;
import org.aswing.Icon;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JTextField;
import org.aswing.JWindow;
import org.aswing.SoftBox;

/**
 * JOptionPane makes it easy to pop up a standard dialog box that prompts users 
 * for a value or informs them of something.
 * <p>
 * There's some shortcut to do there with static methods like <code>showMessageDialog</code>, 
 * <code>showInputDialog</code>. But if you want to make a hole control of JOptionPane, 
 * you can create a JOptionPane by yourself and append it into a your JFrame.
 * @see #showMessageDialog()
 * @see #showInputDialog()
 * @author iiley
 */
class org.aswing.JOptionPane extends JPanel {
	public static var OK_STR:String = "OK";
	public static var CANCEL_STR:String = "Cancel";
	public static var YES_STR:String = "Yes";
	public static var NO_STR:String = "No";
	public static var CLOSE_STR:String = "Close";
	
	
	public static var OK:Number = 1; //00001
	public static var CANCEL:Number = 2; //00010
	public static var YES:Number = 4; //00100
	public static var NO:Number = 8; //01000
	public static var CLOSE:Number = 16; //10000
	
	private var okButton:JButton;
	private var cancelButton:JButton;
	private var yesButton:JButton;
	private var noButton:JButton;
	private var closeButton:JButton;
	
	private var centerPane:JPanel;
	private var msgLabel:JLabel;
	private var inputText:JTextField;
	private var buttonPane:JPanel;
	private var frame:JFrame;
	
	public function JOptionPane() {
		super(new BorderLayout());
		centerPane = SoftBox.createVerticalBox(6);
		msgLabel = new JLabel();
		centerPane.append(ASWingUtils.createPaneToHold(msgLabel, new FlowLayout(FlowLayout.CENTER, 5, 5)));
		inputText = new JTextField();
		var inputContainer:JPanel = new JPanel(new BorderLayout());
		inputContainer.setBorder(new EmptyBorder(null, new Insets(0, 8, 0, 8)));
		inputContainer.append(inputText, BorderLayout.CENTER);
		centerPane.append(inputContainer);
		buttonPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		append(centerPane, BorderLayout.CENTER);
		append(buttonPane, BorderLayout.SOUTH);
	}
	
	public function getFrame():JFrame{
		return frame;
	}
	
	public function getInputText():JTextField{
		return inputText;
	}
	
	public function getMsgLabel():JLabel{
		return msgLabel;
	}
	
	public function getOkButton():JButton{
		if(okButton == null){
			okButton = new JButton(OK_STR);
		}
		return okButton;
	}
	
	public function getCancelButton():JButton{
		if(cancelButton == null){
			cancelButton = new JButton(CANCEL_STR);
		}
		return cancelButton;
	}	
	public function getYesButton():JButton{
		if(yesButton == null){
			yesButton = new JButton(YES_STR);
		}
		return yesButton;
	}
	
	public function getNoButton():JButton{
		if(noButton == null){
			noButton = new JButton(NO_STR);
		}
		return noButton;
	}	
	public function getCloseButton():JButton{
		if(closeButton == null){
			closeButton = new JButton(CLOSE_STR);
		}
		return closeButton;
	}	
	
	public function addButton(button:JButton):Void{
		buttonPane.append(button);
	}
	private function setMessage(msg:String):Void{
		msgLabel.setText(msg);
	}
	private function setIcon(icon:Icon):Void{
		msgLabel.setIcon(icon);
	}	
	private function addCloseListener(button:JButton):Void{
		var f:JFrame = getFrame();
		button.addActionListener(function(){ f.tryToClose(); });
	}
	
	/**
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, parentComponent:Component, modal:Boolean, icon:Icon, buttons:Number)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, parentComponent:Component, modal:Boolean, icon:Icon)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, parentComponent:Component, modal:Boolean)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, parentComponent:Component)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function)
	 * <p>
	 * Show a message box with specifield title, message, and icon.
	 * <p>
	 * for example:
	 * <pre>
	 * var handler:Function = Delegate.create(this, __whenUserConformed);
	 * var pane:JOptionPane = showMessageDialog("title", "is that OK?", handler, null, JOptionPane.YES|JOptionPane.NO);
	 * </pre>
	 * will show a message box with yes and no buttons.
	 * 
	 * @param title the title of the box, can be null
	 * @param msg the message , can be null
	 * @param finishHandler the function(result:Number) to call when user finished input, 
	 * will pass a number as parammeter, its value indicate what user's selection. 
	 * For example CANCEL indicate that user pressed CANCEL button, CLOSE indicate that 
	 * user pressed CLOSE button or just closed the frame by click frame's close button, 
	 * YES indicate that user pressed the YES button etc.
	 * @param parentComponent determines the Frame in which the
	 *		dialog is displayed; can be null
	 * @param modal is the window modal, default is true
	 * @param icon the icon, can be null
	 * @param buttons which buttons need to show.
	 * @param closeBox is close the box frame when user click buttons
	 * @see #OK
	 * @see #CANCEL
	 * @see #YES
	 * @see #NO
	 * @see #CLOSE
	 */
	public static function showMessageDialog(title:String, msg:String, finishHandler:Function, parentComponent:Component, modal:Boolean, icon:Icon, buttons:Number):JOptionPane{
		if(modal == undefined) modal = true;
		var frame:JFrame = new JFrame(ASWingUtils.getOwnerAncestor(parentComponent), title, modal);
		var pane:JOptionPane = new JOptionPane();
		pane.getInputText().setVisible(false);
		pane.setMessage(msg);
		pane.setIcon(icon);
		pane.frame = frame;
		if(buttons == undefined){
			buttons = OK;
		}
		var handler:Function = finishHandler;
		if((buttons & OK) == OK){
			pane.addButton(pane.getOkButton());
			pane.addCloseListener(pane.getOkButton());
			pane.getOkButton().addActionListener(function(){
				handler(JOptionPane.OK);
			});
		}
		if((buttons & YES) == YES){
			pane.addButton(pane.getYesButton());
			pane.addCloseListener(pane.getYesButton());
			pane.getYesButton().addActionListener(function(){
				handler(JOptionPane.YES);
			});
		}
		if((buttons & NO) == NO){
			pane.addButton(pane.getNoButton());
			pane.addCloseListener(pane.getNoButton());
			pane.getNoButton().addActionListener(function(){
				handler(JOptionPane.NO);
			});
		}
		if((buttons & CANCEL) == CANCEL){
			pane.addButton(pane.getCancelButton());
			pane.addCloseListener(pane.getCancelButton());
			pane.getCancelButton().addActionListener(function(){
				handler(JOptionPane.CANCEL);
			});
		}
		if((buttons & CLOSE) == CLOSE){
			pane.addButton(pane.getCloseButton());
			pane.addCloseListener(pane.getCloseButton());
			pane.getCloseButton().addActionListener(function(){
				handler(JOptionPane.CLOSE);
			});
		}
		
		frame.addEventListener(JFrame.ON_WINDOW_CLOSING, 
			function(){
				handler(JOptionPane.CLOSE);
			});
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().append(pane, BorderLayout.CENTER);
		frame.pack();
		var location:Point = ASWingUtils.getScreenCenterPosition();
		location.x = Math.round(location.x - frame.getWidth()/2);
		location.y = Math.round(location.y - frame.getHeight()/2);
		frame.setLocation(location);
		frame.show();
		return pane;
	}
	
	/**
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, defaultValue:String, parentComponent:Component, modal:Boolean, icon:Icon)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, defaultValue:String, parentComponent:Component, modal:Boolean)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, defaultValue:String, parentComponent:Component)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function, defaultValue:String)<br>
	 * showMessageDialog(title:String, msg:String, finishHandler:Function)
	 * <p>
	 * Show a message box with specifield title, message, and icon and a TextField to requare 
	 * user to input a string.
	 * <p>
	 * for example:
	 * <pre>
	 * var handler:Function = Delegate.create(this, __whenUserEntered);
	 * var pane:JOptionPane = showMessageDialog("title", "Please enter your name:", handler, "yournamehere");
	 * </pre>
	 * will show a message box with OK and CANCEL and a input Texts.
	 * 
	 * @param title the title of the box, can be null
	 * @param msg the message , can be null
	 * @param finishHandler the function(result:String) to call when user finished input, 
	 * will pass a string as parammeter, if it is null indicate that user had pressed 
	 * cancel or just closed the frame, otherwise it is the string what user entered.
	 * @param defaultValue the default value for the input
	 * @param parentComponent determines the Frame in which the
			dialog is displayed; can be null
	 * @param modal is the window modal, default is true
	 * @param icon the icon, can be null
	 */
	public static function showInputDialog(title:String, msg:String, finishHandler:Function, defaultValue:String, parentComponent:Component, modal:Boolean, icon:Icon):JOptionPane{
		if(modal == undefined) modal = true;
		var frame:JFrame = new JFrame(ASWingUtils.getOwnerAncestor(parentComponent), title, modal);
		var pane:JOptionPane = new JOptionPane();
		if(defaultValue != undefined){
			pane.inputText.setText(defaultValue);
		}
		pane.setMessage(msg);
		pane.setIcon(icon);
		pane.frame = frame;
		
		pane.addButton(pane.getOkButton());
		pane.addCloseListener(pane.getOkButton());
		pane.addButton(pane.getCancelButton());
		pane.addCloseListener(pane.getCancelButton());
		
		var hander:Function = finishHandler;
		
		pane.getOkButton().addActionListener(
			function(){
				hander(pane.getInputText().getText());
			}
		);
		
		var cancelHandler:Function = function(){
			hander(null);
		};
		
		pane.getCancelButton().addActionListener(cancelHandler);
		frame.addEventListener(JFrame.ON_WINDOW_CLOSING, cancelHandler);
			
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().append(pane, BorderLayout.CENTER);
		frame.pack();
		var location:Point = ASWingUtils.getScreenCenterPosition();
		location.x = Math.round(location.x - frame.getWidth()/2);
		location.y = Math.round(location.y - frame.getHeight()/2);
		frame.setLocation(location);
		frame.show();
		return pane;
	}
}

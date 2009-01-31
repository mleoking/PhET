/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.Component;
import org.aswing.resizer.DefaultResizer;
import org.aswing.resizer.Resizer;
import org.aswing.util.HashMap;

/**
 * ResizerController make manage resizer feature easily.
 * 
 * {@code
 *  var button : JButton = new JButton("click");
 *  button.setSize( 100,25 );
 * 	button.setLocation( 100, 100 );
 * 	
 * 	var label1 : JLabel = new JLabel("avoid resizing");
 * 	label1.setSize(100, 25);
 * 	label1.setLocation( 200, 50 );
 * 	
 *  var label2 : JLabel = new JLabel("ok for resizing");
 * 	label2.setSize(100, 25);
 * 	label2.setLocation( 50, 50 );
 * 	label2.setOpaque( true );
 * 
 * 	try
 * 	{
 * 		// Direct affectation
 * 		var resizer : ResizerController = ResizerController.init( button );
 * 		
 * 		// Simple call to #init method
 * 		ResizerController.init( label1 );
 * 		ResizerController.init( label2 );
 * 	}
 * 	catch( e : Error )
 * 	{
 * 		// log error message
 * 	}
 * 	
 * 	//We can retreive ResizerController for a specific component like :
 * 	var labelResizer : ResizerController = ResizerController.getController( label1 );
 * 	labelResizer.setResizable( false );
 * 	
 * 	resizer.setResizeDirectly( true );
 * 	
 * 	// content must have a layout set to EmptyLayout to allow correct resizing
 * 	content.append( button );
 * 	content.append( label1 );
 * 	content.append( label2 );
 * }
 * <p>
 * Many thanks Romain Ecarnot, this class is based on his ResizerController, and all resizer implement 
 * are inspired by him.
 * @author Romain Ecarnot
 * @author iiley
 */
class org.aswing.resizer.ResizerController
{
	//-------------------------------------------------------------------------
	// Private properties
	//-------------------------------------------------------------------------
	
	private static var _map : HashMap = new HashMap();
	
	private var _component : Component;
		private var _resizer : Resizer;
	private var _resizable : Boolean;
	private var _resizableDirectly : Boolean;
	
	
	//-------------------------------------------------------------------------
	// Public API
	//-------------------------------------------------------------------------
	
	/**
	 * Returns {@link ResizerController} associated to passed-in {@code component}
	 * 
	 * @return {@link ResizerController} instance
	 */
	public static function getController( comp : Component ) : ResizerController
	{
		if( _map.containsKey( comp.getID() ) )
		{
			return _map.get( comp.getID() );
		}
		return null;
	}
	
	/**
	 * Indicates if passed-in component is under a ResizerController control.
	 */
	public static function isUnderResizerControl( comp : Component ) : Boolean
	{
		return _map.containsKey( comp.getID() );	
	}
	
	/**
	 * init( comp : Component)<br>
	 * init( comp : Component , resizer:Resizer)
	 * <p>
	 * Applies resizing behaviour to passed-in component.
	 * @param comp the component which need to be resizable
	 * @param resizer (optional)the resizer, default there will be a DefaultResizer instance created.
	 */
	public static function init( comp : Component , resizer:Resizer) : ResizerController
	{
		if( _map.containsKey( comp.getID() ) )
		{
			var con:ResizerController = _map.get( comp.getID() );
			if(resizer != undefined){
				con.setResizer(resizer);
			}
			return con;	
		}
		return new ResizerController( comp , resizer);
	}
	
	/**
	 * Removes the resizer from a component 
	 */
	public static function remove( comp : Component ) : Void
	{
		if( _map.containsKey( comp.getID() ) )
		{
			var o : ResizerController = _map.remove( comp.getID() );
			o._destroy();
		}
	}
	
	/**
	 * Returns reference to the real used component.
	 */
	public function getComponent() : Component
	{
		return _component;
	}
	
	/**
	 * Returns whether this component is resizable by the user.
	 * 
	 * <p>By default, all components are initially resizable. 
	 * 
	 * @see #setResizable()
	 */
	public function isResizable() : Boolean
	{
		return _resizable;
	}
	
	/**
	 * Sets whether this component is resizable by the user.
	 * 
	 * @param b {@code true} user can resize the component by 
	 * drag to scale the component, {@code false} user can't.
	 * 
	 * @see #isResizable()
	 */
	public function setResizable( b : Boolean ) : Void
	{
		if( _resizable != b )
		{
			_resizable = b;  
			_resizer.setEnabled( isResizable() );
		}
	}
	
	/**
	 * Returns the resizer controller defined in {@link #setResizer}.
	 * 
	 * @see #setResizer
	 */
	public function getResizer() : Resizer
	{
		return _resizer;
	}
	
	/**
	 * Sets the {@link Resizer} controller.
	 * 
	 * <p>By default, use {@code Frame.resizer} one.
	 * 
	 * @param r {@link Resizer} instance
	 * 
	 * @see #getResizer
	 */
	public function setResizer( r : Resizer ) : Void
	{
		if( r != _resizer && r != null )
		{
			_destroyResizer();
			_resizer = r;
			_initResizer();
		}
	}
	
	/**
	 * Return whether need resize widget directly when drag the resizer arrow.
	 * 
	 * @see #setResizeDirectly()
	 */
	public function isResizeDirectly() : Boolean
	{
		return _resizableDirectly;
	}
	
	/**
	 * Indicate whether need resize widget directly when drag the resizer arrow.
	 * 
	 * <p>if set to {@code false}, there will be a rectange to represent then size what 
	 * will be resized to.
	 * 
	 * <p>if set to {@code true}, the widget will be resize directly when drag, but this 
	 * is need more cpu counting.<br>
	 * 
	 * <p>Default is {@coe false}.
	 * 
	 * @see org.aswing.Resizer#setResizeDirectly()
	 */
	public function setResizeDirectly( b : Boolean) : Void
	{
		_resizableDirectly = b;
		_resizer.setResizeDirectly(b);
	}
	
	
	//-------------------------------------------------------------------------
	// Private implementation
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor.
	 * 
	 * @param comp Component where applying resize behaviour.
	 */
	private function ResizerController( comp : Component , resizer:Resizer)
	{
		if( !comp) 
		{
			throw new Error("illegal component when insert to ResizerContainer");
		}
		else
		{
			if( !_map.containsKey( comp.getID()) )
			{
				_registerComponent( comp , resizer);
			}
		}
	}	
	
	/**
	 * Registers couple component / controller to the hashmap.
	 * 
	 * @param comp Component where applying resize behaviour.
	 */
	private function _registerComponent( comp : Component , resizer:Resizer ) : Void
	{
		_component = comp;
		_map.put( comp.getID(), this );
		 
		_resizable = true;
		_resizableDirectly = false;
		if(resizer == undefined) resizer = new DefaultResizer();
		setResizer( resizer );
	}
	
	private function _initResizer():Void{
		_resizer.setOwner(getComponent());
		_resizer.setEnabled(isResizable());
		_resizer.setResizeDirectly(isResizeDirectly());
	}
	
	private function _destroyResizer():Void{
		_resizer.destroy();
		_resizer = null;
	}
	
	private function _destroy( ) : Void
	{   
		_destroyResizer();
		_component = null;
	}
	
}
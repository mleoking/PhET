/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid.skin;

import javax.swing.AbstractButton;

/**
 * A Simple Index Model for a button. Use it to calculate which of the subimages
 * of a skin should be used.
 * 
 * The order of evaluation of the states is:
 * 1. Test if button is disabled
 * 2. Test if button is pressed
 * 3. Test if button is in rollover state
 * 4. Button must be in normal state then.
 * 
 * The first test that evaluates to true determines the index for the skin to
 * be used.
 * 
 * If the component is not a subclass of AbstractButton one can explicitly
 * pass the relevant states to getIndexForState but use the same logic.
 */
public class SkinSimpleButtonIndexModel  {
	private AbstractButton button;
	
    private int normal,rollover,pressed,disabled;
	
	/**
	 * Creates a SkinIndexModel for the button with the indices normal=0,
	 * rollover=1, pushed=2 and disabled=3
	 * @param button
	 */
	public SkinSimpleButtonIndexModel()
	{
		this.normal=0;
		this.rollover=1;
		this.pressed=2;
		this.disabled=3;
	}
    
    /**
     * Creates a SkinIndexModel for the button with the given states.
     * 
     * @param normal the index of the normal image
     * @param rollover the index of the rollover image
     * @param pressed the index of the pressed image
     * @param disabled the index of the disabled image
     */	
	public SkinSimpleButtonIndexModel(int normal,int rollover,int pressed,int disabled)
	{
		this.normal=normal;
		this.rollover=rollover;
		this.pressed=pressed;
		this.disabled=disabled;
	}

    /**
     * Returns the index of the image of the skin to be used for rendering.
     * The button must be set before calling <code>getIndexForState</code>.
     * @see setButton
     * @return int the index of the image that should be used for rendering due to the state of the button
     */
	public int getIndexForState() {
		
		if (!button.isEnabled())
			return disabled;
		if (button.getModel().isPressed() ) 		// Do we need to check for armed ??
			return pressed;
		if (button.getModel().isRollover())
			return rollover;
	
		return normal;
	}
	
	/**
	 * This methode can be used for other Components than AbstractButtons. The states are passed
	 * directly, but the logic to decide which index to use is the same as in <code>getIndexForState</code>
     * so that consistency is preserved. 
	 * @param isEnabled
	 * @param isPressed
	 * @param isRollover
	 * @return int
	 */
	public int getIndexForState(boolean isEnabled, boolean isRollover, boolean isPressed)
	{
		if (!isEnabled)
			return disabled;
		if (isPressed ) 		// Do we need to check for armed ??
			return pressed;
		if (isRollover)
			return rollover;

		return normal;		
	}
	
	/**
	 * Returns the button.
	 * @return AbstractButton
	 */
	public AbstractButton getButton() {
		return button;
	}

	/**
	 * Sets the button.
	 * @param button The button to set
	 */
	public void setButton(AbstractButton button) {
		this.button = button;
	}

}

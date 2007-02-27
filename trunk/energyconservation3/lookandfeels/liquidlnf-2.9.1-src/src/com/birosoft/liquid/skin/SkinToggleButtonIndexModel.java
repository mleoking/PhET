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
import javax.swing.JButton;

/**
 * A Simple Index Model for a button. Use it to calculate which of the subimages
 * of a skin should be used for rendering.
 *
 * The order of evaluation of the states is:
 * 1 Test if button is selected
 * 1.1 Test if button is disabled
 * 1.2. Test if button is pressed
 * 1.3. Test if button is in rollover state
 * 1.4. Button must be in normal state then.
 * 2. If not selected
 * 2.1 Test if button is disabled
 * 2.2. Test if button is pressed
 * 2.3. Test if button is in rollover state
 * 2.4. If checkForDefaultButton is true
 *      there check whether it's the default button.
 *      If it's not or checkForDefaultButton is false return the normal state.
 *
 * The first test that evaluates to true determines the index of the skin to
 * be used.
 *
 * If the component is not a subclass of AbstractButton one can explicitly
 * pass the relevant states to getIndexForState but use the same logic.
 */
public class SkinToggleButtonIndexModel
{
    private AbstractButton button;
    
    private int normal,rollover,pressed,disabled;
    private int selected, selectedRollover, selectedPressed, selectedDisabled;
    private int defaultButton;
    
    boolean checkForDefaultButton;
    
    /**
     * Creates a SkinIndexModel for the button with the indices normal=0,
     * rollover=1, pushed=2 and disabled=3,
     * selected=4, selectedRollover=5, selectedPressed=6, selectedDisabled=7;
     * There's no check concering the default button
     */
    public SkinToggleButtonIndexModel()
    {
        this.normal=0;
        this.rollover=1;
        this.pressed=2;
        this.disabled=3;
        
        this.selected=4;
        this.selectedRollover=5;
        this.selectedPressed=6;
        this.selectedDisabled=7;
        
        this.defaultButton=8;
        checkForDefaultButton=false;
    }
    
    /**
     * Creates a SkinIndexModel for the button with the indices normal=0,
     * rollover=1, pushed=2 and disabled=3,
     * selected=4, selectedRollover=5, selectedPressed=6, selectedDisabled=7;
     * @param checkForDefaultButton if true the button is checked whether it's the default button.
     * The component used for the index model must be a JButton.
     */
    public SkinToggleButtonIndexModel(boolean checkForDefaultButton)
    {
        this();
        this.checkForDefaultButton=checkForDefaultButton;
    }
    /**
     * Creates a SkinIndexModel for the button with the states normal, rollover,
     * pushed and disabled
     * @param button
     */
    public SkinToggleButtonIndexModel(int normal,int rollover,int pressed,int disabled,
    int selected,int selectedRollover, int selectedPressed, int selectedDisabled)
    {
        this.normal=normal;
        this.rollover=rollover;
        this.pressed=pressed;
        this.disabled=disabled;
        
        this.selected=selected;
        this.selectedRollover=selectedRollover;
        this.selectedPressed=selectedPressed;
        this.selectedDisabled=selectedDisabled;
    }
    /**
     * @see com.stefankrause.xplookandfeel.skin.SkinOffsetModel#getIndexForState()
     */
    public int getIndexForState()
    {
        if (button==null) return 0;
        if (!button.isSelected())
        {
            if (!button.isEnabled())
                return disabled;
            if (button.getModel().isPressed() ) 		// Do we need to check for armed ??
                return pressed;
            if (button.getModel().isRollover())
                return rollover;
            if (checkForDefaultButton)
            {
                JButton jb = null;
                if (button instanceof JButton)
                {
                    jb=(JButton)button;
                    if (jb.isDefaultButton())
                    {
                        return defaultButton;
                    }
                }
            }
            return normal;
        }
        else
        {
            if (!button.isEnabled())
                return selectedDisabled;
            if (button.getModel().isPressed() ) 		// Do we need to check for armed ??
                return selectedPressed;
            if (button.getModel().isRollover())
                return selectedRollover;
            return selected;
        }
    }
    
    /**
     * This methode can be used for Non-AbstractButtons. The states are passed
     * directly, but the logic to decide which index to use remains the same.
     * @param isSelected
     * @param isEnabled
     * @param isPressed
     * @param isRollover
     * @return int
     */
    public int getIndexForState(boolean isSelected,boolean isEnabled, boolean isPressed, boolean isRollover)
    {
        if (!isSelected)
        {
            if (!isEnabled)
                return disabled;
            if (isPressed ) 		// Do we need to check for armed ??
                return pressed;
            if (isRollover)
                return rollover;
            return normal;
        }
        else
        {
            if (!isEnabled)
                return selectedDisabled;
            if (isPressed ) 		// Do we need to check for armed ??
                return selectedPressed;
            if (isRollover)
                return selectedRollover;
            return selected;
        }
    }
    
    /**
     * Returns the button.
     * @return AbstractButton
     */
    public AbstractButton getButton()
    {
        return button;
    }
    
    /**
     * Sets the button.
     * @param button The button to set
     */
    public void setButton(AbstractButton button)
    {
        this.button = button;
    }
    
    /**
     * Returns the checkForDefaultButton.
     * @return boolean
     */
    public boolean isCheckForDefaultButton()
    {
        return checkForDefaultButton;
    }
    
    /**
     * Sets the checkForDefaultButton.
     * @param checkForDefaultButton The checkForDefaultButton to set
     */
    public void setCheckForDefaultButton(boolean hasToggleButton)
    {
        this.checkForDefaultButton = hasToggleButton;
    }
    
}

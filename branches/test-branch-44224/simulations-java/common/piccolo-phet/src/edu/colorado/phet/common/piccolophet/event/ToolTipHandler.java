package edu.colorado.phet.common.piccolophet.event;

import javax.swing.*;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows a tooltip for a Piccolo node
 * Warning: this implementation may not correctly restore the correct tool tip text
 * for the target component in all cases.
 *
 * @author Sam Reid
 */
public class ToolTipHandler extends PBasicInputEventHandler {
    private String text;
    private JComponent parent;
    private String previousValue;
    private boolean entered = false;

    /**
     * Constructs an input handler that displays the specified text as a popup tooltip,
     * using the defaults specified on the target component.
     *
     * @param text   the tooltip text
     * @param parent the component on which the associated PNode will be placed
     */
    public ToolTipHandler( String text, JComponent parent ) {
        this.text = text;
        this.parent = parent;
        this.previousValue = parent.getToolTipText();
    }

    /**
     * Sets the tooltip text for the JComponent.
     * If text is null, the tooltip is turned off.
     * 
     * @param text
     */
    public void setText( String text ) {
        this.text = text;
        if ( entered ) {
            parent.setToolTipText( text );
        }
    }
    
    public void mouseEntered( PInputEvent event ) {
        this.entered = true;
        this.previousValue = parent.getToolTipText();
        parent.setToolTipText( text );
    }

    public void mouseExited( PInputEvent event ) {
        parent.setToolTipText( this.previousValue );
        this.entered = false;
    }
}

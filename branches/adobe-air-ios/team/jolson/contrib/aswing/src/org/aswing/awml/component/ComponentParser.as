/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.awml.AwmlConstants;
import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.AwmlUtils;
import org.aswing.awml.core.ComponentObjectParser;
import org.aswing.border.Border;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.geom.Rectangle;
import org.aswing.WindowLayout;

/**
 * Parses {@link org.aswing.Component} level elements.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.component.ComponentParser extends ComponentObjectParser {
    
    private static var ATTR_INDEX:String = "index";
    
    private static var ATTR_ENABLED:String = "enabled";
    private static var ATTR_VISIBLE:String = "visible";
    private static var ATTR_FOCUS:String = "focus";
    private static var ATTR_FOCUSABLE:String = "focusable";
    private static var ATTR_X:String = "x";
    private static var ATTR_Y:String = "y";
    private static var ATTR_MIN_WIDTH:String = "min-width";
    private static var ATTR_MIN_HEIGHT:String = "min-height";
    private static var ATTR_MAX_WIDTH:String = "max-width";
    private static var ATTR_MAX_HEIGHT:String = "max-height";
    private static var ATTR_WIDTH:String = "width";
    private static var ATTR_HEIGHT:String = "height";
    private static var ATTR_PREFERRED_WIDTH:String = "preferred-width";
    private static var ATTR_PREFERRED_HEIGHT:String = "preferred-height";
    private static var ATTR_OPAQUE:String = "opaque";
    private static var ATTR_TOOL_TIP:String = "tool-tip";
    private static var ATTR_HAND_CURSOR:String = "hand-cursor";
    private static var ATTR_CONSTRAINT:String = "constraint";
    private static var ATTR_TRIGGER_ENABLED:String = "trigger-enabled";
    private static var ATTR_ALPHA:String = "alpha";
    private static var ATTR_ANTI_BLURRING:String = "anti-blurring";
    private static var ATTR_CACHE_PREFER_SIZES:String = "cache-prefer-sizes";
    private static var ATTR_DRAG_ENABLED:String = "drag-enabled";
    private static var ATTR_DROP_TRIGGER:String = "drop-trigger";
    private static var ATTR_UI_CLASS:String = "ui-class";
    
    private static var ATTR_ON_CREATED:String = "on-created";
    private static var ATTR_ON_DESTROY:String = "on-destroy";
    private static var ATTR_ON_PAINT:String = "on-paint";
    private static var ATTR_ON_SHOWN:String = "on-shown";
    private static var ATTR_ON_HIDDEN:String = "on-hidden";
    private static var ATTR_ON_MOVED:String = "on-moved";
    private static var ATTR_ON_RESIZED:String = "on-resized";
    private static var ATTR_ON_PRESS:String = "on-press";
    private static var ATTR_ON_RELEASE:String = "on-release";
    private static var ATTR_ON_RELEASE_OUTSIDE:String = "on-release-outside";
    private static var ATTR_ON_ROLL_OVER:String = "on-roll-over";
    private static var ATTR_ON_ROLL_OUT:String = "on-roll-out";
    private static var ATTR_ON_DRAG_OVER:String = "on-drag-over";
    private static var ATTR_ON_DRAG_OUT:String = "on-drag-out";
    private static var ATTR_ON_CLICKED:String = "on-clicked";
    private static var ATTR_ON_FOCUS_GAINED:String = "on-focus-gained";
    private static var ATTR_ON_FOCUS_LOST:String = "on-focus-lost";
    private static var ATTR_ON_KEY_DOWN:String = "on-key-down";
    private static var ATTR_ON_KEY_UP:String = "on-key-up";
    private static var ATTR_ON_MOUSE_WHEEL:String = "on-mouse-wheel";
    private static var ATTR_ON_DRAG_RECOGNIZED:String = "on-drag-recognized";
    private static var ATTR_ON_DRAG_ENTER:String = "on-drag-enter";
    private static var ATTR_ON_DRAG_OVERRING:String = "on-drag-overring";
    private static var ATTR_ON_DRAG_EXIT:String = "on-drag-exit";
    private static var ATTR_ON_DRAG_DROP:String = "on-drag-drop";
    
    private static var ATTR_ON_ACTION:String = "on-action";
    private static var ATTR_ON_STATE_CHANGED:String = "on-state-changed";
    
    private static var CONSTRAINT_NORTH:String = "north";
    private static var CONSTRAINT_SOUTH:String = "south";
    private static var CONSTRAINT_WEST:String = "west";
    private static var CONSTRAINT_EAST:String = "east";
    private static var CONSTRAINT_CENTER:String = "center"; 
    private static var CONSTRAINT_WINDOW_TITLE:String = "window-title";
    private static var CONSTRAINT_WINDOW_CONTENT:String = "window-content";
    
    /**
     * Private Constructor.
     */
    private function ComponentParser(Void) {
        super();
    }
    
    public function parse(awml:XMLNode, component:Component, namespace:AwmlNamespace) {

        component = super.parse(awml, component, namespace);
        
        // init index
        component.setAwmlIndex(getAttributeAsNumber(awml, ATTR_INDEX, null));
        
        
        // set enabled
        component.setEnabled(getAttributeAsBoolean(awml, ATTR_ENABLED, component.isEnabled()));
        
        // set visible
        component.setVisible(getAttributeAsBoolean(awml, ATTR_VISIBLE, component.isVisible()));
        
        // set focus
        component.setFocusable(getAttributeAsBoolean(awml, ATTR_FOCUSABLE, component.isFocusable()));
        if (getAttributeAsBoolean(awml, ATTR_FOCUS, false)) {
            component.requestFocus();
        }
        
        // set location
        component.setX(getAttributeAsNumber(awml, ATTR_X, component.getX()));
        component.setY(getAttributeAsNumber(awml, ATTR_Y, component.getY()));
        
        // set min size
        var minWidth:Number  = getAttributeAsNumber(awml, ATTR_MIN_WIDTH, null);
        var minHeight:Number = getAttributeAsNumber(awml, ATTR_MIN_HEIGHT, null);
        if(minWidth != null || minHeight != null){
            if(minWidth == null){
                minWidth = component.getMinimumWidth();
            }
            if(minHeight == null){
                minHeight = component.getMinimumHeight();
            }
            component.setMinimumSize(minWidth, minHeight);
        }
        
        // set max size
        var maxWidth:Number  = getAttributeAsNumber(awml, ATTR_MAX_WIDTH, null);
        var maxHeight:Number = getAttributeAsNumber(awml, ATTR_MAX_HEIGHT, null);
        if(maxWidth != null || maxHeight != null){
            if(maxWidth == null){
                maxWidth = component.getMaximumWidth();
            }
            if(maxHeight == null){
                maxHeight = component.getMaximumHeight();
            }
            component.setMaximumSize(maxWidth, maxHeight);
        }

        // set size
        component.setWidth(getAttributeAsNumber(awml, ATTR_WIDTH, component.getWidth()));
        component.setHeight(getAttributeAsNumber(awml, ATTR_HEIGHT, component.getHeight()));

        // set size
        var prefWidth:Number = getAttributeAsNumber(awml, ATTR_PREFERRED_WIDTH, null); 
        var prefHeight:Number = getAttributeAsNumber(awml, ATTR_PREFERRED_HEIGHT, null);
        
        if (prefWidth != null || prefHeight != null) {
            if (prefWidth == null) {
                prefWidth = component.getPreferredSize().width;
            } 
            if (prefHeight == null) {
                prefHeight = component.getPreferredSize().height;
            } 
            component.setPreferredSize(prefWidth, prefHeight);
        }
        
        // set cache pref sizes
        component.setCachePreferSizes(getAttributeAsBoolean(awml, ATTR_CACHE_PREFER_SIZES, component.isCachePreferSizes()));
        
        // set opaque
        var opaque:Boolean = getAttributeAsBoolean(awml, ATTR_OPAQUE, null);
        if(opaque != null){
            component.setOpaque(opaque);
        }
        
        // set tool tip
        component.setToolTipText(getAttributeAsString(awml, ATTR_TOOL_TIP, component.getToolTipText()));
        
        // use hand cursor
        component.setUseHandCursor(getAttributeAsBoolean(awml, ATTR_HAND_CURSOR, component.isUseHandCursor()));
        
        // init trigger enabled
        component.setTriggerEnabled(getAttributeAsBoolean(awml, ATTR_TRIGGER_ENABLED, component.isTriggerEnabled()));
        
        // update constraints
        var constraint:String = getAttributeAsString(awml, ATTR_CONSTRAINT);
        switch (constraint) {
            case CONSTRAINT_NORTH:
                component.setConstraints(BorderLayout.NORTH);
                break;
            case CONSTRAINT_SOUTH:
                component.setConstraints(BorderLayout.SOUTH);
                break;
            case CONSTRAINT_WEST:
                component.setConstraints(BorderLayout.WEST);
                break;
            case CONSTRAINT_EAST:
                component.setConstraints(BorderLayout.EAST);
                break;
            case CONSTRAINT_CENTER:
                component.setConstraints(BorderLayout.CENTER);
                break;
            case CONSTRAINT_WINDOW_TITLE:
                component.setConstraints(WindowLayout.TITLE);
                break;
            case CONSTRAINT_WINDOW_CONTENT:
                component.setConstraints(WindowLayout.CONTENT);
                break;
            default:
            	component.setConstraints(constraint);
            	break;
        }
        
        // init alpha
        component.setAlpha(getAttributeAsNumber(awml, ATTR_ALPHA, component.getAlpha()));
        
        // init anti blurring
        component.setAntiBlurring(getAttributeAsBoolean(awml, ATTR_ANTI_BLURRING, component.isAntiBlurring()));
        
        // init drag and drop properties
        component.setDragEnabled(getAttributeAsBoolean(awml, ATTR_DRAG_ENABLED, component.isDragEnabled())); 
        component.setDropTrigger(getAttributeAsBoolean(awml, ATTR_DROP_TRIGGER, component.isDropTrigger()));
        
        // init events
        attachEventListeners(component, Component.ON_CREATED, getAttributeAsEventListenerInfos(awml, ATTR_ON_CREATED));
        attachEventListeners(component, Component.ON_DESTROY, getAttributeAsEventListenerInfos(awml, ATTR_ON_DESTROY));
        attachEventListeners(component, Component.ON_PAINT, getAttributeAsEventListenerInfos(awml, ATTR_ON_PAINT));
        attachEventListeners(component, Component.ON_SHOWN, getAttributeAsEventListenerInfos(awml, ATTR_ON_SHOWN));
        attachEventListeners(component, Component.ON_HIDDEN, getAttributeAsEventListenerInfos(awml, ATTR_ON_HIDDEN));
        attachEventListeners(component, Component.ON_MOVED, getAttributeAsEventListenerInfos(awml, ATTR_ON_MOVED));
        attachEventListeners(component, Component.ON_RESIZED, getAttributeAsEventListenerInfos(awml, ATTR_ON_RESIZED));
        
        attachEventListeners(component, Component.ON_PRESS, getAttributeAsEventListenerInfos(awml, ATTR_ON_PRESS));
        attachEventListeners(component, Component.ON_RELEASE, getAttributeAsEventListenerInfos(awml, ATTR_ON_RELEASE));
        attachEventListeners(component, Component.ON_RELEASEOUTSIDE, getAttributeAsEventListenerInfos(awml, ATTR_ON_RELEASE_OUTSIDE));
        attachEventListeners(component, Component.ON_ROLLOVER, getAttributeAsEventListenerInfos(awml, ATTR_ON_ROLL_OVER));
        attachEventListeners(component, Component.ON_ROLLOUT, getAttributeAsEventListenerInfos(awml, ATTR_ON_ROLL_OUT));
        attachEventListeners(component, Component.ON_DRAGOVER, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_OVER));
        attachEventListeners(component, Component.ON_DRAGOUT, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_OUT));
        attachEventListeners(component, Component.ON_CLICKED, getAttributeAsEventListenerInfos(awml, ATTR_ON_CLICKED));
        attachEventListeners(component, Component.ON_FOCUS_GAINED, getAttributeAsEventListenerInfos(awml, ATTR_ON_FOCUS_GAINED));
        attachEventListeners(component, Component.ON_FOCUS_LOST, getAttributeAsEventListenerInfos(awml, ATTR_ON_FOCUS_LOST));
        attachEventListeners(component, Component.ON_KEY_DOWN, getAttributeAsEventListenerInfos(awml, ATTR_ON_KEY_DOWN));
        attachEventListeners(component, Component.ON_KEY_UP, getAttributeAsEventListenerInfos(awml, ATTR_ON_KEY_UP));
        attachEventListeners(component, Component.ON_MOUSE_WHEEL, getAttributeAsEventListenerInfos(awml, ATTR_ON_MOUSE_WHEEL));
        attachEventListeners(component, Component.ON_DRAG_RECOGNIZED, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_RECOGNIZED));
        attachEventListeners(component, Component.ON_DRAG_ENTER, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_ENTER));
        attachEventListeners(component, Component.ON_DRAG_OVERRING, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_OVERRING));
        attachEventListeners(component, Component.ON_DRAG_EXIT, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_EXIT));
        attachEventListeners(component, Component.ON_DRAG_DROP, getAttributeAsEventListenerInfos(awml, ATTR_ON_DRAG_DROP));
        
        // set UI class
        var uiClass:Function = getAttributeAsClass(awml, ATTR_UI_CLASS, null);
        if (uiClass != null) component.setUI(new uiClass());
        
        return component;
    }

    private function parseChild(awml:XMLNode, nodeName:String, component:Component, namespace:AwmlNamespace):Void {

        super.parseChild(awml, nodeName, component, namespace);
        
        if (nodeName == AwmlConstants.NODE_FOREGROUND) {
            var color:ASColor = AwmlParser.parse(awml);
            if (color != null) component.setForeground(color);
        } else if (nodeName == AwmlConstants.NODE_BACKGROUND) {
            var color:ASColor = AwmlParser.parse(awml);
            if (color != null) component.setBackground(color);
        } else if (nodeName == AwmlConstants.NODE_FONT) {
            var font:ASFont = AwmlParser.parse(awml);
            if (font != null) component.setFont(font);
        } else if (AwmlUtils.isBorderNode(nodeName)) {
            var border:Border = AwmlParser.parse(awml);
            component.setBorder(border); // null is valid value
        } else if (nodeName == AwmlConstants.NODE_CLIP_BOUNDS) {
            var bounds:Rectangle = AwmlParser.parse(awml);
            if (bounds != null) component.setClipBounds(bounds);
        }   
    }   

}

/*
 * Group.java
 *
 * Created on April 26, 2003, 1:50 PM
 */

package com.elementengine.xml.svg;

import java.awt.geom.AffineTransform;

/**
 *
 * @author  Default
 */
public class Group {
    
    private Style style = new Style();
    private Transform transform = new Transform();
    
    /** Creates a new instance of Group */
    public Group() {
        
    }
    void setStyle( Style newStyle){
        style.setStyle(newStyle);
    }
    Style getStyle(){
        return (style);
    }
    void setTransform( Transform newTransform){
        transform.setTransform(newTransform);
    }
    Transform getTransform(){
        return (transform);
    }
     

    
}

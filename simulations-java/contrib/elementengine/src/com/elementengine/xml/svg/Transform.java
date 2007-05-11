/*
 * this.java
 *
 * Created on April 26, 2003, 2:26 PM
 */

package com.elementengine.xml.svg;
import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;
import com.elementengine.xml.Utility;
/**
 *
 * @author  Default
 */
public class Transform extends AffineTransform {
    

    
    /** Creates a new instance of Transform */
    public Transform() {
    }
    
    void setTransform(java.lang.String transformString) {
        int transformOffset = -1;
        int endParenOffset = -1;
        int numTransformTokens;
        StringTokenizer transformTokens = null;
        double transformValue[] = null;


        String transformData;
        transformString = transformString.replace('\n', ' ');  //suppress newline
        transformString = transformString.replace('\t', ' ');  // suppress tab
        transformOffset = transformString.indexOf("translate");  
        if ( transformOffset >= 0){
             endParenOffset = transformString.indexOf(")", transformOffset);
             if ( endParenOffset > 0){
                   transformData = transformString.substring(transformOffset + 9, endParenOffset+1);
                   transformValue   = new double[2];  //trusting that SVG will match required fields
                   transformValue = Utility.getDoubles(transformData);
                   this.translate(transformValue[0], transformValue[1]);
                }
        }
        transformOffset = transformString.indexOf("scale");  
        if ( transformOffset >= 0){
             endParenOffset = transformString.indexOf(")", transformOffset);
             if ( endParenOffset > 0){
                   transformData = transformString.substring(transformOffset+5, endParenOffset+1);
                   transformTokens = new StringTokenizer(transformData,",() \t\n");
                    numTransformTokens = transformTokens.countTokens();
                   transformValue = new double[numTransformTokens];
                   transformValue = Utility.getDoubles(transformData);
                   if (numTransformTokens == 2){
                       this.scale(transformValue[0], transformValue[1]);
                   }else{
                       this.scale(transformValue[0], transformValue[0]);
                   }
                }
        }

        transformOffset = transformString.indexOf("rotate");  
        if ( transformOffset >= 0){
             endParenOffset = transformString.indexOf(")", transformOffset);
             if ( endParenOffset > 0){
                   transformData = transformString.substring(transformOffset+6, endParenOffset+1);
                   transformTokens = new StringTokenizer(transformData,",() \t\n");
                    numTransformTokens = transformTokens.countTokens();
                   transformValue = new double[numTransformTokens];
                   transformValue = Utility.getDoubles(transformData);
                   if (numTransformTokens == 1){
                       this.rotate(transformValue[0]* (Math.PI/180));
                   }
                   if (numTransformTokens == 3){                   
                       this.rotate(transformValue[0]*(Math.PI/180), transformValue[1], transformValue[2]);
                   }
                }
        }
        
        transformOffset = transformString.indexOf("skewX");  
        if ( transformOffset >= 0){
             endParenOffset = transformString.indexOf(")", transformOffset);
             if ( endParenOffset > 0){
                   transformData = transformString.substring(transformOffset + 5, endParenOffset+1);
                   transformValue   = new double[1];  //trusting that SVG will match required fields
                   transformValue = Utility.getDoubles(transformData);
                   this.shear(Math.tan(Math.PI * transformValue[0]/180), 0.0);
                   // notes on w3c and clarifed by Batik code
                }
        }

        transformOffset = transformString.indexOf("skewY");  
        if ( transformOffset >= 0){
             endParenOffset = transformString.indexOf(")", transformOffset);
             if ( endParenOffset > 0){
                   transformData = transformString.substring(transformOffset + 5, endParenOffset+1);
                   transformValue   = new double[1];  //trusting that SVG will match required fields
                   transformValue = Utility.getDoubles(transformData);
                   this.shear(0.0, Math.tan( Math.PI * transformValue[0]/180) );
                   // notes on w3c and clarifed by Batik code
                }
        }        
       
    }
}

/*
 * Style.java
 *
 * Created on January 26, 2003, 10:34 PM
 */

package com.elementengine.xml.svg;
import java.awt.*;
import java.util.*;
import com.elementengine.xml.Utility;

public class Style {
    
    private Color fillColor = new Color(0.0f,0.0f,0.0f,0.0f);  // transparent default
    private Color strokeColor = Color.black;
    private BasicStroke stroke = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);;
    

    public Style() {
    }

    public Style(String styleString) {
        
    }
    
    Color getFillColor() {
        return fillColor;
    }
    Color getStrokeColor() {
        return strokeColor;
    }
    
    BasicStroke getStroke() {
        return stroke;
    }

    void setFillColor(Color value) {
        fillColor = value;
    }
    void setStrokeColor(Color value) {
        strokeColor=value;
    }
    void setStroke(BasicStroke value) {
        stroke = value;
    }
    void setStyle( Style newStyle){
        fillColor = newStyle.getFillColor();
        strokeColor = newStyle.getStrokeColor();
        stroke = newStyle.getStroke();
    }
    void setStyle(String styleInfo){
        boolean opacitySet = false;
        String currentToken = null;
        String styleData = null;
        int i;
        String colorLookup[] = {"aqua", "black", "blue","fuchsia",
                                "gray", "green", "lime", "maroon",
                                "navy", "olive", "purple", "red", 
                                "silver", "teal", "white", "yellow" };
        int colorValue[] ={65535, 16777215, 255, 16711935,
                            8421504, 32768, 65280,8388608,
                            128, 32896, 8388736,16711680,
                            12632256, 32896, 0, 16776960  };                                       

        // reset defaults?
//        fillColor = new Color(0.0f,0.0f,0.0f,0.0f);  // transparent default
//        strokeColor = Color.black;
//        BasicStroke stroke = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);;
                                
                            
                            

        
        
              StringTokenizer styleTokenizer = new StringTokenizer(styleInfo,":;",false);
              
              while(styleTokenizer.hasMoreElements()){
                  currentToken = styleTokenizer.nextToken();
                  currentToken = Utility.trim(currentToken);
                  if (currentToken.equals("stroke")){
                      styleData = styleTokenizer.nextToken();
                      styleData = Utility.trim(styleData);
                      
                      // consider alpha value here.  
                      // get color back and then set stroke color to RGB + alpha
                      // like with Stroke
                      
                      
                      strokeColor= getColor(styleData);
                  } else if  (currentToken.equals("stroke-width")){
                      styleData = styleTokenizer.nextToken();
                      styleData = Utility.trim(styleData);
                      Float strokeWidth  = new Float(styleData);  // get Float from string
                      stroke = new BasicStroke( strokeWidth.floatValue(), stroke.getEndCap(),stroke.getLineJoin(),stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
                  } else if  (currentToken.equals("stroke-dasharray")){
                      styleData = styleTokenizer.nextToken();
                      styleData = Utility.trim(styleData);
                      float dashes[] = Utility.getFloats(styleData);
                      stroke = new BasicStroke( stroke.getLineWidth(), stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), dashes, 1.0f);                      
                      
                  } else if  (currentToken.equals("stroke-opacity")){
                      styleData = styleTokenizer.nextToken();
                      //16777216
                      styleData = Utility.trim(styleData);
                      Float alphaVal  = new Float(styleData);  // get Float from string
                      float f = alphaVal.floatValue() * 255;  // multiply by 255
                      alphaVal = new Float(f);  // new class FLOAT
                      strokeColor = new Color (strokeColor.getRed(),strokeColor.getGreen(), strokeColor.getBlue() ,alphaVal.intValue());  
                      opacitySet=true;
                  } else if  (currentToken.equals("fill")){
                      styleData = styleTokenizer.nextToken();
                       fillColor= getColor(styleData);
                  }
              }

    }
    
    
     Color getColor(String styleData){
        boolean opacitySet = false;
        Color returnColor = Color.black;
        int colorIntValue =0;
        String currentToken = null;
        int i;
        String colorLookup[] = {"aqua", "black", "blue","fuchsia",
                                "gray", "green", "lime", "maroon",
                                "navy", "olive", "purple", "red", 
                                "silver", "teal", "white", "yellow" };
        int colorValue[] ={65535, 0, 255, 16711935,
                            8421504, 32768, 65280,8388608,
                            128, 32896, 8388736,16711680,
                            12632256, 32896, 16777215, 16776960  };                                       

                      styleData = Utility.trim(styleData);
                      if (styleData.equals("none")){
                          return new Color (0.0f,0.0f,0.0f,0.0f); // return transparent color
                      }
                      if (styleData.startsWith("#")) {
                          styleData = styleData.substring(1);
                          colorIntValue = Integer.valueOf(styleData,16).intValue();
                      } else if (styleData.startsWith("RGB")){
                          String RGBData = null;
                          StringTokenizer RGBTokenizer = new StringTokenizer(styleData, "(),",false);
                          for (i=0; i<4;i++){
                              RGBData = RGBTokenizer.nextToken();
                              RGBData = Utility.trim(RGBData);

                              switch (i){
                                  case 1:  colorIntValue = 65536 * Integer.valueOf(RGBData,10).intValue();
                                           break;
                                  case 2:  colorIntValue += 256 * Integer.valueOf(RGBData,10).intValue();
                                           break;
                                  case 3:  colorIntValue +=  Integer.valueOf(RGBData,10).intValue();
                                           break;
                              } // end switch 
                          } //end for
                      }else{  // end if RGB
                          for (i=0;i<16;i++){
                              if (colorLookup[i].equals(styleData)){
                                  colorIntValue = colorValue[i];
                              }
                          } // for
                      } //else  
                      returnColor = new Color(colorIntValue);
                      return returnColor;
        }
     
     public String toString(){
         return(strokeColor + " " + fillColor + " " + stroke);
         
     }



}

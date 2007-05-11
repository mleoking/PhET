
// Copyright 1997, 1998,1999 Carmen Delessio (carmen@blackdirt.com)
// Black Dirt Software http://www.blackdirt.com/graphics/svg
// Free for non-commercial use

// revisions:
// May 12,1999 corrected ";" at end of color - Chris Lilley mailing list
// May 16,1999 implemented simple Java 2d infrastructure, Begin Path

package com.elementengine.xml.svg;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.net.*;
import java.applet.Applet;
import java.lang.Math;
import java.lang.String;
import java.awt.image.*;

import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.elementengine.xml.Parser;
import com.elementengine.xml.Element;
import com.elementengine.xml.Utility;


import javax.swing.*;
import java.awt.event.*;


// XML basics
//import com.ibm.xml.parser.*;
//import org.w3c.dom.*;

public class svgImage extends Canvas{

      int i;
      int j;
      static int offset =0;

      public short inch;

      private DataInputStream d;
     
      static boolean drawFilled;

      float oldfx;
      float oldfy;
      float fx;
      float fy;
      float fx1;
      float fy1;
      float fx2;
      float fy2;
      float fw;
      float fh;
      float frx;
      float fry;


      static StringTokenizer t;
      static StringTokenizer pathTokenizer;
      static String pathData;
      
      static AffineTransform a;


      static short svgWidth = 400;
      static short svgHeight = 300;
      
      
      static Parser parser = null;

      // Use Java2d Basics
      static  Graphics2D svgGraphics;   
      BufferedImage svgImageBuffer;


      static String tempBuffer;
      public int fontStyle;
      public int fontWeight;
      public boolean fontItalic = false;
      public BasicStroke bs = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f);
      

      Color strokeColor;
      Color fillColor;
      
      
      Style localStyle = new Style();
      Style globalStyle = new Style();
//      AffineTransform globalTransform = new AffineTransform();      
//      AffineTransform localTransform = new AffineTransform();   

      Transform globalTransform = new Transform();      
      Transform localTransform = new Transform();   
      Transform scaleTransform = new Transform();   
      
//      AffineTransform scaleTransform = new AffineTransform();   
      double xscale =  1.0;
      double yscale =  1.0;
      
      
 
  public svgImage(String filename)
  {
       if (filename != null) {
                parser = new Parser(filename);
    }
  }
  
    public Image getImage(){
        return (svgImageBuffer);
    }
  

    public void addNotify(){
      super.addNotify();
      getSvgSize();
      svgImageBuffer = new BufferedImage(svgWidth, svgHeight,3);
      if (svgImageBuffer == null){
         System.out.println(" image is null");
         return;
      }
      svgGraphics = svgImageBuffer.createGraphics();
      if (svgGraphics == null) System.out.println(" graphics is null");
      renderSVG();  // put SVG image into svgImagebuffer
   }




   public void paint (Graphics g){
      int newWidth = getWidth();
      int newHeight= getHeight();

      if ( newWidth != svgWidth || newHeight != svgHeight){
            svgImageBuffer = new BufferedImage(newWidth, newHeight,3);
            svgGraphics = svgImageBuffer.createGraphics();
            double nw = newWidth;   // use double arithmetic
            double sw = svgWidth;
            xscale =  nw/sw;
            double nh = newHeight;
            double sh = svgHeight;
            yscale =  nh/sh;
            scaleTransform = new Transform();
            scaleTransform.scale(xscale, yscale);
            svgGraphics.transform(scaleTransform);

            
            
            renderSVG();
       }
       g.drawImage(svgImageBuffer, 0, 0, this);
   }

   public void update(Graphics g){
      paint(g);
   }


   public Dimension getPreferredSize(){
      return new Dimension(svgWidth, svgHeight);
   }

   public Dimension getMinimumSize(){
      return new Dimension((int)Math.round(0.10*svgWidth), (int)Math.round(0.10*svgHeight));
   }


   public void renderSVG() {
        float pathFloat[] = null;
        float currentX=0;
        float currentY=0;
        float  lastQuadControlX = 0;   // for trueType path
        float  lastQuadControlY = 0;

        float  lastCubicControlX = 0;   // for trueType path
        float  lastCubicControlY = 0;
        int currentTransformLevel=0;
        int lastTransformLevel=0;
        
        
        
        Stack transformStack = new Stack();
        int currentStyleLevel=0;
        int lastStyleLevel=0;
        int maxStyleLevel=0;
        int lastNestLevel=0;
        Stack styleStack = new Stack();
        Hashtable styleHash = new Hashtable();
        Hashtable groupHash = new Hashtable();
        
        
        int nestLevel=0;
        svgGraphics.setBackground(Color.white);
        svgGraphics.fillRect(0, 0, svgWidth, svgHeight);
        
        globalTransform = scaleTransform;
        
        Enumeration e = parser.getElements();
        while (e.hasMoreElements()){
           Element elem =  (Element) e.nextElement();
           nestLevel = elem.getNestLevel();
  
           if (lastNestLevel >nestLevel){  // A group has been closed
               globalStyle = new Style();               
               globalTransform= new Transform();
               globalTransform = scaleTransform;               
               
               if (nestLevel == 1){
                      
                      //  globalStyle.setStyle(tempBuffer);
                    } else{
                        Group restoreGroup = (Group) groupHash.get(new String("group" + (nestLevel-1)));
                        globalStyle.setStyle( restoreGroup.getStyle());
                        globalTransform.setTransform((AffineTransform)restoreGroup.getTransform());
                        currentStyleLevel = nestLevel;
                    }
           }
           lastNestLevel = nestLevel;
           localStyle.setStyle(globalStyle); // Apply global style by object assignment
           localTransform.setTransform(globalTransform);

           // check for style attribute for the current element 
           if (elem.hasAttribute("style") && !elem.getTagName().equals("g")){
               localStyle.setStyle(globalStyle); // Apply global style by object assignment
               tempBuffer = elem.getAttribute("style");
               localStyle.setStyle(tempBuffer);  // add local style by String based assignment
           }

           svgGraphics.setTransform((AffineTransform)globalTransform);               

           if (elem.hasAttribute("transform") && !elem.getTagName().equals("g")){
              tempBuffer = elem.getAttribute("transform");
              localTransform.setTransform(tempBuffer);  
              svgGraphics.setTransform((AffineTransform)localTransform);     

           }
           
           if (elem.getTagName().equals("g")) {
               Group saveGroup = new Group();
               if (elem.hasAttribute("style")){
                tempBuffer = elem.getAttribute("style");
                globalStyle.setStyle(tempBuffer);                                  

              }
              if (elem.hasAttribute("transform")){              
                 tempBuffer = elem.getAttribute("transform");
                 globalTransform.setTransform(tempBuffer);

              }
              // save new group object here
              saveGroup.setStyle(globalStyle);               
              saveGroup.setTransform(globalTransform);
              groupHash.put(new String("group" + nestLevel), saveGroup);
           }
           else if (elem.getTagName().equals("text")) {
              tempBuffer = elem.getAttribute("x");
              fx = (float)new Float(tempBuffer).floatValue();
              tempBuffer = elem.getAttribute("y");
              fy = (float)new Float(tempBuffer).floatValue();              
                svgGraphics.drawString( elem.getValue(),fx,fy);
            }
            else if (elem.getTagName().equals("rect")) {
              tempBuffer = elem.getAttribute("x");
              fx = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("y");
              fy = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("width");
              fw = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("height");
              fh = (float)new Float(tempBuffer).floatValue();
              boolean rounded = false;
              if (elem.hasAttribute("rx")) {              
                 rounded = true;
                 tempBuffer = elem.getAttribute("rx");
                 frx = (float)new Float(tempBuffer).floatValue();
              }
              if (elem.hasAttribute("ry")) {              
                 rounded = true;
                 tempBuffer = elem.getAttribute("ry");
                 fry = (float)new Float(tempBuffer).floatValue();
              }
              RoundRectangle2D.Float roundRect = null;
              Rectangle2D.Float rect = null;             
              if (rounded) {
                 roundRect = new RoundRectangle2D.Float(fx, fy,fw, fh,frx,fry);
              }else{
                 rect = new Rectangle2D.Float(fx, fy,fw, fh);
              }
              
              svgGraphics.setPaint(localStyle.getFillColor());
              if (rounded){
                  svgGraphics.fill(roundRect);
              }else{
                  svgGraphics.fill(rect);
              }
              svgGraphics.setStroke(localStyle.getStroke());
              svgGraphics.setPaint(localStyle.getStrokeColor());
              if (rounded){
                  svgGraphics.draw(roundRect);
              }else{
                  svgGraphics.draw(rect);
              }
            }

           
           
            else if (elem.getTagName().equals("ellipse")) {
              tempBuffer = elem.getAttribute("cx");
              fx = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("cy");
              fy = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("rx");
              fw = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("ry");
              fh = (float)new Float(tempBuffer).floatValue();              
              fy = fy - fh;
              fx= fx - fw;
              Ellipse2D.Float ellipse = new Ellipse2D.Float(fx, fy,2*fw, 2*fh);
              svgGraphics.setPaint(localStyle.getFillColor());
              svgGraphics.fill(ellipse);
              svgGraphics.setStroke(localStyle.getStroke());
              svgGraphics.setPaint(localStyle.getStrokeColor());
              svgGraphics.draw(ellipse);
            }
            else if (elem.getTagName().equals("circle")) {
              tempBuffer = elem.getAttribute("cx");
              fx = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("cy");
              fy = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("r");
              fw = (float)new Float(tempBuffer).floatValue();              
              fh = fw;              
              fy = fy - fh;
              fx= fx - fw;
              Ellipse2D.Float ellipse = new Ellipse2D.Float(fx, fy,2*fw, 2*fh);
              svgGraphics.setPaint(localStyle.getFillColor());
              svgGraphics.fill(ellipse);
              svgGraphics.setStroke(localStyle.getStroke());
              svgGraphics.setPaint(localStyle.getStrokeColor());
              svgGraphics.draw(ellipse);
            }
           
           
           
            else if (elem.getTagName().equals("line")) { // requires fixing
              GeneralPath l = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
              tempBuffer = elem.getAttribute("x1");
              fx1 = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("y1");
              fy1 = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("x2");
              fx2 = (float)new Float(tempBuffer).floatValue();              
              tempBuffer = elem.getAttribute("y2");
              fy2 = (float)new Float(tempBuffer).floatValue();                            

              l.moveTo(fx1, fy1);
              l.lineTo(fx2,fy2);
              
              svgGraphics.setPaint(localStyle.getStrokeColor());
              svgGraphics.setStroke(localStyle.getStroke());
              svgGraphics.draw(l);              
            }
            else if (elem.getTagName().equals("polygon")) {
                GeneralPath polygonPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                tempBuffer = elem.getAttribute("points");
                tempBuffer = tempBuffer.replace('\n', ' ');  //suppress newline
                tempBuffer = tempBuffer.replace('\t', ' ');  // suppress tab
                StringTokenizer t = new StringTokenizer(tempBuffer," ,");
                tempBuffer = t.nextToken();
                fx = (float)new Float(tempBuffer).floatValue();              
                tempBuffer = t.nextToken();
                fy = (float)new Float(tempBuffer).floatValue();              
                polygonPath.moveTo( fx , fy );
                  
                while(t.hasMoreElements()){
                  tempBuffer = t.nextToken();
                  fx = (float)new Float(tempBuffer).floatValue();              
                  tempBuffer = t.nextToken();
                  fy = (float)new Float(tempBuffer).floatValue();              
                  polygonPath.lineTo( fx , fy );
                }
                polygonPath.closePath();
                svgGraphics.setPaint(localStyle.getFillColor());
                svgGraphics.fill(polygonPath);
                svgGraphics.setStroke(localStyle.getStroke());
                svgGraphics.setPaint(localStyle.getStrokeColor());
                svgGraphics.draw(polygonPath);
              }
            else if (elem.getTagName().equals("polyline")) {
                GeneralPath polygonPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
                tempBuffer = elem.getAttribute("points");
                tempBuffer = tempBuffer.replace('\n', ' ');  //suppress newline
                tempBuffer = tempBuffer.replace('\t', ' ');  // suppress tab
                StringTokenizer t = new StringTokenizer(tempBuffer," ,");
                tempBuffer = t.nextToken();
                fx = (float)new Float(tempBuffer).floatValue();              
                tempBuffer = t.nextToken();
                fy = (float)new Float(tempBuffer).floatValue();              
                polygonPath.moveTo( fx , fy );
                  
                while(t.hasMoreElements()){
                  tempBuffer = t.nextToken();
                  fx = (float)new Float(tempBuffer).floatValue();              
                  tempBuffer = t.nextToken();
                  fy = (float)new Float(tempBuffer).floatValue();              
                  polygonPath.lineTo( fx , fy );
                }
                svgGraphics.setPaint(localStyle.getFillColor());
                svgGraphics.fill(polygonPath);
                svgGraphics.setStroke(localStyle.getStroke());
                svgGraphics.setPaint(localStyle.getStrokeColor());
                svgGraphics.draw(polygonPath);
            }

            else if (elem.getTagName().equals("path")) {
              tempBuffer = elem.getAttribute("style");
              tempBuffer = elem.getAttribute("d");
//              System.out.println(tempBuffer);
              GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
              t = new StringTokenizer(tempBuffer,"MmLlCczArSsHhVvDdEeFfGgJjQqTtzZ",true);
              String pathCheck = "MmLlCcArSsHhVvDdEeFfGgJjQqTt";
              while(t.hasMoreElements()){
                tempBuffer = t.nextToken();
//                System.out.println("-"+ tempBuffer +"-");
                offset = pathCheck.indexOf(tempBuffer);
                
                if (offset >=0) {                
                      pathData = t.nextToken();
                      pathFloat  = new float[t.countTokens()];  //trusting that right number will be in SVG
                      pathFloat = Utility.getFloats(pathData);
                }

                switch (tempBuffer.charAt(0)){
                  case 'M': //Absolute Move To
                             currentX = pathFloat[0];
                             currentY = pathFloat[1];
                             p.moveTo(currentX, currentY);
                           break;
                  case 'm': //relative Move To
                             currentX += pathFloat[0];
                             currentY += pathFloat[1];
                             p.moveTo(currentX, currentY);
                           break;

                  case 'L': // Absolute Line to
                             currentX = pathFloat[0];
                             currentY = pathFloat[1];
                             p.lineTo(currentX, currentY);
                             break;

                  case 'l': // Relative Line to
                             currentX += pathFloat[0];
                             currentY += pathFloat[1];
                             p.lineTo(currentX, currentY);
                           break;
                  case 'C':  //Cubic Bezier 
                             p.curveTo(pathFloat[0], pathFloat[1], pathFloat[2], pathFloat[3],pathFloat[4],pathFloat[5]);
                             lastCubicControlX = pathFloat[2];
                             lastCubicControlY = pathFloat[3];
                             currentX = pathFloat[4];
                             currentY = pathFloat[5];
                           break;
                  case 'c': //relative Cubic Bezier 
                             pathFloat[0] += currentX;
                             pathFloat[1] += currentY;
                             pathFloat[2] += currentX;
                             pathFloat[3] += currentY;
                             pathFloat[4] += currentX;
                             pathFloat[5] += currentY;
                             p.curveTo(pathFloat[0], pathFloat[1], pathFloat[2], pathFloat[3],pathFloat[4],pathFloat[5]);
                             lastCubicControlX = pathFloat[2]+currentX;
                             lastCubicControlY = pathFloat[3]+currentY;
                             currentX = pathFloat[4];
                             currentY = pathFloat[5];
                      
                           break;
                case 'S':  // Smooth Cubic 
                           p.curveTo(2*currentX-lastCubicControlX, 2*currentY-lastQuadControlY, pathFloat[0], pathFloat[1], pathFloat[2], pathFloat[3]);
                           currentX = (float) p.getCurrentPoint().getX();
                           currentY = (float) p.getCurrentPoint().getY();
                           
                           break;

                  case 's': // relative smooth cubic
                            pathFloat[0] += currentX;
                            pathFloat[1] += currentY;
                            pathFloat[2] += currentX;
                            pathFloat[3] += currentY;
                           
                           p.curveTo(2*currentX-lastCubicControlX, 2*currentY-lastQuadControlY, pathFloat[0], pathFloat[1], pathFloat[2], pathFloat[3]);
                           currentX = (float) p.getCurrentPoint().getX();
                           currentY = (float) p.getCurrentPoint().getY();
                           break;
                  case 'z':   // close path
                              p.closePath();
                           break;
                  case 'Z':   // close path
                              p.closePath();
                           break;

                  case 'A':  // Elliptical Arc
                             boolean largeArc = false;                             
                             boolean sweep =false;
                             if (pathFloat[3] ==1){
                                 largeArc = true;
                             } 
                            if (pathFloat[4] ==1){
                                 sweep = true;
                             }                              
                             p = arcTo(pathFloat[0], pathFloat[1], pathFloat[2], largeArc, sweep,  pathFloat[5], pathFloat[6], p);
                             break;

  
  

                  case 'H':  // Horizontal Line
                             currentX = pathFloat[0];
                             p.lineTo(currentX, currentY);
                           break;

                  case 'h':  // relative horizontal line
                             currentX += pathFloat[0];
                             p.lineTo(currentX, currentY);
                           break;

                  case 'V':  // Vertical Line
                             currentY = pathFloat[0];
                             p.lineTo(currentX, currentY);
                           break;
                  case 'v':  // relativ vertical line
                             currentY += pathFloat[0];
                             p.lineTo(currentX, currentY);
                             break;

                  case 'Q': // quadratic 
                           p.quadTo(pathFloat[0], pathFloat[1], pathFloat[2], pathFloat[3]);
                           lastQuadControlX = pathFloat[0];  
                           lastQuadControlY = pathFloat[1];
                           currentX = (float) p.getCurrentPoint().getX();
                           currentY = (float) p.getCurrentPoint().getY();
                           break;

                  case 'q': // relative quadratic
                            pathFloat[0] += currentX;
                            pathFloat[1] += currentY;
                            pathFloat[2] += currentX;
                            pathFloat[3] += currentY;
                            lastQuadControlX = pathFloat[0];
                            lastQuadControlY = pathFloat[1];
                            p.quadTo(pathFloat[0], pathFloat[1], pathFloat[2], pathFloat[3]);
                            currentX = (float) p.getCurrentPoint().getX();
                            currentY = (float) p.getCurrentPoint().getY();
                           break;
                  case 'T':  // true type - quadratic 
                           p.quadTo(2*currentX-lastQuadControlX, 2*currentY-lastQuadControlY, pathFloat[0], pathFloat[1]);
                           currentX = (float) p.getCurrentPoint().getX();
                           currentY = (float) p.getCurrentPoint().getY();

                           break;

                  case 't':  // relative true type - quadratic 
                           p.quadTo(2*currentX-lastQuadControlX, 2*currentY-lastQuadControlY, pathFloat[0]+currentX, pathFloat[1]+currentY);
                           currentX = (float) p.getCurrentPoint().getX();
                           currentY = (float) p.getCurrentPoint().getY();
                           break;
                }

              }
              // render the path here
                svgGraphics.setPaint(localStyle.getFillColor());
                svgGraphics.fill(p);
                svgGraphics.setStroke(localStyle.getStroke());
                svgGraphics.setPaint(localStyle.getStrokeColor());
                svgGraphics.draw(p);
            } 
        } // has nmore elements
    }
     
  
    public void getSvgSize() {
    int pixelsPerInch =0;
        int pxOffset = -1;
        int inchOffset = -1;
        boolean noMeasure = false;
        Enumeration e = parser.getElements();
        while (e.hasMoreElements()){
           Element elem =  (Element) e.nextElement();


            if (elem.getTagName().equals("svg")) {
              String tempWidth = elem.getAttribute("width");
              String tempHeight = elem.getAttribute("height");

              pxOffset = tempWidth.indexOf("px");
              if (pxOffset < 0 ){
                  inchOffset = tempWidth.indexOf("inch");
                    if (inchOffset < 0){
                        noMeasure = true;
                    }
              } 
              if (noMeasure){ 
                  tempWidth = Utility.trim(tempWidth);
                  svgWidth = (short)new Integer(tempWidth).intValue();                  
              }
              if (pxOffset >=0){
                 tempWidth = tempWidth.substring(0, pxOffset  );
                 tempWidth = Utility.trim(tempWidth);                 
                 svgWidth = (short)new Integer(tempWidth).intValue();
              } 
              if (inchOffset >=0){
                  tempWidth = tempWidth.substring(0, offset );
                  tempWidth = Utility.trim(tempWidth);
                  svgWidth = (short)new Integer(tempWidth).intValue();
                  pixelsPerInch =Toolkit.getDefaultToolkit().getScreenResolution(); 
                  svgWidth *= pixelsPerInch;
              }

              pxOffset = tempHeight.indexOf("px");
              if (pxOffset < 0 ){
                  inchOffset = tempWidth.indexOf("inch");
                    if (inchOffset < 0){
                        noMeasure = true;
                    }
              } 
              if (noMeasure){
                  tempHeight = Utility.trim(tempHeight);
                  svgHeight = (short)new Integer(tempHeight).intValue();                  
              }
              if (pxOffset >=0){
                 tempHeight = tempHeight.substring(0, pxOffset  );
                 tempHeight = Utility.trim(tempHeight);
                 svgHeight = (short)new Integer(tempHeight).intValue();                 

              } 
              if (inchOffset >=0){
                  tempHeight = tempHeight.substring(0, offset );
                  tempHeight = Utility.trim(tempHeight);
                  svgHeight = (short)new Integer(tempHeight).intValue();
                  pixelsPerInch =Toolkit.getDefaultToolkit().getScreenResolution(); 
                  svgWidth *= pixelsPerInch;
              }
              
              return;
            }  // if it is svg
        } //while

    }
    
    // review and document - this is from Batik
        public  GeneralPath arcTo(double rx, double ry,
                                   double angle,
                                   boolean largeArcFlag,
                                   boolean sweepFlag,
                                   double x, double y, GeneralPath pa) {
        //
        // Elliptical arc implementation based on the SVG specification notes
        //

        // Ensure radii are valid
        if (rx == 0 || ry == 0) {
            pa.lineTo((float) x, (float) y);
            return (pa);
        }
        // Get the current (x, y) coordinates of the path

        Point2D p2d = pa.getCurrentPoint();
        double x0 = p2d.getX();
        double y0 = p2d.getY();
        
//        double x0 = 100;
//        double y0 = 100;
	if (x0 == x && y0 == y) {
	    // If the endpoints (x, y) and (x0, y0) are identical, then this
	    // is equivalent to omitting the elliptical arc segment entirely.
	    return(pa) ;
	}
        // Compute the half distance between the current and the final point
        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        // Convert angle from degrees to radians
        angle = Math.toRadians(angle % 360.0);
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);

        //
        // Step 1 : Compute (x1, y1)
        //
        double x1 = (cosAngle * dx2 + sinAngle * dy2);
        double y1 = (-sinAngle * dx2 + cosAngle * dy2);
        // Ensure radii are large enough
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double Prx = rx * rx;
        double Pry = ry * ry;
        double Px1 = x1 * x1;
        double Py1 = y1 * y1;
        // check that radii are large enough
        double radiiCheck = Px1/Prx + Py1/Pry;
        if (radiiCheck > 1) {
            rx = Math.sqrt(radiiCheck) * rx;
            ry = Math.sqrt(radiiCheck) * ry;
            Prx = rx * rx;
            Pry = ry * ry;
        }

        //
        // Step 2 : Compute (cx1, cy1)
        //
        double sign = (largeArcFlag == sweepFlag) ? -1 : 1;
        double sq = ((Prx*Pry)-(Prx*Py1)-(Pry*Px1)) / ((Prx*Py1)+(Pry*Px1));
        sq = (sq < 0) ? 0 : sq;
        double coef = (sign * Math.sqrt(sq));
        double cx1 = coef * ((rx * y1) / ry);
        double cy1 = coef * -((ry * x1) / rx);

        //
        // Step 3 : Compute (cx, cy) from (cx1, cy1)
        //
        double sx2 = (x0 + x) / 2.0;
        double sy2 = (y0 + y) / 2.0;
        double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
        double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

        //
        // Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
        //
        double ux = (x1 - cx1) / rx;
        double uy = (y1 - cy1) / ry;
        double vx = (-x1 - cx1) / rx;
        double vy = (-y1 - cy1) / ry;
        double p, n;
        // Compute the angle start
        n = Math.sqrt((ux * ux) + (uy * uy));
        p = ux; // (1 * ux) + (0 * uy)
        sign = (uy < 0) ? -1d : 1d;
        double angleStart = Math.toDegrees(sign * Math.acos(p / n));

        // Compute the angle extent
        n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        p = ux * vx + uy * vy;
        sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
        double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
        if(!sweepFlag && angleExtent > 0) {
            angleExtent -= 360f;
        } else if (sweepFlag && angleExtent < 0) {
            angleExtent += 360f;
        }
        angleExtent %= 360f;
        angleStart %= 360f;

        //
        // We can now build the resulting Arc2D in double precision
        //
        Arc2D.Double arc = new Arc2D.Double();
        arc.x = cx - rx;
        arc.y = cy - ry;
        arc.width = rx * 2.0;
        arc.height = ry * 2.0;
        arc.start = -angleStart;
        arc.extent = -angleExtent;
        
        AffineTransform t = AffineTransform.getRotateInstance(angle, cx, cy);
        Shape s = t.createTransformedShape(arc);
        pa.append(s, true);
        return(pa);

    }
    

    public static void main (String args[]) {

        String filename = null;
        if (args.length > 0) {
            filename = args[0];
            svgImage svgTest = new svgImage(filename);
           JFrame frame;
           frame = new JFrame("test SVG");
           frame.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {System.exit(0);}
            });
           frame.getContentPane().add("Center", svgTest);
           frame.pack();
           frame.setVisible(true);
         }
         else{
             System.out.println ("Useage: java svgImage <source file>");       
         }
    } 

} //end svgImage





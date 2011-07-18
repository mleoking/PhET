package org.openscience.jmol.app.webexport;

import javax.vecmath.Point3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.i18n.GT;

class Widgets { 
  
  // group of javascript widgets to allow user input to Jmol

  Widget[] widgetList = new Widget[5];

  Widgets() {
    // this should just be a list of available widgets
    widgetList[0] = new SpinOnWidget();
    widgetList[1] = new BackgroundColorWidget();
    widgetList[2] = new StereoViewWidget();
    widgetList[3] = new AnimationWidget();
    widgetList[4] = new ConsoleWidget();
    // widgetList[3] = new DownLoadWidget();
  }

  abstract class Widget {
    String name;

    /**
     * 
     * Each Widget must implement this function and make sure to use
     * the appletID number to specify the target applet i.e. "JmolApplet(appletID)".
     * NOTE anything that must be translated in the web page should be wrapped in both a call to
     * GT.escapeHTML and GT._ as in the following: GT.escapeHTML(GT._("text to translate"))
     * @param appletID
     * @param instance
     * @return  the JavaScript and html to implement the widget
     */
    abstract String getJavaScript(int appletID, JmolInstance instance);

    /**
     *  
     *  A COPY OF THIS .JS FILE MUST BE STORED IN THE html PART OF WEBEXPORT
     *  
     * @return  "none" (no file needed) or javascript file necessary to implement the widget
     */
    abstract String getJavaScriptFileName();// returns the name of the

    /**
     * The list of files returned by this function should contain the full path to
     * each file.  The only exception is that files that only contain a filename
     * will be assumed to be in the html section of WebExport.
     * @return string of filenames.
     */
    abstract String[] getSupportFileNames();// returns array of support file names.
    //These file names should include the full path within the Java application jar.
    //files stored in html part of WebExport will be found even if only the filename
    //is given.

  }

  class SpinOnWidget extends Widget {
    SpinOnWidget() {
      name = GT._("Spin on/off");
    }
    @Override
    String[] getSupportFileNames(){
      String[] fileNames = new String[0];
      return(fileNames);
    }
    @Override
    String getJavaScriptFileName() {
      return "JmolSpin.js";
    }

    @Override
    String getJavaScript(int appletID, JmolInstance instance) {
      return "<input type=\"checkbox\""
          + " id=\"JmolSpinWidget" + appletID + "\""
          + (instance.spinOn ? " checked=\"\"" : "")
          + " onclick=\"jmol_spin(this.checked," + appletID + ");\" "
          + "title=\"" + GT.escapeHTML(GT._("enable/disable spin")) + "\" />"
          + "<label for=\"JmolSpinWidget" + appletID + "\">" 
          + GT.escapeHTML(GT._("Spin on")) + "</label>";
    }
  }

  class BackgroundColorWidget extends Widget {
    BackgroundColorWidget() {
      name = GT._("Background Color");
    }

    @Override
    String getJavaScriptFileName() {
      return ("JmolColorPicker.js");
    }
    
    @Override
    String[] getSupportFileNames(){
      String[] fileNames = new String[0];
      return(fileNames);
    }
    
    @Override
    String getJavaScript(int appletID, JmolInstance instance) {
      Point3f ptRGB = Graphics3D.colorPointFromInt2(instance.bgColor);
      return "<table><tbody><tr><td style=\"min-width:70px;\">"
          + GT.escapeHTML(GT._("background color:"))
          + "</td><td style=\"min-width:70px;\"><script type = 'text/javascript'>"
          + "var scriptStr = 'color background $COLOR$;';"
          + "jmolColorPickerBox(scriptStr, [" 
          + (int)ptRGB.x + "," + (int)ptRGB.y + "," + (int)ptRGB.z
          + "], 'backbox"
          + appletID + "',  '" + appletID + "');"
          + "</script></td></tr></tbody></table>";
    }
  }

  class StereoViewWidget extends Widget {
    StereoViewWidget() {
      name = GT._("Stereo Viewing");
    }

    @Override
    String getJavaScriptFileName() {
      return "none";
    }
    
    @Override
    String[] getSupportFileNames(){
      String[] fileNames = new String[0];
      return(fileNames);
    }
    
    @Override
    String getJavaScript(int appletID, JmolInstance instance) {
      return "<select id=\"StereoMode" + appletID + "\" title=\""
          + GT.escapeHTML(GT._("select stereo type")) + "\""
          + "onchange=\"void(jmolScriptWait((this.options[this.selectedIndex]).value,"
          + appletID + "));\">"
          + "\n<option selected=\"\" value=\"stereo off\">"
          + GT.escapeHTML(GT._("Stereo Off")) + " </option>"
          + "\n<option value=\"stereo REDBLUE\">" + GT.escapeHTML(GT._("Red/Blue"))
          + "</option>"
          + "\n<option value=\"stereo REDCYAN\">" + GT.escapeHTML(GT._("Red/Cyan"))
          + "</option>"
          + "\n<option value=\"stereo REDGREEN\">" + GT.escapeHTML(GT._("Red/Green"))
          + "</option>"
          + "\n</select>";
    }
  }
  class AnimationWidget extends Widget {
    AnimationWidget() {
      name = GT._("Animation Control");
    }

    @Override
    String getJavaScriptFileName() {
      return ("JmolAnimationCntrl.js");
    }

    @Override
    String[] getSupportFileNames(){
      String[] fileNames = new String[9];
      String imagePath = "org/openscience/jmol/app/images/";
      fileNames[0] = imagePath+"lastButton.png";
      fileNames[1] = imagePath+"playButton.png";
      fileNames[2] = imagePath+"playLoopButton.png";
      fileNames[3] = imagePath+"playOnceButton.png";
      fileNames[4] = imagePath+"playPalindromeButton.png";
      fileNames[5] = imagePath+"prevButton.png";
      fileNames[6] = imagePath+"pauseButton.png";
      fileNames[7] = imagePath+"nextButton.png";
      fileNames[8] = imagePath+"firstButton.png";
      return(fileNames);
    }
    
    @Override
    String getJavaScript(int appletID, JmolInstance instance) {
      String jsString ="<table id=\"AnimContrl\" class=\"AnimContrlCSS\">";
      jsString +="<tbody><tr><td>"+GT.escapeHTML(GT._("Animation"))+"</td></tr><tr><td><table><tbody>";
      jsString +="<tr><td><button title=\""+GT.escapeHTML(GT._("First Frame"))+"\" onclick=\"void(jmolScriptWait(\'frame rewind\',"+appletID+"));\">";
      jsString +="<img src = \"firstButton.png\"></button></td>";
      jsString+= "<td><button title=\""+GT.escapeHTML(GT._("Previous Frame"))+"\" onclick=\"void(jmolScriptWait(\'frame previous\',"+appletID+"));\">";
      jsString+= "<img src = \"prevButton.png\" ></button></td>";        
      jsString+= "<td><button title=\""+GT.escapeHTML(GT._("Play"))+"\" onclick=\"void(jmolScriptWait(\'frame play\',"+appletID+"));\">";
      jsString+= "<img src = \"playButton.png\"></button></td>";        
      jsString+= "<td><button title=\""+GT.escapeHTML(GT._("Next Frame"))+"\" onclick=\"void(jmolScriptWait(\'frame next\',"+appletID+"));\">";
      jsString+= "<img src = \"nextButton.png\"></button></td>";        
      jsString+= "<td><button title=\""+GT.escapeHTML(GT._("Pause"))+"\" onclick=\"void(jmolScriptWait(\'frame pause\',"+appletID+"));\">";
      jsString+= "<img src = \"pauseButton.png\"></button></td>"; 
      jsString+= "<td><button title=\""+GT.escapeHTML(GT._("Last Frame"))+"\" onclick=\"void(jmolScriptWait(\'frame last\',"+appletID+"));\">";
      jsString+= "<img src = \"lastButton.png\"></button></td>";
      jsString+= "</tr></tbody></table><table><tbody><tr><td>"+GT.escapeHTML(GT._("Mode:"))+"</td>";
      jsString+= "<td id=\"jmol_loop_"+appletID+"\"><button title=\""+GT.escapeHTML(GT._("Loop"))+"\" onclick=\"jmol_animationmode(\'loop\',"+appletID+");\">";
      jsString+= "<img src = \"playLoopButton.png\" ></button></td>";
      jsString+= "<td id=\"jmol_palindrome_"+appletID+"\"><button title=\""+GT.escapeHTML(GT._("Palindrome"))+"\" onclick=\"jmol_animationmode(\'palindrome\', "+appletID+");\">";
      jsString+= "<img src = \"playPalindromeButton.png\" ></button></td>";
      jsString+= "<td id=\"jmol_playOnce_"+appletID+"\" style=\"background:blue;\"><button title=\""+GT.escapeHTML(GT._("Play Once"))+"\" style=\"font-size:0px\" onclick=\"jmol_animationmode(\'playOnce\', "+appletID+");\">";
      jsString+= "<img src = \"playOnceButton.png\" ></button></td></tr></tbody></table></td></tr></tbody></table>";
      return (jsString);
    }
    
   }
 
  class ConsoleWidget extends Widget {
    ConsoleWidget() {
      name = GT._("Open Console Button");
    }

    @Override
    String getJavaScriptFileName() {
      return ("none");
    }
    
    @Override
    String[] getSupportFileNames(){
      String[] fileNames = new String[0];
      return(fileNames);
    }

    @Override
    String getJavaScript(int appletID, JmolInstance instance) {
      return ("<button title=\"" + GT.escapeHTML(GT._("launch Jmol console"))
          + "\" onclick=\"jmolScript(\'console\',"+appletID+");\">"
          + GT.escapeHTML(GT._("Jmol Console")) + "</button>");
    }
  }
  class DownLoadWidget extends Widget {
    DownLoadWidget() {
      name = GT._("Download view");
    }

    @Override
    String getJavaScriptFileName() {
      // TODO
      return ("none");
    }
    
    @Override
    String[] getSupportFileNames(){
      String[] fileNames = new String[0];
      return(fileNames);
    }

    @Override
    String getJavaScript(int appletID, JmolInstance instance) {
      // TODO
      return (GT._("unimplemented"));
    }
  }

}

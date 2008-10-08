//<pre>
/*
Interface for creating/using control menus for the renderer:

Programmer interface:

	Creating stuff:

		Widget button1 = rootMenu.add(Widget.BUTTON, "foo");
		Widget slider1 = rootMenu.add(Widget.SLIDER, "foo");
		Widget menu1   = rootMenu.add(Widget.MENU  , "foo");

		button1.addState("no").addState("yes"); // ADD A STATE TO A BUTTON
		button1.setState(1);                    // SET BUTTON STATE
		int state = button1.getState();         // GET BUTTON STATE

		slider1.setValue(0.7);                  // SET SLIDER VALUE 0.0 ... 1.0
		double value = slider1.getValue();      // GET SLIDER VALUE 0.0 ... 1.0

	Methods to deal with the mouse:

	 	Widget find(int x, int y); // ROOT WIDGET OBJECT CALLS THIS ON MOUSE-DOWN
		void mouseDrag(int x, int y);
		void mouseUp(int x, int y);

User interface:

	Initially only the rootMenu is visible, in the upper left corner of the applet,
	if it has any children.

	Cycle through the states of a button by clicking on it.

	Modify the value of a slider by clicking or dragging on it.

	Toggle a menu open/closed by clicking on it.
	When it is open you can see its children.
*/

package render;
import java.util.*;
import java.awt.*;

public class Widget extends Vector{

   private static final long serialVersionUID = -2835432274973884522L;
   public int W = 60, numLines; // numLines only applies to menus and indicates
   // how many lines are in the label (names(0 to numLines -1) will be used for
   // the label).  If a label line is too long, it will get broken up automatically
   // (along word boundaries if possible)

   public boolean oneLine = false; // if true and numLines == 1, when line gets
   // too long, W is increased to fit it.  oneLine only applies to menus
   
   // some extra space on the top and bottom makes it
   // look better.
   public int margin = 0; // default no margin.  Margin is added to H (height)
   // so the real height is H + margin * 2
   
   public static int H = 12;// rendering adjusts this value to the height of the
   // font.
   public int height; // height of Widget
   
   public static int BUTTON = 0;
   public static int SLIDER = 1;
   public static int MENU   = 2;

   public Widget(int type, String name) {
      this.type = type;
      
      if(name != null){
        names.addElement(name);
        numLines = 1;
      }else{
        numLines = 0; 
      }
      root = this;
   }

   public Widget add(int type, String name) {
      Widget child = new Widget(type, name);
      addElement(child);
      child.root = root;
      return child;
   }

   public Widget addState(String name) {
      names.addElement(name);
      return this;
   }

   // body of copy moved to clone() to allow subclasses to create copies
   // easily (since they can't do some of what's in clone())
   
   public Widget copy() {
      return (Widget)clone();
   }
   
   // separated from copy() so that subclasses can create copies (because
   // some of the stuff in clone() isn't accessible to subclasses and copy()
   // would return a Widget and not the subclasses's type) - AW
   
   public Object clone(){
     
     Widget clone = (Widget)super.clone();
     clone.names = (Vector)names.clone();
     
     clone.clear(); // shallow copy was not good enough
     for (int i = 0 ; i < size() ; i++)
       clone.addElement(((Widget)elementAt(i)).clone());
     
     return clone;
     
   }

   public Widget child(int i) {
      if (i < 0 || i >= size())
         return null;
      return (Widget)elementAt(i);
   }

   public Widget close() { isOpen = false; return this; }

   public boolean consumesEvents() { return root.consumesEvents; }

   public Widget down() { isDown = true; return this; }

   public Widget find(int x, int y) {
      Widget found = null;

      // to augment height
      int extraSpaceBetweenLines = 0;
      
      if(numLines > 1){
       
        extraSpaceBetweenLines = numLines - 1;
        
      }
      
      if (x >= this.x && x < this.x + W && y >= this.y && y < this.y + H * numLines + extraSpaceBetweenLines)
         return this;

      if (isOpen)
         for (int i = 0 ; i < size() ; i++) {
            Widget widget = child(i).find(x,y);
	    if (widget != null)
	       return widget;
         }

      return null;
   }

   public String getName() { return name(0); }

   public int getState() { return state; }

   public int getType() { return type; }

   public double getValue() { return value; }

   public int getX() { return x; }

   public int getY() { return y; }

   public boolean isTearOff() { return isTearOff; }

   public boolean mouseDrag(int x, int y) {
      switch (type) {
      case 0: // BUTTON
         break;
      case 1: // SLIDER
         value = Math.max(0, Math.min(1, (double)(x - this.x) / W));
         break;
      case 2: // MENU
         break;
      }
      return consumesEvents();
   }

   public boolean mouseUp(int x, int y) {
      switch (type) {
      case 0: // BUTTON
         state = (state + 1) % names.size();
         break;
      case 1: // SLIDER
         value = Math.max(0, Math.min(1, (double)(x - this.x) / W));
         break;
      case 2: // MENU
         isOpen = ! isOpen;
         break;
      }
      up();
      return consumesEvents();
   }

   public Widget moveBy(int x, int y) {
      this.x += x;
      this.y += y;
      return this;
   }

   public Widget moveTo(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public Widget open() { isOpen = true; return this; }

   public int render(Graphics g) { return render(g, x, y); }

   public int render(Graphics g, int x, int y) {
      H = g.getFont().getSize();
      FontMetrics fm = g.getFontMetrics();
      this.x = x;
      this.y = y;
      g.setColor(bgColor);
      
      int extraSpaceBetweenLines = 0;
      
      if(numLines > 1){
       
        extraSpaceBetweenLines = numLines - 1;
        
      }
      
      g.fill3DRect(x,y,W,H*numLines  + extraSpaceBetweenLines + margin*2,!isDown);
      if (type == SLIDER) {
         g.setColor(sliderBarColor);
         g.fill3DRect(x+1,y+1,(int)((W-2)*value),H-2,true);
         g.setColor(Color.black);
	 String s = "" + (int)(99 * value + .5);
	 drawString(g, s, x+W-1 - fm.stringWidth(s), y+H-2);
      }
      g.setColor(Color.black);
      if (type == MENU) {
         
         if(size() != 0){ 
           // draw the show/hide contents rectangle (otherwise there are no
           // contents to show)
        
           if (! isOpen)
	     g.fillRect(x+1,y+2,H/2-1,H-5);
	   g.drawRect(x+1,y+2,H/2-1,H-5);
         }
         
         // draw each of the names on one line
         
         if(numLines != 0){
         
           int heightCurrentLine = margin;
           
           for(int i = 0; i < numLines; i++){
             
             heightCurrentLine += H;
             
             if(i > 0){
              
               heightCurrentLine += 1; // extra space between lines
               
             }
             
             if(fm.stringWidth(name(i)) > W - H){ // -H because of the indent (for the little box
               // that signifies open/close)
               // that keeps the text from using all the space of the widget
              
               // line is too long.  Put extra on another line.
               
               // if only want one line, then extend W
               
               if(oneLine && numLines == 1){
                
                 W = fm.stringWidth(name(i)) + H;
                 
               }else{
                 
                 // line is too long.  Put extra on another line.
               
                 // what stays
               
                 String current = "";
                 String prev = "";
                 String nextLine = name(i); // like prev
                 String old_nextLine = name(i);

                 // first try to break along word boundaries

                 while(fm.stringWidth(current) < W - H){

                   // move one word from nextLine to current
                   prev = current;
                   old_nextLine = nextLine;

                   int breakPoint = nextLine.indexOf(" ");

                   if(breakPoint == -1){

                     // no more word boundaries

                     break;

                   }

                   current = current.concat(nextLine.substring(0,breakPoint + 1));
                   nextLine = nextLine.substring(breakPoint + 1,nextLine.length());

                 }

                 if(prev.length() == 0){

                   // okay, we have a long word here and can't break along word
                   // boundaries

                   current = "";
                   prev = "";
                   nextLine = name(i);
                   old_nextLine = name(i);

                   while(fm.stringWidth(current) < W - H){

                     // move one char from nextLine to current
                     prev = current;

                     current = current.concat(nextLine.substring(0,1));
                     old_nextLine = nextLine;
                     nextLine = nextLine.substring(1,nextLine.length());

                   }

                 }

                 names.set(i,prev); // current is too long
                 names.insertElementAt(old_nextLine,i + 1);

                 numLines++;
               }
               
             }
             
             drawString(g, name(i), x + H -2, y + heightCurrentLine -2);
           }
           
         }
           
      }
      else
         drawString(g, name(state), x+2, y+H-2);
      height = H*numLines +extraSpaceBetweenLines + margin*2;
      y += height;
      
      if (isOpen)
         for (int i = 0 ; i < size() ; i++)
            y = child(i).render(g, x+H-4, y);
      return y;
   }

   public Widget tearOff() { isTearOff = true; return this; }

   public Widget setState(int state) {
      this.state = Math.max(0, Math.min(names.size()-1, state));
      return this;
   }

   public Widget setValue(double value) {
      this.value = Math.max(0, Math.min(1, value));
      return this;
   }

   public String toString() {
      return "{ " + (type==0?"BUTTON ":type==1?"SLIDER ":"MENU ") + name(0) + " " + x + "," + y + " }";
   }

   public Widget up() { isDown = false; return this; }

   void drawString(Graphics g, String s, int x, int y) {
      g.setColor(Color.black);
      g.drawString(s, x, y);
      g.drawString(s, x+1, y);
   }

   String name(int i) {
      return i<0||i>=names.size() ? "" : (String)names.elementAt(i);
   }
   
   public void setBackground(Color bgColor){
   	
   	 this.bgColor = bgColor; 	
   	
   }
   
   /**
    * The equals for Vector does comparison of stuff that's contained
    * in Vector.  If Widget used that, then two Widgets without children
    * would be equal even if they were very different Widgets.
    *
    */

   public boolean equals(Object o){
     
     return o == this;
    
   }
   
   public boolean consumesEvents = true;
   
   private int x = -1000, y = 0;
   private int type, state = 0;
   private boolean isOpen = false, isDown = false, isTearOff;
   private double value = 0.5;
   protected Vector names = new Vector(); // it would be nice for subclasses to have access to this.
   private Widget root;

   private Color bgColor = new Color(255,255,255,80);
   private static Color sliderBarColor = new Color(255,255,255,80);
}


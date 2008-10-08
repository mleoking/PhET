
package render;
import java.util.*;
import java.awt.*;

public class Menus extends Vector {

   public Widget menu(int i) {
      return i < 0 || i >= size() ? null : (Widget)elementAt(i);
   }

   public Widget add(Widget menu) {
      remove(menu);
      addElement(menu);
      return menu;
   }

   public Widget add(String label, int x, int y) {
      Widget menu = new Widget(Widget.MENU, label);
      menu.moveTo(x,y);
      return add(menu);
   }

   public void remove(Widget menu) {
      for (int i = 0 ; i < size() ; i++)
	 if (elementAt(i) == (Object)menu) {
	    removeElementAt(i);
	    break;
	 }
   }

   public void render(Graphics g) {
      for (int i = 0 ; i < size() ; i++)
          menu(i).render(g);
   }

   public Widget widgetAt(int x, int y) {
      Widget widget = null;
      for (int i = size()-1 ; i >= 0 ; i--)
         if ((widget=menu(i).find(x,y)) != null)
            break;
      return widget;
   }

   int downX, downY, mx, my;
   Widget newTearOff = null;

   public Widget newTearOff() { return newTearOff; }

   public boolean mouseDown(int x, int y) {
      downX = mx = x;
      downY = my = y;
      newTearOff = null;
      selected = widgetAt(x,y);
      if (selected != null)
	 selected.down();
      return selected != null && selected.consumesEvents();
   }

   public boolean mouseDrag(int x, int y) {

      // MOVE A TEAR-OFF MENU

      if (selected != null && selected.isTearOff()) {
	 selected.moveBy(x - mx, y - my);
	 mx = x;
	 my = y;
      }

      // CREATE A TEAR-OFF

     
      else if (selected != null && selected.getType() == Widget.MENU &&
               (x >= selected.getX() + selected.W || x < selected.getX())) {
	 selected.up();
         selected = selected.copy();
	 newTearOff = selected;
         addElement(selected);
         selected.tearOff();
         selected.moveBy(x - downX, y - downY);
	 mx = x;
	 my = y;
      }
      return selected != null && selected.mouseDrag(x,y);
   }

   public boolean mouseUp(int x, int y) {
      if (selected != null) {
         if (! selected.isTearOff() || selected.find(downX,downY) == selected)
            selected.mouseUp(x,y);
         else
	    selected.up();

	 selected = null;
         return true;
      }
      return false;
   } 
   
   private static final long serialVersionUID = 4498669601629532308L;
   private Widget selected = null;
}


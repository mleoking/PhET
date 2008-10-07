//<pre>

package render;

import java.awt.*;


public interface Renderable {

   public void render(Graphics g);

   public boolean mouseDown(Event evt, int x, int y);
   public boolean mouseUp(Event evt, int x, int y);
   public boolean mouseDrag(Event evt, int x, int y);
   public boolean keyUp(Event event, int key) ;
}

//<pre>

package render;

import java.awt.*;

/**
 * used for creating embedded renderers 
 * Created on Jul 1, 2004
 */
public interface InteractiveRenderable {

   public void render(Graphics g);

   public boolean mouseDown(Event evt, int x, int y);
   public boolean mouseUp(Event evt, int x, int y);
   public boolean mouseDrag(Event evt, int x, int y);
   public boolean keyUp(Event event, int key) ;
}

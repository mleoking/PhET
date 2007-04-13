import JSci.instruments.*;
import java.awt.*;
import javax.swing.*;

/** 
    An example of image processing in real time.
    A simulated image source is created; it is passed through a
    filter, that adds some rectangles, and a ROI selection is added.
    Finally, a Player is added and started.
*/

public class Test {
    public static void main(String[] args) {
	ROI r = new RectangularROI(10,10,30,30);
	JSci.instruments.ImageFilter f = new MyFilter();
	Player p = new Player();
	JSci.instruments.ImageSource fs = new TestImageSource();
	fs.setSink(f);
	f.setSink(p);
	p.addROI(r);
	p.start();
    }
}

class MyFilter extends JSci.instruments.ImageFilterAdapter {
    public void filter(JSci.instruments.Image f) { 
	f.addOverlay(new JSci.instruments.Overlay() {
		public void paint(Graphics g) {
		    g.setColor(Color.YELLOW);
		    ((Graphics2D)g).draw(new Rectangle(3,3,100,70));
		}
	    });
    } 
    public Component getFilterControlComponent() {
	return new JLabel("FILTER");
    }
    public String getName() { return "Mio filtro"; }
}

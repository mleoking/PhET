/**
 * Canvas for painting controlled curves into
 */
import java.applet.*;
import java.awt.*;

public class CurveCanvas extends Canvas implements Runnable{

  Image offscreen;
  ControlCurve curve;
  MouseEvent mouseEvent;

  public CurveCanvas(MouseEvent mouseEvent, ControlCurve curve){
    this.mouseEvent = mouseEvent;
    this.curve = curve;
  }
  
  public void paint(Graphics g) {
    if (offscreen == null) {
      offscreen = createImage(size().width, size().height);
    }
    Graphics og = offscreen.getGraphics();
    og.clearRect(0,0,size().width,size().height);
    curve.paint(og);
    g.drawImage(offscreen,0,0,null);
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void update() {
    update(getGraphics());
  }

  public void run() {
    for (;;) {
      Event e = mouseEvent.get();
      if (e.id == Event.MOUSE_DOWN) {
	if (curve.selectPoint(e.x,e.y) == -1) { /*no point selected add new one*/
	  curve.addPoint(e.x,e.y);
	  update();
	}
      } else if (e.id == Event.MOUSE_DRAG) {
	curve.setPoint(e.x,e.y);
	  update();
      } else if (e.id == Event.MOUSE_UP) {
	if(e.shiftDown()) {
	  curve.removePoint(); //Shift Click removes control points
	  update();
	}
      }
    }
  }


}


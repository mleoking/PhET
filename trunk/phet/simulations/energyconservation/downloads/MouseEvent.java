/**
 * This class is used for communication between the thread that responds to
 * user input and the one that does the drawing.
 * It compresses repeated MouseDrag events into one MouseDrag
 */
import java.awt.*;

public class MouseEvent {
  private Event e;

  public synchronized void put (Event e){
    while (this.e != null &&
	   !(this.e.id == Event.MOUSE_DRAG && e.id == Event.MOUSE_DRAG)) {
      try {
	wait();
      } catch (InterruptedException ex) {
	System.err.println("Exception: " + ex);
      }
    }
    this.e = e;
    notify();
  }

  public synchronized Event get(){
    while (e == null) {
      try {
	wait();
      } catch (InterruptedException ex) {
	System.err.println("Exception: " + ex);
      }
    }
    notify();
    Event save = e;
    e = null;
    return save;
  }
} 

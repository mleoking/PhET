import java.awt.*;
import idx3d.*;

public class idx3d_TestApp extends Frame
{
	public static void main(String[] args)
	{
		new idx3d_TestApp();
	}

	public idx3d_TestApp()
	{
		this.resize(320,240);
		this.setTitle("idx3d_TestApp");
		this.move(200,200);
		this.add(new idx3d_TestComponent());
		this.show();
		this.toFront();	
	}
	
	public boolean handleEvent(Event evt)
	{
		if (evt.id==Event.WINDOW_DESTROY)
		{
			System.exit(0);
		}
		return super.handleEvent(evt);
	}
}
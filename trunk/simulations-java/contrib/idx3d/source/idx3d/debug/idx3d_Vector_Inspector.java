package idx3d.debug;
import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class idx3d_Vector_Inspector extends InspectorFrame
{
	public idx3d_Vector_Inspector(idx3d_Vector vec, String id)
	{
		super(vec, id);
		
		addEntry(new InspectorFrameEntry(this,"float","x",vec.x+""));
		addEntry(new InspectorFrameEntry(this,"float","y",vec.y+""));
		addEntry(new InspectorFrameEntry(this,"float","z",vec.z+""));
	}
	
}
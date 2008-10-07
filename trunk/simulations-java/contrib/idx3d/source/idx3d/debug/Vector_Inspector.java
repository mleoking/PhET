package idx3d.debug;
import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class Vector_Inspector extends InspectorFrame
{
	public Vector_Inspector(java.util.Vector vec, String id)
	{
		super(vec, id);
		addEntry(new InspectorFrameEntry(this,"int","size",vec.size()+""));
		
		java.util.Enumeration enum=vec.elements();
		int index=0;
		while (enum.hasMoreElements())
			addEntry(new InspectorFrameEntry(this,enum.nextElement(),"elementAt("+(index++)+")"));

	}
	
}
package idx3d.debug;

import idx3d.*;
import java.util.Vector;
import java.util.Hashtable;


public class Inspector
{
	private Inspector() {}
	
	public static void inspect(Object obj)
	{
		inspect(obj,null);
	}
	
	public static void inspect(Object obj, String name)
	{
		String id=(name!=null)?name:"";
		if (obj instanceof idx3d_Vector) new idx3d_Vector_Inspector((idx3d_Vector)obj,id);
		if (obj instanceof idx3d_Vertex) new idx3d_Vertex_Inspector((idx3d_Vertex)obj,id);
		if (obj instanceof idx3d_Object) new idx3d_Object_Inspector((idx3d_Object)obj,id);
		if (obj instanceof idx3d_Matrix) new idx3d_Matrix_Inspector((idx3d_Matrix)obj,id);
		if (obj instanceof idx3d_Triangle) new idx3d_Triangle_Inspector((idx3d_Triangle)obj,id);
		if (obj instanceof idx3d_Scene) new idx3d_Scene_Inspector((idx3d_Scene)obj,id);
		if (obj instanceof idx3d_Texture) new idx3d_Texture_Inspector((idx3d_Texture)obj,id);
		if (obj instanceof idx3d_Light) new idx3d_Light_Inspector((idx3d_Light)obj,id);
		if (obj instanceof idx3d_Material) new idx3d_Material_Inspector((idx3d_Material)obj,id);

		if (obj instanceof Vector) new Vector_Inspector((Vector)obj,id);
		if (obj instanceof Hashtable) new Hashtable_Inspector((Hashtable)obj,id);
	}
	
}

import idx3d.*;
import java.awt.*;

public class idx3d_TestComponent extends Panel implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;
	boolean initialized=false;
	boolean antialias=false;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	
	public idx3d_TestComponent()
	{
	}


	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			
			idx3d_Material crystal=new idx3d_Material("glass.material");
			crystal.setReflectivity(255);
			scene.addMaterial("Crystal",crystal);
			
			idx3d_Material plastic=new idx3d_Material(new idx3d_Texture("texture.jpg"));
			scene.addMaterial("Plastic",plastic);
			
			scene.environment.setBackground(idx3d_TextureFactory.CHECKERBOARD(160,120,2,0x000000,0x999999));
			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,80));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xFFCC99,100,40));
			
			// Create Torus as a Lattice Object from a circle path
				
				idx3d_Vector[] path=new idx3d_Vector[9];
				
				path[0]=new idx3d_Vector(0.4f,0.0f,0);
				path[1]=new idx3d_Vector(0.6f,0.3f,0);
				path[2]=new idx3d_Vector(0.8f,0.4f,0);
				path[3]=new idx3d_Vector(0.9f,0.3f,0);
				path[4]=new idx3d_Vector(1.0f,0.0f,0);
				path[5]=new idx3d_Vector(0.9f,-0.3f,0);
				path[6]=new idx3d_Vector(0.8f,-0.4f,0);
				path[7]=new idx3d_Vector(0.6f,-0.3f,0);
				path[8]=new idx3d_Vector(0.4f,0.0f,0);
		
				scene.addObject("Torus",idx3d_ObjectFactory.ROTATIONOBJECT(path,16));
				scene.object("Torus").rotate(4.2f,0.2f,-0.5f);
				scene.object("Torus").shift(-0.5f,0f,0f);
				scene.object("Torus").scale(0.72f);
				scene.object("Torus").setMaterial(scene.material("Plastic"));
				
			// Create Wineglass as a Lattice Object
			
				path=new idx3d_Vector[15];
				path[0]=new idx3d_Vector(0.0f,0.2f,0);
				path[1]=new idx3d_Vector(0.13f,0.25f,0);
				path[2]=new idx3d_Vector(0.33f,0.3f,0);
				path[3]=new idx3d_Vector(0.43f,0.6f,0);
				path[4]=new idx3d_Vector(0.48f,0.9f,0);
				path[5]=new idx3d_Vector(0.5f,0.9f,0);
				path[6]=new idx3d_Vector(0.45f,0.6f,0);
				path[7]=new idx3d_Vector(0.35f,0.3f,0);
				path[8]=new idx3d_Vector(0.25f,0.2f,0);
				path[9]=new idx3d_Vector(0.1f,0.15f,0);
				path[10]=new idx3d_Vector(0.1f,0.0f,0);
				path[11]=new idx3d_Vector(0.1f,-0.5f,0);
				path[12]=new idx3d_Vector(0.35f,-0.55f,0);
				path[13]=new idx3d_Vector(0.4f,-0.6f,0);
				path[14]=new idx3d_Vector(0.0f,-0.6f,0);
	
				scene.addObject("Wineglass",idx3d_ObjectFactory.ROTATIONOBJECT(path,16));
				scene.object("Wineglass").rotate(0.5f,0f,0f);
				scene.object("Wineglass").setMaterial(scene.material("Crystal"));

			idx_Thread = new Thread(this);
			idx_Thread.start();
			
			initialized=true;
	}

	public synchronized void paint(Graphics g)
	{
		repaint();
	}

	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				idx_Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				System.out.println("idx://interrupted");
			}
		}
	}

	public synchronized void update(Graphics g)
	{
		if (!initialized) return;
		if (autorotation)
		{
			scene.object("Torus").rotate(-0.05f,0f,0.05f);
			scene.object("Wineglass").rotate(0.00f,0.08f,-0.05f);
		}
		scene.render();
		g.drawImage(scene.getImage(),0,0,this);
	}		

	public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
   	     return true;
   	}

	public boolean mouseDown(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		setMovingCursor();
		return true;
	}

	public boolean keyDown(Event evt,int key)
	{
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,0.2f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,-0.2f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(-0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(0.2f,0f,0f); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
		if ((char)key=='.') {scene.defaultCamera.roll(0.2f); return true; }
		if ((char)key==',') {scene.defaultCamera.roll(-0.2f); return true; }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
			
		if ((char)key=='e') {export(); return true; }
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); return true; }		
		
		return true;
	}

	private void export()
	{
		try{
			idx3d_3ds_Exporter.exportToStream(new java.io.FileOutputStream(new java.io.File("export.3ds")),scene);
		}
		catch(Exception ignored){}
	}

	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		scene.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		return true;
	}

	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		setNormalCursor();
		return true;
	}
	
	private void setMovingCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.MOVE_CURSOR);
	}
	
	private void setNormalCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.HAND_CURSOR);		
	}
	
	private Frame getFrame()
	{
		Component comp=this;
		while ((comp=comp.getParent())!=null) if(comp instanceof Frame) return (Frame)comp;
		return null;
	}
	
	public void reshape(int x, int y, int w, int h)
	{
		super.reshape(x,y,w,h);
		if (!initialized) init();
		scene.resize(w,h);
	}

	
	public synchronized void repaint()
	{
		if (getGraphics() != null) update(getGraphics());
	}
}

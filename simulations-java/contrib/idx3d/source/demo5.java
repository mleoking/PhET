import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class demo5 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean antialias=false;

	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			scene.defaultCamera.setFov(100f);
			
			
			// Material setup
			
				scene.addMaterial("Glass",new idx3d_Material(getDocumentBase(),"glass.material"));
				scene.addMaterial("Chrome",new idx3d_Material(getDocumentBase(),"chrome.material"));
				scene.addMaterial("Blue",new idx3d_Material(getDocumentBase(),"blue.material"));
				scene.addMaterial("Orange",new idx3d_Material(0xFF6600));
			
				idx3d_Material flatGreen=new idx3d_Material(0x00FF00);
				flatGreen.setFlat(true);
				scene.addMaterial("Green",flatGreen);
				
				scene.addMaterial("Yellow",new idx3d_Material(0xFFCC00));
			
			// Light setup
			
				scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,80));			
				scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xFFCC99,100,40));
				scene.addLight("Light3",new idx3d_Light(new idx3d_Vector(0f,-1f,0.5f),0x666666,320,80));
			
			// Object setup
					
			try{
				new idx3d_3ds_Importer().importFromURL(new java.net.URL(getDocumentBase(),"demo5.3ds"),scene);
			}
			catch(Exception e){System.out.println(e+"");}
			
			scene.rebuild();
			for (int i=0; i<scene.objects;i++) scene.object[i].detach();
			scene.normalize();
			scene.scale(0.64f);
			
			scene.object("Blob1").setMaterial(scene.material("Glass"));				
			scene.object("Blob2").setMaterial(scene.material("Blue"));
			scene.object("Text1").setMaterial(scene.material("Chrome"));				
			scene.object("Text2").setMaterial(scene.material("Chrome"));
			scene.object("Cube").setMaterial(scene.material("Green"));
			scene.object("Cone").setMaterial(scene.material("Yellow"));
			scene.object("Sphere").setMaterial(scene.material("Orange"));
			idx3d_TextureProjector.projectFrontal(scene.object("Blob1"));
			
		
			idx3d_Texture bkgrd=idx3d_Texture.blendTopDown(
				idx3d_TextureFactory.CHECKERBOARD(this.size().width,this.size().height,4,0x000000,0x999999),
				new idx3d_Texture(getDocumentBase(),"idxbkgrd.jpg"));
			scene.environment.setBackground(bkgrd);
			
				
		
	}

	public synchronized void paint(Graphics g)
	{
	}

	public void start()
	{
		if (idx_Thread == null)
		{
			idx_Thread = new Thread(this);
			idx_Thread.start();
		}
	}
	
	public void stop()
	{
		if (idx_Thread != null)
		{
			idx_Thread.stop();
			idx_Thread = null;
		}
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
			catch (InterruptedException e){}
		}
	}

	public synchronized void update(Graphics g)
	{
		if (autorotation)
		{
			float speed=1;
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000)/20;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/1000)/20;
			scene.rotate(-speed*dx/4,speed*dy,speed*0.04f);
			
		}
		scene.object("Blob2").rotateSelf(0.02f,0.05f,-0.03f);
		scene.object("Cube").rotateSelf(-0.08f,0.02f,0.07f);
		scene.object("Cone").rotateSelf(0.03f,-0.06f,0.04f);
		scene.object("Text2").rotateSelf(0.03f,0,0.04f);
		
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
	
	public boolean mouseMove(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		return true;
	}

	public boolean keyDown(Event evt,int key)
	{
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,-0.2f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,0.2f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(-0.2f,0f,0f); return true; }
		
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
		return true;
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
}

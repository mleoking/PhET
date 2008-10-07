import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class demo11 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean antialias=false;
	idx3d_FXLensFlare lensFlare;
	
	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			
			scene.addMaterial("Sky",new idx3d_Material(idx3d_TextureFactory.CHECKERBOARD(128,128,1,0x000000,0x666666)));
			scene.material("Sky").setFlat(true);
			
			scene.addMaterial("Glass",new idx3d_Material(getDocumentBase(),"glass.material"));
			scene.material("Glass").setTransparency(60);
			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(-0.2f,-0.2f,1f),0xFFFFFF,120,120));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,-4f),0x0066FF,120,80));
			
			try{
				new idx3d_3ds_Importer().importFromURL(new java.net.URL(getDocumentBase(),"demon.3ds"),scene);
				scene.normalize();
				
				scene.object("Skydome").setMaterial(scene.material("Sky"));
				
				scene.object("Demon").setMaterial(scene.material("Glass"));
				idx3d_TextureProjector.projectTop(scene.object("Demon"));
				
				scene.object("Demon").matrixMeltdown();
				scene.defaultCamera.lookAt(scene.object("Demon").getCenter().transform(scene.matrix));
				scene.rotate(3.84159265f/2,0f,0f);
				scene.scale(3f);
				scene.shift(0,0.32f,0);
				
				lensFlare=new idx3d_FXLensFlare("LensFlare1", scene,false);
				lensFlare.preset2();
				scene.object("LensFlare1").scale(16f);
			}
			catch(Exception e){System.out.println(e+"");}
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
			float speed=0.64f;
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000)/20;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/2000)/8;
			scene.rotate(speed*dx,speed*dy,0f);
			scene.defaultCamera.setFov(80f+30f*(float)Math.sin(((float)System.currentTimeMillis())/1000));
		}
	
		scene.render();
		lensFlare.apply();
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
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		if ((char)key=='1') {lensFlare.preset1(); return true; }
		if ((char)key=='2') {lensFlare.preset2(); return true; }
		if ((char)key=='3') {lensFlare.preset3(); return true; }
		
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

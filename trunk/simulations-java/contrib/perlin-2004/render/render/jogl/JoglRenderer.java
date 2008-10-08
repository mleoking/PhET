package render.jogl;

import net.java.games.jogl.GL;
import net.java.games.jogl.GLDrawable;
import net.java.games.jogl.GLU;
import net.java.games.jogl.util.BufferUtils;
import net.java.games.jogl.util.GLUT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import render.Geometry;
import render.Material;
import render.Matrix;
import render.Renderer;
import render.Texture;

/**
 * JOGL version of the software renderer, instead of drawing triangles
 * into the buffer, we just push triangles into the graphics card.
 */
public class JoglRenderer extends Renderer
{
	List baseGeometry = new ArrayList(10);//reverse lookup for geometries
	
  public static boolean printError = false;// beware.. if your video card is not
  // top of the line, you might get a whole bunch of "Error: invalid operation"s
  // although the animation will look fine
  
  public static final int DEBUG_NEW_LIGHTS = 0x00000001; // OR this with DEBUG for lights dbg
  public static final int DEBUG_PICKING = 0x00000002; // OR this with DEBUG for picking dbg
  public static final int DEBUG_TEXTURE = 0x00000004; // etc...
  public static final int DEBUG_VERBOSE = 0x00000008; // etc...  
  public int DEBUG = 0x00000000; //| DEBUG_NEW_LIGHTS;
  
	public GL gl; 
	public GLU glu;
	public GLUT glut;
	public GLDrawable drawable;
  
  // Getters added: [dch 9/1/04] -----------
  public GL getGL()     { return this.gl;   } 
  public GLU getGLU()   { return this.glu;  } 
  public GLUT getGLUT() { return this.glut; } 
  
  // Method added: [dch 9/1/04] -----------
  public void setGLAmbient(float r, float g, float b) {
    gl.glLightModelfv
      (GL.GL_LIGHT_MODEL_AMBIENT, 
      new float[] {r, g, b, 1f });
  }

  private List textures = new ArrayList();
  private double br = 0, bg = 0, bb = 0;
  
  public int[] init(int w, int h) 
  {
     glut = new GLUT();
     
     nLights = 0;
     world = new Geometry();
     this.W = w;
     this.H = h;
     Matrix.identity(camera);
     computeCamera();

     if((DEBUG & DEBUG_VERBOSE) != 0)
       System.out.println("starting out fl "+FL);
     
     //Set up slight brighter than default ambient lighting - car
     float[] lmodel_ambient = { 1f, 1f, 1f, 1f };
     gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);
     gl.glEnable(GL.GL_LIGHTING);     
     gl.glEnable(GL.GL_DEPTH_TEST);     
     gl.glEnable(GL.GL_NORMALIZE);     
     
     pix = new int[w * h];
     return pix;
  }
  
  /**
   * Added to tell the hardware we are using a new texture,
   * this was the lazy way of doing it - having the actual
   * Texture class do it would be better..
   * @param tex
   */
	private void addTexture(Texture tex) 
  {
		if( (DEBUG & DEBUG_TEXTURE) != 0)
			System.out.println("Adding a texture ");
    
		int[] texname = new int[1];
		gl.glGenTextures(1,texname);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texname[0]);
		tex.textname = texname[0];
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		
		//it would be better if we could use the rectangle extension
		//but jogl only seems to have GL.GL_TEXTURE_RECTANGLE_NV
		//which is not nvidia specific i think
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, tex.width, tex.height, 0, GL.GL_BGRA, GL.GL_UNSIGNED_INT_8_8_8_8_REV,  tex.texels);
		textures.add(tex);
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR && printError) {
				System.out.println("Error: "+glu.gluErrorString(error));
		}		
	}

	private double mycam[] = new double[16];
	protected synchronized void computeCamera() {

		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (this.manualCameraControl) 
    {
			glu.gluLookAt(cameraPos[0],cameraPos[1],cameraPos[2],cameraAim[0], cameraAim[1], cameraAim[2],
				cameraUp[0], cameraUp[1], cameraUp[2]);
    }
		else 
    {
      glu.gluLookAt(0,0,FL,0,0,0,0,1,0);
      super.computeCamera();

    }
		
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mycam);
	}
	
	protected void fill(int x, int y, int w, int h, int packed) {
	}

	// CLEAR DAMAGED PART OF SCREEN
	protected void clearScreen() {
		gl.glClearColor((float)br,(float)bg,(float)bb,0);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

  //Changed to support up to eight lights via OpenGL lighting system. 
  //Throws RuntimeException if more than eight lights are added - car
  public void addLight(double x, double y, double z, double lR, double lG, double lB)
  {
    if (nLights >= 8) // OpenGL only supports eight lights sources
     {
      RuntimeException lightException =
        new RuntimeException("Cannot add more than eight light sources!");
      lightException.fillInStackTrace();
      throw lightException;
    }

    if( (DEBUG & DEBUG_NEW_LIGHTS) != 0 )
      System.out.println(
          "adding light ("+x+", "+y+", "+z+", "+lR+", "+lG+", "+lB+")");

    int lightIndex = GL.GL_LIGHT0 + nLights;
    float[] lightPosition = {(float)x,  (float)y,  (float)z,  0f };
    float[] lightDiffuse  = {(float)lR, (float)lG, (float)lB, 1f };
    float[] lightSpecular = {(float)lR, (float)lG, (float)lB, 1f };    
    
    gl.glLightfv(lightIndex, GL.GL_POSITION, lightPosition);
    gl.glLightfv(lightIndex, GL.GL_DIFFUSE,  lightDiffuse);
    gl.glLightfv(lightIndex, GL.GL_SPECULAR, lightSpecular);
    gl.glEnable(lightIndex);
    nLights++;
  }	
	
	public void setBgColor(double r, double g, double b) {
		br=r;bg=g;bb=b;	
	}
	
	public synchronized void render() 
  {		
		// find all textures that are animated and update them 
		// might make it harder for Texture to be self contain
		for (int i=0; i<textures.size(); i++)
    {
			Texture text = (Texture) textures.get(i);
			if (text.animated == true) {
				gl.glBindTexture(GL.GL_TEXTURE_2D, text.textname);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		
				// it would be better if we could use the rectangle extension
				// but jogl only seems to have GL.GL_TEXTURE_RECTANGLE_NV
				// which is not nvidia specific i think
				gl.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, text.width, text.height, GL.GL_BGRA, GL.GL_UNSIGNED_INT_8_8_8_8_REV,  text.texels);				
			}
		}		
		super.render();
	}

	/**
	 * Set up the state of each of the geometries so that we can draw them
	 */
 // private int count1=0
	protected void renderGeometry(Geometry s) 
  {
     int count1 = 0;
     s.material.tableMode = false;
	   matrix.copy(s.globalMatrix);
	   matrix.postMultiply(camera);
		
	   if (s.material.hasTexture()) {
	   		Texture text = s.material.getTexture();
	   		if (text.textname==0)
	   			this.addTexture(text);
	   }
	   //what does this do?
	   if (!renderT && transparencyOf(s) != 0) {
		   tS[nt++] = s;
		   return;
	   }

	   double r;
	   normat.copy(matrix);
	   double nort;
	   for (int j = 0; j < 3; j++) {
		   r = 0;
		   for (int i = 0; i < 3; i++) {
			   nort = normat.get(i, j);
			   r += nort * nort;
		   }
		   for (int i = 0; i < 3; i++)
			   normat.set(i, j, normat.get(i, j) / r);
	   }

	   transparency = transparencyOf(s);
	   
	   int m = s.getMeshRows();
	   if (m >= 0)
		  s.computeMeshNormals();
	   //s.modified = false;

	    //get colors
		double[] dcolor = new double[4];
    
    s.material.getAmbient(dcolor);
		float[] color = new float[4];
		color[0] = (float)dcolor[0];
    color[1] = (float)dcolor[1];
		color[2] = (float)dcolor[2];	
    color[3] = 1;


    s.material.getDiffuse(dcolor);
		float[] diffcolor = new float[4];
		diffcolor[0] = (float)dcolor[0];
		diffcolor[1] = (float)dcolor[1];
		diffcolor[2] = (float)dcolor[2];
		diffcolor[3] = 1;
    
		s.material.getSpecular(dcolor);
		float[] scolor = new float[4];
		scolor[0] = (float)dcolor[0];
		scolor[1] = (float)dcolor[1];
		scolor[2] = (float)dcolor[2];
    scolor[3] = 1;
    
    float shininess = (float)dcolor[3] * 4;
		
		Material mat = s.material;
		color[3] = 1-(float)transparency;
		
		//check for double sidedness
		if (s.isDoubleSided()) {
			gl.glDisable(GL.GL_CULL_FACE);
		} else {
			gl.glEnable(GL.GL_CULL_FACE);
		}
		
		//check for transparancy
		if (color[3]!=1) 
    {
			gl.glDepthMask(false);
      
			// Enable transparency:
			gl.glEnable (GL.GL_BLEND);
			gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); 
      
			//This is the safer function (but we would have to sort)
			//This blend function renders the same independently of the
			//order in which the transparent polygons are drawn.
			//gl.glBlendFunc (GL.GL_SRC_ALPHA, GL.GL_ONE);
		}
		else
    {
			 //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			 //This is the default rendering function
			 gl.glDepthMask(true);
			 gl.glDisable(GL.GL_BLEND);
	  }
	   
	   gl.glShadeModel(GL.GL_SMOOTH);
	   
	   //check for texture
	   if (s.material.hasTexture()) {
		   gl.glEnable(GL.GL_TEXTURE_2D);
		   gl.glBindTexture(GL.GL_TEXTURE_2D, s.material.getTexture().textname);	   	   
	   }

	   //settng up color of the geometry
	   gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
	   gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, diffcolor);
	   gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, color);
	   gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, scolor);
     gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, shininess);


	   
	   //loading name for selection
	   int name = s.getglname();
	   if (name<baseGeometry.size())
		   baseGeometry.set(name-1,s);
	   else 
	   	   baseGeometry.add(s);
	   gl.glLoadName(name);	
	   
	   //pushing in the transformation matrix of the geometry
	   //this is one of the slow part of this implementation
	   //we are calculating the matrix in software, it would be
	   //faster if we could calculate in hardware
	   gl.glPushMatrix();
	   gl.glMatrixMode(GL.GL_MODELVIEW);

	   //note, ken's matrix class is saved in column order
	   //opengl expects row order
	   gl.glMultMatrixd(matrix.getR());

	   drawVertices(s);

	   gl.glPopMatrix();
	   gl.glDepthMask(true);
	   if (s.material.hasTexture()) {
	   		gl.glDisable(GL.GL_TEXTURE_2D);
	   }
	   count1++;
	}

	/**
	 * Pushes triangles to the hardware using vertex array
	 * @param s Geometry to render
	 */
	protected void drawVertices(Geometry s) 
  {
		if (s.verticeBuffer==null) {
			//creating the buffer to send to the array calls
			//length = vertices.length for the number of vertices, 3 are the number of coordinates (x,y,z), 4 is size of float
			//order has to be native order
			FloatBuffer vertices = ByteBuffer.allocateDirect(s.vertices.length*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
			FloatBuffer normals = ByteBuffer.allocateDirect(s.vertices.length*3*4).order(ByteOrder.nativeOrder()).asFloatBuffer();		
			FloatBuffer texs = null;
			float uclamp = 1;
			float vclamp = 1;
      
			//if we need to create array for texture if we are actually using one
			if (s.material.hasTexture()) {
				texs = ByteBuffer.allocateDirect(s.vertices.length*2*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
				uclamp = (float)s.material.getTexture().uclamp;
				vclamp = (float)s.material.getTexture().vclamp;
			}
			
			// inserting the data into the arrays
			// note that the buffer is tightly packed, so
			// the stride has to be 0, 
			// question why would we not tightly pack the array?
			for (int i=0; i<s.vertices.length; i++) {
				vertices.put((float)s.vertices[i][0]);
				normals.put((float)s.vertices[i][3]);
				vertices.put((float)s.vertices[i][1]);
				normals.put((float)s.vertices[i][4]);
				vertices.put((float)s.vertices[i][2]);
				normals.put((float)s.vertices[i][5]);

				if (s.material.hasTexture()) {
					texs.put((float)s.vertices[i][6]*uclamp);
					texs.put((float)(1-(1-s.vertices[i][7])*vclamp));
				}
			}
			
			//creating the array describing the arrays.
			int e=0;
      
			// figuring out how big the array has to be - need to do this
			// because each face could have a different number of triangles
			for (int i=0; i<s.faces.length; i++)
				e+= 3*(s.faces[i].length-2);
			
			//allocating the buffer
			s.facesBuffer = new int[e];
			
			//inserting the data
			e=0;
			for (int j = 0; j < s.faces.length; j++) {
				 int f[] = s.faces[j];
				 if (f != null)
					 for (int k = 1; k < f.length - 1; k++) {
					 	s.facesBuffer[e] = f[0];
					 	e++;
					 	s.facesBuffer[e]= f[k];
					 	e++;
					 	s.facesBuffer[e] = f[k+1];
					 	e++;
					 }					 
			}
			s.verticeBuffer = vertices;
			s.normalBuffer = normals;
			s.textureBuffer = texs;
		}
		
		FloatBuffer vertices = (FloatBuffer) s.verticeBuffer;
		FloatBuffer normals = (FloatBuffer) s.normalBuffer;
		FloatBuffer texs = (FloatBuffer) s.textureBuffer;
		
		//if the geometry is animated, we repopulate the arrays (only vertex 
		//and normals) - we might repopulate once too many the first frame...
		if (s.animated) {
			vertices.clear();
			normals.clear();
			
			for (int i=0; i<s.vertices.length; i++) {
				vertices.put((float)s.vertices[i][0]);
				normals.put((float)s.vertices[i][3]);
				vertices.put((float)s.vertices[i][1]);
				normals.put((float)s.vertices[i][4]);
				vertices.put((float)s.vertices[i][2]);
				normals.put((float)s.vertices[i][5]);				
			}
		}
		
		// enabling the client state we need to specify which arrays we are using
		// in our case vertex array, normal array and possibly texture coord array
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		if (s.material.hasTexture()) 
			gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		
		//defining the arrays, the arrays are tightly packed, stride = 0
		gl.glVertexPointer(3,GL.GL_FLOAT,0,vertices);
		gl.glNormalPointer(GL.GL_FLOAT,0,normals);
		if (s.material.hasTexture()) 
			gl.glTexCoordPointer(2, GL.GL_FLOAT,0, texs);
		
		
		//drawing the triangles :)
		gl.glDrawElements(GL.GL_TRIANGLES, s.facesBuffer.length, GL.GL_UNSIGNED_INT, s.facesBuffer);

		//errors
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR && printError) {
				System.out.println("Error: "+glu.gluErrorString(error));
		}
		
		//disabling the client state
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);
		if (s.material.hasTexture()) 
			gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
	}
	
	/**
	 * manually pushing each triangles of a geometry to the hardware
	 * I am not using this method right now as it is slow than drawVertices,
	 * but I have not done empirical tests yet.  Possible later...
	 * @param s geometry to be drawn
	 */
	protected void drawTriangles(Geometry s) {
		for (int j = 0; j < s.faces.length; j++) {
			 int f[] = s.faces[j];
			 if (f != null)
				 for (int k = 1; k < f.length - 1; k++)
					 fillAndClipTriangle(s, f[0], f[k], f[k + 1]);
		}
		
	}
	
	/**
	 * push one triangle to the hardware,
	 * opengl will take care of the clipping
	 */
	protected void fillAndClipTriangle(Geometry s, int iA, int iB, int iC) {
		//get the coordinates for each vertex
		double []v0 = s.vertices[iA];
		double []v1 = s.vertices[iB];
		double []v2 = s.vertices[iC];		
		
		//pushing one triangle
		gl.glBegin(GL.GL_TRIANGLES);
			double uclamp = 1;
			double vclamp = 1;
			//getting the uclamp and vclamp if we are texturing
			if (s.material.hasTexture()) {
				uclamp = s.material.getTexture().uclamp;
				vclamp = s.material.getTexture().vclamp;
			}
			
			//for first vertex get u and v coordinates
			if (s.material.hasTexture()) {
				gl.glTexCoord2d((v0[6]*uclamp),(1-(1-v0[7])*vclamp));
			}
			//for first vertex get normal
			gl.glNormal3d(v0[3],v0[4],v0[5]);
			//for first vertex get point
			gl.glVertex3d(v0[0],v0[1],v0[2]);

			//second vertex
			if (s.material.hasTexture()) {
				gl.glTexCoord2d((uclamp*v1[6]),(1-vclamp*(1-v1[7])));	
			}
			gl.glNormal3d(v1[3],v1[4],v1[5]);
			gl.glVertex3d(v1[0],v1[1],v1[2]);

			//third vertex
			if (s.material.hasTexture()) {
				gl.glTexCoord2d((uclamp*v2[6]),(1-vclamp*(1-v2[7])));	
			}			
			gl.glNormal3d(v2[3],v2[4],v2[5]);
			gl.glVertex3d(v2[0],v2[1],v2[2]);
		gl.glEnd();
	}
		
	protected void transformVertex(Matrix m, double vec[], int i) {
	   for (int j=0; j<vec.length; j++)
	   	t1[i][j] = vec[j];
	}

	/**
	 * Overloads the Renderer method to get a geometry, in our case
   * this has to be called in the display thread, but our event 
   * handler system takes care of that.
	 */
	public synchronized Geometry getGeometry(int x, int y) {
		//The Size Of The Viewport. [0] Is <x>, [1] Is <y>, [2] Is <length>, [3] Is <width>
		IntBuffer  buffer = BufferUtils.newIntBuffer(512);
		int   hits;		
		int viewport[] = new int[4];
		
		// This Sets The Array <viewport> To The Size
    // And Location Of The Screen Relative To The Window
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		gl.glSelectBuffer(512, buffer);
    
		// Puts OpenGL In Selection Mode. Nothing Will Be Drawn. 
    // Object ID's and Extents Are Stored In The Buffer.
		gl.glRenderMode(GL.GL_SELECT);
		gl.glInitNames();   // Initializes The Name Stack
		gl.glPushName(0);		
		gl.glMatrixMode(GL.GL_PROJECTION);             // Selects The Projection Matrix
		gl.glPushMatrix();                             // Push The Projection Matrix
		gl.glLoadIdentity();                           // Resets The Matrix

		// This Creates A Matrix That Will Zoom Up To A Small 
    // Portion Of The Screen, Where The Mouse Is.
		glu.gluPickMatrix(x, viewport[3]-y, 1.0f, 1.0f, viewport);
		
    //		Apply The Perspective Matrix
		glu.gluPerspective(Math.toDegrees(FOV), (float)(viewport[2]-viewport[0])
      / (float) (viewport[3]-viewport[1]), 0.1f, 100.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);           // Select The Modelview Matrix
		render();                               // Render Targets To Selection Buffer
		gl.glMatrixMode(GL.GL_PROJECTION);          // Select The Projection Matrix
		gl.glPopMatrix();                        // Pop The Projection Matrix
		gl.glMatrixMode(GL.GL_MODELVIEW);           // Select The Modelview Matrix
		hits=gl.glRenderMode(GL.GL_RENDER);         // Switch To Render Mode, Find Out How Many		
		if (hits > 0)                              // If There Were More Than 0 Hits
		{
			int  choose = buffer.get(3);                // Make Our Selection The First Object
			int  depth = buffer.get(1);      	
			for (int loop = 1; loop < hits; loop++)   // Loop Through All The Detected Hits							
			{
				// If This Object Is Closer To Us Than The One We Have Selected
				if (buffer.get(loop*4+1) < depth) {
					choose = buffer.get(loop*4+3);       // Select The Closer Object
					depth = buffer.get(loop*4+1);        // Store How Far Away It Is
				}       
			}
			if (choose <1) return null;
			Geometry s = (Geometry)baseGeometry.get(choose-1);
			return s;			

		 }		 		 
	   return null;
	}

	/**
	 * get the coordinates of a point in world coordinates and put them into xyz
	 * if we did not hit anything, returns false
	 * this method also has to be called during the display thread.  Our event system
	 * handles this.
	 * Because of floating point error due to the way the points are saved in hardware,
	 * the result may not be totally correct (ie, if we expect z to be 0, we will get z to
	 * be close to 0 within an error rate which I have not calculated yet).
	 * Also this works currently with the default software renderer camera calculation
	 * It should not work with when you are doing user camera movement (may actually 
	 * just need to remove one line).
	 */
	public boolean getPoint(int x, int y, double[]xyz) 
 {
		float [] dd = new float[1];

		int viewport[] = new int[4];
		
		// This Sets The Array <viewport> To The Size And Location Of The Screen Relative To The Window
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		
		int dy = viewport[3] - y;
		//Read the actual depth component of the point
		gl.glReadPixels(x,dy,1,1,GL.GL_DEPTH_COMPONENT, GL.GL_FLOAT, dd);
		
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR && printError) {
				System.out.println("Error: "+glu.gluErrorString(error));
		}
		
		//we didn't hit anything
		if (dd[0] == 1) return false;

		//get projection matrice
		double projmat[] = new double[16];
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmat);

		double []wx = new double[1];
		double []wy = new double[1];
		double []wz = new double[1];
		
		// it is still wrong (ie error rate)
		// mycam is the camera array which 
    // is set when computeCamera is called
		glu.gluUnProject(x,dy,dd[0],mycam,projmat,viewport,wx,wy,wz);
		
		Matrix invert =  new Matrix();
    
		//now we have to invert depending on the rotation of the camera
		//may not need to do if we are doing user camera movement.
		invert.invert(camera);
		double []w = new double[4];
		super.xf(invert,wx[0],wy[0],wz[0],1,w);
    
		for (int i=0; i<xyz.length;i++)
			xyz[i] = w[i];
				
		return true;
	}
  
}// end 

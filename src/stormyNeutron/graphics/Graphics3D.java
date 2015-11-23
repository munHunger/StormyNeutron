package stormyNeutron.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import stormyNeutron.graphics.utilities.AnimationEvent;
import stormyNeutron.graphics.utilities.AnimationState;
import stormyNeutron.graphics.utilities.AnimationEventController;
import stormyNeutron.graphics.utilities.Camera;
import stormyNeutron.graphics.utilities.Face;
import stormyNeutron.graphics.utilities.Model;
import stormyNeutron.graphics.utilities.ModelPart;
import stormyNeutron.graphics.utilities.OBJLoader;
import stormyNeutron.util.Globals;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * <img src="http://www.geekend.fr/wp-content/uploads/2012/02/Lwjgl_logo.jpg" style="width:30%"><br />
 * Creates a window in which the world will be displayed in breathtaking 3D<br />
 * Uses LWJGL to create all graphics.
 * @author munhunger
 */
public class Graphics3D implements Runnable{
	private Camera camera;
	private int modelDisplayList;
	private HashMap<String, Integer> models = new HashMap<String, Integer>();
	private float size = 3.5f;

	private boolean ready = false;
	private RenderQueue renderQueue = new RenderQueue();

	private AnimationEventController animationEventController = new AnimationEventController(40); //24 FPS
	
	public void setRenderQueue(RenderQueue rq)
	{
		this.renderQueue = rq;
	}
	
	/**
	 * This is all that is needed.<br />
	 * Everything is dependant on Globals, so make sure to setup Globals before creating Graphics3D object or it will not work.<br />
	 * Press F to toggle fullscreen <br />
	 * Press escape to exit the application.<br />
	 * Note: Only use escape to exit!
	 */
	public Graphics3D(){
	}

	/**
	 * Function to check if the Graphics thread has finished setting up initial variables 
	 * @return true if thread is done setting up variables
	 */
	public boolean ready()
	{
		return ready;
	}
	public void run()
	{
		setupDisplay();
		setupCamera();
		setupStates();
		setupLighting();
		glMatrixMode(GL_MODELVIEW);
		ready = true;
		long lastTime = 0;
		updateLight(GL_LIGHT1, new Vector3f(0.0f,0.0f,32.0f), new Vector3f(0.9f, 0.75f, 0.75f));
		while(!Display.isCloseRequested()){
			processInput();
			long time = System.currentTimeMillis();
			glClearColor(0.5f, 0.8f, 1.0f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glPushAttrib(GL_TRANSFORM_BIT);
			glPushMatrix();
			glLoadIdentity();
			camera.applyPerspective();
			camera.processInput(lastTime*0.05f);
			camera.applyTranslations();
			//animationEventController.step();
			render();

			glPopAttrib();

			lastTime = System.currentTimeMillis() - time;
			glPopMatrix();
			Display.update();
		}
	}

	/**
	 * Handles keyboard input related to the main graphic part.
	 * so, key 'f' to toggle fullscreen, and escape to exit the program
	 * tab for settings and statistics
	 * '1' and '2' for spawning sheep and wolves
	 */
	private void processInput() {
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			try {
				Display.setFullscreen(!Display.isFullscreen());
				Thread.sleep(100);
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			Display.destroy();
			System.exit(0);
		}
	}

	/**
	 * Main render function.
	 * This renders animations, setups lights and calls other sub functions to render out everything to the screen.
	 */
	private void render() {
		Vector3f center = new Vector3f(0f, 0f, 0f);
		glTranslatef(center.x, center.y, center.z); //Moves the world to keep the worldCenter at center point

		float worldSunIntensity = 0.5f;
		Vector3f sunPosition = new Vector3f(Globals.width/2*size, Globals.height/2*size, 2f);
		Vector3f.add(sunPosition, (Vector3f)camera.getPosition().negate(), sunPosition);
		updateLight(GL_LIGHT0, sunPosition, new Vector3f(0.15f+worldSunIntensity, worldSunIntensity, worldSunIntensity-0.2f));

		for(GraphicObject go : renderQueue.getModels())
			renderModel(go.getModel(), go.getPosition(), go.getRotation(), go.getScale());
		
		for(AnimationEvent animEvent : animationEventController.getEvents()){
			AnimationState currentState = animEvent.getStateSum();
			if(currentState != null){
				renderModel(currentState.model, currentState.position, currentState.rotation, currentState.scale);
			}
		}
	}

	/**
	 * Updates the position and color of a light
	 * @param light the light to update, this should be GL_LIGHTx, where x=0-9
	 * @param position a vector that points to the position of the light after movement
	 * @param color a vector that holds color information of the light
	 */
	private void updateLight(int light, Vector3f position, Vector3f color) {
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		ByteBuffer temp = ByteBuffer.allocateDirect(16);
		temp.order(ByteOrder.nativeOrder());
		glLight(light, GL_POSITION, (FloatBuffer)temp.asFloatBuffer().put(new float[]{position.x, position.y, position.z, 1.0f}).flip());
		glLight(light, GL_DIFFUSE, (FloatBuffer)temp.asFloatBuffer().put(new float[]{color.x, color.y, color.z, 1.0f}).flip());
		glPopMatrix();
	}

	private HashMap<Integer, FloatBuffer> lightScale = new HashMap<>();
	/**
	 * Scales the light intensity
	 * @param lights all lights to be scaled each of these integers should be GL_LIGHTx where x = 0-9
	 * @param scale the amount to scale
	 */
	private void scaleLights(int[] lights, float scale) {
		glMatrixMode(GL_MODELVIEW);
		glPushMatrix();
		for(int l = 0; l < lights.length; l++){
			if(!lightScale.containsKey(l)){
				ByteBuffer temp = ByteBuffer.allocateDirect(16);
				temp.order(ByteOrder.nativeOrder());
				FloatBuffer fBuffer = temp.asFloatBuffer();
				lightScale.put(l, fBuffer);
				glGetLight(lights[l], GL_DIFFUSE, fBuffer);
			}
			FloatBuffer fBuffer = lightScale.get(l);
			for(int i = 0; i < fBuffer.capacity(); i++){
				float f = fBuffer.get(i);
				f *= scale;
				fBuffer.put(i, f);
			}
			glLight(lights[l], GL_DIFFUSE, fBuffer);
		}
		glPopMatrix();
	}
	
	/**
	 * Renders a model that has the key modelName.
	 * If such a model does not exist, it will try to load one.
	 * @param modelName The name of the model. there should be an .obj file in res/ that has the name "res/modelName.obj".
	 * @param position The position of where to render the model
	 * @param rotation The rotation of the model.
	 * @param size The scale of the model. Do note that having any part of this vector set to 0 will "implode" the world.
	 */
	private void renderModel(String modelName, Vector3f position, Vector3f rotation, Vector3f size) {
		if(models.containsKey(modelName)){
			glTranslatef(position.x, position.y, position.z);
			glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
			glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
			glRotatef(rotation.z, 0.0f, 0.0f, 1.0f);
			float lightScale = Math.min(size.x, Math.min(size.y, size.z));
			if(lightScale != 1.0f)
				scaleLights(new int[]{GL_LIGHT0, GL_LIGHT1}, lightScale);
			glScalef(size.x, size.y, size.z);
			glCallList(models.get(modelName));
			glScalef(1.0f/size.x, 1.0f/size.y, 1.0f/size.z);
			if(lightScale != 1.0f)
				scaleLights(new int[]{GL_LIGHT0, GL_LIGHT1}, 1.0f/lightScale);
			glRotatef(-rotation.z, 0.0f, 0.0f, 1.0f);
			glRotatef(-rotation.y, 0.0f, 1.0f, 0.0f);
			glRotatef(-rotation.x, 1.0f, 0.0f, 0.0f);
			glTranslatef(-position.x, -position.y, -position.z);
		}
		else
		{
			Integer newModel = setupModelList("res/" + modelName + ".obj");
			models.put(modelName, newModel);
			renderModel(modelName, position, rotation, size);
		}
	}

	/**
	 * Loads a .obj file into a Model object and then converts the Model to a list that is renderable using openGL.
	 * @param modelName The name of the model. there should be an .obj file in res/ that has the name "res/modelName.obj".
	 * @return the integer handle to the openGL compiled list
	 * @see OBJLoader
	 */
	private int setupModelList(String modelName) {
		modelDisplayList = glGenLists(1);
		glNewList(modelDisplayList, GL_COMPILE);
		{
			try {
				Model m;
				m = OBJLoader.loadModel(new File(modelName));
				for(ModelPart modelPart : m.getModelParts()){
					Vector3f color = modelPart.getColor();
					glColor3f(color.x, color.y, color.z);
					glBegin(GL_TRIANGLES);
					for (Face face : modelPart.getFaces()) {
						Vector3f n1 = m.getNormals().get((int)(face.getNormals().x - 1));
						glNormal3f(n1.x, n1.y, n1.z);
						Vector3f v1 = m.getVerticies().get((int)(face.getVerticies().x - 1));
						glVertex3f(v1.x, v1.y, v1.z);

						Vector3f n2 = m.getNormals().get((int)(face.getNormals().y - 1));
						glNormal3f(n2.x, n2.y, n2.z);
						Vector3f v2 = m.getVerticies().get((int)(face.getVerticies().y - 1));
						glVertex3f(v2.x, v2.y, v2.z);

						Vector3f n3 = m.getNormals().get((int)(face.getNormals().z - 1));
						glNormal3f(n3.x, n3.y, n3.z);
						Vector3f v3 = m.getVerticies().get((int)(face.getVerticies().z - 1));
						glVertex3f(v3.x, v3.y, v3.z);
					}
					glEnd();
				}
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		glEndList();
		return modelDisplayList;
	}

	/**
	 * Initial setup of all lighting.
	 * This will setup ambient light.
	 * start a few light sources and setup their color, position, cutoff and attenuation.
	 * it will also enable the light sources
	 */
	private void setupLighting() {
		ByteBuffer temp = ByteBuffer.allocateDirect(16);
		temp.order(ByteOrder.nativeOrder());
		glLightModel(GL_LIGHT_MODEL_AMBIENT, (FloatBuffer)temp.asFloatBuffer().put(new float[]{0.0f, 0.0f, 0.0f, 1.0f}).flip());
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer)temp.asFloatBuffer().put(new float[]{Globals.width/2*4.0f, Globals.height/2*4.0f, 0.0f, 1.0f}).flip());
		glLight(GL_LIGHT0, GL_DIFFUSE, (FloatBuffer)temp.asFloatBuffer().put(new float[]{1.0f, 1.0f, 1.0f, 1.0f}).flip());
		glLight(GL_LIGHT0, GL_SPOT_DIRECTION, (FloatBuffer)temp.asFloatBuffer().put(new float[]{0.0f, 0.0f, 0.0f, 1.0f}).flip());
		glLight(GL_LIGHT0, GL_SPOT_EXPONENT, (FloatBuffer)temp.asFloatBuffer().put(new float[]{0.0f, 0.0f, 0.0f, 1.0f}).flip());
		glLight(GL_LIGHT1, GL_DIFFUSE, (FloatBuffer)temp.asFloatBuffer().put(new float[]{0.0f, 0.0f, 0.0f, 0.0f}).flip());
		glLight(GL_LIGHT1, GL_QUADRATIC_ATTENUATION, (FloatBuffer)temp.asFloatBuffer().put(new float[]{0.000001f, 0.000001f, 0.000001f, 1.0f}).flip());
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_LIGHT1);
		glEnable(GL_COLOR_MATERIAL);
		glColorMaterial(GL_FRONT, GL_DIFFUSE);
	}

	/**
	 * Sets up global openGL states.
	 * Mostly stuff like enable GL_DEPTH_TEST
	 */
	private void setupStates() {
		glEnable(GL_DEPTH_TEST);
		glShadeModel(GL_SMOOTH); //should be set to smooth by default but just in case.
	}

	/**
	 * Sets up a camera with perspective.
	 */
	private void setupCamera() {
		camera = new Camera(new Vector3f(0.0f, 0.0f, 32.0f), new Vector3f(-45.0f, 0.0f, 0.0f), 0.01f, 1000.0f);
		camera.applyPerspective();
		camera.lockRotation();
	}

	/**
	 * Fetches the camera
	 * @return The camera object used for rendering
	 */
	public Camera getCamera()
	{
		return camera;
	}
	
	/**
	 * Opens the window where everything will be contained.
	 */
	private void setupDisplay() {
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			for(int i = 0; i < modes.length; i++){
				if(modes[i].getWidth() == Globals.screenWidth && modes[i].getHeight() == Globals.screenHeight && modes[i].isFullscreenCapable())
					Display.setDisplayMode(modes[i]);
			}

			Display.setTitle("Stormy Neutron");

			
			ByteBuffer[] iconList = new ByteBuffer[2];
			iconList[0] = loadIcon("res/ICON32.png");
			iconList[1] = loadIcon("res/ICON32.png");
			Display.setIcon(iconList);

			Display.create();
		} catch (LWJGLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * loads a taskbar/window icon to be used for the display.
	 * @param filename The location of the file to load
	 * @return ByteBuffer of the image
	 * @throws IOException
	 */
	private ByteBuffer loadIcon(String filename) throws IOException {
		BufferedImage image = ImageIO.read(new File(filename)); // load image
		// convert image to byte array
		byte[] buffer = new byte[image.getWidth() * image.getHeight() * 4];
		int counter = 0;
		for (int x = 0; x < image.getHeight(); x++)
			for (int y = 0; y < image.getWidth(); y++)
			{
				int colorSpace = image.getRGB(y, x);
				buffer[counter + 0] = (byte) ((colorSpace << 8) >> 24);
				buffer[counter + 1] = (byte) ((colorSpace << 16) >> 24);
				buffer[counter + 2] = (byte) ((colorSpace << 24) >> 24);
				buffer[counter + 3] = (byte) (colorSpace >> 24);
				counter += 4;
			}
		return ByteBuffer.wrap(buffer);
	}

}

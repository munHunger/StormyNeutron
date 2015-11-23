package stormyNeutron.graphics.utilities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;


/**
 * <img src="http://i.imgur.com/c9c6GYP.png" style="width:30%"><br />
 * Represents 3D object with normals and triangulated faces.
 * The model is split up in different subModel parts each with it's own color
 * @author OSM Group 5 - DollyWood project
 * @version 1.1
 * @see ModelPart
 */
public class Model {
	private ArrayList<ModelPart> modelParts = new ArrayList<ModelPart>();
	
	/**
	 * Adds a vertex to the currently active ModelPart
	 * @param vertex A vertex to add.
	 */
	public void addVertex(Vector3f vertex){
		if(modelParts.isEmpty())
			newModelPart();
		modelParts.get(modelParts.size()-1).getVerticies().add(vertex);
	}

	/**
	 * Adds a normal to the currently active ModelPart
	 * @param normal A normal to add.
	 */
	public void addNormal(Vector3f normal){
		if(modelParts.isEmpty())
			newModelPart();
		modelParts.get(modelParts.size()-1).getNormals().add(normal);
	}

	/**
	 * Adds a face to the currently active ModelPart
	 * @param face A face to add.
	 */
	public void addFace(Face face){
		if(modelParts.isEmpty())
			newModelPart();
		modelParts.get(modelParts.size()-1).getFaces().add(face);
	}
	
	/**
	 * Creates a new modelPart with default colors 1.5f,0.5f,0.5f
	 */
	private void newModelPart() {
		modelParts.add(new ModelPart(new Vector3f(1.5f, 0.5f, 0.5f)));
	}
	
	/**
	 * Creates a new ModelPart. The newly created part will become the models active part.
	 * Note that you cannot change active part in any way other than creating new parts.
	 * @param color The color of the ModelPart. Note that each part can only have a single monotone color. No vertex painting
	 */
	public void newModelPart(Vector3f color) {
		modelParts.add(new ModelPart(color));
	}
	
	/**
	 * @return A list of all the ModelParts contained within the model.
	 */
	public ArrayList<ModelPart> getModelParts(){
		return modelParts;
	}
	
	/**
	 * Verticies getter, gets verticies from all ModelParts.
	 * @return list of verticies
	 */
	public ArrayList<Vector3f> getVerticies() {
		ArrayList<Vector3f> verts = new ArrayList<Vector3f>();
		for(ModelPart p : modelParts)
			verts.addAll(p.getVerticies());
		return verts;
	}

	/**
	 * Normal getter, gets normals from all ModelParts.
	 * @return list of normals.
	 */
	public ArrayList<Vector3f> getNormals() {
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		for(ModelPart p : modelParts)
			normals.addAll(p.getNormals());
		return normals;
	}

	/**
	 * Face getter, gets faces from all ModelParts.
	 * @return list of faces.
	 * @see Face
	 */
	public ArrayList<Face> getFaces() {
		ArrayList<Face> faces = new ArrayList<Face>();
		for(ModelPart p : modelParts)
			faces.addAll(p.getFaces());
		return faces;
	}

}

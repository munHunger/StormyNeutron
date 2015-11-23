package stormyNeutron.graphics.utilities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;



/**
 * <img src="http://i.imgur.com/kyw3KUi.png" style="width:30%"><br />
 * Part of a bigger Model scope.<br />
 * Each model consists of one or more ModelParts
 * @see Model
 * @author OSM Group 5 - DollyWood project
 * @version 1.0
 */
public class ModelPart {
	private ArrayList<Vector3f> verticies = new ArrayList<>();
	private ArrayList<Vector3f> normals = new ArrayList<>();
	private ArrayList<Face> faces = new ArrayList<>();
	private Vector3f color;
	
	/**
	 * @param color The color of the newly created ModelPart
	 */
	public ModelPart(Vector3f color){
		this.color = color;
	}
	
	public Vector3f getColor() {
		return color;
	}
	public void setColor(Vector3f color) {
		this.color = color;
	}
	public ArrayList<Vector3f> getNormals() {
		return normals;
	}
	public void setNormals(ArrayList<Vector3f> normals) {
		this.normals = normals;
	}
	public ArrayList<Face> getFaces() {
		return faces;
	}
	public void setFaces(ArrayList<Face> faces) {
		this.faces = faces;
	}
	public ArrayList<Vector3f> getVerticies() {
		return verticies;
	}
	public void setVerticies(ArrayList<Vector3f> verticies) {
		this.verticies = verticies;
	}
	
}

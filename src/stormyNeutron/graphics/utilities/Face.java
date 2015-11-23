package stormyNeutron.graphics.utilities;

import org.lwjgl.util.vector.Vector3f;

/**
 * 3D objects are made up by faces. Only triangular faces are represented. 
 * @see Model
 * @author OSM Group 5 - DollyWood project
 * @version 1.1
 */
public class Face {
	private Vector3f verticies; //Index for 3 verticies
	private Vector3f normals; //Index for 3 normals
	
	/**
	 * 
	 * @param verticies Index in {@link Model#getVerticies()} for three verticies.
	 * @param normals Index in {@link Model#getNormals()} for three normals.
	 */
	public Face(Vector3f verticies, Vector3f normals){
		this.setVerticies(verticies);
		this.setNormals(normals);
	}

	/**
	 * Getter for verticies
	 * @return List of verticies
	 */
	public Vector3f getVerticies() {
		return verticies;
	}

	/**
	 * Setter for verticies
	 * @param verticies New list of verticies
	 */
	public void setVerticies(Vector3f verticies) {
		this.verticies = verticies;
	}

	/**
	 * Getter for normals
	 * @return List of normals
	 */
	public Vector3f getNormals() {
		return normals;
	}

	/**
	 * Setter for normals
	 * @param normals New list of normals
	 */
	public void setNormals(Vector3f normals) {
		this.normals = normals;
	}
}

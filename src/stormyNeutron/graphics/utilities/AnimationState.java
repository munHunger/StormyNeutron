package stormyNeutron.graphics.utilities;

import org.lwjgl.util.vector.Vector3f;

/**
 * Represents a keyframe.
 * @author OSM Group 5 - DollyWood project
 * @version 1.0
 */
public class AnimationState{
	public String model;
	public Vector3f position;
	public Vector3f rotation;
	public Vector3f scale;
	public int stateType;
	public float speed;
	
	public AnimationState(String model, Vector3f position, Vector3f rotation, Vector3f scale, int stateType, float speed){
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
		this.stateType = stateType;
		this.speed = speed;
	}
	
	public AnimationState(Vector3f position, Vector3f rotation, Vector3f scale, float speed){
		this(null, position, rotation, scale, 0, speed);
	}
	public AnimationState(String model, Vector3f position, Vector3f rotation, Vector3f scale, float speed){
		this(model, position, rotation, scale, 0, speed);
	}
	public AnimationState(String model, Vector3f position, Vector3f rotation, Vector3f scale){
		this(model, position, rotation, scale, 0, 0.0f);
	}
	public AnimationState(String model, Vector3f position, float speed){
		this(model, position, new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(1.0f, 1.0f, 1.0f), 0, speed);
	}
	
	public AnimationState(){
	}
	
	/**
	 * Returns a copy of the state
	 */
	public AnimationState clone(){
		AnimationState clone = new AnimationState();
		clone.model = String.valueOf(model);
		clone.position = new Vector3f(position.x, position.y, position.z);
		clone.rotation = new Vector3f(rotation.x, rotation.y, rotation.z);
		clone.scale = new Vector3f(scale.x, scale.y, scale.z);
		clone.stateType = stateType;
		clone.speed = speed;
		return clone;
	}
}
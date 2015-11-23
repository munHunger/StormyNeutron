package stormyNeutron.graphics.utilities;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;


/**
 * Represents and entire animation with multiple keyframes.
 * @see AnimationState
 * @author OSM Group 5 - DollyWood project
 * @version 1.02
 */
public class AnimationEvent 
{
	public static final int ANIMATION_STATE_LINEAR = 0;//TBI
	public static final int ANIMATION_STATE_SMOOTH = 1;//TBI
	public String subAnimationID;
	public String superAnimationID;
	public float currentModelProgress;
	public float currentAnimationProgress;
	public ArrayList<AnimationState> modelStates = new ArrayList<AnimationState>();
	public ArrayList<AnimationState> animationStates = new ArrayList<AnimationState>();
	public ArrayList<AnimationState> cameraStates = new ArrayList<AnimationState>();
	public AnimationState currentModelState;
	public AnimationState currentAnimationState;
	public AnimationState currentCameraState;
	public int loopType;

	public AnimationEvent(String animationID) {
		this.superAnimationID = animationID;
	}

	public AnimationState getStateSum(){
		String model = currentModelState.model;
		Vector3f position = new Vector3f();
		Vector3f rotation = new Vector3f();
		Vector3f scale = new Vector3f();
		position = vectorTranslate(currentModelState.position, currentAnimationState.position, currentAnimationState.rotation);
		rotation = vectorRotate(currentModelState.rotation, currentAnimationState.rotation, currentAnimationState.rotation);
		//Vector3f.add(currentModelState.position, currentAnimationState.position, position);
		//Vector3f.add(currentModelState.rotation, currentAnimationState.rotation, rotation);
		Vector3f.add(currentModelState.scale, currentAnimationState.scale, scale);
		return new AnimationState(model, position, rotation, scale);
	}
	
	private Vector3f vectorRotate(Vector3f addVector, Vector3f baseVector, Vector3f rotation) {
		Vector3f result = new Vector3f();
		result.x = (float) (baseVector.x + addVector.x*Math.cos(Math.toRadians(rotation.y)) + addVector.y*Math.sin(Math.toRadians(rotation.z)));
		result.y = (float) (baseVector.y + addVector.y*Math.cos(Math.toRadians(rotation.z)));
		result.z = (float) (baseVector.z + addVector.z*Math.cos(Math.toRadians(rotation.x)));
		return result;
	}
	
	private Vector3f vectorTranslate(Vector3f addVector, Vector3f baseVector, Vector3f rotation) {
		Vector3f result = new Vector3f();
		result.x = (float) (baseVector.x + addVector.x*Math.cos(Math.toRadians(rotation.y))*Math.cos(Math.toRadians(rotation.z)) + addVector.z*Math.sin(Math.toRadians(rotation.y)) + addVector.y*Math.sin(Math.toRadians(rotation.z)));
		result.y = (float) (baseVector.y + addVector.y*Math.cos(Math.toRadians(rotation.x))*Math.cos(Math.toRadians(rotation.z)) + addVector.z*Math.sin(Math.toRadians(rotation.x)) + addVector.x*Math.sin(Math.toRadians(rotation.z)));
		result.z = (float) (baseVector.z + addVector.z*Math.cos(Math.toRadians(rotation.x))*Math.cos(Math.toRadians(rotation.y)) + addVector.y*Math.sin(Math.toRadians(rotation.x)) + addVector.x*Math.sin(Math.toRadians(rotation.y)));
		return result;
	}

	public void resetModelState() {
		currentModelProgress = 0.0f;
		currentModelState = modelStates.get(0).clone();
	}
	
	public void resetAnimationState() {
		currentAnimationProgress = 0.0f;
		currentAnimationState = animationStates.get(0).clone();
	}

	public int totalStates() {
		return modelStates.size() + animationStates.size() + cameraStates.size();
	}
}
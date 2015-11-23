package stormyNeutron.graphics.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

/**
 * AnimationEventController is a holder and executor of events.
 * It can hold multiple animations(or events) each with multiple keyframes(or states).
 * The controller can be run on it's own thread or step by step using step function.
 * <br />
 * It calculates the current state of the animation based on 2 states and a progress float.
 * @see AnimationEvent
 * @see AnimationState
 * @author OSM Group 5 - DollyWood project
 * @version 1.0
 */
public class AnimationEventController implements Runnable{
	
	private int tickLength;
	public ArrayList<AnimationEvent> events = new ArrayList<>();
	
	/**
	 * Sets for how long the thread should sleep between working. Only relevant if used with a thread.
	 * @param tickLength Time in ms
	 */
	public AnimationEventController(int tickLength)
	{
		this.tickLength = tickLength;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * returns all animationEvents
	 * @return
	 */
	public synchronized ArrayList<AnimationEvent> getEvents(){
		return (ArrayList<AnimationEvent>) events.clone();
	}
	
	/**
	 * Calculates the current state of the animation based on 2 states and a progress float, for each of the events.
	 */
	public synchronized void step(){
		Iterator<AnimationEvent> it = events.iterator();
		while(it.hasNext()){
			AnimationEvent e = it.next();
			if((int)e.currentModelProgress+1 == e.modelStates.size()){
				if(e.loopType == 0)
					it.remove();
				else
					e.resetModelState();
				continue;
			}
			if((int)e.currentAnimationProgress+1 == e.animationStates.size()){
				it.remove();
				continue;
			}
			else{
				updateState(e.currentModelState, e.currentModelProgress, e.modelStates);
				e.currentModelProgress += e.modelStates.get((int)e.currentModelProgress).speed;
				
				if((int)e.currentAnimationProgress+1 < e.animationStates.size()){
					updateState(e.currentAnimationState, e.currentAnimationProgress, e.animationStates);
					e.currentAnimationProgress += e.animationStates.get((int)e.currentAnimationProgress).speed;
				}
				/*if((int)e.currentAnimationProgress+1 == e.animationStates.size()){
					if(e.loopType == 1){
						e.resetAnimationState();
					}
				}*/
			}
		}
	}
	
	private void updateState(AnimationState state, float progress, ArrayList<AnimationState> stateList){
		state.position = blendVectors(stateList.get((int)progress).position, stateList.get((int)progress+1).position, progress-(int)progress);
		state.scale = blendVectors(stateList.get((int)progress).scale, stateList.get((int)progress+1).scale, progress-(int)progress);
		state.rotation = blendVectors(stateList.get((int)progress).rotation, stateList.get((int)progress+1).rotation, progress-(int)progress);
		state.model = stateList.get((int)progress).model;
	}
	
	private Vector3f blendVectors(Vector3f preVal, Vector3f postVal, float ammount){
		float x = preVal.x + (postVal.x-preVal.x)*(ammount);
		float y = preVal.y + (postVal.y-preVal.y)*(ammount);
		float z = preVal.z + (postVal.z-preVal.z)*(ammount);
		return new Vector3f(x,y,z);
	}
	
	/**
	 * Starter for thread.
	 * it stops when there are no more events to animate.
	 */
	public void run() {
		while(events.size() > 0){
			step();
			try {
				Thread.sleep(tickLength);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Loads a non-binary .ani file and returns the AnimationEvent within.
	 * <br>
	 * A .ani file consists primarily of 5 fields. and looks like the following<br />
	 * o string with model name<br />
	 * p x.x y.y z.z //Position vector<br />
	 * r x.x y.y z.z //Rotation vector<br />
	 * sc x.x y.y z.z //Scale vector<br />
	 * s x.x //Speed of the keyframe.<br />
	 * 
	 * These field should appear in this order, and can be repeated for each keyframe.<br />
	 * You can then specify if the animation should loop with the string:<br />
	 * lt LOOP
	 * @param filename Location of the .ani file
	 * @param position Position offset. This is where the animation will be centered
	 * @param rotation Rotation offset.
	 * @param scale Scale offset. Note that they should be 0.0, if a final scale of 1.0 is desired
	 * @throws FileNotFoundException
	 */
	public void loadEvent(String filename, String animationID, Vector3f position, Vector3f rotation, Vector3f scale, float speed) throws FileNotFoundException{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line = null;
		AnimationEvent event = new AnimationEvent(animationID);
		ArrayList<AnimationEvent> events = new ArrayList<>();
		ArrayList<AnimationState> animationStates = new ArrayList<>();
		event.subAnimationID = "default";
		AnimationState state = null;
		try {
			while((line = in.readLine()) != null){
				if(line.startsWith("o ") || line.startsWith("a ")){
					String[] components = line.split(" ");
					state = new AnimationState();
					state.model = components[1];
					state.position = new Vector3f();
					state.rotation = new Vector3f();
					state.scale = new Vector3f();
					if(components.length > 2 && !event.subAnimationID.equals(components[2])){
						if(event.totalStates() > 0){
							event.currentModelState = event.modelStates.get(0).clone();
							event.currentAnimationState = new AnimationState(position, rotation, scale, speed);
							events.add(event);
						}
						event = new AnimationEvent(animationID);
						event.subAnimationID = components[2];
					}
					if(components[0].equals("o"))
						event.modelStates.add(state);
					else if(components[0].equals("a")){
							animationStates.add(state);
							state.position = new Vector3f(position.x, position.y, position.z);
							state.rotation = new Vector3f(rotation.x, rotation.y, rotation.z);
							state.scale = new Vector3f(scale.x, scale.y, scale.z);
					}
				}
				else if(line.startsWith("p ")){
					String[] components = line.split(" ");
					Vector3f.add(state.position, parseVector(components, 1), state.position);
				}
				else if(line.startsWith("r ")){
					String[] components = line.split(" ");
					Vector3f.add(state.rotation, parseVector(components, 1), state.rotation);
				}
				else if(line.startsWith("sc ")){
					String[] components = line.split(" ");
					Vector3f.add(state.scale, parseVector(components, 1), state.scale);
				}
				else if(line.startsWith("s ")){
					String[] components = line.split(" ");
					state.speed = Float.parseFloat(components[1]);
				}
				else if(line.startsWith("lt ")){
					String[] components = line.split(" ");
					if(components[1].equals("LOOP"))
						event.loopType = 1;
				}
			}
			in.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		event.currentModelState = event.modelStates.get(0).clone();
		if(event.animationStates.size() > 0)
			event.currentAnimationState = event.animationStates.get(0).clone();
		else
			event.currentAnimationState = new AnimationState(position, rotation, scale, speed);
		events.add(event);
		
		for(AnimationEvent e : events)
			e.animationStates.addAll(animationStates);
		this.events.addAll(events);
	}
	
	private Vector3f parseVector(String[] components, int offset){
		float x = Float.parseFloat(components[0+offset]);
		float y = Float.parseFloat(components[1+offset]);
		float z = Float.parseFloat(components[2+offset]);
		return new Vector3f(x,y,z);
	}

	public void addAnimationState(AnimationState animationState, String animationID) {
		for(AnimationEvent e : events){
			if(e.superAnimationID.equals(animationID))
				e.animationStates.add(animationState);
		}
	}

	public void setRandomAnimationSpeed(String animationID, float min, float max) {
		float range = Math.abs(max-min)/2.0f;
		Random random = new Random();
		for(AnimationEvent e : events){
			if(e.superAnimationID.equals(animationID)){
				for(AnimationState s : e.animationStates){
					s.speed = min+(random.nextFloat()*2.0f*range+range);
				}
			}
		}
	}

	public void setRandomModelSpeed(String animationID, float min, float max) {
		float range = Math.abs(max-min)/2.0f;
		Random random = new Random();
		float randomSpeed = min+(random.nextFloat()*2.0f*range+range);
		for(AnimationEvent e : events){
			if(e.superAnimationID.equals(animationID)){
				for(AnimationState s : e.modelStates){
					s.speed = randomSpeed;
				}
			}
		}
	}

}

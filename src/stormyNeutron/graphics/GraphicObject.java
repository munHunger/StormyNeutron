package stormyNeutron.graphics;

import org.lwjgl.util.vector.Vector3f;


public class GraphicObject
{
	private Vector3f position, scale, rotation;
	private String model;
	
	public GraphicObject(float x, float y, float z, String model)
	{
		this.position = new Vector3f(x, y, z);
		this.model = model;
		this.scale = new Vector3f(1f,1f,1f);
		this.rotation = new Vector3f(0f,0f,0f);
	}

	public void setPosition(float x, float y, float z)
	{
		this.position = new Vector3f(x, y, z);
	}
	
	public void setScale(float x, float y, float z)
	{
		this.scale = new Vector3f(x, y, z);
	}

	public void setRotation(float x, float y, float z)
	{
		this.rotation = new Vector3f(x, y, z);
	}
	
	public String getModel()
	{
		return model;
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public Vector3f getScale()
	{
		return scale;
	}
	
	public Vector3f getRotation()
	{
		return rotation;
	}
}

package stormyNeutron.graphics.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.util.vector.Vector3f;


/**
 * Loads non-binary .OBJ-file
 * @author OSM Group 5 - DollyWood project
 * @version 1.1
 */
public class OBJLoader {
	
	/**
	 * Generates a model from <code> modelFile </code> 
	 * @see Model
	 * @param modelFile file that points to .OBJ-file.
	 * @return model represented by the <code> modelFile </code>.
	 * @throws FileNotFoundException
	 */
	public static Model loadModel(File modelFile) throws FileNotFoundException{
		BufferedReader in = new BufferedReader(new FileReader(modelFile));
		String line = null;
		String mtllib = "";
		Model model = new Model();
		try {
			while((line = in.readLine()) != null){
				if(line.startsWith("v ")){ //Line is a 3-part vertex
					String[] components = line.split(" ");
					float x = Float.parseFloat(components[1]);
					float y = Float.parseFloat(components[2]);
					float z = Float.parseFloat(components[3]);
					model.addVertex(new Vector3f(x,y,z));
				}
				else if(line.startsWith("vn ")){ //Line is a 3-part vertex-normal
					String[] components = line.split(" ");
					float x = Float.parseFloat(components[1]);
					float y = Float.parseFloat(components[2]);
					float z = Float.parseFloat(components[3]);
					model.addNormal(new Vector3f(x,y,z));
				}
				else if(line.startsWith("f ")) { //Line is a face, pointing to 3 verticies and 3 vertex-normals
					String[] components = line.split(" ");
					String[][] faceString = new String[][] {
							components[1].split("/"), 
							components[2].split("/"), 
							components[3].split("/")};
					Vector3f verticies = new Vector3f(Float.parseFloat(faceString[0][0]), Float.parseFloat(faceString[1][0]), Float.parseFloat(faceString[2][0]));
					Vector3f normals = new Vector3f(Float.parseFloat(faceString[0][2]), Float.parseFloat(faceString[1][2]), Float.parseFloat(faceString[2][2]));
					Face face = new Face(verticies, normals);
					model.addFace(face);
				}
				else if(line.startsWith("o ")) { //Line is a new object header
					model.newModelPart();
				}
				else if(line.startsWith("mtllib"))
					mtllib = line.split(" ")[1];
				else if(line.startsWith("usemtl "))
				{
					String parent = modelFile.getParent();
					model.getLatestPart().setColor(loadMTL(parent + "/" + mtllib, line.split(" ")[1]));
				}
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
	
	public static Vector3f loadMTL(String path, String mtlName)
	{
		try (BufferedReader in = new BufferedReader(new FileReader(path)))
		{
			String line = null;
			String[] kd = null;
			while((line = in.readLine()) != null)
			{
				if(line.startsWith("newmtl ") && line.contains(mtlName))
				{
					in.readLine();
					in.readLine();
					kd = in.readLine().split(" ");
				}
			}
			return new Vector3f(Float.parseFloat(kd[1]), Float.parseFloat(kd[2]), Float.parseFloat(kd[3]));
		}
		catch(IOException e) { e.printStackTrace(); }
		return new Vector3f(1.0f, 1.0f, 1.0f);
	}
}

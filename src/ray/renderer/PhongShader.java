package ray.renderer;

import ray.light.PointLight;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public class PhongShader implements Renderer {
    
    private double phongCoeff = 2.5;
    
    public PhongShader() { }
    
    public void setAlpha(double a) {
        phongCoeff = a;
    }
    
    @Override
    //calculate radiance along the given array
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
            int sampleIndex, Color outColor) 
    {
		IntersectionRecord iRec = new IntersectionRecord();
		Color diffuse = new Color();
		Color specular = new Color();
		
		//if it hits 
		if (scene.getFirstIntersection(iRec, ray)) 
		{
			// N = normal 
			// V = view direction
			// L = light that points toward the source
			// R = Reflection 
			Vector3 N = new Vector3(iRec.frame.w);  N.normalize();
			Vector3 V = new Vector3(ray.direction); V.normalize(); 
			Vector3 R = new Vector3();
			Vector3 L = new Vector3();

			//Todo later: find specular and diffuse and add them to outcolor 
			
			
			scene.getCamera().getRay(ray, iRec.texCoords.x, iRec.texCoords.y);

		} 
		else 
		{
			scene.getBackground().evaluate(ray.direction, outColor);
		}
	}
      
    }


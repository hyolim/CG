package ray.renderer;

import ray.material.Material;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

/**
 * A renderer that computes radiance due to emitted and directly reflected light
 * only.
 * 
 * @author cxz (at Columbia)
 */

public class DirectOnlyRenderer implements Renderer {


	/**
	 * This is the object that is responsible for computing direct illumination.
	 */
	DirectIlluminator direct = null;

	/**
	 * The default is to compute using uninformed sampling wrt. projected solid
	 * angle over the hemisphere.
	 */
	public DirectOnlyRenderer() {
		this.direct = new ProjSolidAngleIlluminator();
	}

	/**
	 * This allows the rendering algorithm to be selected from the input file by
	 * substituting an instance of a different class of DirectIlluminator.
	 * @param direct the object that will be used to compute direct illumination
	 */
	public void setDirectIlluminator(DirectIlluminator direct) {
		this.direct = direct;
	}

	public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler,
			int sampleIndex, Color outColor) 
	{
		IntersectionRecord iRec = new IntersectionRecord();
		Color emittedRadiance = new Color();
		Color directRadiance = new Color();
		Vector3 in = new Vector3();
		Vector3 out = new Vector3();
		Point2 seed = new Point2();
		
		//if it hits 
		if (scene.getFirstIntersection(iRec, ray)) {

			//1) compute the emitted light radiance from the
			//   current surface if the surface is a light surface
			emittedRadiance(iRec, ray.direction, emittedRadiance);
			outColor.set(emittedRadiance);

			//2) direct reflected radiance from other lights
			//   by calling  direct.directIllumination
			sampler.sample(1, sampleIndex, seed);
			direct.directIllumination(scene, in, out, iRec, seed, directRadiance);
			outColor.add(directRadiance);

		}
		//else just set background 
		else 
		{
			scene.getBackground().evaluate(ray.direction, outColor);
		}
	}

	/**
	 * Compute the radiance emitted by a surface.
	 * 
	 * @param iRec
	 *            Information about the surface point being shaded
	 * @param dir
	 *            The exitant direction (surface coordinates)
	 * @param outColor
	 *            The emitted radiance is written to this color
	 */
	protected void emittedRadiance(IntersectionRecord iRec, Vector3 dir,
									Color outColor) 
	{
		LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();

		//compute emitted radiance if the surface is a light surface
		if (iRec.surface.getMaterial().isEmitter()) 
		{
			lRec.set(iRec);
			lRec.emitDir.set(dir);
			iRec.surface.getMaterial().emittedRadiance(lRec, outColor);
		} else outColor.set(0);
		
	}
}
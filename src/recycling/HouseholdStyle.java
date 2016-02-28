package recycling;

import java.awt.Color;
import java.awt.Font;

import javax.media.j3d.Shape3D;

import repast.simphony.visualization.visualization3D.AppearanceFactory;
import repast.simphony.visualization.visualization3D.ShapeFactory;
import repast.simphony.visualization.visualization3D.style.Style3D;
import repast.simphony.visualization.visualization3D.style.TaggedAppearance;
import repast.simphony.visualization.visualization3D.style.TaggedBranchGroup;

public class HouseholdStyle implements Style3D<HouseholdAgent> {

	@Override
	public TaggedBranchGroup getBranchGroup(HouseholdAgent obj,
			TaggedBranchGroup taggedGroup) {
		// TODO Auto-generated method stub
		if (taggedGroup == null || taggedGroup.getTag() == null) {
			taggedGroup = new TaggedBranchGroup("DEFAULT");
			Shape3D sphere = ShapeFactory.createSphere(.03f, "DEFAULT");
			sphere.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
			taggedGroup.getBranchGroup().addChild(sphere);

			return taggedGroup;
		}
		return null;
	}

	@Override
	public float[] getScale(HouseholdAgent obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float[] getRotation(HouseholdAgent obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLabel(HouseholdAgent obj, String currentLabel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getLabelColor(HouseholdAgent obj, Color currentColor) {
		// TODO Auto-generated method stub
		return Color.GREEN;
	}

	@Override
	public Font getLabelFont(HouseholdAgent obj, Font currentFont) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public repast.simphony.visualization.visualization3D.style.Style3D.LabelPosition getLabelPosition(
			HouseholdAgent obj,
			repast.simphony.visualization.visualization3D.style.Style3D.LabelPosition curentPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaggedAppearance getAppearance(HouseholdAgent obj,
			TaggedAppearance appearance, Object shapeID) {
		// TODO Auto-generated method stub
		if (appearance == null) {
			appearance = new TaggedAppearance();
		}

		if (obj.getRecyclePref() == 0)
		  AppearanceFactory.setMaterialAppearance(appearance.getAppearance(), Color.red);
		else if (obj.getRecyclePref() == 1)
		  AppearanceFactory.setMaterialAppearance(appearance.getAppearance(), Color.green
				  );
		return appearance;
	}

	@Override
	public float getLabelOffset(HouseholdAgent obj) {
		// TODO Auto-generated method stub
		return .035f;
	}
	
	
	public Color getColor(final Object obj){

			if (obj instanceof HouseholdAgent) { // depends 
				HouseholdAgent hh = (HouseholdAgent) obj;
				return new Color(hh.red, hh.green, hh.blue);
			}
		return this.getColor(obj);
	}
	

}
	
//	public Color get3DColor(HouseholdAgent agent){
//	//	Color currentColor = this.g
//		return this.getLabelColor(agent, currentColor);
//	}



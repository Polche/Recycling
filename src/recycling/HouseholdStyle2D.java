package recycling;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;


public class HouseholdStyle2D extends DefaultStyleOGL2D{
	
	public Color getColor(final Object obj){

			if (obj instanceof HouseholdAgent) { // depends 
				HouseholdAgent hh = (HouseholdAgent) obj;
				return new Color(hh.red, hh.green, hh.blue);
			}
		return super.getColor(obj);
	}
	

}

package recycling;

import java.awt.Color;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.grid.Grid;
import repast.simphony.util.ContextUtils;






import com.sun.istack.internal.logging.Logger;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class ZoneAgent {
	
	//private int familyMembers;
	private static Logger LOGGER = Logger.getLogger(ZoneAgent.class.getName(), 
			ZoneAgent.class);
	private String socialStatus; // = socio-economic status of the area
	//private String ageGroup;
	private List<LondonCitizenAgent> householdMembers;
	
	
	private int householdID;
	private Coordinate coordinates;
	private boolean recyclingStatus;
	
	private double habit;
	private double convenience; // = access to facilities
	private double normAcceptance;
	private double influenceFactor;
	
	private ZoneContext hhContext;// = super.hhContext;
	public Geography<ZoneAgent> geography;
	private MultiPolygon polygon;
	private double popDen;
	private int hholds;
	private double avhholdsz;
	
	/* @param context - the context within which this agent resides
	   @param householdID - ID of this particular household agent       	
	*/
	//HouseholdContext hhContext
	public ZoneAgent(){
		super();
	//	this.hhContext = hhContext;
		//this.popdensity = popdensity;
		this.geography = geography;
		this.hholds = hholds;
	//	this.avhholdsz = avhholdsz;
		//this.householdID = householdID;
		//this.geometry = geometry;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void step(){
		Context<ZoneContext> context = ContextUtils.getContext(this);
		geography = ((Geography<ZoneAgent>)context.getProjection(Constants.HOUSEHOLD_GEOGRAPHY));
		
		geography.moveByDisplacement(this, RandomHelper.nextDoubleFromTo(-0.0005, 0.0005), 
				RandomHelper.nextDoubleFromTo(-0.0005, 0.0005));
		
	}
	
//	public int getID(){
//		return householdID;
//	}
//	
//	public void setID(int householdID){
//		this.householdID = householdID;
//	}
	
//	public void setContext(HouseholdContext hhcontext){
//		this.hhContext = hhContext;
//	}
	
//	public double getPopDen(){
//		return popdensity;
//	}
	
//	public void setPopDen(double popDen){
//		this.popDen = popDen;		
//	}
	
	public int getHholds(){
		return hholds;
	}
	
	public void setHholds(int hholds){
		this.hholds = hholds;
	}
	
//	public double getAvhholdsz(){
//		return avhholdsz;
//	}
//	
//	public void setAvhholdsz(double avhholdsz){
//		this.avhholdsz = avhholdsz;
//	}
	
	public Geography<ZoneAgent> getGeography(){
		return geography;
	}

	public void setGeography(Geography<ZoneAgent> geography){
		this.geography = geography;
	}
	
	public Geometry getGeometry(){
		return polygon;
	}
	
	public void setGeometry(MultiPolygon polygon){
		this.polygon = polygon;
		
	}
	 // TO DO: more actions - perceive, obey, recall ?!
	
	
	
}

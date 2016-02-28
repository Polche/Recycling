package Base;

import java.util.logging.Logger;

public class Constants {

	private static Logger LOGGER = Logger.getLogger(Constants.class.getName());

	public static final String CONTEXT_ID = "recycling";
	public static final String MAIN_GEOGRAPHY_ID = "geography";

	public static final String GRID_ID = "Grid";	
	public static final String GRID_VALUE_LAYER_ID = "value layer";

	public static final String Zone_Context_ID = "ZoneContext";
	public static final String ZONE_GEOGRAPHY = "ZoneGeography";

	public static final String SOCIAL_NETWORK = "Social network";

	public static final double COMMON_INTEREST_EDGE_WEIGHT = 0.95; // CHANGE !!
	
	public static final double sociable = 0.7;
	public static final String proEnvAttitude = "concerned";
	public static final String noEnvAttude = "unconcerned";
	
	public static final double prob = 0.80; // probability of being a recycler
	public static final double neighbourInfluenceStrenght = 0.5;
	public final static int behaviourChangeStepsLimit = 300;
	
	//The threshold that has to be emt so as to adopt change in behaviour
	public final static double adoptionThreshold = 0.3;
	public final static double irrationalChance = 0.0001;

	public final static int maxInDegree = 15;
	public final static int maxOutDegree = 15;

	
}



package recycling;

import Base.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import com.vividsolutions.jts.geom.*;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.NetworkFactory;
import repast.simphony.context.space.graph.NetworkFactoryFinder;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.ContinuousSpace;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.continuous.RandomCartesianAdder;
import repast.simphony.space.gis.DefaultGeography;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import repast.simphony.space.gis.ShapefileLoader;
import repast.simphony.space.gis.SimpleAdder;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StrictBorders;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.valueLayer.GridValueLayer;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.opengis.feature.simple.SimpleFeature;

public class RecyclingContextBuilder implements ContextBuilder<HouseholdAgent>{

	private int hholds = 300;
	//Geometry geometry;
	public List<HouseholdAgent> neighbours;

	private static Logger LOGGER = Logger.getLogger(RecyclingContextBuilder.class.getName());
	private Context<HouseholdAgent> mainContext;

	public static Context<ZoneAgent> zoneContext;
	public static Geography<ZoneAgent> zoneProjection;
	private ZoneAgent zoneAgent;
	private Network<HouseholdAgent> network;
	public Grid<HouseholdAgent> grid;
	public HouseholdAgent agent;

	private void loadParameters(){
		
		Parameters parameters = RunEnvironment.getInstance().getParameters();
		Date startDate;
		try
		{
			startDate = (new SimpleDateFormat("dd/MM/yyyy")).parse((String) parameters.getValue("startDate"));
			System.out.println("RecyclingContextBuilder: The data will be set to " + startDate);
		}
		catch (ParseException e1)
		{
			// TODO Auto-generated catch block
			System.err.println("RecyclingContextBuilder: The start date parameter is in a format which cannot be parsed by this program");
			System.err.println("RecyclingContextBuilder: The data will be set to 01/01/2006 by default");
			startDate = new Date(2006, 1, 1);
			e1.printStackTrace();
		}
		
		int nearbyNeighbours = (Integer)parameters.getValue("nearbyNeighbours");
	//	String householdType = (String)parameters.getValue("householdType");
	}
	@SuppressWarnings("unchecked")
	@Override
	public Context build(Context<HouseholdAgent> context) {
		this.mainContext = context;
		context.setId(Constants.CONTEXT_ID);
		
		this.loadParameters();

		NetworkBuilder < HouseholdAgent > netBuilder = new NetworkBuilder < HouseholdAgent >
			(Constants.SOCIAL_NETWORK, context , true );
		netBuilder.buildNetwork ();

		grid = GridFactoryFinder.createGridFactory(null).createGrid
				(Constants.GRID_ID, context, new GridBuilderParameters<HouseholdAgent>(new WrapAroundBorders(), 
						new RandomGridAdder<HouseholdAgent>(), false, 80, 70 ));
		GridValueLayer valueLayer = new GridValueLayer("Value Layer", true,
				new WrapAroundBorders(), 80, 70);
		
	
		context.addValueLayer(valueLayer);		

		for(int i = 0; i<hholds; ++i){
			agent = new HouseholdAgent(context);	
			context.add(agent);
		}
		// TO DO: compute when exactly it has to stop running !!
		RunEnvironment.getInstance().endAt(20000);

		return context;
	}

	private void initialiseProbabilities(){
	}

	private void buildSocialNetwork(){

	//	NetworkFactory networkFactory = NetworkFactoryFinder.createNetworkFactory(null);
		// create a small world social network
		double beta = 0.1;
		int degree = 2;
		boolean directed = true;
		boolean symmetric = true;
		//	NetworkGenerator gen = new WattsBetaSmallWorldGenerator(beta, degree, symmetric);
		//	Network network = networkFactory.createNetwork
		//(Constants.SOCIAL_NETWORK, mainContext, gen, directed);
		//	Network < HouseholdAgent > network = ( Network < HouseholdAgent >) context .
		//			getProjection (Constants.SOCIAL_NETWORK);
		network = SmallWorldSocialNetwork.buildNetwork(mainContext, Constants.SOCIAL_NETWORK, 100, 0.5, 2);
//		for (Object thisEdge : network.getEdges())
//		{
//			((RepastEdge) thisEdge).setWeight(RandomHelper.nextDouble());
//		}	
	}

	public Network<HouseholdAgent> getSocialNetwork()
	{
		return this.network;
	}
}

//		IndexedIterable<HouseholdAgent> allAgents = (IndexedIterable<HouseholdAgent>) 
//				context.getObjects(HouseholdAgent.class);
//		HouseholdAgent randomAgent = allAgents.get((int)Math.round(Math.random() * (allAgents.size()-1)));
//		HouseholdAgent randomAgent2 = allAgents.get((int)Math.round(Math.random() * (allAgents.size()-1)));
//		RepastEdge<HouseholdAgent> edge = network.getEdge(randomAgent, randomAgent2);
//		if(!randomAgent.equals(this)){
//			network.addEdge(randomAgent, randomAgent2);
//			System.out.println("BLAAAAAAH"+network.numEdges());
//		}


//		File file = new  File("data/MSOA_2011_BFE_BARNET.shp");
//		ShapefileLoader<ZoneAgent>  loader = null; 
//		
//		
//			try {
//				zoneContext = new ZoneContext();
//				mainContext.addSubContext(zoneContext);
//				zoneProjection = GeographyFactoryFinder.createGeographyFactory(null)
//						.createGeography(Constants.ZONE_GEOGRAPHY, zoneContext, 
//						new GeographyParameters<ZoneAgent>(new SimpleAdder<ZoneAgent>()));
//				loader = new ShapefileLoader(ZoneAgent.class, file.toURL(), 
//						zoneProjection, zoneContext);
//			//	HouseholdAgent hhagent = new HouseholdAgent(householdProjection);	
//			//	householdContext.add(hhagent);
//			//	System.out.println(hhagent.getGeography());	
//
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			//List<HouseholdAgent> agents = new ArrayList<HouseholdAgent>();
//			
//			while(loader.hasNext())
//				loader.next();		
//			mainContext.addSubContext(zoneContext);
//			System.out.println(agent.getHholds());




//		}
//		try {
//			loadData(HouseholdAgent.class, ".data/Zones2.shp", geography, context);
//		} catch (MalformedURLException | FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}


/*
	@SuppressWarnings("unchecked")
	private void loadHouseholds(String filename, Context<Object> context, Geography geography){
		URL url = null;
		try {
			url = new File(filename).toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		List<SimpleFeature> features = new ArrayList<SimpleFeature>();

		// Try to load the shapefile
		SimpleFeatureIterator fiter = null;
		ShapefileDataStore store = null;
		store = new ShapefileDataStore(url);

		try {
			fiter = store.getFeatureSource().getFeatures().features();

			while(fiter.hasNext()){
				features.add(fiter.next());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			fiter.close();
			store.dispose();
		}

		// For each feature in the file
		for (SimpleFeature feature : features){
			Geometry geom = (Geometry)feature.getDefaultGeometry();
			Object agent = null;

			if (geom instanceof Polygon){
				Polygon p = (Polygon)feature.getDefaultGeometry();
				geom = (Polygon)p.getGeometryN(0);

				// Read the feature attributes and assign to the HouseholdAgent

				double density = (double)feature.getAttribute("POPDEN");
				int hholds = (int)feature.getAttribute("HHOLDS");
				double avhholdsz = (double)feature.getAttribute("AVHHOLDSZ");

				agent = new HouseholdAgent(householdContext, density, hholds, avhholdsz);
				hhCount = 10;
				for(int i = 0; i<hhCount; ++i){
					context.add(agent);
					geography.move(agent, geom);
				}
			}
			else{System.out.println("Error creating agent for  " + geom);}
		}
	}

	public static void loadData
	(String shapefile, Geography geography)throws MalformedURLException, FileNotFoundException{
		File file = new  File("data/MSOA_2011_BFE_BARNET.shp");
		ShapefileLoader<HouseholdAgent> loader = null;
		try{
			loader = new ShapefileLoader<HouseholdAgent>(HouseholdAgent.class, file.toURI().toURL(), 
					geography, householdContext);
			while(loader.hasNext()){
				loader.next();
			}
			loader.load();
		}
		catch ( IOException e)
		{
			e.printStackTrace();
		}
	}


 */




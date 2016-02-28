package recycling;

import Neighbourhoods.*;
import Base.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.parameter.Parameters;
import repast.simphony.query.space.gis.GeographyWithin;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.query.space.grid.VNQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridDimensions;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.MooreContains;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.collections.IndexedIterable;
import repast.simphony.valueLayer.GridValueLayer;

public class HouseholdAgent implements Comparable {

	public int ticksPerMonth;
	protected static String householdID;	
	public Context<HouseholdAgent> myContext;
	//private Context<?> context;
	public Network<HouseholdAgent> network;
	
	public boolean isRecycler;
	public int nearbyNeighbours;
	public double influenceFactor;
	public double neighbourInfluenceStrength;
	public String attitude;
	//public double sociable;
	public boolean easeOfAccess;
	
	
	public double goal;
	public double recyclePref;

	public int red; // assign colour based on behavior
	public int green;
	public int blue;

	public double habit;
	//public double socialStatus;
	public double awareness; // this will come into play when we introduce authority agents?
	//??
	private double adoptionThreshold;
	private double adoptionLikelihood;	

	private boolean adopting;
	private int stepsSinceGiveUp;
	private int adoptionSteps;
	private int time;


	//public List<HouseholdAgent> neighbours;
	public Grid myGrid; public Geography myGeography;	

	/**
	 * Constructor for SmallWorld social network
	 * @param householdID
	 * @param context
	 */
	public HouseholdAgent(Context<?> context){
		this.myContext = (Context<HouseholdAgent>) context;
		//this.householdID = householdID;
	}

	/**
	 * This is the "main" method that calls all the other methods 
	 * @ each clock tick  
	 */

	@ScheduledMethod(start=1, interval=1, priority = 1)
	public void step(){
		Context<HouseholdAgent> context = ContextUtils.getContext(this);
		Grid<HouseholdAgent>grid = (Grid)context.getProjection(Constants.GRID_ID);

		this.getGridLocation();
		this.getMooreNeighbourhood();
		this.createMooreSocialNetwork();
		this.createRandomSocialNetwork();

		this.setColours();
	}

	public GridPoint getGridLocation(){
		Context context = ContextUtils.getContext(this);
		Grid grid = (Grid)context.getProjection(Constants.GRID_ID);
		GridPoint myGridPoint = grid.getLocation(this);
		System.out.println("My coordinates are: " + myGridPoint.getX() + " " + myGridPoint.getY());

		return myGridPoint;
	}

	private int getMooreNeighbourhood(){

		Context<HouseholdAgent> context = ContextUtils.getContext(this);
		Grid<HouseholdAgent> grid = (Grid)context.getProjection(Constants.GRID_ID); 
		GridPoint centralPoint = grid.getLocation(this);
		GridCellNgh<HouseholdAgent> nghCreator = 
				new GridCellNgh(grid, centralPoint, HouseholdAgent.class, nearbyNeighbours, nearbyNeighbours);
		List<GridCell<HouseholdAgent>> neighbourCells = nghCreator.getNeighborhood(false);
		int mooreNghSize = neighbourCells.size();
		int gridCellSize = 0;

		List<GridCell<HouseholdAgent>> actualNeighbours = 
				new ArrayList<GridCell<HouseholdAgent>>();
		int actualNeighboursSize = actualNeighbours.size();

		for(Object obj: neighbourCells){
			if(obj instanceof GridCell){
				gridCellSize = ((GridCell) obj).size();
				if (gridCellSize>0){
					actualNeighbours.add((GridCell<HouseholdAgent>) obj);
					System.out.println("Number of actual agents: " + " " + actualNeighboursSize);
					System.out.println("Number of agents in a cell:" + " " + gridCellSize);
				}
			}
		}
		return actualNeighboursSize;


	}

	public void setSocialNetwork(Network<HouseholdAgent> network){
		this.network = network;
	}

	public void createRandomSocialNetwork(){
		this.time = (int) RepastEssentials.GetTickCount();
		//	this.timeOfDay = (this.time % this.mainContext.ticksPerDay);

		Grid<HouseholdAgent> grid = 
				(Grid<HouseholdAgent>) ContextUtils.getContext(this).getProjection(Constants.GRID_ID);
		GridPoint otherPoint = grid.getLocation(this);
		int x = otherPoint.getX();
		int y = otherPoint.getY();
		//		int nextXPoint = grid.getLocation(this).getX()+1;
		//		int nextYPoint = grid.getLocation(this).getY()+1;

		HouseholdAgent other = null;
		ArrayList<HouseholdAgent> influencedRandomPeers = new ArrayList<HouseholdAgent>();
		double sociable = 0.8;
			//if(this.time % 30 == 0){
		//  
		if(Math.random() < (sociable * 0.0001)  && network.getInDegree(this) < Constants.maxInDegree )
		{
			System.out.println("Adding a random edge");
			IndexedIterable<HouseholdAgent> allNodes = (IndexedIterable<HouseholdAgent>) myContext.getObjects(HouseholdAgent.class);
			HouseholdAgent randomAgent = allNodes.get((int)Math.round(Math.random() * (allNodes.size()-1)));
			RepastEdge<HouseholdAgent> edge = network.getEdge(randomAgent, this);
			double distance = grid.getDistance(randomAgent.getGridLocation(), this.getGridLocation());

			if(edge == null && randomAgent != this && distance<=15.0 ) //&& random.checkCanEdgeOut()
			{
				network.addEdge(randomAgent, this);
				influencedRandomPeers.add(randomAgent);
				System.out.println("I randomly attached to " + randomAgent.getGridLocation().toString());
				//System.out.println(id + ": I attached to " + other.getNeighbor().getID() + " with influence " + nd /*+ " and prob " + prob + " and points " + points);
			}
		}
	//}
}


	// Not used atm
	// Getting neighbours in a geography projection

	/*
	public List<HouseholdAgent> getNeighbours()
	{
		GeographyWithin<HouseholdAgent> neighbourhood 
		= new GeographyWithin<HouseholdAgent>(this.myGeography, this.observedRadius, this);
		this.neighbours = (List<HouseholdAgent>) neighbourhood.query();
		int neighboursCount = this.neighbours.size();
		return this.neighbours;
	}

	 */

	public void createMooreSocialNetwork(){

		Context<HouseholdAgent> context = ContextUtils.getContext(this);
		Grid<HouseholdAgent> grid = (Grid)context.getProjection(Constants.GRID_ID); 

		network = ( Network < HouseholdAgent >) context .
				getProjection (Constants.SOCIAL_NETWORK);
		//	network = SmallWorldSocialNetwork.buildNetwork(this.myContext, Constants.SOCIAL_NETWORK, 100, 0.5, 2);
		//		IndexedIterable<HouseholdAgent> allAgents = (IndexedIterable<HouseholdAgent>) 
		//				((Context<?>) context).getObjects(HouseholdAgent.class);	

		MooreQuery<HouseholdAgent> moorequery = new MooreQuery<HouseholdAgent>(grid, this, 1, 1);
		Iterator<HouseholdAgent> iterator = moorequery.query().iterator();	
		MooreContains<HouseholdAgent> mooreContains = new MooreContains<HouseholdAgent>(grid);


		List<HouseholdAgent> neighbours = new ArrayList<HouseholdAgent>();
		int neighboursCount = 0;			

		for (Object o : moorequery.query()){
			if(this.time % 30 == 0){
				if(o instanceof HouseholdAgent){
					//	if(mooreContains.isNeighbor(this, (HouseholdAgent)o, 0)==true)
					{				
						neighbours.add((HouseholdAgent) o);
						neighboursCount++;	
						network.addEdge((HouseholdAgent) o, this);
					}
				}

			}
		}
		Iterable<RepastEdge<HouseholdAgent>> edges = network.getInEdges(this);

		System.out.println("Edges "+ " " + network.numEdges());
		System.out.println(neighboursCount);

		if(neighboursCount > 0){

			System.out.println("Number of nearby agents" + " "  
					+ this.getMooreNeighbourhood());
		} else {
			System.out.println("Nearby cells are empty");
		}		

		setSocialNetwork(network);
	}

	/**
	 * Method representing observing/ interacting with neighbours
	 * over a period of time 
	 * At the end the likelihood for a household to change 
	 * their recycling habits increases if the decision criteria/ threshold is met 
	 */


	// TO DO: add scheduled delay!!
	private void calculateNeighboursInfluence(ArrayList<HouseholdAgent> neighbours, NeighbourhoodMeasures measure, Network net){
		int numInteractions = 0;
		// get social influence
		// time might have to be changed ??
		Iterator<HouseholdAgent> iterator = neighbours.iterator();
		// TO DO: will need this if statement as the social interactions do not
		//happen at each tick
		//	if(((time % (21 * this.ticksPerMonth)) == 0)){
		
		// TO DO: SET EDGES WEIGHT !!
		if(network.getDegree(this)>0){
			while(iterator.hasNext())
			{
				HouseholdAgent myNeighbours = iterator.next();
				// SET SOME MAXIMUM IN DEGREE FOR THE NEIGHBOURHOOD!!6
				if(network.getDegree(this)>1){
					RepastEdge<HouseholdAgent> myNeighbour = (RepastEdge<HouseholdAgent>) network.getInEdges(this);
					if (myNeighbour.getSource().isRecycler && this.attitude.equals(Constants.proEnvAttitude))
					{	
						// PROPER COMPUTATION HAS TO COME IN HERE !!
						// influence factor = proximity + attitude + isSociable 
						influenceFactor = influenceFactor + myNeighbour.getWeight()
								* (myNeighbour.getSource()).goal;
//						
//						if  (postive_threshold > Math.abs(other.influence - this.influence)){ //Positive Interactions
//							other.influence = other.influence + ((other.beta)*(this.influence/(num_interactions)));
//							other.num_positive_interactions++;
//							this.influence = this.influence + ((this.beta)*(other.influence/(num_interactions)));
//							num_positive_interactions++;
//						}
					}
				}
			}
			//		}
		//	interactions = influenceFactor;

			if (influenceFactor > 
			(Double) RepastEssentials.GetParameter("adoptionThreshold")){
				this.adoptionLikelihood = 1.0;}
			else{
				this.adoptionLikelihood = 0.0;
			}

		}
	}
	/** Maybe should simply be part of observedBehaviour??
	 * 
	 */

	private void rememberObservedBehaviour(){

	}

	/**
	 * Calculate probability of adoption particular change in behaviour
	 * based on p[agent in period t+1 chooses A]= (1-α)μ+ αNb 
	 * @param myNeighbourhood
	 */
	private void calculateAdoptionLikelihood(ArrayList<NeighbourhoodBasics> myNeighbourhood){

	}


	/**
	 * 
	 */
	private void calculateBehaviourChange(){

	}

	private void setRecyclePref(double preference){
		//TO DO: calculations come in here
		this.recyclePref = preference;
	}

	public double getRecyclePref(){
		return recyclePref;
	}
	private void makeDecision(){
		DecisionMaking decisions = new DecisionMaking();
	}

	public void setStartDate(){

	}

	public void setState(int state) {

	}

	// Create list with all agent's attributes
	public HashMap<String, Object> createAttributesList(){
		return null;	
	}

	private void setColours() {

		if (network.getInDegree(this)==0){ //(this.recyclePref < 1) { // red
			this.red = 0xFF;
			this.green = 0x0;
			this.blue = 0x0;

		} else if (network.getInDegree(this)>1){ //(this.recyclePref > 0 ) { // 
			this.red = 0x0;
			this.green = 0xFF; 
			this.blue = 0x0;
		} else {
		}
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		HouseholdAgent h1 = (HouseholdAgent) o;
		return this.compareTo(h1);
	}
}

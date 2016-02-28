package recycling;

import java.util.Iterator;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.graph.NetworkGenerator;
import repast.simphony.context.space.graph.WattsBetaSmallWorldGenerator;
import repast.simphony.space.graph.EdgeCreator;
import repast.simphony.space.graph.Network;
import repast.simphony.space.graph.RepastEdge;

public class SmallWorldSocialNetwork{
	
	static int nodesCount;
	//Network<HouseholdAgent> network;
	
	public static Network buildNetwork(Context<HouseholdAgent> context, String networkName, int nodesCount, double SWBeta, int degree){
		nodesCount = 330;
		
		for (int i = 0; i<nodesCount; ++i){
			context.add(new HouseholdAgent(context));
		}
		
		NetworkBuilder <HouseholdAgent> builder = new NetworkBuilder(networkName, context, true);
		// change parameters !!
		NetworkGenerator generator = new WattsBetaSmallWorldGenerator(1, 6, false);
		builder.setGenerator(generator);
		Network<HouseholdAgent> network = builder.buildNetwork(); 
		
		//EdgeCreator<RepastEdge, HouseholdAgent> edgeCreator = new EdgeCreator();
		//builder.setEdgeCreator(null);
		
		Iterable<HouseholdAgent> iterable = network.getNodes();
		Iterator<HouseholdAgent> iterator = iterable.iterator();
		
		while(iterator.hasNext()){
			iterator.next().setSocialNetwork(network);
		}
		return network;
		
	}

}

package recycling;

import java.awt.Color;

import repast.simphony.space.graph.RepastEdge;
import repast.simphony.visualization.visualization3D.style.DefaultEdgeStyle3D;

public class EdgeStyle extends DefaultEdgeStyle3D {
	public Color getColor(RepastEdge<?> edge)
	{
		RepastEdge<HouseholdAgent> e = (RepastEdge<HouseholdAgent>) edge;

		return Color.GREEN;
		
	}

}

package Neighbourhoods;

import recycling.*;

public class NeighbourhoodBasics {
	
		HouseholdAgent neighbour;
		static String householdID;	 
		double influenceFactor;
		
		public NeighbourhoodBasics(HouseholdAgent neighbour, double influenceFactor){
			this.neighbour = neighbour;
			this.influenceFactor = influenceFactor;
		}
		public HouseholdAgent getNeighbour(){
			return neighbour;
		}
		
		


}

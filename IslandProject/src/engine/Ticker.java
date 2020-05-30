package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import econ.Island;
import econ.Goods.Good;

public class Ticker {
	ArrayList<Island> islands = new ArrayList<Island>();
	
	HashMap<Good, HashMap<Island, Double>> collatedProductionCosts = new HashMap<Good, HashMap<Island, Double>>();
	
	
	public Ticker(ArrayList<Island> islands) {
		this.islands = islands;
	}
	
	public void collateProductionCosts() {
		HashMap<Good, HashMap<Island, Double>> collatedProductionCosts = new HashMap<Good, HashMap<Island, Double>>();
		for (Island island : islands) {
			HashMap<Good, Double> islandProductionCosts = island.getUpdatedProductionCosts();
			
			Iterator<Entry<Good, Double>> hmIterator = islandProductionCosts.entrySet().iterator();
			
			while(hmIterator.hasNext()) {
				Entry<Good, Double> mapElement = hmIterator.next();
				HashMap<Island, Double> specificProductionCosts = new HashMap<Island, Double>();
				if (collatedProductionCosts.containsKey(mapElement.getKey())) {
					specificProductionCosts = collatedProductionCosts.get(mapElement.getKey());
				}
				specificProductionCosts.put(island, mapElement.getValue());
				if (collatedProductionCosts.containsKey(mapElement.getKey())) {
					collatedProductionCosts.replace(mapElement.getKey(), specificProductionCosts);
				} else {
					collatedProductionCosts.put(mapElement.getKey(), specificProductionCosts);
				}
			}
		}
		
		for (Island island : islands) {
			island.updatePurchaseCosts(collatedProductionCosts);
		}
	}
	
	public void initializeProductionCosts() {
		HashMap<Good, HashMap<Island, Double>> collatedProductionCosts = new HashMap<Good, HashMap<Island, Double>>();
		for (Island island : islands) {
			HashMap<Good, Double> islandProductionCosts = island.getProductionCosts();
			
			Iterator<Entry<Good, Double>> hmIterator = islandProductionCosts.entrySet().iterator();
			
			while(hmIterator.hasNext()) {
				Entry<Good, Double> mapElement = hmIterator.next();
				HashMap<Island, Double> specificProductionCosts = new HashMap<Island, Double>();
				if (collatedProductionCosts.containsKey(mapElement.getKey())) {
					specificProductionCosts = collatedProductionCosts.get(mapElement.getKey());
				}
				specificProductionCosts.put(island, mapElement.getValue());
				if (collatedProductionCosts.containsKey(mapElement.getKey())) {
					collatedProductionCosts.replace(mapElement.getKey(), specificProductionCosts);
				} else {
					collatedProductionCosts.put(mapElement.getKey(), specificProductionCosts);
				}
			}
		}
		
		for (Island island : islands) {
			island.updatePurchaseCosts(collatedProductionCosts);
		}
	}
	
	public void tick() {
		initializeProductionCosts();
		long curTime = System.nanoTime();
		boolean loop = true;
		while (loop) {
			boolean remaining = false;
			collateProductionCosts();
			for (Island island : islands) {
				boolean purchased = island.buyBestGood();
				remaining = remaining || purchased;
				collateProductionCosts();
			}
			if (!remaining) {
				loop = false;
			}
		}
		for (Island island : islands) {
			island.rollover();
		}
		System.out.println((System.nanoTime() - curTime) / (1000000.0) + " ms for that tick");
	}
	
	public void tickTest() {
		double currentCash = 0;
		double futureCash = 0;
		for (Island island : islands) {
			currentCash += island.getCurrentCash();
			futureCash += island.getFutureCash();
			System.out.println(island.getName() + " has $" + (island.getCurrentCash() + island.getFutureCash()));
		}
		System.out.println((currentCash + futureCash));
	}
}

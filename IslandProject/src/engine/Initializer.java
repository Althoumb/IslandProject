package engine;

import java.util.ArrayList;
import java.util.HashMap;

import econ.Island;
import econ.Goods.Good;

public class Initializer {

	public static void main(String[] args) {
		ArrayList<Island> islands = new ArrayList<Island>();
		for (int i = 1; i <= 1000; i++) {
			islands.add(new Island(Integer.toString(i)));
		}
		for (Island island : islands) {
			HashMap<Good, Double> randomProductionCosts = new HashMap<Good, Double>();
			HashMap<Good, Double> randomProductionDeltas = new HashMap<Good, Double>();
			HashMap<Island, Double> randomShippingCosts = new HashMap<Island, Double>();
			
			for (Good good : Good.values()) {
				randomProductionCosts.put(good, Math.random() * 10);
				randomProductionDeltas.put(good, Math.random() * 2 + 2);
			}
			
			for (Island island2 : islands) {
				randomShippingCosts.put(island2, Math.random() * 1);
			}
			
			island.updateProductionCosts(randomProductionCosts);
			island.updateProductionDelta(randomProductionDeltas);
			island.updateShippingCosts(randomShippingCosts);
			island.updateCash(100);
		}
		
		Ticker ticker = new Ticker(islands);
		for (int i = 0; i < 100; i++) {
			ticker.tick();
			ticker.tickTest();
		}
	}

}

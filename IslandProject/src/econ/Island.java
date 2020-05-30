package econ;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import econ.Goods.Good;

public class Island {
	String name;
	HashMap<Good, Double> productionCosts = new HashMap<Good, Double>();
	HashMap<Good, Double> productionDelta = new HashMap<Good, Double>();
	HashMap<Island, Double> shippingCosts = new HashMap<Island, Double>();
	HashMap<Good, HashMap<Island, Double>> purchaseCosts = new HashMap<Good, HashMap<Island, Double>>();
	
	HashMap<Good, Integer> quantityEachGood = new HashMap<Good, Integer>();
	HashMap<Good, Integer> quantityProduced = new HashMap<Good, Integer>();	
	
	HashMap<Good, Island> cheapestIslands = new HashMap<Good, Island>();
	
	boolean goodSold;
	
	double cash;
	double futureCash = 0;
	double builtUpUtils = 0;
	
	public Island(String name) {
		this.name = name;
		for (Good good : Good.values()) {
			quantityEachGood.put(good, 0);
			quantityProduced.put(good, 0);
			purchaseCosts.put(good, new HashMap<Island, Double>());
		}
	}
	
	public void soldGood() {
		goodSold = true;
	}
	
	public String getName() {
		return name;
	}
	
	public Double getFutureCash() {
		return futureCash;
	}
	
	public Double getCurrentCash() {
		return cash;
	}
	
	public void updateCash(double cash) {
		this.cash = cash;
	}
	
	public void addFutureCash(double cash) {
		this.futureCash += cash;
	}
	
	public void incrementProduction(Good good) {
		int quantity = quantityProduced.get(good);
		quantityProduced.replace(good, quantity + 1);
	}
	
	public void updateProductionCosts(HashMap<Good, Double> productionCosts) {
		this.productionCosts = productionCosts;
	}
	
	public void updateShippingCosts(HashMap<Island, Double> shippingCosts) {
		this.shippingCosts = shippingCosts;
	}
	
	public void updateProductionDelta(HashMap<Good, Double> productionDelta) {
		this.productionDelta = productionDelta;
	}
	
	public void rollover() {
		cash += futureCash;
		builtUpUtils = 0;
		futureCash = 0;
		for (Good good : Good.values()) {
			quantityEachGood.replace(good, 0);
			quantityProduced.replace(good, 0);
		}
	}
	
	/*
	public void updatePurchaseCosts(HashMap<Good, HashMap<Island, Double>> purchaseCosts) {		
		long curTime = System.nanoTime();
		Iterator<Entry<Good, HashMap<Island, Double>>> hmIterator = purchaseCosts.entrySet().iterator();

		while (hmIterator.hasNext()) {
			Entry<Good, HashMap<Island, Double>> mapElement = hmIterator.next();
			Good good = mapElement.getKey();
			HashMap<Island, Double> unshippedPrices = mapElement.getValue();
			HashMap<Island, Double> shippedPrices = new HashMap<Island, Double>();
			
			Iterator<Entry<Island, Double>> hmIterator2 = unshippedPrices.entrySet().iterator();
			
			while (hmIterator2.hasNext()) {
				Entry<Island, Double> mapElement2 = hmIterator2.next();
				
				Island island = mapElement2.getKey();
				double price = mapElement2.getValue();
				double shippingCost = shippingCosts.get(island);
				shippedPrices.put(island, price + shippingCost);
			}
			
			this.purchaseCosts.put(good, shippedPrices);
		}
		
		System.out.println((System.nanoTime() - curTime) / (1000000.0) + " ms for that part 3");
	}
	*/
	
	public void updatePurchaseCosts(HashMap<Good, HashMap<Island, Double>> purchaseCosts) {		
		//long curTime = System.nanoTime();
		if (!purchaseCosts.isEmpty()) {
			Iterator<Entry<Good, HashMap<Island, Double>>> hmIterator = purchaseCosts.entrySet().iterator();
	
			while (hmIterator.hasNext()) {
				Entry<Good, HashMap<Island, Double>> mapElement = hmIterator.next();
				Good good = mapElement.getKey();
				HashMap<Island, Double> unshippedPrices = mapElement.getValue();
				HashMap<Island, Double> shippedPrices = new HashMap<Island, Double>();

				shippedPrices = this.purchaseCosts.get(good);
				
				Iterator<Entry<Island, Double>> hmIterator2 = unshippedPrices.entrySet().iterator();
				
				while (hmIterator2.hasNext()) {
					Entry<Island, Double> mapElement2 = hmIterator2.next();
					
					Island island = mapElement2.getKey();
					double price = mapElement2.getValue();
					double shippingCost = shippingCosts.get(island);
					shippedPrices.put(island, price + shippingCost);
				}
				
				this.purchaseCosts.put(good, shippedPrices);
			}
		}
		
		//System.out.println((System.nanoTime() - curTime) / (1000000.0) + " ms for that part 3");
	}
	
	public HashMap<Good, Double> getProductionCosts(){
		return productionCosts;
	}
	
	public HashMap<Good, Double> getUpdatedProductionCosts(){
		Iterator<Entry<Good, Double>> goodIterator = productionCosts.entrySet().iterator();
		HashMap<Good, Double> updatedProductionCosts = new HashMap<Good, Double>();

		if (goodSold) {
			while (goodIterator.hasNext()) {
				Entry<Good, Double> goodEntry = goodIterator.next();
				double productionCost = goodEntry.getValue();
				Good good = goodEntry.getKey();
				if (quantityProduced.get(good) != 0) {
					updatedProductionCosts.put(good, productionCost + productionDelta.get(good) * quantityProduced.get(good));
				}
			}
			goodSold = false;
		}
		
		return updatedProductionCosts;
	}
	
	public boolean buyBestGood() {
		
		Iterator<Entry<Good, HashMap<Island, Double>>> purchaseIterator = purchaseCosts.entrySet().iterator();
		
		while (purchaseIterator.hasNext()) {
			
			double cost = Double.MAX_VALUE;
			Island cheapestIsland = null;
			
			Entry<Good, HashMap<Island, Double>> element = purchaseIterator.next();
			Iterator<Entry<Island, Double>> islandIterator = element.getValue().entrySet().iterator();
			
			while (islandIterator.hasNext()) {
				Entry<Island, Double> pricePair = islandIterator.next();
				if (pricePair.getValue() < cost) {
					cost = pricePair.getValue();
					cheapestIsland = pricePair.getKey();
				}
				
			}
			
			cheapestIslands.put(element.getKey(), cheapestIsland);
		}
		
		Iterator<Entry<Good, Integer>> goodIterator = quantityEachGood.entrySet().iterator();
		
		double highestMarginalUtility = 0;
		Island islandToPurchaseFrom = null;
		double price = 0;
		double bestPrice = Double.MAX_VALUE;
		Good purchasedGood = null;
		
		while (goodIterator.hasNext()) {
			Entry<Good, Integer> mapElement = goodIterator.next();
			double currentUtil = mapElement.getKey().util(mapElement.getValue());
			Good currentGood = mapElement.getKey();
			price = purchaseCosts.get(currentGood).get(cheapestIslands.get(currentGood));
			
			if (currentUtil - price > highestMarginalUtility) {
				highestMarginalUtility = currentUtil;
				islandToPurchaseFrom = cheapestIslands.get(currentGood);
				purchasedGood = currentGood;
				bestPrice = price;
			}
		}
		
		if ((highestMarginalUtility != 0)&&(cash - bestPrice >= 0)) {
			cash = cash - bestPrice;
			
			double shippingCost = shippingCosts.get(islandToPurchaseFrom);
			islandToPurchaseFrom.addFutureCash(bestPrice - shippingCost);
			islandToPurchaseFrom.incrementProduction(purchasedGood);
			islandToPurchaseFrom.soldGood();
			
			Iterator<Entry<Island, Double>> shippingIterator = shippingCosts.entrySet().iterator();
			while (shippingIterator.hasNext()) {
				Entry<Island, Double> islandElement = shippingIterator.next();
				islandElement.getKey().addFutureCash(shippingCost / (double) shippingCosts.size());
			}
			
			quantityEachGood.replace(purchasedGood, quantityEachGood.get(purchasedGood) + 1);
			builtUpUtils += highestMarginalUtility;

			//System.out.println("Island " + name + " purchases " + purchasedGood + " from island " + islandToPurchaseFrom.getName() + " for " + (bestPrice - shippingCost) + " with " + shippingCost + " shipping.");
			//System.out.println("Island " + name + " now has " + quantityEachGood.get(purchasedGood) + " " + purchasedGood + " with " + builtUpUtils + " built up utils and " + cash + " cash, and " + futureCash + " future cash.");
			return true;
		} else {
			return false;
		}
	}
}

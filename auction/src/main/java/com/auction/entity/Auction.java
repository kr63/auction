package com.auction.entity;

import com.auction.exceptions.AuctionImpossibleException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@NoArgsConstructor
public class Auction {

    private NavigableMap<Float, Integer> demandBids = new TreeMap<>(Collections.reverseOrder());
    private NavigableMap<Float, Integer> sellBids = new TreeMap<>();
    private Integer maximumVolume;
    private Float minimumPrice;
    private boolean auctionPossible;

    public boolean isAuctionPossible() {
        Float demandMaxPrice = demandBids.firstKey();
        Float sellMinPrice = sellBids.firstKey();
        return (demandMaxPrice > sellMinPrice);
    }

    public void addBid(Bid bid) {
        Type type = bid.getType();
        float price = bid.getPrice();
        int volume = bid.getVolume();

        switch (type) {
            case DEMAND:
                updateVolume(demandBids, price, volume);
                break;
            case SELL:
                updateVolume(sellBids, price, volume);
                break;
        }
    }

    public Integer getMaximumVolume() throws AuctionImpossibleException {
        if (isAuctionPossible()) {
            SortedSet<Map.Entry<Float, Integer>> sortedSet = calculateResultMatrix();
            return sortedSet.first().getValue();
        }
        throw new AuctionImpossibleException();
    }

    public Float getMinimumPrice() throws AuctionImpossibleException {
        if (isAuctionPossible()) {
            SortedSet<Map.Entry<Float, Integer>> sortedSet = calculateResultMatrix();
            return sortedSet.first().getKey();
        }
        throw new AuctionImpossibleException();
    }

    private void updateVolume(NavigableMap<Float, Integer> bids, float price, int volume) {
        Integer presentVolume = bids.putIfAbsent(price, volume);
        if (presentVolume != null) {
            bids.put(price, volume + presentVolume);
        }
    }

    private NavigableMap<Float, Integer> calculateAbsoluteVolume(NavigableMap<Float, Integer> map) {
        NavigableMap<Float, Integer> output = new TreeMap<>();
        Float firstKey = map.firstKey();
        for (Map.Entry<Float, Integer> entry : map.entrySet()) {
            Float key = entry.getKey();
            NavigableMap<Float, Integer> subMap = map.subMap(firstKey, true, key, true);
            int subMapValuesSum = subMap.values().stream().mapToInt(Integer::intValue).sum();
            output.put(key, subMapValuesSum);
        }
        return output;
    }

    private SortedSet<Map.Entry<Float, Integer>> calculateResultMatrix() {
        NavigableMap<Float, Integer> sellPossibleVolumes = calculateAbsoluteVolume(sellBids);
        NavigableMap<Float, Integer> demandPossibleVolumes = calculateAbsoluteVolume(demandBids);

        for (Map.Entry<Float, Integer> entry : demandPossibleVolumes.entrySet()) {
            Float key = entry.getKey();
            Integer value = entry.getValue();
            Integer orDefault = sellPossibleVolumes.getOrDefault(key, Integer.MAX_VALUE);
            sellPossibleVolumes.put(key, Math.min(value, orDefault));
        }

        Comparator<Map.Entry<Float, Integer>> comparator =
                Map.Entry.<Float, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.comparingByKey());
        SortedSet<Map.Entry<Float, Integer>> output = new TreeSet<>(comparator);
        output.addAll(sellPossibleVolumes.entrySet());
        return output;
    }
}

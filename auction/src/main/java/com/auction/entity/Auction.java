package com.auction.entity;

import com.auction.exceptions.AuctionImpossibleException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

@Getter
@Setter
@NoArgsConstructor
public class Auction {

    private NavigableMap<Float, Integer> demandBids = new TreeMap<>(Collections.reverseOrder());
    private NavigableMap<Float, Integer> sellBids = new TreeMap<>();

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

    private void updateVolume(NavigableMap<Float, Integer> bids, float price, int volume) {
        Integer presentVolume = bids.putIfAbsent(price, volume);
        if (presentVolume != null) {
            bids.put(price, volume + presentVolume);
        }
    }

    public Float getEquilibrium() throws AuctionImpossibleException {
        Float demandMaxPrice = demandBids.firstKey();
        Float sellMinPrice = sellBids.firstKey();
        if (demandMaxPrice < sellMinPrice) {
            throw new AuctionImpossibleException();
        }
        return -1f;
    }
}

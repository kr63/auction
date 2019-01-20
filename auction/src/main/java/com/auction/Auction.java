package com.auction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.NavigableMap;
import java.util.TreeMap;

@Getter
@Setter
@NoArgsConstructor

class Auction {

    private NavigableMap<Float, Integer> demandBids = new TreeMap<>();
    private NavigableMap<Float, Integer> sellBids = new TreeMap<>();

    void addBid(Bid bid) {

        BidType bitType = bid.getBidType();

        switch (bitType) {
            case DEMAND:
                demandBids.put(bid.getPrice(), bid.getVolume());
                break;
            case SELL:
                sellBids.put(bid.getPrice(), bid.getVolume());
                break;
        }
    }

    void getEquilibrium() {
    }
}

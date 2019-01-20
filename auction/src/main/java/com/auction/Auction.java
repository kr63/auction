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
        float bidPrice = bid.getPrice();
        int bidVolume = bid.getVolume();

        switch (bitType) {
            case DEMAND:
                updateBidVolume(bidPrice, bidVolume, demandBids);
                break;
            case SELL:
                updateBidVolume(bidPrice, bidVolume, sellBids);
                break;
        }
    }

    private void updateBidVolume(float bidPrice, int bidVolume, NavigableMap<Float, Integer> navigableMap) {
        Integer presentVolume = navigableMap.putIfAbsent(bidPrice, bidVolume);
        if (presentVolume != null) {
            navigableMap.put(bidPrice, bidVolume + presentVolume);
        }
    }

    void getEquilibrium() {
    }
}

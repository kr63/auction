package com.auction;

import com.auction.entity.Auction;
import com.auction.entity.Bid;
import com.auction.entity.Type;
import com.auction.exceptions.AuctionImpossibleException;

public class AuctionDemo {

    public static void main(String[] args) {
        Auction auction = new Auction();
        Bid demandBid = new Bid(Type.DEMAND, 50, 9f);
        Bid sellBid = new Bid(Type.SELL, 200, 11.2f);
        auction.addBid(demandBid);
        auction.addBid(sellBid);
        try {
            System.out.println(auction.getEquilibrium());
        } catch (AuctionImpossibleException e) {
            System.out.println("0 n/a");
        }
    }
}

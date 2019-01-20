package com.auction;

public class AuctionDemo {

    public static void main(String[] args) {
        Auction auction = new Auction();
        Bid demandBid = new Bid(BidType.DEMAND, 50, 9f);
        Bid sellBid = new Bid(BidType.SELL, 200, 11.2f);
        auction.addBid(demandBid);
        auction.addBid(sellBid);
        auction.getEquilibrium();
    }
}

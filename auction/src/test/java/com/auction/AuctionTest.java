package com.auction;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuctionTest {

    @Test
    public void testEntities() {
        Auction auction = new Auction();
        Bid demandBid = new Bid(BidType.DEMAND, 50, 9);
        Bid sellBid = new Bid(BidType.SELL, 200, 11.2f);
        auction.addBid(demandBid);
        auction.addBid(sellBid);

        assertNotNull("Auction should exist", auction);
        assertNotNull("Bid should exist", demandBid);
        assertEquals(sellBid.getBidType(), BidType.SELL);
        assertEquals(sellBid.getPrice(), 11.2f, 0.0f);
        assertEquals(demandBid.getVolume(), 50);
    }


}
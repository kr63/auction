package com.auction;

import org.junit.Before;
import org.junit.Test;

import java.util.NavigableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuctionTest {

    private Auction auction;

    @Before
    public void setup() {
        auction = new Auction();
    }

    @Test
    public void testEntities() {
        Bid demandBid = new Bid(BidType.DEMAND, 50, 9.0f);
        Bid sellBid = new Bid(BidType.SELL, 200, 11.2f);
        auction.addBid(demandBid);
        auction.addBid(sellBid);

        assertNotNull("Auction should exist", auction);
        assertNotNull("Bid should exist", demandBid);
        assertEquals(sellBid.getBidType(), BidType.SELL);
        assertEquals(sellBid.getPrice(), 11.2f, 0.0f);
        assertEquals(demandBid.getVolume(), 50);
    }

    @Test
    public void testVolumeAccumulation() {
        // given
        for (int i = 0; i < 3; i++) {
            auction.addBid(new Bid(BidType.DEMAND, 10, 1.0f));
            auction.addBid(new Bid(BidType.SELL, 1, 1.0f));
        }
        NavigableMap<Float, Integer> demandBids = auction.getDemandBids();
        NavigableMap<Float, Integer> sellBids= auction.getSellBids();

        // when
        Integer demandBidsVolume = demandBids.get(1.0f);
        Integer sellBidsVolume = sellBids.get(1.0f);

        // then
        assertEquals(demandBidsVolume, Integer.valueOf(3 * 10));
        assertEquals(sellBidsVolume, Integer.valueOf(3));
    }


}
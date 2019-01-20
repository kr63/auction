package com.auction;

import com.auction.entity.Auction;
import com.auction.entity.Bid;
import com.auction.entity.Type;
import com.auction.exceptions.AuctionImpossibleException;
import org.junit.Before;
import org.junit.Test;

import java.util.NavigableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class AuctionTest {

    private Auction auction;

    @Before
    public void setup() {
        auction = new Auction();
    }

    @Test
    public void testEntitiesCreation() {
        // given
        Bid demandBid = new Bid(Type.DEMAND, 50, 9.0f);
        Bid sellBid = new Bid(Type.SELL, 200, 11.2f);

        // when
        auction.addBid(demandBid);
        auction.addBid(sellBid);

        // then
        assertThat(auction).as("Auction should exist").isNotNull();
        assertThat(demandBid).as("Bid should exist").isNotNull();
        assertThat(sellBid.getType()).isEqualTo(Type.SELL);
        assertThat(sellBid.getPrice()).isEqualTo(11.2f);
        assertThat(demandBid.getVolume()).isEqualTo(50);
    }

    @Test
    public void testVolumeAccumulation() {
        // given
        for (int i = 0; i < 3; i++) {
            auction.addBid(new Bid(Type.DEMAND, 10, 1.0f));
            auction.addBid(new Bid(Type.SELL, 1, 1.0f));
        }
        NavigableMap<Float, Integer> demandBids = auction.getDemandBids();
        NavigableMap<Float, Integer> sellBids = auction.getSellBids();

        // when
        Integer demandVolume = demandBids.get(1.0f);
        Integer sellVolume = sellBids.get(1.0f);

        // then
        assertThat(demandVolume).isEqualTo(30);
        assertThat(sellVolume).isEqualTo(3);
    }

    @Test
    public void testAuctionPossibility() {
        // given
        auction.addBid(new Bid(Type.DEMAND, 10, 7.0f));
        auction.addBid(new Bid(Type.DEMAND, 10, 5.0f));
        auction.addBid(new Bid(Type.SELL, 10, 10.0f));
        auction.addBid(new Bid(Type.SELL, 10, 20.0f));
        // when & then
        assertThatExceptionOfType(AuctionImpossibleException.class).isThrownBy(() -> auction.getEquilibrium());
    }


}
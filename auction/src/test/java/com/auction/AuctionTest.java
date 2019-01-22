package com.auction;

import com.auction.entity.Auction;
import com.auction.entity.Bid;
import com.auction.entity.Type;
import com.auction.exceptions.AuctionImpossibleException;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        assertThat(auction.isPossible()).isEqualTo(false);
        assertThatExceptionOfType(AuctionImpossibleException.class).isThrownBy(() -> auction.getMaximumVolume());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPossibleVolumeValue() throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {

        // given
        auction.addBid(new Bid(Type.SELL, 5, 5.0f));
        auction.addBid(new Bid(Type.SELL, 10, 10.0f));
        auction.addBid(new Bid(Type.DEMAND, 5, 15.0f));
        auction.addBid(new Bid(Type.DEMAND, 10, 10.0f));
        auction.addBid(new Bid(Type.DEMAND, 15, 5.0f));
        NavigableMap<Float, Integer> sellBids = auction.getSellBids();
        NavigableMap<Float, Integer> demandBids = auction.getDemandBids();

        // when
        Method method = Auction.class.getDeclaredMethod("calculateAbsoluteVolume", NavigableMap.class);
        method.setAccessible(true);
        NavigableMap<Float, Integer> sellBidsPossibleVolumes =
                (NavigableMap<Float, Integer>) method.invoke(auction, sellBids);
        NavigableMap<Float, Integer> demandBidsPossibleVolumes =
                (NavigableMap<Float, Integer>) method.invoke(auction, demandBids);

        // then
        assertThat(sellBidsPossibleVolumes).containsOnlyKeys(5.0f, 10.0f);
        assertThat(sellBidsPossibleVolumes.get(5.0f)).isEqualTo(5);
        assertThat(sellBidsPossibleVolumes.get(10.0f)).isEqualTo(15);
        assertThat(demandBidsPossibleVolumes).containsOnlyKeys(5.0f, 10.0f, 15.0f);
        assertThat(demandBidsPossibleVolumes.get(5.0f)).isEqualTo(30);
        assertThat(demandBidsPossibleVolumes.get(10.0f)).isEqualTo(15);
        assertThat(demandBidsPossibleVolumes.get(15.0f)).isEqualTo(5);
    }

    @Test
    public void testMaximumVolume() throws AuctionImpossibleException {
        // given
        auction.addBid(new Bid(Type.DEMAND, 5, 15.0f));
        auction.addBid(new Bid(Type.DEMAND, 5, 10.0f));
        auction.addBid(new Bid(Type.DEMAND, 5, 5.0f));
        auction.addBid(new Bid(Type.SELL, 7, 2.0f));
        auction.addBid(new Bid(Type.SELL, 8, 4.0f));

        // when
        Integer maximumVolume = auction.getMaximumVolume();
        Float minimumPrice = auction.getMinimumPrice();

        // then
        assertThat(maximumVolume).isEqualTo(15);
        assertThat(minimumPrice).isEqualTo(4.0f);
    }

    @Test
    public void testEqualQuantity() throws AuctionImpossibleException {
        // given
        auction.addBid(new Bid(Type.DEMAND, 1, 10.1f));
        auction.addBid(new Bid(Type.SELL, 1, 10.1f));
        // when & then
        assertThat(auction.isPossible()).isEqualTo(true);
        assertThat(auction.getMaximumVolume()).isEqualTo(1);
        assertThat(auction.getMinimumPrice()).isEqualTo(10.1f);
    }

    @Test
    public void testCrossDemandAndSellCurve() throws AuctionImpossibleException {
        auction.addBid(new Bid(Type.DEMAND, 1, 20f));
        auction.addBid(new Bid(Type.DEMAND, 1, 15f));
        auction.addBid(new Bid(Type.DEMAND, 1, 10f));
        auction.addBid(new Bid(Type.DEMAND, 1, 5f));
        auction.addBid(new Bid(Type.SELL, 1, 5f));
        auction.addBid(new Bid(Type.SELL, 1, 10f));
        auction.addBid(new Bid(Type.SELL, 1, 17f));
        auction.addBid(new Bid(Type.SELL, 1, 22f));

        assertThat(auction.isPossible()).isEqualTo(true);
        assertThat(auction.getMaximumVolume()).isEqualTo(2);
        assertThat(auction.getMinimumPrice()).isEqualTo(10f);
    }

    @Test
    public void testMinDemandPriceHigherThanMinSellPrice() throws AuctionImpossibleException {
        auction.addBid(new Bid(Type.DEMAND, 10, 10f));
        auction.addBid(new Bid(Type.DEMAND, 10, 6f));
        auction.addBid(new Bid(Type.DEMAND, 10, 5f));
        auction.addBid(new Bid(Type.DEMAND, 10, 1f));
        auction.addBid(new Bid(Type.SELL, 10, 2f));
        auction.addBid(new Bid(Type.SELL, 10, 5f));
        auction.addBid(new Bid(Type.SELL, 10, 8f));
        auction.addBid(new Bid(Type.SELL, 10, 10f));

        assertThat(auction.isPossible()).isEqualTo(true);
        assertThat(auction.getMaximumVolume()).isEqualTo(20);
        assertThat(auction.getMinimumPrice()).isEqualTo(5f);
    }
}
package com.auction;

import com.auction.entity.Auction;
import com.auction.entity.Bid;
import com.auction.entity.Type;
import com.auction.exceptions.AuctionImpossibleException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class AuctionDemo {

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(args[0]));
            Auction auction = readInputData(in);
            Integer maximumVolume = auction.getMaximumVolume();
            Float minimumPrice = auction.getMinimumPrice();
            System.out.println(maximumVolume + " " + minimumPrice);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Your should provide input file as argument!");
        } catch (AuctionImpossibleException e) {
            System.out.println("0 n/a");
        } catch (FileNotFoundException e) {
            System.out.println(String.format("File '%s' not found!", args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Auction readInputData(BufferedReader in) throws IOException {
        Auction output = new Auction();
        String line;
        StringTokenizer stk;
        String type;
        int volume;
        float price;
        while ((line = in.readLine()) != null) {
            stk = new StringTokenizer(line);
            type = stk.nextToken();
            volume = Integer.parseInt(stk.nextToken());
            price = Float.parseFloat(stk.nextToken());

            switch (type) {
                case "B":
                    output.addBid(new Bid(Type.DEMAND, volume, price));
                    break;
                case "S":
                    output.addBid(new Bid(Type.SELL, volume, price));
                    break;
            }
        }
        return output;
    }
}

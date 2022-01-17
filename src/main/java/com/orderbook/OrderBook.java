package com.orderbook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class OrderBook{
    private final static BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private final static int MAX_PRICE_LEVEL = 99999;
    private final int[] book = new int[MAX_PRICE_LEVEL];
    private int bestBid = 0;
    private int bestAsk = MAX_PRICE_LEVEL;

    public void insertQuote(int scaledPrice, int scaledQuantity, char side) {
        if (scaledQuantity == 0) {
            book[scaledPrice] = 0;
            if (bestBid == scaledPrice) {
                bestBid = findNextBestBid(bestBid);
            } else if (bestAsk == scaledPrice) {
                bestAsk = findNextBestAsk(bestAsk);
            }
        } else if (book[scaledPrice] != 0){
            book[scaledPrice] += scaledQuantity;
        } else {
            book[scaledPrice] = scaledQuantity;
            if (side == 'b' && scaledPrice > bestBid) {
                bestBid = scaledPrice;
            } else if (side == 's' && scaledPrice < bestAsk) {
                bestAsk = scaledPrice;
            }
        }
    }

    public String topLevel() {
        List<Integer> bid = bestBid == 0 ? List.of() : List.of(bestBid);
        List<Integer> ask = bestAsk == MAX_PRICE_LEVEL ? List.of() : List.of(bestAsk);
        return formatBidsAndAsks(bid, ask);
    }

    public BigDecimal averagePriceOverNLevels(int n) {
        List<Integer> asks = bids(n);
        List<Integer> bids = asks(n);
        int itemCount = asks.size() + bids.size();
        if (itemCount == 0) return BigDecimal.ZERO;
        return Stream.concat(asks.stream(),bids.stream())
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(itemCount).multiply(ONE_HUNDRED), 8, RoundingMode.HALF_UP);
    }

    public BigDecimal totalQuantityOverNLevels(int n) {
        return Stream.concat(asks(n).stream(), bids(n).stream())
                .map(i -> BigDecimal.valueOf(book[i]))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal volumeWeightedPriceOverNLevels(int n) {
        List<Integer> asks = asks(n);
        List<Integer> bids = bids(n);
        BigDecimal totalScaledQuantity = Stream.concat(asks.stream(), bids.stream())
                                            .map(i -> BigDecimal.valueOf(book[i]))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalScaledQuantity.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return Stream.concat(asks.stream(), bids.stream())
                .map(i -> BigDecimal.valueOf(i).multiply(BigDecimal.valueOf(book[i])))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(totalScaledQuantity.multiply(ONE_HUNDRED), 8, RoundingMode.HALF_UP);
    }

    public String printAllQuotes() {
        List<Integer> bids = bids(MAX_PRICE_LEVEL);
        List<Integer> asks = asks(MAX_PRICE_LEVEL);
        return formatBidsAndAsks(bids, asks);
    }

    private List<Integer> bids(int level) {
        List<Integer> bids = new ArrayList<>();
        for (int i = bestBid; i > 0 ; i--) {
            if (book[i] > 0) {
                bids.add(i);
            }
            if (bids.size() == level) return bids;
        }
        return bids;
    }

    private List<Integer> asks(int level) {
        List<Integer> asks = new ArrayList<>();
        for (int i = bestAsk; i < MAX_PRICE_LEVEL ; i++) {
            if (book[i] > 0) {
                asks.add(i);
            }
            if (asks.size() == level) return asks;
        }
        return asks;
    }

    private int findNextBestBid(int currBestBid) {
        for (int i = currBestBid -1; i >= 0; i--) {
            if (book[i] > 0) {
                return i;
            }
        }
        return 0;
    }
    private int findNextBestAsk(int currBestAsk) {
        for (int i = currBestAsk+1; i < MAX_PRICE_LEVEL; i++) {
            if (book[i] > 0) {
                return i;
            }
        }
        return MAX_PRICE_LEVEL;
    }

    private String formatBidsAndAsks(List<Integer> bids, List<Integer> asks) {
        int levels = Integer.max(bids.size(), asks.size());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < levels; i++) {
            sb.append(i).append(": ");
            if (i < bids.size()) {
                sb.append(String.format("%.2f", book[bids.get(i)] / 100.0)).append(" ").append(bids.get(i)/ 100.0);
            } else {
                sb.append("          "); // Fill gap with 10 characters for formatting in case fewer bids than asks
            }
            sb.append(" | ");
            if (i < asks.size()) {
                sb.append(asks.get(i) / 100.0).append(" ").append(String.format("%.2f", book[asks.get(i)] / 100.0));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}


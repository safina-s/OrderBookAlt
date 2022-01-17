package com.orderbook;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderBookTest {
    @Test
    public void insertQuote_newSell_addsQuote(){
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(3299, 10000, 's');
        assertEquals("0:            | 32.99 100.00\n", orderBook.printAllQuotes());
    }

    @Test
    public void insertQuote_newBuyQuote_insertsIntoBid() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(3299, 10000, 'b');
        assertEquals("0: 100.00 32.99 | \n", orderBook.printAllQuotes());
    }

    @Test
    public void insertQuote_samePrice_aggregatesQuantity() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(3280, 10000, 's');
        orderBook.insertQuote(3280, 10000, 's');
        assertEquals("0:            | 32.8 200.00\n", orderBook.printAllQuotes());
    }

    @Test
    public void insertQuote_zeroQuantity_removesQuoteAndReturnsEmptyString() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(3280, 10000, 's');
        orderBook.insertQuote(3280, 0, 's');
        assertEquals("", orderBook.printAllQuotes());
    }

    @Test
    public void printAllQuotes_equalAsksAndBids_returnsFormattedString() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(5299, 16000, 's');
        orderBook.insertQuote(5420, 17080, 's');
        orderBook.insertQuote(3759, 194950, 'b');
        orderBook.insertQuote(4160, 65456, 'b');

        String allQuotes = orderBook.printAllQuotes();
        String expected = """
                0: 654.56 41.6 | 52.99 160.00
                1: 1949.50 37.59 | 54.2 170.80
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void printAllQuotes_fewerBids_returnsFormattedString() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(5299, 16000, 's');
        orderBook.insertQuote(5420, 17080, 's');
        orderBook.insertQuote(3759, 194950, 'b');

        String allQuotes = orderBook.printAllQuotes();
        String expected = """
                0: 1949.50 37.59 | 52.99 160.00
                1:            | 54.2 170.80
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void printAllQuotes_fewerSells_returnsFormattedString() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(5299, 16000, 'b');
        orderBook.insertQuote(5420, 17080, 's');
        orderBook.insertQuote(3759, 194950, 'b');

        String allQuotes = orderBook.printAllQuotes();
        String expected = """
                0: 160.00 52.99 | 54.2 170.80
                1: 1949.50 37.59 |\s
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void topLevel_noDataExists_returnsEmptyString() {
        OrderBook orderBook = new OrderBook();
        String allQuotes = orderBook.topLevel();
        assertEquals("", allQuotes);
    }

    @Test
    public void topLevel_dataExists_returnsFormattedString() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(5299, 16000, 's');
        orderBook.insertQuote(5420, 17080, 's');
        orderBook.insertQuote(3759, 194950, 'b');
        orderBook.insertQuote(4160, 65456, 'b');

        String allQuotes = orderBook.topLevel();
        String expected = """
                0: 654.56 41.6 | 52.99 160.00
                """;
        assertEquals(expected, allQuotes);
    }

    @Test
    public void averagePriceOverNLevels_differentNumberOfBidsAndAsks_returnsAveragePrice() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(1277, 10000, 'b');
        orderBook.insertQuote(53458, 10000, 's');
        orderBook.insertQuote(5423, 10000, 's');
        BigDecimal averagePrice = orderBook.averagePriceOverNLevels(2);
        assertEquals(0, BigDecimal.valueOf(200.52666667).compareTo(averagePrice));

    }

    @Test
    public void averagePriceOverNLevels_sameNumberOfBidsAsks_returnsAveragePrice() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(1277, 10000, 'b');
        orderBook.insertQuote(1450, 10000, 'b');
        orderBook.insertQuote(53458, 10000, 's');
        orderBook.insertQuote(5423, 10000, 's');
        BigDecimal averagePrice = orderBook.averagePriceOverNLevels(2);
        assertEquals(0, BigDecimal.valueOf(154.02).compareTo(averagePrice));
    }

    @Test
    public void averagePriceOverNLevels_levelsRequestGreaterThanExisting_returnsAveragePrice() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(1277, 10000, 'b');
        orderBook.insertQuote(1450, 10000, 'b');
        orderBook.insertQuote(53458, 10000, 's');
        orderBook.insertQuote(5423, 10000, 's');
        BigDecimal averagePrice = orderBook.averagePriceOverNLevels(10);
        assertEquals(0, BigDecimal.valueOf(154.02).compareTo(averagePrice));
    }

    @Test
    public void averagePriceOverNLevels_noQuotes_returnsZero() {
        OrderBook orderBook = new OrderBook();
        BigDecimal averagePrice = orderBook.averagePriceOverNLevels(2);
        assertEquals(0, BigDecimal.ZERO.compareTo(averagePrice));
    }

    @Test
    public void totalQuantityOverNLevels_noQuotes_returnsZero() {
        OrderBook orderBook = new OrderBook();
        assertEquals(0, BigDecimal.ZERO.compareTo(orderBook.totalQuantityOverNLevels(10)));
    }

    @Test
    public void totalQuantityOverNLevels_aggregatedQuote_returnsTotalQuantity() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(1277, 16000, 'b');
        orderBook.insertQuote(1450, 20067, 'b');
        orderBook.insertQuote(53458, 60050, 's');
        orderBook.insertQuote(53458, 10099, 's');
        BigDecimal totalQuantity = orderBook.totalQuantityOverNLevels(10);
        assertEquals(0, BigDecimal.valueOf(1062.16).compareTo(totalQuantity));
    }


    @Test
    public void volumeWeightedAveragePrice_dataExists_returnsVolumeWeightedAveragePrice() {
        OrderBook orderBook = new OrderBook();
        orderBook.insertQuote(1277, 16000, 'b');
        orderBook.insertQuote(1450, 20067, 'b');
        orderBook.insertQuote(53458, 60050, 's');
        orderBook.insertQuote(53458, 10099, 's');
        BigDecimal volumeWeightedPrice = orderBook.volumeWeightedPriceOverNLevels(2);
        assertEquals(0, BigDecimal.valueOf(357.71958952).compareTo(volumeWeightedPrice));
    }

    @Test
    public void volumeWeightedAveragePrice_noQuotes_returnsZero() {
        OrderBook orderBook = new OrderBook();
        assertEquals(0, BigDecimal.ZERO.compareTo(orderBook.volumeWeightedPriceOverNLevels(1)));
    }
}
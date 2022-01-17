package com.orderbook;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderBookCollectionTest {

    @Test
    void getOrderBook_nonExistingInstrument_returnsNull() {
        OrderBookCollection orderBookCollection = new OrderBookCollection();
        assertNull(orderBookCollection.getOrderBook("non-existent"));
    }

    @Test
    void insertQuote_validQuote_insertsInOrderBook() {
        OrderBookCollection orderBookCollection = new OrderBookCollection();
        String quote = "t=1638848595|i=BTCUSD|p=32.99|q=100|s=s";
        orderBookCollection.insertQuote(quote);
        assertNotNull(orderBookCollection.getOrderBook("BTCUSD"));
    }
}
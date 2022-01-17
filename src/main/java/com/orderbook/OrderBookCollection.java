package com.orderbook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class OrderBookCollection implements Runnable {
    private final Map<String, OrderBook> instrumentUniverse = new HashMap<>();

    public OrderBook getOrderBook(String instrument) {
        return instrumentUniverse.get(instrument);
    }

    public void insertQuote(String quote) {
        String[] splitStr = quote.split("\\|");
        String instrument = splitStr[1].substring(2);
        int price = convertDataToScaledInt(splitStr[2].substring(2));
        int quantity = convertDataToScaledInt(splitStr[3].substring(2));
        char side = splitStr[4].substring(2).charAt(0);
        instrumentUniverse.computeIfAbsent(instrument, k -> new OrderBook()).insertQuote(price, quantity, side);
    }

    private void readFromFile() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("exchange.txt");
            try (BufferedReader br
                         = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    insertQuote(line);
                }
            }
            inputStream.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void run() {
        readFromFile();
    }

    private int convertDataToScaledInt(String rawNumber) {
        return new BigDecimal(rawNumber).setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
    }
}

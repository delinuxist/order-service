package com.tradingengine.orderservice.enums;

public enum APIKEY {
    KEY("7d21b2cb-9942-4948-9699-1b9fa5a8ad1c");

    private final String key;
    APIKEY(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

package com.tradeguard.api.constants;

public class OrderUrls {
    public static final String BASE_API = "/api";
    public static final String VERSION = "/v1";
    public static final String ORDER_BASE = BASE_API + VERSION + "/orders";
    public static final String ORDER_BY_ID = "/{orderId}";
    public static final String ORDER_LIST = "/list";
    public static final String ORDER_MATCH = ORDER_BY_ID + "/match";
}


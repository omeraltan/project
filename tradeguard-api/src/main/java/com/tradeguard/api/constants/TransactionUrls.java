package com.tradeguard.api.constants;

public class TransactionUrls {
    public static final String BASE_API = "/api";
    public static final String VERSION = "/v1";
    public static final String TRANSACTION_BASE = BASE_API + VERSION + "/transactions";
    public static final String GET_ALL_TRANSACTIONS = "/all";
    public static final String GET_CUSTOMER_TRANSACTIONS = "/customer/{customerId}";
    public static final String DEPOSIT = "/deposit";
    public static final String WITHDRAW = "/withdraw";
}

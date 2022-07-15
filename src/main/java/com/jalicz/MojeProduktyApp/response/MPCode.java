package com.jalicz.MojeProduktyApp.response;

public enum MPCode {
    SUCCESS(1, "Vše proběhlo úspěšně"),
    NO_ITEM_FOUND(2, "Žádný předmět nebyl nalezen"),
    INVALID_PASSWORD(3, "Nesprávné heslo"),
    ERROR(4, "Něco se pokazilo");

    private static final MPCode DEFAULT = ERROR;

    public final int code;
    public final String msg;

    MPCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static MPCode getByCode(int code) {
        for(MPCode c: values()) if(c.code == code) return c;
        return DEFAULT;
    }
}

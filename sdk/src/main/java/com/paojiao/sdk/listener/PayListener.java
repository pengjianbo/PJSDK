package com.paojiao.sdk.listener;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/10/28 下午6:05
 */
public interface PayListener {
    void onPaySuccess();
    void onPayFailure();
    void onPayCancel();
}

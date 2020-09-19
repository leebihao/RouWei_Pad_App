package com.scinan.sdk.error;

/**
 * Created by lijunjie on 2017/12/1.
 */

public class WaitNextTransferException extends Exception {
    public WaitNextTransferException() {
    }

    public WaitNextTransferException(String detailMessage) {
        super(detailMessage);
    }

    public WaitNextTransferException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public WaitNextTransferException(Throwable throwable) {
        super(throwable);
    }
}

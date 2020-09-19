package com.scinan.sdk.error;

/**
 * Created by lijunjie on 2017/12/1.
 */

public class ResponseTransferException extends Exception {
    public ResponseTransferException() {
    }

    public ResponseTransferException(String detailMessage) {
        super(detailMessage);
    }

    public ResponseTransferException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ResponseTransferException(Throwable throwable) {
        super(throwable);
    }
}

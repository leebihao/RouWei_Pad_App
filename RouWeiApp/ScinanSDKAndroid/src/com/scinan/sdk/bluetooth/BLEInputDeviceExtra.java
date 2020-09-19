package com.scinan.sdk.bluetooth;

import java.util.UUID;

/**
 * Created by lijunjie on 17/3/2.
 */

public abstract class BLEInputDeviceExtra implements InputDeviceExtra {

    public abstract UUID getReadServiceUuid();

    public abstract UUID getWriteServiceUuid();

    public abstract UUID getReadCharacteristic();

    public abstract UUID getWriteCharacteristic();
}

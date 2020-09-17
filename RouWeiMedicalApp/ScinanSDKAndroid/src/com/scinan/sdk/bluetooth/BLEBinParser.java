package com.scinan.sdk.bluetooth;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by lijunjie on 17/4/26.
 */

public class BLEBinParser {

    public static BLEBinFile parseFromPath(String path) throws Exception {
        BLEBinFile file = new BLEBinFile(path);

        if (!file.exists()) {
            throw new Exception("file not exists " + path);
        }

        InputStream stream;
        stream = new FileInputStream(file);

        byte[] source = new byte[0x40000];
        stream.read(source, 0, source.length);
        stream.close();

        file.setSource(source);

        file.setVer(Conversion.buildUint16(source[5], source[4]));
        file.setLen(Conversion.buildUint16(source[7], source[6]));
        file.setTypeB(((file.getVer() & 1) == 1));
        System.arraycopy(source, 8, file.getUid(), 0, 4);

        return file;
    }

    public static BLEBinFile parseFromBytes(byte[] data) throws Exception {
        BLEBinFile file = new BLEBinFile();
        file.setVer(Conversion.buildUint16(data[1], data[0]));
        file.setTypeB(((file.getVer() & 1) == 1));
        file.setLen(Conversion.buildUint16(data[3], data[2]));
        return file;
    }
}

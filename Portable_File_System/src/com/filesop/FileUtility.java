package com.filesop;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class FileUtility {

    public static byte[] convertToArrayBytes(int integerValue) 
    {
        final ByteBuffer byteBufTemp = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        byteBufTemp.order(ByteOrder.LITTLE_ENDIAN);
        byteBufTemp.putInt(integerValue);
        return byteBufTemp.array();
    }

    public static int convertByteArray(byte[] byteArray) 
    {
        final ByteBuffer byteBufTemp = ByteBuffer.wrap(byteArray);
        byteBufTemp.order(ByteOrder.LITTLE_ENDIAN);
        return byteBufTemp.getInt();
    }

    public static void createNewDir(String dirName) 
    {
        String dir_path = System.getProperty("user.home") + "/Desktop/" + dirName;
        File dir = new File(dir_path);
        
        if (!dir.exists()) {
            try 
            {
            	dir.mkdir();
            } 
            catch (Exception exp) 
            {
            	exp.printStackTrace();
            }
        }
    }

}

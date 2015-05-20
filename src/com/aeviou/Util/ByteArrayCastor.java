package com.aeviou.Util;

public class ByteArrayCastor {
	/**
	 * cast 4 byte in b beginning from offset to int (big endian)
	 * @param b
	 * @param offset
	 * @return
	 */
    public static int getInteger(byte[] b, int offset) {
    	return ((b[offset+0] & 0xFF) << 24) 
    			| ((b[offset+1] & 0xFF) << 16)
    			| ((b[offset+2] & 0xFF) << 8) 
    			| (b[offset+3] & 0xFF);
    }

    /**
     * cast 2 byte in b beginning from offset to char (big endian)
     * @param b
     * @param offset
     * @return
     */
    public static char getChar(byte[] b, int offset) {
    	return (char) (((b[offset+0] & 0xFF) << 8)
    			| (b[offset+1] & 0xFF));
    }
}

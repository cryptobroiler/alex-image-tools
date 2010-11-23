/**
 * 
 */
package org.highscreen.utility;

import java.io.RandomAccessFile;
import java.security.MessageDigest;

/**
 * @author deus
 * 
 */
public class ChunkInfo {
	public int start;
	public int size;
	public String hash;
	public String name;
	private byte[] data = null;

	public ChunkInfo(int start, int size, String hash, String name) {
		this.start = start;
		this.size = size;
		this.hash = hash;
		this.name = name;
		data = new byte[size];
	}

	public void dumpToFile() throws Exception {
		RandomAccessFile file = new RandomAccessFile(name + ".bin", "rw");
		file.write(data);
	}

	public void readData(RandomAccessFile file) throws Exception {
		file.seek(start);
		file.read(data);
		if (!calculateMD5Checksum().equals(hash)) {
			throw new Exception("Broken image!");
		}
		System.out.println("Chunk " + name + "("
				+ Integer.toHexString(start) + " to "
				+ Integer.toHexString(start + size)
				+ ") read successfully");
	}
	private String calculateMD5Checksum() throws Exception {
		MessageDigest chk;
		String result = "";

		chk = MessageDigest.getInstance("MD5");

		chk.update(data);
		byte[] digest = chk.digest();
		for (byte b : digest) {
			result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		}

		return result;
	}
}

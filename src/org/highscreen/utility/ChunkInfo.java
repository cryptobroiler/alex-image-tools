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
	public long start;
	public long size;
	public String hash;
	public byte[] byteHash = null;
	public String name;

	public ChunkInfo(long start, long size, String hash, String name) {
		this.start = start;
		this.size = size;
		this.hash = hash;
		this.name = name;
	}

	public static ChunkInfo getInfoFromFile(String name) throws Exception {
		ChunkInfo info = new ChunkInfo(0, 0, "", "");
		RandomAccessFile file = new RandomAccessFile(name + ".bin", "r");
		info.start = 0;
		info.size = file.length();
		info.name = name;
		info.byteHash = calculateMD5Checksum(file);
		return info;

	}

	public void dumpToFileFromImage(RandomAccessFile image) throws Exception {
		int read;
		RandomAccessFile out = new RandomAccessFile(name + ".bin", "rw");
		MessageDigest chk = MessageDigest.getInstance("MD5");
		image.seek(start);
		if (size <= Integer.MAX_VALUE) {
			byte[] data = new byte[(int) size];
			image.read(data);
			chk.update(data);
			out.write(data);
		} else {
			long bytesRead = 0;
			while (bytesRead < size) {
				read = image.read();
				chk.update((byte) read);
				out.write(read);
				bytesRead++;
			}
		}
		byte[] digest = chk.digest();
		String md5 = "";
		for (byte b : digest) {
			md5 += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		}
		if (!md5.equals(hash)) {
			throw new Exception("Broken image!");
		}
		System.out.println("Chunk " + name + "(" + Long.toHexString(start)
				+ " to " + Long.toHexString(start + size)
				+ ") dumped successfully " + md5 + " " + hash);

	}

	public void writeDataToImage(RandomAccessFile image) throws Exception {
		System.out.println("Writing chunk to image...");
		RandomAccessFile in = new RandomAccessFile(name + ".bin", "r");
		image.seek(start);
		MessageDigest chk = MessageDigest.getInstance("MD5");
		size = in.length();
		if (size <= Integer.MAX_VALUE) {
			byte[] data = new byte[(int) size];
			in.read(data);
			chk.update(data);
			image.write(data);
		} else {
			long bytesRead = 0;
			int read;
			while (bytesRead < size) {
				read = image.read();
				chk.update((byte) read);
				image.write(read);
				bytesRead++;
			}
		}
		byte[] digest = chk.digest();
		String md5 = "";
		for (byte b : digest) {
			md5 += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		}
		System.out.println("Chunk " + name + "(" + Long.toHexString(start)
				+ " to " + Long.toHexString(start + size)
				+ ") written successfully, hash: " + md5);
	}

	private static byte[] calculateMD5Checksum(RandomAccessFile fis)
			throws Exception {
		MessageDigest chk;
		byte[] data = new byte[(int) fis.length()];
		chk = MessageDigest.getInstance("MD5");
		fis.read(data);
		return chk.digest(data);
	}
}

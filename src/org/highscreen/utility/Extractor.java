package org.highscreen.utility;

import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Vector;

public class Extractor {
	private int sections;

	private Vector<ChunkInfo> chunks = new Vector<ChunkInfo>();
	private String[] chunkNames = { "bootloader", "kernel", "system", "data",
			"package", "recovery", "cache" };
	private static RandomAccessFile image;

	public Extractor(String fileName) {
		try {
			image = new RandomAccessFile(fileName, "r");
			readHeader();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readHeader() throws Exception {
		sections = readLEInt();
		for (int i = 0; i < sections; i++) {
			chunks.add(new ChunkInfo(readLEInt(), readLEInt(), readMD5(),
					chunkNames[i]));
		}
	}

	private int readLEInt() throws Exception {
		return Integer.reverseBytes(image.readInt());
	}

	private String readMD5() throws Exception {
		byte[] md5 = new byte[16];
		String result = "";
		image.read(md5);
		for (byte b : md5) {
			result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	private static String getMD5Checksum(byte[] data) throws Exception {
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

	public void splitImage() {
		try {
			for (ChunkInfo c : chunks) {
				c.readData(image);
				c.dumpToFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		Extractor ex = new Extractor(args[0]);
		ex.splitImage();

	}

}

package org.highscreen.utility;

import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.Vector;

public class Extractor {
	private int sections;

	private Vector<ChunkInfo> chunks = new Vector<ChunkInfo>();
	protected static String[] chunkNames = { "bootloader", "kernel", "system",
			"data", "package", "recovery", "cache" };
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

	public void splitImage() {
		try {
			System.out.println("Splitting image");
			for (ChunkInfo c : chunks) {
				c.dumpToFileFromImage(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showUsage() {
		System.out.println("Usage:");
		System.out
				.println("tool pack <filename> -- create total.img from bin-files in current dir");
		System.out
				.println("tool unpack <filename> -- split total.img to bin-files");
	}

	public static void main(String[] args) {
		if (args.length == 2) {
			if (args[0].equals("pack")) {

				Packer packer = new Packer(args[1]);
				packer.makeImage();
			} else if (args[0].equals("unpack")) {
				Extractor ex = new Extractor(args[1]);
				ex.splitImage();
			}
		} else {
			showUsage();
		}

	}

}

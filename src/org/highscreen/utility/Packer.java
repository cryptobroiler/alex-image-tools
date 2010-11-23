package org.highscreen.utility;

import java.io.RandomAccessFile;
import java.util.Vector;

public class Packer {
	private static RandomAccessFile image;
	private Vector<ChunkInfo> chunks = new Vector<ChunkInfo>();
	private static final int headerSize = 172;

	public Packer(String fileName) {
		try {
			image = new RandomAccessFile(fileName, "rw");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeLEInt(int val)
			throws Exception {
		image.writeInt(Integer.reverseBytes(val));
	}

	private void writeHeader() throws Exception {
		System.out.println("Writing header:");
		int sections = chunks.size();
		writeLEInt(sections);
		for (ChunkInfo chunk : chunks) {
			writeLEInt((int) chunk.start);
			writeLEInt((int) chunk.size);
			image.write(chunk.byteHash);
		}
		System.out.println(image.length());
	}

	public void makeImage() {
		try {
			readChunks();
			calculateChunkBounds();
			writeHeader();
			for (ChunkInfo chunk : chunks) {
				chunk.writeDataToImage(image);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculateChunkBounds() {
		chunks.get(0).start = headerSize;
		for (int i = 0; i < chunks.size() - 1; i++) {
			chunks.get(i + 1).start = chunks.get(i).size
					+ chunks.get(i).start;
		}
	}

	private void readChunks() throws Exception {
		System.out.println("Reading ChunkInfo:");
		for (String name : Extractor.chunkNames) {
			System.out.println(name);
			chunks.add(ChunkInfo.getInfoFromFile(name));
		}
		System.out.println("ChunkInfo read successfully!");
	}
}

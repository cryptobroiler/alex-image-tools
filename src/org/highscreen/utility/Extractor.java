package org.highscreen.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Extractor {

	private int sections;
	private RandomAccessFile image;
	private int bootloaderStart;
	private int bootloaderSize;
	private byte[] bootloaderData;
	private String bootloaderMD5 = "";
	private int kernelStart;
	private int kernelSize;
	private byte[] kernelData;
	private String kernelMD5 = "";
	private int systemStart;
	private int systemSize;
	private byte[] systemData;
	private String systemMD5 = "";
	private int dataStart;
	private int dataSize;
	private byte[] dataData;
	private String dataMD5 = "";
	public Extractor(String fileName) {
		try {
			image = new RandomAccessFile(fileName, "r");
			readHeader();
		} catch (FileNotFoundException exception) {
			System.err.println(exception.toString());
		}

	}

	private void readHeader() {
		try {
			sections = readLEInt();
			bootloaderStart = readLEInt();
			bootloaderSize = readLEInt();
			bootloaderMD5 = readMD5();
			kernelStart = readLEInt();
			kernelSize = readLEInt();
			kernelMD5 = readMD5();
			systemStart = readLEInt();
			systemSize = readLEInt();
			systemMD5 = readMD5();
			dataStart = readLEInt();
			dataSize = readLEInt();
			dataMD5 = readMD5();
			

		} catch (IOException exception) {
			System.err.println(exception);
		}
	}

	private int readLEInt() throws IOException {
		return Integer.reverseBytes(image.readInt());
	}

	private String readMD5() throws IOException {
		byte[] md5 = new byte[16];
		String result = "";
		image.read(md5);
		for (byte b : md5) {
			result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	private static String getMD5Checksum(byte[] data) {
		MessageDigest chk;
		String result = "";
		try {
			chk = MessageDigest.getInstance("MD5");

			chk.update(data);
			byte[] digest = chk.digest();
			for (byte b : digest) {
				result += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void extractBootloader() {
		try {
			bootloaderData = new byte[bootloaderSize];
			image.seek(bootloaderStart);
			image.read(bootloaderData);
			System.out.println("Bootloader MD5 Original: " + bootloaderMD5);
			String chk = getMD5Checksum(bootloaderData);
			System.out.println("Bootloader MD5: "
					+ chk);
			if (chk.equals(bootloaderMD5)) {
				System.out.println("Bootloader extracted successfully: check bootloader.bin");
				RandomAccessFile boot = new RandomAccessFile("bootloader.bin", "rw");
				boot.write(bootloaderData);
			}
			else {
				System.err.println("Broken image!");
				throw new IOException();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void extractKernel() {
		try {
			kernelData = new byte[kernelSize];
			image.seek(kernelStart);
			image.read(kernelData);
			System.out.println("Kernel MD5 Original: " + kernelMD5);
			String chk = getMD5Checksum(kernelData);
			System.out.println("Kernel MD5: "
					+ chk);
			if (chk.equals(kernelMD5)) {
				System.out.println("Kernel extracted successfully: check kernel.bin");
				RandomAccessFile boot = new RandomAccessFile("kernel.bin", "rw");
				boot.write(kernelData);
			}
			else {
				System.err.println("Broken image!");
				throw new IOException();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extractSystem() {
		try {
			systemData = new byte[systemSize];
			image.seek(systemStart);
			image.read(systemData);
			System.out.println("System MD5 Original: " + systemMD5);
			String chk = getMD5Checksum(systemData);
			System.out.println("System MD5: " + chk);
			if (chk.equals(systemMD5)) {
				System.out
						.println("System extracted successfully: check system.bin");
				RandomAccessFile boot = new RandomAccessFile("system.bin", "rw");
				boot.write(systemData);
			} else {
				System.err.println("Broken image!");
				throw new IOException();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void extractData() {
		try {
			dataData = new byte[dataSize];
			image.seek(dataStart);
			image.read(dataData);
			System.out.println("Data MD5 Original: " + dataMD5);
			String chk = getMD5Checksum(dataData);
			System.out.println("Data MD5: " + chk);
			if (chk.equals(dataMD5)) {
				System.out
						.println("Data extracted successfully: check data.bin");
				RandomAccessFile boot = new RandomAccessFile("data.bin", "rw");
				boot.write(dataData);
			} else {
				System.err.println("Broken image!");
				throw new IOException();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Extractor ex = new Extractor(args[0]);
		ex.extractBootloader();
		ex.extractKernel();
		ex.extractSystem();
		ex.extractData();
	}

}

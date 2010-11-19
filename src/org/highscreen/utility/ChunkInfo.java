/**
 * 
 */
package org.highscreen.utility;

/**
 * @author deus
 * 
 */
public class ChunkInfo {
	public int start;
	public int size;
	public String hash;
	public String name;
	public ChunkInfo(int start,int size, String hash, String name){
		this.start = start;
		this.size = size;
		this.hash = hash;
		this.name = name;
	}
}

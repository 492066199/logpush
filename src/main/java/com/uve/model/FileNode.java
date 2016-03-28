package com.uve.model;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public class FileNode {
	private ByteBuffer bf;
	private long offset;
	private Future<Integer> cnt;
	
	public ByteBuffer getBf() {
		return bf;
	}
	public void setBf(ByteBuffer bf) {
		this.bf = bf;
	}
	public long getOffset() {
		return offset;
	}
	public void setOffset(long offset) {
		this.offset = offset;
	}
	public Future<Integer> getCnt() {
		return cnt;
	}
	public void setCnt(Future<Integer> cnt) {
		this.cnt = cnt;
	}
	
	
}

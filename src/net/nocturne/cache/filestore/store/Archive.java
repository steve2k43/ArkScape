// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Archive.java

package net.nocturne.cache.filestore.store;

import net.nocturne.cache.filestore.io.InputStream;
import net.nocturne.cache.filestore.io.OutputStream;
import net.nocturne.cache.filestore.util.bzip2.BZip2Compressor;
import net.nocturne.cache.filestore.util.bzip2.BZip2Decompressor;
import net.nocturne.cache.filestore.util.crc32.CRC32HGenerator;
import net.nocturne.cache.filestore.util.gzip.GZipCompressor;
import net.nocturne.cache.filestore.util.gzip.GZipDecompressor;
import net.nocturne.cache.filestore.util.whirlpool.Whirlpool;
import net.nocturne.cache.filestore.utils.Constants;

// Referenced classes of package com.alex.store:
//            MainFile

public class Archive {

	private int id;
	private int revision;
	private int compression;
	private byte[] data;
	private int[] keys;
	private int hash;

	protected Archive(int id, byte[] archive, int[] keys) {
		this.id = id;
		this.keys = keys;
		decompress(archive);

	}

	public Archive(int id, int compression, int revision, byte[] data) {
		this.id = id;
		this.compression = compression;
		this.revision = revision;
		this.data = data;
	}

	public byte[] compress() {
		OutputStream stream = new OutputStream();
		stream.writeByte(compression);
		byte[] compressedData;
		switch (compression) {
		case Constants.NO_COMPRESSION: // no compression
			compressedData = data;
			stream.writeInt(data.length);
			break;
		case Constants.BZIP2_COMPRESSION:
			compressedData = null; // TODO
			compressedData = BZip2Compressor.compress(data);
			stream.writeInt(compressedData.length);
			stream.writeInt(data.length);
			// throw new RuntimeException("BZIP2_COMPRESSION NOT ADDED");
		default: // gzip
			compressedData = GZipCompressor.compress(data);
			stream.writeInt(compressedData.length);
			stream.writeInt(data.length);
			break;
		}
		stream.writeBytes(compressedData);
		if (keys != null && keys.length == 4)
			stream.encodeXTEA(keys, 5, stream.getOffset());
		if (revision != -1)
			stream.writeShort(revision);
		byte[] compressed = new byte[stream.getOffset()];
		stream.setOffset(0);
		stream.getBytes(compressed, 0, compressed.length);
		return compressed;
	}

	private void decompress(byte[] archive) {
		InputStream stream = new InputStream(archive);
		if (keys != null && keys.length == 4)
			stream.decodeXTEA(keys);
		compression = stream.readUnsignedByte();
		int compressedLength = stream.readInt();
		if (compressedLength < 0
				|| compressedLength > Constants.MAX_VALID_ARCHIVE_LENGTH)
			throw new RuntimeException("INVALID ARCHIVE HEADER");
		switch (compression) {
		case Constants.NO_COMPRESSION: // no compression
			data = new byte[compressedLength];
			checkRevision(compressedLength, archive, stream.getOffset());
			stream.readBytes(data, 0, compressedLength);
			break;
		case Constants.BZIP2_COMPRESSION: // bzip2
			int length = stream.readInt();
			if (length <= 0) {
				data = null;
				break;
			}
			data = new byte[length];
			checkRevision(compressedLength, archive, stream.getOffset());
			BZip2Decompressor.decompress(data, archive, compressedLength, 9);
			break;
		default: // gzip
			length = stream.readInt();
			if (length <= 0 || length > 1000000000) {
				data = null;
				break;
			}
			data = new byte[length];
			checkRevision(compressedLength, archive, stream.getOffset());
			if (!GZipDecompressor.decompress(stream, data))
				data = null;
			break;
		}
	}

	private void checkRevision(int compressedLength, byte[] archive, int o) {
		InputStream stream = new InputStream(archive);
		int offset = stream.getOffset();
		if (stream.getLength() - (compressedLength + o) >= 2) {
			stream.setOffset(stream.getLength() - 2);
			revision = stream.readUnsignedShort();
			stream.setOffset(offset);
		} else
			revision = -1;

	}

	public Object[] editNoRevision(byte[] data, MainFile mainFile) {
		this.data = data;
		if (compression == Constants.BZIP2_COMPRESSION)
			compression = Constants.GZIP_COMPRESSION;
		byte[] compressed = compress();
		if (!mainFile.putArchiveData(id, compressed))
			return null;
		return new Object[] { CRC32HGenerator.getHash(compressed),
				Whirlpool.getHash(compressed, 0, compressed.length) };
	}

	public int getId() {
		return id;
	}

	public byte[] getData() {
		return data;
	}

	public int getDecompressedLength() {
		return data.length;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

	public int getCompression() {
		return compression;
	}

	public int[] getKeys() {
		return keys;
	}

	public int getHash() {

		return hash;
	}

	public void setHash(int hash) {

		this.hash = hash;
	}

	public void setKeys(int[] keys) {
		this.keys = keys;
	}
}
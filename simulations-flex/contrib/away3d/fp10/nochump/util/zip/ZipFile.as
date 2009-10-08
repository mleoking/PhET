/*
nochump.util.zip.ZipFile
Copyright (C) 2007 David Chang (dchang@nochump.com)

This file is part of nochump.util.zip.

nochump.util.zip is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

nochump.util.zip is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/
package nochump.util.zip {

	import flash.events.EventDispatcher;
	import flash.events.ProgressEvent;
	import flash.utils.ByteArray;
	import flash.utils.Dictionary;
	import flash.utils.Endian;
	import flash.utils.IDataInput;

	[Event(name="entryParsed",		type="nochump.util.zip.ZipEvent")]
	[Event(name="entryParseError",	type="nochump.util.zip.ZipErrorEvent")]
	[Event(name="progress",			type="flash.events.ProgressEvent")]

	/**
	 * This class represents a Zip archive.  You can ask for the contained
	 * entries, or get an input stream for a file entry.  The entry is
	 * automatically decompressed.
	 * 
	 * @author David Chang
	 */
	public class ZipFile extends EventDispatcher {
		
		private var buf:ByteArray; // data from which zip entries are read.
		private var entryList:Array;
		private var entryTable:Dictionary;
		private var locOffsetTable:Dictionary;
		
		/**
		 * Opens a Zip file reading the given data.
		 * 
		 * @param data
		 */
		public function ZipFile(data:IDataInput) {
			buf = new ByteArray();
			buf.endian = Endian.LITTLE_ENDIAN;
			data.readBytes(buf);
			readEntries();
		}
		
		/**
		 * Returns an array of all Zip entries in this Zip file.
		 */
		public function get entries():Array {
			return entryList;
		}
		
		/**
		 * Returns the number of entries in this zip file.
		 */
		public function get size():uint {
			return entryList.length;
		}
		
		/**
		 * Searches for a zip entry in this archive with the given name.
		 *
		 * @param name the name. May contain directory components separated by
		 * slashes ('/').
		 * @return the zip entry, or null if no entry with that name exists.
		 */
		public function getEntry(name:String):ZipEntry {
			return entryTable[name];
		}
		
		/**
		 * Creates a byte array reading the given zip entry as
		 * uncompressed data.  Normally zip entry should be an entry
		 * returned by getEntry() or entries().
		 * 
		 * @param entry the entry to create a byte array for.
		 * @return the byte array, or null if the requested entry does not exist.
		 */
		public function getInput(entry:ZipEntry):ByteArray {
			// extra field for local file header may not match one in central directory header
			buf.position = locOffsetTable[entry.name] + ZipConstants.LOCHDR - 2;
			var len:uint = buf.readShort(); // extra length
			buf.position += entry.name.length + len;
			var b1:ByteArray = new ByteArray();
			// read compressed data
			if(entry.compressedSize > 0) buf.readBytes(b1, 0, entry.compressedSize);
			switch(entry.method) {
				case ZipConstants.STORED:
					return b1;
					break;
				case ZipConstants.DEFLATED:
					/*
					if(Security.sandboxType == Security.APPLICATION) {
						// apollo environment
						b1.inflate();
						return b1;
					}
					/**/
					var b2:ByteArray = new ByteArray();
					var inflater:Inflater = new Inflater();
					inflater.setInput(b1);
					inflater.inflate(b2);
					dispatchEvent( new ZipEvent(ZipEvent.ENTRY_PARSED, false, false, b2) );
					return b2;
					break;
				default:
					throw new ZipError("invalid compression method");
			}
			return null;
		}

		public function parseInput(entry:ZipEntry):void {
			// extra field for local file header may not match one in central directory header
			buf.position = locOffsetTable[entry.name] + ZipConstants.LOCHDR - 2;
			var len:uint = buf.readShort(); // extra length
			buf.position += entry.name.length + len;
			var b1:ByteArray = new ByteArray();
			// read compressed data
			if(entry.compressedSize > 0) buf.readBytes(b1, 0, entry.compressedSize);
			switch(entry.method) {
				case ZipConstants.STORED:
					dispatchEvent( new ZipEvent(ZipEvent.ENTRY_PARSED, false, false, b1) );
					break;
				case ZipConstants.DEFLATED:
					var inflater:Inflater = new Inflater();
					inflater.addEventListener(ZipEvent.ENTRY_PARSED, onZipEntryParsed);
					inflater.addEventListener(ProgressEvent.PROGRESS, onZipEntryProgress);
					var b2:ByteArray = new ByteArray();
					inflater.setInput(b1);
					inflater.queuedInflate(b2);
					break;
				default:
					throw new ZipError("invalid compression method");
			}
		}

		private function onZipEntryParsed(event:ZipEvent):void {
			dispatchEvent( new ZipEvent(ZipEvent.ENTRY_PARSED, false, false, event.entry) );
		}

		private function onZipEntryProgress(event:ProgressEvent):void {
			dispatchEvent( new ProgressEvent(ProgressEvent.PROGRESS, false, false, event.bytesLoaded, event.bytesTotal ) );
		}

		/**
		 * Read the central directory of a zip file and fill the entries
		 * array.  This is called exactly once when first needed.
		 */
		private function readEntries():void {
			readEND();
			entryTable = new Dictionary();
			locOffsetTable = new Dictionary();
			// read cen entries
			for(var i:uint = 0; i < entryList.length; i++) {
				var tmpbuf:ByteArray = new ByteArray();
				tmpbuf.endian = Endian.LITTLE_ENDIAN;
				buf.readBytes(tmpbuf, 0, ZipConstants.CENHDR);
				if(tmpbuf.readUnsignedInt() != ZipConstants.CENSIG) throw new ZipError("invalid CEN header (bad signature)");
				// handle filename
				tmpbuf.position = ZipConstants.CENNAM;
				var len:uint = tmpbuf.readUnsignedShort();
				if(len == 0) throw new ZipError("missing entry name");
				var e:ZipEntry = new ZipEntry(buf.readUTFBytes(len));
				// handle extra field
				len = tmpbuf.readUnsignedShort();
				e.extra = new ByteArray();
				if(len > 0) buf.readBytes(e.extra, 0, len);
				// handle file comment
				buf.position += tmpbuf.readUnsignedShort();
				// now get the remaining fields for the entry
				tmpbuf.position = ZipConstants.CENVER;
				e.version = tmpbuf.readUnsignedShort();
				e.flag = tmpbuf.readUnsignedShort();
				if ((e.flag & 1) == 1) throw new ZipError("encrypted ZIP entry not supported");
				e.method = tmpbuf.readUnsignedShort();
				e.dostime = tmpbuf.readUnsignedInt();
			    e.crc = tmpbuf.readUnsignedInt();
			    e.compressedSize = tmpbuf.readUnsignedInt();
			    e.size = tmpbuf.readUnsignedInt();
			    // add to entries and table
			    entryList[i] = e;
			    entryTable[e.name] = e;
			    // loc offset
			    tmpbuf.position = ZipConstants.CENOFF;
			    locOffsetTable[e.name] = tmpbuf.readUnsignedInt();
			}
		}
		
		/**
		 * Reads the total number of entries in the central dir and
		 * positions buf at the start of the central directory.
		 */
		private function readEND():void {
			var b:ByteArray = new ByteArray();
			b.endian = Endian.LITTLE_ENDIAN;
			buf.position = findEND();
			buf.readBytes(b, 0, ZipConstants.ENDHDR);
			b.position = ZipConstants.ENDTOT;
			entryList = new Array(b.readUnsignedShort());
			b.position = ZipConstants.ENDOFF;
			buf.position = b.readUnsignedInt();
		}
		
		private function findEND():uint {
			var i:uint = buf.length - ZipConstants.ENDHDR;
			var n:uint = Math.max(0, i - 0xffff); // 0xffff is max zip file comment length
			// TODO: issue when n is 0 and ENDSIG not found (since variable i cannot be negative)
			for(i; i >= n; i--) {
				if(buf[i] != 0x50) continue; // quick check that the byte is 'P'
				buf.position = i;
				if(buf.readUnsignedInt() == ZipConstants.ENDSIG) return i;
			}
			throw new ZipError("invalid zip");
			return 0;
		}
		
	}
	
}
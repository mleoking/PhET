/*
nochump.util.zip.ZipError
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
	
	import flash.errors.IOError;
	
	/**
	 * Thrown during the creation or input of a zip file.
	 */
	public class ZipError extends IOError {
		
		public function ZipError(message:String = "", id:int = 0) {
			super(message, id);
		}
		
	}
	
}

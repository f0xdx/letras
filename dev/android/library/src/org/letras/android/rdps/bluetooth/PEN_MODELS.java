/*******************************************************************************
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is MundoCore Java.
 * 
 * The Initial Developer of the Original Code is Telecooperation Group,
 * Department of Computer Science, Technische Universität Darmstadt.
 * Portions created by the Initial Developer are
 * Copyright © 2009-2011 the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 * Felix Heinrichs
 * Niklas Lochschmidt
 * Jannik Jochem
 ******************************************************************************/
package org.letras.android.rdps.bluetooth;

import org.letras.android.rdps.bluetooth.decoder.IO2StreamDecoder;
import org.letras.android.rdps.bluetooth.decoder.SU1BStreamDecoder;

public enum PEN_MODELS {
	NOKIA_SU_1B("ANOTO STREAMING",SU1BStreamDecoder.class, "Nokia SU-1B"),
	LOGITECH_IO2("ANOTOSTREAMING",IO2StreamDecoder.class, "Logitech IO2");
	
	Class<?> decoderClass;
	String sppChannelName;
	String friendlyName;
	
	private PEN_MODELS(String sppChannelName, Class<?> decoderClass, String friendlyName) {
		this.decoderClass = decoderClass;
		this.sppChannelName = sppChannelName;
		this.friendlyName = friendlyName;
	}
	
	public static String[] penModels() {
		String[] result = new String[values().length];
		for (int i = 0; i < values().length; i++) {
			result[i] = values()[i].friendlyName;
		}
		return result;
	}
	
	public static PEN_MODELS getPenModelWithFriendlyName(String friendlyName) {
		for (PEN_MODELS model : values()) {
			if (model.friendlyName.equals(friendlyName))
				return model;
		}
		
		return null;
	}
}

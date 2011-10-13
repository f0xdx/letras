package org.letras.psi.semantic;

import org.mundo.annotation.mcSerialize;
import org.mundo.rt.TypedArray;

/**
 * The {@link HandwritingRecognitionResult} is created by a Handwriting-
 * Recognition-Service when a set of traces has been recognized.
 * 
 * A list of {@link #alternateResults} is optional.
 * @author niklas
 */
@mcSerialize
public class HandwritingRecognitionResult {

	/**
	 * the penId of the pen who wrote the recognized set of traces
	 */
	public String penId;
	
	/**
	 * the GUIDs of the traces that have been recognized 
	 */
	public TypedArray traceGUID;
	
	/**
	 * the best result that the recognizer found
	 */
	public String topResult;
	
	/**
	 * the alternate results of the recognizer
	 */
	public TypedArray alternateResults;
}

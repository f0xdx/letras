package org.letras.psi.semantic;

import org.mundo.annotation.mcMethod;
import org.mundo.annotation.mcRemote;

/**
 * This interface declares the methods provided by a HandwritingRecognition-Services
 * It abstracts from the concrete underlying Recognition Engine.
 * @author niklas
 */
@mcRemote
public interface IHandwritingRecognitionService {

	public final int LANGUAGE_GERMAN = 1031;
	public final int LANGUAGE_ENGLISH = 1033;
	
	/**
	 * Add the channel to the set of region-channels on which ink will be recognized 
	 * and converted into {@link HandwritingRecognitionResult}s
	 * The results will then be send back over the same channel.
	 * 
	 * @param channel the region-channel on which to recognize the ink
	 */
	@mcMethod
	public void startRecognitionFor(String channel);
	
	/**
	 * Same as {@link #startRecognitionFor(String)} but adding a hint to use a specific
	 * language when recognizing the string
	 * 
	 * @param channel the region-channel on which to recognize the ink
	 * @param languageId the languageId (e.g. {@link #LANGUAGE_ENGLISH},...)
	 * @return true if the language is available, false otherwise
	 */
	@mcMethod
	public boolean startRecognitionFor(String channel, int languageId);
	
	/**
	 * Stop recognition on the specified region-channel
	 * @param channel stop listening on this region-channel
	 */
	@mcMethod
	public void stopRecognitionFor(String channel);
	
}

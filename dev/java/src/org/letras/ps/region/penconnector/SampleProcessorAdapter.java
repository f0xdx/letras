package org.letras.ps.region.penconnector;

import org.letras.api.pen.IPen.IPenListener;
import org.letras.api.pen.IPenEvent;
import org.letras.api.pen.IPenSample;
import org.letras.api.pen.IPenState;

public class SampleProcessorAdapter implements IPenListener {

	private final ISampleProcessor sampleProcessor;

	public SampleProcessorAdapter(ISampleProcessor sampleProcessor) {
		this.sampleProcessor = sampleProcessor;
	}

	@Override
	public void receivePenEvent(IPenEvent penEvent) {
		// For now we treat all events accept DOWN as UP events
		if ((penEvent.getState() & IPenState.DOWN) == IPenState.DOWN)
			sampleProcessor.penDown();
		else
			sampleProcessor.penUp();
	}

	@Override
	public void receivePenSample(IPenSample penSample) {
		this.sampleProcessor.handleSample(penSample);
	}

}

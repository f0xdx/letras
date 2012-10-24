package org.letras.ps.region.penconnector;

import org.letras.api.pen.IPen.IPenListener;
import org.letras.api.pen.IPenState;
import org.letras.api.pen.PenEvent;
import org.letras.api.pen.PenSample;

public class SampleProcessorAdapter implements IPenListener {

	private final ISampleProcessor sampleProcessor;

	public SampleProcessorAdapter(ISampleProcessor sampleProcessor) {
		this.sampleProcessor = sampleProcessor;
	}

	@Override
	public void receivePenEvent(PenEvent penEvent) {
		// For now we treat all events accept DOWN as UP events
		if ((penEvent.getNewState() & IPenState.DOWN) == IPenState.DOWN)
			sampleProcessor.penDown();
		else
			sampleProcessor.penUp();
	}

	@Override
	public void receivePenSample(PenSample penSample) {
		this.sampleProcessor.handleSample(penSample);
	}

}

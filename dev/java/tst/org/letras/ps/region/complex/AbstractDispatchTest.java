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
package org.letras.ps.region.complex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.letras.ps.region.RegionProcessor;
import org.letras.psi.iregion.IDigitalInkConsumer;
import org.letras.psi.iregion.IRegion;
import org.letras.psi.iregion.RegionEvent;
import org.letras.psi.iregion.RegionMessage;
import org.letras.psi.iregion.RegionSample;
import org.letras.tools.penrecorder.PenRecording;
import org.letras.tools.penrecorder.PenRecordingPlayer;
import org.letras.util.region.document.RegionDocumentRegionSet;
import org.letras.util.region.document.RegionDocument;
import org.letras.util.region.document.RegionDocumentPublisher;
import org.letras.util.region.document.RegionDocumentReceiver;
import org.mundo.rt.Logger;
import org.mundo.rt.Mundo;

public abstract class AbstractDispatchTest implements IDigitalInkConsumer {
	private static RegionProcessor rp;
	private BlockingQueue<SourceRegionMessage> messages;
	private List<MessageConsumer> consumers;
	private RegionDocument doc;
	private PenRecordingPlayer penRecordingPlayer;
	private PenRecording penRecording;
	private static Logger log; 
	
	private class SourceRegionMessage {
		public RegionMessage message;
		public IRegion source;
		
		public SourceRegionMessage(RegionMessage message, IRegion source) {
			this.message = message;
			this.source = source;
		}
		
		@Override
		public String toString() {
			return source.channel() + " <= " + message;
		}
		
	}
	
	private interface MessageConsumer {
		boolean isSatisified();
		boolean consume(SourceRegionMessage message);
	}
	
	private class SamplesConsumer implements MessageConsumer {
		private String[] regions;
		private HashMap<String, Integer> sampleCounts;
		private int currentStep = 0;
		private int steppedRegions = 0;

		public SamplesConsumer(String ... regions) {
			this.regions = regions;
			this.sampleCounts = new HashMap<String, Integer>();
			for (String regionName: regions)
				sampleCounts.put(regionName, 0);
		}

		@Override
		public boolean consume(SourceRegionMessage message) {
			if (!(message.message instanceof RegionSample))
				return false;
			String region = message.source.channel();
			Integer sampleCount = sampleCounts.get(region);
			if (sampleCount == null) {
				// don't know region, do not want
				return false;
			}
			if (sampleCount > currentStep && steppedRegions != 0) {
				// this region has already received a sample while others didn't yet - refuse the sample
				return false;
			}
			sampleCounts.put(region, sampleCount + 1);
			steppedRegions++;
			if (steppedRegions == regions.length) {
				// all regions have received a sample, ready to process the next sample over all regions
				steppedRegions = 0;
				currentStep++;
			}
			return true;
		}

		@Override
		public boolean isSatisified() {
			return steppedRegions == 0;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("sample(");
			boolean first = true;
			for (String region: regions) {
				if (first) {
					builder.append(region);
					first = false;
				} else {
					builder.append(", ");
					builder.append(region);
				}
			}
			builder.append(")+");
			return builder.toString();
		}
	}
	
	private class EventConsumer implements MessageConsumer {

		private int type;
		private HashSet<String> continuesRegions;
		private HashSet<String> nonContinuesRegions;
		private String[] originalNonContinuesRegions;
		private String[] originalContinuesRegions;

		public EventConsumer(int type, String[] continuesRegions, String[] nonContinuesRegions) {
			this.type = type;
			this.originalContinuesRegions = continuesRegions;
			this.originalNonContinuesRegions = nonContinuesRegions;
			this.continuesRegions = new HashSet<String>(Arrays.asList(continuesRegions));
			this.nonContinuesRegions = new HashSet<String>(Arrays.asList(nonContinuesRegions));
		}
		
		@Override
		public boolean consume(SourceRegionMessage message) {
			if (!(message.message instanceof RegionEvent))
				return false;
			RegionEvent event = (RegionEvent) message.message;
			if ((event.getType() & (~RegionEvent.CONTINUES)) != type)
				return false;
			if (event.continues(event.getGuid()) && continuesRegions.contains(message.source.channel())) {
				continuesRegions.remove(message.source.channel());
				return true;
			} else if (!event.continues(event.getGuid()) && nonContinuesRegions.contains(message.source.channel())) {
				nonContinuesRegions.remove(message.source.channel());
				return true;
			}
			return false;
		}

		@Override
		public boolean isSatisified() {
			return continuesRegions.isEmpty() && nonContinuesRegions.isEmpty();
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("event(");
			if (type == RegionEvent.PEN_DOWN)
				builder.append("PEN_DOWN, ");
			else if (type == RegionEvent.PEN_UP)
				builder.append("PEN_UP, ");
			else if (type == RegionEvent.TRACE_START)
				builder.append("TRACE_START, ");
			else if (type == RegionEvent.TRACE_END)
				builder.append("TRACE_END, ");
			else
				builder.append("!Invalid Event!, ");
			builder.append("cont{");
			boolean first = true;
			for (String region: originalContinuesRegions) {
				if (first) {
					builder.append(region);
					first = false;
				} else {
					builder.append(", ");
					builder.append(region);
				}
			}
			builder.append("}, nonCont{");
			first = true;
			for (String region: originalNonContinuesRegions) {
				if (first) {
					builder.append(region);
					first = false;
				} else {
					builder.append(", ");
					builder.append(region);
				}
			}
			builder.append("})");
			return builder.toString();
		}
		
	}
	
	public AbstractDispatchTest(String regionFileName, String penRecordingName) throws Exception {
		messages = new LinkedBlockingQueue<SourceRegionMessage>();
		consumers = new ArrayList<MessageConsumer>();
		penRecordingPlayer = new PenRecordingPlayer();
		penRecordingPlayer.setServiceZone("lan");
		penRecording = new PenRecording(new File(penRecordingName));
		Mundo.registerService(penRecordingPlayer);
		loadAndPublish(regionFileName);
		// give the RPS some time to discover the player and the regions
		Thread.sleep(1000);
	}
	
	public AbstractDispatchTest(RegionDocumentRegionSet regions, String penRecordingName) throws Exception {
		messages = new LinkedBlockingQueue<SourceRegionMessage>();
		consumers = new ArrayList<MessageConsumer>();
		penRecordingPlayer = new PenRecordingPlayer();
		penRecordingPlayer.setServiceZone("lan");
		penRecording = new PenRecording(new File(penRecordingName));
		Mundo.registerService(penRecordingPlayer);
		Mundo.registerService(regions);
		RegionDocumentReceiver rec = new RegionDocumentReceiver(regions.getDocument());
		rec.setActiveConsumer(this);
		Thread.sleep(1000);
	}
	
	@BeforeClass
	public static void initializeRPS() {
		Mundo.init();
		rp = RegionProcessor.getInstance();
		log = Logger.getLogger(AbstractDispatchTest.class);
	}
	
	@Test
	public void testDispatch() throws InterruptedException {
		setupConsumers();
		penRecordingPlayer.play(penRecording);
		int i = 0;
		MessageConsumer consumer = consumers.get(i);
		SourceRegionMessage message = null;
		try {
		while ((message = messages.poll(10, TimeUnit.SECONDS)) != null) {
			System.out.println("Message received: " + message.toString());
			if (!consumer.consume(message)) {
				assertTrue("Consumer " + consumer + " is not satisfied and refused to consume message " + message + ".", consumer.isSatisified());
				i++;
				assertTrue("Consumer " + consumer + " refused to consume message " + message + " and no consumers are left.", i < consumers.size());
				consumer = consumers.get(i);
				assertTrue("Consumer " + consumer + " refused to consume message " + message + ".", consumer.consume(message));
				if (i == consumers.size() - 1 && consumer.isSatisified())
					break;
			}
		}
		assertTrue("No more messages arrived while consumers were still left in the queue. Processed " + i + " messages.", i == consumers.size() - 1);
		assertTrue("Last consumer " + consumer + " is not satisfied.", consumer.isSatisified());
		System.out.println("Test executed successfully.");
		} catch (AssertionError e) {
			Thread.sleep(500);
			System.out.println("Messages left in queue:");
			while (!messages.isEmpty())
				System.out.println(messages.remove());
			System.out.println("----- end messages ----");
			throw new AssertionError(e);
		}
	}

	protected void consumeSamples(String ... regions) {
		consumers.add(new SamplesConsumer(regions));
	}
	
	protected void consumeEvents(int type, String[] continuesRegions, String[] nonContinuesRegions) {
		consumers.add(new EventConsumer(type, continuesRegions, nonContinuesRegions));
	}
	
	protected abstract void setupConsumers();

	private void loadAndPublish(String regionFileName) throws Exception {
		doc = RegionDocument.fromFile(new File(regionFileName));
		RegionDocumentPublisher pub = new RegionDocumentPublisher(doc);
		RegionDocumentReceiver rec = new RegionDocumentReceiver(doc);
		rec.setActiveConsumer(this);
	}

	@AfterClass
	public static void tearDownRPS() {
		Mundo.shutdown();
	}

	@Override
	public void consume(IRegion source, RegionSample regionSample) {
		try {
			messages.put(new SourceRegionMessage(regionSample, source));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void consume(IRegion source, RegionEvent regionEvent) {
		try {
			messages.put(new SourceRegionMessage(regionEvent, source));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}

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
package org.letras.api.region;

import org.mundo.annotation.mcSerialize;
import org.mundo.rt.GUID;

/**
 * Event type used to describe digital ink data structures. This kind
 * of events is communicated to the interested listeners on the region
 * channels to enable them to process the digital ink data structures.
 * 
 * @author felix_h
 * @author jannik
 * @version 0.2
 */
@mcSerialize
public class RegionEvent {

	// defaults
	
	/**
	 * This indicates that the pen is now down.
	 */
	public static final int PEN_DOWN 	= 0x00000001;
	
	/**
	 * This indicates that the pen is now up.
	 */
	public static final int PEN_UP 		= 0x00000002;
	
	/**
	 * This indicates the start of a digital ink trace.
	 */
	public static final int TRACE_START = 0x00000004;
	
	/**
	 * This indicates the end of a digital ink trace.
	 */
	public static final int TRACE_END 	= 0x00000008;
	
	/**
	 * This indicates that the digital ink trace 
	 * continues another trace.
	 */
	public static final int CONTINUES 	= 0x00000010;
	
	// members

	/**
	 * The type of this {@link RegionEvent}. 
	 */
	protected int type;
	
	/**
	 * Globally unique identifier used to identify digital ink
	 * data structures that belong together (i.e. structures where
	 * a region boundary was crossed)
	 */
	protected GUID guid;
		
	/**
	 * PenID of the pen from which this sample originates
	 */
	protected String penID;
	
	// constructors

	/**
	 * No-argument constructor used for serialization.
	 */
	public RegionEvent() {
		
	}
	
	/**
	 * Regular constructor initializing the members.
	 * 
	 * @param type		the type if this event
	 * @param guid		globally unique identifier
	 * @param penID		the Identifier of the pen the event originates from
	 */
	public RegionEvent(int type, GUID guid, String penID) {
		this.type = type;
		this.guid = guid;
		this.penID = penID;
	}
	
	// methods

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the guid
	 */
	public GUID getGuid() {
		return guid;
	}
	
	/**
	 * @return thePenID()
	 */
	public String getPenID() {
		return penID;
	}
	
	/**
	 * @return true iff this is a pen down event
	 */
	public boolean penDown() {
		return ((this.type & PEN_DOWN) == PEN_DOWN);
	}
	
	/**
	 * @return true iff this is a pen up event
	 */
	public boolean penUp() {
		return ((this.type & PEN_UP) == PEN_UP);
	}
	
	/**
	 * Checks whether this event indicates a trace start.
	 * 
	 * @return <code>true</code> iff this is a trace start event
	 */
	public boolean traceStart() {
		return ((this.type & TRACE_START) == TRACE_START);
	}
	
	/**
	 * Checks whether this event indicates a trace end.
	 * 
	 * @return <code>true</code> iff this is a trace end event
	 */
	public boolean traceEnd() {
		return ((this.type & TRACE_END) == TRACE_END);
	}
	
	/**
	 * Checks whether this event indicates a continuing trace
	 * for the trace with the given {@link GUID}.
	 * 
	 * @param guid	guid of a trace
	 * @return 		<code>true</code> iff this event describes a continuing
	 * 				trace to the trace with the provided {@link GUID}
	 */
	public boolean continues(GUID guid) {
		return ((guid != null) &&
				((this.type & CONTINUES) == CONTINUES) &&
				(guid.equals(this.guid)));
	}
	
	// convenience methods for creation
	
	
	/**
	 * Creates an {@link RegionEvent} that indicates that the pen is down.
	 * @param guid guid to link an up event to this down event
	 * @param penId	the Identifier of the pen the event originates from
	 * @return pen down event
	 */
	public static RegionEvent createPenDownEvent(GUID guid, String penId) {
		return new RegionEvent(PEN_DOWN, guid, penId);
	}
	
	/**
	 * Creates an {@link RegionEvent} that indicates that the pen is up.
	 * @param guid guid to link this up event to an earlier down event
	 * @param penId	the Identifier of the pen the event originates from
	 * @return pen up event
	 */
	public static RegionEvent createPenUpEvent(GUID guid, String penId) {
		return new RegionEvent(PEN_UP, guid, penId);
	}
	
	/**
	 * Creates a not continuing {@link RegionEvent} indicating the
	 * start of a trace.
	 * 
	 * @param guid 		to use for the trace
	 * @param penId		the Identifier of the pen the event originates from
	 * @return 	trace start event (not continuing)
	 */
	public static RegionEvent createTraceStartEvent(GUID guid, String penId) {
		return new RegionEvent(TRACE_START, guid, penId);
	}
	
	/**
	 * Creates a not continuing {@link RegionEvent} indicating the
	 * end of a trace.
	 * 
	 * @param	guid to use for the trace
	 * @param 	penId	the Identifier of the pen the event originates from
	 * @return 	trace end event (not continuing)
	 */
	public static RegionEvent createTraceEndEvent(GUID guid, String penId) {
		return new RegionEvent(TRACE_END, guid, penId);
	}

	/**
	 * Creates a continuing {@link RegionEvent} indicating the
	 * start of a trace.
	 * 
	 * @param	guid to use for the trace
	 * @param 	penId	the Identifier of the pen the event originates from
	 * @return 	trace start event (continuing)
	 */
	public static RegionEvent createContinuingTraceStartEvent(GUID guid, String penId) {
		return new RegionEvent(TRACE_START | CONTINUES , guid, penId);
	}
	
	/**
	 * Creates a continuing {@link RegionEvent} indicating the
	 * end of a trace.
	 * 
	 * @param	guid to use for the trace
	 * @param 	penId	the Identifier of the pen the event originates from
	 * @return 	trace end event (continuing)
	 */
	public static RegionEvent createContinuingTraceEndEvent(GUID guid, String penId) {
		return new RegionEvent(TRACE_END | CONTINUES , guid, penId);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("event(");
		if (penDown()) builder.append("PEN_DOWN, ");
		if (penUp()) builder.append("PEN_UP, ");
		if (traceStart()) builder.append("TRACE_START, ");
		if (traceEnd()) builder.append("TRACE_END, ");
		builder.append("GUID=");
		builder.append(guid);
		builder.append(", ");
		if (continues(guid))
			builder.append("continuous)");
		else
			builder.append("non-continuous)");
		builder.append(" from pen '");
		builder.append(penID);
		builder.append("'");
		return builder.toString();
	}
}

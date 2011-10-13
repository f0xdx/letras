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
package org.letras.tools.regionmonitor.ink.view;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * Factory class to create the used line strokes.
 *
 * @author Felix Heinrichs <felix.heinrichs@gmail.com>
 */
public class LineStroke extends BasicStroke {

	public static enum Strokes {
		SMALL_BLUE, MEDIUM_BLUE, LARGE_BLUE,
		SMALL_WHITE, MEDIUM_WHITE, LARGE_WHITE,
		SMALL_BLACK, MEDIUM_BLACK, LARGE_BLACK;
	}

	// defaults

	private static final float SMALL_SIZE = 1.5f;
	private static final float MEDIUM_SIZE = 3.0f;
	private static final float LARGE_SIZE= 6.0f;

	private static final int STROKE_CAP = BasicStroke.CAP_ROUND;
	private static final int STROKE_JOIN = BasicStroke.JOIN_ROUND;

	// members

	private Color strokeColor;

	public Color getStrokeColor() {
		return strokeColor;
	}

	// constructors

	protected LineStroke(float width, int cap, int join) {
		super(width, cap, join);
	}

	// methods

	public static LineStroke createLineStroke(float width, Color c) {
		LineStroke result = new LineStroke(width, STROKE_CAP, STROKE_JOIN);
		result.strokeColor = c;
		return result;
	}
	
	public static LineStroke createLineStroke(LineStroke.Strokes type) {
		assert (type!=null);
		LineStroke result =  null;
		switch (type) {
			case SMALL_BLUE:	result = new LineStroke(SMALL_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.BLUE;
								break;
			case MEDIUM_BLUE:	result = new LineStroke(MEDIUM_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.BLUE;
								break;
			case LARGE_BLUE:	result = new LineStroke(LARGE_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.BLUE;
								break;
			case SMALL_WHITE:	result = new LineStroke(SMALL_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.WHITE;
								break;
			case MEDIUM_WHITE:	result = new LineStroke(MEDIUM_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.WHITE;
								break;
			case LARGE_WHITE:	result = new LineStroke(LARGE_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.WHITE;
								break;
			case SMALL_BLACK:	result = new LineStroke(SMALL_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.BLACK;
								break;
			case MEDIUM_BLACK:	result = new LineStroke(MEDIUM_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.BLACK;
								break;
			case LARGE_BLACK:	result = new LineStroke(LARGE_SIZE, STROKE_CAP, STROKE_JOIN);
								result.strokeColor = Color.BLACK;
								break;
			default:
		}
		return result;
	}

	public static LineStroke createLineStroke(String lineStroke) 
		throws IllegalArgumentException {
		assert (lineStroke!=null);
		return createLineStroke(LineStroke.Strokes.valueOf(lineStroke));
	}
}

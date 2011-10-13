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
import org.letras.psi.ipen.DoIPen;
import org.letras.psi.ipen.PenSample;
import org.mundo.rt.Mundo;
import org.mundo.rt.DoObject;
import org.mundo.rt.IReceiver;
import org.mundo.rt.Message;
import org.mundo.rt.MessageContext;
import org.mundo.rt.Service;
import org.mundo.service.ResultSet;
import org.mundo.service.ServiceInfo;
import org.mundo.service.ServiceInfoFilter;
import org.mundo.service.ServiceManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class represents a simple receiver for pen-samples that are created by one
 * or many pens on one or many RawDataProcessors. This is only to test whether
 * the setup of a RawDataProcessor has been successful and to show how a continuous 
 * query can be used to discover pens in a specific Mundo zone. 
 * @author niklas
 */
public class Receiver extends Service implements IReceiver, ResultSet.ISignal {

	@Override
	public void init() {
		ServiceInfoFilter serviceInfoFilter = new ServiceInfoFilter();
		serviceInfoFilter.filterInterface("org.letras.psi.ipen.IPen");
		try {
		    //this only works if the node.conf.xml contains an instance of the ContentBroker
			ServiceManager.getInstance().contQuery(serviceInfoFilter, this.getSession(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void received(Message msg, MessageContext ctx) {
		final Object obj = msg.getObject();
		if (obj instanceof PenSample) {
			PenSample penSample = (PenSample) msg.getObject();
			System.out.println(String.format("channel: %s x-coordinate: %f, y-coordinate: %f", ctx.channel, penSample.getX(), penSample.getY()));
		}

	}

	@Override
	public void inserted(ResultSet arg0, int arg1, int arg2) {
		System.out.println("New pen found");
		for (Object obj : arg0.getList().subList(arg1, arg1+arg2)) {
			final DoObject doObject = ((ServiceInfo) obj).doService;
			final DoIPen doIPen = new DoIPen(doObject);
			getSession().subscribe(this.getServiceZone(), doIPen.channel(),  this);
		}
	}

	@Override
	public void propChanged(ResultSet arg0, int arg1) {	}

	@Override
	public void propChanging(ResultSet arg0, int arg1){	}

	@Override
	public void removed(ResultSet arg0, int arg1, int arg2) { }

	@Override
	public void removing(ResultSet arg0, int arg1, int arg2) {
	    //This callback method should be used for handling disconnecting pens
	}
	
	public static void main(String[] args) {
	   Mundo.init();
		
		System.out.println("Starting receiver for pen-samples");
		
		Receiver receiver = new Receiver();
		receiver.setServiceZone("lan");
		Mundo.registerService(receiver);
		
		System.out.println("Receiver is ready");
        
		//Wait until the user hits a key
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Mundo.shutdown();
	}	
		
    
}

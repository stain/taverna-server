/*
 * Copyright (C) 2010-2011 The University of Manchester
 * 
 * See the file "LICENSE.txt" for license terms.
 */
package org.taverna.server.master;

import static java.util.Arrays.asList;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.taverna.server.master.exceptions.NoListenerException;
import org.taverna.server.master.interfaces.Listener;
import org.taverna.server.master.interfaces.TavernaRun;
import org.taverna.server.master.rest.TavernaServerListenersREST;
import org.taverna.server.master.rest.TavernaServerListenersREST.ListenerDescription;
import org.taverna.server.master.rest.TavernaServerListenersREST.TavernaServerListenerREST;
import org.taverna.server.master.utils.InvocationCounter.CallCounted;

/**
 * RESTful interface to a single listener attached to a workflow run.
 * 
 * @author Donal Fellows
 */
abstract class SingleListenerREST implements TavernaServerListenerREST,
		OneListenerBean {
	private Listener listen;
	private TavernaRun run;

	@Override
	public SingleListenerREST connect(Listener listen, TavernaRun run) {
		this.listen = listen;
		this.run = run;
		return this;
	}

	@Override
	@CallCounted
	public String getConfiguration() {
		return listen.getConfiguration();
	}

	@Override
	@CallCounted
	public ListenerDescription getDescription(UriInfo ui) {
		return new ListenerDescription(listen, ui.getAbsolutePathBuilder());
	}

	@Override
	@CallCounted
	public TavernaServerListenersREST.Properties getProperties(UriInfo ui) {
		return new TavernaServerListenersREST.Properties(ui
				.getAbsolutePathBuilder().path("{prop}"),
				listen.listProperties());
	}

	@Override
	@CallCounted
	public TavernaServerListenersREST.Property getProperty(
			final String propertyName) throws NoListenerException {
		List<String> p = asList(listen.listProperties());
		if (p.contains(propertyName)) {
			return makePropertyInterface().connect(listen, run, propertyName);
		}
		throw new NoListenerException("no such property");
	}

	protected abstract ListenerPropertyREST makePropertyInterface();
}

/**
 * Description of properties supported by {@link InputREST}.
 * 
 * @author Donal Fellows
 */
interface OneListenerBean {
	SingleListenerREST connect(Listener listen, TavernaRun run);
}
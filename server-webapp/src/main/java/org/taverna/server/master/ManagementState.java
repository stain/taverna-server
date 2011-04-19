/*
 * Copyright (C) 2010-2011 The University of Manchester
 * 
 * See the file "LICENSE.txt" for license terms.
 */
package org.taverna.server.master;

import javax.annotation.PostConstruct;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceAware;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.taverna.server.master.utils.JDOSupport;

/** The persistent, manageable state of the Taverna Server web application. */
@PersistenceAware
class ManagementState extends JDOSupport<WebappState>implements ManagementModel {
	public ManagementState() {
		super(WebappState.class);
	}

	/** Whether we should log all workflows sent to us. */
	private boolean logIncomingWorkflows = false;

	/** Whether we allow the creation of new workflow runs. */
	private boolean allowNewWorkflowRuns = true;

	/**
	 * Whether outgoing exceptions should be logged before being converted to
	 * responses.
	 */
	private boolean logOutgoingExceptions = false;

	/**
	 * The file that all usage records should be appended to, or <tt>null</tt>
	 * if they should be just dropped.
	 */
	private String usageRecordLogFile = null;

	@Override
	public void setLogIncomingWorkflows(boolean logIncomingWorkflows) {
		this.logIncomingWorkflows = logIncomingWorkflows;
		store();
	}

	@Override
	public boolean getLogIncomingWorkflows() {
		load();
		return logIncomingWorkflows;
	}

	@Override
	public void setAllowNewWorkflowRuns(boolean allowNewWorkflowRuns) {
		this.allowNewWorkflowRuns = allowNewWorkflowRuns;
		store();
	}

	@Override
	public boolean getAllowNewWorkflowRuns() {
		load();
		return allowNewWorkflowRuns;
	}

	@Override
	public void setLogOutgoingExceptions(boolean logOutgoingExceptions) {
		this.logOutgoingExceptions = logOutgoingExceptions;
		store();
	}

	@Override
	public boolean getLogOutgoingExceptions() {
		load();
		return logOutgoingExceptions || true;
	}

	@Override
	public String getUsageRecordLogFile() {
		load();
		return usageRecordLogFile;
	}

	@Override
	public void setUsageRecordLogFile(String usageRecordLogFile) {
		this.usageRecordLogFile = usageRecordLogFile;
		store();
	}

	private static final int KEY = 42; // whatever

	private WebappState get() {
		Query q = query("id == " + KEY);
		q.setUnique(true);
		return (WebappState) q.execute();
	}

	private boolean loadedState;

	@PostConstruct
	public void load() {
		if (loadedState || !isPersistent())
			return;
		boolean ok = false;
		Xact tx = beginTx();
		try {
			WebappState state = get();
			if (state == null)
				return;
			allowNewWorkflowRuns = state.getAllowNewWorkflowRuns();
			logIncomingWorkflows = state.getLogIncomingWorkflows();
			logOutgoingExceptions = state.getLogOutgoingExceptions();
			usageRecordLogFile = state.getUsageRecordLogFile();
			tx.commit();
			ok = true;
			loadedState = true;
		} finally {
			if (!ok)
				tx.rollback();
		}
	}

	private void store() {
		if (!isPersistent())
			return;
		boolean ok = false;
		Xact tx = beginTx();
		try {
			WebappState state = get();
			if (state == null) {
				state = new WebappState();
				// save state
				state.id = KEY; // whatever...
				state = persist(state);
			}
			state.setAllowNewWorkflowRuns(allowNewWorkflowRuns);
			state.setLogIncomingWorkflows(logIncomingWorkflows);
			state.setLogOutgoingExceptions(logOutgoingExceptions);
			state.setUsageRecordLogFile(usageRecordLogFile);
			tx.commit();
			ok = true;
			loadedState = true;
		} finally {
			if (!ok)
				tx.rollback();
		}
	}
}

@PersistenceCapable(table = "MANAGEMENTSTATE__WEBAPPSTATE")
class WebappState implements ManagementModel {
	public WebappState() {
	}
	
	@PrimaryKey
	protected int id;
	
	/** Whether we should log all workflows sent to us. */
	@Persistent
	private boolean logIncomingWorkflows;
	
	/** Whether we allow the creation of new workflow runs. */
	@Persistent
	private boolean allowNewWorkflowRuns;
	
	/**
	 * Whether outgoing exceptions should be logged before being converted
	 * to responses.
	 */
	@Persistent
	private boolean logOutgoingExceptions;

	/** Where to write usage records. */
	@Persistent
	private String usageRecordLogFile;
	
	@Override
	public void setLogIncomingWorkflows(boolean logIncomingWorkflows) {
		this.logIncomingWorkflows = logIncomingWorkflows;
	}
	
	@Override
	public boolean getLogIncomingWorkflows() {
		return logIncomingWorkflows;
	}
	
	@Override
	public void setAllowNewWorkflowRuns(boolean allowNewWorkflowRuns) {
		this.allowNewWorkflowRuns = allowNewWorkflowRuns;
	}
	
	@Override
	public boolean getAllowNewWorkflowRuns() {
		return allowNewWorkflowRuns;
	}
	
	@Override
	public void setLogOutgoingExceptions(boolean logOutgoingExceptions) {
		this.logOutgoingExceptions = logOutgoingExceptions;
	}
	
	@Override
	public boolean getLogOutgoingExceptions() {
		return logOutgoingExceptions;
	}
	
	@Override
	public String getUsageRecordLogFile() {
		return usageRecordLogFile;
	}
	
	@Override
	public void setUsageRecordLogFile(String usageRecordLogFile) {
		this.usageRecordLogFile = usageRecordLogFile;
	}
}

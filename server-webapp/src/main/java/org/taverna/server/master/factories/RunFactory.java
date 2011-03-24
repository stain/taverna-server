package org.taverna.server.master.factories;

import org.taverna.server.master.common.Workflow;
import org.taverna.server.master.exceptions.NoCreateException;
import org.taverna.server.master.interfaces.TavernaRun;
import org.taverna.server.master.utils.UsernamePrincipal;

/**
 * How to construct a Taverna Server Workflow Run.
 * 
 * @author Donal Fellows
 */
public interface RunFactory {
	/**
	 * Make a Taverna Server workflow run that is bound to a particular user
	 * (the "creator") and able to run a particular workflow.
	 * 
	 * @param creator
	 *            The user creating the workflow instance.
	 * @param workflow
	 *            The workflow to instantiate
	 * @return An object representing the run.
	 * @throws NoCreateException On failure.
	 */
	public TavernaRun create(UsernamePrincipal creator, Workflow workflow) throws NoCreateException;
}

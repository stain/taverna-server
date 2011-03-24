/*
 * Copyright (C) 2010-2011 The University of Manchester
 * 
 * See the file "LICENSE.txt" for license terms.
 */
package org.taverna.server.master.rest;

import static java.util.Collections.emptyList;
import static org.taverna.server.master.common.Namespaces.XLINK;
import static org.taverna.server.master.common.Roles.USER;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.cxf.jaxrs.ext.Description;
import org.taverna.server.master.common.Credential;
import org.taverna.server.master.common.Permission;
import org.taverna.server.master.common.Trust;
import org.taverna.server.master.common.Uri;
import org.taverna.server.master.common.VersionedElement;
import org.taverna.server.master.exceptions.BadStateChangeException;
import org.taverna.server.master.exceptions.InvalidCredentialException;
import org.taverna.server.master.exceptions.NoCredentialException;
import org.taverna.server.master.utils.InvocationCounter.CallCounted;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Manages the security of the workflow run. In general, only the owner of a run
 * may access this resource. Many of these security-related resources may only
 * be changed before the run is set to operating.
 * 
 * @author Donal Fellows
 */
@RolesAllowed(USER)
@Description("Manages the security of the workflow run. In general, only the owner of a run may access this resource.")
public interface TavernaServerSecurityREST {
	/**
	 * Gets a description of the security information supported by the workflow
	 * run.
	 * 
	 * @param ui
	 *            About the URI used to access this resource.
	 * @return A description of the security information.
	 */
	@GET
	@Path("/")
	@Produces({ "application/xml", "application/json" })
	@Description("Gives a description of the security information supported by the workflow run.")
	@CallCounted
	@NonNull
	Descriptor describe(@NonNull @Context UriInfo ui);

	/**
	 * Gets the identity of who owns the workflow run.
	 * 
	 * @return The name of the owner of the run.
	 */
	@GET
	@Path("owner")
	@Produces("text/plain")
	@Description("Gives the identity of who owns the workflow run.")
	@CallCounted
	@NonNull
	String getOwner();

	/*
	 * @PUT @Path("/") @Consumes(APPLICATION_OCTET_STREAM) @CallCounted @NonNull
	 * public void set(@NonNull InputStream contents, @NonNull @Context UriInfo
	 * ui);
	 */

	/**
	 * @return A list of credentials supplied to this workflow run.
	 */
	@GET
	@Path("credentials")
	@Produces({ "application/xml", "application/json" })
	@Description("Gives a list of credentials supplied to this workflow run.")
	@CallCounted
	@NonNull
	CredentialList listCredentials();

	/**
	 * Describe a particular credential.
	 * 
	 * @param id
	 *            The id of the credential to fetch.
	 * @return The description of the credential.
	 * @throws NoCredentialException
	 *             If the credential doesn't exist.
	 */
	@GET
	@Path("credentials/{id}")
	@Produces({ "application/xml", "application/json" })
	@Description("Describes a particular credential.")
	@CallCounted
	@NonNull
	Credential getParticularCredential(@NonNull @PathParam("id") String id)
			throws NoCredentialException;

	/**
	 * Update a particular credential.
	 * 
	 * @param id
	 *            The id of the credential to update.
	 * @param c
	 *            The details of the credential to use in the update.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return Description of the updated credential.
	 * @throws InvalidCredentialException
	 *             If the credential description isn't valid.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@PUT
	@Path("credentials/{id}")
	@Consumes({ "application/xml", "application/json" })
	@Produces({ "application/xml", "application/json" })
	@Description("Updates a particular credential.")
	@CallCounted
	@NonNull
	Credential setParticularCredential(@NonNull @PathParam("id") String id,
			@NonNull Credential c, @NonNull @Context UriInfo ui)
			throws InvalidCredentialException, BadStateChangeException;

	/**
	 * Adds a new credential.
	 * 
	 * @param c
	 *            The details of the credential to create.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return Description of the created credential.
	 * @throws InvalidCredentialException
	 *             If the credential description isn't valid.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@POST
	@Path("credentials")
	@Consumes({ "application/xml", "application/json" })
	@Description("Creates a new credential.")
	@CallCounted
	@NonNull
	Response addCredential(@NonNull Credential c, @NonNull @Context UriInfo ui)
			throws InvalidCredentialException, BadStateChangeException;

	/**
	 * Deletes all credentials associated with a run.
	 * 
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return A characterisation of a successful delete.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@DELETE
	@Path("credentials")
	@Description("Deletes all credentials.")
	@CallCounted
	@NonNull
	Response deleteAllCredentials(@NonNull @Context UriInfo ui)
			throws BadStateChangeException;

	/**
	 * Deletes one credential associated with a run.
	 * 
	 * @param id
	 *            The identity of the credential to delete.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return A characterisation of a successful delete.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@DELETE
	@Path("credentials/{id}")
	@Description("Deletes a particular credential.")
	@CallCounted
	@NonNull
	Response deleteCredential(@NonNull @PathParam("id") String id,
			@NonNull @Context UriInfo ui) throws BadStateChangeException;

	/**
	 * @return A list of trusted identities supplied to this workflow run.
	 */
	@GET
	@Path("trusts")
	@Produces({ "application/xml", "application/json" })
	@Description("Gives a list of trusted identities supplied to this workflow run.")
	@CallCounted
	@NonNull
	TrustList listTrusted();

	/**
	 * Describe a particular trusted identity.
	 * 
	 * @param id
	 *            The id of the trusted identity to fetch.
	 * @return The description of the trusted identity.
	 * @throws NoCredentialException
	 *             If the trusted identity doesn't exist.
	 */
	@GET
	@Path("trusts/{id}")
	@Produces({ "application/xml", "application/json" })
	@Description("Describes a particular trusted identity.")
	@CallCounted
	@NonNull
	Trust getParticularTrust(@NonNull @PathParam("id") String id)
			throws NoCredentialException;

	/**
	 * Update a particular trusted identity.
	 * 
	 * @param id
	 *            The id of the trusted identity to update.
	 * @param t
	 *            The details of the trusted identity to use in the update.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return Description of the updated trusted identity.
	 * @throws InvalidCredentialException
	 *             If the trusted identity description isn't valid.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@PUT
	@Path("trusts/{id}")
	@Consumes({ "application/xml", "application/json" })
	@Produces({ "application/xml", "application/json" })
	@Description("Updates a particular trusted identity.")
	@CallCounted
	@NonNull
	Trust setParticularTrust(@NonNull @PathParam("id") String id,
			@NonNull Trust t, @NonNull @Context UriInfo ui)
			throws InvalidCredentialException, BadStateChangeException;

	/**
	 * Adds a new trusted identity.
	 * 
	 * @param t
	 *            The details of the trusted identity to create.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return Description of the created trusted identity.
	 * @throws InvalidCredentialException
	 *             If the trusted identity description isn't valid.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@POST
	@Path("trusts")
	@Consumes({ "application/xml", "application/json" })
	@Description("Adds a new trusted identity.")
	@CallCounted
	@NonNull
	Response addTrust(@NonNull Trust t, @NonNull @Context UriInfo ui)
			throws InvalidCredentialException, BadStateChangeException;

	/**
	 * Deletes all trusted identities associated with a run.
	 * 
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return A characterisation of a successful delete.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@DELETE
	@Path("trusts")
	@Description("Deletes all trusted identities.")
	@CallCounted
	@NonNull
	Response deleteAllTrusts(@NonNull @Context UriInfo ui)
			throws BadStateChangeException;

	/**
	 * Deletes one trusted identity associated with a run.
	 * 
	 * @param id
	 *            The identity of the trusted identity to delete.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return A characterisation of a successful delete.
	 * @throws BadStateChangeException
	 *             If the workflow run is not in the initialising state.
	 */
	@DELETE
	@Path("trusts/{id}")
	@Description("Deletes a particular trusted identity.")
	@CallCounted
	@NonNull
	Response deleteTrust(@NonNull @PathParam("id") String id,
			@NonNull @Context UriInfo ui) throws BadStateChangeException;

	/**
	 * @return A list of (non-default) permissions associated with this workflow
	 *         run.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 */
	@GET
	@Path("permissions")
	@Produces({ "application/xml", "application/json" })
	@Description("Gives a list of all non-default permissions associated with the enclosing workflow run. By default, nobody has any access at all except for the owner of the run.")
	@CallCounted
	@NonNull
	PermissionsDescription describePermissions(@NonNull @Context UriInfo ui);

	/**
	 * Describe the particular permission granted to a user.
	 * 
	 * @param id
	 *            The name of the user whose permissions are to be described.
	 * @return The permission they are granted.
	 */
	@GET
	@Path("permissions/{id}")
	@Produces("text/plain")
	@Description("Describes the permission granted to a particular user.")
	@CallCounted
	@NonNull
	Permission describePermission(@NonNull @PathParam("id") String id);

	/**
	 * Update the permission granted to a user.
	 * 
	 * @param id
	 *            The name of the user whose permissions are to be updated. Note
	 *            that the owner always has full permissions.
	 * @param perm
	 *            The permission level to set.
	 * @return The permission level that has actually been set.
	 */
	@PUT
	@Consumes("text/plain")
	@Produces("text/plain")
	@Path("permissions/{id}")
	@Description("Updates the permissions granted to a particular user.")
	@CallCounted
	@NonNull
	Permission setPermission(@NonNull @PathParam("id") String id,
			@NonNull Permission perm);

	/**
	 * Delete the permissions associated with a user, which restores them to the
	 * default (no access unless they are the owner or have admin privileges).
	 * 
	 * @param id
	 *            The name of the user whose permissions are to be revoked.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return An indication that the delete has been successful (or not).
	 */
	@DELETE
	@Path("permissions/{id}")
	@Description("Deletes (by resetting to default) the permissions associated with a particular user.")
	@CallCounted
	@NonNull
	Response deletePermission(@NonNull @PathParam("id") String id,
			@NonNull @Context UriInfo ui);

	/**
	 * Manufacture a permission setting for a previously-unknown user.
	 * 
	 * @param desc
	 *            A description of the name of the user and the permission level
	 *            to grant them.
	 * @param ui
	 *            Information about the URI used to access this resource.
	 * @return An indication that the create has been successful (or not).
	 */
	@POST
	@Path("permissions")
	@Consumes({ "application/xml", "application/json" })
	@Description("Creates a new assignment of permissions to a particular user.")
	@CallCounted
	@NonNull
	Response makePermission(@NonNull PermissionDescription desc,
			@NonNull @Context UriInfo ui);

	// TODO document this helper class
	@XmlRootElement(name = "securityDescriptor")
	@XmlType(name = "SecurityDescriptor")
	public static final class Descriptor extends VersionedElement {
		@XmlElement
		public String owner;
		@XmlElement
		public Uri permissions;

		@XmlElement
		public Credentials credentials;
		@XmlElement
		public Trusts trusts;

		public Descriptor() {
		}

		public Descriptor(@NonNull UriBuilder ub, @NonNull String owner,
				@NonNull Credential[] credential, @NonNull Trust[] trust) {
			super(true);
			this.owner = owner;
			this.permissions = new Uri(ub, "permissions");
			this.credentials = new Credentials(ub.build("credentials"),
					credential);
			this.trusts = new Trusts(ub.build("trusts"), trust);
		}

		// TODO document this helper class
		@XmlType(name = "CredentialCollection")
		public static final class Credentials {
			@XmlAttribute(name = "href", namespace = XLINK)
			public URI href;
			@XmlElement
			public Credential[] credential;

			public Credentials() {
			}

			public Credentials(@NonNull URI uri,
					@NonNull Credential[] credential) {
				this.href = uri;
				this.credential = credential.clone();
			}
		}

		// TODO document this helper class
		@XmlType(name = "TrustCollection")
		public static final class Trusts {
			@XmlAttribute(name = "href", namespace = XLINK)
			public URI href;
			@XmlElement
			public Trust[] trust;

			public Trusts() {
			}

			public Trusts(@NonNull URI uri, @NonNull Trust[] trust) {
				this.href = uri;
				this.trust = trust.clone();
			}
		}
	}

	// TODO document this helper class
	@XmlRootElement(name = "credentials")
	public static final class CredentialList extends VersionedElement {
		@XmlElement
		public Credential[] credential;

		public CredentialList() {
		}

		public CredentialList(@NonNull Credential[] credential) {
			super(true);
			this.credential = credential.clone();
		}
	}

	// TODO document this helper class
	@XmlRootElement(name = "trustedIdentities")
	public static final class TrustList extends VersionedElement {
		@XmlElement
		public Trust[] trust;

		public TrustList() {
		}

		public TrustList(@NonNull Trust[] trust) {
			super(true);
			this.trust = trust.clone();
		}
	}

	// TODO document this helper class
	@XmlRootElement(name = "permissionsDescriptor")
	public static class PermissionsDescription extends VersionedElement {
		@XmlRootElement(name = "userPermission")
		public static class LinkedPermissionDescription extends Uri {
			@XmlElement
			public String userName;
			@XmlElement
			public Permission permission;

			public LinkedPermissionDescription() {
			}

			public LinkedPermissionDescription(@NonNull UriBuilder ub,
					@NonNull String userName, @NonNull Permission permission,
					String... strings) {
				super(ub, strings);
				this.userName = userName;
				this.permission = permission;
			}
		}

		@XmlElement
		public List<LinkedPermissionDescription> permission;

		public PermissionsDescription() {
			permission = emptyList();
		}

		public PermissionsDescription(@NonNull UriBuilder ub,
				@NonNull Map<String, Permission> permissionMap) {
			permission = new ArrayList<LinkedPermissionDescription>();
			List<String> userNames = new ArrayList<String>(
					permissionMap.keySet());
			Collections.sort(userNames);
			for (String user : userNames)
				permission.add(new LinkedPermissionDescription(ub, user,
						permissionMap.get(user), user));
		}
	}

	// TODO document this helper class
	@XmlRootElement(name = "permissionUpdate")
	public static class PermissionDescription {
		@XmlElement
		public String userName;
		@XmlElement
		public Permission permission;
	}
}
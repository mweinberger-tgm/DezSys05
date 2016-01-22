package dezsys05.mweinberger;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.*;

public class LDAPConnector {

	private DirContext ctx;

	public static final String GROUP = "group.service5";

	public LDAPConnector(String host, int port, String auth_user, String auth_password) {
		Hashtable<String, Object> env = new Hashtable<>(11);

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, auth_user);
		env.put(Context.SECURITY_CREDENTIALS, auth_password);

		try {
			this.ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public NamingEnumeration search(String inBase, String inFilter) {
		try {
			return this.ctx.search(inBase, inFilter, new SearchControls());
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void updateAttribute(String inDN, String inAttribute, String inValue) {

		ModificationItem[] mods = new ModificationItem[1];
		Attribute mod0 = new BasicAttribute(inAttribute, inValue);
		mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
		try {
			this.ctx.modifyAttributes(inDN, mods);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	private String getAttributeValue(NamingEnumeration namingEnum, String attributeName) {
		try {
			while (namingEnum.hasMore()) {
				SearchResult sr = (SearchResult) namingEnum.next();
				if (sr.getAttributes().get(attributeName) != null) {
					return sr.getAttributes().get(attributeName).get().toString();
				}
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getDescription() {
		NamingEnumeration results = this.search("dc=nodomain,dc=com", "(&(objectclass=PosixGroup)(cn=" + GROUP + "))");
		return getAttributeValue(results, "description");
	}

	public void setDescription(String description) {
		updateAttribute("cn=" + GROUP + ",dc=nodomain,dc=com", "description", description);
	}

	public void close() {
		try {
			this.ctx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
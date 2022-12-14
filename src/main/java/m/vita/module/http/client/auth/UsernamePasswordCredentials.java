package m.vita.module.http.client.auth;

import java.io.Serializable;
import java.security.Principal;

import m.vita.module.http.annotation.Immutable;
import m.vita.module.http.util.Args;
import m.vita.module.http.util.LangUtils;

@Immutable
public class UsernamePasswordCredentials implements Credentials, Serializable {


    private final BasicUserPrincipal principal;
    private final String password;

    /**
     * The constructor with the username and password combined string argument.
     *
     * @param usernamePassword the username:password formed string
     * @see #toString
     */
    public UsernamePasswordCredentials(final String usernamePassword) {
        super();
        Args.notNull(usernamePassword, "Username:password string");
        final int atColon = usernamePassword.indexOf(':');
        if (atColon >= 0) {
            this.principal = new BasicUserPrincipal(usernamePassword.substring(0, atColon));
            this.password = usernamePassword.substring(atColon + 1);
        } else {
            this.principal = new BasicUserPrincipal(usernamePassword);
            this.password = null;
        }
    }


    /**
     * The constructor with the username and password arguments.
     *
     * @param userName the user name
     * @param password the password
     */
    public UsernamePasswordCredentials(final String userName, final String password) {
        super();
        Args.notNull(userName, "Username");
        this.principal = new BasicUserPrincipal(userName);
        this.password = password;
    }

    public Principal getUserPrincipal() {
        return this.principal;
    }

    public String getUserName() {
        return this.principal.getName();
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        return this.principal.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UsernamePasswordCredentials) {
            final UsernamePasswordCredentials that = (UsernamePasswordCredentials) o;
            if (LangUtils.equals(this.principal, that.principal)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.principal.toString();
    }

}


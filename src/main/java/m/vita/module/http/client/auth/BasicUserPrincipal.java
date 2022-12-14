package m.vita.module.http.client.auth;

import java.io.Serializable;
import java.security.Principal;

import m.vita.module.http.annotation.Immutable;
import m.vita.module.http.util.Args;
import m.vita.module.http.util.LangUtils;

@Immutable
public final class BasicUserPrincipal implements Principal, Serializable {

    private final String username;

    public BasicUserPrincipal(final String username) {
        super();
        Args.notNull(username, "User name");
        this.username = username;
    }

    public String getName() {
        return this.username;
    }

    @Override
    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.username);
        return hash;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BasicUserPrincipal) {
            final BasicUserPrincipal that = (BasicUserPrincipal) o;
            if (LangUtils.equals(this.username, that.username)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[principal: ");
        buffer.append(this.username);
        buffer.append("]");
        return buffer.toString();
    }

}


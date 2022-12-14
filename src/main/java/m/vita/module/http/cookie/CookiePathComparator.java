package m.vita.module.http.cookie;

import java.io.Serializable;
import java.util.Comparator;

import m.vita.module.http.annotation.Immutable;

@Immutable
public class CookiePathComparator implements Serializable, Comparator<Cookie> {

    private String normalizePath(final Cookie cookie) {
        String path = cookie.getPath();
        if (path == null) {
            path = "/";
        }
        if (!path.endsWith("/")) {
            path = path + '/';
        }
        return path;
    }

    public int compare(final Cookie c1, final Cookie c2) {
        final String path1 = normalizePath(c1);
        final String path2 = normalizePath(c2);
        if (path1.equals(path2)) {
            return 0;
        } else if (path1.startsWith(path2)) {
            return -1;
        } else if (path2.startsWith(path1)) {
            return 1;
        } else {
            // Does not really matter
            return 0;
        }
    }

}

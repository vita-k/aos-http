package m.vita.module.http.header;

public interface HttpParams {

    /**
     * Obtains the value of the given parameter.
     *
     * @param name the parent name.
     *
     * @return  an object that represents the value of the parameter,
     *          <code>null</code> if the parameter is not set or if it
     *          is explicitly set to <code>null</code>
     *
     * @see #setParameter(String, Object)
     */
    Object getParameter(String name);

    /**
     * Assigns the value to the parameter with the given name.
     *
     * @param name parameter name
     * @param value parameter value
     */
    HttpParams setParameter(String name, Object value);

    /**
     * Creates a copy of these parameters.
     *
     * @return  a new set of parameters holding the same values as this one
     */
    HttpParams copy();

    /**
     * Removes the parameter with the specified name.
     *
     * @param name parameter name
     *
     * @return true if the parameter existed and has been removed, false else.
     */
    boolean removeParameter(String name);

    /**
     * Returns a {@link Long} parameter value with the given name.
     * If the parameter is not explicitly set, the default value is returned.
     *
     * @param name the parent name.
     * @param defaultValue the default value.
     *
     * @return a {@link Long} that represents the value of the parameter.
     *
     * @see #setLongParameter(String, long)
     */
    long getLongParameter(String name, long defaultValue);

    /**
     * Assigns a {@link Long} to the parameter with the given name
     *
     * @param name parameter name
     * @param value parameter value
     */
    HttpParams setLongParameter(String name, long value);

    /**
     * Returns an {@link Integer} parameter value with the given name.
     * If the parameter is not explicitly set, the default value is returned.
     *
     * @param name the parent name.
     * @param defaultValue the default value.
     *
     * @return a {@link Integer} that represents the value of the parameter.
     *
     * @see #setIntParameter(String, int)
     */
    int getIntParameter(String name, int defaultValue);

    /**
     * Assigns an {@link Integer} to the parameter with the given name
     *
     * @param name parameter name
     * @param value parameter value
     */
    HttpParams setIntParameter(String name, int value);

    /**
     * Returns a {@link Double} parameter value with the given name.
     * If the parameter is not explicitly set, the default value is returned.
     *
     * @param name the parent name.
     * @param defaultValue the default value.
     *
     * @return a {@link Double} that represents the value of the parameter.
     *
     * @see #setDoubleParameter(String, double)
     */
    double getDoubleParameter(String name, double defaultValue);

    /**
     * Assigns a {@link Double} to the parameter with the given name
     *
     * @param name parameter name
     * @param value parameter value
     */
    HttpParams setDoubleParameter(String name, double value);

    /**
     * Returns a {@link Boolean} parameter value with the given name.
     * If the parameter is not explicitly set, the default value is returned.
     *
     * @param name the parent name.
     * @param defaultValue the default value.
     *
     * @return a {@link Boolean} that represents the value of the parameter.
     *
     * @see #setBooleanParameter(String, boolean)
     */
    boolean getBooleanParameter(String name, boolean defaultValue);

    /**
     * Assigns a {@link Boolean} to the parameter with the given name
     *
     * @param name parameter name
     * @param value parameter value
     */
    HttpParams setBooleanParameter(String name, boolean value);

    /**
     * Checks if a boolean parameter is set to <code>true</code>.
     *
     * @param name parameter name
     *
     * @return <tt>true</tt> if the parameter is set to value <tt>true</tt>,
     *         <tt>false</tt> if it is not set or set to <code>false</code>
     */
    boolean isParameterTrue(String name);

    /**
     * Checks if a boolean parameter is not set or <code>false</code>.
     *
     * @param name parameter name
     *
     * @return <tt>true</tt> if the parameter is either not set or
     *         set to value <tt>false</tt>,
     *         <tt>false</tt> if it is set to <code>true</code>
     */
    boolean isParameterFalse(String name);

}

/*
 * $Id: ResponseUtils.java 164747 2005-04-26 05:47:48Z hrabago $ 
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts.util;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.taglib.TagUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;

/**
 * General purpose utility methods related to generating a servlet response
 * in the Struts controller framework.
 *
 * @version $Rev: 164747 $ $Date: 2005-04-26 06:47:48 +0100 (Tue, 26 Apr 2005) $
 */
public class ResponseUtils {


    // ------------------------------------------------------- Static Variables


    /**
     * The message resources for this package.
     */
    protected static MessageResources messages =
        MessageResources.getMessageResources
        ("org.apache.struts.util.LocalStrings");


    /**
     * Java 1.4 encode method to use instead of deprecated 1.3 version.
     */
    private static Method encode = null;


    /**
     * Commons logging instance.
     */
    private static final Log log = LogFactory.getLog(ResponseUtils.class);


    /**
     * Initialize the encode variable with the
     * Java 1.4 method if available.
     */
    static {

        try {
            // get version of encode method with two String args
            Class[] args = new Class[]{String.class, String.class};
            encode = URLEncoder.class.getMethod("encode", args);
        } catch (NoSuchMethodException e) {
            log.debug("Could not find Java 1.4 encode method.  Using deprecated version.", e);
        }
    }

    

    // --------------------------------------------------------- Public Methods


    /**
     * Filter the specified string for characters that are sensitive to
     * HTML interpreters, returning the string with these characters replaced
     * by the corresponding character entities.
     *
     * @param value The string to be filtered and returned
     */
    public static String filter(String value) {

        if (value == null || value.length() == 0) {
            return value;
        }

        StringBuffer result = null;
        String filtered = null;
        for (int i = 0; i < value.length(); i++) {
            filtered = null;
            switch (value.charAt(i)) {
                case '<':
                    filtered = "&lt;";
                    break;
                case '>':
                    filtered = "&gt;";
                    break;
                case '&':
                    filtered = "&amp;";
                    break;
                case '"':
                    filtered = "&quot;";
                    break;
                case '\'':
                    filtered = "&#39;";
                    break;
            }

            if (result == null) {
                if (filtered != null) {
                    result = new StringBuffer(value.length() + 50);
                    if (i > 0) {
                        result.append(value.substring(0, i));
                    }
                    result.append(filtered);
                }
            } else {
                if (filtered == null) {
                    result.append(value.charAt(i));
                } else {
                    result.append(filtered);
                }
            }
        }

        return result == null ? value : result.toString();
    }



    
    /**
	 * <p>URLencodes a string assuming the character encoding is UTF-8.</p>
	 *
	 * @param url
	 * @return String The encoded url in UTF-8
	 */
	public static String encodeURL(String url) {
		return encodeURL(url, "UTF-8");
	}

    
    /**
     * Use the new URLEncoder.encode() method from Java 1.4 if available, else
     * use the old deprecated version.  This method uses reflection to find the
     * appropriate method; if the reflection operations throw exceptions, this
     * will return the url encoded with the old URLEncoder.encode() method.
     * @param enc The character encoding the urlencode is performed on.
     * @return String The encoded url.
     */
    public static String encodeURL(String url, String enc) {
        try {

			if(enc==null || enc.length()==0){
				enc = "UTF-8";
			}

            // encode url with new 1.4 method and UTF-8 encoding
            if (encode != null) {
                return (String) encode.invoke(null, new Object[]{url,  enc});
            }

        } catch (IllegalAccessException e) {
            log.debug("Could not find Java 1.4 encode method. Using deprecated version.", e);
        } catch (InvocationTargetException e) {
            log.debug("Could not find Java 1.4 encode method. Using deprecated version.", e);
        }

        return URLEncoder.encode(url);
    }


    /**
     * Write the specified text as the response to the writer associated with
     * this page.  <strong>WARNING</strong> - If you are writing body content
     * from the <code>doAfterBody()</code> method of a custom tag class that
     * implements <code>BodyTag</code>, you should be calling
     * <code>writePrevious()</code> instead.
     *
     * @param pageContext The PageContext object for this page
     * @param text The text to be written
     *
     * @exception JspException if an input/output error occurs (already saved)
     * @deprecated use TagUtils.write() method instead.
     *      This method will be removed after Struts 1.2.
     */
    public static void write(PageContext pageContext, String text)
        throws JspException {

        TagUtils.getInstance().write(pageContext, text);

    }


    /**
     * Write the specified text as the response to the writer associated with
     * the body content for the tag within which we are currently nested.
     *
     * @param pageContext The PageContext object for this page
     * @param text The text to be written
     *
     * @exception JspException if an input/output error occurs (already saved)
     * @deprecated use TagUtils.writePrevious() method instead.  
     *      This method will be removed after Struts 1.2.
     */
    public static void writePrevious(PageContext pageContext, String text)
        throws JspException {

        TagUtils.getInstance().writePrevious(pageContext, text);

    }


}

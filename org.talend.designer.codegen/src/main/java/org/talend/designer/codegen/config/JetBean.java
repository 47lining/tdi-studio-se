// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the  agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//   
// ============================================================================
package org.talend.designer.codegen.config;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.eclipse.core.runtime.Platform;
import org.talend.commons.utils.StringUtils;

/**
 * Jet container for a particular component.
 * 
 * $Id$
 * 
 */
public class JetBean {

    private Object argument;

    private String jetPluginRepository;

    private HashMap<String, String> classPath;

    private String templateRelativeUri;

    private boolean forceOverwrite = true;

    private ClassLoader loader = null;

    private String className = "";

    private Method method = null;

    private String version = null;

    private String language = null;

    private String codePart = null;
    
    private String family = "common";

    /**
     * Minimal Constructor.
     */
    public JetBean() {
    }

    /**
     * Full Constructor.
     * 
     * @param jetPluginRepository
     * @param classpathVariable
     * @param classpathParameter
     * @param templateRelativeUri
     */
    public JetBean(String jetPluginRepository, String templateRelativeUri, String className, String version,
            String language, String codePart) {
        this.classPath = new HashMap<String, String>();
        this.jetPluginRepository = jetPluginRepository;
        this.templateRelativeUri = templateRelativeUri;
        this.version = version;
        String tmpClassName = "";
        if (className.lastIndexOf(".") > -1) {
            tmpClassName = className.substring(className.lastIndexOf("."));
        } else {
            tmpClassName = className;
        }
        this.className = StringUtils.capitalize(tmpClassName);
        this.language = StringUtils.capitalize(language);
        if ((codePart != null) && (codePart.length() != 0)) {
            this.codePart = StringUtils.capitalize(codePart);
        } else {
            this.codePart = "";
        }
    }

    /**
     * Getter for classPath.
     * 
     * @return the classPath
     */
    public HashMap<String, String> getClassPath() {
        return this.classPath;
    }

    /**
     * Sets the classPath.
     * 
     * @param classPath the classPath to set
     */
    public void setClassPath(HashMap<String, String> classPath) {
        this.classPath = classPath;
    }

    /**
     * add a variable to the classPath.
     * 
     * @param classpathVariable
     * @param classpathParameter
     */
    public void addClassPath(String classpathVariable, String classpathParameter) {
        this.classPath.put(classpathVariable, classpathParameter);
    }

    /**
     * Getter for jetPluginRepository.
     * 
     * @return the jetPluginRepository
     */
    public String getJetPluginRepository() {
        return jetPluginRepository;
    }

    /**
     * Sets the jetPluginRepository.
     * 
     * @param jetPluginRepository the jetPluginRepository to set
     */
    public void setJetPluginRepository(String jetPluginRepository) {
        this.jetPluginRepository = jetPluginRepository;
    }

    /**
     * Getter for argument.
     * 
     * @return the argument
     */
    public Object getArgument() {
        return argument;
    }

    /**
     * Sets the argument.
     * 
     * @param argument the argument to set
     */
    public void setArgument(Object argument) {
        this.argument = argument;
    }

    /**
     * Getter for templateRelativeUri.
     * 
     * @return the templateRelativeUri
     */
    public String getTemplateRelativeUri() {
        return templateRelativeUri;
    }

    /**
     * Sets the templateRelativeUri.
     * 
     * @param templateRelativeUri the templateRelativeUri to set
     */
    public void setTemplateRelativeUri(String templateRelativeUri) {
        this.templateRelativeUri = templateRelativeUri;
    }

    /**
     * Return this Bean Template Full URI.
     * 
     * @return
     */
    public String getTemplateFullUri() {
        return getUri(getJetPluginRepository(), getTemplateRelativeUri());
    }

    /**
     * Return uri for this plugin.
     * 
     * @param pluginId
     * @param relativeUri
     * @return
     */
    private String getUri(String pluginId, String relativeUri) {
        String base = Platform.getBundle(pluginId).getEntry("/").toString();
        String result = base + relativeUri;
        return result;
    }

    /**
     * Getter for forceOverwrite.
     * 
     * @return the forceOverwrite
     */
    public boolean isForceOverwrite() {
        return forceOverwrite;
    }

    /**
     * Sets the forceOverwrite.
     * 
     * @param forceOverwrite the forceOverwrite to set
     */
    public void setForceOverwrite(boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((this.templateRelativeUri == null) ? 0 : this.templateRelativeUri.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JetBean other = (JetBean) obj;
        if (this.templateRelativeUri == null) {
            if (other.templateRelativeUri != null) {
                return false;
            }
        } else if (!this.templateRelativeUri.equals(other.templateRelativeUri)) {
            return false;
        }
        return true;
    }

    /**
     * Getter for loader.
     * 
     * @return the loader
     */
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    /**
     * Sets the loader.
     * 
     * @param loader the loader to set
     */
    public void setClassLoader(ClassLoader newloader) {
        this.loader = newloader;
    }

    /**
     * Getter for method.
     * 
     * @return the method
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Sets the method.
     * 
     * @param method the method to set
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    /**
     * Getter for className.
     * 
     * @return the className
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Sets the className.
     * 
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Getter for version.
     * 
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Sets the version.
     * 
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Getter for language.
     * 
     * @return the language
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Sets the language.
     * 
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Getter for codePart.
     * 
     * @return the codePart
     */
    public String getCodePart() {
        return this.codePart;
    }

    /**
     * Sets the codePart.
     * 
     * @param codePart the codePart to set
     */
    public void setCodePart(String codePart) {
        this.codePart = codePart;
    }

    public String getFamily() {
        return family;
    }

    
    public void setFamily(String family) {
        this.family = family;
    }
    
}

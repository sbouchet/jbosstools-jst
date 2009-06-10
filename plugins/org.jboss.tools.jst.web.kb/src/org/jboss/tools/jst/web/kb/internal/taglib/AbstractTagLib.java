/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jst.web.kb.internal.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.model.project.ext.store.XMLStoreConstants;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.internal.KbObject;
import org.jboss.tools.jst.web.kb.internal.KbXMLStoreConstants;
import org.jboss.tools.jst.web.kb.taglib.IAttribute;
import org.jboss.tools.jst.web.kb.taglib.IComponent;
import org.jboss.tools.jst.web.kb.taglib.INameSpace;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.w3c.dom.Element;

/**
 * Abstract implementation of ITagLibrary
 * @author Alexey Kazakov
 */
public abstract class AbstractTagLib extends KbObject implements ITagLibrary {
	public static String URI = "uri";

	protected INameSpace nameSpace;
	protected String uri;
	protected String version;
	protected boolean hasExtenededComponents = false;
	private Map<String, IComponent> components = new HashMap<String, IComponent>();
	private IComponent[] componentsArray;

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.TagLibrary#getAllComponents()
	 */
	public IComponent[] getComponents() {
		if(componentsArray==null) {
			synchronized (components) {
				componentsArray = components.values().toArray(new IComponent[components.size()]);
			}
		}
		return componentsArray;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.TagLibrary#getComponent(java.lang.String)
	 */
	public IComponent getComponent(String name) {
		return components.get(name);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.TagLibrary#getComponents(java.lang.String)
	 */
	public IComponent[] getComponents(String nameTemplate) {
		return getComponents(nameTemplate, null);
	}

	public IComponent[] getComponents(String nameTemplate, IPageContext context) {
		List<IComponent> list = new ArrayList<IComponent>();
		IComponent[] comps = getComponents();
		for (int i = 0; i < comps.length; i++) {
			if(comps[i].getName().startsWith(nameTemplate) && (context==null || checkExtended(comps[i], context))) {
				list.add(comps[i]);
			}
		}
		return list.toArray(new IComponent[list.size()]);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.TagLibrary#getComponents(org.jboss.tools.jst.web.kb.KbQuery, org.jboss.tools.jst.web.kb.PageContext)
	 */
	public IComponent[] getComponents(KbQuery query, IPageContext context) {
		String prefix = getPrefix(query, context);
		return getComponents(query, prefix, context);
	}

	protected String getPrefix(KbQuery query, IPageContext context) {
		String prefix = null;
		Map<String, INameSpace> nameSpaces = context.getNameSpaces(query.getOffset());
		if(nameSpaces!=null) {
			INameSpace nameSpace = nameSpaces.get(getURI());
			if(nameSpace!=null) {
				prefix = nameSpace.getPrefix();
			}
		}
		return prefix;
	}

	private static final IComponent[] EMPTY_ARRAY = new IComponent[0];

	protected IComponent[] getComponents(KbQuery query, String prefix, IPageContext context) {
		String fullTagName = null;
		boolean mask = false;
		if(query.getType()==KbQuery.Type.TAG_NAME) {
			fullTagName = query.getValue();
			mask = query.isMask();
		} else {
			fullTagName = query.getLastParentTag();
		}
		if(fullTagName == null) {
			return EMPTY_ARRAY;
		}
		if(mask) {
			if(fullTagName.length()==0) {
				return getExtendedComponents(context);
			}
			if(prefix==null) {
				return getComponents(fullTagName, context);
			}
		}
		String tagName = fullTagName;
		int prefixIndex = fullTagName.indexOf(':');
		String queryPrefix = null;
		if(prefix!=null && prefixIndex>-1) {
			queryPrefix = fullTagName.substring(0, prefixIndex);
			if(prefixIndex<fullTagName.length()-1) {
				tagName = fullTagName.substring(prefixIndex+1);
			} else {
				tagName = null;
			}
		}
		if(mask) {
			if(prefixIndex<0) {
				if(prefix.startsWith(tagName)) {
					return getExtendedComponents(context);
				}
				return EMPTY_ARRAY;
			}
			if(prefix.equals(queryPrefix)) {
				if(tagName == null) {
					return getExtendedComponents(context);
				}
				return getComponents(tagName, context);
			}
			return EMPTY_ARRAY;
		}
		IComponent comp = getComponent(tagName);
		if(checkExtended(comp, context)) {
			return new IComponent[]{comp};
		}
		return EMPTY_ARRAY;
	}

	protected IComponent[] getExtendedComponents(IPageContext context) {
		if(hasExtenededComponents) {
			Set<IComponent> comps = new HashSet<IComponent>();
			synchronized(components) {
				for (IComponent component : components.values()) {
					if(checkExtended(component, context)) {
						comps.add(component);
					}
				}
			}
			return comps.toArray(new IComponent[0]);
		}
		return getComponents();
	}

	protected boolean checkExtended(IComponent component, IPageContext context) {
		if(!component.isExtended()) {
			return true;
		}
		ITagLibrary[] libs = context.getLibraries();
		for (int i = 0; i < libs.length; i++) {
			if(libs[i]!=this && libs[i].getURI().equals(uri)) {
				IComponent ac = libs[i].getComponent(component.getName());
				if(!ac.isExtended()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds component to tag lib.
	 * @param component
	 */
	public void addComponent(IComponent component) {
		adopt((KbObject)component);
		components.put(component.getName(), component);
		componentsArray=null;
		if(component.isExtended()) {
			hasExtenededComponents = true;
		}
	}

	/**
	 * @param components the components to set
	 */
	protected void setComponents(Map<String, IComponent> components) {
		this.components = components;
		for (IComponent component : components.values()) {
			if(component.isExtended()) {
				hasExtenededComponents = true;
				break;
			}
		}
		componentsArray=null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#getSourcePath()
	 */
	@Override
	public IPath getSourcePath() {
		return source;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#setSourcePath(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public void setSourcePath(IPath source) {
		this.source = source;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.TagLibrary#getResource()
	 */
	public IResource getResource() {
		if(resource != null) return resource;
		if(source != null) {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(source);
		}
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(IFile resource) {
		this.resource = resource;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.IComponent#getNameSpace()
	 */
	public INameSpace getDefaultNameSpace() {
		return nameSpace;
	}

	/**
	 * @param nameSpace the name space to set
	 */
	public void setDefaultNameSpace(INameSpace nameSpace) {
		this.nameSpace = nameSpace;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.TagLibrary#getURI()
	 */
	public String getURI() {
		return uri;
	}

	/**
	 * @param uri the URI to set
	 */
	public void setURI(String uri) {
		this.uri = uri;
	}

	public void setURI(IValueInfo s) {
		uri = s == null ? null : s.getValue();
		attributesInfo.put(URI, s);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.taglib.ITagLibrary#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.ProposalProcessor#getProposals(org.jboss.tools.jst.web.kb.KbQuery, org.jboss.tools.jst.web.kb.PageContext)
	 */
	public TextProposal[] getProposals(KbQuery query, IPageContext context) {
		String prefix = getPrefix(query, context);
		List<TextProposal> proposals = new ArrayList<TextProposal>();
		IComponent[] components = getComponents(query, prefix, context);
		if(query.getType() == KbQuery.Type.TAG_NAME) {
			for (int i = 0; i < components.length; i++) {
				proposals.add(getProposal(prefix, components[i]));
			}
		} else {
			for (int i = 0; i < components.length; i++) {
				if (components[i] == null)
					continue;
				TextProposal[] componentProposals  = components[i].getProposals(query, context);
				for (int j = 0; j < componentProposals.length; j++) {
					proposals.add(componentProposals[j]);
				}
			}
		}
		return proposals.toArray(new TextProposal[proposals.size()]);
	}

	protected TextProposal getProposal(String prefix, IComponent component) {
		TextProposal proposal = new TextProposal();
		proposal.setContextInfo(component.getDescription());
		StringBuffer label = new StringBuffer();
		if(prefix!=null) {
			label.append(prefix + KbQuery.PREFIX_SEPARATOR);
		}
		label.append(component.getName());
		proposal.setLabel(label.toString());

		IAttribute[] attributes = component.getPreferableAttributes();
		StringBuffer attributeSB = new StringBuffer();
		for (int j = 0; j < attributes.length; j++) {
			attributeSB.append(" ").append(attributes[j].getName()).append("=\"\"");
		}
		label.append(attributeSB);
		if(!component.canHaveBody()) {
			label.append(" /");
		}

		proposal.setReplacementString(label.toString());

		int position = proposal.getReplacementString().indexOf('"');
		if(position!=-1) {
			position ++;
		} else {
			position = proposal.getReplacementString().length();
		}
		proposal.setPosition(position);
		return proposal;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#clone()
	 */
	@Override
	public AbstractTagLib clone() throws CloneNotSupportedException {
		AbstractTagLib t = (AbstractTagLib)super.clone();
		t.components = new HashMap<String, IComponent>();
		for (IComponent c: components.values()) {
			if(c.isExtended()) {
				t.hasExtenededComponents = true;
			}
			t.addComponent(((AbstractComponent)c).clone());
		}
		t.components.putAll(components);
		return t;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#merge(org.jboss.tools.jst.web.kb.internal.KbObject)
	 */
	@Override
	public List<Change> merge(KbObject s) {
		List<Change> changes = super.merge(s);
		//TODO
		return changes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#getXMLName()
	 */
	@Override
	public String getXMLName() {
		return KbXMLStoreConstants.TAG_LIBRARY;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#toXML(org.w3c.dom.Element, java.util.Properties)
	 */
	@Override
	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);

		XModelObject old = pushModelObject(context);

		saveAttributeValues(element);

		for (IComponent c: components.values()) {
			((KbObject)c).toXML(element, context);
		}

		popModelObject(context, old);
		return element;
	}

	protected void saveAttributeValues(Element element) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.KbObject#loadXML(org.w3c.dom.Element, java.util.Properties)
	 */
	@Override
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);

		XModelObject old = pushModelObject(context);

		loadAttributeValues(element);

		Element[] cs = XMLUtilities.getChildren(element, KbXMLStoreConstants.TAG_COMPONENT);
		for (Element e: cs) {
			String cls = e.getAttribute(XMLStoreConstants.ATTR_CLASS);
			AbstractComponent c = null;
			if(KbXMLStoreConstants.CLS_TLD_LIBRARY.equals(cls)) {
				c = new TLDTag();
			} else if(KbXMLStoreConstants.CLS_FACELET_LIBRARY.equals(cls)) {
				c = new FaceletTag();
			} else if(KbXMLStoreConstants.CLS_FACESCONFIG_LIBRARY.equals(cls)) {
				c = new FacesConfigComponent();
			} else {
				//consider other cases;
			}
			if(c != null) {
				c.loadXML(e, context);
				addComponent(c);
			}
		}

		popModelObject(context, old);
	}

	protected void loadAttributeValues(Element element) {
		setURI(attributesInfo.get(URI));
	}
}
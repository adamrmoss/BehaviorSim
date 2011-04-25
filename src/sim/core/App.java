/*
 * BehaviorSim - version 1.0 
 * 
 * Copyright (C) 2010 The BehaviorSim Development Team, fasheng@cs.gsu.edu.
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * 
 * Info, Questions, Suggestions & Bugs Report to fasheng@cs.gsu.edu.
 *  
 */

package sim.core;

import java.util.ArrayList;
import java.util.List;

import sim.core.dclass.DynamicManager;
import sim.model.behavior.BehaviorNetwork;
import sim.model.entity.BNCategory;
import sim.model.entity.Category;

/**
 * A user-defined application object. It is used to wrap general application
 * information.
 * 
 * @author Fasheng Qiu
 * @version 1.0
 * @since 03/07/2009
 */
public class App implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -622276748073653471L;

	// Application Engine
	private static AppEngine engine = AppEngine.getInstance();

	// Application Identification, for application management
	private long appId = System.currentTimeMillis();

	// Application is dirty or not
	private boolean isDirty = false;

	// Application is not saved
	public static final int DIRTY = 0;

	// Application saved
	public static final int CLEAN = 1;

	// Application name
	private String appName = "";

	// Application configuration file
	private String appFileName = null;

	// Application directory full-path
	private String appDir = null;

	// Application resource directory name, currently it resides in the
	// application directory
	private String appResourceDir = engine.system.systemParameters
			.getResourcePath();

	// Application listeners for various events
	private List listeners = null;

	// Application dynamic manager
	public DynamicManager dm = null;

	// Application class loader
	private ClassLoader cl = null;

	// Current active entity
	public Category currentEntity = null;

	// Temporary category object, it is only used in the copy-paste of category
	// definition
	// panel, see @{sim.ui.panels.DefineCategoryPanel}
	public Category copiedCategory = null;

	/**
	 * Constructor
	 */
	App() {
		listeners = new ArrayList();
		cl = new AppClassLoader();
	}

	/**
	 * Register an application listener
	 * 
	 * @param appListener
	 *            Application listener to register
	 */
	public void registerAppListener(AppListener appListener) {
		listeners.add(appListener);
	}

	/**
	 * Construct the application. The dynamic manager is setup.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		this.dm = new DynamicManager(cl);
		for (int i = 0; i < listeners.size(); i++) {
			AppListener l = (AppListener) listeners.get(i);
			l.onInitEvent(appId);
		}
	}

	/**
	 * Destroy the application
	 */
	public void destroy() {
		for (int i = 0; i < listeners.size(); i++) {
			AppListener l = (AppListener) listeners.get(i);
			l.onDestroyEvent(appId);
		}
	}

	/**
	 * @return the appId
	 */
	public long getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(long appId) {
		this.appId = appId;
	}

	/**
	 * @return the isDirty
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * @param isDirty
	 *            the isDirty to set
	 */
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the appFile
	 */
	public String getAppFileName() {
		return appFileName;
	}

	/**
	 * @param appFile
	 *            the appFile to set
	 */
	public void setAppFileName(String appFile) {
		this.appFileName = appFile;
	}

	/**
	 * @return the appDir
	 */
	public String getAppDir() {
		return appDir;
	}

	/**
	 * @param appDir
	 *            the appDir to set
	 */
	public void setAppDir(String appDir) {
		this.appDir = appDir;
	}

	/**
	 * @return the appResourceDir
	 */
	public String getAppResourceDir() {
		return appResourceDir;
	}

	/**
	 * @param appResourceDir
	 *            the appResourceDir to set
	 */
	public void setAppResourceDir(String appResourceDir) {
		this.appResourceDir = appResourceDir;
	}

	/**
	 * @return the dm
	 */
	public DynamicManager getDm() {
		return dm;
	}

	/**
	 * @param dm
	 *            the dm to set
	 */
	public void setDm(DynamicManager dm) {
		this.dm = dm;
	}

	/**
	 * Set the current active entity. Generally it is used to set the currently
	 * computing entity whose methods may be invoked later
	 * 
	 * @param e
	 *            The current active entity
	 */
	public void setCurrentEntity(Category e) {
		this.currentEntity = e;
	}

	/**
	 * Return the currently active entity
	 * 
	 * @return the currently active entity
	 */
	public Category getCurrentEntity() {
		return this.currentEntity;
	}

	/**
	 * Return the current behavior network
	 * 
	 * @return the current behavior network
	 */
	public BehaviorNetwork getCurrentBehaviorNetwork() {
		return ((BNCategory) currentEntity).getBehaviorNetwork();
	}

	/**
	 * Update the current behavior network of the current entity.
	 * 
	 * @param bn
	 *            The updated behavior network
	 */
	public void updateCurrentBehaviorNetwork(BehaviorNetwork bn) {
		((BNCategory) currentEntity).setBehaviorNetwork(bn);
	}

}

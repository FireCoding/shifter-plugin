/*
 * Copyright 2011-2016 Kay Stenschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kstenschke.shifter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.kstenschke.shifter.resources.StaticTexts;
import com.kstenschke.shifter.resources.forms.ShifterConfiguration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

@State(name = "ShifterSettingsState", storages = {@Storage(id = "shifter", file = StoragePathMacros.APP_CONFIG + "/shifter.xml")})
class ShifterApplicationComponent implements PersistentStateComponent<ShifterApplicationComponent>, ExportableApplicationComponent { //, ApplicationComponent, Configurable {

    private ShifterConfiguration settingsPanel = null;

	/**
	 * Constructor
     */
    public ShifterApplicationComponent() {

    }

    public static ShifterApplicationComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(ShifterApplicationComponent.class);
    }

    @Override
    public ShifterApplicationComponent getState() {
        return this;
    }

    @Override
    public void loadState(ShifterApplicationComponent state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    /**
     * @return  JComponent
     */
    public JComponent createComponent() {
        if (settingsPanel == null) {
            settingsPanel = new ShifterConfiguration();
        }

        settingsPanel.init();

        reset();

        return settingsPanel.getRootPanel();
    }

    @Nls
    public String getDisplayName() {
		return StaticTexts.SETTINGS_DISPLAY_NAME;
	}

    public boolean isModified() {
        return settingsPanel != null && settingsPanel.isModified();
    }

    public void disposeUIResources() {
		settingsPanel = null;
	}

    public void reset() {

    }

    public void apply() throws ConfigurationException {
        if (settingsPanel != null) {
            settingsPanel.apply();
        }
    }

    public String getHelpTopic() {
		return null;
	}

    public void initComponent() {
        if(settingsPanel != null) settingsPanel.init();
    }

    public void disposeComponent() {
		settingsPanel = null;
	}

    @NotNull
    public String getComponentName() {
		return StaticTexts.SETTINGS_COMPONENT_NAME;
	}

    @NotNull
    @Override
    public File[] getExportFiles() {
        return new File[] {PathManager.getOptionsFile("shifter")};
    }

    @NotNull
    @Override
    public String getPresentableName() {
        return "shifter";
    }
}

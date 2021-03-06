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

package com.kstenschke.shifter.resources.forms;

import com.kstenschke.shifter.listeners.ListenerRestoreSettings;
import com.kstenschke.shifter.models.ShifterPreferences;
import com.kstenschke.shifter.utils.UtilsFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

public class ShifterConfiguration {

    private JPanel rootPanel;
    private JTextArea textAreaDictionaryTerms;
    private JTextArea textAreaDictionaryExpressions;
    private JCheckBox checkBoxPreserveCase;

    private JRadioButton radioButtonCaseSensitive;
    private JRadioButton radioButtonCaseInsensitive;
    private JRadioButton radioButtonShiftInSeconds;
    private JRadioButton radioButtonShiftInMilliseconds;
    private JScrollPane scrollPaneDictionaryTerms;
    private JPanel jPanelOptions;
    private JPanel jPanelTopBar;
    private JSpinner spinnerShiftMore;
    private JTextField restoreSettings;
    private JScrollPane scrollPaneDictionaryExpressions;
    private JTextPane thisDictionaryConfiguresShiftableTextPane;
    private JTextPane thisDictionaryDefinesShiftableTextPane;

    public Boolean hasSomethingChanged = false;

    /**
     * Constructor
     */
    public ShifterConfiguration() {
        init();
    }

    public void init() {
        textAreaDictionaryTerms.setTabSize(2);
        textAreaDictionaryExpressions.setTabSize(2);

        refreshFormValues();
        initFormListeners();
    }

    private void initFormListeners() {
        ChangeListener somethingChangedListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                hasSomethingChanged = true;
            }
        };

        radioButtonShiftInSeconds.addChangeListener(somethingChangedListener);
        radioButtonShiftInMilliseconds.addChangeListener(somethingChangedListener);
        radioButtonCaseSensitive.addChangeListener(somethingChangedListener);
        radioButtonCaseInsensitive.addChangeListener(somethingChangedListener);

        KeyListener keyListenerSomethingChanged = getKeyListenerSomethingChanged();
        textAreaDictionaryTerms.addKeyListener(keyListenerSomethingChanged);
        textAreaDictionaryExpressions.addKeyListener(keyListenerSomethingChanged);

        restoreSettings.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restoreSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        ListenerRestoreSettings listenerRestoreSettings = new ListenerRestoreSettings(this);
        restoreSettings.addMouseListener(listenerRestoreSettings);
    }

    @NotNull
    private KeyListener getKeyListenerSomethingChanged() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
                hasSomethingChanged = true;
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        };
    }

    /**
     * Refresh settings and dictionary content from stored preference or factory default
     */
    public void refreshFormValues() {
        int shiftMoreValue = ShifterPreferences.getShiftMoreSize();
        this.spinnerShiftMore.setModel( new SpinnerNumberModel(shiftMoreValue, 2, 999, 1));

        if( ShifterPreferences.getSortingMode().equals(ShifterPreferences.SORTING_MODE_CASE_SENSITIVE)) {
            this.radioButtonCaseSensitive.setSelected(true);
        } else {
            this.radioButtonCaseInsensitive.setSelected(true);
        }

        if( ShifterPreferences.getShiftingModeOfTimestamps().equals(ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS)) {
            this.radioButtonShiftInSeconds.setSelected(true);
        } else {
            this.radioButtonShiftInMilliseconds.setSelected(true);
        }

        boolean isActivePreserveCase  = ShifterPreferences.getIsActivePreserveCase();
        this.checkBoxPreserveCase.setSelected(isActivePreserveCase);

        String termsDictionary   = ShifterPreferences.getTermsDictionary();
        if( termsDictionary == null || termsDictionary.isEmpty() )  {
            termsDictionary = getDefaultTerms();
        }
        textAreaDictionaryTerms.setText(termsDictionary);

        String expressionsDictionary   = ShifterPreferences.getExpressionsDictionary();
        if( expressionsDictionary == null || expressionsDictionary.isEmpty() )  {
            expressionsDictionary = getDefaultExpressions();
        }
        textAreaDictionaryExpressions.setText(expressionsDictionary);
    }

    public String getDefaultTerms() {
        //@note for the .txt resource to be included in the jar, it must be set in compiler resource settings
        InputStream dictionaryStream= this.getClass().getResourceAsStream("terms.txt");

        return dictionaryStream == null ? "" : UtilsFile.getFileStreamAsString(dictionaryStream);
    }

    public String getDefaultExpressions() {
        //@note for the .txt resource to be included in the jar, it must be set in compiler resource settings
        InputStream dictionaryStream= this.getClass().getResourceAsStream("expressions.txt");

        return dictionaryStream == null ? "" : UtilsFile.getFileStreamAsString(dictionaryStream);
    }

    /**
     * Reset default settings
     */
    public void reset() {
        spinnerShiftMore.setValue(10);
        radioButtonCaseInsensitive.setSelected(true);
        radioButtonShiftInSeconds.setSelected(true);

        this.textAreaDictionaryTerms.setText(getDefaultTerms());
        this.textAreaDictionaryExpressions.setText(getDefaultExpressions());
    }

    /**
     * @return  JPanel
     */
    public JPanel getRootPanel() {
        return rootPanel;
    }

    public String getShiftMoreSize() {
        return spinnerShiftMore.getValue().toString();
    }

    /**
     * @return  Integer     Sorting mode
     */
    private Integer getSelectedSortingMode() {
        return radioButtonCaseSensitive.isSelected()
                ? ShifterPreferences.SORTING_MODE_CASE_SENSITIVE
                : ShifterPreferences.SORTING_MODE_CASE_INSENSITIVE;
    }

    public Integer getSelectedShiftingModeOfTimestamps() {
        return radioButtonShiftInSeconds.isSelected()
                ? ShifterPreferences.SHIFTING_MODE_TIMESTAMP_SECONDS
                : ShifterPreferences.SHIFTING_MODE_TIMESTAMP_MILLISECONDS;
    }

    /**
     * @return  boolean
     */
    public boolean getIsActivePreserveCase() {
        return checkBoxPreserveCase.isSelected();
    }

    /**
     * Config modified?
     *
     * @return boolean
     */
    public boolean isModified() {
        return    this.hasSomethingChanged
               || Integer.parseInt( this.spinnerShiftMore.getValue().toString()) != ShifterPreferences.getShiftMoreSize()
               || ! this.textAreaDictionaryTerms.getText().equals( ShifterPreferences.getTermsDictionary() )
               || ! ShifterPreferences.getSortingMode().equals( this.getSelectedSortingMode() )
               || ! ShifterPreferences.getIsActivePreserveCase().equals(this.checkBoxPreserveCase.isSelected()
               || ! ShifterPreferences.getShiftingModeOfTimestamps().equals( this.getSelectedShiftingModeOfTimestamps())
        );
    }

    /**
     * @return  String
     */
    public String getDictionary() {
        return this.textAreaDictionaryTerms.getText();
    }

    public void apply() {
        // Store configuration
        ShifterPreferences.saveShiftMoreSize(this.getShiftMoreSize());
        ShifterPreferences.saveSortingMode(this.getSelectedSortingMode());
        ShifterPreferences.saveIsActivePreserveCase(this.getIsActivePreserveCase());
        ShifterPreferences.saveShiftingModeTimestamps(this.getSelectedShiftingModeOfTimestamps());
        // Store dictionary
        String dictionary = this.getDictionary();
        if( dictionary != null ) {
            ShifterPreferences.saveDictionary(dictionary);
        }

        this.hasSomethingChanged = false;
    }

}
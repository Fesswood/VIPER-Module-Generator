package com.github.fesswood.ui;

import com.github.fesswood.data.ModuleMetaData;
import com.github.fesswood.ui.common.CreateModuleView;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.event.*;

/**
 * Created by fesswood on 21.09.16.
 */
public class ViperOption implements CreateModuleView {
    private JCheckBox cbInteractor;
    private JCheckBox cbRepository;
    private JTextField tfModuleName;
    private JPanel jpRoot;
    private JTextField tfModelName;
    private JCheckBox cbFragmentPresenter;
    private JCheckBox cbActivity;
    // private FormFilledListener formFilledListener;


    public ViperOption() {
   /*     tfModuleName.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (!(e.getKeyChar() == 27 || e.getKeyChar() == 65535)) {
                    if(e.getKeyCode() == 0){
                        tfModelName.setText(tfModuleName.getDocument().
                    }
                    tfModelName.setText(tfModelName.getText() + e.getKeyChar());
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }
        });*/
        tfModuleName.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                tfModelName.setText(tfModuleName.getText());
                //whatever you want
            }
            public void removeUpdate(DocumentEvent e) {
                    tfModelName.setText(tfModuleName.getText());
                //whatever you want
            }
            public void insertUpdate(DocumentEvent e) {
                tfModelName.setText(tfModuleName.getText());
            }
        });
        cbRepository.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED){
                tfModelName.setEnabled(true);
                tfModelName.setText(tfModuleName.getText());
            }
            else if(e.getStateChange() == ItemEvent.DESELECTED){
                tfModelName.setEnabled(false);
                tfModelName.setText("");
            }
        });
    }

    @Override
    @Nullable
    public ValidationInfo validate() {
        String text = tfModuleName.getText();
        String badChars = "/^[^/:*?\"<>\\|]+$/"; // forbidden characters \ / : * ? " < > |"
        String dot = "/^\\./"; // cannot start with dot (.)"
        String badNames = "/^(nul|prn|con|lpt[0-9]|com[0-9])(.|$)/i"; // forbidden file names"


        if (text.isEmpty()) {
            return new ValidationInfo("Имя модуля не может быть пустым!", tfModuleName);
        }
        if (text.matches(badChars) || text.startsWith(dot) || text.matches(badNames)) {
            return new ValidationInfo("Имя модуля не дебильным!", tfModuleName);
        }
        return null;
    }

    @NotNull
    @Override
    public ModuleMetaData getModuleMetaData() {
        boolean isNeedInteracor = cbInteractor.isSelected();
        boolean isNeedRepository = cbRepository.isSelected();
        boolean isNeedActivity = cbActivity.isSelected();
        boolean isNeedFragmentPresenter = cbFragmentPresenter.isSelected();
        String moduleName = tfModuleName.getText();
        String modelName = tfModelName.getText();
        return new ModuleMetaData(isNeedRepository, isNeedInteracor, isNeedActivity, isNeedFragmentPresenter,
                                    moduleName, modelName);
    }

    @Override
    public JComponent getRootView() {
        return jpRoot;
    }


}

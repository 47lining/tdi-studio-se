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
package org.talend.designer.core.ui.editor.properties.controllers;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.CorePlugin;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.EParameterFieldType;
import org.talend.core.model.process.IElementParameter;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.properties.controllers.generator.IDynamicProperty;
import org.talend.librariesmanager.model.ModulesNeededProvider;

/**
 * DOC yzhang class global comment. Detailled comment <br/>
 * 
 * $Id: ColumnListController.java 1 2006-12-12 下午02:04:32 +0000 (下午02:04:32) yzhang $
 * 
 */
public class ModuleListController extends AbstractElementPropertySectionController {

    private static final String MODULE = "MODULE"; //$NON-NLS-1$

    /**
     * DOC dev ColumnListController constructor comment.
     * 
     * @param parameterBean
     */
    public ModuleListController(IDynamicProperty dp) {
        super(dp);
    }

    public Command createCommand(Button button) {
        FileDialog dial = new FileDialog(composite.getShell(), SWT.NONE);
        dial.setFilterExtensions(FilesUtils.getAcceptJARFilesSuffix());
        String file = dial.open();
        if (file != null) {
            if (!file.equals("")) { //$NON-NLS-1$
                String propertyName = (String) button.getData(PARAMETER_NAME);
                String lastSegment = Path.fromOSString(file).lastSegment();
                if (!elem.getPropertyValue(propertyName).equals(lastSegment)) {
                    try {
                        CorePlugin.getDefault().getLibrariesService().deployLibrary(Path.fromOSString(file).toFile().toURL());
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                    }
                    return new PropertyChangeCommand(elem, propertyName, lastSegment);
                }
            }
        }
        return null;
    }

    public Command createCommand(SelectionEvent selectionEvent) {
        Set<String> elementsName;
        Control ctrl;
        elementsName = hashCurControls.keySet();
        for (String name : elementsName) {
            Object o = hashCurControls.get(name);
            if (o instanceof Control) {
                ctrl = (Control) o;
                if (ctrl == null) {
                    hashCurControls.remove(name);
                    return null;
                }

                if (ctrl.equals(selectionEvent.getSource()) && ctrl instanceof CCombo) {
                    boolean isDisposed = ((CCombo) ctrl).isDisposed();
                    if (!isDisposed && (!elem.getPropertyValue(name).equals(((CCombo) ctrl).getText()))) {

                        String value = new String(""); //$NON-NLS-1$
                        for (int i = 0; i < elem.getElementParameters().size(); i++) {
                            IElementParameter param = elem.getElementParameters().get(i);
                            if (param.getName().equals(name)) {
                                for (int j = 0; j < param.getListItemsValue().length; j++) {
                                    if (((CCombo) ctrl).getText().equals(param.getListItemsDisplayName()[j])) {
                                        value = (String) param.getListItemsValue()[j];
                                    }
                                }
                            }
                        }
                        if (value.equals(elem.getPropertyValue(name))) { // same value so no need to do anything
                            return null;
                        }
                        CorePlugin.getDefault().getLibrariesService().resetModulesNeeded();
                        return new PropertyChangeCommand(elem, name, value);
                    }
                }

            }
        }
        return null;
    }

    IControlCreator cbCtrl = new IControlCreator() {

        public Control createControl(final Composite parent, final int style) {
            CCombo cb = new CCombo(parent, style);
            return cb;
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.editor.properties2.editors.AbstractElementPropertySectionController#createControl()
     */
    @Override
    public Control createControl(final Composite subComposite, final IElementParameter param, final int numInRow,
            final int nbInRow, final int top, final Control lastControl) {
        if (param.getDisplayName().startsWith("!!")) {
            if (param.getField() == EParameterFieldType.MODULE_LIST) {
                param.setDisplayName(EParameterName.MODULE_LIST.getDisplayName());
            }
        }

        DecoratedField dField = new DecoratedField(subComposite, SWT.BORDER, cbCtrl);
        if (param.isRequired()) {
            FieldDecoration decoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                    FieldDecorationRegistry.DEC_REQUIRED);
            dField.addFieldDecoration(decoration, SWT.RIGHT | SWT.TOP, false);
        }

        Control cLayout = dField.getLayoutControl();
        CCombo combo = (CCombo) dField.getControl();

        combo.setEditable(false);
        cLayout.setBackground(subComposite.getBackground());
        combo.setEnabled(!param.isReadOnly());
        combo.addSelectionListener(listenerSelection);
        if (elem instanceof Node) {
            combo.setToolTipText(VARIABLE_TOOLTIP + param.getVariableName());
        }

        CLabel labelLabel = getWidgetFactory().createCLabel(subComposite, param.getDisplayName());
        FormData data = new FormData();
        if (lastControl != null) {
            data.left = new FormAttachment(lastControl, 0);
        } else {
            data.left = new FormAttachment((((numInRow - 1) * MAX_PERCENT) / nbInRow), 0);
        }
        data.top = new FormAttachment(0, top);
        labelLabel.setLayoutData(data);
        if (numInRow != 1) {
            labelLabel.setAlignment(SWT.RIGHT);
        }
        // *********************
        data = new FormData();
        int currentLabelWidth = STANDARD_LABEL_WIDTH;
        GC gc = new GC(labelLabel);
        Point labelSize = gc.stringExtent(param.getDisplayName());
        gc.dispose();

        if ((labelSize.x + ITabbedPropertyConstants.HSPACE) > currentLabelWidth) {
            currentLabelWidth = labelSize.x + ITabbedPropertyConstants.HSPACE;
        }

        if (numInRow == 1) {
            if (lastControl != null) {
                data.left = new FormAttachment(lastControl, currentLabelWidth);
            } else {
                data.left = new FormAttachment(0, currentLabelWidth);
            }

        } else {
            data.left = new FormAttachment(labelLabel, 0, SWT.RIGHT);
        }
        data.top = new FormAttachment(0, top);
        cLayout.setLayoutData(data);
        Point initialSize = dField.getLayoutControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);

        Button btnEdit = getWidgetFactory().createButton(subComposite, "", SWT.PUSH); //$NON-NLS-1$
        btnEdit.setImage(CorePlugin.getImageDescriptor(DOTS_BUTTON).createImage());

        data = new FormData();
        data.left = new FormAttachment(cLayout, 0, SWT.RIGHT);
        data.right = new FormAttachment(cLayout, ITabbedPropertyConstants.HSPACE + STANDARD_BUTTON_WIDTH, SWT.RIGHT);
        data.top = new FormAttachment(0, top);
        data.height = STANDARD_HEIGHT - 2;
        btnEdit.setLayoutData(data);
        btnEdit.setData(NAME, MODULE);
        btnEdit.setData(PARAMETER_NAME, param.getName());
        btnEdit.setEnabled(!param.isReadOnly());
        btnEdit.addSelectionListener(listenerSelection);

        // **********************
        hashCurControls.put(param.getName(), combo);
        updateData();
        // this.dynamicTabbedPropertySection.updateColumnList(null);

        dynamicProperty.setCurRowSize(initialSize.y + ITabbedPropertyConstants.VSPACE);
        return cLayout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController#estimateRowSize(org.eclipse.swt.widgets.Composite,
     * org.talend.core.model.process.IElementParameter)
     */
    @Override
    public int estimateRowSize(Composite subComposite, IElementParameter param) {
        DecoratedField dField = new DecoratedField(subComposite, SWT.BORDER, cbCtrl);
        Point initialSize = dField.getLayoutControl().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        dField.getLayoutControl().dispose();

        return initialSize.y + ITabbedPropertyConstants.VSPACE;
    }

    private void updateData() {
        if (elem instanceof Node) {
            updateModuleList((Node) elem);
        }
    }

    private void updateModuleList(Node node) {
        List<ModuleNeeded> moduleNeededList = ModulesNeededProvider.getModulesNeeded();
        Set<String> moduleList = new HashSet<String>();
        for (ModuleNeeded module : moduleNeededList) {
            moduleList.add(module.getModuleName());
        }

        String[] moduleNameList = moduleList.toArray(new String[0]);

        for (int i = 0; i < node.getElementParameters().size(); i++) {
            IElementParameter param = node.getElementParameters().get(i);
            if (param.getField() == EParameterFieldType.MODULE_LIST) {
                param.setListItemsDisplayName(moduleNameList);
                param.setListItemsValue(moduleNameList);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
    }

    SelectionListener listenerSelection = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent event) {
            if (event.getSource() instanceof Button) {
                Command cmd = createCommand((Button) event.getSource());
                if (cmd != null) {
                    getCommandStack().execute(cmd);
                }
            } else {
                Command cmd = createCommand(event);
                if (cmd != null) {
                    getCommandStack().execute(cmd);
                }
            }
        }
    };

    @Override
    public void refresh(IElementParameter param, boolean check) {
        updateData();

        String[] curNameList = param.getListItemsDisplayName();

        Object value = param.getValue();

        CCombo combo = (CCombo) hashCurControls.get(param.getName());

        combo.setItems(curNameList);
        if (value instanceof String) {
            combo.setText((String) value);
        }
    }
}

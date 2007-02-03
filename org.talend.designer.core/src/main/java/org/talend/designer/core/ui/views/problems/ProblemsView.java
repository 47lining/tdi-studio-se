// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006 Talend - www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.core.ui.views.problems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.talend.commons.ui.image.EImage;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.commons.utils.data.container.MapList;
import org.talend.core.model.process.Problem;
import org.talend.core.model.process.Problem.ProblemStatus;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.ui.MultiPageTalendEditor;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.process.ProcessPart;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class ProblemsView extends ViewPart {

    private TreeItem warningItem, errorItem;

    private Map<Problem, TreeItem> warningsMap, errorsMap;

    private Image errorImage;

    private Image warningImage;

    private static final String ERRORS_TEXT = Messages.getString("ProblemsView.errorTitle"); //$NON-NLS-1$

    private static final String WARNINGS_TEXT = Messages.getString("ProblemsView.waringTitle"); //$NON-NLS-1$

    public ProblemsView() {

        warningsMap = new HashMap<Problem, TreeItem>();
        errorsMap = new HashMap<Problem, TreeItem>();

        warningImage = ImageProvider.getImage(EImage.WARNING_SMALL);
        errorImage = ImageProvider.getImage(EImage.ERROR_SMALL);

        setPartName(""); //$NON-NLS-1$
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());
        final Tree tree = new Tree(parent, SWT.SINGLE);
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.addListener(SWT.Selection, new Listener() {

            public void handleEvent(Event event) {
                TreeItem[] items = tree.getSelection();
                if (items.length > 0) {
                    if (warningsMap.containsValue(items[0])) {
                        for (Problem problem : warningsMap.keySet()) {
                            TreeItem item = warningsMap.get(problem);
                            if (item.equals(items[0])) {
                                if (problem.getElement() instanceof Node) {
                                    selectInEditor((Node) problem.getElement());
                                }
                            }
                        }
                    }

                    if (errorsMap.containsValue(items[0])) {
                        for (Problem problem : errorsMap.keySet()) {
                            TreeItem item = errorsMap.get(problem);
                            if (item.equals(items[0])) {
                                if (problem.getElement() instanceof Node) {
                                    selectInEditor((Node) problem.getElement());
                                }
                            }
                        }
                    }
                }
            }
        });
        TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
        column1.setText(Messages.getString("ProblemsView.name")); //$NON-NLS-1$
        column1.setWidth(200);
        column1.setResizable(true);
        TreeColumn column2 = new TreeColumn(tree, SWT.CENTER);
        column2.setText(Messages.getString("ProblemsView.description")); //$NON-NLS-1$
        column2.setWidth(400);
        column2.setAlignment(SWT.LEFT);
        column2.setResizable(true);

        warningItem = new TreeItem(tree, SWT.NONE);
        warningItem.setText(new String[] { WARNINGS_TEXT + " (0)", "" }); //$NON-NLS-1$ //$NON-NLS-2$
        warningItem.setImage(ImageProvider.getImage(EImage.HIERARCHY_ICON));
        errorItem = new TreeItem(tree, SWT.NONE);
        errorItem.setImage(ImageProvider.getImage(EImage.HIERARCHY_ICON));
        errorItem.setText(new String[] { ERRORS_TEXT + " (0)", "" }); //$NON-NLS-1$ //$NON-NLS-2$

        Problems.recheckCurrentProblems(this);
    }

    @SuppressWarnings("unchecked")//$NON-NLS-1$
    private void selectInEditor(Node node) {
        IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

        if (editorPart instanceof MultiPageTalendEditor) {
            MultiPageTalendEditor multi = (MultiPageTalendEditor) editorPart;
            GraphicalViewer viewer = multi.getTalendEditor().getViewer();
            Object object = viewer.getRootEditPart().getChildren().get(0);
            if (object instanceof ProcessPart) {
                for (EditPart editPart : (List<EditPart>) ((ProcessPart) object).getChildren()) {
                    if (editPart instanceof NodePart) {
                        if (((NodePart) editPart).getModel().equals(node)) {
                            viewer.select(editPart);
                            multi.setFocus();
                        }
                    }
                }
            }
        }
    }

    public void clearAll() {
        TreeItem[] treeItems = errorItem.getItems();
        for (int i = 0; i < treeItems.length; i++) {
            treeItems[i].dispose();
        }
        errorsMap.clear();
        errorItem.setText(new String[] { ERRORS_TEXT + " (0)", "" }); //$NON-NLS-1$ //$NON-NLS-2$

        treeItems = warningItem.getItems();
        for (int i = 0; i < treeItems.length; i++) {
            treeItems[i].dispose();
        }
        warningsMap.clear();
        warningItem.setText(new String[] { WARNINGS_TEXT + " (0)", "" }); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * DOC retset problemsTree with the new problems.
     * 
     * @param problems the new problems
     */
    public void setProblems(MapList<ProblemStatus, Problem> problems) {
        clearAll();
        ProblemStatus status;
        List<Problem> problemList;
        Iterator it = problems.keySet().iterator();
        while (it.hasNext()) {
            status = (ProblemStatus) it.next();
            problemList = problems.get(status);
            for (Problem problem : problemList) {
                addProblem(status, problem);
            }
        }
    }

    public void addProblem(ProblemStatus status, Problem problem) {
        TreeItem subItem;
        int nbItems;

        switch (status) {
        case ERROR:
            subItem = new TreeItem(errorItem, SWT.NONE);
            subItem.setImage(errorImage);
            subItem.setText(new String[] { problem.getElement().getElementName(), problem.getDescription() });

            errorsMap.put(problem, subItem);
            nbItems = errorsMap.size();
            errorItem.setText(new String[] { ERRORS_TEXT + " (" + nbItems + ")", "" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            break;
        case WARNING:
            subItem = new TreeItem(warningItem, SWT.NONE);
            subItem.setImage(warningImage);
            subItem.setText(new String[] { problem.getElement().getElementName(), problem.getDescription() });

            warningsMap.put(problem, subItem);
            nbItems = warningsMap.size();
            warningItem.setText(new String[] { WARNINGS_TEXT + " (" + nbItems + ")", "" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            break;
        default:
        }
    }

    public void removeProblem(ProblemStatus status, Problem problem) {
        int nbItems;
        Set<Problem> setProblems;
        List<Problem> problemsToRemove = new ArrayList<Problem>();

        switch (status) {
        case ERROR:
            setProblems = errorsMap.keySet();

            for (Problem currentProblem : setProblems) {
                if (currentProblem.equals(problem)) {
                    TreeItem item = errorsMap.get(currentProblem);
                    problemsToRemove.add(currentProblem);
                    item.dispose();
                }
            }

            for (Problem currentProblem : problemsToRemove) {
                errorsMap.remove(currentProblem);
            }

            nbItems = errorsMap.size();
            errorItem.setText(new String[] { ERRORS_TEXT + " (" + nbItems + ")", "" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            break;
        case WARNING:
            setProblems = warningsMap.keySet();

            for (Problem currentProblem : setProblems) {
                if (currentProblem.equals(problem)) {
                    TreeItem item = warningsMap.get(currentProblem);
                    problemsToRemove.add(currentProblem);
                    item.dispose();
                }
            }

            for (Problem currentProblem : problemsToRemove) {
                warningsMap.remove(currentProblem);
            }

            nbItems = warningsMap.size();
            warningItem.setText(new String[] { WARNINGS_TEXT + " (" + nbItems + ")", "" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            break;
        default:
        }
    }

    public List<Problem> getProblemList(ProblemStatus status) {
        Set<Problem> setProblems;
        List<Problem> problemList = new ArrayList<Problem>();

        switch (status) {
        case ERROR:
            setProblems = errorsMap.keySet();

            for (Problem problem : setProblems) {
                problemList.add(problem);
            }
            break;
        case WARNING:
            setProblems = warningsMap.keySet();

            for (Problem problem : setProblems) {
                problemList.add(problem);
            }
            break;
        default:
        }
        return problemList;
    }

    @Override
    public void setFocus() {
    }

    public void setPartName(String title) {
        String viewName = Messages.getString("ProblemsView.problems.defaultTitle"); //$NON-NLS-1$

        if (!title.equals("")) { //$NON-NLS-1$
            viewName = viewName + "(" + title + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        super.setPartName(viewName);
    }
}

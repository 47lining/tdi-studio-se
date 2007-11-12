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
package org.talend.designer.core.ui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.talend.designer.core.i18n.Messages;
import org.talend.designer.core.ui.editor.TalendEditor;
import org.talend.designer.core.ui.editor.cmd.MultiplePasteCommand;
import org.talend.designer.core.ui.editor.cmd.NodesPasteCommand;
import org.talend.designer.core.ui.editor.cmd.NotesPasteCommand;
import org.talend.designer.core.ui.editor.connections.ConnLabelEditPart;
import org.talend.designer.core.ui.editor.nodes.NodeLabelEditPart;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.notes.NoteEditPart;

/**
 * DOC nrousseau class global comment. Detailled comment <br/>
 * 
 * $Id$
 * 
 */
public class GEFPasteAction extends SelectionAction {

    /**
     * DOC nrousseau NodesPasteAction constructor comment.
     * 
     * @param part
     */
    public GEFPasteAction(IWorkbenchPart part) {
        super(part);
        setId(ActionFactory.PASTE.getId());
        setText(Messages.getString("NodesPasteAction.paste")); //$NON-NLS-1$
        ISharedImages sharedImages = part.getSite().getWorkbenchWindow().getWorkbench().getSharedImages();
        setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.ui.actions.WorkbenchPartAction#calculateEnabled()
     */
    @Override
    protected boolean calculateEnabled() {
        Object o = Clipboard.getDefault().getContents();

        org.eclipse.swt.dnd.Clipboard systemClipboard = new org.eclipse.swt.dnd.Clipboard(Display.getCurrent());
        Object systemObject = systemClipboard.getContents(TextTransfer.getInstance());

        if (o == null && systemObject != null && systemObject instanceof String) {
            return true;
        }

        if (o instanceof String) {
            return true;
        }

        if (!(o instanceof List)) {
            return false;
        }
        List objects = (List) o;
        if (objects.isEmpty()) {
            return false;
        }
        for (Object currentObject : objects) {
            if (!(currentObject instanceof NodePart) && !(currentObject instanceof NoteEditPart)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")//$NON-NLS-1$
    public void run() {
        Object clipBoardContent = Clipboard.getDefault().getContents();

        org.eclipse.swt.dnd.Clipboard systemClipboard = new org.eclipse.swt.dnd.Clipboard(Display.getCurrent());
        Object systemObject = systemClipboard.getContents(TextTransfer.getInstance());

        if (clipBoardContent instanceof List) {
            List<EditPart> partsList = (List<EditPart>) Clipboard.getDefault().getContents();
            if (partsList == null || partsList.isEmpty()) {
                return;
            }

            List<NodePart> nodeParts = new ArrayList<NodePart>();
            List<NoteEditPart> noteParts = new ArrayList<NoteEditPart>();

            for (Object o : partsList) {
                if (o instanceof NodePart) {
                    nodeParts.add((NodePart) o);
                } else if (o instanceof NoteEditPart) {
                    noteParts.add((NoteEditPart) o);
                }
            }

            TalendEditor editor = (TalendEditor) this.getWorkbenchPart();
            if (nodeParts.size() != 0 && noteParts.size() != 0) {

                MultiplePasteCommand mpc = new MultiplePasteCommand(nodeParts, noteParts, editor.getProcess());
                execute(mpc);
            } else if (nodeParts.size() != 0) {
                NodesPasteCommand cmd = new NodesPasteCommand(nodeParts, editor.getProcess());
                execute(cmd);
            } else if (noteParts.size() != 0) {
                NotesPasteCommand cmd = new NotesPasteCommand(noteParts, editor.getProcess());
                execute(cmd);
            }
        } else if (clipBoardContent instanceof String) {
            List objects = getSelectedObjects();

            if (objects.size() == 1) {
                String content = (String) clipBoardContent;
                if (objects.get(0) instanceof NoteEditPart
                        && ((NoteEditPart) objects.get(0)).getDirectEditManager() != null) {
                    Text text = ((NoteEditPart) objects.get(0)).getDirectEditManager().getTextControl();
                    if (text != null) {
                        text.insert(content);
                    }
                } else if (objects.get(0) instanceof ConnLabelEditPart
                        && ((ConnLabelEditPart) objects.get(0)).getDirectEditManager() != null) {
                    Text text = ((ConnLabelEditPart) objects.get(0)).getDirectEditManager().getTextControl();
                    if (text != null) {
                        text.insert(content);
                    }
                } else if (objects.get(0) instanceof NodeLabelEditPart
                        && ((NodeLabelEditPart) objects.get(0)).getDirectEditManager() != null) {
                    {
                        Text text = (Text) ((NodeLabelEditPart) objects.get(0)).getDirectEditManager().getCellEditor()
                                .getControl();
                        if (text != null) {
                            text.insert(content);
                        }
                    }

                }
            }
        } else if (systemObject != null && systemObject instanceof String) {
            List objects = getSelectedObjects();

            if (objects.size() == 1) {
                String content = (String) systemObject;
                if (objects.get(0) instanceof NoteEditPart
                        && ((NoteEditPart) objects.get(0)).getDirectEditManager() != null) {
                    Text text = ((NoteEditPart) objects.get(0)).getDirectEditManager().getTextControl();
                    if (text != null) {
                        text.insert(content);
                    }
                } else if (objects.get(0) instanceof ConnLabelEditPart
                        && ((ConnLabelEditPart) objects.get(0)).getDirectEditManager() != null) {
                    Text text = ((ConnLabelEditPart) objects.get(0)).getDirectEditManager().getTextControl();
                    if (text != null) {
                        text.insert(content);
                    }
                } else if (objects.get(0) instanceof NodeLabelEditPart
                        && ((NodeLabelEditPart) objects.get(0)).getDirectEditManager() != null) {
                    {
                        Text text = (Text) ((NodeLabelEditPart) objects.get(0)).getDirectEditManager().getCellEditor()
                                .getControl();
                        if (text != null) {
                            text.insert(content);
                        }
                    }

                }
            }
        }
    }
}

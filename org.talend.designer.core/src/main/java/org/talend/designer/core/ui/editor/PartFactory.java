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
package org.talend.designer.core.ui.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.talend.designer.core.ui.editor.connections.ConnLabelEditPart;
import org.talend.designer.core.ui.editor.connections.Connection;
import org.talend.designer.core.ui.editor.connections.ConnectionLabel;
import org.talend.designer.core.ui.editor.connections.ConnectionPart;
import org.talend.designer.core.ui.editor.connections.ConnectionPerformance;
import org.talend.designer.core.ui.editor.connections.ConnectionPerformanceEditPart;
import org.talend.designer.core.ui.editor.connections.ConnectionTrace;
import org.talend.designer.core.ui.editor.connections.ConnectionTraceEditPart;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainer;
import org.talend.designer.core.ui.editor.nodecontainer.NodeContainerPart;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodeLabel;
import org.talend.designer.core.ui.editor.nodes.NodeLabelEditPart;
import org.talend.designer.core.ui.editor.nodes.NodePart;
import org.talend.designer.core.ui.editor.nodes.NodePerformance;
import org.talend.designer.core.ui.editor.nodes.NodePerformanceEditPart;
import org.talend.designer.core.ui.editor.notes.Note;
import org.talend.designer.core.ui.editor.notes.NoteEditPart;
import org.talend.designer.core.ui.editor.process.Process;
import org.talend.designer.core.ui.editor.process.ProcessPart;

/**
 * The PartFactory will create an EditPart factory for each model object that is created in the diagram. <br/>
 * 
 * $Id$
 * 
 */
public class PartFactory implements EditPartFactory {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
     */
    public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;

        if (model instanceof Process) {
            part = new ProcessPart();
        } else if (model instanceof Node) {
            part = new NodePart();
        } else if (model instanceof Connection) {
            part = new ConnectionPart();
        } else if (model instanceof ConnectionLabel) {
            part = new ConnLabelEditPart();
        } else if (model instanceof ConnectionPerformance) {
            part = new ConnectionPerformanceEditPart();
        } else if (model instanceof ConnectionTrace) {
            part = new ConnectionTraceEditPart();
        } else if (model instanceof NodeLabel) {
            part = new NodeLabelEditPart();
        } else if (model instanceof NodeContainer) {
            part = new NodeContainerPart();
        } else if (model instanceof NodePerformance) {
            part = new NodePerformanceEditPart();
        } else if (model instanceof Note) {
            part = new NoteEditPart();
        } else {
            return null;
        }
        // tell the newly created part about the model object
        part.setModel(model);

        return part;
    }
}

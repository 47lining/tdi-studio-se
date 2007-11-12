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
package org.talend.sqlbuilder.ui;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.ui.swt.colorstyledtext.ColorManager;
import org.talend.commons.ui.swt.colorstyledtext.ColorStyledText;
import org.talend.commons.ui.swt.proposal.StyledTextContentAdapter;
import org.talend.core.CorePlugin;
import org.talend.repository.model.RepositoryNode;
import org.talend.sqlbuilder.Messages;
import org.talend.sqlbuilder.SqlBuilderPlugin;
import org.talend.sqlbuilder.ui.proposal.SQLEditorLabelProvider;
import org.talend.sqlbuilder.ui.proposal.SQLEditorProposalAdapter;
import org.talend.sqlbuilder.ui.proposal.SQLEditorProposalProvider;

/**
 * qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend-code-templates.xml 1 2007-3-7 上午10:52:44 (星期五, 29 九月 2006) qzhang $
 * 
 */
public class SqlEditDialog extends Dialog {

    private RepositoryNode repositoryNode;

    private String title;

    private StyledText colorText;

    private final String language = "tsql";

    private String sql;

    /**
     * qzhang SqlEditDialog constructor comment.
     * 
     * @param parentShell
     */
    public SqlEditDialog(Shell parentShell, String title, String sql, RepositoryNode repositoryNode) {
        super(parentShell);
        this.title = title;
        this.sql = sql;
        this.repositoryNode = repositoryNode;
        setShellStyle(SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE | SWT.RESIZE | SWT.MIN | SWT.MAX);
    }

    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.getString("GUIModificationQueryAction.TextDialog.TitleText"));
        if (title != null) {
            newShell.setText(title);
        }
        newShell.setSize(600, 400);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite control = (Composite) super.createDialogArea(parent);

        // create text viewer
        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = true;
        gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = GridData.FILL;
        gid.verticalAlignment = GridData.FILL;
        control.setLayoutData(gid);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        control.setLayout(gridLayout);
        ColorManager colorManager = new ColorManager(CorePlugin.getDefault().getPreferenceStore());
        colorText = new ColorStyledText(control, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, colorManager, language);
        Font font = new Font(control.getDisplay(), "courier", 10, SWT.NONE); //$NON-NLS-1$
        colorText.setFont(font);

        GridData gd = new GridData(GridData.FILL_BOTH);
        colorText.setLayoutData(gd);
        colorText.setText(sql);
        colorText.addModifyListener(new ModifyListener() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
             */
            public void modifyText(ModifyEvent e) {
                sql = colorText.getText();
            }
        });
        createEditorProposal();
        return control;
    }

    /**
     * Creates proposal for editor.
     */
    private void createEditorProposal() {
        try {
            // create KeyStroke use Ctrl+Space
            KeyStroke keyStroke = KeyStroke.getInstance("Ctrl+Space"); //$NON-NLS-1$
            IControlContentAdapter controlContentAdapter = new StyledTextContentAdapter();
            IContentProposalProvider contentProposalProvider = new SQLEditorProposalProvider(repositoryNode, language);

            SQLEditorProposalAdapter contentProposalAdapter = new SQLEditorProposalAdapter(colorText, controlContentAdapter,
                    contentProposalProvider, keyStroke, new char[] { ' ', '.' });
            contentProposalAdapter.setPropagateKeys(true);
            contentProposalAdapter.setFilterStyle(ContentProposalAdapter.FILTER_CUMULATIVE);
            contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
            contentProposalAdapter.setLabelProvider(new SQLEditorLabelProvider());
            contentProposalAdapter.setAutoActivationDelay(10);
            contentProposalAdapter.setPopupSize(new Point(300, 200));
        } catch (Exception e) {
            SqlBuilderPlugin.log(Messages.getString("SQLBuilderEditorComposite.logMessage"), e); //$NON-NLS-1$
        }
    }

    public String getSql() {
        return this.sql;
    }
}

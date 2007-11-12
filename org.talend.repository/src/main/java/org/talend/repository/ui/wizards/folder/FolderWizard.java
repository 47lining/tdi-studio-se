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
package org.talend.repository.ui.wizards.folder;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.ui.images.ECoreImage;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.ui.wizards.genHTMLDoc.DocumentationHelper;

/**
 * Wizard for the creation of a new project. <br/>
 * 
 * $Id$
 * 
 */
public class FolderWizard extends Wizard {

    /** Main page. */
    private FolderWizardPage mainPage;

    private IPath path;

    private ERepositoryObjectType type;

    private final String defaultLabel;

    /**
     * Constructs a new NewProjectWizard.
     * 
     * @param author Project author.
     * @param server
     * @param password
     */
    public FolderWizard(IPath path, ERepositoryObjectType type, String defaultLabel) {
        super();
        this.path = path;
        this.type = type;
        this.defaultLabel = defaultLabel;
        setDefaultPageImageDescriptor(ImageProvider.getImageDesc(ECoreImage.FOLDER_WIZ));
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
        mainPage = new FolderWizardPage(defaultLabel);
        addPage(mainPage);
        setWindowTitle(Messages.getString("NewFolderWizard.windowTitle")); //$NON-NLS-1$
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        String folderName = mainPage.getName();

        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();

        boolean isSyncWithDocumentationNode = DocumentationHelper.isSyncWithDocumentation();
        boolean isPathNotExisting = DocumentationHelper.isPathValid(ERepositoryObjectType.JOBS, path, folderName);
        try {

            if (defaultLabel == null) {
                repositoryFactory.createFolder(type, path, folderName);

                // Check in Documentation node also.
                if (type != ERepositoryObjectType.DOCUMENTATION && isSyncWithDocumentationNode && isPathNotExisting ) {
                    repositoryFactory.createFolder(ERepositoryObjectType.JOBS, path, folderName);
                }
            } else {
                repositoryFactory.renameFolder(type, path, folderName);
                isPathNotExisting = DocumentationHelper.isPathValid(ERepositoryObjectType.JOBS, path, defaultLabel);
                if (type == ERepositoryObjectType.PROCESS && isSyncWithDocumentationNode && !isPathNotExisting) {
                    repositoryFactory.renameFolder(ERepositoryObjectType.JOBS, path, folderName);
                }
            }
            return true;
        } catch (PersistenceException e) {
            MessageDialog.openError(getShell(), Messages.getString("NewFolderWizard.failureTitle"), Messages //$NON-NLS-1$
                    .getString("NewFolderWizard.failureText")); //$NON-NLS-1$ //$NON-NLS-2$
            ExceptionHandler.process(e);
            return false;
        }
    }

    public boolean isValid(String folderName) {
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        try {
            if (defaultLabel == null) {
                return repositoryFactory.isPathValid(type, path, folderName);
            } else {
                return repositoryFactory.isPathValid(type, path.removeLastSegments(1), folderName);
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            return false;
        }
    }
}

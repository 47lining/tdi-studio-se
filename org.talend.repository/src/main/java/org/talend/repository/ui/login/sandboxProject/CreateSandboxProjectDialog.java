// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.ui.login.sandboxProject;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.image.ImageProvider;
import org.talend.commons.ui.swt.formtools.LabelledCombo;
import org.talend.commons.ui.swt.formtools.LabelledText;
import org.talend.commons.utils.PasswordHelper;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.Project;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.User;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.repository.i18n.Messages;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.ui.wizards.newproject.NewProjectWizardPage;

/**
 * ggu class global comment. Detailled comment
 */
public class CreateSandboxProjectDialog extends TitleAreaDialog {

    /*
     * project settings
     */

    private Group projectGroup, projectSvnGroup;

    private LabelledCombo languageCombo;

    private LabelledText projectLabelText, projectDescText, projectSvnUrlText, projectSvnLoginText, projectSvnPassText;

    /*
     * user settings
     */

    private Group userGroup, userSvnGroup;

    private LabelledText userLoginText, userPassText, userFirstNameText, userLastNameText, userDefaultSvnUserText,
            userDefaultSvnPassText;

    public CreateSandboxProjectDialog(Shell parentShell) {
        super(parentShell);
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        ImageDescriptor imgDesc = brandingService.getLoginHImage();
        if (imgDesc != null) {
            setTitleImage(ImageProvider.getImage(imgDesc));
        }
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        IBrandingService brandingService = (IBrandingService) GlobalServiceRegister.getDefault().getService(
                IBrandingService.class);
        String fullProductName = brandingService.getFullProductName();
        newShell.setText(Messages.getString("CreateSandboxProjectDialog.Title", fullProductName)); //$NON-NLS-1$
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        createProjectInfors(composite);
        addProjectListeners();
        addUserListeners();
        return composite;
    }

    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);
        checkFields();
        return contents;
    }

    private void createProjectInfors(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        projectGroup = new Group(composite, SWT.NONE);
        projectGroup.setLayout(new GridLayout(2, false));
        projectGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        projectLabelText = new LabelledText(projectGroup, Messages.getString("CreateSandboxProjectDialog.ProjectLabel")); //$NON-NLS-1$
        GridData layoutData = new GridData();
        layoutData.widthHint = 180;
        layoutData.minimumWidth = 180;
        projectLabelText.setLayoutData(layoutData);

        projectDescText = new LabelledText(projectGroup,
                Messages.getString("CreateSandboxProjectDialog.ProjectDesc"), 1, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.heightHint = 30;
        layoutData.minimumHeight = 30;
        projectDescText.setLayoutData(layoutData);

        languageCombo = new LabelledCombo(projectGroup, Messages.getString("NewProjectWizardPage.language"), null, new String[] { //$NON-NLS-1$
                ECodeLanguage.JAVA.getName(), ECodeLanguage.PERL.getName() });
        layoutData = new GridData();
        layoutData.widthHint = 80;
        layoutData.minimumWidth = 80;
        languageCombo.getCombo().setLayoutData(layoutData);
        languageCombo.select(0); // default for java

        projectSvnGroup = new Group(projectGroup, SWT.NONE);
        projectSvnGroup.setText(Messages.getString("CreateSandboxProjectDialog.ProjectSvnAdvance")); //$NON-NLS-1$
        projectSvnGroup.setLayout(new GridLayout(4, false));
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        projectSvnGroup.setLayoutData(layoutData);

        projectSvnUrlText = new LabelledText(projectSvnGroup, Messages.getString("CreateSandboxProjectDialog.svnUrl"), 3); //$NON-NLS-1$
        projectSvnLoginText = new LabelledText(projectSvnGroup, Messages.getString("CreateSandboxProjectDialog.Login")); //$NON-NLS-1$
        projectSvnPassText = new LabelledText(projectSvnGroup, Messages.getString("CreateSandboxProjectDialog.Password")); //$NON-NLS-1$
        projectSvnPassText.getTextControl().setEchoChar('*');
        // user
        Composite userComp = new Composite(projectGroup, SWT.NONE);
        GridData userLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        userLayoutData.horizontalSpan = 2;
        userComp.setLayoutData(userLayoutData);
        userComp.setLayout(new GridLayout());

        createUserInfors(userComp);

    }

    private void addProjectListeners() {

        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFields();
            }
        };
        projectLabelText.addModifyListener(listener);
        projectDescText.addModifyListener(listener);
        projectSvnUrlText.addModifyListener(listener);
        projectSvnLoginText.addModifyListener(listener);
    }

    private void createUserInfors(Composite parent) {
        userGroup = new Group(parent, SWT.NULL);
        userGroup.setText(Messages.getString("CreateSandboxProjectDialog.createUserLabel")); //$NON-NLS-1$
        userGroup.setLayout(new GridLayout(4, false));
        userGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        userLoginText = new LabelledText(userGroup, Messages.getString("CreateSandboxProjectDialog.Login")); //$NON-NLS-1$
        userPassText = new LabelledText(userGroup, Messages.getString("CreateSandboxProjectDialog.Password")); //$NON-NLS-1$
        userPassText.getTextControl().setEchoChar('*');

        userFirstNameText = new LabelledText(userGroup, Messages.getString("CreateSandboxProjectDialog.userFirstname")); //$NON-NLS-1$
        userLastNameText = new LabelledText(userGroup, Messages.getString("CreateSandboxProjectDialog.userLastname")); //$NON-NLS-1$

        userSvnGroup = new Group(userGroup, SWT.NONE);
        userSvnGroup.setText(Messages.getString("CreateSandboxProjectDialog.userSvnSettingLabel")); //$NON-NLS-1$
        userSvnGroup.setLayout(new GridLayout(4, false));
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 4;
        userSvnGroup.setLayoutData(layoutData);

        userDefaultSvnUserText = new LabelledText(userSvnGroup, Messages.getString("CreateSandboxProjectDialog.Login")); //$NON-NLS-1$
        userDefaultSvnPassText = new LabelledText(userSvnGroup, Messages.getString("CreateSandboxProjectDialog.Password")); //$NON-NLS-1$
        userDefaultSvnPassText.getTextControl().setEchoChar('*');
    }

    private void addUserListeners() {
        ModifyListener listener = new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                checkFields();
            }
        };
        userLoginText.addModifyListener(listener);
        userPassText.addModifyListener(listener);
    }

    private void checkFields() {

        // check field
        String projectLabel = projectLabelText.getText();
        if (projectLabel.length() == 0) {
            setErrorMessage(Messages.getString("NewProjectWizardPage.nameEmpty")); //$NON-NLS-1$
        } else {
            if (!Pattern.matches(RepositoryConstants.PROJECT_PATTERN, projectLabel)
                    || NewProjectWizardPage.isKeywords(projectLabel.toLowerCase())
                    || ECodeLanguage.JAVA.getName().equalsIgnoreCase(projectLabel)) {
                setErrorMessage(Messages.getString("NewProjectWizardPage.illegalCharacter")); //$NON-NLS-1$
            } else if (isProjectNameAlreadyUsed(projectLabel)) {
                setErrorMessage(Messages.getString("NewProjectWizardPage.projectNameAlredyExists")); //$NON-NLS-1$
            } else if (projectSvnUrlText.getText().length() == 0) {
                setErrorMessage(Messages.getString("CreateSandboxProjectDialog.URLMessage")); //$NON-NLS-1$
            } else if (projectSvnLoginText.getText().length() == 0) {
                setErrorMessage(Messages.getString("CreateSandboxProjectDialog.userLoginMessage")); //$NON-NLS-1$
            } else if ((userLoginText.getText().length() == 0 || !Pattern.matches(RepositoryConstants.MAIL_PATTERN, userLoginText
                    .getText()))) {
                setErrorMessage(Messages.getString("CreateSandboxProjectDialog.userLoginValidMessage")); //$NON-NLS-1$
            } else if (userPassText.getText().length() == 0) {
                setErrorMessage(Messages.getString("CreateSandboxProjectDialog.userPasswordEmptyMessage")); //$NON-NLS-1$
            } else {
                setErrorMessage(null);
            }
        }
    }

    public void setErrorMessage(String newErrorMessage) {
        super.setErrorMessage(newErrorMessage);
        Button button = getButton(IDialogConstants.OK_ID);
        if (button != null && !button.isDisposed()) {
            button.setEnabled(newErrorMessage == null);
        }

    }

    private boolean isProjectNameAlreadyUsed(String newProjectName) {
        IProxyRepositoryFactory repositoryFactory = ProxyRepositoryFactory.getInstance();
        Project[] projects = null;
        try {
            projects = repositoryFactory.readProject();
        } catch (Exception e) {
            return true;
        }
        for (Project project : projects) {
            if (Project.createTechnicalName(newProjectName).compareTo(project.getTechnicalLabel()) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void okPressed() {
        //
        Project newProject = new Project();
        newProject.setLabel(projectLabelText.getText());
        // newProject.setTechnicalLabel( Project.createTechnicalName(newProject.getLabel()));
        newProject.setDescription(projectDescText.getText());
        newProject.setLanguage(ECodeLanguage.getCodeLanguage(languageCombo.getText()));

        User newUser = PropertiesFactory.eINSTANCE.createUser();
        newUser.setLogin(userLoginText.getText());
        newUser.setFirstName(userFirstNameText.getText());
        newUser.setLastName(userLastNameText.getText());
        try {
            newUser.setPassword(PasswordHelper.encryptPasswd(userPassText.getText()));
        } catch (NoSuchAlgorithmException e) {
            ExceptionHandler.process(e);
        }

        newProject.setAuthor(newUser); // set in project to record

        boolean ok = false;
        try {
            ok = CorePlugin.getDefault().getRepositoryService().getProxyRepositoryFactory().createSandboxProject(newProject,
                    projectSvnUrlText.getText(), projectSvnLoginText.getText(), projectSvnPassText.getText(),
                    userDefaultSvnUserText.getText(), userDefaultSvnPassText.getText());
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
            MessageDialog
                    .openError(
                            getShell(),
                            Messages.getString("CreateSandboxProjectDialog.Failure"), Messages.getString("CreateSandboxProjectDialog.failureMessage")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (ok) { // if not created, will don't close the dialog
            MessageDialog
                    .openInformation(
                            getShell(),
                            Messages.getString("CreateSandboxProjectDialog.successTitile"), Messages.getString("CreateSandboxProjectDialog.successMessage")); //$NON-NLS-1$ //$NON-NLS-2$
            super.okPressed();
        }
    }

}

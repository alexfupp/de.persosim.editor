package de.persosim.editor.ui.editor;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.MasterFileIdentifier;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.utils.HexString;

public class PersoEditorView {
	public static final String ID = "de.persosim.editor.e4.ui.plugin.partdescriptor.persoeditor";
	private TabFolder tabFolder;

	public void updateContent(Path personalizationFile) {
		if (!Files.exists(personalizationFile)) {
			throw new IllegalArgumentException("Personalization file does not exist");
		}

		try (Reader reader = Files.newBufferedReader(personalizationFile)) {
			Personalization perso = (Personalization) PersonalizationFactory.unmarshal(reader);
			updateContent(perso);
		} catch (IOException e) {
			BasicLogger.logException(getClass(), "Reading the personalization file failed.", e, LogLevel.ERROR);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "File error", "Reading the personalization file failed.");
		}
	}

	public void updateContent(Personalization perso) {
		updateUi(perso);
	}

	private void updateUi(Personalization perso) {
		TabItem tbtmmf = new TabItem(tabFolder, SWT.NONE);
		tbtmmf.setText("Masterfile");
		Composite editor = new Composite(tabFolder, SWT.NONE);
		DatagroupEditorBuilder.build(editor, perso, null);
		tbtmmf.setControl(editor);

		tbtmmf = new TabItem(tabFolder, SWT.NONE);
		tbtmmf.setText("ePassport");
		editor = new Composite(tabFolder, SWT.NONE);
		DatagroupEditorBuilder.build(editor, perso, new DedicatedFileIdentifier(HexString.toByteArray(DefaultPersonalization.AID_EPA)));
		tbtmmf.setControl(editor);

		tbtmmf = new TabItem(tabFolder, SWT.NONE);
		tbtmmf.setText("eID");
		editor = new Composite(tabFolder, SWT.NONE);
		DatagroupEditorBuilder.build(editor, perso, new DedicatedFileIdentifier(HexString.toByteArray(DefaultPersonalization.AID_EID)));
		tbtmmf.setControl(editor);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		parent.setLayout(gl_parent);

		Group grpData = new Group(parent, SWT.NONE);
		grpData.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpData.setText("Data");
		grpData.setBounds(0, 0, 66, 66);

		tabFolder = new TabFolder(grpData, SWT.NONE);

		Group grpControl = new Group(parent, SWT.NONE);
		grpControl.setLayout(new RowLayout(SWT.HORIZONTAL));
		grpControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpControl.setText("Signature Options");

		Button btnResignEfCardSecurity = new Button(grpControl, SWT.CHECK);
		btnResignEfCardSecurity.setText("Resign EF.CardSecurity");

		Button btnResignEfchipsecurity = new Button(grpControl, SWT.CHECK);
		btnResignEfchipsecurity.setText("Resign EF.ChipSecurity");

		Button btnUpdateEfsod = new Button(grpControl, SWT.CHECK);
		btnUpdateEfsod.setText("Update EF.SOD");
	}

	@Focus
	public void setFocus() {
	}

	@Persist
	void doSave(@Optional IProgressMonitor monitor) {
	}
}

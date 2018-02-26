package de.persosim.editor.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.handlers.HandlerProvider;
import de.persosim.editor.ui.editor.handlers.ObjectHandler;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.TypeIdentifier;

public class DfEditor {

	private Tree dfTree;

	public DfEditor(Composite viewer, DedicatedFile df, NewEditorCallback editor, boolean compress,
			HandlerProvider provider) {

		viewer.setLayout(new FillLayout());

		dfTree = new Tree(viewer, SWT.NONE);

		dfTree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!(e.item instanceof TreeItem)) {
					return;
				}

				Composite localEditor = editor.getParent();
				localEditor.setLayout(new GridLayout(1, false));
				showEditor((TreeItem) e.item, localEditor);
				localEditor.requestLayout();
				localEditor.pack();
				editor.done();
			}

			private void showEditor(TreeItem item, Composite localEditor) {
				ObjectHandler objectHandler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);

				if (objectHandler != null) {
					objectHandler.createEditor(localEditor, item);
				}

				localEditor.pack();
				localEditor.requestLayout();
				localEditor.redraw();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		Menu menu = new Menu(dfTree);
		dfTree.setMenu(menu);
		
		ObjectHandler dfHandler = provider.get(df);
		if (dfHandler != null){
			TreeItem dummy = new TreeItem(dfTree, SWT.NONE);
			dfHandler.createMenu(menu, dummy);
			dummy.dispose();
		}
		
		menu.addMenuListener(new MenuListener() {
			
			@Override
			public void menuShown(MenuEvent e) {
				editor.getParent();
			}
			
			@Override
			public void menuHidden(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		for (CardObject elementaryFile : df.findChildren(new TypeIdentifier(ElementaryFile.class))) {
			provider.get(elementaryFile).createItem(dfTree, elementaryFile, provider);
		}
		
		show();

		dfTree.pack();
	}

	public void show() {
		for (TreeItem current : dfTree.getItems()) {
			dfTree.showItem(current);
			show(current.getItems());
		}
	}

	private void show(TreeItem[] items) {
		for (TreeItem current : items) {
			dfTree.showItem(current);
			show(current.getItems());
		}
	}

	public void persist() {
		for (TreeItem current : dfTree.getItems()) {
			ObjectHandler handler = (ObjectHandler) current.getData(ObjectHandler.HANDLER);
			if (handler != null) {
				handler.persist(current);
			}
			
			persist(current.getItems());
		}
	}

	private void persist(TreeItem[] items) {
		for (TreeItem current : items) {
			ObjectHandler handler = (ObjectHandler) current.getData(ObjectHandler.HANDLER);
			if (handler != null) {
				handler.persist(current);
			}
			
			persist(current.getItems());
		}
	}
}

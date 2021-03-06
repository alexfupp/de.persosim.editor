package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.utils.HexString;

public class DatagroupDumpHandler extends AbstractObjectHandler {

	
	private Map<Integer, String> dgMapping;

	public DatagroupDumpHandler(Map<Integer, String> dgMapping) {
		this.dgMapping = dgMapping;
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			return true;
		}
		return false;
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof ElementaryFile) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((ElementaryFile) object, provider, item);
			return item;
		}
		return null;
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof ElementaryFile) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((ElementaryFile) object, provider, item);
			return item;
		}
		return null;
	}

	protected void handleItem(ElementaryFile ef, HandlerProvider provider, TreeItem item) {
		item.setData(ef);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile) item.getData();
			Integer sfid = null;
			for (CardObjectIdentifier current : ef.getAllIdentifiers()) {
				if (current instanceof ShortFileIdentifier) {
					sfid = ((ShortFileIdentifier) current).getShortFileIdentifier();
					break;
				}
			}

			String newText = "";
			if (dgMapping.containsKey(sfid)) {
				newText = dgMapping.get(sfid);
			} else {
				newText = "DG " + sfid;
			}
			
			item.setText(newText + getChangedText(item));
		}
	}

	@Override
	protected String getType() {
		return "elementary file, not editable file content dump";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(1, false));
		if (item.getData() instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile) item.getData();
			Text text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
			text.setFont(JFaceResources.getTextFont());
			
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			text.setLayoutData(layoutData);
			text.setEditable(false);
			try {
				text.setText(HexString.dump(ef.getContent()));
			} catch (AccessDeniedException e) {
				text.setText(e.getMessage());
			}
		}
	}

	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		super.createMenu(menu, item);
		MenuItem mitem = new MenuItem(menu, SWT.NONE);
		mitem.setText("Remove datagroup");
		mitem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementaryFile ef = (ElementaryFile) item.getData();
				try {
					ef.getParent().removeChild(ef);
					item.dispose();
				} catch (AccessDeniedException e1) {
					BasicLogger.logException(getClass(), e1, LogLevel.WARN);
				}
			}
		});
	}
}

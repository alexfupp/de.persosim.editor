package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;

public class DatagroupHandler extends DatagroupDumpHandler {

	public static final String EXTRACTED_TLV = "EXTRACTED_TLV";
	
	public DatagroupHandler(Map<Integer, String> dgMapping) {
		super(dgMapping);
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			return true;
		}
		return false;
	}
	
	@Override
	protected final void handleItem(ElementaryFile ef, HandlerProvider provider, TreeItem item) {
		item.setData(ef);
		setText(item);
		item.setData(HANDLER, this);
		try {
			TlvDataObject tlvObject = TlvDataObjectFactory.createTLVDataObject(ef.getContent());
			item.setData(EXTRACTED_TLV, tlvObject);
			handleItem(provider, item, tlvObject);
		} catch (AccessDeniedException e) {
			BasicLogger.logException(getClass(), e, LogLevel.WARN);
		}
	}

	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		ObjectHandler handler = provider.get(tlvObject);
		if (handler != null) {
			handler.createItem(item, tlvObject, provider);
		}
	}
	
	
	@Override
	protected String getType() {
		return "elementary file, not editable";
	}
	
	@Override
	public void persist(TreeItem item) {
		ElementaryFile ef = (ElementaryFile) item.getData();
		TlvDataObject tlv = (TlvDataObject) item.getData(EXTRACTED_TLV);
		
		if (tlv != null) {
			try {
				ef.setContent(tlv.toByteArray());
			} catch (AccessDeniedException e) {
				BasicLogger.logException(getClass(), e, LogLevel.WARN);
			}
		}
	}

}

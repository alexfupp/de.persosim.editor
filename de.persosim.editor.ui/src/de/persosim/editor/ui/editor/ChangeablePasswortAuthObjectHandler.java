package de.persosim.editor.ui.editor;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.LengthChecker;
import de.persosim.editor.ui.editor.checker.NumberChecker;
import de.persosim.editor.ui.editor.handlers.EditorFieldHelper;
import de.persosim.editor.ui.editor.handlers.ObjectHandler;
import de.persosim.editor.ui.editor.handlers.PasswordAuthObjectHandler;
import de.persosim.editor.ui.editor.handlers.TlvModifier;
import de.persosim.simulator.cardobjects.ChangeablePasswordAuthObject;
import de.persosim.simulator.cardobjects.Iso7816LifeCycleState;
import de.persosim.simulator.cardobjects.PasswordAuthObjectWithRetryCounter;
import de.persosim.simulator.exception.AccessDeniedException;

public class ChangeablePasswortAuthObjectHandler extends PasswordAuthObjectHandler implements ObjectHandler {

	public ChangeablePasswortAuthObjectHandler(Collection<Integer> allowedIds) {
		super(allowedIds);
	}
	
	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		
		ChangeablePasswordAuthObject authObject = ((ChangeablePasswordAuthObject)item.getData());
		
		EditorFieldHelper.createField(item, false, composite, new TlvModifier() {
			
			@Override
			public void setValue(String string) {
				try {
					authObject.updateLifeCycleState(Iso7816LifeCycleState.CREATION_OPERATIONAL_ACTIVATED);
					authObject.setPassword(string.getBytes(StandardCharsets.US_ASCII));
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}
			
			@Override
			public void remove() {
				try {
					authObject.updateLifeCycleState(Iso7816LifeCycleState.CREATION_OPERATIONAL_DEACTIVATED);
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}
			
			@Override
			public String getValue() {
				return new String(authObject.getPassword(), StandardCharsets.US_ASCII);
			}
		}, new AndChecker(new NumberChecker(), new LengthChecker(5,6)), authObject.getPasswordName() + ", possible lengths are 5 or 6 characters");
		
		if (authObject instanceof PasswordAuthObjectWithRetryCounter){
			PasswordAuthObjectWithRetryCounter pwdWithRetryCounter = (PasswordAuthObjectWithRetryCounter) authObject;
			EditorFieldHelper.createField(item, true, composite, new TlvModifier() {
				
				@Override
				public void setValue(String string) {
					try {
						int newValue = Integer.parseInt(string);
						if (newValue >= 0 && newValue <= pwdWithRetryCounter.getRetryCounterDefaultValue()){
							pwdWithRetryCounter.resetRetryCounterToDefault();
							while (pwdWithRetryCounter.getRetryCounterCurrentValue() != Integer.parseInt(string)){
								pwdWithRetryCounter.decrementRetryCounter();
							}	
						}
					} catch (AccessDeniedException | NumberFormatException e) {
						BasicLogger.logException(getClass(), e, LogLevel.WARN);
					}
				}
				
				@Override
				public void remove() {
					// not intended
				}
				
				@Override
				public String getValue() {
					return Integer.toString(pwdWithRetryCounter.getRetryCounterCurrentValue());
				}
				
			}, new AndChecker(new NumberChecker(), new MaxValueChecker(pwdWithRetryCounter.getRetryCounterDefaultValue())), "Retry counter, max. value value is " + pwdWithRetryCounter.getRetryCounterDefaultValue());
		}
	}
	
}

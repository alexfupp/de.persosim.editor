package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult;
import de.persosim.editor.ui.editor.checker.TextFieldChecker;

public class LengthChecker implements TextFieldChecker {

	private int minLength;
	private int maxLength;

	public LengthChecker(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().length() <= maxLength && field.getText().length() <= minLength){
			return FieldCheckResult.OK;
		}
		return FieldCheckResult.WARNING;
	}

}

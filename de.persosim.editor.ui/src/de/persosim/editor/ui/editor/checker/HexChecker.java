package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;
import de.persosim.simulator.utils.HexString;

public class HexChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		try {
			HexString.toByteArray(field.getText());
		} catch (Exception e){
			return new FieldCheckResult("Not a valid hexadecimal string", State.ERROR);
		}
		return FieldCheckResult.OK;
	}

}

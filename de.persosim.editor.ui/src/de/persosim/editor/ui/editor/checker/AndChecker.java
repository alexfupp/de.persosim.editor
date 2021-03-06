package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class AndChecker implements TextFieldChecker {
	
	private TextFieldChecker[] checkers;

	public AndChecker(TextFieldChecker ... checkers) {
		this.checkers = checkers;
	}

	@Override
	public FieldCheckResult check(Text field) {
		for (TextFieldChecker checker : checkers){
			FieldCheckResult check = checker.check(field);
			if (check.getState() != State.OK){
				return check;
			}
		}
		return FieldCheckResult.OK;
	}

}

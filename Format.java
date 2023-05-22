import javax.swing.text.*;

public class Format extends PlainDocument {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// max lenght
	private int max;
	
	public Format(int limit) {
		super();
		this.max = limit;
	}
	
	// in this method we check if the current lenght of the textField and the text we want to write doesn't surpass the limit
	// if this condition is true the text will be written in the current TextField
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (str == null)
			return;
		if ((getLength() + str.length()) <= max) 
	// we use insertString to write the new text into the current TextField if the condition is true
	// with attr we define how the text should be formatted, but in this case we don't define anything about it
			super.insertString(offset, str.toUpperCase(), attr);
	}
	
	
}

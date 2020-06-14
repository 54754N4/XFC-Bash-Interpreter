package model.command;

import java.io.InputStream;
import java.io.OutputStream;

public interface RedirectableExecutable extends Executable {
	OutputStream stdin();
	InputStream stdout();
	InputStream stderr();
	void stdin(InputStream stream);
	void stdout(OutputStream stream);
	void stderr(OutputStream stream);
	void mergeSTDOUT();
}

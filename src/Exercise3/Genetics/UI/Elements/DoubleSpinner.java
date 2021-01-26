package Exercise3.Genetics.UI.Elements;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

//A Spinner working with double values
public class DoubleSpinner extends JSpinner {

    private static final long serialVersionUID = 1L;

    public DoubleSpinner() {
        super();
        // Model setup
        SpinnerNumberModel model = new SpinnerNumberModel(0.0, 0.0, 100.0, 0.001);
        this.setModel(model);
        model.setMaximum(1.0);
        NumberEditor editor = (NumberEditor)this.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(2);
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        Dimension d = this.getPreferredSize();
        this.setPreferredSize(d);

    }


}
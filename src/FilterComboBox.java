import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class FilterComboBox extends JComboBox {

    private List<String> comboBoxList;

    public FilterComboBox() {
        this(new ArrayList<String>());
    }

    public FilterComboBox(List<String> comboBoxList) {
        super(comboBoxList.toArray());

        this.comboBoxList = comboBoxList;
        this.setEditable(true);

        initListener();
    }

    private void initListener() {
        final JTextField textfield = (JTextField) this.getEditor().getEditorComponent();
        textfield.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent event) {
                switch(event.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        requestFocus(false);
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                        break;
                    default:
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                filter(textfield.getText());
                            }
                        });
                }
            }
        });

        getAccessibleContext().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                        && AccessibleState.FOCUSED.equals(event.getNewValue())
                        && getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                    ComboPopup popup = (ComboPopup) getAccessibleContext().getAccessibleChild(0);
                    JList list = popup.getList();
                    setSelectedItem(String.valueOf(list.getSelectedValue()));
                }
            }
        });
    }

    public void filter(String inputText) {
        List<String> filterList= new ArrayList<String>();
        for (String text : comboBoxList) {
            if (text.toLowerCase().contains(inputText.toLowerCase())) {
                filterList.add(text);
            }
        }
        if (!filterList.isEmpty()) {
            setModel(new DefaultComboBoxModel(filterList.toArray()));
            setSelectedItem(inputText);
            showPopup();
        }  else {
            hidePopup();
        }
    }

    public String getInputText() {
        return ((JTextField) this.getEditor().getEditorComponent()).getText();
    }

    public void addItem(String text) {
        super.addItem(text);
        comboBoxList.add(text);
    }

}
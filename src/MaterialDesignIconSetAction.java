import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class MaterialDesignIconSetAction extends AnAction {

    public void actionPerformed(AnActionEvent event) {
        MaterialDesignIconSetDialog dialog = new MaterialDesignIconSetDialog(getEventProject(event));
        dialog.show();
    }

}

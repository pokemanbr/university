package markup;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElement implements Elements {

    private List<Elements> list = new ArrayList<>(); 

    protected AbstractElement(List<Elements> list) {
        this.list = list;
    }

    public void toMarkdown(StringBuilder stringBuilder, String border) {
        stringBuilder.append(border);
        for (Elements text : list) {
            StringBuilder newText = new StringBuilder();
            text.toMarkdown(newText);
            stringBuilder.append(newText);
        }   
        stringBuilder.append(border);
    }

    public void toBBCode(StringBuilder stringBuilder, String borderBegin, String borderEnd) {
        stringBuilder.append(borderBegin);
        for (Elements text : list) {
            StringBuilder newText = new StringBuilder();
            text.toBBCode(newText);
            stringBuilder.append(newText);
        }   
        stringBuilder.append(borderEnd);
    }
}

package markup;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractList implements ParagraphList {

    private List<ListItem> list = new ArrayList<>(); 

    protected AbstractList(List<ListItem> list) {
        this.list = list;
    }

    public void toBBCode(StringBuilder stringBuilder, String start, String end) {
        stringBuilder.append(start);
        for (ListItem text : list) {
            StringBuilder newText = new StringBuilder();
            text.toBBCode(newText);
            stringBuilder.append(newText);
        }   
        stringBuilder.append(end);
    }
}

package markup;

import java.util.ArrayList;
import java.util.List;

public class ListItem implements ParagraphList {
    
    private List<ParagraphList> list;     

    public ListItem(List<ParagraphList> list) {
        this.list = list;
    }

    @Override
    public void toBBCode(StringBuilder stringBuilder) {
        stringBuilder.append("[*]");
        for (ParagraphList text : list) {
            text.toBBCode(stringBuilder);
        } 
    }
}
